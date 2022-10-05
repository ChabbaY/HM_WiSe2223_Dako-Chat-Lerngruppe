package edu.hm.dako.chatClient;

import edu.hm.dako.common.ChatPDU;
import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Abstrakte Klasse mit Basisfunktionalitaet fuer clientseitige Message-Processing-Threads
 * @author Peter Mandl
 */
public abstract class AbstractMessageListenerThread extends Thread {

    private static final Logger LOG = LogManager.getLogger(AbstractMessageListenerThread.class);

    // Kennzeichen zum Beenden der Bearbeitung
    protected boolean finished = false;

    // Verbindung zum Server
    protected Connection connection;

    // Schnittstelle zum User-Interface
    protected ClientUserInterface userInterface;

    // Gemeinsame Daten zwischen Client-Thread und Message-Processing-Thread
    protected SharedClientData sharedClientData;

    public AbstractMessageListenerThread(ClientUserInterface userInterface, Connection con,
                                         SharedClientData sharedData) {

        this.userInterface = userInterface;
        this.connection = con;
        this.sharedClientData = sharedData;
    }

    /**
     * Event vom Server zur Veraenderung der UserListe (eingeloggte Clients) verarbeiten
     *
     * @param receivedPdu Empfangene PDU
     */
    protected void handleUserListEvent(ChatPDU receivedPdu) {

        LOG.debug(
                "Login- oder Logout-Event-PDU fuer " + receivedPdu.getUserName() + " empfangen");

        // Neue Userliste zur Darstellung an User Interface uebergeben
        LOG.debug("Empfangene Userliste: " + receivedPdu.getClients());
        userInterface.setUserList(receivedPdu.getClients());
    }

    /**
     * Chat-PDU empfangen
     *
     * @return Empfangene ChatPDU
     * @throws Exception Verbindungsfehler
     */
    protected ChatPDU receive() throws Exception {
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
     * Aktion zur Behandlung ankommender Login-Responsesd.
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
