package edu.hm.dako.chatbenchmarking.gui;

/**
 * Startparameter für das Benchmarking User Interface
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class UserInterfaceStartData {
    /**
     * Anzahl geplanter Requests
     */
    long numberOfRequests;

    /**
     * Zeit des Testbeginns
     */
    String startTime;

    /**
     * Anzahl der geplanten Event-Nachrichten
     */
    long numberOfPlannedEventMessages;

    /**
     * Konstruktor
     */
    public UserInterfaceStartData() {
    }

    /**
     * getter
     *
     * @return numberOfRequests
     */
    public long getNumberOfRequests() {
        return numberOfRequests;
    }

    /**
     * setter
     *
     * @param numberOfRequests numberOfRequests
     */
    public void setNumberOfRequests(long numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
    }

    /**
     * getter
     *
     * @return startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * setter
     *
     * @param startTime startTime
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * getter
     *
     * @return numberOfPlannedEventMessages
     */
    public long getNumberOfPlannedEventMessages() {
        return numberOfPlannedEventMessages;
    }

    /**
     * setter
     *
     * @param numberOfPlannedEventMessages numberOfPlannedEventMessages
     */
    public void setNumberOfPlannedEventMessages(long numberOfPlannedEventMessages) {
        this.numberOfPlannedEventMessages = numberOfPlannedEventMessages;
    }
}