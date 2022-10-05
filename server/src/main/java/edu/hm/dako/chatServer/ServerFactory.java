package edu.hm.dako.chatServer;

import edu.hm.dako.common.AuditLogImplementationType;
import edu.hm.dako.common.ChatServerImplementationType;
import edu.hm.dako.connection.LoggingConnectionDecorator;
import edu.hm.dako.connection.Connection;
import edu.hm.dako.connection.ServerSocketInterface;
import edu.hm.dako.connection.tcp.TcpServerSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;

/**
 * Uebernimmt die Konfiguration und Erzeugung bestimmter Server-Typen.
 * @author Peter Mandl
 */
public final class ServerFactory {
    private static final Logger LOG = LogManager.getLogger(ServerFactory.class);
    protected static Connection connection;
    protected static AuditLogConnection auditLogConnection = null;

    private ServerFactory() {}

    /**
     * Erzeugt einen Chat-Server
     * @param implType Implementierungytyp des Servers
     * @param serverPort Listenport
     * @param sendBufferSize Groesse des Sendepuffers in Byte
     * @param receiveBufferSize Groesse des Empfangspuffers in Byte
     * @param serverGuiInterface Referenz auf GUI fuer Callback
     * @return Referenz auf ChatServer-Interface
     * @throws Exception Fehler beim Erzeugen eines Sockets
     */
    public static ChatServerInterface getServer(ChatServerImplementationType implType, int serverPort,
                                                int sendBufferSize, int receiveBufferSize,
                                                ChatServerGuiInterface serverGuiInterface) throws Exception {
        LOG.debug("ChatServer (" + implType.toString() + ") wird gestartet, Serverport: "
                + serverPort + ", Sendepuffer: " + sendBufferSize + ", Empfangspuffer: "
                + receiveBufferSize);
        System.out.println("ChatServer (" + implType
                + ") wird gestartet, Listen-Port: " + serverPort + ", Sendepuffer: "
                + sendBufferSize + ", Empfangspuffer: " + receiveBufferSize);

        switch (implType) {

            case TCPSimpleImplementation:

                try {
                    TcpServerSocket tcpServerSocket = new TcpServerSocket(serverPort, sendBufferSize,
                            receiveBufferSize);
                    return new SimpleChatServerImpl(Executors.newCachedThreadPool(),
                            getDecoratedServerSocket(tcpServerSocket), serverGuiInterface);
                } catch (Exception e) {
                    throw new Exception(e);
                }

                // Weitere Implementierungstypen derzeit nicht implementiert

            default:
                System.out.println("Dezeit nur TCPSimpleImplementation implementiert!");
                throw new RuntimeException("Unknown type: " + implType);
        }
    }

    /**
     * Erzeugt einen Chat-Server mit Verbindung zum AuditLog-Server
     * @param implType Implementierungytyp des Servers
     * @param serverPort Listenport
     * @param sendBufferSize Groesse des Sendepuffers in Byte
     * @param receiveBufferSize Groesse des Empfangspuffers in Byte
     * @param serverGuiInterface Referenz auf GUI fuer Callback
     * @param auditLogImplementationType AzditLog-Server-Tyo UDP oder TCP
     * @param auditLogServerHostnameOrIP Hostname, in dem der AuditLog-Server laeuft
     * @param auditLogServerPort Port des AuditLog-Servers
     * @return Referenz auf ChatServer-Interface
     * @throws Exception - Fehler beim Erzeugen eines Sockets
     */
    public static ChatServerInterface getServerWithAuditLog(ChatServerImplementationType implType, int serverPort,
                                                            int sendBufferSize, int receiveBufferSize,
                                                            ChatServerGuiInterface serverGuiInterface,
                                                            AuditLogImplementationType auditLogImplementationType,
                                                            String auditLogServerHostnameOrIP, int auditLogServerPort) throws Exception {

        // Zunaechst Verbindung zum AuditLog-Server aufbauen
        LOG.debug("ChatServer wird mit AuditLogServer gestartet, ChatServer Port: " + serverPort + ", Sendepuffer: " + sendBufferSize + ", Empfangspuffer: "
                + receiveBufferSize + ", AuditLogServer Port: " + auditLogServerPort + ", AuditLogServer Hostname or IP: "
                + auditLogServerHostnameOrIP);

        // Verbindung zum AuditLog-Server aufbauen

        int typeOfAuditLogConnection = AuditLogConnection.AUDITLOG_CONNECTION_TYPE_TCP;

        if (auditLogImplementationType == AuditLogImplementationType.AuditLogServerTCPImplementation) {
            typeOfAuditLogConnection = AuditLogConnection.AUDITLOG_CONNECTION_TYPE_TCP;
        } else if (auditLogImplementationType == AuditLogImplementationType.AuditLogServerUDPImplementation) {
            typeOfAuditLogConnection = AuditLogConnection.AUDITLOG_CONNECTION_TYPE_UDP;
        } else if (auditLogImplementationType == AuditLogImplementationType.AuditLogServerRMIImplementation) {
            typeOfAuditLogConnection = AuditLogConnection.AUDITLOG_CONNECTION_TYPE_RMI;
        }

        try {
            auditLogConnection = new AuditLogConnection(typeOfAuditLogConnection, auditLogServerHostnameOrIP, auditLogServerPort);
            auditLogConnection.connectToAuditLogServer();
            LOG.debug("Verbindung zum AuditLog Server aufgebaut");
        } catch (Exception e) {
            // AuditLogServer nicht ordentlich initialisiert
            auditLogConnection = null;
            //ExceptionHandler.logException(e);
            LOG.debug("Verbindung zum AuditLog-Server konnte nicht aufgebaut werden");

            // Server arbeitet ohne AuditLog-Server
        }

        // Dann Chat-Server mit Verbindungsendpunkt erzeugen

        switch (implType) {

            case TCPSimpleImplementation:

                try {
                    TcpServerSocket tcpServerSocket = new TcpServerSocket(serverPort, sendBufferSize,
                            receiveBufferSize);
                    return new SimpleChatServerImpl(Executors.newCachedThreadPool(),
                            getDecoratedServerSocket(tcpServerSocket), serverGuiInterface, auditLogConnection);
                } catch (Exception e) {
                    throw new Exception(e);
                }

                // Weitere Implementierungstypen derzeit nicht implementiert

            default:
                System.out.println("Derzeit nur TCPSimpleImplementation implementiert!");
                throw new RuntimeException("Unknown type: " + implType);
        }
    }

    /**
     * Dekoratiert ServerSocket mit Logging-Funktionalitaet
     * @param serverSocket Serverseitiger Kommunikationsendpunkt fuer den LISTEN Port)
     * @return Referenz auf dekoriertes ServerSocket
     */
    private static ServerSocketInterface getDecoratedServerSocket(
            ServerSocketInterface serverSocket) {
        return new DecoratingServerSocket(serverSocket);
    }

    /**
     * Pruefe, ob AuditLog-Server verbunden ist
     * @return Verbindung aufgebaut = true
     */
    public static boolean isAuditLogServerConnected() {
        return auditLogConnection != null;
    }

    /**
     * Dekoriert Server-Socket mit Logging-Funktionalitaet
     * @author mandl
     */
    private static class DecoratingServerSocket implements ServerSocketInterface {

        private final ServerSocketInterface wrappedServerSocket;

        DecoratingServerSocket(ServerSocketInterface wrappedServerSocket) {
            this.wrappedServerSocket = wrappedServerSocket;
        }

        @Override
        public Connection accept() throws Exception {
            return new LoggingConnectionDecorator(wrappedServerSocket.accept());
        }

        @Override
        public void close() throws Exception {
            wrappedServerSocket.close();
        }

        @Override
        public boolean isClosed() {
            return wrappedServerSocket.isClosed();
        }
    }
}
