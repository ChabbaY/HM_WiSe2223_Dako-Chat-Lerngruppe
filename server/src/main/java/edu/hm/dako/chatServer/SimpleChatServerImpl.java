package edu.hm.dako.chatServer;

import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.common.AuditLogPduType;
import edu.hm.dako.common.ChatPDU;
import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.common.PduType;
import edu.hm.dako.connection.Connection;
import edu.hm.dako.connection.ServerSocketInterface;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simple-Chat-Server-Implementierung
 * @author Peter Mandl
 */
public class SimpleChatServerImpl extends AbstractChatServer {

    private static final Logger LOG = LogManager.getLogger(SimpleChatServerImpl.class);

    // Threadpool fuer Worker-Threads
    private final ExecutorService executorService;

    // Socket fuer den Listener, der alle Verbindungsaufbauwuensche der Clients
    // entgegennimmt
    private final ServerSocketInterface socket;

    // Verbindung zum AuditLog-Server
    private final AuditLogConnection auditLogConnection;

    /**
     * Konstruktor
     * @param executorService Threadpool
     * @param socket Listen Socket
     * @param serverGuiInterface Referenz auf das Server-GUI-Interface
     */
    public SimpleChatServerImpl(ExecutorService executorService,
                                ServerSocketInterface socket, ChatServerGuiInterface serverGuiInterface) {
        this.executorService = executorService;
        this.socket = socket;
        this.serverGuiInterface = serverGuiInterface;
        counter = new SharedServerCounter();
        counter.logoutCounter = new AtomicInteger(0);
        counter.eventCounter = new AtomicInteger(0);
        counter.confirmCounter = new AtomicInteger(0);
        this.auditLogConnection = null;
        LOG.debug("SimpleChatServerImpl konstruiert");
    }

    /**
     * Konstruktor
     * @param executorService Threadpool
     * @param socket Listen Socket
     * @param serverGuiInterface Referenz auf das Server-GUI-Interface
     * @param auditLogConnection Referenz auf Auditlog-Server-Verbindung
     */
    public SimpleChatServerImpl(ExecutorService executorService, ServerSocketInterface socket,
                                ChatServerGuiInterface serverGuiInterface,
                                AuditLogConnection auditLogConnection) {
        this.executorService = executorService;
        this.socket = socket;
        this.serverGuiInterface = serverGuiInterface;
        counter = new SharedServerCounter();
        counter.logoutCounter = new AtomicInteger(0);
        counter.eventCounter = new AtomicInteger(0);
        counter.confirmCounter = new AtomicInteger(0);
        this.auditLogConnection = auditLogConnection;

        /* Nur zum Test
        // Ersten AuditLog-Satz senden
        try {
            ChatPDU beginPdu = new ChatPDU();
            beginPdu.setPduType(PduType.UNDEFINED);
            beginPdu.setMessage("Beginn des Audit-Logs");
            beginPdu.setUserName("Chat-Server");
            auditLogConnection.send(beginPdu, AuditLogPduType.BEGIN_AUDIT_REQUEST);
        } catch (Exception e) {
            LOG.debug("Start-PDU an den AuditLog-Server konnte nicht gesendet werden, AuditLog-Server nicht aktiv?");
        }
        */

        LOG.debug("SimpleChatServerImpl konstruiert");
    }

    @Override
    public void start() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                // Clientliste erzeugen
                clients = SharedChatClientList.getInstance();

                while (!Thread.currentThread().isInterrupted() && !socket.isClosed()) {
                    try {
                        // Auf ankommende Verbindungsaufbauwuensche warten
                        System.out.println("SimpleChatServer wartet auf Verbindungsanfragen von Clients...");
                        Connection connection = socket.accept();
                        LOG.debug("Neuer Verbindungsaufbauwunsch empfangen");

                        if (auditLogConnection == null) {
                            // Neuen Workerthread starten ohne AuditLog-Verbindung
                            executorService.submit(new SimpleChatWorkerThreadImpl(connection, clients,
                                    counter, serverGuiInterface));
                        } else {
                            // Wenn der AuditLog-Server verbunden ist, dann jedem WorkerThread die Verbindung zu diesem mitgeben
                            executorService.submit(new SimpleChatWorkerThreadImpl(connection, clients,
                                    counter, serverGuiInterface, auditLogConnection));
                        }
                    } catch (Exception e) {
                        if (socket.isClosed()) {
                            LOG.debug("Socket wurde geschlossen");
                        } else {
                            LOG.error("Exception beim Entgegennehmen von Verbindungsaufbauwuenschen: " + e);
                            ExceptionHandler.logException(e);
                        }
                    }
                }
                return null;
            }
        };

        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    @Override
    public void stop() throws Exception {

        // Alle Verbindungen zu aktiven Clients abbauen
        Vector<String> sendList = clients.getClientNameList();
        for (String s : new Vector<>(sendList)) {
            ClientListEntry client = clients.getClient(s);
            try {
                if (client != null) {
                    client.getConnection().close();
                    LOG.error("Verbindung zu Client " + client.getUserName() + " geschlossen");
                }
            } catch (Exception e) {
                LOG.debug("Fehler beim Schliessen der Verbindung zu Client " + client.getUserName());
                ExceptionHandler.logException(e);
            }
        }

        // Loeschen der Userliste
        clients.deleteAll();
        Thread.currentThread().interrupt();

        // Serversocket schliessen
        socket.close();
        LOG.debug("Listen-Socket geschlossen");

        // Verbindung zu AuditLog-Server schliessen

        if (auditLogConnection != null) {
            auditLogConnection.close();
            LOG.debug("AuditLogServer Connection closed");
        }

        // Threadpool schliessen
        executorService.shutdown();
        LOG.debug("Threadpool freigegeben");

        System.out.println("SimpleChatServer beendet sich");
    }
}