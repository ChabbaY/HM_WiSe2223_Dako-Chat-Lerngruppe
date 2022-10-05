package edu.hm.dako.chatClient;

import edu.hm.dako.common.ClientConversationStatus;
import edu.hm.dako.connection.ConnectionFactory;
import edu.hm.dako.connection.DecoratingConnectionFactory;
import edu.hm.dako.common.ChatPDU;
import edu.hm.dako.common.PduType;
import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.connection.Connection;
import edu.hm.dako.connection.tcp.TcpConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gemeinsame Funktionalitaet fuer alle Client-Implementierungen.
 * @author Peter Mandl
 */
public abstract class AbstractChatClient implements ClientCommunication {

    private static final Logger LOG = LogManager.getLogger(AbstractChatClient.class);

    // Username (Login-Kennung) des Clients
    protected String userName;
    protected String threadName;
    protected int localPort;
    protected int serverPort;
    protected String remoteServerAddress;
    protected ClientUserInterface userInterface;

    // Connection Factory
    protected ConnectionFactory connectionFactory;

    // und Verbindung zum Server
    protected Connection connection;

    // Gemeinsame Daten des Clientthreads und dem Message-Listener-Threads
    protected SharedClientData sharedClientData;

    // Thread, der die ankommenden Nachrichten fuer den Client verarbeitet
    protected Thread messageListenerThread;

    /**
     * Konstruktion eines Chat-Clients
     *
     * @param userInterface       GUI-Interface
     * @param serverPort          Port des Servers
     * @param remoteServerAddress Adresse des Servers
     */
    public AbstractChatClient(ClientUserInterface userInterface, int serverPort,
                              String remoteServerAddress) {

        this.userInterface = userInterface;
        this.serverPort = serverPort;
        this.remoteServerAddress = remoteServerAddress;

        /*
         * Verbindung zum Server aufbauen
         */
        try {
            connectionFactory = getDecoratedFactory(new TcpConnectionFactory());
            connection = connectionFactory.connectToServer(remoteServerAddress, serverPort,
                    localPort, 20000, 20000);
        } catch (Exception e) {
            ExceptionHandler.logException(e);
        }

        LOG.debug("Verbindung zum Server steht");

        /*
         * Gemeinsame Datenstruktur aufbauen
         */
        sharedClientData = new SharedClientData();
        sharedClientData.messageCounter = new AtomicInteger(0);
        sharedClientData.logoutCounter = new AtomicInteger(0);
        sharedClientData.eventCounter = new AtomicInteger(0);
        sharedClientData.confirmCounter = new AtomicInteger(0);
        sharedClientData.messageCounter = new AtomicInteger(0);
    }

    /**
     * Ergaenzt ConnectionFactory um Logging-Funktionalitaet
     *
     * @param connectionFactory ConnectionFactory
     * @return Dekorierte ConnectionFactory
     */
    public static ConnectionFactory getDecoratedFactory(
            ConnectionFactory connectionFactory) {
        return new DecoratingConnectionFactory(connectionFactory);
    }

    @Override
    public void login(String name) throws IOException {
        userName = name;
        sharedClientData.userName = name;
        sharedClientData.status = ClientConversationStatus.REGISTERING;
        ChatPDU requestPdu = new ChatPDU();
        requestPdu.setPduType(PduType.LOGIN_REQUEST);
        requestPdu.setClientStatus(sharedClientData.status);
        Thread.currentThread().setName("Client-" + userName);
        requestPdu.setClientThreadName(Thread.currentThread().getName());
        requestPdu.setUserName(userName);
        try {
            connection.send(requestPdu);
            LOG.debug("Login-Request-PDU fuer Client {} an Server gesendet", userName);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    @Override
    public void logout(String name) throws IOException {

        sharedClientData.status = ClientConversationStatus.UNREGISTERING;
        ChatPDU requestPdu = new ChatPDU();
        requestPdu.setPduType(PduType.LOGOUT_REQUEST);
        requestPdu.setClientStatus(sharedClientData.status);
        requestPdu.setClientThreadName(Thread.currentThread().getName());
        requestPdu.setUserName(userName);
        try {
            connection.send(requestPdu);
            sharedClientData.logoutCounter.getAndIncrement();
            LOG.debug("Logout-Request von {} gesendet, LogoutCount = {} ", requestPdu.getUserName()
                    , sharedClientData.logoutCounter.get());

        } catch (Exception e) {
            LOG.debug("Senden der Logout-Nachricht nicht moeglich");
            throw new IOException();
        }
    }

    @Override
    public void tell(String name, String text) throws IOException {

        ChatPDU requestPdu = new ChatPDU();
        requestPdu.setPduType(PduType.CHAT_MESSAGE_REQUEST);
        requestPdu.setClientStatus(sharedClientData.status);
        requestPdu.setClientThreadName(Thread.currentThread().getName());
        requestPdu.setUserName(userName);
        requestPdu.setMessage(text);
        sharedClientData.messageCounter.getAndIncrement();
        requestPdu.setSequenceNumber(sharedClientData.messageCounter.get());
        try {
            connection.send(requestPdu);
            LOG.debug("Chat-Message-Request-PDU fuer Client {} an Server gesendet, Inhalt: {}", name, text);
            LOG.debug("MessageCounter: {}, SequenceNumber: {}", sharedClientData.messageCounter.get(),
                    requestPdu.getSequenceNumber());
        } catch (Exception e) {
            LOG.debug("Senden der Chat-Nachricht nicht moeglich");
            throw new IOException();
        }
    }

    @Override
    public void cancelConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            ExceptionHandler.logException(e);
        }
    }

    @Override
    public boolean isLoggedOut() {
        return (sharedClientData.status == ClientConversationStatus.UNREGISTERED);
    }
}