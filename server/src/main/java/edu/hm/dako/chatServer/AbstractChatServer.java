package edu.hm.dako.chatServer;

/**
 * shared attributes for all implementations
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public abstract class AbstractChatServer implements ChatServerInterface {
    /**
     * Konstruktor
     */
    public AbstractChatServer() {
    }

    /**
     * shared between all worker threads: managed list of all logged in clients
     */
    protected SharedChatClientList clients;

    /**
     * counter for test
      */
    protected SharedServerCounter counter;

    /**
     * referencing server GUI to register events
      */
    protected ChatServerGUIInterface serverGuiInterface;
}