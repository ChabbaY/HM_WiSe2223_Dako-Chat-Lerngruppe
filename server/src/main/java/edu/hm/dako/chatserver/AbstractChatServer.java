package edu.hm.dako.chatserver;

/**
 * shared attributes for all implementations
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public abstract class AbstractChatServer implements ServerInterface {
    /**
     * Konstruktor
     */
    AbstractChatServer() {
        super();
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
    protected ServerGUIInterface serverGuiInterface;
}