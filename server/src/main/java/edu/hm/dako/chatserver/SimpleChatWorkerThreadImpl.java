package edu.hm.dako.chatserver;

import edu.hm.dako.common.AuditLogPDUType;
import edu.hm.dako.common.ClientConversationStatus;
import edu.hm.dako.connection.Connection;
import edu.hm.dako.connection.ConnectionTimeoutException;
import edu.hm.dako.connection.EndOfFileException;
import edu.hm.dako.common.ChatPDU;
import edu.hm.dako.common.ExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Vector;

/**
 * Worker-Thread zur serverseitigen Bedienung einer Session mit einem Client. Jedem Chat-Client wird serverseitig ein
 * Worker-Thread zugeordnet.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class SimpleChatWorkerThreadImpl extends AbstractWorkerThread {
    /**
     * referencing the logger
     */
    private static final Logger LOG = LogManager.getLogger(SimpleChatWorkerThreadImpl.class);

    /**
     * connection to the audit log server
     */
    protected AuditLogConnection auditLogConnection;

    /**
     * true if the audit log server has been enabled on server start
     */
    protected final boolean auditLogServerEnabled;

    /**
     * Erzeugen eines Worker Threads für die Kommunikation mit einem Chat-Client
     *
     * @param con                Verbindung zum Chat-Client
     * @param clients            Liste der angemeldeten Chat-Clients
     * @param counter            Referenz auf diverse Zähler für Tests
     * @param serverGuiInterface Referenz auf GUI des Chat-Servers
     */
    public SimpleChatWorkerThreadImpl(Connection con, SharedChatClientList clients, SharedServerCounter counter,
                                      ServerGUIInterface serverGuiInterface) {
        super(con, clients, counter, serverGuiInterface);
        this.auditLogConnection = null;
        this.auditLogServerEnabled = false;
        System.out.println("WorkerThread ohne AuditLog erzeugt");
    }

    /**
     * Erzeugen eines Worker Threads für die Kommunikation mit einem Chat-Client. Zusätzlich wird
     * eine Verbindung zu einem AuditLog-Server übergeben.
     *
     * @param con                Verbindung zum Chat-Client
     * @param clients            Liste der angemeldeten Chat-Clients
     * @param counter            Referenz auf diverse Zähler für Tests
     * @param serverGuiInterface Referenz auf GUI des Chat-Servers
     * @param auditLogConnection Verbindung zum AuditLog-Server
     */
    public SimpleChatWorkerThreadImpl(Connection con, SharedChatClientList clients,
                                      SharedServerCounter counter, ServerGUIInterface serverGuiInterface,
                                      AuditLogConnection auditLogConnection) {
        super(con, clients, counter, serverGuiInterface);

        if (auditLogConnection != null) {
            this.auditLogServerEnabled = true;
            this.auditLogConnection = auditLogConnection;
            System.out.println("WorkerThread mit AuditLog erzeugt");
        } else {
            this.auditLogServerEnabled = false;
        }
    }

    @Override
    public void run() {
        LOG.debug("ChatWorker-Thread erzeugt, ThreadName: " + Thread.currentThread().getName());
        while (!finished && !Thread.currentThread().isInterrupted()) {
            try {
                // Warte auf nächste Nachricht des Clients und führe
                // entsprechende Aktion aus
                handleIncomingMessage();
            } catch (Exception e) {
                LOG.error("Exception während der Nachrichtenverarbeitung");
                ExceptionHandler.logException(e);
            }
        }
        LOG.debug(Thread.currentThread().getName() + " beendet sich");
        closeConnection();
    }

    /**
     * Senden eines Login-List-Update-Event an alle angemeldeten Clients
     *
     * @param pdu Zu sendende PDU
     */
    protected void sendLoginListUpdateEvent(ChatPDU pdu) {
        // Liste der eingeloggten bzw. sich einloggenden User ermitteln
        Vector<String> clientList = clients.getRegisteredClientNameList();

        LOG.debug("Aktuelle ClientListe, die an die Clients übertragen wird: " + clientList);

        pdu.setClients(clientList);

        Vector<String> clientList2 = clients.getClientNameList();
        new Vector<>(clientList2).forEach(s -> {
            LOG.debug("Für " + s
                    + " wird Login- oder Logout-Event-PDU an alle aktiven Clients gesendet");
            ClientListEntry client = clients.getClient(s);
            try {
                if (client != null) {

                    client.getConnection().send(pdu);
                    LOG.debug("Login- oder Logout-Event-PDU an " + client.getUserName() + " gesendet");
                    clients.increaseNumberOfSentChatEvents(client.getUserName());
                    eventCounter.getAndIncrement();
                }
            } catch (Exception e) {
                LOG.error("Senden einer Login- oder Logout-Event-PDU an " + s + " nicht möglich");
                ExceptionHandler.logException(e);
            }
        });
    }

    @Override
    protected void loginRequestAction(ChatPDU receivedPdu) {
        ChatPDU pdu;
        LOG.debug("Login-Request-PDU für " + receivedPdu.getUserName() + " empfangen");

        // Neuer Client möchte sich einloggen, Client in Client-Liste
        // eintragen
        if (!clients.existsClient(receivedPdu.getUserName())) {
            LOG.debug("User nicht in ClientListe: " + receivedPdu.getUserName());
            ClientListEntry client = new ClientListEntry(receivedPdu.getUserName(), connection);
            client.setLoginTime(System.nanoTime());
            clients.createClient(receivedPdu.getUserName(), client);
            clients.changeClientStatus(receivedPdu.getUserName(),
                    ClientConversationStatus.REGISTERING);
            LOG.debug("User " + receivedPdu.getUserName() + " nun in ClientListe");

            userName = receivedPdu.getUserName();
            clientThreadName = receivedPdu.getClientThreadName();
            Thread.currentThread().setName(receivedPdu.getUserName());
            LOG.debug("Länge der ClientListe: " + clients.size());
            serverGuiInterface.increaseNumberOfLoggedInClients();

            // Login-Event an alle Clients (auch an den gerade aktuell
            // anfragenden) senden
            Vector<String> clientList = clients.getClientNameList();
            pdu = ChatPDU.createLoginEventPdu(userName, clientList, receivedPdu);
            sendLoginListUpdateEvent(pdu);

            // Login Response senden
            ChatPDU responsePdu = ChatPDU.createLoginResponsePdu(userName, receivedPdu);

            try {
                clients.getClient(userName).getConnection().send(responsePdu);
            } catch (Exception e) {
                LOG.debug("Senden einer Login-Response-PDU an " + userName + " fehlgeschlagen");
                LOG.debug("Exception Message: " + e.getMessage());
            }

            LOG.debug("Login-Response-PDU an Client " + userName + " gesendet");

            // Zustand des Clients ändern
            clients.changeClientStatus(userName, ClientConversationStatus.REGISTERED);
        } else {
            // User bereits angemeldet, Fehlermeldung an Client senden,
            // Fehlercode an Client senden
            pdu = ChatPDU.createLoginErrorResponsePdu(receivedPdu, ChatPDU.LOGIN_ERROR);

            try {
                connection.send(pdu);
                LOG.debug("Login-Response-PDU an " + receivedPdu.getUserName()
                        + " mit Fehlercode " + ChatPDU.LOGIN_ERROR + " gesendet");
            } catch (Exception e) {
                LOG.debug("Senden einer Login-Response-PDU an " + receivedPdu.getUserName()
                        + " nicht möglich");
                ExceptionHandler.logExceptionAndTerminate(e);
            }
        }
    }

    @Override
    protected void logoutRequestAction(ChatPDU receivedPdu) {
        ChatPDU pdu;
        logoutCounter.getAndIncrement();
        LOG.debug("Logout-Request von " + receivedPdu.getUserName() + ", LogoutCount = "
                + logoutCounter.get());

        LOG.debug("Logout-Request-PDU von " + receivedPdu.getUserName() + " empfangen");

        if (!clients.existsClient(userName)) {
            LOG.debug("User nicht in ClientListe: " + receivedPdu.getUserName());
        } else {
            // Event an den Client versenden
            Vector<String> clientList = clients.getClientNameList();
            pdu = ChatPDU.createLogoutEventPdu(userName, clientList, receivedPdu);

            clients.changeClientStatus(receivedPdu.getUserName(),
                    ClientConversationStatus.UNREGISTERING);
            sendLoginListUpdateEvent(pdu);
            serverGuiInterface.decreaseNumberOfLoggedInClients();

            // Der Thread muss hier noch warten, bevor eine Logout-Response gesendet wird, da sich sonst ein Client
            // abmeldet, bevor er seinen letzten Event empfangen hat. das funktioniert nicht bei einer grossen Anzahl
            // an Clients (kalkulierte Events stimmen dann nicht mit tatsächlich empfangenen Events überein). In der
            // Advanced-Variante wird noch ein Confirm gesendet, das ist sicherer.
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                ExceptionHandler.logException(e);
            }

            clients.changeClientStatus(receivedPdu.getUserName(),
                    ClientConversationStatus.UNREGISTERED);

            // Logout Response senden
            sendLogoutResponse(receivedPdu.getUserName());

            // Worker-Thread des Clients, der den Logout-Request gesendet hat, auch gleich zum Beenden markieren
            clients.finish(receivedPdu.getUserName());
            LOG.debug("Länge der ClientListe beim Vormerken zum Löschen von " + receivedPdu.getUserName() + ": "
                    + clients.size());
        }
    }

    @Override
    protected void chatMessageRequestAction(ChatPDU receivedPdu) {
        ClientListEntry client;
        clients.setRequestStartTime(receivedPdu.getUserName(), startTime);
        clients.increaseNumberOfReceivedChatMessages(receivedPdu.getUserName());
        serverGuiInterface.increaseNumberOfRequests();
        LOG.debug("Chat-Message-Request-PDU von " + receivedPdu.getUserName() + " mit Sequenznummer "
                + receivedPdu.getSequenceNumber() + " empfangen");

        if (!clients.existsClient(receivedPdu.getUserName())) {
            LOG.debug("User nicht in ClientListe: " + receivedPdu.getUserName());
        } else {
            // Liste der betroffenen Clients ermitteln
            Vector<String> sendList = clients.getClientNameList();
            ChatPDU pdu = ChatPDU.createChatMessageEventPdu(userName, receivedPdu);

            // Event an Clients senden
            for (String s : new Vector<>(sendList)) {
                client = clients.getClient(s);
                try {
                    if ((client != null) && (client.getStatus() != ClientConversationStatus.UNREGISTERED)) {
                        pdu.setUserName(client.getUserName());
                        client.getConnection().send(pdu);
                        LOG.debug("Chat-Event-PDU an " + client.getUserName() + " gesendet");
                        clients.increaseNumberOfSentChatEvents(client.getUserName());
                        eventCounter.getAndIncrement();
                        LOG.debug(userName + ": EventCounter erhöht = " + eventCounter.get()
                                + ", Aktueller ConfirmCounter = " + confirmCounter.get()
                                + ", Anzahl gesendeter ChatMessages von dem Client = "
                                + receivedPdu.getSequenceNumber());
                    }
                } catch (Exception e) {
                    LOG.debug("Senden einer Chat-Event-PDU an " + client.getUserName() + " nicht möglich");
                    ExceptionHandler.logException(e);
                }
            }

            client = clients.getClient(receivedPdu.getUserName());
            if (client != null) {
                ChatPDU responsePdu = ChatPDU.createChatMessageResponsePdu(
                        receivedPdu.getUserName(), 0, 0, 0, 0,
                        client.getNumberOfReceivedChatMessages(), receivedPdu.getClientThreadName(),
                        (System.nanoTime() - client.getStartTime()));

                if (responsePdu.getServerTime() / 1000000 > 100) {
                    LOG.debug(Thread.currentThread().getName()
                            + ", Benötigte Serverzeit vor dem Senden der Response-Nachricht > 100 ms: "
                            + responsePdu.getServerTime() + " ns = "
                            + responsePdu.getServerTime() / 1000000 + " ms");
                }

                try {
                    client.getConnection().send(responsePdu);
                    LOG.debug(
                            "Chat-Message-Response-PDU an " + receivedPdu.getUserName() + " gesendet");
                } catch (Exception e) {
                    LOG.debug("Senden einer Chat-Message-Response-PDU an " + client.getUserName()
                            + " nicht möglich");
                    ExceptionHandler.logExceptionAndTerminate(e);
                }
            }
            LOG.debug("Aktuelle Länge der ClientListe: " + clients.size());
        }
    }

    /**
     * Verbindung zu einem Client ordentlich abbauen
     */
    private void closeConnection() {

        LOG.debug("Schliessen der Chat-Connection zum " + userName);

        // Bereinigen der ClientListe, falls erforderlich

        if (clients.existsClient(userName)) {
            LOG.debug("Close Connection für " + userName
                    + ", Länge der ClientListe vor dem bedingungslosen Löschen: " + clients.size());

            clients.deleteClientWithoutCondition(userName);
            LOG.debug("Länge der ClientListe nach dem bedingungslosen Löschen von " + userName + ": " + clients.size());
        }

        try {
            connection.close();
        } catch (Exception e) {
            LOG.debug("Exception bei close");
            // ExceptionHandler.logException(e);
        }
    }

    /**
     * Antwort-PDU für den initiierenden Client aufbauen und senden
     *
     * @param eventInitiatorClient Name des Clients
     */
    private void sendLogoutResponse(String eventInitiatorClient) {

        ClientListEntry client = clients.getClient(eventInitiatorClient);

        if (client != null) {
            ChatPDU responsePdu = ChatPDU.createLogoutResponsePdu(eventInitiatorClient, 0, 0, 0,
                    0, client.getNumberOfReceivedChatMessages(), clientThreadName);

            LOG.debug(eventInitiatorClient + ": SentEvents aus ClientListe: "
                    + client.getNumberOfSentEvents() + ": ReceivedConfirms aus ClientListe: "
                    + client.getNumberOfReceivedEventConfirms());
            try {
                clients.getClient(eventInitiatorClient).getConnection().send(responsePdu);
            } catch (Exception e) {
                LOG.debug("Senden einer Logout-Response-PDU an " + eventInitiatorClient + " fehlgeschlagen");
                LOG.debug("Exception Message: " + e.getMessage());
            }

            LOG.debug("Logout-Response-PDU an Client " + eventInitiatorClient + " gesendet");
        }
    }

    /**
     * Prüft, ob Clients aus der ClientListe gelöscht werden können
     *
     * @return boolean, true: Client gelöscht, false: Client nicht gelöscht
     */
    private boolean checkIfClientIsDeletable() {

        ClientListEntry client;

        // Worker-Thread beenden, wenn sein Client schon abgemeldet ist
        if (userName != null) {
            client = clients.getClient(userName);
            if (client != null) {
                if (client.isFinished()) {
                    // Lösche den Client aus der ClientListe. Ein Löschen ist aber nur zulässig, wenn der Client
                    // nicht mehr in einer anderen Warteliste ist
                    LOG.debug("Länge der ClientListe vor dem Entfernen von " + userName + ": "
                            + clients.size());
                    if (clients.deleteClient(userName)) {
                        // Jetzt kann auch Worker-Thread beendet werden
                        LOG.debug("Länge der ClientListe nach dem Entfernen von " + userName + ": "
                                + clients.size());
                        LOG.debug("Worker-Thread für " + userName + " zum Beenden vorgemerkt");
                        return true;
                    }
                }
            }
        }

        // Garbage Collection in der ClientListe durchführen
        Vector<String> deletedClients = clients.gcClientList();
        if (deletedClients.contains(userName)) {
            LOG.debug("Über Garbage Collector ermittelt: Laufender Worker-Thread für " + userName
                    + " kann beendet werden");
            finished = true;
            return true;
        }
        return false;
    }

    @Override
    protected void handleIncomingMessage() {
        if (checkIfClientIsDeletable()) {
            return;
        }

        // Warten auf nächste Nachricht
        ChatPDU receivedPdu;

        // Nach einer Minute wird geprüft, ob Client noch eingeloggt ist
        final int RECEIVE_TIMEOUT = 1200000;

        try {
            receivedPdu = (ChatPDU) connection.receive(RECEIVE_TIMEOUT);

            // Nachricht empfangen.
            // Zeitmessung für Serverbearbeitungszeit starten
            startTime = System.nanoTime();
        } catch (ConnectionTimeoutException e) {
            // Wartezeit beim Empfang abgelaufen, prüfen, ob der Client
            // überhaupt noch etwas sendet
            LOG.debug("Timeout beim Empfangen, " + RECEIVE_TIMEOUT + " ms ohne Nachricht vom Client");

            if (clients.getClient(userName) != null) {
                if (clients.getClient(userName).getStatus() == ClientConversationStatus.UNREGISTERING) {
                    // Worker-Thread wartet auf eine Nachricht vom Client, aber es
                    // kommt nichts mehr an
                    LOG.error("Client ist im Zustand UNREGISTERING und bekommt aber keine Nachricht mehr");
                    // Zur Sicherheit eine Logout-Response-PDU an Client senden und
                    // dann Worker-Thread beenden
                    finished = true;
                }
            }
            return;

        } catch (EndOfFileException e) {
            LOG.debug("End of File beim Empfang, vermutlich Verbindungsabbau des Partners für " + userName);
            finished = true;
            return;

        } catch (java.net.SocketException e) {
            LOG.error("Verbindungsabbruch beim Empfang der nächsten Nachricht vom Client " + getName());
            finished = true;
            return;

        } catch (Exception e) {
            LOG.error("Empfang einer Nachricht fehlgeschlagen, WorkerThread für User: " + userName);
            ExceptionHandler.logException(e);
            finished = true;
            return;
        }

        // Empfangene Nachricht bearbeiten
        try {
            switch (receivedPdu.getPduType()) {
                // Login-Request vom Client empfangen
                case LOGIN_REQUEST -> {
                    loginRequestAction(receivedPdu);
                    if (auditLogServerEnabled) {
                        // AuditLog-Satz erzeugen und senden
                        try {
                            auditLogConnection.send(receivedPdu, AuditLogPDUType.LOGIN_REQUEST);
                        } catch (Exception e) {
                            ExceptionHandler.logException(e);
                        }
                    }
                }
                // Chat-Nachricht angekommen, an alle verteilen
                case CHAT_MESSAGE_REQUEST -> {
                    chatMessageRequestAction(receivedPdu);
                    if (auditLogServerEnabled) {
                        // AuditLog-Satz erzeugen und senden
                        try {
                            auditLogConnection.send(receivedPdu, AuditLogPDUType.CHAT_MESSAGE_REQUEST);
                        } catch (Exception e) {
                            ExceptionHandler.logException(e);
                        }
                    }
                }
                // Logout-Request vom Client empfangen
                case LOGOUT_REQUEST -> {
                    logoutRequestAction(receivedPdu);
                    if (auditLogServerEnabled) {
                        // AuditLog-Satz erzeugen und senden
                        try {
                            auditLogConnection.send(receivedPdu, AuditLogPDUType.LOGOUT_REQUEST);
                        } catch (Exception e) {
                            ExceptionHandler.logException(e);
                        }
                    }
                }
                default -> LOG.debug("Falsche PDU empfangen von Client: " + receivedPdu.getUserName() + ", PduType: "
                        + receivedPdu.getPduType());
            }
        } catch (Exception e) {
            LOG.error("Exception bei der Nachrichtenverarbeitung");
            ExceptionHandler.logExceptionAndTerminate(e);
        }
    }
}