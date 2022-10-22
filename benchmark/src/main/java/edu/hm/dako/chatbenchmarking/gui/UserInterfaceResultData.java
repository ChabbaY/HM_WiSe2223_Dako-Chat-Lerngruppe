package edu.hm.dako.chatbenchmarking.gui;

/**
 * data for viewing result in GUI
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class UserInterfaceResultData {
    /**
     * Arithmetisches Mittel
     */
    double mean;

    /**
     * Standardabweichung
     */
    double standardDeviation;

    /**
     * Anzahl gesendeter Requests
     */
    private long numberOfSentRequests;

    /**
     * Anzahl empfangener Responses
     */
    private long numberOfResponses;

    /**
     * Anzahl verlorener (nicht empfangener) Responses
     */
    private long numberOfLostResponses;

    /**
     * Anzahl an Wiederholungen beim Senden
     */
    private long numberOfRetries;

    /**
     * Testende als Datum/Uhrzeit-String
     */
    private String endTime;

    /**
     * Testdauer in Sekunden
     */
    private long elapsedTime;

    /**
     * Anzahl gesendeter Event-Nachrichten im Server
     */
    private long numberOfSentEventMessages;

    /**
     * Anzahl empfangener Event-Bestätigungen im Server
     */
    private long numberOfReceivedConfirmEvents;

    /**
     * Anzahl verlorener Bestätigungen im Server
     */
    private long numberOfLostConfirmEvents;

    /**
     * Anzahl von erneuten Sendungen von Event-Nachrichten im Server
     */
    private long numberOfRetriedEvents;

    /**
     * Mittlere Serverbearbeitungszeit in ms
     */
    private double avgServerTime;

    /**
     * Maximale Heap-Belegung während des Testlaufs in MiB
     */
    private long maxHeapSize;

    /**
     * Maximale CPU-Auslastung während des Testlaufs in Prozent
     */
    private float maxCpuUsage;

    /**
     * Minimum (Minimale RTT über alle Requests)
     */
    private double minimum;

    /**
     * Maximum (Maximale RTT über alle Requests)
     */
    private double maximum;

    /**
     * 10 % Percentile, alle RTT-Werte, die zu den kleinsten 10 % gehören
     */
    private double percentile10;

    /**
     * 10 % Percentile = 25%-Quartil, alle RTT-Werte, die zu den kleinsten 25 % gehören
     */
    private double percentile25;

    /**
     * Median = 50%-Quartil
     */
    private double percentile50;

    /**
     * 75 % percentile
     */
    private double percentile75;

    /**
     * 90 % Percentile
     */
    private double percentile90;

    /**
     * Spannweite (zwischen Minimum und Maximum)
     */
    private double range;

    /**
     * Interquartilsabstand zwischen 25%- und 75%-Percentile
     */
    private double interQuartilRange;

    /**
     * Konstruktor
     */
    public UserInterfaceResultData() {
    }

    /**
     * getter
     *
     * @return numberOfSentRequests
     */
    public long getNumberOfSentRequests() {
        return numberOfSentRequests;
    }

    /**
     * setter
     *
     * @param numberOfSentRequests numberOfSentRequests
     */
    public void setNumberOfSentRequests(long numberOfSentRequests) {
        this.numberOfSentRequests = numberOfSentRequests;
    }

    /**
     * getter
     *
     * @return numberOfResponses
     */
    public long getNumberOfResponses() {
        return numberOfResponses;
    }

    /**
     * setter
     *
     * @param numberOfResponses numberOfResponses
     */
    public void setNumberOfResponses(long numberOfResponses) {
        this.numberOfResponses = numberOfResponses;
    }

    /**
     * getter
     *
     * @return numberOfLostResponses
     */
    public long getNumberOfLostResponses() {
        return numberOfLostResponses;
    }

    /**
     * setter
     *
     * @param numberOfLostResponses numberOfLostResponses
     */
    public void setNumberOfLostResponses(long numberOfLostResponses) {
        this.numberOfLostResponses = numberOfLostResponses;
    }

    /**
     * getter
     *
     * @return numberOfRetries
     */
    public long getNumberOfRetries() {
        return numberOfRetries;
    }

    /**
     * setter
     *
     * @param numberOfRetries numberOfRetries
     */
    public void setNumberOfRetries(long numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
    }

    /**
     * getter
     *
     * @return elapsedTime
     */
    public long getElapsedTime() {
        return elapsedTime;
    }

    /**
     * setter
     *
     * @param elapsedTime elapsedTime
     */
    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    /**
     * getter
     *
     * @return endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * setter
     *
     * @param endTime endTime
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * getter
     *
     * @return numberOfSentEventMessages
     */
    public long getNumberOfSentEventMessages() {
        return numberOfSentEventMessages;
    }

    /**
     * setter
     *
     * @param numberOfSentEventMessages numberOfSentEventMessages
     */
    public void setNumberOfSentEventMessages(long numberOfSentEventMessages) {
        this.numberOfSentEventMessages = numberOfSentEventMessages;
    }

    /**
     * getter
     *
     * @return numberOfReceivedConfirmEvents
     */
    public long getNumberOfReceivedConfirmEvents() {
        return numberOfReceivedConfirmEvents;
    }

    /**
     * setter
     *
     * @param numberOfReceivedConfirmEvents numberOfReceivedConfirmEvents
     */
    public void setNumberOfReceivedConfirmEvents(long numberOfReceivedConfirmEvents) {
        this.numberOfReceivedConfirmEvents = numberOfReceivedConfirmEvents;
    }

    /**
     * getter
     *
     * @return numberOfLostConfirmEvents
     */
    public long getNumberOfLostConfirmEvents() {
        return numberOfLostConfirmEvents;
    }

    /**
     * setter
     *
     * @param numberOfLostConfirmEvents numberOfLostConfirmEvents
     */
    public void setNumberOfLostConfirmEvents(long numberOfLostConfirmEvents) {
        this.numberOfLostConfirmEvents = numberOfLostConfirmEvents;
    }

    /**
     * getter
     *
     * @return numberOfRetriedEvents
     */
    public long getNumberOfRetriedEvents() {
        return numberOfRetriedEvents;
    }

    /**
     * setter
     *
     * @param numberOfRetriedEvents numberOfRetriedEvents
     */
    public void setNumberOfRetriedEvents(long numberOfRetriedEvents) {
        this.numberOfRetriedEvents = numberOfRetriedEvents;
    }

    /**
     * getter
     *
     * @return avgServerTime
     */
    public double getAvgServerTime() {
        return avgServerTime;
    }

    /**
     * setter
     *
     * @param avgServerTime avgServerTime
     */
    public void setAvgServerTime(double avgServerTime) {
        this.avgServerTime = avgServerTime;
    }

    /**
     * getter
     *
     * @return maxHeapSize
     */
    public long getMaxHeapSize() {
        return maxHeapSize;
    }

    /**
     * setter
     *
     * @param maxHeapSize maxHeapSize
     */
    public void setMaxHeapSize(long maxHeapSize) {
        this.maxHeapSize = maxHeapSize;
    }

    /**
     * getter
     *
     * @return maxCpuUsage
     */
    public double getMaxCpuUsage() {
        return maxCpuUsage;
    }

    /**
     * setter
     *
     * @param maxCpuUsage maxCpuUsage
     */
    public void setMaxCpuUsage(float maxCpuUsage) {
        this.maxCpuUsage = maxCpuUsage;
    }

    /**
     * getter
     *
     * @return minimum
     */
    public double getMinimum() {
        return minimum;
    }

    /**
     * setter
     *
     * @param minimum minimum
     */
    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    /**
     * getter
     *
     * @return maximum
     */
    public double getMaximum() {
        return maximum;
    }

    /**
     * setter
     *
     * @param maximum maximum
     */
    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    /**
     * getter
     *
     * @return percentile10
     */
    public double getPercentile10() {
        return percentile10;
    }

    /**
     * setter
     *
     * @param percentile10 percentile10
     */
    public void setPercentile10(double percentile10) {
        this.percentile10 = percentile10;
    }

    /**
     * getter
     *
     * @return percentile25
     */
    public double getPercentile25() {
        return percentile25;
    }

    /**
     * setter
     *
     * @param percentile25 percentile25
     */
    public void setPercentile25(double percentile25) {
        this.percentile25 = percentile25;
    }

    /**
     * getter
     *
     * @return percentile50
     */
    public double getPercentile50() {
        return percentile50;
    }

    /**
     * setter
     *
     * @param percentile50 percentile50
     */
    public void setPercentile50(double percentile50) {
        this.percentile50 = percentile50;
    }

    /**
     * getter
     *
     * @return percentile75
     */
    public double getPercentile75() {
        return percentile75;
    }

    /**
     * setter
     *
     * @param percentile75 percentile75
     */
    public void setPercentile75(double percentile75) {
        this.percentile75 = percentile75;
    }

    /**
     * getter
     *
     * @return percentile90
     */
    public double getPercentile90() {
        return percentile90;
    }

    /**
     * setter
     *
     * @param percentile90 percentile90
     */
    public void setPercentile90(double percentile90) {
        this.percentile90 = percentile90;
    }

    /**
     * getter
     *
     * @return interQuartilRange
     */
    public double getInterQuartilRange() {
        return interQuartilRange;
    }

    /**
     * setter
     *
     * @param interQuartilRange interQuartilRange
     */
    public void setInterQuartilRange(double interQuartilRange) {
        this.interQuartilRange = interQuartilRange;
    }

    /**
     * getter
     *
     * @return range
     */
    public double getRange() {
        return range;
    }

    /**
     * setter
     *
     * @param range range
     */
    public void setRange(double range) {
        this.range = range;
    }

    /**
     * getter
     *
     * @return mean
     */
    public double getMean() {
        return mean;
    }

    /**
     * setter
     *
     * @param mean mean
     */
    public void setMean(double mean) {
        this.mean = mean;
    }

    /**
     * getter
     *
     * @return standardDeviation
     */
    public double getStandardDeviation() {
        return standardDeviation;
    }

    /**
     * setter
     *
     * @param standardDeviation standardDeviation
     */
    public void setStandardDeviation(double standardDeviation) {
        this.standardDeviation = standardDeviation;
    }
}