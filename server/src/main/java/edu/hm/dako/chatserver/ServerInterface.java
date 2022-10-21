package edu.hm.dako.chatserver;

/**
 * Einheitliche Schnittstelle aller Server
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public interface ServerInterface {
    /**
     * starts the server
     */
    void start();

    /**
     * stops the server
     *
     * @throws Exception - Fehler beim Beenden aller Threads des Chat-Servers
     */
    void stop() throws Exception;
}