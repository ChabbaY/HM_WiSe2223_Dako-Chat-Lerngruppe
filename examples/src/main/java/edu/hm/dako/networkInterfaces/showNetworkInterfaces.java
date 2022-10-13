package edu.hm.dako.networkInterfaces;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * Ausgabe aller Informationen der vorhandenen Netzwerk-Interfaces
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class showNetworkInterfaces {

    /**
     * Hauptprogramm
     *
     * @param args - Argumente (nicht verwendet)
     */
    public static void main(String[] args) {
        new showNetworkInterfaces().execute();
    }

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