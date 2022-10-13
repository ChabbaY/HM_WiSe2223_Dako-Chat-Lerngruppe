package edu.hm.dako.connection;

/**
 * Stellt eine einheitliche Schnittstelle für Implementierungen verschiedener
 * Protokolle (z.B. TCP oder UDP) dar.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public interface ServerSocketInterface {
    /**
     * Wartet blockierend auf Verbindungsanfragen und stellt dann eine Verbindung her.
     *
     * @return Verbindung zum Client
     * @throws Exception Fehler in der Verbindung
     */
    Connection accept() throws Exception;

    /**
     * Schliesst den Socket und wird beim Herunterfahren des Servers aufgerufen.
     *
     * @throws Exception Fehler in der Verbindung
     */
    void close() throws Exception;

    /**
     * Gibt zurück ob der Socket schon geschlossen ist oder nicht.
     *
     * @return True, falls Socket bereits geschlossen.
     */
    boolean isClosed();
}