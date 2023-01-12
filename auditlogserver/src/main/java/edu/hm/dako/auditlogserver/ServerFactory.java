package edu.hm.dako.auditlogserver;

import edu.hm.dako.auditlogserver.gui.ALServerGUIInterface;
import edu.hm.dako.common.AuditLogImplementationType;
import edu.hm.dako.connection.Connection;
import edu.hm.dako.connection.ServerSocketInterface;
import edu.hm.dako.connection.tcp.TCPServerSocket;
import edu.hm.dako.connection.udp.UDPServerSocket;
import java.util.concurrent.Executors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * create a new audit log server
 *
 * @author Kilian Brandner
 */
public class ServerFactory {
    /**
     * referencing the logger
     */
    private static final Logger LOG = LogManager.getLogger(ServerFactory.class);

    /**
     * constructor
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
    public static ALServerInterface getServer(AuditLogImplementationType implType, int serverPort,
            int sendBufferSize, int receiveBufferSize,
            ALServerGUIInterface serverGuiInterface) throws Exception {
        LOG.debug("ChatServer (" + implType.toString() + ") wird gestartet, Serverport: "
                + serverPort + ", Sendepuffer: " + sendBufferSize + ", Empfangspuffer: "
                + receiveBufferSize);
        System.out.println("ChatServer (" + implType
                + ") wird gestartet, Listen-Port: " + serverPort + ", Sendepuffer: "
                + sendBufferSize + ", Empfangspuffer: " + receiveBufferSize);

        if (implType == AuditLogImplementationType.AuditLogServerTCPImplementation) {
            try {
                TCPServerSocket tcpServerSocket = new TCPServerSocket(serverPort, sendBufferSize,
                        receiveBufferSize);
                return new AuditLogTcpImpl(Executors.newCachedThreadPool(),
                        getDecoratedServerSocket(tcpServerSocket), serverGuiInterface);
            } catch (Exception e) {
                throw new Exception(e);
            }
        } else if (implType == AuditLogImplementationType.AuditLogServerUDPImplementation) {
            try {
                UDPServerSocket udpServerSocket = new UDPServerSocket(serverPort, sendBufferSize, receiveBufferSize);
                return new AuditLogUdpImpl(Executors.newCachedThreadPool(), getDecoratedServerSocket(udpServerSocket), serverGuiInterface);
            } catch (Exception e) {
                throw new Exception(e);
            }
        } else if (implType == AuditLogImplementationType.AuditLogServerRMIImplementation) {
            try {
                return new AuditLogRmiImpl(serverGuiInterface, serverPort);
            } catch (Exception e) {
                throw new Exception(e);
            }
        }
        System.out.println("Derzeit nur TCPSimpleImplementation implementiert!");
        throw new RuntimeException("Unknown type: " + implType);
    }

    /**
     * Dekoriert ServerSocket mit Logging-Funktionalität
     *
     * @param serverSocket Serverseitiger Kommunikationsendpunkt (für den LISTEN
     *                     Port)
     * @return Referenz auf dekoriertes ServerSocket
     */
    private static ServerSocketInterface getDecoratedServerSocket(ServerSocketInterface serverSocket) {
        return new DecoratingServerSocket(serverSocket);
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