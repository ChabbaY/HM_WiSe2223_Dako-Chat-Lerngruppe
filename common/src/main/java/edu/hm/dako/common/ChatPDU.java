package edu.hm.dako.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.util.Vector;

/**
 * Nachrichtenaufbau für Chat-Protokoll (für alle Nachrichtentypen: Request, Response, Event, Confirm)
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class ChatPDU implements Serializable {
    @Serial
    private static final long serialVersionUID = -6172619032079227585L;

    /**
     * constant for no error
     */
    public final static int NO_ERROR = 0;

    /**
     * constant that a login error occurred
     */
    public final static int LOGIN_ERROR = 1;

    /**
     * referencing the logger
     */
    private static final Logger log = LogManager.getLogger(ChatPDU.class);

    /**
     * Kommandos bzw. PDU-Typen
     */
    private PDUType pduType;

    /**
     * Login-Name des Clients
     */
    private String userName;

    /**
     * Name des Clients, von dem ein Event initiiert wurde
     */
    private String eventUserName;

    /**
     * Name des Client-Threads, der den Request absendet
     */
    private String clientThreadName;

    /**
     * Name des Threads, der den Request im Server
     */
    private String serverThreadName;

    /**
     * Zählt die übertragenen Nachrichten eines Clients, optional nutzbar für unsichere Transportmechanismen bearbeitet
     */
    private long sequenceNumber;

    /**
     * Nutzdaten (eigentliche Chat-Nachricht in Textform)
     */
    private String message;

    /**
     * Liste aller angemeldeten User
     */
    private Vector<String> clients;

    /**
     * Zeit in Nanosekunden, die der Server für die komplette Bearbeitung einer Chat-Nachricht benötigt
     * (inklusive kompletter Verteilung an alle angemeldeten User). Diese Zeit wird vom Server vor dem Absenden der
     * Response eingetragen
     */
    private long serverTime;

    /**
     * Conversation-Status aus Sicht des Servers
     */
    private ClientConversationStatus clientStatus;

    /**
     * Fehlercode, derzeit nur 1 Fehlercode definiert
     */
    private int errorCode;

    /**
     * Daten zur statistischen Auswertung, die mit der Logout-Response-PDU mitgesendet werden:
     * Anzahl der verarbeiteten Chat-Nachrichten des Clients
     */
    private long numberOfReceivedChatMessages;

    /**
     * Anzahl an gesendeten Events an andere Clients
     */
    private long numberOfSentEvents;

    /**
     * Anzahl an empfangenen Bestätigungen der anderen Clients
     */
    private long numberOfReceivedConfirms;

    /**
     * Anzahl verlorener bzw. nicht zugestellten Bestätigungen anderer Clients
     */
    private long numberOfLostConfirms;

    /**
     * Anzahl der Wiederholungen von Nachrichten (nur bei verbindungslosen Transportsystemen)
     */
    private long numberOfRetries;

    /**
     * Konstruktor
     */
    public ChatPDU() {
        pduType = PDUType.UNDEFINED;
        userName = null;
        eventUserName = null;
        clientThreadName = null;
        serverThreadName = null;
        sequenceNumber = 0;
        errorCode = NO_ERROR;
        message = null;
        serverTime = 0;
        clients = null;
        clientStatus = ClientConversationStatus.UNREGISTERED;
        numberOfReceivedChatMessages = 0;
        numberOfSentEvents = 0;
        numberOfReceivedConfirms = 0;
        numberOfLostConfirms = 0;
        numberOfRetries = 0;
    }

    /**
     * Konstruktor
     *
     * @param cmd     Pdu-Typ
     * @param clients ClientListe
     */
    public ChatPDU(PDUType cmd, Vector<String> clients) {
        this.pduType = cmd;
        this.clients = clients;
    }

    /**
     * Konstruktor
     *
     * @param cmd     Pdu-Typ
     * @param message Chat-Nachricht
     */
    public ChatPDU(PDUType cmd, String message) {
        this.pduType = cmd;
        this.message = message;
    }

    /**
     * Ausgabe der PDU ins Logfile
     *
     * @param pdu Chat-PDU
     */
    public static void printPdu(ChatPDU pdu) {
        // System.out.println(pdu);
        log.debug(pdu);
    }

    /**
     * Erzeugen einer Logout-Event-PDU
     *
     * @param userName    Client, der Logout-Request-PDU gesendet hat
     * @param clientList  Liste der registrierten User
     * @param receivedPdu Empfangene PDU (Logout-Request-PDU)
     * @return Erzeugte PDU
     */
    public static ChatPDU createLogoutEventPdu(String userName, Vector<String> clientList, ChatPDU receivedPdu) {
        ChatPDU pdu = new ChatPDU();
        pdu.setPduType(PDUType.LOGOUT_EVENT);
        pdu.setUserName(userName);
        pdu.setEventUserName(userName);
        pdu.setServerThreadName(Thread.currentThread().getName());
        pdu.setClientThreadName(receivedPdu.getClientThreadName());
        pdu.setClients(clientList);
        pdu.setClientStatus(ClientConversationStatus.UNREGISTERING);
        return pdu;
    }

    /**
     * Erzeugen einer Login-Event-PDU
     *
     * @param userName    Client, der Login-Request-PDU gesendet hat
     * @param clientList  Liste der registrierten User
     * @param receivedPdu Empfangene PDU (Login-Request-PDU)
     * @return Erzeugte PDU
     */
    public static ChatPDU createLoginEventPdu(String userName, Vector<String> clientList, ChatPDU receivedPdu) {
        ChatPDU pdu = new ChatPDU();
        pdu.setPduType(PDUType.LOGIN_EVENT);
        pdu.setServerThreadName(Thread.currentThread().getName());
        pdu.setClientThreadName(receivedPdu.getClientThreadName());
        pdu.setUserName(userName);
        pdu.setEventUserName(receivedPdu.getUserName());
        pdu.setUserName(receivedPdu.getUserName());
        pdu.setClients(clientList);
        pdu.setClientStatus(ClientConversationStatus.REGISTERING);
        return pdu;
    }

    /**
     * Erzeugen einer Login-Response-PDU*
     *
     * @param eventInitiator Ursprünglicher Client, der Login-Request-PDU gesendet hat
     * @param receivedPdu    Empfangene PDU
     * @return Erzeugte PDU
     */
    public static ChatPDU createLoginResponsePdu(String eventInitiator, ChatPDU receivedPdu) {
        ChatPDU pdu = new ChatPDU();
        pdu.setPduType(PDUType.LOGIN_RESPONSE);
        pdu.setServerThreadName(Thread.currentThread().getName());
        pdu.setClientThreadName(receivedPdu.getClientThreadName());
        pdu.setUserName(eventInitiator);
        pdu.setClientStatus(ClientConversationStatus.REGISTERED);
        return pdu;
    }

    /**
     * Erzeugen einer Chat-Message-Event-PDU
     *
     * @param userName    Client, der Chat-Message-Request-PDU gesendet hat
     * @param receivedPdu Chat-Message-Request-PDU
     * @return Erzeugte PDU
     */
    public static ChatPDU createChatMessageEventPdu(String userName, ChatPDU receivedPdu) {
        ChatPDU pdu = new ChatPDU();
        pdu.setPduType(PDUType.CHAT_MESSAGE_EVENT);
        pdu.setServerThreadName(Thread.currentThread().getName());
        pdu.setClientThreadName(receivedPdu.getClientThreadName());
        pdu.setUserName(userName);
        pdu.setEventUserName(receivedPdu.getUserName());
        pdu.setSequenceNumber(receivedPdu.getSequenceNumber());
        pdu.setClientStatus(ClientConversationStatus.REGISTERED);
        pdu.setMessage(receivedPdu.getMessage());
        return pdu;
    }

    /**
     * Erzeugen einer Logout-Response-PDU
     *
     * @param eventInitiator                Ursprünglicher Client, der Logout-Request-PDU gesendet hat
     * @param numberOfSentEvents            Anzahl an den Client gesendeter Events
     * @param numberOfLostEventConfirms     Anzahl verlorener EventConfirms des Clients
     * @param numberOfReceivedEventConfirms Anzahl empfangender EventConfirms des Clients
     * @param numberOfRetries               Anzahl wiederholter Nachrichten
     * @param numberOfReceivedChatMessages  Anzahl empfangender Chat-Messages des Clients
     * @param clientThreadName              Name des Client-Threads
     * @return Aufgebaute ChatPDU
     */
    public static ChatPDU createLogoutResponsePdu(String eventInitiator, long numberOfSentEvents,
                                                  long numberOfLostEventConfirms, long numberOfReceivedEventConfirms,
                                                  long numberOfRetries, long numberOfReceivedChatMessages,
                                                  String clientThreadName) {
        ChatPDU pdu = new ChatPDU();
        pdu.setPduType(PDUType.LOGOUT_RESPONSE);
        pdu.setServerThreadName(Thread.currentThread().getName());
        pdu.setClientThreadName(clientThreadName);
        pdu.setClientStatus(ClientConversationStatus.UNREGISTERED);

        // Statistikdaten versorgen
        pdu.setNumberOfSentEvents(numberOfSentEvents);
        pdu.setNumberOfLostEventConfirms(numberOfLostEventConfirms);
        pdu.setNumberOfReceivedEventConfirms(numberOfReceivedEventConfirms);
        pdu.setNumberOfRetries(numberOfRetries);
        pdu.setNumberOfReceivedChatMessages(numberOfReceivedChatMessages);
        pdu.setUserName(eventInitiator);
        return pdu;
    }

    /**
     * Erzeugen einer Chat-Message-Response-PDU
     *
     * @param eventInitiator                Ursprünglicher Client, der Chat-Message-Request-PDU gesendet hat
     * @param numberOfSentEvents            Anzahl an den Client gesendeter Events
     * @param numberOfLostEventConfirms     Anzahl verlorener EventConfirms des Clients
     * @param numberOfReceivedEventConfirms Anzahl empfangender EventConfirms des Clients
     * @param numberOfRetries               Anzahl wiederholter Nachrichten
     * @param numberOfReceivedChatMessages  Anzahl empfangender Chat-Messages des Clients
     * @param clientThreadName              Name des Client-Threads
     * @param serverTime                    RequestBearbeitungszeit im Server
     * @return Erzeugte PDU
     */
    public static ChatPDU createChatMessageResponsePdu(String eventInitiator,
                                                       long numberOfSentEvents, long numberOfLostEventConfirms,
                                                       long numberOfReceivedEventConfirms, long numberOfRetries,
                                                       long numberOfReceivedChatMessages, String clientThreadName,
                                                       long serverTime) {
        ChatPDU pdu = new ChatPDU();
        pdu.setPduType(PDUType.CHAT_MESSAGE_RESPONSE);
        pdu.setServerThreadName(Thread.currentThread().getName());

        pdu.setClientThreadName(clientThreadName);
        pdu.setEventUserName(eventInitiator);
        pdu.setUserName(eventInitiator);

        pdu.setClientStatus(ClientConversationStatus.REGISTERED);

        // Statistikdaten versorgen
        pdu.setSequenceNumber(numberOfReceivedChatMessages);
        pdu.setNumberOfSentEvents(numberOfSentEvents);
        pdu.setNumberOfLostEventConfirms(numberOfLostEventConfirms);
        pdu.setNumberOfReceivedEventConfirms(numberOfReceivedEventConfirms);
        pdu.setNumberOfRetries(numberOfRetries);
        pdu.setNumberOfReceivedChatMessages(numberOfReceivedChatMessages);

        // Serverbearbeitungszeit
        pdu.setServerTime(serverTime);
        return pdu;
    }

    /**
     * Erzeugen einer Login-Response-PDU mit Fehlermeldung
     *
     * @param receivedPdu Empfangene PDU
     * @param errorCode   Fehlercode, der in der PDU übertragen werden soll
     * @return Erzeugte PDU
     */
    public static ChatPDU createLoginErrorResponsePdu(ChatPDU receivedPdu, int errorCode) {
        ChatPDU pdu = new ChatPDU();
        pdu.setPduType(PDUType.LOGIN_RESPONSE);
        pdu.setServerThreadName(Thread.currentThread().getName());
        pdu.setClientThreadName(receivedPdu.getClientThreadName());
        pdu.setUserName(receivedPdu.getUserName());
        pdu.setClientStatus(ClientConversationStatus.UNREGISTERED);
        pdu.setErrorCode(errorCode);
        return pdu;
    }

    /**
     * Erzeugen einer Login-Event-Confirm-PDU
     *
     * @param userName    Name des Clients
     * @param receivedPdu Empfangene PDU
     * @return Erzeugte PDU
     */
    public static ChatPDU createLoginEventConfirm(String userName, ChatPDU receivedPdu) {
        ChatPDU pdu = new ChatPDU();
        pdu.setPduType(PDUType.LOGIN_EVENT_CONFIRM);
        pdu.setClientStatus(ClientConversationStatus.REGISTERED);
        pdu.setClientThreadName(Thread.currentThread().getName());
        pdu.setServerThreadName(receivedPdu.getServerThreadName());
        pdu.setUserName(userName);
        pdu.setEventUserName(receivedPdu.getEventUserName());
        return pdu;
    }

    /**
     * Erzeugen einer Logout-Event-Confirm-PDU
     *
     * @param userName    Name des Clients
     * @param receivedPdu Empfangene PDU
     * @return Erzeugte PDU
     */
    public static ChatPDU createLogoutEventConfirm(String userName, ChatPDU receivedPdu) {
        ChatPDU pdu = new ChatPDU();
        pdu.setPduType(PDUType.LOGOUT_EVENT_CONFIRM);
        pdu.setClientStatus(ClientConversationStatus.UNREGISTERING);
        pdu.setServerThreadName(receivedPdu.getServerThreadName());
        pdu.setUserName(userName);
        pdu.setEventUserName(receivedPdu.getEventUserName());
        return pdu;
    }

    /**
     * Erzeugen einer Chat-Message-Event-Confirm-PDU
     *
     * @param userName    Name des Clients
     * @param receivedPdu Empfangene PDU
     * @return Erzeugte PDU
     */
    public static ChatPDU createChatMessageEventConfirm(String userName, ChatPDU receivedPdu) {
        ChatPDU pdu = new ChatPDU();
        pdu.setPduType(PDUType.CHAT_MESSAGE_EVENT_CONFIRM);
        pdu.setClientStatus(ClientConversationStatus.REGISTERED);
        pdu.setClientThreadName(Thread.currentThread().getName());
        pdu.setServerThreadName(receivedPdu.getServerThreadName());
        pdu.setUserName(userName);
        pdu.setEventUserName(receivedPdu.getEventUserName());
        return pdu;
    }

    @Override
    public String toString() {
        return "\n"
                + "ChatPdu ****************************************************************************************************"
                + "\n" + "PduType: " + this.pduType + ", " + "\n" + "userName: " + this.userName
                + ", " + "\n" + "eventUserName: " + this.eventUserName + ", " + "\n"
                + "clientThreadName: " + this.clientThreadName + ", " + "\n"
                + "serverThreadName: " + this.serverThreadName + ", " + "\n" + "errorCode: "
                + this.errorCode + ", " + "\n" + "sequenceNumber: " + this.sequenceNumber + "\n"
                + "serverTime: " + this.serverTime + ", " + "\n" + "clientStatus: "
                + this.clientStatus + "," + "\n" + "numberOfReceivedChatMessages: "
                + this.numberOfReceivedChatMessages + ", " + "\n" + "numberOfSentEvents: "
                + this.numberOfSentEvents + ", " + "\n" + "numberOfLostConfirms: "
                + this.numberOfLostConfirms + ", " + "\n" + "numberOfRetries: "
                + this.numberOfRetries + "\n" + "clients (Userliste): " + this.clients + ", "
                + "\n" + "message: " + this.message + "\n"
                + "**************************************************************************************************** ChatPdu"
                + "\n";
    }

    /**
     * getter
     *
     * @return pduType
     */
    public PDUType getPduType() {
        return pduType;
    }

    /**
     * setter
     *
     * @param pduType pduType
     */
    public void setPduType(PDUType pduType) {
        this.pduType = pduType;
    }

    /**
     * getter
     *
     * @return clients
     */
    public Vector<String> getClients() {
        return clients;
    }

    /**
     * setter
     *
     * @param clients clients
     */
    public void setClients(Vector<String> clients) {
        this.clients = clients;
    }

    /**
     * getter
     *
     * @return userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * setter
     *
     * @param userName userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * getter
     *
     * @return eventUserName
     */
    public String getEventUserName() {
        return eventUserName;
    }

    /**
     * setter
     *
     * @param eventUserName eventUserName
     */
    public void setEventUserName(String eventUserName) {
        this.eventUserName = eventUserName;
    }

    /**
     * getter
     *
     * @return clientThreadName
     */
    public String getClientThreadName() {
        return (clientThreadName);
    }

    /**
     * setter
     *
     * @param clientThreadName clientThreadName
     */
    public void setClientThreadName(String clientThreadName) {
        this.clientThreadName = clientThreadName;
    }

    /**
     * getter
     *
     * @return serverThreadName
     */
    public String getServerThreadName() {
        return (serverThreadName);
    }

    /**
     * setter
     *
     * @param serverThreadName serverThreadName
     */
    public void setServerThreadName(String serverThreadName) {
        this.serverThreadName = serverThreadName;
    }

    /**
     * getter
     *
     * @return message
     */
    public String getMessage() {
        return (message);
    }

    /**
     * setter
     *
     * @param message message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * getter
     *
     * @return serverTime
     */
    public long getServerTime() {
        return (serverTime);
    }

    /**
     * setter
     *
     * @param serverTime serverTime
     */
    public void setServerTime(long serverTime) {
        this.serverTime = serverTime;
    }

    /**
     * getter
     *
     * @return sequenceNumber
     */
    public long getSequenceNumber() {
        return (sequenceNumber);
    }

    /**
     * setter
     *
     * @param sequenceNumber sequenceNumber
     */
    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * getter
     *
     * @return clientStatus
     */
    public ClientConversationStatus getClientStatus() {
        return clientStatus;
    }

    /**
     * setter
     *
     * @param clientStatus clientStatus
     */
    public void setClientStatus(ClientConversationStatus clientStatus) {
        this.clientStatus = clientStatus;
    }

    /**
     * getter
     *
     * @return numberOfSentEvents
     */
    public long getNumberOfSentEvents() {
        return (numberOfSentEvents);
    }

    /**
     * setter
     *
     * @param numberOfSentEvents numberOfSentEvents
     */
    public void setNumberOfSentEvents(long numberOfSentEvents) {
        this.numberOfSentEvents = numberOfSentEvents;
    }

    /**
     * getter
     *
     * @return numberOfReceivedConfirms
     */
    public long getNumberOfReceivedConfirms() {
        return (numberOfReceivedConfirms);
    }

    /**
     * setter
     *
     * @param numberOfReceivedEventConfirms numberOfReceivedConfirms
     */
    public void setNumberOfReceivedEventConfirms(long numberOfReceivedEventConfirms) {
        this.numberOfReceivedConfirms = numberOfReceivedEventConfirms;
    }

    /**
     * getter
     *
     * @return numberOfLostConfirms
     */
    public long getNumberOfLostConfirms() {
        return (numberOfLostConfirms);
    }

    /**
     * setter
     *
     * @param numberOfLostEventConfirms numberOfLostConfirms
     */
    public void setNumberOfLostEventConfirms(long numberOfLostEventConfirms) {
        this.numberOfLostConfirms = numberOfLostEventConfirms;
    }

    /**
     * getter
     *
     * @return numberOfRetries
     */
    public long getNumberOfRetries() {
        return (numberOfRetries);
    }

    /**
     * setter
     *
     * @param numberOfRetries numberOfRetries
     */
    public void setNumberOfRetries(long numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
    }

    /**
     * getter
     *
     * @return numberOfReceivedChatMessages
     */
    public long getNumberOfReceivedChatMessages() {
        return (numberOfReceivedChatMessages);
    }

    /**
     * setter
     *
     * @param numberOfReceivedChatMessages numberOfReceivedChatMessages
     */
    public void setNumberOfReceivedChatMessages(long numberOfReceivedChatMessages) {
        this.numberOfReceivedChatMessages = numberOfReceivedChatMessages;
    }

    /**
     * getter
     *
     * @return errorCode
     */
    public int getErrorCode() {
        return (errorCode);
    }

    /**
     * setter
     *
     * @param errorCode errorCode
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}