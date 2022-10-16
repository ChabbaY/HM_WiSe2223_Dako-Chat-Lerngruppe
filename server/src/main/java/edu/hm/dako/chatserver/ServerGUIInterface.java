package edu.hm.dako.chatserver;

/**
 * Interface, das der ServerGUI bereitstehen muss
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public interface ServerGUIInterface {
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