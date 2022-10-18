package edu.hm.dako.examples.networkInterfaces;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Ausgabe aller Informationen der vorhandenen Netzwerk-Interfaces
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class ShowNetworkInterfaces {

    /**
     * Hauptprogramm
     *
     * @param args - Argumente (nicht verwendet)
     */
    public static void main(String[] args) {
        new ShowNetworkInterfaces().execute();
    }

    /**
     * Konstruktor
     */
    public ShowNetworkInterfaces() {
    }

    /**
     * alle Adressen
     */
    public void listAllAddresses() {
        Enumeration<NetworkInterface> interfaces;

        try {
            interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                System.out.println(networkInterface.getName() + ", "
                        + networkInterface.getDisplayName() + ", "
                        + networkInterface.getMTU() + ", "
                        + Arrays.toString(networkInterface.getHardwareAddress()) + ", "
                        + networkInterface.isLoopback() + ", "
                        + networkInterface.isUp() + ", "
                        + networkInterface.isVirtual()
                );
            }
        } catch (SocketException ignored) {
        }
    }

    /**
     * alle broadcast Adressen
     * @return ip address list
     */
    public List<InetAddress> listAllBroadcastAddresses() {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces;

        try {
            interfaces = NetworkInterface.getNetworkInterfaces();

            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                broadcastList.addAll(networkInterface.getInterfaceAddresses()
                        .stream()
                        .map(InterfaceAddress::getBroadcast)
                        .filter(Objects::nonNull).toList());
            }
        } catch (SocketException ignored) {
        }

        return broadcastList;
    }

    /**
     * Durchführung der Auflistung
     */
    public void execute() {
        List<InetAddress> list = listAllBroadcastAddresses();
        Iterator<InetAddress> iterator = list.iterator();

        System.out.println("Liste aller vorhandenen Broadcast-Adressen: ");
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        System.out.println("Liste aller vorhandenen Interfaces: ");
        listAllAddresses();
    }
}