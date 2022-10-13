package edu.hm.dako.echoUdpApp;

import edu.hm.dako.connection.udp.UdpServerConnection;
import edu.hm.dako.connection.udp.UdpServerSocket;

/**
 * SingleThreaded Server (UDP)
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class EchoUdpServer {
    UdpServerSocket serverSocket = null;
    UdpServerConnection con = null;

    public static void main(String[] args) {
        System.out.println("Server gestartet");

        EchoUdpServer server = new EchoUdpServer();

        try {
            server.createSocket();
            server.waitForConnection();
            while (true) {
                server.echo();
            }
        } catch (Exception e) {
            System.out.println("Exception beim Echo-Handling oder Verbindung abgebaut");
            server.close();
        }
    }

    /**
     * Server-Socket erzeugen
     *
     * @throws Exception Fehler in der Verbindung zum Server
     */
    private void createSocket() throws Exception {
        try {
            serverSocket = new UdpServerSocket(55000, 400000, 400000);
        } catch (Exception e) {
            System.out.println("Exception");
            throw new Exception();
        }
    }

    /**
     * Auf Verbindungsaufbauwunsch eines Clients warten
     *
     * @throws Exception Fehler in der Verbindung zum Server
     */
    private void waitForConnection() throws Exception {
        try {
            con = (UdpServerConnection) serverSocket.accept();
            System.out.println("Serversocket erzeugt");
        } catch (Exception e) {
            System.out.println("Exception");
            throw new Exception();
        }
    }

    /**
     * Nachricht vom Client empfangen und zurücksenden
     *
     * @throws Exception Fehler in der Verbindung zum Server
     */
    private void echo() throws Exception {
        try {
            SimplePDU receivedPdu = (SimplePDU) con.receive();
            String message = receivedPdu.getMessage();
            System.out.println("PDU empfangen, Message-Länge = " + message.length() + ", Message: " + message);
            if (message.compareTo("CLOSE") == 0) {
                System.out.println("close erkannt");
                System.out.println("PDU empfangen, Message-Länge = " + message.length() + " Message: " + message);
                System.exit(1);
                throw new Exception();
            }
            con.send(receivedPdu);
        } catch (Exception e) {
            System.out.println("Exception beim Empfang");
            throw new Exception();
        }
    }

    /**
     * Verbindung schliessen
     */
    private void close() {
        try {
            con.close();
            System.out.println("Verbindung geschlossen");
        } catch (Exception e) {
            System.out.println("Exception beim close");
        }
    }
}