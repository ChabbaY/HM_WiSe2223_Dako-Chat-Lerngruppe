package edu.hm.dako.chatBenchmarking;

/**
 * Startparameter für das Benchmarking User Interface
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class UserInterfaceStartData {
    // Anzahl geplanter Requests
    long numberOfRequests;

    // Zeit des Testbeginns
    String startTime;

    // Anzahl der geplanten Event-Nachrichten
    long numberOfPlannedEventMessages;

    public long getNumberOfRequests() {
        return numberOfRequests;
    }

    public void setNumberOfRequests(long numberOfRequests) {
        this.numberOfRequests = numberOfRequests;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public long getNumberOfPlannedEventMessages() {
        return numberOfPlannedEventMessages;
    }

    public void setNumberOfPlannedEventMessages(long numberOfPlannedEventMessages) {
        this.numberOfPlannedEventMessages = numberOfPlannedEventMessages;
    }
}