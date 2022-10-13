package edu.hm.dako.rmiBeispiel;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * Beispiel für einen RMI-Server
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class oneServer {
    public static void main(String[] args) {
        oneServerImpl myServer = null;

        System.out.println("oneServer wird erzeugt ...");

        // Remote-Objekt registrieren und Server starten
        Registry registry = null;
        try {
            // Registry erzeugen
            registry = LocateRegistry.createRegistry(1099);
        } catch (Exception e) {
            System.out.println("Fehler beim Anlegen des Registries");
            System.exit(1);
        }

        try {
            myServer = new oneServerImpl();
            System.out.println("oneServer erzeugt");
        } catch (Exception e) {
            System.out.println("Fehler beim Erzeugen des oneServers");
            System.exit(2);
        }

        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {
        }

        try {
            Naming.rebind("oneServer", myServer);
            String[] registeredServers = registry.list();

            System.out.print("Aktuell registrierte Objekte im RMI-Registry: ");
            for (String s : registeredServers) {
                System.out.print(s);
            }
            System.out.println();

        } catch (Exception e) {
            System.out.println("Fehler beim Bind");
        }

        System.out.println("oneServer läuft, wartet auf Requests");
    }
}