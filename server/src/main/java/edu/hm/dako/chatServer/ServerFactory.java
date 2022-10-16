package edu.hm.dako.chatServer;

import edu.hm.dako.common.AuditLogImplementationType;
import edu.hm.dako.common.ChatServerImplementationType;
import edu.hm.dako.connection.ConnectionLogger;
import edu.hm.dako.connection.Connection;
import edu.hm.dako.connection.ServerSocketInterface;
import edu.hm.dako.connection.tcp.TCPServerSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Executors;

/**
 * Übernimmt die Konfiguration und Erzeugung bestimmter Server-Typen.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public final class ServerFactory {
    /**
     * referencing the logger
     */
    private static final Logger LOG = LogManager.getLogger(ServerFactory.class);

    /**
     * connection to audit log server
     */
    private static AuditLogConnection auditLogConnection = null;

    /**
     * Konstruktor
     */
    private ServerFactory() {
    }

    /**
     * Erzeugt einen Chat-Server
     *
     * @param implType           Implementierungstyp des Servers
     * @param serverPort         Listenport
     * @param sendBufferSize     Größe des Sendepuffers in Byte
     * @param receiveBufferSize  Größe des Empfangspuffers in Byte
     * @param serverGuiInterface Referenz auf GUI für Callback
     * @return Referenz auf ChatServer-Interface
     * @throws Exception Fehler beim Erzeugen eines Sockets
     */
    public static ChatServerInterface getServer(ChatServerImplementationType implType, int serverPort,
                                                int sendBufferSize, int receiveBufferSize,
                                                ChatServerGUIInterface serverGuiInterface) throws Exception {
        LOG.debug("ChatServer (" + implType.toString() + ") wird gestartet, Serverport: "
                + serverPort + ", Sendepuffer: " + sendBufferSize + ", Empfangspuffer: "
                + receiveBufferSize);
        System.out.println("ChatServer (" + implType
                + ") wird gestartet, Listen-Port: " + serverPort + ", Sendepuffer: "
                + sendBufferSize + ", Empfangspuffer: " + receiveBufferSize);

        if (implType == ChatServerImplementationType.TCPSimpleImplementation) {
            try {
                TCPServerSocket tcpServerSocket = new TCPServerSocket(serverPort, sendBufferSize,
                        receiveBufferSize);
                return new SimpleChatServerImpl(Executors.newCachedThreadPool(),
                        getDecoratedServerSocket(tcpServerSocket), serverGuiInterface);
            } catch (Exception e) {
                throw new Exception(e);
            }
            // Weitere Implementierungstypen derzeit nicht implementiert
        }
        System.out.println("Derzeit nur TCPSimpleImplementation implementiert!");
        throw new RuntimeException("Unknown type: " + implType);//TODO more implementations
    }

    /**
     * Erzeugt einen Chat-Server mit Verbindung zum AuditLog-Server
     *
     * @param implType                   Implementierungstyp des Servers
     * @param serverPort                 Listenport
     * @param sendBufferSize             Größe des Sendepuffers in Byte
     * @param receiveBufferSize          Größe des Empfangspuffers in Byte
     * @param serverGuiInterface         Referenz auf GUI für Callback
     * @param auditLogImplementationType AuditLog-Server-Tyo UDP oder TCP
     * @param auditLogServerHostnameOrIP Hostname, in dem der AuditLog-Server läuft
     * @param auditLogServerPort         Port des AuditLog-Servers
     * @return Referenz auf ChatServer-Interface
     * @throws Exception - Fehler beim Erzeugen eines Sockets
     */
    public static ChatServerInterface getServerWithAuditLog(ChatServerImplementationType implType, int serverPort,
                                                            int sendBufferSize, int receiveBufferSize,
                                                            ChatServerGUIInterface serverGuiInterface,
                                                            AuditLogImplementationType auditLogImplementationType,
                                                            String auditLogServerHostnameOrIP, int auditLogServerPort) throws Exception {
        // Zunächst Verbindung zum AuditLog-Server aufbauen
        LOG.debug("ChatServer wird mit AuditLogServer gestartet, ChatServer Port: " + serverPort + ", Sendepuffer: " + sendBufferSize + ", Empfangspuffer: "
                + receiveBufferSize + ", AuditLogServer Port: " + auditLogServerPort + ", AuditLogServer Hostname or IP: "
                + auditLogServerHostnameOrIP);

        // Verbindung zum AuditLog-Server aufbauen
        int typeOfAuditLogConnection = AuditLogConnection.AUDIT_LOG_CONNECTION_TYPE_TCP;

        if (auditLogImplementationType == AuditLogImplementationType.AuditLogServerUDPImplementation) {
            typeOfAuditLogConnection = AuditLogConnection.AUDIT_LOG_CONNECTION_TYPE_UDP;
        } else if (auditLogImplementationType == AuditLogImplementationType.AuditLogServerRMIImplementation) {
            typeOfAuditLogConnection = AuditLogConnection.AUDIT_LOG_CONNECTION_TYPE_RMI;
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
        if (implType == ChatServerImplementationType.TCPSimpleImplementation) {
            try {
                TCPServerSocket tcpServerSocket = new TCPServerSocket(serverPort, sendBufferSize,
                        receiveBufferSize);
                return new SimpleChatServerImpl(Executors.newCachedThreadPool(),
                        getDecoratedServerSocket(tcpServerSocket), serverGuiInterface, auditLogConnection);
            } catch (Exception e) {
                throw new Exception(e);
            }

            // Weitere Implementierungstypen derzeit nicht implementiert
        }
        System.out.println("Derzeit nur TCPSimpleImplementation implementiert!");
        throw new RuntimeException("Unknown type: " + implType);//TODO more implementations
    }

    /**
     * Dekoriert ServerSocket mit Logging-Funktionalität
     *
     * @param serverSocket Serverseitiger Kommunikationsendpunkt (für den LISTEN Port)
     * @return Referenz auf dekoriertes ServerSocket
     */
    private static ServerSocketInterface getDecoratedServerSocket(ServerSocketInterface serverSocket) {
        return new DecoratingServerSocket(serverSocket);
    }

    /**
     * Prüfe, ob AuditLog-Server verbunden ist
     *
     * @return Verbindung aufgebaut = true
     */
    public static boolean isAuditLogServerConnected() {
        return (auditLogConnection != null);
    }

    /**
     * Dekoriert Server-Socket mit Logging-Funktionalität
     */
    private record DecoratingServerSocket(ServerSocketInterface wrappedServerSocket) implements ServerSocketInterface {
        @Override
        public Connection accept() throws Exception {
            return new ConnectionLogger(wrappedServerSocket.accept());
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