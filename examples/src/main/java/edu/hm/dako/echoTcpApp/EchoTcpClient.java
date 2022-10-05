package edu.hm.dako.echoTcpApp;

import edu.hm.dako.connection.tcp.TcpConnection;
import edu.hm.dako.connection.tcp.TcpConnectionFactory;

/**
 * Echo Client
 * @author Peter Mandl
 */
public class EchoTcpClient {
    static final int NR_OF_MSG = 10; // Anzahl zu sendender Nachrichten
    static final int MAX_LENGTH = 100; // Nachrichtenlaenge
    TcpConnectionFactory tcpFactory;
    TcpConnection con = null;

    EchoTcpClient() {
        tcpFactory = new TcpConnectionFactory();
        System.out.println("Client gestartet");
    }

    public static void main(String[] args) {

        EchoTcpClient client = new EchoTcpClient();
        try {
            client.connect();
            for (int i = 0; i < NR_OF_MSG; i++) {
                client.echo();
                System.out.println(i + 1 + ". Message gesendet, Laenge =  " + MAX_LENGTH);
            }
            client.close();
        } catch (Exception e) {
            System.exit(1);
        }
    }

    /**
     * Einfache Echo-PDU erzeugen
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
     * @throws Exception Fehler beim Verbindungsaufbau
     */
    private void connect() throws Exception {
        try {
            con = (TcpConnection) tcpFactory.connectToServer("localhost", 55000, 0,
                    400000, 400000);
            System.out.println("Verbindung steht");
        } catch (Exception e) {
            System.out.println("Exception during connect");
            throw new Exception();
        }
    }

    /**
     * Echo-Request Senden und Echo-Response empfangen
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