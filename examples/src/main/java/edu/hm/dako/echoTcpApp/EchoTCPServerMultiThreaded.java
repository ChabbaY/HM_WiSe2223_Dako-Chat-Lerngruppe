package edu.hm.dako.echoTcpApp;

import edu.hm.dako.connection.tcp.TCPConnection;
import edu.hm.dako.connection.tcp.TCPServerSocket;

/**
 * Multithreaded Echo Client
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class EchoTCPServerMultiThreaded {
    TCPServerSocket serverSocket = null;
    final TCPConnection con = null;

    /**
     * Multithreaded Echo Client
     *
     * @param args currently ignored
     */
    public static void main(String[] args) {
        System.out.println("Server gestartet");
        EchoTCPServerMultiThreaded server = new EchoTCPServerMultiThreaded();
        try {
            server.createSocket();
        } catch (Exception e) {
            System.out.println("Exception beim Erzeugen des Server-Sockets");
            System.exit(1);
        }

        boolean listening = true;
        while (listening) {
            try {
                System.out.println("Server wartet auf Verbindungsanfragen ...");
                TCPConnection con = server.waitForConnection();
                EchoWorkerThread w1 = new EchoWorkerThread(con);
                w1.start();
            } catch (Exception e3) {
                System.out.println("Exception in einem WorkerThread");
                listening = false;
                server.close();
            }
        }
    }

    /**
     * Konstruktor
     */
    public EchoTCPServerMultiThreaded() {
    }

    /**
     * Server-Socket erzeugen
     *
     * @throws Exception Fehler beim Erzeugen eines Sockets
     */
    private void createSocket() throws Exception {
        try {
            serverSocket = new TCPServerSocket(55000, 400000, 400000);
        } catch (Exception e) {
            System.out.println("Exception");
            throw new Exception();
        }
    }

    /**
     * Auf Verbindungsaufbauwunsch eines Clients warten
     *
     * @return Verbindung zum Client
     * @throws Exception Fehler bei der Entgegennahme der Verbindung
     */
    private TCPConnection waitForConnection() throws Exception {
        try {
            TCPConnection con = (TCPConnection) serverSocket.accept();
            System.out.println("Verbindung akzeptiert");
            return (con);
        } catch (Exception e) {
            System.out.println("Exception");
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