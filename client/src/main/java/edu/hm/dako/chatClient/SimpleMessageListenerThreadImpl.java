package edu.hm.dako.chatClient;

import edu.hm.dako.common.ChatPDU;
import edu.hm.dako.common.ClientConversationStatus;
import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Thread wartet auf ankommende Nachrichten vom Server und bearbeitet diese.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class SimpleMessageListenerThreadImpl extends AbstractMessageListenerThread {
    private static final Logger LOG = LogManager.getLogger(SimpleMessageListenerThreadImpl.class);

    public SimpleMessageListenerThreadImpl(ClientUserInterface userInterface, Connection con,
                                           SharedClientData sharedData) {
        super(userInterface, con, sharedData);
    }

    @Override
    protected void loginResponseAction(ChatPDU receivedPdu) {
        if (receivedPdu.getErrorCode() == ChatPDU.LOGIN_ERROR) {
            // Login hat nicht funktioniert
            LOG.error("Login-Response-PDU für Client " + receivedPdu.getUserName()
                    + " mit Login-Error empfangen");
            userInterface.setErrorMessage(
                    "Chat-Server", "Anmelden beim Server nicht erfolgreich, Benutzer "
                            + receivedPdu.getUserName() + " vermutlich schon angemeldet",
                    receivedPdu.getErrorCode());
            sharedClientData.status = ClientConversationStatus.UNREGISTERED;

            // Verbindung wird gleich geschlossen
            try {
                connection.close();
            } catch (Exception e) {
                ExceptionHandler.logException(e);
            }
        } else {
            // Login hat funktioniert
            sharedClientData.status = ClientConversationStatus.REGISTERED;

            userInterface.loginComplete();

            Thread.currentThread().setName("Listener" + "-" + sharedClientData.userName);
            LOG.debug("Login-Response-PDU für Client " + receivedPdu.getUserName() + " empfangen");
        }
    }

    @Override
    protected void loginEventAction(ChatPDU receivedPdu) {
        // EventZähler für Testzwecke erhöhen
        sharedClientData.eventCounter.getAndIncrement();
        int events = SharedClientData.loginEvents.incrementAndGet();

        LOG.debug(sharedClientData.userName + " erhält LoginEvent, LoginEventCounter: " + events);

        try {
            handleUserListEvent(receivedPdu);
        } catch (Exception e) {
            ExceptionHandler.logException(e);
        }
    }

    @Override
    protected void logoutResponseAction(ChatPDU receivedPdu) {
        LOG.debug(sharedClientData.userName + " empfängt Logout-Response-PDU für Client " + receivedPdu.getUserName());
        sharedClientData.status = ClientConversationStatus.UNREGISTERED;

        userInterface.setSessionStatisticsCounter(sharedClientData.eventCounter.longValue(),
                sharedClientData.confirmCounter.longValue(), 0, 0, 0);

        LOG.debug("Vom Client gesendete Chat-Nachrichten:  " + sharedClientData.messageCounter.get());

        finished = true;
        userInterface.logoutComplete();
    }

    @Override
    protected void logoutEventAction(ChatPDU receivedPdu) {
        // EventZähler für Testzwecke erhöhen
        sharedClientData.eventCounter.getAndIncrement();
        int events = SharedClientData.logoutEvents.incrementAndGet();

        LOG.debug("LogoutEventCounter: " + events);

        try {
            handleUserListEvent(receivedPdu);
        } catch (Exception e) {
            ExceptionHandler.logException(e);
        }
    }

    @Override
    protected void chatMessageResponseAction(ChatPDU receivedPdu) {
        LOG.debug("Sequenznummer der Chat-Response-PDU " + receivedPdu.getUserName() + ": "
                + receivedPdu.getSequenceNumber() + ", MessageCounter: "
                + sharedClientData.messageCounter.get());

        LOG.debug(Thread.currentThread().getName()
                + ", Benötigte Serverzeit gleich nach Empfang der Response-Nachricht: "
                + receivedPdu.getServerTime() + " ns = " + receivedPdu.getServerTime() / 1000000
                + " ms");

        if (receivedPdu.getSequenceNumber() == sharedClientData.messageCounter.get()) {
            // Zuletzt gemessene Serverzeit für das Benchmarking merken
            userInterface.setLastServerTime(receivedPdu.getServerTime());

            // Nächste Chat-Nachricht darf eingegeben werden
            userInterface.setLock(false);

            LOG.debug("Chat-Response-PDU für Client " + receivedPdu.getUserName() + " empfangen");
        } else {
            LOG.debug("Sequenznummer der Chat-Response-PDU " + receivedPdu.getUserName()
                    + " passt nicht: " + receivedPdu.getSequenceNumber() + "/"
                    + sharedClientData.messageCounter.get());
        }
    }

    @Override
    protected void chatMessageEventAction(ChatPDU receivedPdu) {
        LOG.debug("Chat-Message-Event-PDU von " + receivedPdu.getEventUserName() + " empfangen");

        // EventZähler für Testzwecke erhöhen
        sharedClientData.eventCounter.getAndIncrement();
        int events = SharedClientData.messageEvents.incrementAndGet();

        LOG.debug("MessageEventCounter: " + events);

        // Empfangene Chat-Nachricht an User Interface zur
        // Darstellung übergeben
        userInterface.setMessageLine(receivedPdu.getEventUserName(), receivedPdu.getMessage());
    }

    /**
     * Bearbeitung aller vom Server ankommenden Nachrichten
     */
    public void run() {
        ChatPDU receivedPdu = null;

        LOG.debug("SimpleMessageListenerThread gestartet");

        while (!finished) {
            try {
                // Nächste ankommende Nachricht empfangen
                LOG.debug("Auf die nächste Nachricht vom Server warten");
                receivedPdu = receive();
                LOG.debug("Nach receive Aufruf, ankommende PDU mit PduType = "
                        + receivedPdu.getPduType());
            } catch (Exception e) {
                finished = true;
            }

            if (receivedPdu != null) {
                switch (sharedClientData.status) {
                    case REGISTERING:
                        switch (receivedPdu.getPduType()) {
                            // Login-Bestätigung vom Server angekommen
                            case LOGIN_RESPONSE -> loginResponseAction(receivedPdu);
                            // Meldung vom Server, dass sich die Liste der
                            // angemeldeten User erweitert hat
                            case LOGIN_EVENT -> loginEventAction(receivedPdu);
                            case LOGOUT_EVENT -> logoutEventAction(receivedPdu);
                            // Chat-Nachricht vom Server gesendet
                            case CHAT_MESSAGE_EVENT -> chatMessageEventAction(receivedPdu);
                            default -> LOG.debug("Ankommende PDU im Zustand " + sharedClientData.status
                                    + " wird verworfen");
                        }
                        break;
                    case REGISTERED:
                        switch (receivedPdu.getPduType()) {
                            // Die eigene zuletzt gesendete Chat-Nachricht wird vom
                            // Server bestätigt
                            case CHAT_MESSAGE_RESPONSE -> chatMessageResponseAction(receivedPdu);
                            // Chat-Nachricht vom Server gesendet
                            case CHAT_MESSAGE_EVENT -> chatMessageEventAction(receivedPdu);
                            // Meldung vom Server, dass sich die Liste der
                            // angemeldeten User erweitert hat
                            case LOGIN_EVENT -> loginEventAction(receivedPdu);
                            case LOGOUT_EVENT -> logoutEventAction(receivedPdu);
                            default -> LOG.debug("Ankommende PDU im Zustand " + sharedClientData.status
                                    + " wird verworfen");
                        }
                        break;
                    case UNREGISTERING:
                        switch (receivedPdu.getPduType()) {
                            // Chat-Nachricht vom Server gesendet
                            case CHAT_MESSAGE_EVENT -> chatMessageEventAction(receivedPdu);
                            // Bestätigung des eigenen Logout
                            case LOGOUT_RESPONSE -> logoutResponseAction(receivedPdu);
                            // Meldung vom Server, dass sich die Liste der
                            // angemeldeten User verändert hat
                            case LOGIN_EVENT -> loginEventAction(receivedPdu);
                            case LOGOUT_EVENT -> logoutEventAction(receivedPdu);
                            default -> LOG.debug("Ankommende PDU im Zustand " + sharedClientData.status
                                    + " wird verworfen");
                        }
                        break;
                    case UNREGISTERED:
                        LOG.debug("Ankommende PDU im Zustand " + sharedClientData.status + " wird verworfen");
                        break;
                    default:
                        LOG.debug("Unzulässiger Zustand " + sharedClientData.status);
                }
            }
        }

        // Verbindung noch schliessen
        try {
            connection.close();
        } catch (Exception e) {
            ExceptionHandler.logException(e);
        }
        LOG.debug("Ordnungsgemäßes Ende des SimpleMessageListener-Threads für User " + sharedClientData.userName
                + ", Status: " + sharedClientData.status);
    } // run
}