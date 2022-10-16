package edu.hm.dako.connection.tcp;

import edu.hm.dako.connection.Connection;
import edu.hm.dako.connection.ConnectionTimeoutException;
import edu.hm.dako.connection.EndOfFileException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 * Implementierung der TCP-Verbindung
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class TCPConnection implements Connection {
    private static final Logger log = LogManager.getLogger(TCPConnection.class);
    // Verwendetes TCP-Socket
    private final Socket socket;
    // Ein- und Ausgabestrom der Verbindung
    private ObjectOutputStream out;
    private ObjectInputStream in;

    /**
     * Verbindungsendpunkt auf Serverseite anlegen
     *
     * @param serverSocket      TCP-Serversocket (mit Listen-Port)
     * @param sendBufferSize    Größe des Sendepuffers in Byte
     * @param receiveBufferSize Größe des Empfangspuffers in Byte
     * @param keepAlive         Option KEEP_ALIVE
     * @param TcpNoDelay        Option TCP_NO_DELAY
     * @throws IOException      if it fails to establish a connection
     */
    public TCPConnection(ServerSocket serverSocket, int sendBufferSize, int receiveBufferSize, boolean keepAlive,
                         boolean TcpNoDelay) throws IOException {
        try {
            // Verbindungsaufbauwunsch akzeptieren
            this.socket = serverSocket.accept();

            // Verbindungsparameter setzen
            setConnectionParameters(sendBufferSize, receiveBufferSize, keepAlive, TcpNoDelay);

            // Ein- und Ausgabe-Objektströme erzeugen
            createObjectStreams();

            log.debug(Thread.currentThread().getName()
                    + ": Verbindung aufgebaut, Remote-TCP-Port " + socket.getPort());
        } catch (IOException e) {
            log.error("IOException beim Anlegen des Verbindungsendpunkts" + e.getMessage());
            throw e;
        }
    }

    /**
     * Verbindungsendpunkt auf Clientseite anlegen.
     * Zur Information: Standardgröße des Empfangspuffers einer TCP-Verbindung: 8192 Byte.
     * Standardgröße des Sendepuffers einer TCP-Verbindung: 8192 Byte.
     *
     * @param remoteServerAddress Entfernter Hostname
     * @param serverPort          Port des Servers
     * @param localHost           Lokale IP_Adresse, die verwendet werden soll
     * @param localPort           Lokaler Port (bei 0 wird einer vergeben)
     * @param sendBufferSize      Größe des Sendepuffers in Byte
     * @param receiveBufferSize   Größe des Empfangspuffers in Byte
     * @param keepAlive           Option KEEP_ALIVE
     * @param TcpNoDelay          Option TCP_NO_DELAY
     * @throws IOException        if it fails to establish a connection
     */
    public TCPConnection(String remoteServerAddress, int serverPort, String localHost,
                         int localPort, int sendBufferSize, int receiveBufferSize,
                         boolean keepAlive, boolean TcpNoDelay) throws IOException {
        try {
            // Socket erzeugen
            this.socket = new Socket();

            // Verbindungsparameter setzen
            setConnectionParameters(sendBufferSize, receiveBufferSize, keepAlive, TcpNoDelay);

            // Adresse binden
            InetSocketAddress myLocalAddress = new InetSocketAddress(localPort);
            socket.bind(myLocalAddress);

            // Verbindung aufbauen
            InetSocketAddress remoteAddress = new InetSocketAddress(remoteServerAddress, serverPort);
            socket.connect(remoteAddress);

            // Ein- und Ausgabe-Objektströme erzeugen
            createObjectStreams();

        } catch (IOException e) {
            log.error("IOException beim Anlegen des Verbindungsendpunkts" + e.getMessage());
            throw e;
        }
    }

    /**
     * Ein- und Ausgabe-Objektströme erzeugen
     * Achtung: Erst Ausgabestrom, dann Eingabestrom erzeugen, sonst Fehler
     * beim Verbindungsaufbau, siehe API-Beschreibung
     */
    private void createObjectStreams() throws IOException {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Verbindungsparameter setzen
     *
     * @param sendBufferSize    Sendepuffergröße
     * @param receiveBufferSize Empfangspuffergröße
     * @param keepAlive         Option KEEP_ALIVE
     * @param TcpNoDelay        Option TCP_NO_DELAY
     */
    private void setConnectionParameters(int sendBufferSize, int receiveBufferSize,
                                         boolean keepAlive, boolean TcpNoDelay) {
        try {
            log.debug("Standardgröße des Empfangspuffers der Verbindung: "
                    + socket.getReceiveBufferSize() + " Byte");
            log.debug("Standardgröße des Sendepuffers der Verbindung: "
                    + socket.getSendBufferSize() + " Byte");
            socket.setReceiveBufferSize(receiveBufferSize);
            socket.setSendBufferSize(sendBufferSize);
            log.debug("Eingestellte Größe des Empfangspuffers der Verbindung: "
                    + socket.getReceiveBufferSize() + " Byte");
            log.debug("Eingestellte Größe des Sendepuffers der Verbindung: "
                    + socket.getSendBufferSize() + " Byte");

            // Weitere TCP-Optionen einstellen
            socket.setTcpNoDelay(TcpNoDelay);
            socket.setKeepAlive(keepAlive);

            // TCP-Optionen ausgeben
            if (socket.getKeepAlive()) {
                log.debug("KeepAlive-Option ist für die Verbindung aktiviert");
            } else {
                log.debug("KeepAlive-Option ist für die Verbindung nicht aktiviert");
            }

            if (socket.getTcpNoDelay()) {
                log.debug("Nagle-Algorithmus ist für die Verbindung aktiviert");
            } else {
                log.debug("Nagle-Algorithmus ist für die Verbindung nicht aktiviert");
            }

            if (socket.getReuseAddress()) {
                log.debug("SO_REUSE_ADDRESS ist für den Port aktiviert, Port kann gleich wieder ohne Wartezeit verwendet werden");
            } else {
                log.debug("SO_REUSE_ADDRESS ist für den Port nicht aktiviert");
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Serializable receive(int timeout)
            throws ConnectionTimeoutException, EndOfFileException {
        if (!socket.isConnected()) {
            log.debug("Empfangsversuch, obwohl Verbindung nicht mehr steht");
            throw new EndOfFileException();
        }

        try {
            socket.setSoTimeout(timeout);
            Object message = in.readObject();
            socket.setSoTimeout(0);
            return (Serializable) message;
        } catch (java.net.SocketTimeoutException e) {
            throw new ConnectionTimeoutException();
        } catch (java.io.EOFException e) {
            log.debug("End of File beim Empfang");
            throw new EndOfFileException();
        } catch (Exception e) {
            log.debug("Vermutlich SocketException: " + e);
            throw new EndOfFileException();
        }
    }

    @Override
    public Serializable receive() throws Exception {
        if (!socket.isConnected()) {
            log.debug("Empfangsversuch, obwohl Verbindung nicht mehr steht");
            throw new EndOfFileException();
        }
        try {
            socket.setSoTimeout(0);
            Object message = in.readObject();
            return (Serializable) message;
        } catch (Exception e) {
            log.debug("Exception beim Empfang " + socket.getInetAddress());
            log.debug(e.getMessage());
            throw new IOException();
        }
    }

    @Override
    public void send(Serializable message) throws Exception {
        if (socket.isClosed()) {
            log.debug("Sendeversuch, obwohl Socket geschlossen ist");
            throw new IOException();
        }
        if (!socket.isConnected()) {
            log.debug("Sendeversuch, obwohl Verbindung nicht mehr steht");
            throw new IOException();
        }

        try {
            out.writeObject(message);
            out.flush();
        } catch (Exception e) {
            log.debug("Exception beim Sendeversuch an " + socket.getInetAddress());
            log.debug(e.getMessage());
            throw new IOException();
        }
    }

    @Override
    public synchronized void close() throws IOException {
        try {
            out.flush();
            log.debug("Verbindungssocket wird geschlossen, lokaler Port: "
                    + socket.getLocalPort() + ", entfernter Port: " + socket.getPort());
            socket.close();
        } catch (Exception e) {
            log.debug("Exception beim Verbindungsabbau " + socket.getInetAddress());
            log.debug(e.getMessage());
            throw new IOException(new IOException());
        }
    }
}