package edu.hm.dako.echoTcpSocketApp;

import java.io.IOException;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Multithreaded Echo Client
 * @author P. Mandl
 */
public class EchoTcpServerMultithreaded {
    static final int SERVER_PORT = 55000; // Port des Servers
    ServerSocket serverSocket = null; // Serversocket fuer Listen

    /**
     * Konstruktor
     * @param serverPort Serverport
     * @throws BindException Port schon belegt
     * @throws IOException Fehler beim Socket-Zugriff
     */
    public EchoTcpServerMultithreaded(int serverPort)
            throws BindException, IOException {

        serverSocket = createServerSocket(serverPort);
    }

    public static void main(String[] args) {

        EchoTcpServerMultithreaded server = null;

        System.out.println("Server gestartet");

        try {
            server = new EchoTcpServerMultithreaded(SERVER_PORT);
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
                System.out.println("Exception in einem Workerthread");
                listening = false;
                server.closeServerSocket();
            }
        }
    }

    /**
     * Erzeugt ein TCP-Serversocket und bindet es an einen Port
     * @param port Portnummer, die verwendet werden soll
     * @throws BindException Port schon belegt
     * @throws IOException I/O-Fehler bei der Socket-Erzeugung
     */
    private ServerSocket createServerSocket(int port)
            throws BindException, IOException {

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
            System.out.println("Schwerwiegender Fehler beim Anlegen eines TCP-Sockets mit Portnummer "
                    + port + ": " + e);
            throw e;
        }
    }

    /**
     * Auf Verbindungsaufbauwunsch eines Clients warten
     * @return Verbindung zum Client
     * @throws Exception I/O-Fehler bei der Socket-Erzeugung
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