package edu.hm.dako.connection.udp;


import edu.hm.dako.connection.ServerSocketInterface;
import edu.hm.dako.connection.Connection;

import java.io.IOException;
import java.net.SocketException;

/**
 * Socket fuer den Server auf UDP-Basis
 */
public class UdpServerSocket implements ServerSocketInterface {

    private final UdpSocket socket;

    public UdpServerSocket(int serverPort, int sendBufferSize,
                           int receiveBufferSize) throws SocketException {
        this.socket = new UdpSocket(serverPort, sendBufferSize,
                receiveBufferSize);
    }

    @Override
    public Connection accept() throws Exception {
        return new UdpServerConnection(socket);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public boolean isClosed() {
        return socket.isClosed();
    }
}
