package edu.hm.dako.echoTcpSocketApp;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Workerthread fuer multihreaded Server
 * @author P. Mandl
 */
public class EchoWorkerThread extends Thread {

    Socket connection; // Verbindungssocket
    ObjectOutputStream out; // Ausgabestrom der Verbindung
    ObjectInputStream in; // Eingabestrom der Verbindung

    private boolean connect;

    /**
     * Konstruktor
     *
     * @param connection - Verbindungssocket
     * @throws Exception - Fehler beim Aufbau der Objektstroeme
     */
    public EchoWorkerThread(Socket connection) throws Exception {

        try {
            this.connection = connection;
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
            connect = true;
            this.setName("WorkerThread-"
                    + connection.getLocalPort() + "/" + connection.getPort());
        } catch (Exception e) {
            System.out.println(this.getName() + " : Exception beim Anlegen der Objectstreams");
            throw e;
        }
    }

    /**
     * Thread fuer die Verbindung
     */
    public void run() {
        System.out.println(this.getName() + " gestartet");
        while (connect) {
            try {
                echo();
            } catch (Exception e1) {
                try {
                    connect = false;
                } catch (Exception e2) {
                    connect = false;
                    System.out.println(this.getName() + " : Exception bei Verbindungsabbau");
                }
            }
        }
    }

    /**
     * Nachricht vom Client empfangen und zuruecksenden
     * @throws Exception - Senden oder Empfangen fehlerhaft
     */
    private void echo() throws Exception {
        try {
            SimplePDU receivedPdu = (SimplePDU) in.readObject();
            String message = receivedPdu.getMessage();
            System.out.println("PDU empfangen, Message-Laenge = " + message.length());
            out.writeObject(receivedPdu);
        } catch (Exception e) {
            if (connection.isConnected()) {
                System.out.println("Client hat die Verbindung abgebaut");
                closeConnection();
            }
            throw new Exception();
        }
    }

    /**
     * Verbindung abbauen
     */
    private void closeConnection() {
        try {
            connection.close();
            System.out.println("Verbindung geschlossen");
        } catch (Exception e) {
            System.out.println("Exception waehrend des Schliessen der Verbindung");
        }
    }
}
