package edu.hm.dako.udpMulticast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

/**
 * Multicast Receiver
 * Anlegen einer Multicast-Gruppe und Empfangen von Nachrichten über diese.
 * Die neue Methode joinGroup(ab Java 14) erfordert die Angabe des Netzwerk-Interface,
 * über das Multicast-Nachrichten empfangen werden sollen. Dies wird im Beispiel statisch
 * auf "en0" eingestellt. Diese Bezeichnung ist bei macOS für LAN-Interfaces üblich.
 * Besser ist es, das Netzwerk-Interface dynamisch zu ermitteln. Das ist über die Klasse
 * NetworkInterface möglich.
 *
 * @author Peter Mandl, edited by Lerngruppe
 * @version 2.0
 */
public class UdpMulticastReceiver {
    /**
     * multicast port
     */
    public final static int MY_MULTICAST_PORT = 7000;

    /**
     * local port
     */
    public final static int MY_LOCAL_PORT = 7000;

    /**
     * multicast address
     */
    public final static String MY_MULTICAST_ADDRESS = "224.10.1.1";

    /**
     * Hauptprogramm
     *
     * @param args nicht genutzt
     */
    public static void main(String[] args) {
        InetAddress myMulticastAddress;
        InetSocketAddress group;
        MulticastSocket s = null;
        NetworkInterface networkInterface;

        try {
            // Verwendung des Ports 7000 und der IP-Klasse-D-Adresse 224.10.1.1 für die Multicast-Gruppe
            myMulticastAddress = InetAddress.getByName(MY_MULTICAST_ADDRESS);
            group = new InetSocketAddress(myMulticastAddress, MY_LOCAL_PORT);
            s = new MulticastSocket(MY_MULTICAST_PORT);
            System.out.println("Multicast socket created with port " + s.getLocalPort());

            // Zur Multicast-Gruppe beitreten
            networkInterface = NetworkInterface.getByName("en0");
            s.joinGroup(group, networkInterface);
            // s.joinGroup(group); (deprecated)
            System.out.println("Multicast group joined");
        } catch (IOException e) {
            System.out.println("Error in creating multicast socket");
            System.exit(1);
        }

        byte[] block = new byte[1024];
        DatagramPacket packet = new DatagramPacket(block, block.length);

        boolean running = true;

        while (running) {
            try {
                System.out.println("Waiting for multicast message ...");

                // Nachricht empfangen
                s.receive(packet);

                // ... und ausgeben
                String receivedMessage = new String(packet.getData(), 0, packet.getLength());
                System.out.print("Packet received in Multicast group: >");
                System.out.println(receivedMessage + "<");
            } catch (IOException e) {
                System.out.println("Error in receiving Multicast packet");
                running = false;
                s.close();
            }
        }
    }

    /**
     * Konstruktor
     */
    public UdpMulticastReceiver() {
    }
}