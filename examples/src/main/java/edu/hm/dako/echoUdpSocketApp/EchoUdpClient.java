package edu.hm.dako.echoUdpSocketApp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Echo Client auf Basis von UDP Datagram-Sockets
 * @author Peter Mandl, edited by Lerngruppe
 * @version 2.0
 */
public class EchoUdpClient {
    protected DatagramSocket socket;
    protected InetAddress serverAddress;
    protected int serverPort;

    /**
     * Konstruktor
     * @param serverPort PortNummer des Echo-Servers
     * @throws IOException Fehler beim Anlegen des Sockets
     */
    public EchoUdpClient(int serverPort) throws IOException {
        try {
            socket = new DatagramSocket();
            serverAddress = InetAddress.getLocalHost();
            this.serverPort = serverPort;
            System.out.println("Serverhost: " + serverAddress.getHostName() + ", Serverport: " + this.serverPort);
        } catch (IOException e) {
            socket.close();
            throw e;
        }
    }

    /**
     * Hauptprogramm
     * @param args - Argumente (nicht verwendet)
     */
    public static void main(String[] args) {
        final int serverPort = 56000;
        try {
            EchoUdpClient echoClient = new EchoUdpClient(serverPort);
            System.out.println("UDP Echo Client started");
            echoClient.execute();
            System.out.println("UDP Echo Client finished");
        } catch (IOException e) {
            System.out.println("Error in Creating a datagram socket");
            System.out.println("UDP Echo Server not started");
        }
    }

    /**
     * Ausführung der Echo-Verarbeitung
     */
    public void execute() {
        String myMessage = "Des is de Nachricht, die zurück komma soi";

        try {
            sendPacket(myMessage, myMessage.length(), serverAddress, serverPort);
            receivePacket();
        } catch (IOException e) {
            closeSocket();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            System.out.println("Error in sleep");
        }
    }

    /**
     * Datagram senden
     * @param message Zu sendende Nachricht als String
     * @param length Länge der Nachricht
     * @param serverAddress Adresse des Serverrechners
     * @param serverPort Port des Servers
     * @throws IOException Fehler beim Senden
     */
    protected void sendPacket(String message, int length, InetAddress serverAddress, int serverPort) throws IOException {
        byte[] buffer;
        buffer = message.getBytes();

        DatagramPacket packet = new DatagramPacket(buffer, length, serverAddress, serverPort);
        try {
            socket.send(packet);
            String sendMessage = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Message sent : " + length + " Bytes >" + sendMessage + "<");
        } catch (IOException e) {
            System.out.println("Exception in send");
            throw e;
        }
    }

    /**
     * Senden einer Nachricht an den Echo-Server
     * @throws IOException - Fehler beim Empfangen des Echos
     */
    protected void receivePacket() throws IOException {
        byte[] buffer = new byte[65535];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
            String receivedMessage = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Echo received: " + packet.getLength() +
                    " Bytes >" + receivedMessage + "<");
        } catch (IOException e) {
            System.out.println("Exception in receive");
            throw e;
        }
    }

    /**
     * Schliessen des Datagram-Sockets
     */
    protected void closeSocket() {
        try {
            socket.close();
        } catch (Exception e) {
            System.out.println("Error in close");
        }
    }
}