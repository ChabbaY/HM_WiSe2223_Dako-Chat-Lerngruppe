package edu.hm.dako.connection.udp;

import edu.hm.dako.connection.Connection;

import java.io.Serializable;

/**
 * UDP-Verbindung aus Sicht des Clients
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class UDPClientConnection implements Connection {

    /**
     * Timeout für {@link UDPSocket#receive(int)}, das ist die maximale Wartezeit beim Empfang von UDP-Datagrammen
     */
    private final int receivingTimeout;
    // UDP-Socket der Verbindung
    private final UDPSocket clientSocket;

    /**
     * UDP-Verbindung aus Sicht des Clients
     *
     * @param clientSocket UDP-Socket der Verbindung
     * @param receivingTimeout die maximale Wartezeit beim Empfang von UDP-Datagrammen
     */
    public UDPClientConnection(UDPSocket clientSocket, int receivingTimeout) {
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