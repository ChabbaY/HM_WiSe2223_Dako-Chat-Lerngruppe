package edu.hm.dako.examples.echoUdpApp;

import edu.hm.dako.connection.udp.UDPClientConnection;
import edu.hm.dako.connection.udp.UDPClientConnectionFactory;

/**
 * Echo Client(UDP)
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class EchoUDPClient {
    /**
     * Anzahl zu sendender Nachrichten
     */
    static final int NR_OF_MSG = 5;

    /**
     * Nachrichtenlänge
     */
    static final int MAX_LENGTH = 10;
    final UDPClientConnectionFactory udpFactory;
    UDPClientConnection con = null;

    /**
     * Konstruktor
     */
    EchoUDPClient() {
        udpFactory = new UDPClientConnectionFactory();
        System.out.println("Client gestartet");
    }

    /**
     * Echo Client(UDP)
     *
     * @param args currently ignored
     */
    public static void main(String[] args) {
        EchoUDPClient client = new EchoUDPClient();

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
     * @return PDU
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
     * @throws Exception Fehler in der Verbindung zum Server
     */
    private void connect() throws Exception {
        try {
            con = (UDPClientConnection) udpFactory.connectToServer("localhost", 55000,
                    0, 400000, 400000);
            System.out.println("Verbindung steht");
        } catch (Exception e) {
            System.out.println("Exception during connect");
            throw new Exception();
        }
    }

    /**
     * Echo-Request senden und Echo-Response empfangen
     *
     * @throws Exception Fehler in der Verbindung zum Server
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
            SimplePDU closePdu = new SimplePDU("CLOSE");
            con.send(closePdu);
            System.out.println("CLOSE gesendet: " + closePdu.getMessage());
            con.close();
            System.out.println("Verbindung abgebaut");
        } catch (Exception e) {
            System.out.println("Exception beim close");
            throw new Exception();
        }
    }
}