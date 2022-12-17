package edu.hm.dako.auditlogserver;

import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Liste aller angemeldeten Server. Diese Liste wird im Auditlog-Server als
 * Singleton verwaltet
 * (darf nur einmal erzeugt werden). Alle Worker-Threads im Auditlog-Server
 * nutzen diese Liste.
 * <p>
 * Die Liste wird als HashMap organisiert. Als Schlüssel wird der Server Socket
 * (Adresse:Port) verwendet.
 * <p>
 * Genereller Hinweis: Zur Umgehung von ConcurrentModificationExceptions wird
 * bei der Iteration durch Listen generell
 * eine Kopie der Liste angelegt.
 *
 * @author Linus Englert
 */
public class SharedChatServerList {
    /**
     * referencing the logger
     */
    private static final Logger LOG = LogManager.getLogger(SharedChatServerList.class);

    /**
     * Liste aller eingeloggten Clients
     */
    private static ConcurrentHashMap<String, ServerListEntry> clients;

    private static SharedChatServerList instance;

    private SharedChatServerList() {
    }

    /**
     * Thread-sicheres Erzeugen einer Instanz der Liste
     *
     * @return Referenz auf die erzeugte Liste
     */
    public static synchronized SharedChatServerList getInstance() {
        if (SharedChatServerList.instance == null) {
            SharedChatServerList.instance = new SharedChatServerList();
            // ClientListe nur einmal erzeugen
            clients = new ConcurrentHashMap<>();
        }
        return SharedChatServerList.instance;
    }

    /**
     * Löschen der gesamten Liste
     */
    public void clear() {
        clients.clear();
    }

    /**
     * getter
     *
     * @param serverAddress address of the server
     * @param serverPort    port of the server
     * @return Referenz auf den gesuchten Server
     */
    public synchronized ServerListEntry getServer(String serverAddress, String serverPort) {
        return clients.get(serverAddress + ":" + serverPort);
    }

    /**
     * getter
     *
     * @param serverKey key in the hash map
     * @return the mapped server list entry
     */
    public synchronized ServerListEntry getServer(String serverKey) {
        return clients.get(serverKey);
    }

    /**
     * Stellt eine Liste aller Sockets der eingetragenen Server bereit
     *
     * @return Vektor mit allen Sockets der eingetragenen Server
     */
    public synchronized Vector<String> getServerSocketList() {
        return new Vector<>(new HashSet<>(clients.keySet()));
    }

    /**
     * Prüft, ob ein Client in der Userliste ist
     *
     * @param serverAddress address of the server
     * @param serverPort    port of the server
     * @return true = Server existiert, false = Server existiert nicht
     */
    public synchronized boolean existsServer(String serverAddress, String serverPort) {
        if ((serverAddress != null) && (serverPort != null)) {
            String socket = serverAddress + ":" + serverPort;
            if (!clients.containsKey(socket)) {
                LOG.debug("Server nicht in ServerListe: " + socket);
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Legt einen neuen Server an
     *
     * @param serverAddress address of the server
     * @param serverPort    port of the server
     * @param server        Server-Daten
     */
    public synchronized void createServer(String serverAddress, String serverPort, ServerListEntry server) {
        clients.put(serverAddress + ":" + serverPort, server);
    }

    /**
     * Aktualisierung eines vorhandenen Servers
     *
     * @param serverAddress address of the server
     * @param serverPort    port of the server
     * @param server        Server-Daten
     */
    public synchronized void updateServer(String serverAddress, String serverPort, ServerListEntry server) {
        String socket = serverAddress + ":" + serverPort;
        ServerListEntry existingClient = clients.get(socket);

        if (existingClient != null) {
            clients.put(socket, server);
        } else {
            LOG.debug("User nicht in ClientListe: " + socket);
        }
    }

    /**
     * Prüft, ob ein Server in keiner Warteliste mehr ist und daher gelöscht werden
     * kann
     *
     * @param serverAddress address of the server
     * @param serverPort    port of the server
     * @return true Löschen möglich, sonst false
     */
    public synchronized boolean deletable(String serverAddress, String serverPort) {
        String socket = serverAddress + ":" + serverPort;
        for (String s : new Vector<>(clients.keySet())) {
            ServerListEntry server = clients.get(s);
            if (server.getWaitList().contains(socket)) {
                // Client noch in einer Warteliste
                LOG.debug("Löschen nicht möglich, da Client " + socket
                        + " noch in der Warteliste von " + server.getServerAddress() + ":" + server.getServerPort()
                        + " ist");
                return false;
            }
        }
        return true;
    }

    /**
     * Löscht einen Server zwangsweise inklusive aller Einträge in Wartelisten
     *
     * @param serverAddress address of the server
     * @param serverPort    port of the server
     */
    public synchronized void deleteServerWithoutCondition(String serverAddress, String serverPort) {
        String socket = serverAddress + ":" + serverPort;
        LOG.debug("Client  " + socket + " zwangsweise aus allen Listen entfernen");
        for (String s : new HashSet<>(clients.keySet())) {
            ServerListEntry server = clients.get(s);
            if (server.getWaitList().contains(socket)) {
                LOG.error("Client " + socket
                        + " wird aus der ClientListe entfernt, obwohl er noch in der Warteliste von Client "
                        + server.getServerAddress() + ":" + server.getServerPort() + " ist!");
                server.getWaitList().remove(socket);
            }
        }

        // Client kann nun entfernt werden
        clients.remove(socket);
        LOG.debug("Client  " + socket + " vollständig aus allen Wartelisten entfernt");
    }

    /**
     * Entfernt einen Server aus der ServerListe. Der Server darf nur gelöscht
     * werden, wenn er nicht mehr in der
     * Warteliste eines anderen Server ist.
     *
     * @param serverAddress address of the server
     * @param serverPort    port of the server
     * @return true bei erfolgreichem Löschen, sonst false
     */
    public synchronized boolean deleteServer(String serverAddress, String serverPort) {
        String socket = serverAddress + ":" + serverPort;
        LOG.debug("ServerListe vor dem Löschen von " + socket + ": " + printServerList());
        LOG.debug("Logout für " + socket + ", Länge der ServerListe vor dem Löschen von: " + socket + ": "
                + clients.size());

        boolean deletedFlag = false;
        ServerListEntry removeCandidateClient = clients.get(socket);
        if (removeCandidateClient != null) {
            // Event-Warteliste des Servers leer?
            LOG.debug("Länge der ServerListe " + socket + ": " + clients.size());
            if ((removeCandidateClient.getWaitList().size() == 0) && (removeCandidateClient.isFinished())) {
                // Warteliste leer, jetzt prüfen, ob er noch in anderen Wartelisten ist
                LOG.debug("Warteliste von Server " + removeCandidateClient.getServerAddress() + ":"
                        + removeCandidateClient.getServerPort() + " ist leer und Server ist zum Beenden vorgemerkt");

                for (String s : new HashSet<>(clients.keySet())) {
                    ServerListEntry server = clients.get(s);
                    if (server.getWaitList().contains(socket)) {
                        LOG.debug("Löschen nicht möglich, da Server " + socket
                                + " noch in der Warteliste von " + s + " ist");
                        return false;
                    }
                }

                // Server kann entfernt werden, sofern er auch zum Beenden vorgemerkt ist.
                clients.remove(socket);
                deletedFlag = true;
            }
        }

        LOG.debug("Länge der ServerListe nach dem Löschen von " + socket + ": " + clients.size());
        LOG.debug("ServerListe nach dem Löschen von " + socket + ": " + printServerList());
        return deletedFlag;
    }

    /**
     * Garbage Collector der ClientListe bereinigt nicht mehr benötigte Clients
     *
     * @return Namensliste aller entfernten Clients
     */
    public synchronized Vector<String> gcServerList() {
        Vector<String> deletedServers = new Vector<>();

        for (String s1 : new Vector<>(clients.keySet())) {
            boolean clientUsed = true;
            ServerListEntry server1 = clients.get(s1);
            if ((server1.getWaitList().size() == 0) && (server1.isFinished())) {
                // Eigene Warteliste leer, jetzt prüfen, ob auch alle anderen Wartelisten diesen
                // Client nicht enthalten
                clientUsed = false;
                for (String s2 : new Vector<>(clients.keySet())) {
                    ServerListEntry server2 = clients.get(s2);
                    if (server2.getWaitList().contains(s1)) {
                        // Client noch in einer Warteliste
                        clientUsed = true;
                    }
                }
            }
            if (!clientUsed) {
                LOG.debug("Garbage Collection: Client " + server1.getServerAddress() + ":" + server1.getServerPort()
                        + " wird aus ClientListe entfernt");
                deletedServers.add(s1);
                clients.remove(s1);
            }
        }
        return deletedServers;
    }

    /**
     * getter
     *
     * @return size of the list
     */
    public synchronized long size() {
        return clients.size();
    }

    /**
     * Erhöht den Zähler für empfangene Chat-Event-Confirm-PDUs für einen Server
     *
     * @param serverAddress address of the server
     * @param serverPort    port of the server
     */
    public synchronized void increaseNumberOfReceivedChatEventConfirms(String serverAddress, String serverPort) {
        ServerListEntry server = clients.get(serverAddress + ":" + serverPort);
        if (server != null) {
            server.increaseNumberOfReceivedEventConfirms();

        }
    }

    /**
     * Erhöht den Zähler für gesendete Chat-Event-PDUs für einen Server
     *
     * @param serverAddress address of the server
     * @param serverPort    port of the server
     */
    public synchronized void increaseNumberOfSentChatEvents(String serverAddress, String serverPort) {
        ServerListEntry server = clients.get(serverAddress + ":" + serverPort);
        if (server != null) {
            server.increaseNumberOfSentEvents();
        }
    }

    /**
     * Erhöht den Zähler für empfangene Chat-Message-PDUs eines Servers
     *
     * @param serverAddress address of the server
     * @param serverPort    port of the server
     */
    public synchronized void increaseNumberOfReceivedChatMessages(String serverAddress, String serverPort) {
        ServerListEntry server = clients.get(serverAddress + ":" + serverPort);
        if (server != null) {
            server.increaseNumberOfReceivedChatMessages();
        }
    }

    /**
     * Erstellt eine Liste aller Server, die noch ein Event bestätigen müssen.
     *
     * @param serverAddress address of the server
     * @param serverPort    port of the server
     * @return Referenz auf Warteliste des Servers
     */
    public synchronized Vector<String> createWaitList(String serverAddress, String serverPort) {
        ServerListEntry server = clients.get(serverAddress + ":" + serverPort);
        if (server != null) {
            for (String s : new HashSet<>(clients.keySet())) {
                server.addWaitListEntry(serverAddress, serverPort);
            }
            LOG.debug("Warteliste für " + serverAddress + ":" + serverPort + " erzeugt");
        } else {
            LOG.debug("Warteliste für " + serverAddress + ":" + serverPort + " konnte nicht erzeugt werden");
            return null;
        }
        return server.getWaitList();
    }

    /**
     * Löscht eine Event-Warteliste für einen Server
     *
     * @param serverAddress address of the server
     * @param serverPort    port of the server
     */
    public synchronized void deleteWaitList(String serverAddress, String serverPort) {
        ServerListEntry server = clients.get(serverAddress + ":" + serverPort);
        if (server != null) {
            server.clearWaitList();
        }
    }

    /**
     * Löscht einen Eintrag aus der Event-Warteliste
     *
     * @param serverAddress address of the server, für den ein Listeneintrag aus
     *                      seiner Warteliste gelöscht werden soll
     * @param serverPort    port of the server, für den ein Listeneintrag aus seiner
     *                      Warteliste gelöscht werden soll
     * @param entryAddress  address of the server, der aus der Event-Warteliste
     *                      gelöscht werden soll
     * @param entryPort     port of the server, der aus der Event-Warteliste
     *                      gelöscht werden soll
     * @return Anzahl der noch vorhandenen Einträge in der Liste
     * @throws Exception Eintrag, der gelöscht werden sollte, ist nicht vorhanden
     */

    public synchronized int deleteWaitListEntry(String serverAddress, String serverPort,
            String entryAddress, String entryPort) throws Exception {
        String socket = serverAddress + ":" + serverPort;
        String entrySocket = entryAddress + ":" + entryPort;
        LOG.debug("Client: " + entrySocket + ", aus Warteliste von " + socket + " löschen ");

        ServerListEntry server = clients.get(socket);

        if (server == null) {
            LOG.debug("Kein Eintrag für " + socket + " in der ClientListe vorhanden");
            throw new Exception();
        } else if (server.getWaitList().size() == 0) {
            LOG.debug("Warteliste für " + socket + " war vorher schon leer");
            return 0;
        } else {
            server.getWaitList().remove(entrySocket);
            LOG.debug("Eintrag für " + entrySocket + " aus der Warteliste von " + socket + " gelöscht");
            return server.getWaitList().size();
        }
    }

    /**
     * Liefert die Länge der Event-Warteliste für einen Server
     *
     * @param serverAddress address of the server
     * @param serverPort    port of the server
     * @return Anzahl der noch vorhandenen Einträge in der Liste
     */
    public synchronized int getWaitListSize(String serverAddress, String serverPort) {
        ServerListEntry server = clients.get(serverAddress + ":" + serverPort);
        if (server != null) {
            return server.getWaitList().size();
        }
        return 0;
    }

    /**
     * Setzt Kennzeichen, dass die Arbeit für einen User eingestellt werden kann
     *
     * @param serverAddress address of the server
     * @param serverPort    port of the server
     */
    public synchronized void finish(String serverAddress, String serverPort) {
        ServerListEntry server = clients.get(serverAddress + ":" + serverPort);
        if (server != null) {
            server.setFinished(true);
            LOG.debug("Finished-Kennzeichen gesetzt für: " + serverAddress + ":" + serverPort);
        }
    }

    /**
     * Ausgeben der aktuellen ServerListe einschliesslich der Wartelisten der Server
     *
     * @return Liste mit Servern
     */
    public String printServerList() {
        StringBuilder stringBuilder = new StringBuilder("ServerListe mit zugehörigen Wartelisten: ");

        if (clients.isEmpty()) {
            stringBuilder.append(" leer\n");
        } else {
            stringBuilder.append("\n");
            for (String s : new HashSet<>(clients.keySet())) {
                ServerListEntry server = clients.get(s);
                stringBuilder.append(server.getServerAddress()).append(":").append(server.getServerPort()).append(", ");
                stringBuilder.append(server.getWaitList()).append("\n");
            }
        }
        return stringBuilder.toString();
    }
}