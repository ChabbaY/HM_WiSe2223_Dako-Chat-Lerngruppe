package edu.hm.dako.chatServer;

/**
 * Interface, das der ServerGUI bereitstellen muss
 * @author Paul Mandl
 */
public interface ChatServerGuiInterface {

    void showStartData(ServerStartData data);

    void incrNumberOfLoggedInClients();

    void decrNumberOfLoggedInClients();

    void incrNumberOfRequests();
}
