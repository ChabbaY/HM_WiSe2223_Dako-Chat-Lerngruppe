package edu.hm.dako.chatServer;

import edu.hm.dako.common.ClientConversationStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Liste aller angemeldeten Clients. Diese Liste wird im Server als Singleton verwaltet (darf nur einmal erzeugt
 * werden). Alle Worker-Threads im Server nutzen diese Liste.
 * <p>
 * Die Liste wird als HashMap organisiert. Als Schlüssel wird der Username von Clients verwendet.
 * <p>
 * Genereller Hinweis: Zur Umgehung von ConcurrentModificationExceptions wird bei der Iteration durch Listen generell
 * eine Kopie der Liste angelegt.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class SharedChatClientList {
    private static final Logger LOG = LogManager.getLogger(SharedChatClientList.class);
    // Liste aller eingeloggten Clients
    private static ConcurrentHashMap<String, ClientListEntry> clients;

    private static SharedChatClientList instance;

    private SharedChatClientList() {
    }

    /**
     * Thread-sicheres Erzeugen einer Instanz der Liste
     *
     * @return Referenz auf die erzeugte Liste
     */
    public static synchronized SharedChatClientList getInstance() {
        if (SharedChatClientList.instance == null) {
            SharedChatClientList.instance = new SharedChatClientList();
            // ClientListe nur einmal erzeugen
            clients = new ConcurrentHashMap<>();
        }
        return SharedChatClientList.instance;
    }

    /**
     * Löschen der gesamten Liste
     */
    public void deleteAll() {
        clients.clear();
    }

    /**
     * Status eines Clients verändern
     *
     * @param userName  Name des Users (Clients)
     * @param newStatus Neuer Status
     */
    public synchronized void changeClientStatus(String userName, ClientConversationStatus newStatus) {
        ClientListEntry client = clients.get(userName);
        client.setStatus(newStatus);
        clients.replace(userName, client);
        LOG.debug("User " + userName + " nun in Status: " + newStatus);
    }

    /**
     * Lesen des Conversation-Status für einen Client
     *
     * @param userName Name des Users (Clients)
     * @return Conversation-Status des Clients
     */
    public synchronized ClientConversationStatus getClientStatus(String userName) {
        ClientListEntry client = clients.get(userName);
        if (client != null) {
            return (client.getStatus());
        } else {
            return ClientConversationStatus.UNREGISTERED;
        }
    }

    /**
     * Client auslesen
     *
     * @param userName Name des Clients
     * @return Referenz auf den gesuchten Client
     */
    public synchronized ClientListEntry getClient(String userName) {
        return clients.get(userName);
    }

    /**
     * Stellt eine Liste aller Namen der eingetragenen Clients bereit
     *
     * @return Vektor mit allen Namen der eingetragenen Clients
     */
    public synchronized Vector<String> getClientNameList() {
        return new Vector<>(new HashSet<>(clients.keySet()));
    }

    /**
     * Stellt eine Liste aller Namen der eingetragenen Clients bereit, die im Zustand REGISTERING oder REGISTERED sind
     *
     * @return Vektor mit allen Namen der eingetragenen Clients, die registriert sind oder die sich gerade registrieren
     */
    public synchronized Vector<String> getRegisteredClientNameList() {
        Vector<String> clientNameList = new Vector<>();
        for (String s : new HashSet<>(clients.keySet())) {
            if ((getClientStatus(s) == ClientConversationStatus.REGISTERING)
                    || (getClientStatus(s) == ClientConversationStatus.REGISTERED)) {
                clientNameList.add(s);
            }
        }
        return clientNameList;
    }

    /**
     * Prüft, ob ein Client in der Userliste ist
     *
     * @param userName - Name des Clients
     * @return true = Client existiert, false = Client existiert nicht
     */
    public synchronized boolean existsClient(String userName) {
        if (userName != null) {
            if (!clients.containsKey(userName)) {
                LOG.debug("User nicht in ClientListe: " + userName);
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Legt einen neuen Client an
     *
     * @param userName - Name des neuen Clients
     * @param client   - Client-Daten
     */
    public synchronized void createClient(String userName, ClientListEntry client) {
        clients.put(userName, client);
    }

    /**
     * Aktualisierung eines vorhandenen Clients
     *
     * @param userName Name des Clients
     * @param client   Client-Daten
     */
    public synchronized void updateClient(String userName, ClientListEntry client) {
        ClientListEntry existingClient = clients.get(userName);

        if (existingClient != null) {
            clients.put(userName, client);
        } else {
            LOG.debug("User nicht in ClientListe: " + userName);
        }
    }

    /**
     * Prüft, ob ein Client in keiner Warteliste mehr ist und daher gelöscht werden kann
     *
     * @param userName Name des Clients
     * @return true Löschen möglich, sonst false
     */
    public synchronized boolean deletable(String userName) {
        for (String s : new Vector<>(clients.keySet())) {
            ClientListEntry client = clients.get(s);
            if (client.getWaitList().contains(userName)) {
                // Client noch in einer Warteliste
                LOG.debug("Löschen nicht möglich, da Client " + userName
                        + " noch in der Warteliste von " + client.getUserName() + " ist");
                return false;
            }
        }
        return true;
    }

    /**
     * Löscht einen Client zwangsweise inklusive aller Einträge in Wartelisten
     *
     * @param userName Name des Clients
     */
    public synchronized void deleteClientWithoutCondition(String userName) {
        LOG.debug("Client  " + userName + " zwangsweise aus allen Listen entfernen");
        for (String s : new HashSet<>(clients.keySet())) {
            ClientListEntry client = clients.get(s);
            if (client.getWaitList().contains(userName)) {
                LOG.error("Client " + userName
                        + " wird aus der ClientListe entfernt, obwohl er noch in der Warteliste von Client "
                        + client.getUserName() + " ist!");
                client.getWaitList().remove(userName);
            }
        }

        // Client kann nun entfernt werden
        clients.remove(userName);
        LOG.debug("Client  " + userName + " vollständig aus allen Wartelisten entfernt");
    }

    /**
     * Entfernt einen Client aus der ClientListe. Der Client darf nur gelöscht werden, wenn er nicht mehr in der
     * Warteliste eines anderen Client ist.
     *
     * @param userName Name des Clients
     * @return true bei erfolgreichem Löschen, sonst false
     */
    public synchronized boolean deleteClient(String userName) {
        LOG.debug("ClientListe vor dem Löschen von " + userName + ": " + printClientList());
        LOG.debug("Logout für " + userName + ", Länge der ClientListe vor dem Löschen von: " + userName + ": "
                + clients.size());

        boolean deletedFlag = false;
        ClientListEntry removeCandidateClient = clients.get(userName);
        if (removeCandidateClient != null) {
            // Event-Warteliste des Clients leer?
            LOG.debug("Länge der ClientListe " + userName + ": " + clients.size());
            if ((removeCandidateClient.getWaitList().size() == 0) && (removeCandidateClient.isFinished())) {
                // Warteliste leer, jetzt prüfen, ob er noch in anderen
                // Wartelisten ist
                LOG.debug("Warteliste von Client " + removeCandidateClient.getUserName()
                        + " ist leer und Client ist zum Beenden vorgemerkt");

                for (String s : new HashSet<>(clients.keySet())) {
                    ClientListEntry client = clients.get(s);
                    if (client.getWaitList().contains(userName)) {
                        LOG.debug("Löschen nicht möglich, da Client " + userName
                                + " noch in der Warteliste von " + s + " ist");
                        return false;
                    }
                }

                // Client kann entfernt werden, sofern er auch zum Beenden
                // vorgemerkt ist.
                clients.remove(userName);
                deletedFlag = true;
            }
        }

        LOG.debug("Länge der ClientListe nach dem Löschen von " + userName + ": "
                + clients.size());
        LOG.debug("ClientListe nach dem Löschen von " + userName + ": " + printClientList());
        return deletedFlag;
    }

    /**
     * Garbage Collector der ClientListe bereinigt nicht mehr benötigte Clients
     *
     * @return Namensliste aller entfernten Clients
     */
    public synchronized Vector<String> gcClientList() {
        Vector<String> deletedClients = new Vector<>();

        for (String s1 : new Vector<>(clients.keySet())) {
            boolean clientUsed = true;
            ClientListEntry client1 = clients.get(s1);
            if ((client1.getWaitList().size() == 0) && (client1.isFinished())) {
                // Eigene Warteliste leer, jetzt prüfen, ob auch alle anderen
                // Wartelisten diesen Client nicht enthalten
                clientUsed = false;
                for (String s2 : new Vector<>(clients.keySet())) {
                    ClientListEntry client2 = clients.get(s2);
                    if (client2.getWaitList().contains(s1)) {
                        // Client noch in einer Warteliste
                        clientUsed = true;
                    }
                }
            }
            if (!clientUsed) {
                LOG.debug("Garbage Collection: Client " + client1.getUserName()
                        + " wird aus ClientListe entfernt");
                deletedClients.add(s1);
                clients.remove(s1);
            }
        }
        return deletedClients;
    }

    /**
     * Länge der Liste ausgeben
     *
     * @return Länge der Liste
     */
    public synchronized long size() {
        return clients.size();
    }

    /**
     * Erhöht den Zähler für empfangene Chat-Event-Confirm-PDUs für einen Client
     *
     * @param userName Name des Clients
     */
    public synchronized void increaseNumberOfReceivedChatEventConfirms(String userName) {

        ClientListEntry client = clients.get(userName);
        if (client != null) {
            client.increaseNumberOfReceivedEventConfirms();

        }
    }

    /**
     * Erhöht den Zähler für gesendete Chat-Event-PDUs für einen Client
     *
     * @param userName - Name des Clients
     */
    public synchronized void increaseNumberOfSentChatEvents(String userName) {
        ClientListEntry client = clients.get(userName);
        if (client != null) {
            client.increaseNumberOfSentEvents();
        }
    }

    /**
     * Erhöht den Zähler für empfangene Chat-Message-PDUs eines Clients
     *
     * @param userName - Name des Clients
     */
    public synchronized void increaseNumberOfReceivedChatMessages(String userName) {
        ClientListEntry client = clients.get(userName);
        if (client != null) {
            client.increaseNumberOfReceivedChatMessages();
        }
    }

    /**
     * Setzt die Ankunftszeit eines Chat-Requests für die Serverzeitmessung
     *
     * @param userName  - Name des Clients
     * @param startTime - Ankunftszeit
     */

    public synchronized void setRequestStartTime(String userName, long startTime) {
        ClientListEntry client = clients.get(userName);
        if (client != null) {
            client.setStartTime(startTime);
            LOG.debug(
                    "Startzeit für Benutzer " + userName + " gesetzt: " + client.getStartTime());
        } else {
            LOG.debug("Startzeit für Benutzer konnte nicht gesetzt werden:" + userName);
        }
    }

    /**
     * Liefert die Ankunftszeit eines Chat-Requests
     *
     * @param userName Name des Clients
     * @return Ankunftszeit des Requests in ns
     */
    public synchronized long getRequestStartTime(String userName) {
        ClientListEntry client = clients.get(userName);
        if (client != null) {
            return (client.getStartTime());
        } else {
            return 0;
        }
    }

    /**
     * Erstellt eine Liste aller Clients, die noch ein Event bestätigen müssen. Es werden nur registrierte und sich in
     * Registrierung befindliche Clients ausgewählt.
     *
     * @param userName Id des Clients, für den eine Warteliste erstellt werden soll
     * @return Referenz auf Warteliste des Clients
     */
    public synchronized Vector<String> createWaitList(String userName) {
        ClientListEntry client = clients.get(userName);
        if (client != null) {
            for (String s : new HashSet<>(clients.keySet())) {
                // Nur registrierte oder sich gerade registrierende Clients in
                // die Warteliste aufnehmen
                if ((client.getStatus() == ClientConversationStatus.REGISTERED)
                        || (client.getStatus() == ClientConversationStatus.REGISTERING)) {
                    client.addWaitListEntry(s);
                }
            }
            LOG.debug("Warteliste für " + userName + " erzeugt");
        } else {
            LOG.debug("Warteliste für " + userName + " konnte nicht erzeugt werden");
            return null;
        }
        return client.getWaitList();
    }

    /**
     * Löscht eine Event-Warteliste für einen Client
     *
     * @param userName Name des Clients, für den die Liste gelöscht werden soll
     */
    public synchronized void deleteWaitList(String userName) {
        ClientListEntry client = clients.get(userName);
        if (client != null) {
            client.clearWaitList();
        }
    }

    /**
     * Löscht einen Eintrag aus der Event-Warteliste
     *
     * @param userName  Name des Clients, für den ein Listeneintrag aus seiner Warteliste gelöscht werden soll
     * @param entryName name des Clients, der aus der Event-Warteliste gelöscht werden soll
     * @return Anzahl der noch vorhandenen Einträge in der Liste
     * @throws Exception Eintrag, der gelöscht werden sollte, ist nicht vorhanden
     */

    public synchronized int deleteWaitListEntry(String userName, String entryName)
            throws Exception {
        LOG.debug("Client: " + userName + ", aus Warteliste von " + entryName + " löschen ");

        ClientListEntry client = clients.get(userName);

        if (client == null) {
            LOG.debug("Kein Eintrag für " + userName + " in der ClientListe vorhanden");
            throw new Exception();
        } else if (client.getWaitList().size() == 0) {
            LOG.debug("Warteliste für " + userName + " war vorher schon leer");
            return 0;
        } else {
            client.getWaitList().remove(entryName);
            LOG.debug("Eintrag für " + entryName + " aus der Warteliste von " + userName
                    + " gelöscht");
            return client.getWaitList().size();
        }
    }

    /**
     * Liefert die Länge der Event-Warteliste für einen Client
     *
     * @param userName Name des Clients
     * @return Anzahl der noch vorhandenen Einträge in der Liste
     */
    public synchronized int getWaitListSize(String userName) {
        ClientListEntry client = clients.get(userName);
        if (client != null) {
            return client.getWaitList().size();
        }
        return 0;
    }

    /**
     * Setzt Kennzeichen, dass die Arbeit für einen User eingestellt werden kann
     *
     * @param userName - Name des Clients
     */
    public synchronized void finish(String userName) {
        ClientListEntry client = clients.get(userName);
        if (client != null) {
            client.setFinished(true);
            LOG.debug("Finished-Kennzeichen gesetzt für: " + userName);
        }
    }

    /**
     * Ausgeben der aktuellen ClientListe einschliesslich der Wartelisten der Clients
     *
     * @return Liste mit Clients
     */
    public String printClientList() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("ClientListe mit zugehörigen Wartelisten: ");

        if (clients.isEmpty()) {
            stringBuilder.append(" leer\n");
        } else {
            stringBuilder.append("\n");
            for (String s : new HashSet<>(clients.keySet())) {
                ClientListEntry client = clients.get(s);
                stringBuilder.append(client.getUserName()).append(", ");
                stringBuilder.append(client.getWaitList()).append("\n");
            }
        }
        return stringBuilder.toString();
    }
}