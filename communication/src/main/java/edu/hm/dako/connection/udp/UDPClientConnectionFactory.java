package edu.hm.dako.connection.udp;

import edu.hm.dako.connection.ConnectionFactory;
import edu.hm.dako.connection.Connection;

import java.net.InetAddress;

/**
 * Fabrik für das Erzeugen von Verbindungen zum Server
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class UDPClientConnectionFactory implements ConnectionFactory {
    /**
     * Fabrik für das Erzeugen von Verbindungen zum Server
     */
    public UDPClientConnectionFactory() {
    }
    @Override
    public Connection connectToServer(String remoteServerAddress, int serverPort,
                                      int localPort, int sendBufferSize, int receiveBufferSize) throws Exception {
        UDPSocket udpSocket = new UDPSocket(localPort, sendBufferSize, receiveBufferSize);
        udpSocket.setRemoteAddress(InetAddress.getByName(remoteServerAddress));
        udpSocket.setRemotePort(serverPort);

        // Maximale Wartezeit beim Empfang einer Nachricht in Millisekunden. Wenn in dieser Zeit keine Nachricht kommt,
        // wird das Empfangen abgebrochen. Mit verschiedenen Einstellungen experimentieren.
        int defaultResponseTimeout = 5000;//TODO try different settings

        return new UDPClientConnection(udpSocket, defaultResponseTimeout);
    }
}