package edu.hm.dako.chatClient;

import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.common.SystemConstants;

/**
 * Verwaltet eine Verbindung zum Server.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class ClientImpl extends AbstractChatClient {
    /**
     * Konstruktor
     *
     * @param userInterface       Schnittstelle zum User-Interface
     * @param serverPort          PortNummer des Servers
     * @param remoteServerAddress IP-Adresse/Hostname des Servers
     * @param serverType          Typ des Servers
     */
    public ClientImpl(ClientUserInterface userInterface, int serverPort, String remoteServerAddress, String serverType) {
        super(userInterface, serverPort, remoteServerAddress);
        this.serverPort = serverPort;
        this.remoteServerAddress = remoteServerAddress;

        Thread.currentThread().setName("Client");
        threadName = Thread.currentThread().getName();

        try {
            if (serverType.equals(SystemConstants.IMPL_TCP_SIMPLE)) {
                // Simple TCP Server erzeugen, derzeit gibt es nur den einen
                messageListenerThread = new SimpleMessageListenerThreadImpl(userInterface, connection, sharedClientData);
            }
            messageListenerThread.start();
        } catch (Exception e) {
            ExceptionHandler.logException(e);
        }
    }
}