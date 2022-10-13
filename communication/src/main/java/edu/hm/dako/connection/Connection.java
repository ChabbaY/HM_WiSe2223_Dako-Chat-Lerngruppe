package edu.hm.dako.connection;

import java.io.Serializable;

/**
 * Wird vom Client und vom Server zur Kommunikation verwendet.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public interface Connection {

    /**
     * Blockiert maximal eine angegebene Zeit in ms bis eine serialisierte Nachricht als Java-Objekt eintrifft.
     *
     * @param timeout Maximale Wartezeit in ms
     * @return Die erhaltene Nachricht des Kommunikationspartners
     * @throws Exception                  Fehler in der Verbindung
     * @throws ConnectionTimeoutException Timeout beim Empfang
     */
    Serializable receive(int timeout) throws Exception, ConnectionTimeoutException;

    /**
     * Blockiert bis eine serialisierte Nachricht als Java-Objekt eintrifft.
     *
     * @return Die erhaltene Nachricht des Kommunikationspartners
     * @throws Exception - Fehler in der Verbindung
     */
    Serializable receive() throws Exception;

    /**
     * Sendet eine Nachricht an den Kommunikationspartner.
     *
     * @param message Die zu sendende Nachricht
     * @throws Exception Fehler in der Verbindung
     */
    void send(Serializable message) throws Exception;

    /**
     * Baut die Verbindung zum Kommunikationspartner ab.
     *
     * @throws Exception - Fehler in der Verbindung
     */
    void close() throws Exception;
}