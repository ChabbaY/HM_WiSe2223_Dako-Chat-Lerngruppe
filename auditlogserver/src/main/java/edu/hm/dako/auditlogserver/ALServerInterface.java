package edu.hm.dako.auditlogserver;

/**
 * Einheitliche Schnittstelle aller Server
 *
 * @author Gabriel Bartolome
 */
public interface ALServerInterface {
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