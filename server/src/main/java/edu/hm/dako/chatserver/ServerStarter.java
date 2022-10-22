package edu.hm.dako.chatserver;

import edu.hm.dako.chatserver.gui.ServerFxGUI;
import edu.hm.dako.common.AuditLogImplementationType;
import edu.hm.dako.common.ChatServerImplementationType;
import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.common.SystemConstants;
import edu.hm.dako.common.Tupel;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

/**
 * starts the chat server
 *
 * @author Peter Mandl, edited by Lerngruppe
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

    private static boolean GUI = true;

    /**
     * starts the chat server
     *
     * @param args available args, please only use non-default, auditlog-protocol must be specified before auditlog-port
     *             --nogui disables the gui
     *             --protocol=tcpsimple (default; tcpadvanced not implemented yet)
     *             --port=50001 (default)
     *             --send-buffer=300000 (default)
     *             --receive-buffer=300000 (default)
     *             --auditlog=true | false (default true)
     *             --auditlog-protocol=tcp | udp | rmi (default tcp)
     *             --auditlog-host=localhost (default)
     *             --auditlog-port=40001 (default)
     */
    public static void main(String[] args) {
        // Log4j2-Logging aus Datei konfigurieren
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File file = new File("config/log4j/log4j2.chatServer.xml");
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
                    starter.stopChatServer();
                    return;
                }
            }
        }
    }

    /**
     * Konstruktor
     */
    public ServerStarter(String[] args) {
        String implType = SystemConstants.IMPL_TCP_SIMPLE;
        int port = 50001;
        int sendBuffer = 300000;
        int receiveBuffer = 300000;
        boolean auditlog = true;
        String auditlog_protocol = SystemConstants.AUDIT_LOG_SERVER_TCP_IMPL;
        String auditlog_host = "localhost";
        int auditlog_port = 40001;

        for(String s: args) {
            String[] values = s.split("=");
            switch (values[0]) {
                case "--nogui" -> GUI = false;
                case "--protocol" -> {
                    if ("tcpadvanced".equals(values[1])) {
                        implType = SystemConstants.IMPL_TCP_ADVANCED;
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
                case "--auditlog" -> {
                    if ("false".equals(values[1])) {
                        auditlog = false;
                    }
                }
                case "--auditlog-protocol" -> {
                    if ("udp".equals(values[1])) {
                        auditlog_protocol = SystemConstants.AUDIT_LOG_SERVER_UDP_IMPL;
                    } else if ("rmi".equals(values[1])) {
                        auditlog_protocol = SystemConstants.AUDIT_LOG_SERVER_RMI_IMPL;
                    }
                }
                case "--auditlog-host" -> auditlog_host = values[1];
                case "--auditlog-port" -> {
                    Tupel<Integer, Boolean> result = validateAuditLogServerPort(values[1], auditlog_protocol);
                    auditlog_port = result.getX();
                    startable = result.getY();
                }
            }
        }

        if (GUI) {
            ServerFxGUI.main(args);
        } else {
            try {
                if (auditlog) {
                    startChatServer(implType, port, sendBuffer, receiveBuffer,
                            auditlog_host, auditlog_port, auditlog_protocol);
                } else {
                    startChatServer(implType, port, sendBuffer, receiveBuffer);
                }
            } catch (Exception e) {
                LOG.error("Server konnte nicht gestartet werden: " + e.getMessage());
            }
        }
    }

    /**
     * Chat-Server starten
     *
     * @param implType          Implementierungstyp, der zu starten ist
     * @param serverPort        Serverport, die der Server als Listener-Port nutzen soll
     * @param sendBufferSize    Sendepuffergröße, die der Server nutzen soll
     * @param receiveBufferSize Empfangspuffergröße, die der Server nutzen soll
     */
    private boolean startChatServer(String implType, int serverPort, int sendBufferSize, int receiveBufferSize)
            throws Exception {
        ChatServerImplementationType serverImpl;
        if (implType.equals(SystemConstants.IMPL_TCP_ADVANCED)) {
            serverImpl = ChatServerImplementationType.TCPAdvancedImplementation;
        } else {
            serverImpl = ChatServerImplementationType.TCPSimpleImplementation;
        }

        try {
            chatServer = ServerFactory.getServer(serverImpl, serverPort, sendBufferSize, receiveBufferSize,
                    null);
        } catch (Exception e) {
            LOG.error("Fehler beim Starten des Chat-Servers: " + e.getMessage());
            ExceptionHandler.logException(e);
            throw new Exception(e);
        }
        if (!startable) {
            return false;
        } else {
            // Server starten
            chatServer.start();
            return true;
        }
    }
    private boolean startChatServer(String implType, int serverPort, int sendBufferSize, int receiveBufferSize,
                                 String auditLogServerHostname, int auditLogServerPort, String auditLogServerImplType)
            throws Exception {
        ChatServerImplementationType serverImpl;
        if (implType.equals(SystemConstants.IMPL_TCP_ADVANCED)) {
            serverImpl = ChatServerImplementationType.TCPAdvancedImplementation;
        } else {
            serverImpl = ChatServerImplementationType.TCPSimpleImplementation;
        }

        AuditLogImplementationType auditLogImplementationType = switch (auditLogServerImplType) {
            case SystemConstants.AUDIT_LOG_SERVER_UDP_IMPL ->
                    AuditLogImplementationType.AuditLogServerUDPImplementation;
            case SystemConstants.AUDIT_LOG_SERVER_RMI_IMPL ->
                    AuditLogImplementationType.AuditLogServerRMIImplementation;
            default -> AuditLogImplementationType.AuditLogServerTCPImplementation;
        };

        try {
            LOG.debug("ChatServer soll mit AuditLog gestartet werden");
            chatServer = ServerFactory.getServerWithAuditLog(serverImpl, serverPort, sendBufferSize, receiveBufferSize,
                    null, auditLogImplementationType, auditLogServerHostname, auditLogServerPort);
        } catch (Exception e) {
            LOG.error("Fehler beim Starten des Chat-Servers: {}", e.getMessage());
            ExceptionHandler.logException(e);
            throw new Exception(e);
        }

        if (!startable) {
            LOG.error("Server konnte nicht gestartet werden aufgrund fehlerhafter Eingaben");
            return false;
        } else {
            if (!ServerFactory.isAuditLogServerConnected()) {
                // AuditLog-Server Verbindung nicht vorhanden
                LOG.error("Verbindung zum AuditLog-Server konnte nicht hergestellt werden");
            }

            // Server starten
            chatServer.start();
            return true;
        }
    }

    private void stopChatServer() {
        try {
            chatServer.stop();
        } catch (Exception e) {
            LOG.error("Fehler beim Stoppen des Chat-Servers");
            ExceptionHandler.logException(e);
        }
    }

    //----VALIDATION-----------------------------------------------

    /**
     * validate auditLog-server port
     *
     * @return Tupel\<port, startable\>
     */
    public static Tupel<Integer, Boolean> validateAuditLogServerPort(String port, String auditLogServerImplType) {
        int iServerPort = 0;
        boolean startable = true;
        if (port.matches("[0-9]+")) {
            iServerPort = Integer.parseInt(port);
            if ((iServerPort < 1) || (iServerPort > 65535)) {
                startable = false;
            } else if (auditLogServerImplType.equals(SystemConstants.AUDIT_LOG_SERVER_RMI_IMPL)) {
                // Falls RMI ausgewählt wurde, wird standardmäßig der RMI-Registry-Port 1099 verwendet
                iServerPort = Integer.parseInt(SystemConstants.DEFAULT_AUDIT_LOG_SERVER_RMI_REGISTRY_PORT);
                LOG.debug("Standard-Port für RMI-Registry: {}", iServerPort);
            } else {
                LOG.debug("Port für AuditLog-Server: {}", iServerPort);
            }
        } else {
            startable = false;
        }
        return new Tupel<>(iServerPort, startable);
    }

    /**
     * validate server port
     *
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