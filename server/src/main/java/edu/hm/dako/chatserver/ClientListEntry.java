package edu.hm.dako.chatserver;

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
    /**
     * Referenz auf den logger
     */
    private static final Logger LOG = LogManager.getLogger(ClientListEntry.class);

    /**
     * Kennzeichen zum Beenden des Worker-Threads
     */
    boolean finished;

    /**
     * Login-Name des Clients
     */
    private String userName;

    /**
     * Verbindung-Handle für Transportverbindung zum Client
     */
    private Connection con;

    /**
     * Ankunftszeit einer Chat-Message für die Serverzeit-Messung
     */
    private long startTime;

    /**
     * Conversation-Status des Clients
     */
    private ClientConversationStatus status;

    /**
     * Anzahl der verarbeiteten Chat-Nachrichten des Clients (Sequenznummer)
     */
    private long numberOfReceivedChatMessages;

    /**
     * Anzahl gesendeter Events (ChatMessageEvents, LoginEvents, LogoutEvents), die der Server für den Client sendet
     */
    private long numberOfSentEvents;

    /**
     * Anzahl aller empfangenen Confirms (ChatMessageConfirm, LoginConfirm, LogoutConfirm) für den Client
     */
    private long numberOfReceivedEventConfirms;

    /**
     * Anzahl nicht erhaltener Bestätigungen (derzeit nicht genutzt)
     */
    private long numberOfLostEventConfirms;

    /**
     * Anzahl an Nachrichtenwiederholungen (derzeit nicht genutzt)
     */
    private long numberOfRetries;

    /**
     * Liste, die auf alle Clients verweist, die noch kein Event-Confirm für einen konkret laufenden Request gesendet
     * haben (nur für Advanced Chat notwendig)
     */
    private Vector<String> waitList;

    /**
     * Konstruktor
     *
     * @param userName client alias
     * @param con connection to client
     */
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

    /**
     * getter
     *
     * @return userName: client alias
     */
    public synchronized String getUserName() {
        return userName;
    }

    /**
     * setter
     *
     * @param userName client alias
     */
    public synchronized void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * getter
     *
     * @return con: connection to client
     */
    public synchronized Connection getConnection() {
        return (con);
    }

    /**
     * setter
     *
     * @param con connection to client
     */
    public synchronized void setConnection(Connection con) {
        this.con = con;
    }

    /**
     * setter
     *
     * @param time login time of a client
     */
    public synchronized void setLoginTime(long time) {
    }

    /**
     * getter
     *
     * @return startTime: Ankunftszeit einer Chat-Message für die Serverzeit-Messung
     */
    public synchronized long getStartTime() {
        return (startTime);
    }

    /**
     * setter
     *
     * @param startTime Ankunftszeit einer Chat-Message für die Serverzeit-Messung
     */
    public synchronized void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * getter
     *
     * @return numberOfReceivedChatMessages: received messages
     */
    public synchronized long getNumberOfReceivedChatMessages() {
        return (numberOfReceivedChatMessages);
    }

    /**
     * setter
     *
     * @param nr number of received chat messages
     */
    public synchronized void setNumberOfReceivedChatMessages(long nr) {
        this.numberOfReceivedChatMessages = nr;
    }

    /**
     * getter
     *
     * @return numberOfSentEvents: sent events
     */
    public synchronized long getNumberOfSentEvents() {
        return (numberOfSentEvents);
    }

    /**
     * setter
     *
     * @param nr numberOfSentEvents
     */
    public synchronized void setNumberOfSentEvents(long nr) {
        this.numberOfSentEvents = nr;
    }

    /**
     * getter
     *
     * @return numberOfReceivedEventConfirms
     */
    public synchronized long getNumberOfReceivedEventConfirms() {
        return (numberOfReceivedEventConfirms);
    }

    /**
     * setter
     *
     * @param nr numberOfReceivedEventConfirms
     */
    public synchronized void setNumberOfReceivedEventConfirms(long nr) {
        this.numberOfReceivedEventConfirms = nr;
    }

    /**
     * getter
     *
     * @return numberOfLostEventConfirms
     */
    public synchronized long getNumberOfLostEventConfirms() {
        return (numberOfLostEventConfirms);
    }

    /**
     * setter
     *
     * @param nr numberOfLostEventConfirms
     */
    public synchronized void setNumberOfLostEventConfirms(long nr) {
        this.numberOfLostEventConfirms = nr;
    }

    /**
     * getter
     *
     * @return numberOfRetries
     */
    public synchronized long getNumberOfRetries() {
        return (numberOfRetries);
    }

    /**
     * setter
     *
     * @param nr numberOfRetries
     */
    public synchronized void setNumberOfRetries(long nr) {
        this.numberOfRetries = nr;
    }

    /**
     * getter
     *
     * @return status
     */
    public synchronized ClientConversationStatus getStatus() {
        return status;
    }

    /**
     * setter
     *
     * @param status status
     */
    public synchronized void setStatus(ClientConversationStatus status) {
        this.status = status;
    }

    /**
     * getter
     *
     * @return finished
     */
    public synchronized boolean isFinished() {
        return finished;
    }

    /**
     * setter
     *
     * @param finished finished
     */
    public synchronized void setFinished(boolean finished) {
        this.finished = finished;
    }

    /**
     * increases numberOfSentEvents
     */
    public synchronized void increaseNumberOfSentEvents() {
        this.numberOfSentEvents++;
    }

    /**
     * increase numberOfReceivedEventConfirms
     */
    public synchronized void increaseNumberOfReceivedEventConfirms() {
        this.numberOfReceivedEventConfirms++;
    }

    /**
     * increase numberOfLostEventConfirms
     */
    public synchronized void increaseNumberOfLostEventConfirms() {
        this.numberOfLostEventConfirms++;
    }

    /**
     * increase numberOfReceivedChatMessages
     */
    public synchronized void increaseNumberOfReceivedChatMessages() {
        this.numberOfReceivedChatMessages++;
    }

    /**
     * increase numberOfRetries
     */
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
     *
     * @return waitList
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
