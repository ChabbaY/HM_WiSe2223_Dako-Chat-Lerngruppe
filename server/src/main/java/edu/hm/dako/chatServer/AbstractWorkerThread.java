package edu.hm.dako.chatServer;


import edu.hm.dako.common.ChatPDU;
import edu.hm.dako.connection.Connection;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstrakte Klasse mit Basisfunktionalitaet fuer serverseitige Worker-Threads
 * @author Peter Mandl
 */
public abstract class AbstractWorkerThread extends Thread {

    // Verbindungs-Handle
    protected Connection connection;

    // Kennzeichen zum Beenden des Worker-Threads
    protected boolean finished = false;

    // Username des durch den Worker-Thread bedienten Clients
    protected String userName = null;

    // Client-Threadname
    protected String clientThreadName = null;

    // Startzeit fuer die Serverbearbeitungszeit
    protected long startTime;

    // Gemeinsam fuer alle Workerthreads verwaltete Liste aller eingeloggten Clients
    protected SharedChatClientList clients;

    // Referenzen auf globale Zaehler fuer Testausgaben
    protected AtomicInteger logoutCounter;
    protected AtomicInteger eventCounter;
    protected AtomicInteger confirmCounter;

    protected ChatServerGuiInterface serverGuiInterface;

    /**
     * Konstruktor
     * @param con Verbindung zum Chat-Client
     * @param clients Liste der angemeldeten Chat-Clients
     * @param counter Referenz auf diverse Zaehler fuer Tests
     * @param serverGuiInterface Referenz auf GUI des Chat-Servers
     */
    public AbstractWorkerThread(Connection con, SharedChatClientList clients,
                                SharedServerCounter counter, ChatServerGuiInterface serverGuiInterface) {
        this.connection = con;
        this.clients = clients;
        this.logoutCounter = counter.logoutCounter;
        this.eventCounter = counter.eventCounter;
        this.confirmCounter = counter.confirmCounter;
        this.serverGuiInterface = serverGuiInterface;
    }

    /**
     * Aktion fuer die Behandlung ankommender Login-Requests: Neuen Client anlegen und alle Clients informieren
     * @param receivedPdu Empfangene PDU
     */
    protected abstract void loginRequestAction(ChatPDU receivedPdu);

    /**
     * Aktion fuer die Behandlung ankommender Logout-Requests: Alle Clients informieren, Response senden und Client
     * loeschen
     * @param receivedPdu Empfangene PDU
     */
    protected abstract void logoutRequestAction(ChatPDU receivedPdu);

    /**
     * Aktion fuer die Behandlung ankommender ChatMessage-Requests: Chat-Nachricht an alle Clients weitermelden
     * @param receivedPdu Empfangene PDU
     */
    protected abstract void chatMessageRequestAction(ChatPDU receivedPdu);

    /**
     * Aktion fuer die Behandlung ankommender ChatMessageConfirm-PDUs
     * Verarbeitung einer ankommenden Nachricht eines Clients (Implementierung des serverseitigen
     * Chat-Zustandsautomaten)
     * @throws Exception Schwerwiegender Fehler, z. B. in der Verbindung zum Client
     */
    protected abstract void handleIncomingMessage() throws Exception;
}
