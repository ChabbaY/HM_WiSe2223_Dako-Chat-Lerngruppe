package edu.hm.dako.networkInterfaces;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ausgabe aller Informationen der vorhandenen Netzwerk-Interfaces
 *
 * @author P.Mandl
 */
public class showNetworkInterfaces {

    /**
     * Hauptprogramm
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
                        .filter(address -> address.getBroadcast() != null)
                        .map(address -> address.getBroadcast())
                        .collect(Collectors.toList()));
            }
        } catch (SocketException ignored) {
        }

        return broadcastList;
    }

    public void execute() {
        List<InetAddress> list = listAllBroadcastAddresses();
        Iterator iterator = list.iterator();

        System.out.println("Liste aller vorhandenen Broadcast-Adressen: ");
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }

        System.out.println("Liste aller vorhandenen Interfaces: ");
        listAllAddresses();
    }
}
