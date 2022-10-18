package edu.hm.dako.examples.echoTcpApp;

import edu.hm.dako.connection.tcp.TCPConnection;
import edu.hm.dako.connection.tcp.TCPConnectionFactory;

/**
 * Echo Client
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class EchoTCPClient {
    /**
     * Anzahl zu sendender Nachrichten
     */
    static final int NR_OF_MSG = 10;

    /**
     * Nachrichtenlänge
     */
    static final int MAX_LENGTH = 100;
    final TCPConnectionFactory tcpFactory;
    TCPConnection con = null;

    /**
     * Konstruktor
     */
    EchoTCPClient() {
        tcpFactory = new TCPConnectionFactory();
        System.out.println("Client gestartet");
    }

    /**
     * Echo Client
     *
     * @param args currently ignored
     */
    public static void main(String[] args) {
        EchoTCPClient client = new EchoTCPClient();
        try {
            client.connect();
            for (int i = 0; i < NR_OF_MSG; i++) {
                client.echo();
                System.out.println(i + 1 + ". Message gesendet, Länge =  " + MAX_LENGTH);
            }
            client.close();
        } catch (Exception e) {
            System.exit(1);
        }
    }

    /**
     * Einfache Echo-PDU erzeugen
     *
     * @return PDU ChatPDU
     */
    private static SimplePDU createMessage() {
        char[] charArray = new char[MAX_LENGTH];
        for (int j = 0; j < MAX_LENGTH; j++) {
            charArray[j] = 'A';
        }
        return (new SimplePDU(String.valueOf(charArray)));
    }

    /**
     * Verbindung zum Server aufbauen
     *
     * @throws Exception Fehler beim Verbindungsaufbau
     */
    private void connect() throws Exception {
        try {
            con = (TCPConnection) tcpFactory.connectToServer("localhost", 55000, 0,
                    400000, 400000);
            System.out.println("Verbindung steht");
        } catch (Exception e) {
            System.out.println("Exception during connect");
            throw new Exception();
        }
    }

    /**
     * Echo-Request Senden und Echo-Response empfangen
     *
     * @throws Exception Fehler beim Empfang
     */
    private void echo() throws Exception {
        SimplePDU requestPDU = createMessage();
        try {
            con.send(requestPDU);
            SimplePDU responsePDU = (SimplePDU) con.receive();
            System.out.println("Message " + responsePDU.getMessage() + " empfangen");
        } catch (Exception e) {
            System.out.println("Exception during send or receive");
            throw new Exception();
        }
    }

    /**
     * Verbindung abbauen
     *
     * @throws Exception Fehler beim Verbindungsabbau
     */
    private void close() throws Exception {
        try {
            con.close();
            System.out.println("Verbindung abgebaut");
        } catch (Exception e) {
            System.out.println("Exception during close");
            throw new Exception();
        }
    }
}