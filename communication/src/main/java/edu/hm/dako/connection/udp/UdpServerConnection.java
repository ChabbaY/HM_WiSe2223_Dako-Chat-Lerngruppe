package edu.hm.dako.connection.udp;

import edu.hm.dako.connection.Connection;

import java.io.Serializable;

/**
 * Verbindung aus Sicht des Servers über UDP
 */
public class UdpServerConnection implements Connection {
    private final UdpSocket serverSocket;

    private UdpPseudoConnectionContext udpRemoteObject; // Empfangene Request-PDU

    public UdpServerConnection(UdpSocket serverSocket) {
        this.serverSocket = serverSocket;
        udpRemoteObject = new UdpPseudoConnectionContext();
    }

    /**
     * Der Empfang der Daten vom UDP-Client erfolgt bereits im Konstruktor. Diese
     * Methode gibt nur die bereits empfangene Nachricht zurück.
     *
     * @see edu.hm.dako.connection.Connection#receive()
     */
    @Override
    public Serializable receive(int timeout) throws Exception {
        Object pdu = serverSocket.receive(timeout);
        udpRemoteObject = new UdpPseudoConnectionContext(serverSocket.getRemoteAddress(), serverSocket.getRemotePort(),
                pdu);
        return (Serializable) udpRemoteObject.getObject();
    }

    public Serializable receive() throws Exception {
        Object pdu = serverSocket.receive(0);
        udpRemoteObject = new UdpPseudoConnectionContext(serverSocket.getRemoteAddress(), serverSocket.getRemotePort(),
                pdu);
        return (Serializable) udpRemoteObject.getObject();
    }

    @Override
    public void send(Serializable message) throws Exception {
        serverSocket.send(udpRemoteObject.getRemoteAddress(), udpRemoteObject.getRemotePort(), message);
    }

    /**
     * Dies ist nur eine Dummy-Methode. Der ServerSocket darf nicht geschlossen
     * werden, da der Server sonst keine Requests mehr entgegennehmen kann. Es
     * gibt im Unterschied zu TCP-Sockets keine Verbindungssockets bei UDP,
     * sondern nur ein UDP-Socket, über das alles empfangen wird.
     */
    @Override
    public void close() {
    }
}