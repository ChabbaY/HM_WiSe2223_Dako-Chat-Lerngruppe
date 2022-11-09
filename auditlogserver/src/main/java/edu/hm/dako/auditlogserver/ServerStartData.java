package edu.hm.dako.auditlogserver;

/**
 * stores some data about the server start
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class ServerStartData {
    String startTime;

    /**
     * Konstruktor
     */
    public ServerStartData() {
    }

    /**
     * retrieves the start time of the server
     *
     * @return startTime of the server as String
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * set the server startTime
     *
     * @param startTime server start time in String format
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}