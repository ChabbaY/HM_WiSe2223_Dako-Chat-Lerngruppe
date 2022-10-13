package edu.hm.dako.connection.udp;

import edu.hm.dako.connection.ServerSocketInterface;
import edu.hm.dako.connection.Connection;

import java.net.SocketException;

/**
 * Socket für den Server auf UDP-Basis
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class UdpServerSocket implements ServerSocketInterface {
    private final UdpSocket socket;

    public UdpServerSocket(int serverPort, int sendBufferSize, int receiveBufferSize) throws SocketException {
        this.socket = new UdpSocket(serverPort, sendBufferSize, receiveBufferSize);
    }

    @Override
    public Connection accept() {
        return new UdpServerConnection(socket);
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