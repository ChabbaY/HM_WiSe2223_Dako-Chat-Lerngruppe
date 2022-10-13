package edu.hm.dako.echoTcpApp;

import edu.hm.dako.connection.tcp.TcpConnection;
import edu.hm.dako.connection.tcp.TcpServerSocket;

/**
 * SingleThreaded Echo Server
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class EchoTcpServerSingleThreaded {
    TcpServerSocket serverSocket = null;
    TcpConnection con = null;

    public static void main(String[] args) {
        System.out.println("Server gestartet");
        EchoTcpServerSingleThreaded server = new EchoTcpServerSingleThreaded();

        try {
            server.createSocket();
            server.waitForConnection();
            while (true) {
                server.echo();
            }
        } catch (Exception e) {
            System.out.println("Exception beim Echo-Handling");
            server.close();
        }
    }

    /**
     * Server-Socket erzeugen
     *
     * @throws Exception Fehler bei der SocketErzeugung
     */
    private void createSocket() throws Exception {
        try {
            serverSocket = new TcpServerSocket(55000, 400000, 400000);
        } catch (Exception e) {
            System.out.println("Exception");
            throw new Exception();
        }
    }

    /**
     * Auf Verbindungsaufbauwunsch eines Clients warten
     *
     * @throws Exception Fehler bei der Verbindungsentgegennahme
     */
    private void waitForConnection() throws Exception {
        try {
            con = (TcpConnection) serverSocket.accept();
            System.out.println("Verbindung akzeptiert");
        } catch (Exception e) {
            System.out.println("Exception");
            throw new Exception();
        }
    }

    /**
     * Nachricht vom Client empfangen und zurücksenden
     *
     * @throws Exception Fehler beim Receive
     */
    private void echo() throws Exception {
        try {
            SimplePDU receivedPdu = (SimplePDU) con.receive();
            String message = receivedPdu.getMessage();
            System.out.println("PDU empfangen, Message-Länge = " + message.length());
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
