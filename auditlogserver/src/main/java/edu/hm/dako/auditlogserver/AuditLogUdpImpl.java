package edu.hm.dako.auditlogserver;

import edu.hm.dako.auditlogserver.gui.ALServerGUIInterface;
import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.connection.Connection;
import edu.hm.dako.connection.ServerSocketInterface;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * audit log udp implementation
 *
 * @author Kilian Brandner
 */
public class AuditLogUdpImpl extends AbstractALServer {
    /**
     * referencing the logger
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * ThreadPool für Worker-Threads
     */
    private final ExecutorService executorService;

    /**
     * Socket für den Listener, der alle Verbindungsaufbauwünsche der ChatServer entgegennimmt
     */
    private final ServerSocketInterface socket;

    /**
     * constructor
     *
     * @param executorService executor service
     * @param socket server socket
     * @param gui server gui
     */
    public AuditLogUdpImpl(ExecutorService executorService, ServerSocketInterface socket, ALServerGUIInterface gui) {
        this.executorService = executorService;
        this.socket = socket;
        this.alServerGUIInterface = gui;

        LOG.debug("AuditLogServer konstruiert!");
    }

    @Override
    public void start() {
        Thread thread = new Thread(() -> {
            // ClientListe erzeugen
            clients = SharedChatServerList.getInstance();

                try {
                    // Auf ankommende Verbindungsaufbauwünsche warten
                    Connection connection = socket.accept();

                    // Neuen WorkerThread starten ohne AuditLog-Verbindung
                    executorService.submit(new AuditlogWorkerThread(connection, alServerGUIInterface));
                } catch (Exception e) {
                    if (socket.isClosed()) {
                        LOG.debug("Socket wurde geschlossen");
                    } else {
                        LOG.error("Exception beim Entgegennehmen von Verbindungsaufbauwünschen: " + e);
                        ExceptionHandler.logException(e);
                    }
                }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void stop() throws Exception {
        // Alle Verbindungen zu aktiven Clients abbauen
        Vector<String> sendList = clients.getServerSocketList();
        for (String s : new Vector<>(sendList)) {
            ServerListEntry client = clients.getServer(s);
            try {
                if (client != null) {
                    client.getConnection().close();
                    LOG.error("Verbindung zu Client " + client.getServerAddress() + ":" + client.getServerPort()
                            + " geschlossen");
                }
            } catch (Exception e) {
                LOG.debug("Fehler beim Schliessen der Verbindung zu Client " + client.getServerAddress() + ":"
                        + client.getServerPort());
                ExceptionHandler.logException(e);
            }
        }

        // Löschen der Userliste
        clients.clear();
        Thread.currentThread().interrupt();

        // Serversocket schliessen
        socket.close();
        LOG.debug("Listen-Socket geschlossen");

        // ThreadPool schliessen
        executorService.shutdown();
        LOG.debug("ThreadPool freigegeben");

        System.out.println("SimpleChatServer beendet sich");
    }
}
