package edu.hm.dako.chatServer;

/**
 * Einheitliche Schnittstelle aller Server
 * @author Peter Mandl
 */
public interface ChatServerInterface {

    /**
     * Startet den Server
     */
    void start();

    /**
     * Stoppt den Server
     *
     * @throws Exception - Fehler beim Beenden aller Threads des Chat-Servers
     */
    void stop() throws Exception;
}
