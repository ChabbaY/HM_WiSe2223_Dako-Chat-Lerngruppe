package edu.hm.dako.auditlogserver.gui;

import edu.hm.dako.auditlogserver.ServerStartData;

/**
 * Interface, das der AuditLog ServerGUI bereitstehen muss
 *
 * @author Gabriel Bartolome
 */
public interface ALServerGUIInterface {
    /**
     * shows server start information in the GUI, e.g. the start time
     *
     * @param data start data of the server
     */
    void showStartData(ServerStartData data);

    /**
     * increases the number of logged in clients
     */
    void increaseNumberOfLoggedInClients();

    /**
     * decreases the number of logged in clients
     */
    void decreaseNumberOfLoggedInClients();

    /**
     * increases the number of incoming requests
     */
    void increaseNumberOfRequests();
}