package edu.hm.dako.chatclient;

import java.io.IOException;

/**
 * Interface zur Kommunikation des Chat-Clients mit dem Chat-Server
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public interface ClientCommunication {
    /**
     * Login-Request an den Server senden
     *
     * @param name - Username (Login-Kennung)
     * @throws IOException - Fehler in der Verbindung
     */
    void login(String name) throws IOException;

    /**
     * Logout-Request an den Server senden
     *
     * @param name Username (Login-Kennung)
     * @throws IOException - Fehler in der Verbindung
     */
    void logout(String name) throws IOException;

    /**
     * Senden einer Chat-Nachricht zur Verteilung an den Server
     *
     * @param name - Username (Login-Kennung)
     * @param text - Chat-Nachricht
     * @throws IOException - Fehler in der Verbindung
     */
    void tell(String name, String text) throws IOException;

    /**
     * Abbruch der Verbindung zum Server
     */
    void cancelConnection();

    /**
     * Prüfen, ob Logout schon komplett vollzogen
     *
     * @return boolean - true = Logout abgeschlossen
     */
    boolean isLoggedOut();
}