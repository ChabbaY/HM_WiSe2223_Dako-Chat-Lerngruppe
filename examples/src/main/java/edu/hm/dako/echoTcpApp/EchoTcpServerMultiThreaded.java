package edu.hm.dako.echoTcpApp;

import edu.hm.dako.connection.tcp.TcpConnection;
import edu.hm.dako.connection.tcp.TcpServerSocket;

/**
 * Multithreaded Echo Client
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class EchoTcpServerMultiThreaded {
    TcpServerSocket serverSocket = null;
    final TcpConnection con = null;

    public static void main(String[] args) {
        System.out.println("Server gestartet");
        EchoTcpServerMultiThreaded server = new EchoTcpServerMultiThreaded();
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
                TcpConnection con = server.waitForConnection();
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
     * Server-Socket erzeugen
     *
     * @throws Exception Fehler beim Erzeugen eines Sockets
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
     * @return Verbindung zum Client
     * @throws Exception Fehler bei der Entgegennahme der Verbindung
     */
    private TcpConnection waitForConnection() throws Exception {
        try {
            TcpConnection con = (TcpConnection) serverSocket.accept();
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