package edu.hm.dako.auditlogserver;

import edu.hm.dako.auditlogserver.gui.ALServerGUIInterface;

/**
 * shared attributes for all implementations
 *
 * @author Gabriel Bartolome
 */
public abstract class AbstractALServer implements ALServerInterface {
    /**
     * Konstruktor
     */
    AbstractALServer() {
        super();
    }

    /**
     * shared between all worker threads: managed list of all logged in clients
     */
    protected SharedChatServerList clients;

    /**
     * referencing server GUI to register events
     */
    protected ALServerGUIInterface alServerGUIInterface;
}