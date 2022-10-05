package edu.hm.dako.chatClient;

import java.util.Vector;

/**
 * Interface zur Ausfuehrung von Aktionen ueber die Praesentationslogik
 * @author Mandl
 */
public interface ClientUserInterface {

    /**
     * Uebergabe der Startdaten an die GUI
     * @param userList Liste der aktuell angemeldeten User
     */
    void setUserList(Vector<String> userList);

    /**
     * Uebergabe einer Nachricht zur Ausgabe in der Messagezeile
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
     * @param lock true, wenn Client warten muss, sonst false
     */
    void setLock(boolean lock);

    /**
     * Abfragen, ob Benutzer den Chat stoppen will
     * @return Kennzeichen, ob Benutzer stoppen will, true = stoppen
     */
    boolean isTestAborted();

    /**
     * Stoppen des laufenden Chats
     */
    void abortTest();

    /**
     * (Rueck)setzen des Stop-Flags
     */
    void releaseTest();

    /**
     * Puefen, ob Test gerade laeuft
     * @return true = Test laeuft, falese = Test laeuft nicht
     */
    boolean isRunning();

    /**
     * Auslesen der zuletzt benoetigten Zeit fuer die Bearbeitung eines Chat-Message-Requests im Server
     * @return - benoetigte Zeit
     */
    long getLastServerTime();

    /**
     * Serverbearbeitungszeit des letzten Chat-Message-Requests merken
     * @param lastServerTime Zuletzt gemessene Serverbearbeitungszeit
     */
    void setLastServerTime(long lastServerTime);

    /**
     * Zaehler einer Chat-Session setzen (Zaehlung erfolgt im Server)
     * @param numberOfSentEvents Waehrend der Session gesendete Events
     * @param numberOfReceivedConfirms Waehrend der Session gesendete/empfangene Confirms
     * @param numberOfLostConfirms Waehrend der Session verlorene Confirms
     * @param numberOfRetries Waehrend der Session gesendete Wiederholungen
     * @param numberOfReceivedChatMessages Waehrend der Session empfangene Chat-Nachrichten
     */
    void setSessionStatisticsCounter(long numberOfSentEvents,
                                     long numberOfReceivedConfirms, long numberOfLostConfirms, long numberOfRetries,
                                     long numberOfReceivedChatMessages);

    long getNumberOfSentEvents();

    long getNumberOfReceivedConfirms();

    long getNumberOfLostConfirms();

    long getNumberOfRetries();

    long getNumberOfReceivedChatMessages();

    /**
     * Uebergabe einer Fehlermeldung
     * @param sender Absender der Fehlermeldung
     * @param errorMessage Fehlernachricht
     * @param errorCode Error Code
     */

    void setErrorMessage(String sender, String errorMessage, long errorCode);

    /**
     * Login vollstaendig und Chat-GUI kann angezeigt werden
     */
    void loginComplete();

    /**
     * Logout vollstaendig durchgefuehrt
     */
    void logoutComplete();
}