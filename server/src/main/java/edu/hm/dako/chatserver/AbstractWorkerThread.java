package edu.hm.dako.chatserver;

import edu.hm.dako.chatserver.gui.ServerGUIInterface;
import edu.hm.dako.common.ChatPDU;
import edu.hm.dako.connection.Connection;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstrakte Klasse mit Basisfunktionalität für serverseitige Worker-Threads
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public abstract class AbstractWorkerThread extends Thread {
    /**
     * Verbindung-Handle
     */
    protected final Connection connection;

    /**
     * Kennzeichen zum Beenden des Worker-Threads
     */
    protected boolean finished = false;

    /**
     * Username des durch den Worker-Thread bedienten Clients
     */
    protected String userName = null;

    /**
     * Client-ThreadName
     */
    protected String clientThreadName = null;

    /**
     * Startzeit für die Serverbearbeitungszeit
     */
    protected long startTime;

    /**
     * Gemeinsam für alle WorkerThreads verwaltete Liste aller eingeloggten Clients
     */
    protected final SharedChatClientList clients;

    /**
     * Referenz auf globalen Zähler für Testausgaben
     */
    protected final AtomicInteger logoutCounter;

    /**
     * Referenz auf globalen Zähler für Testausgaben
     */
    protected final AtomicInteger eventCounter;

    /**
     * Referenz auf globalen Zähler für Testausgaben
     */
    protected final AtomicInteger confirmCounter;

    /**
     * Referenz auf GUI des Chat-Servers
     */
    protected final ServerGUIInterface serverGuiInterface;

    /**
     * Konstruktor
     *
     * @param con                Verbindung zum Chat-Client
     * @param clients            Liste der angemeldeten Chat-Clients
     * @param counter            Referenz auf diverse Zähler für Tests
     * @param serverGuiInterface Referenz auf GUI des Chat-Servers
     */
    public AbstractWorkerThread(Connection con, SharedChatClientList clients, SharedServerCounter counter,
                                ServerGUIInterface serverGuiInterface) {
        this.connection = con;
        this.clients = clients;
        this.logoutCounter = counter.logoutCounter;
        this.eventCounter = counter.eventCounter;
        this.confirmCounter = counter.confirmCounter;
        this.serverGuiInterface = serverGuiInterface;
    }

    /**
     * Aktion für die Behandlung ankommender Login-Requests: Neuen Client anlegen und alle Clients informieren
     *
     * @param receivedPdu Empfangene PDU
     */
    protected abstract void loginRequestAction(ChatPDU receivedPdu);

    /**
     * Aktion für die Behandlung ankommender Logout-Requests: Alle Clients informieren, Response senden und Client
     * löschen
     *
     * @param receivedPdu Empfangene PDU
     */
    protected abstract void logoutRequestAction(ChatPDU receivedPdu);

    /**
     * Aktion für die Behandlung ankommender ChatMessage-Requests: Chat-Nachricht an alle Clients weitermelden
     *
     * @param receivedPdu Empfangene PDU
     */
    protected abstract void chatMessageRequestAction(ChatPDU receivedPdu);

    /**
     * Aktion für die Behandlung ankommender ChatMessageConfirm-PDUs
     * Verarbeitung einer ankommenden Nachricht eines Clients (Implementierung des serverseitigen
     * Chat-Zustandsautomaten)
     */
    protected abstract void handleIncomingMessage();
}