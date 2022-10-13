package edu.hm.dako.connection.udp;

import edu.hm.dako.connection.Connection;

import java.io.Serializable;

/**
 * UDP-Verbindung aus Sicht des Clients
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class UdpClientConnection implements Connection {

    /**
     * Timeout für {@link UdpSocket#receive(int)}, das ist die maximale Wartezeit beim Empfang von UDP-Datagrammen
     */
    private final int receivingTimeout;
    // UDP-Socket der Verbindung
    private final UdpSocket clientSocket;

    public UdpClientConnection(UdpSocket clientSocket, int receivingTimeout) {
        this.clientSocket = clientSocket;
        this.receivingTimeout = receivingTimeout;
    }

    @Override
    public Serializable receive(int timeout) throws Exception {
        return (Serializable) clientSocket.receive(timeout);
    }

    @Override
    public Serializable receive() throws Exception {
        return (Serializable) clientSocket.receive(receivingTimeout);
    }

    @Override
    public void send(Serializable message) throws Exception {
        clientSocket.send(clientSocket.getRemoteAddress(), clientSocket.getRemotePort(),
                message);
    }

    @Override
    public void close() {
        clientSocket.close();
    }
}