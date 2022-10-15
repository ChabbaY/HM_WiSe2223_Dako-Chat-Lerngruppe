package edu.hm.dako.connection.udp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.net.BindException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Diese Klasse kapselt die Datagram-Sockets und stellt eine etwas komfortablere Schnittstelle zur Verfügung.
 * Der Mehrwert dieser Klasse im Vergleich zur Standard-DatagramSocket-Klasse ist die Nutzung eines Objektstroms zur
 * Kommunikation über UDP.
 * Achtung: Maximale DatagramLänge: 64 KByte
 *
 * @author Peter Mandl, edited by Lerngruppe
 * @version 1.0.0
 */
public class UdpSocket {
    static final int MAX_BUFFER_SIZE = 65527;
    private static final Logger log = LogManager.getLogger(UdpSocket.class);
    private DatagramSocket socket;
    private InetAddress remoteAddress;
    private int remotePort;

    /**
     * Konstruktor
     *
     * @param port UDP-Port, der lokal für das Datagramm-Socket verwendet werden soll
     * @throws SocketException Fehler beim Erzeugen des Sockets oder beim Erzeugen einer Adresse
     */
    public UdpSocket(int port) throws SocketException {
        try {
            socket = new DatagramSocket(port);
            System.out.println("Größe des Empfangspuffers des Datagram-Sockets: " + socket.getReceiveBufferSize()
                    + " Byte");
            System.out.println("Größe des Sendepuffers des Datagram-Sockets: " + socket.getSendBufferSize() + " Byte");
        } catch (BindException e) {
            log.error("Port " + port + " auf dem Rechner schon in Benutzung, Bind Exception: " + e);
            System.out.println("Port " + port + " auf dem Rechner schon in Benutzung, Bind Exception: " + e);
            throw e;
        } catch (SocketException e) {
            log.error("Datagram-SocketFehler: " + e);
            throw e;
        }
    }

    /**
     * Konstruktor
     *
     * @param port              UDP-Port, der lokal für das Datagramm-Socket verwendet werden soll
     * @param sendBufferSize    Größe des Sendepuffers in Byte
     * @param receiveBufferSize Größe des Empfangspuffers in Byte
     * @throws SocketException Fehler beim Erzeugen des Sockets oder beim Erzeugen einer Adresse
     */
    public UdpSocket(int port, int sendBufferSize, int receiveBufferSize) throws SocketException {
        try {
            socket = new DatagramSocket(port);
            socket.setReceiveBufferSize(receiveBufferSize);
            socket.setSendBufferSize(sendBufferSize);
            System.out.println("Größe des Empfangspuffers des Datagram-Sockets: " + socket.getReceiveBufferSize()
                    + " Byte");
            System.out.println("Größe des Sendepuffers des Datagram-Sockets: " + socket.getSendBufferSize() + " Byte");
        } catch (BindException e) {
            log.error("Port " + port + " auf dem Rechner schon in Benutzung, Bind Exception: " + e);
            System.out.println("Port " + port + " auf dem Rechner schon in Benutzung, Bind Exception: " + e);
            throw e;
        } catch (SocketException e) {
            log.debug("Datagram-SocketFehler: " + e);
        }
    }

    /**
     * Empfangen einer Nachricht über UDP
     *
     * @param timeout Wartezeit in ms
     * @return Referenz auf Nachricht, die empfangen wurde
     * @throws IOException              Fehler bei der Ein-/Ausgabe
     * @throws SocketTimeoutException   Timeout beim Empfang
     * @throws StreamCorruptedException Stream zerstört
     * @throws Exception                Sonstiger Fehler
     */
    public Object receive(int timeout) throws IOException, SocketTimeoutException, StreamCorruptedException, Exception {
        // Maximale Wartezeit beim Empfang einstellen
        try {
            socket.setSoTimeout(timeout);
        } catch (SocketException e) {
            log.error("RECEIVE: " + "Fehler beim Einstellen der maximalen Wartezeit");
            throw e;
        }

        byte[] bytes = new byte[MAX_BUFFER_SIZE];
        DatagramPacket packet = new DatagramPacket(bytes, bytes.length);

        try {
            // Blockiert nur, bis Timeout abgelaufen ist
            socket.receive(packet);
            log.debug("Datagramm empfangen, Datagramm-Länge: " + packet.getLength());
        } catch (SocketTimeoutException e1) {
            log.debug("Timeout beim Empfangen");
            throw e1;
        } catch (IOException e2) {
            log.error("Fehler beim Empfangen eines Datagramms");
            throw e2;
        }

        ByteArrayInputStream baInputStream = new ByteArrayInputStream(packet.getData());
        ObjectInputStream ois = new ObjectInputStream(baInputStream);

        Object pdu;
        try {
            pdu = ois.readObject();

            remoteAddress = packet.getAddress();
            remotePort = packet.getPort();

            log.debug("Entfernter Port: " + packet.getPort() + ", Zielport: " + socket.getLocalPort());
        } catch (ClassNotFoundException e1) {
            log.error("ClassNotFoundException beim Empfang: ", e1);
            // throw e1;
            return null;
        } catch (StreamCorruptedException e2) {
            log.error("Invalid Stream beim Empfang: ", e2);
            // throw e2;
            return null;
        } catch (Exception e3) {
            log.error("Sonstiger schwerwiegender Fehler beim Empfang: ", e3);
            // throw e3;
            return null;
        }
        return pdu;
    }

    /**
     * Senden einer Nachricht über UDP
     *
     * @param remoteAddress Adresse des Empfängers
     * @param remotePort    Port des Empfängers
     * @param pdu           Zu sendende PDU
     * @throws IOException Fehler beim Senden
     */
    public void send(InetAddress remoteAddress, int remotePort, Object pdu) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(pdu);
        byte[] bytes = out.toByteArray();

        log.debug("Zu sendende Bytes: " + bytes.length);

        DatagramPacket packet = new DatagramPacket(bytes, bytes.length, remoteAddress, remotePort);

        log.debug("Senden mit Quelladresse " + packet.getAddress() + ":" + packet.getPort());

        try {
            socket.send(packet);
        } catch (IOException e) {
            log.error("Fehler beim Senden einer PDU");
            throw e;
        }
    }

    /**
     * Datagram-Socket schliessen
     */
    public void close() {
        log.debug("CLOSE: " + "Socket wird geschlossen");
        socket.close();
    }

    /**
     * getter
     *
     * @return Lokale Adresse
     */
    public String getLocalAddress() {
        return socket.getLocalAddress().getHostAddress();
    }

    /**
     * getter
     *
     * @return lokalen Port
     */
    public int getLocalPort() {
        return socket.getLocalPort();
    }

    /**
     * getter
     *
     * @return Remote Adresse
     */
    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * setter
     *
     * @param remoteAddress entfernte Adresse
     */
    public void setRemoteAddress(InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    /**
     * getter
     *
     * @return Remote Port
     */
    public int getRemotePort() {
        return remotePort;
    }

    /**
     * setter
     *
     * @param remotePort entfernter Port
     */
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    /**
     * getter
     *
     * @return isClosed: true when connection closed
     */
    public boolean isClosed() {
        return socket.isClosed();
    }
}