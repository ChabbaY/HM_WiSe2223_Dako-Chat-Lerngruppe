package edu.hm.dako.chatServer;

/**
 * Gemeinsame Attribute für alle Implementierungen
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public abstract class AbstractChatServer implements ChatServerInterface {
    // Gemeinsam für alle WorkerThreads verwaltete Liste aller eingeloggten
    // Clients
    protected SharedChatClientList clients;

    // Zähler für Test
    protected SharedServerCounter counter;

    // Referenz auf Server GUI für die Meldung von Ereignissen
    protected ChatServerGuiInterface serverGuiInterface;
}