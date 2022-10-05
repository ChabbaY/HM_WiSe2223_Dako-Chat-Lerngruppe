package edu.hm.dako.udpMulticast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Multicast Sender
 * Sendet in eine Multicast-Gruppe
 * @author P.Mandl
 * @version 2.0
 */
public class UdpMulticastSender {

    public final static int MY_MULTICAST_PORT = 7000;
    public final static String MY_MULTICAST_ADDRESS = "224.10.1.1";

    /**
     * Hauptprogramm
     * @param args nicht genutzt
     */
    public static void main(String[] args) {

        InetAddress group = null;
        MulticastSocket s = null;

        try {
            group = InetAddress.getByName(MY_MULTICAST_ADDRESS);
            s = new MulticastSocket();
            System.out.println("Multicast socket created");
            // Sender muss nicht in Multicast-Gruppe sein
        } catch (IOException e) {
            System.out.println("Error in creating multicast socket");
            System.exit(1);
        }

        // Nachricht fuellen, lokalen Port hinzugeben
        byte[] buffer;
        String message = "Des is a Multicast von Port ";
        //Integer port = (Integer) s.getLocalPort();
        String messageToSend = message.concat(((Integer) s.getLocalPort()).toString());

        buffer = messageToSend.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer,
                buffer.length, group, MY_MULTICAST_PORT);

        // ... und senden
        try {
            s.send(packet);
            System.out.println("Packet sent to Multicast group "
                    + group.getHostAddress() + ":" + MY_MULTICAST_PORT
                    + " >" + messageToSend + "<");
        } catch (IOException e) {
            System.out.println("Error in sending Multcast packet");
        }
        s.close();
    }
} 
