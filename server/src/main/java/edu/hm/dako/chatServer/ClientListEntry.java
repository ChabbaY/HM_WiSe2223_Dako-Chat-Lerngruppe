package edu.hm.dako.chatServer;

import edu.hm.dako.common.ClientConversationStatus;
import edu.hm.dako.connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Vector;

/**
 * Eintrag in der serverseitigen Client-Liste zur Verwaltung der angemeldeten User inklusive des Conversation-Status.
 * Der Eintrag enthält auch eine Warteliste für Clients (User), die auf eine Confirm-Nachricht für ein vorher gesendetes
 * Event warten. Diese Liste wird nur im AdvancedChat benötigt.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class ClientListEntry {
    private static final Logger LOG = LogManager.getLogger(ClientListEntry.class);
    // Kennzeichen zum Beenden des Worker-Threads
    boolean finished;
    // Login-Name des Clients
    private String userName;
    // Verbindung-Handle für Transportverbindung zum Client
    private Connection con;
    // Login-Zeitpunkt

    // Ankunftszeit einer Chat-Message für die Serverzeit-Messung
    private long startTime;

    // Conversation-Status des Clients
    private ClientConversationStatus status;

    // Anzahl der verarbeiteten Chat-Nachrichten des Clients (Sequenznummer)
    private long numberOfReceivedChatMessages;

    // Anzahl gesendeter Events (ChatMessageEvents, LoginEvents, LogoutEvents),
    // die der Server für den Client sendet
    private long numberOfSentEvents;

    // Anzahl aller empfangenen Confirms (ChatMessageConfirm, LoginConfirm,
    // LogoutConfirm) für den Client
    private long numberOfReceivedEventConfirms;

    // Anzahl nicht erhaltener Bestätigungen (derzeit nicht genutzt)
    private long numberOfLostEventConfirms;

    // Anzahl an Nachrichtenwiederholungen (derzeit nicht genutzt)
    private long numberOfRetries;

    // Liste, die auf alle Clients verweist, die noch kein Event-Confirm für
    // einen konkret laufenden Request gesendet haben (nur für Advanced Chat notwendig)
    private Vector<String> waitList;

    public ClientListEntry(String userName, Connection con) {
        this.userName = userName;
        this.con = con;
        this.finished = false;
        this.startTime = 0;
        this.status = ClientConversationStatus.UNREGISTERED;
        this.numberOfReceivedChatMessages = 0;
        this.numberOfSentEvents = 0;
        this.numberOfReceivedEventConfirms = 0;
        this.numberOfLostEventConfirms = 0;
        this.numberOfRetries = 0;
        this.waitList = new Vector<>();
    }

    @Override
    public String toString() {
        return "ChatClientListEntry+++++++++++++++++++++++++++++++++++++++++++++" +
                "UserName: " + this.userName +
                "\n" +
                "Connection: " + this.con +
                "\n" +
                "Status: " + this.status +
                "\n" +
                "+++++++++++++++++++++++++++++++++++++++++++++ChatClientListEntry";
    }

    public synchronized String getUserName() {
        return userName;
    }

    public synchronized void setUserName(String userName) {
        this.userName = userName;
    }

    public synchronized Connection getConnection() {
        return (con);
    }

    public synchronized void setConnection(Connection con) {
        this.con = con;
    }

    public synchronized void setLoginTime(long time) {
    }

    public synchronized long getStartTime() {
        return (startTime);
    }

    public synchronized void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public synchronized long getNumberOfReceivedChatMessages() {
        return (numberOfReceivedChatMessages);
    }

    public synchronized void setNumberOfReceivedChatMessages(long nr) {
        this.numberOfReceivedChatMessages = nr;
    }

    public synchronized long getNumberOfSentEvents() {
        return (numberOfSentEvents);
    }

    public synchronized void setNumberOfSentEvents(long nr) {
        this.numberOfSentEvents = nr;
    }

    public synchronized long getNumberOfReceivedEventConfirms() {
        return (numberOfReceivedEventConfirms);
    }

    public synchronized void setNumberOfReceivedEventConfirms(long nr) {
        this.numberOfReceivedEventConfirms = nr;
    }

    public synchronized long getNumberOfLostEventConfirms() {
        return (numberOfLostEventConfirms);
    }

    public synchronized void setNumberOfLostEventConfirms(long nr) {
        this.numberOfLostEventConfirms = nr;
    }

    public synchronized long getNumberOfRetries() {
        return (numberOfRetries);
    }

    public synchronized void setNumberOfRetries(long nr) {
        this.numberOfRetries = nr;
    }

    public synchronized ClientConversationStatus getStatus() {
        return status;
    }

    public synchronized void setStatus(ClientConversationStatus status) {
        this.status = status;
    }

    public synchronized boolean isFinished() {
        return finished;
    }

    public synchronized void setFinished(boolean finished) {
        this.finished = finished;
    }

    public synchronized void increaseNumberOfSentEvents() {
        this.numberOfSentEvents++;
    }

    public synchronized void increaseNumberOfReceivedEventConfirms() {
        this.numberOfReceivedEventConfirms++;
    }

    public synchronized void increaseNumberOfLostEventConfirms() {
        this.numberOfLostEventConfirms++;
    }

    public synchronized void increaseNumberOfReceivedChatMessages() {
        this.numberOfReceivedChatMessages++;
    }

    public synchronized void increaseNumberOfRetries() {
        this.numberOfRetries++;
    }

    /**
     * Ergänzen eines Eintrags in der Warteliste
     *
     * @param userName Name des Clients
     */
    public synchronized void addWaitListEntry(String userName) {
        this.waitList.add(userName);
        LOG.debug("Warteliste von " + this.userName + " ergänzt um " + userName);
    }

    /**
     * Lesen der Warteliste für ein Event
     */
    public synchronized Vector<String> getWaitList() {
        return waitList;
    }

    /**
     * Aktualisierung der Liste aller Clients, die auf ein Confirm für ein
     * gesendetes Event warten
     *
     * @param list ClientListe
     */
    public synchronized void setWaitList(Vector<String> list) {
        this.waitList = list;
        LOG.debug("Warteliste von " + this.userName + ": " + waitList);
    }

    /**
     * Lösche einer Warteliste für ein Event
     */
    public synchronized void clearWaitList() {
        waitList.clear();
    }
}
