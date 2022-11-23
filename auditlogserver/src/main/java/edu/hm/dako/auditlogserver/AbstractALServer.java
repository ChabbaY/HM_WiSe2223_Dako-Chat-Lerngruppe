package edu.hm.dako.auditlogserver;

import edu.hm.dako.auditlogserver.gui.ALServerGUIInterface;
import edu.hm.dako.chatserver.ServerInterface;
import edu.hm.dako.chatserver.SharedChatClientList;
import edu.hm.dako.chatserver.SharedServerCounter;
import edu.hm.dako.chatserver.gui.ServerGUIInterface;

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
    protected SharedChatClientList clients;

    /**
     * counter for test
     */
    protected SharedServerCounter counter;

    /**
     * referencing server GUI to register events
     */
    protected ALServerGUIInterface alServerGUIInterface;
}