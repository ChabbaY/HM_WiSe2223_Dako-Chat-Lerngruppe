package edu.hm.dako.chatclient;

import edu.hm.dako.common.ClientConversationStatus;
import edu.hm.dako.common.PDUType;
import edu.hm.dako.connection.ConnectionFactory;
import edu.hm.dako.connection.ConnectionFactoryLogger;
import edu.hm.dako.common.ChatPDU;
import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.connection.Connection;
import edu.hm.dako.connection.tcp.TCPConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gemeinsame Funktionalität für alle Client-Implementierungen.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public abstract class AbstractChatClient implements ClientCommunication {
    /**
     * referencing the logger
     */
    private static final Logger LOG = LogManager.getLogger(AbstractChatClient.class);

    /**
     * Username (Login-Kennung) des Clients
     */
    protected String userName;

    /**
     * Kennung des Threads
     */
    protected String threadName;

    /**
     * lokaler Port
     */
    protected int localPort;

    /**
     * Server Port
     */
    protected int serverPort;

    /**
     * Server Adresse
     */
    protected String remoteServerAddress;

    /**
     * Referenz auf die Client GUI
     */
    protected final ClientUserInterface userInterface;

    /**
     * Connection Factory
     */
    protected ConnectionFactory connectionFactory;

    /**
     * und Verbindung zum Server
     */
    protected Connection connection;

    /**
     * Gemeinsame Daten des Client-Threads und des Message-Listener-Threads
     */
    protected final SharedClientData sharedClientData;

    /**
     * Thread, der die ankommenden Nachrichten für den Client verarbeitet
     */
    protected Thread messageListenerThread;

    /**
     * Konstruktion eines Chat-Clients
     *
     * @param userInterface       GUI-Interface
     * @param serverPort          Port des Servers
     * @param remoteServerAddress Adresse des Servers
     */
    public AbstractChatClient(ClientUserInterface userInterface, int serverPort, String remoteServerAddress) {
        this.userInterface = userInterface;
        this.serverPort = serverPort;
        this.remoteServerAddress = remoteServerAddress;

        // Verbindung zum Server aufbauen
        try {
            connectionFactory = getDecoratedFactory(new TCPConnectionFactory());
            connection = connectionFactory.connectToServer(remoteServerAddress, serverPort, localPort, 20000,
                    20000);
        } catch (Exception e) {
            ExceptionHandler.logException(e);
        }

        LOG.debug("Verbindung zum Server steht");

        //Gemeinsame Datenstruktur aufbauen
        sharedClientData = new SharedClientData();
        sharedClientData.logoutCounter = new AtomicInteger(0);
        sharedClientData.eventCounter = new AtomicInteger(0);
        sharedClientData.confirmCounter = new AtomicInteger(0);
        sharedClientData.messageCounter = new AtomicInteger(0);
    }

    /**
     * Ergänzt ConnectionFactory um Logging-Funktionalität
     *
     * @param connectionFactory ConnectionFactory
     * @return Dekorierte ConnectionFactory
     */
    public static ConnectionFactory getDecoratedFactory(
            ConnectionFactory connectionFactory) {
        return new ConnectionFactoryLogger(connectionFactory);
    }

    @Override
    public void login(String name) throws IOException {
        userName = name;
        sharedClientData.userName = name;
        sharedClientData.status = ClientConversationStatus.REGISTERING;
        ChatPDU requestPdu = new ChatPDU();
        requestPdu.setPduType(PDUType.LOGIN_REQUEST);
        requestPdu.setClientStatus(sharedClientData.status);
        Thread.currentThread().setName("Client-" + userName);
        requestPdu.setClientThreadName(Thread.currentThread().getName());
        requestPdu.setUserName(userName);
        try {
            connection.send(requestPdu);
            LOG.debug("Login-Request-PDU für Client {} an Server gesendet", userName);
        } catch (Exception e) {
            throw new IOException();
        }
    }

    @Override
    public void logout(String name) throws IOException {
        sharedClientData.status = ClientConversationStatus.UNREGISTERING;
        ChatPDU requestPdu = new ChatPDU();
        requestPdu.setPduType(PDUType.LOGOUT_REQUEST);
        requestPdu.setClientStatus(sharedClientData.status);
        requestPdu.setClientThreadName(Thread.currentThread().getName());
        requestPdu.setUserName(userName);
        try {
            connection.send(requestPdu);
            sharedClientData.logoutCounter.getAndIncrement();
            LOG.debug("Logout-Request von {} gesendet, LogoutCount = {} ", requestPdu.getUserName()
                    , sharedClientData.logoutCounter.get());

        } catch (Exception e) {
            LOG.debug("Senden der Logout-Nachricht nicht möglich");
            throw new IOException();
        }
    }

    @Override
    public void tell(String name, String text) throws IOException {
        ChatPDU requestPdu = new ChatPDU();
        requestPdu.setPduType(PDUType.CHAT_MESSAGE_REQUEST);
        requestPdu.setClientStatus(sharedClientData.status);
        requestPdu.setClientThreadName(Thread.currentThread().getName());
        requestPdu.setUserName(userName);
        requestPdu.setMessage(text);
        sharedClientData.messageCounter.getAndIncrement();
        requestPdu.setSequenceNumber(sharedClientData.messageCounter.get());
        try {
            connection.send(requestPdu);
            LOG.debug("Chat-Message-Request-PDU für Client {} an Server gesendet, Inhalt: {}", name, text);
            LOG.debug("MessageCounter: {}, SequenceNumber: {}", sharedClientData.messageCounter.get(),
                    requestPdu.getSequenceNumber());
        } catch (Exception e) {
            LOG.debug("Senden der Chat-Nachricht nicht möglich");
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