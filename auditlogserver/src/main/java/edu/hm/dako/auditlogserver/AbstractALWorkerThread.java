package edu.hm.dako.auditlogserver;

import edu.hm.dako.auditlogserver.gui.ALServerGUIInterface;
import edu.hm.dako.chatserver.SharedChatClientList;
import edu.hm.dako.chatserver.SharedServerCounter;
import edu.hm.dako.chatserver.gui.ServerGUIInterface;
import edu.hm.dako.common.ChatPDU;
import edu.hm.dako.connection.Connection;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstrakte Klasse mit Basisfunktionalität für serverseitige Worker-Threads
 *
 * @author Gabriel Bartolome
 */
public abstract class AbstractALWorkerThread extends Thread {
    /**
     * Verbindung-Handle
     */
    protected final Connection connection;

    /**
     * Kennzeichen zum Beenden des Worker-Threads
     */
    protected boolean finished = false;


    /**
     * Client-ThreadName
     */
    protected String clientThreadName = null;

    /**
     * Startzeit für die Serverbearbeitungszeit
     */
    protected long startTime;


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
     * Referenz auf GUI des Auditlog-Servers
     * wofür brauchen wir die
     */
    protected final ALServerGUIInterface alServerGUIInterface;

    /**
     * Konstruktor
     *
     * @param con                Verbindung zum Client
     * @param counter            Referenz auf diverse Zähler für Tests
     * @param alServerGuiInterface Referenz auf GUI des Chat-ALServers
     */
    public AbstractALWorkerThread(Connection con, SharedChatClientList clients, SharedServerCounter counter,
                                  ALServerGUIInterface alServerGuiInterface) {
        this.connection = con;
        this.logoutCounter = counter.logoutCounter;
        this.eventCounter = counter.eventCounter;
        this.confirmCounter = counter.confirmCounter;
        this.alServerGUIInterface = alServerGuiInterface;
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