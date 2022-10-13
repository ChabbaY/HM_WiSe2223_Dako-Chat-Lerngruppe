package edu.hm.dako.chatServer;

/**
 * Interface, das der ServerGUI bereitstellen muss
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public interface ChatServerGuiInterface {
    void showStartData(ServerStartData data);

    void increaseNumberOfLoggedInClients();

    void decreaseNumberOfLoggedInClients();

    void increaseNumberOfRequests();
}