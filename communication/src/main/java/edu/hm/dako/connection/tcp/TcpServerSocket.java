package edu.hm.dako.connection.tcp;

import edu.hm.dako.connection.Connection;
import edu.hm.dako.connection.ServerSocketInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;

/**
 * Server-Socket Implementierung auf TCP-Basis
 * @author Peter Mandl
 */
public class TcpServerSocket implements ServerSocketInterface {

    private static final Logger log = LogManager.getLogger(TcpServerSocket.class);

    private static java.net.ServerSocket serverSocket;
    int sendBufferSize;
    int receiveBufferSize;

    /**
     * Erzeugt ein TCP-Serversocket und bindet es an einen Port.
     * @param port Portnummer, die verwendet werden soll
     * @param sendBufferSize Groesse des Sendepuffers in Byte
     * @param receiveBufferSize Groesse des Empfangspuffers in Byte
     * @throws BindException Port schon belegt
     * @throws IOException I/O-Fehler bei der Socket-Erzeugung
     */
    public TcpServerSocket(int port, int sendBufferSize, int receiveBufferSize)
            throws BindException, IOException {

        this.sendBufferSize = sendBufferSize;
        this.receiveBufferSize = receiveBufferSize;
        try {
            serverSocket = new java.net.ServerSocket();

            // Bind erst nach Setzen der SO_REUSEADDR Option, sonst wird die Option nicht angenommen
            serverSocket.setReuseAddress(true);
            InetSocketAddress socketAddress = new InetSocketAddress(port);
            serverSocket.bind(socketAddress);

        } catch (BindException e) {
            log.debug(
                    "Port " + port + " auf dem Rechner schon in Benutzung, Bind Exception: " + e);
            throw e;
        } catch (IOException e) {
            log.debug("Schwerwiegender Fehler beim Anlegen eines TCP-Sockets mit Portnummer "
                    + port + ": " + e);
            throw e;
        }
    }

    @Override
    public Connection accept() throws IOException {
        return new TcpConnection(serverSocket, sendBufferSize, receiveBufferSize,
                false, true);
    }

    @Override
    public void close() throws IOException {
        log.debug(
                "Serversocket wird geschlossen, lokaler Port: " + serverSocket.getLocalPort());
        serverSocket.close();
    }

    @Override
    public boolean isClosed() {
        return serverSocket.isClosed();
    }
}
