package edu.hm.dako.chatclient;

import edu.hm.dako.common.ChatPDU;
import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstrakte Klasse mit Basisfunktionalität für clientseitige Message-Processing-Threads
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public abstract class AbstractMessageListenerThread extends Thread {
    /**
     * referencing the logger
     */
    private static final Logger LOG = LogManager.getLogger(AbstractMessageListenerThread.class);

    /**
     * Kennzeichen zum Beenden der Bearbeitung
     */
    protected boolean finished = false;

    /**
     * Verbindung zum Server
     */
    protected final Connection connection;

    /**
     * Schnittstelle zum User-Interface
     */
    protected final ClientUserInterface userInterface;

    /**
     * Gemeinsame Daten zwischen Client-Thread und Message-Processing-Thread
     */
    protected final SharedClientData sharedClientData;

    /**
     * Konstruktor
     *
     * @param userInterface Schnittstelle zum User-Interface
     * @param con Verbindung zum Server
     * @param sharedData Gemeinsame Daten zwischen Client-Thread und Message-Processing-Thread
     */
    public AbstractMessageListenerThread(ClientUserInterface userInterface, Connection con,
                                         SharedClientData sharedData) {
        this.userInterface = userInterface;
        this.connection = con;
        this.sharedClientData = sharedData;
    }

    /**
     * Event vom Server zur Veränderung der UserListe (eingeloggte Clients) verarbeiten
     *
     * @param receivedPdu Empfangene PDU
     */
    protected void handleUserListEvent(ChatPDU receivedPdu) {
        LOG.debug("Login- oder Logout-Event-PDU für " + receivedPdu.getUserName() + " empfangen");

        // Neue Userliste zur Darstellung an User Interface übergeben
        LOG.debug("Empfangene Userliste: " + receivedPdu.getClients());
        if (userInterface != null) userInterface.setUserList(receivedPdu.getClients());
    }

    /**
     * Chat-PDU empfangen
     *
     * @return Empfangene ChatPDU
     */
    protected ChatPDU receive() {
        try {
            return (ChatPDU) connection.receive();
        } catch (Exception e) {
            ExceptionHandler.logException(e);
        }
        return null;
    }

    /**
     * Aktion zur Behandlung ankommender ChatMessageEvents.
     *
     * @param receivedPdu Ankommende PDU
     */
    protected abstract void chatMessageResponseAction(ChatPDU receivedPdu);

    /**
     * Aktion zur Behandlung ankommender ChatMessageResponses.
     *
     * @param receivedPdu Ankommende PDU
     */
    protected abstract void chatMessageEventAction(ChatPDU receivedPdu);

    /**
     * Aktion zur Behandlung ankommender Login-Responses.
     *
     * @param receivedPdu Ankommende PDU
     */
    protected abstract void loginResponseAction(ChatPDU receivedPdu);

    /**
     * Aktion zur Behandlung ankommender Login-Events.
     *
     * @param receivedPdu Ankommende PDU
     */
    protected abstract void loginEventAction(ChatPDU receivedPdu);

    /**
     * Aktion zur Behandlung ankommender Logout-Events.
     *
     * @param receivedPdu Ankommende PDU
     */
    protected abstract void logoutEventAction(ChatPDU receivedPdu);

    /**
     * Aktion zur Behandlung ankommender Logout-Responses.
     *
     * @param receivedPdu Ankommende PDU
     */
    protected abstract void logoutResponseAction(ChatPDU receivedPdu);
}