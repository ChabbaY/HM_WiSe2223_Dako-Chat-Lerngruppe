package edu.hm.dako.echoTcpSocketApp;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Multithreaded Echo Client
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class EchoTCPServerMultiThreaded {
    /**
     * Port des Servers
     */
    static final int SERVER_PORT = 55000;

    /**
     * Serversocket für Listen
     */
    ServerSocket serverSocket;

    /**
     * Konstruktor
     *
     * @param serverPort Serverport
     * @throws BindException Port schon belegt
     * @throws IOException   Fehler beim Socket-Zugriff
     */
    public EchoTCPServerMultiThreaded(int serverPort) throws BindException, IOException {
        serverSocket = createServerSocket(serverPort);
    }

    /**
     * Multithreaded Echo Client
     *
     * @param args currently ignored
     */
    public static void main(String[] args) {
        EchoTCPServerMultiThreaded server = null;

        System.out.println("Server gestartet");

        try {
            server = new EchoTCPServerMultiThreaded(SERVER_PORT);
        } catch (Exception e) {
            System.out.println("Exception beim Erzeugen des Server-Sockets");
            System.exit(1);
        }

        boolean listening = true;

        while (listening) {
            try {
                System.out.println("Server wartet auf Verbindungsanfragen ...");
                Socket connection = server.waitForConnection();
                EchoWorkerThread w1 = new EchoWorkerThread(connection);
                w1.start();
            } catch (Exception e) {
                System.out.println("Exception in einem WorkerThread");
                listening = false;
                server.closeServerSocket();
            }
        }
    }

    /**
     * Erzeugt ein TCP-Serversocket und bindet es an einen Port
     *
     * @param port PortNummer, die verwendet werden soll
     * @throws BindException Port schon belegt
     * @throws IOException   I/O-Fehler bei der Socket-Erzeugung
     */
    private ServerSocket createServerSocket(int port) throws BindException, IOException {
        ServerSocket serverSocket;

        try {
            serverSocket = new java.net.ServerSocket();
            InetSocketAddress socketAddress = new InetSocketAddress(port);
            serverSocket.bind(socketAddress);
            return (serverSocket);

        } catch (BindException e) {
            System.out.println(
                    "Port " + port + " auf dem Rechner schon in Benutzung, Bind Exception: " + e);
            throw e;
        } catch (IOException e) {
            System.out.println("Schwerwiegender Fehler beim Anlegen eines TCP-Sockets mit PortNummer "
                    + port + ": " + e);
            throw e;
        }
    }

    /**
     * Auf Verbindungsaufbauwunsch eines Clients warten
     *
     * @return Verbindung zum Client
     * @throws IOException I/O-Fehler bei der Socket-Erzeugung
     */
    private Socket waitForConnection() throws IOException {
        try {
            Socket connection = serverSocket.accept();
            System.out.println("Verbindung akzeptiert");
            return (connection);
        } catch (IOException e) {
            System.out.println("Exception beim Annehmen eines Verbindungswunsches");
            throw e;
        }
    }

    /**
     * Verbindung schliessen
     */
    private void closeServerSocket() {
        try {
            serverSocket.close();
            System.out.println("Verbindung geschlossen");
        } catch (Exception e) {
            System.out.println("Exception beim close");
        }
    }
}