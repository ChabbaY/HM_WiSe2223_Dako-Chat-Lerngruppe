package edu.hm.dako.examples.echoTcpSocketApp;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;

/**
 * Echo Client auf der Basis von TCP Sockets
 *
 * @author Peter Mandl, edited by Lerngruppe
 * @version 2.0.0
 */

public class EchoTCPClient {
    /**
     * Anzahl zu sendender Nachrichten
     */
    static final int NR_OF_MSG = 5;

    /**
     * Port des Servers
     */
    static final int SERVER_PORT = 55000;

    /**
     * Serverrechner
     */
    static final String SERVER_HOST = "localhost";

    /**
     * Verbindungssocket
     */
    Socket connection;

    /**
     * Ausgabestrom der Verbindung
     */
    ObjectOutputStream out;

    /**
     * Eingabestrom der Verbindung
     */
    ObjectInputStream in;

    /**
     * Konstruktor
     */
    EchoTCPClient() {
        System.out.println("Client gestartet");
        out = null;
        in = null;
        connection = null;
    }

    /**
     * Hauptprogramm
     *
     * @param args (nicht benutzt)
     */
    public static void main(String[] args) {

        new EchoTCPClient().execute();
    }

    /**
     * Echo abwickeln
     *
     * @param i Nummer der Nachricht
     * @throws Exception Fehler beim Senden oder Empfangen
     */
    public void echo(int i) throws Exception {
        SimplePDU requestPDU = createMessage(i);
        try {
            send(requestPDU);
            System.out.println("Gesendet >" + requestPDU.getMessage() + "<");
            SimplePDU responsePDU = (SimplePDU) receive();
            System.out.println("Empfangen >" + responsePDU.getMessage() + "<");
        } catch (Exception e) {
            System.out.println("Exception während des Sendens oder Empfangens");
            throw new Exception();
        }
    }

    /**
     * Nachricht blockierend empfangen
     *
     * @return Referenz auf Nachricht
     * @throws IOException Fehler beim Empfang
     */
    public Serializable receive() throws IOException {

        if (!connection.isConnected()) {
            System.out.println("Empfangsversuch, obwohl Verbindung nicht mehr steht");
            throw new IOException();
        }
        try {
            connection.setSoTimeout(0);
            Object message = in.readObject();
            return (Serializable) message;
        } catch (Exception e) {
            System.out.println("Exception beim Empfang " + connection.getInetAddress());
            System.out.println(e.getMessage());
            throw new IOException();
        }
    }

    /**
     * Nachricht senden
     *
     * @param message Nachricht
     * @throws IOException Fehler beim Senden
     */
    public void send(Serializable message) throws IOException {
        if (connection.isClosed()) {
            System.out.println("Sendeversuch, obwohl Socket geschlossen ist");
            throw new IOException();
        }
        if (!connection.isConnected()) {
            System.out.println("Sendeversuch, obwohl Verbindung nicht mehr steht");
            throw new IOException();
        }

        try {
            out.writeObject(message);
        } catch (Exception e) {
            System.out.println("Exception beim Sendeversuch an " + connection.getInetAddress());
            System.out.println(e.getMessage());
            throw new IOException();
        }
    }

    /**
     * Verbindung abbauen
     *
     * @throws Exception Fehler beim Verbindungsaufbau
     */
    public void closeConnection() throws Exception {
        try {
            connection.close();
            System.out.println("Verbindung geschlossen");
        } catch (Exception e) {
            System.out.println("Exception beim Schliessen der Verbindung");
            throw new Exception();
        }
    }

    /**
     * Einfache Echo-PDU erzeugen
     *
     * @param i Nummer der PDU
     * @return Gefüllte PDU
     */
    public SimplePDU createMessage(int i) {
        String message = "";
        message = message.concat(Integer.toString(i));
        message = message.concat(". Nachricht des Clients mit Port ");
        message = message.concat(Integer.toString(connection.getLocalPort()));
        return (new SimplePDU(message));
    }

    /**
     * Verbindung zum Echo Server aufbauen
     * es wird mehrmals versucht, eine Verbindung aufzubauen.
     *
     * @param remoteServerAddress IP-Adresse oder Hostname des Servers
     * @param serverPort          Port des Servers
     * @return Verbindungssocket
     * @throws IOException Fehler beim Verbindungsaufbau
     */
    public Socket connectToServer(String remoteServerAddress, int serverPort) throws IOException {
        Socket connection = null;

        // Maximale Anzahl an Verbindungsaufbauversuchen zum Server, die ein Client
        // unternimmt, bevor er abbricht
        final int MAX_CONNECTION_ATTEMPTS = 3;

        // Zählt die Verbindungsaufbauversuche, bis eine Verbindung vom Server
        // angenommen wird
        long connectionTryCounter = 0;

        boolean connected = false;
        int attempts = 0;
        // condition (attempts < MAX_CONNECTION_ATTEMPTS) excluded, as an exception will be thrown before
        while (!connected) {
            try {
                connectionTryCounter++;
                connection = new Socket(remoteServerAddress, serverPort);
                connected = true;
            } catch (IOException e1) {

                // Ein wenig warten und erneut versuchen
                attempts++;
                try {
                    Thread.sleep(100);
                } catch (Exception ignored) {
                }

            } catch (Exception e) {
                System.out.println("Sonstige Exception beim Verbindungsaufbau " + e.getMessage());
            }
            if (attempts >= MAX_CONNECTION_ATTEMPTS) {
                throw new IOException();
            }
        }

        System.out.println("Anzahl der Verbindungsaufbauversuche für die Verbindung zum Server: "
                + connectionTryCounter);

        out = new ObjectOutputStream(connection.getOutputStream());
        in = new ObjectInputStream(connection.getInputStream());
        return connection;
    }

    /**
     * Eigentliche Programmlogik zur Ausführung eines Echos
     */
    public void execute() {
        // Verbindung zum Server aufbauen
        try {
            connection = connectToServer(SERVER_HOST, SERVER_PORT);
        } catch (IOException e) {
            System.out.println("Exception während des Verbindungsaufbaus");
            System.exit(1);
        }

        // Mehrere Nachrichten senden und auf Echo warten
        for (int i = 0; i < NR_OF_MSG; i++) {
            try {
                // Nur ein wenig warten
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            try {
                echo(i + 1);
            } catch (Exception e1) {
                try {
                    closeConnection();
                } catch (Exception ignored) {
                }
            }
        }
    }
}