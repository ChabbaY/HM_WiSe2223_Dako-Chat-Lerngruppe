package edu.hm.dako.chatclient;

import java.util.Vector;

/**
 * Interface zur Ausführung von Aktionen über die Präsentationslogik
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public interface ClientUserInterface {
    /**
     * Übergabe der Startdaten an die GUI
     *
     * @param userList Liste der aktuell angemeldeten User
     */
    void setUserList(Vector<String> userList);

    /**
     * Übergabe einer Nachricht zur Ausgabe in der MessageZeile
     *
     * @param sender  Absender der Nachricht
     * @param message Nachrichtentext
     */
    void setMessageLine(String sender, String message);

    /**
     * Lesen der Sperre
     *
     * @return Lock
     */
    boolean getLock();

    /**
     * Sperren bzw. Entsperren der Eingabe von Chat-Nachrichten an der GUI
     *
     * @param lock true, wenn Client warten muss, sonst false
     */
    void setLock(boolean lock);

    /**
     * Abfragen, ob Benutzer den Chat stoppen will
     *
     * @return Kennzeichen, ob Benutzer stoppen will, true = stoppen
     */
    boolean isTestAborted();

    /**
     * Stoppen des laufenden Chats
     */
    void abortTest();

    /**
     * (Rück)setzen des Stop-Flags
     */
    void releaseTest();

    /**
     * Prüfen, ob Test gerade läuft
     *
     * @return true = Test läuft, false = Test läuft nicht
     */
    boolean isRunning();

    /**
     * Auslesen der zuletzt benötigten Zeit für die Bearbeitung eines Chat-Message-Requests im Server
     *
     * @return - benötigte Zeit
     */
    long getLastServerTime();

    /**
     * Serverbearbeitungszeit des letzten Chat-Message-Requests merken
     *
     * @param lastServerTime Zuletzt gemessene Serverbearbeitungszeit
     */
    void setLastServerTime(long lastServerTime);

    /**
     * Zähler einer Chat-Session setzen (Zählung erfolgt im Server)
     *
     * @param numberOfSentEvents           Während der Session gesendete Events
     * @param numberOfReceivedConfirms     Während der Session gesendete/empfangene Confirms
     * @param numberOfLostConfirms         Während der Session verlorene Confirms
     * @param numberOfRetries              Während der Session gesendete Wiederholungen
     * @param numberOfReceivedChatMessages Während der Session empfangene Chat-Nachrichten
     */
    void setSessionStatisticsCounter(long numberOfSentEvents,
                                     long numberOfReceivedConfirms, long numberOfLostConfirms, long numberOfRetries,
                                     long numberOfReceivedChatMessages);

    /**
     * getter
     *
     * @return number of sent events
     */
    long getNumberOfSentEvents();

    /**
     * getter
     *
     * @return number of received confirms
     */
    long getNumberOfReceivedConfirms();

    /**
     * getter
     *
     * @return number of lost confirms
     */
    long getNumberOfLostConfirms();

    /**
     * getter
     *
     * @return number of retries
     */
    long getNumberOfRetries();

    /**
     * getter
     *
     * @return number of received chat messages
     */
    long getNumberOfReceivedChatMessages();

    /**
     * Übergabe einer Fehlermeldung
     *
     * @param sender       Absender der Fehlermeldung
     * @param errorMessage Fehlernachricht
     * @param errorCode    Error Code
     */
    void setErrorMessage(String sender, String errorMessage, long errorCode);

    /**
     * Login vollständig und Chat-GUI kann angezeigt werden
     */
    void loginComplete();

    /**
     * Logout vollständig durchgeführt
     */
    void logoutComplete();
}