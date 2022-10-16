package edu.hm.dako.connection.udp;

import edu.hm.dako.connection.ServerSocketInterface;
import edu.hm.dako.connection.Connection;

import java.net.SocketException;

/**
 * Socket für den Server auf UDP-Basis
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class UDPServerSocket implements ServerSocketInterface {
    private final UDPSocket socket;

    /**
     * Konstruktor
     *
     * @param serverPort port des server
     * @param sendBufferSize größe des Sendepuffers
     * @param receiveBufferSize größe des Empfangspuffers
     * @throws SocketException error creating or accessing the socket
     */
    public UDPServerSocket(int serverPort, int sendBufferSize, int receiveBufferSize) throws SocketException {
        this.socket = new UDPSocket(serverPort, sendBufferSize, receiveBufferSize);
    }

    @Override
    public Connection accept() {
        return new UDPServerConnection(socket);
    }

    @Override
    public void close() {
        socket.close();
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }
}