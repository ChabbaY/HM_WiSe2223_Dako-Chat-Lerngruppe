package edu.hm.dako.auditlogserver;

import edu.hm.dako.connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Vector;

/**
 * Eintrag in der serverseitigen Server-Liste zur Verwaltung der angemeldeten Chat-Server.
 * Der Eintrag enthält auch eine Warteliste für Server, die auf eine Confirm-Nachricht für ein vorher gesendetes
 * Event warten. Diese Liste wird nur im AdvancedChat benötigt.
 *
 * @author Linus Englert
 */
public class ServerListEntry {
    /**
     * Referenz auf den logger
     */
    private static final Logger LOG = LogManager.getLogger(ServerListEntry.class);

    /**
     * Kennzeichen zum Beenden des Worker-Threads
     */
    boolean finished;

    /**
     * Adresse des Servers
     */
    private String serverAddress;

    /**
     * Port des Servers
     */
    private String serverPort;

    /**
     * Verbindung-Handle für Transportverbindung zum Server
     */
    private Connection con;

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
     * Liste, die auf alle Server verweist, die noch kein Event-Confirm für einen konkret laufenden Request gesendet
     * haben (nur für Advanced Chat notwendig)
     */
    private Vector<String> waitList;

    /**
     * Konstruktor
     *
     * @param serverAddress address of the server
     * @param serverPort port of the server
     * @param con connection to server
     */
    public ServerListEntry(String serverAddress, String serverPort, Connection con) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.con = con;
        this.finished = false;
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
                "Address: " + this.serverAddress +
                "\n" +
                "Port: " + this.serverPort +
                "\n" +
                "Connection: " + this.con +
                "\n" +
                "+++++++++++++++++++++++++++++++++++++++++++++ChatClientListEntry";
    }

    /**
     * getter
     *
     * @return serveAddress: address of the server
     */
    public synchronized String getServerAddress() {
        return serverAddress;
    }

    /**
     * setter
     *
     * @param serverAddress address of the server
     */
    public synchronized void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**
     * getter
     *
     * @return servePort: port of the server
     */
    public synchronized String getServerPort() {
        return serverPort;
    }

    /**
     * setter
     *
     * @param serverPort port of the server
     */
    public synchronized void setServerPort(String serverPort) {
        this.serverPort = serverPort;
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
     * adds an entry to the wait list
     *
     * @param serverAddress address of the server
     * @param serverPort port of the server
     */
    public synchronized void addWaitListEntry(String serverAddress, String serverPort) {
        this.waitList.add(serverAddress + ":" + serverPort);
        LOG.debug("Warteliste von " + this.serverAddress + ":" + this.serverPort + " ergänzt um " +
                serverAddress + ":" + serverPort);
    }

    /**
     * getter
     *
     * @return waitList
     */
    public synchronized Vector<String> getWaitList() {
        return waitList;
    }

    /**
     * setter
     *
     * @param list wait list
     */
    public synchronized void setWaitList(Vector<String> list) {
        this.waitList = list;
        LOG.debug("Warteliste von " + this.serverAddress + ":" + this.serverPort + ": " + waitList);
    }

    /**
     * clears the wait list
     */
    public synchronized void clearWaitList() {
        waitList.clear();
    }
}