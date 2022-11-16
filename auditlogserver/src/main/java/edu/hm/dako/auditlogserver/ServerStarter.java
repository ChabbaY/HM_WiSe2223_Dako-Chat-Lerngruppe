package edu.hm.dako.auditlogserver;

import edu.hm.dako.auditlogserver.gui.AuditLogFxGUI;
import edu.hm.dako.chatserver.ServerInterface;
import edu.hm.dako.common.AuditLogImplementationType;
import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.common.SystemConstants;
import edu.hm.dako.common.Tupel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * starts the audit log server
 *
 * @author Linus Englert
 */
public class ServerStarter {
    /**
     * referencing the logger
     */
    private static final Logger LOG = LogManager.getLogger(ServerStarter.class);

    /**
     * Interface der Chat-Server-Implementierung
     */
    private ServerInterface chatServer;

    /**
     * Flag, das angibt, ob der Server gestartet werden kann (alle Plausibilitätsprüfungen erfüllt)
     */
    private boolean startable = true;

    /**
     * flag that is true when a GUI is used
     */
    private static boolean GUI = true;

    /**
     * starts the audit log server
     *
     * @param args available args, please only use non-default
     *             --nogui disables the gui
     *             --protocol=tcp | udp | rmi (default; udp and rmi not implemented yet)
     *             --port=40001 (default)
     *             --send-buffer=300000 (default)
     *             --receive-buffer=300000 (default)
     */
    public static void main(String[] args) {
        // Log4j2-Logging aus Datei konfigurieren
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File file = new File("config/log4j/log4j2.auditLogTcpServer.xml");
        context.setConfigLocation(file.toURI());

        ServerStarter starter = new ServerStarter(args);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while(!GUI) {
            String input = "";
            try {
                System.out.println("stop zum Beenden");
                input = br.readLine();
            } catch (IOException e) {
                LOG.error("Fehler bei Eingabe: " + e.getMessage());
            }

            switch(input) {
                case "stop" -> {
                    starter.stopAuditLogServer();
                    return;
                }
            }
        }

    }

    /**
     * Konstruktor
     *
     * @param args available args, please only use non-default
     *              --nogui disables the gui
     *             --protocol=tcp | udp | rmi (default; udp and rmi not implemented yet)
     *             --port=40001 (default)
     *             --send-buffer=300000 (default)
     *             --receive-buffer=300000 (default)
     */
    public ServerStarter(String[] args){
        String implType = SystemConstants.AUDIT_LOG_SERVER_TCP_IMPL;
        int port = 40001;
        int sendBuffer = 300000;
        int receiveBuffer = 300000;

        for(String s: args) {
            String[] values = s.split("=");
            switch (values[0]) {
                case "--nogui" -> GUI = false;
                case "--protocol" -> {
                    if ("udp".equals(values[1])) {
                        implType = SystemConstants.AUDIT_LOG_SERVER_UDP_IMPL;
                    } else if ("rmi".equals(values[1])) {
                        implType = SystemConstants.AUDIT_LOG_SERVER_RMI_IMPL;
                    }
                }
                case "--port" -> {
                    Tupel<Integer, Boolean> result = validateServerPort(values[1]);
                    port = result.getX();
                    startable = result.getY();
                }
                case "--send-buffer" -> {
                    Tupel<Integer, Boolean> result = validateSendBufferSize(values[1]);
                    sendBuffer = result.getX();
                    startable = result.getY();
                }
                case "--receive-buffer" -> {
                    Tupel<Integer, Boolean> result = validateReceiveBufferSize(values[1]);
                    receiveBuffer = result.getX();
                    startable = result.getY();
                }
            }
        }

        if (GUI) {
            AuditLogFxGUI.main(args);
        } else {
            try {
                startAuditLogServer(implType, port, sendBuffer, receiveBuffer);
            } catch (Exception e) {
                LOG.error("Server konnte nicht gestartet werden: " + e.getMessage());
            }
        }
    }

    /**
     * Audit-Log-Server starten
     *
     * @param implType          Implementierungstyp, der zu starten ist
     * @param serverPort        Serverport, die der Server als Listener-Port nutzen soll
     * @param sendBufferSize    Sendepuffergröße, die der Server nutzen soll
     * @param receiveBufferSize Empfangspuffergröße, die der Server nutzen soll
     */
    private boolean startAuditLogServer(String implType, int serverPort, int sendBufferSize, int receiveBufferSize)
            throws Exception {
        AuditLogImplementationType serverImpl;
        if (implType.equals(SystemConstants.AUDIT_LOG_SERVER_TCP_IMPL)) {
            serverImpl = AuditLogImplementationType.AuditLogServerTCPImplementation;
        } else if (implType.equals(SystemConstants.AUDIT_LOG_SERVER_UDP_IMPL)) {
            serverImpl = AuditLogImplementationType.AuditLogServerUDPImplementation;
        } else {
            serverImpl = AuditLogImplementationType.AuditLogServerRMIImplementation;
        }

        try {
            //chatServer = ServerFactory.getServer(serverImpl, serverPort, sendBufferSize, receiveBufferSize, null);TODO
        } catch (Exception e) {
            LOG.error("Fehler beim Starten des Chat-Servers: " + e.getMessage());
            ExceptionHandler.logException(e);
            throw new Exception(e);
        }
        if (!startable) {
            return false;
        } else {
            // Server starten
            //chatServer.start();TODO
            return true;
        }
    }

    /**
     * Audit-Log-Server stoppen
     */
    private void stopAuditLogServer() {
        try {
            chatServer.stop();
        } catch (Exception e) {
            LOG.error("Fehler beim Stoppen des Chat-Servers");
            ExceptionHandler.logException(e);
        }
    }

    //----VALIDATION--------------------------------------------------

    /**
     * validate server port
     *
     * @param port port to validate
     * @return port
     */
    public static Tupel<Integer, Boolean> validateServerPort(String port) {
        int iServerPort = 0;
        boolean startable = true;
        if (port.matches("[0-9]+")) {
            iServerPort = Integer.parseInt(port);
            if ((iServerPort < 1) || (iServerPort > 65535)) {
                startable = false;
            } else {
                LOG.debug("Serverport: " + iServerPort);
            }
        } else {
            startable = false;
        }
        return new Tupel<>(iServerPort, startable);
    }

    /**
     * validate send buffer size
     *
     * @param size buffer size to validate
     * @return send buffer size
     */
    public static Tupel<Integer, Boolean> validateSendBufferSize(String size) {
        int iSendBufferSize = 0;
        boolean startable = true;
        if (size.matches("[0-9]+")) {
            iSendBufferSize = Integer.parseInt(size);
            if ((iSendBufferSize <= 0)
                    || (iSendBufferSize > Integer.parseInt(SystemConstants.MAX_SEND_BUFFER_SIZE))) {
                startable = false;
            } else {
                LOG.debug("Sendepuffer: " + iSendBufferSize);
            }
        } else {
            startable = false;
        }
        return new Tupel<>(iSendBufferSize, startable);
    }

    /**
     * validate receive buffer size
     *
     * @param size buffer size to validate
     * @return receive buffer size
     */
    public static Tupel<Integer, Boolean> validateReceiveBufferSize(String size) {
        int iReceiveBufferSize = 0;
        boolean startable = true;
        if (size.matches("[0-9]+")) {
            iReceiveBufferSize = Integer.parseInt(size);
            if ((iReceiveBufferSize <= 0)
                    || (iReceiveBufferSize > Integer.parseInt(SystemConstants.MAX_RECEIVE_BUFFER_SIZE))) {
                startable = false;
            } else {
                LOG.debug("Empfangspuffergröße: {}", iReceiveBufferSize);
            }
        } else {
            startable = false;
        }
        return new Tupel<>(iReceiveBufferSize, startable);
    }
}