package edu.hm.dako.chatBenchmarking;

import edu.hm.dako.chatClient.ClientUserInterface;
import edu.hm.dako.common.ExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Basisklasse zum Starten eines Benchmarks
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class BenchmarkingClientCoordinator extends Thread
        implements BenchmarkingStartInterface, ClientUserInterface {
    private static final Logger log = LogManager.getLogger(BenchmarkingClientCoordinator.class);
    // Übergebene Parameter vom User-Interface
    UserInterfaceInputParameters params;
    // GUI-Schnittstelle
    BenchmarkingClientUserInterface benchmarkingClientGui;
    // Anzahl aller Requests, die auszuführen sind
    long numberOfAllRequests;
    // Startzeit des Tests
    long startTime;
    // Startzeit als String
    String startTimeAsString;
    // Kalender zur Umrechnung der Startzeit
    Calendar cal;
    // Thread zur Zeitzählung für die Dauer des Tests
    BenchmarkingTimeCounterThread timeCounterThread;
    // Daten aller Client-Threads zur Verwaltung der Statistik
    private SharedClientStatistics sharedData;
    private CpuUtilisationWatch cpuUtilisationWatch;
    // Kennzeichen, ob gerade ein Test läuft (es darf nur einer zu einer Zeit
    // laufen)
    private boolean running = false;
    // Kennzeichen, ob Test in der GUI gestoppt wurde
    private boolean abortedFlag = false;

    /**
     * Methode liefert die aktuelle Zeit als String
     *
     * @param cal Kalender
     * @return Zeit als String
     */
    private String getCurrentTime(Calendar cal) {
        return new SimpleDateFormat("dd.MM.yy HH:mm:ss:SSS").format(cal.getTime());
    }

    @Override
    public void executeTest(UserInterfaceInputParameters param,
                            BenchmarkingClientUserInterface clientGui) {

        this.params = param;
        this.benchmarkingClientGui = clientGui;

        clientGui.setMessageLine(param.mapImplementationTypeToString(param.getChatServerImplementationType())
                + ": Benchmark gestartet");

        // Anzahl aller erwarteten Requests ermitteln
        numberOfAllRequests = (long) param.getNumberOfClients() * param.getNumberOfMessages();

        // Gemeinsamen Datenbereich für alle Threads anlegen
        sharedData = new SharedClientStatistics(param.getNumberOfClients(),
                param.getNumberOfMessages(), param.getClientThinkTime());

        // Berechnung aller Messages für Progress-Bar
        if (clientGui.getProgressBar() != null) {
            clientGui.getProgressBar()
                    .setMaximum(param.getNumberOfClients() * param.getNumberOfMessages()
                            + param.getNumberOfClients() + param.getNumberOfClients());
        }

        startTime = 0;
        cal = Calendar.getInstance();
        startTime = cal.getTimeInMillis();
        startTimeAsString = getCurrentTime(cal);

        timeCounterThread = new BenchmarkingTimeCounterThread(clientGui);
        timeCounterThread.start();

        cpuUtilisationWatch = new CpuUtilisationWatch();
        start();
    }

    /**
     * Thread zur Entkoppelung des User-Interface von der Testausführung, damit im User-Interface Eingaben möglich
     * sind, während der Benchmark läuft (z.B. Abbruch).
     */
    @Override
    public void run() {

        // Test aktiv
        running = true;

        // Client-Threads in Abhängigkeit des Implementierungstyps instanziieren
        // und starten
        ExecutorService executorService = Executors
                .newFixedThreadPool(params.getNumberOfClients());

        for (int i = 0; i < params.getNumberOfClients(); i++) {
            executorService.submit(
                    BenchmarkingClientFactory.getClient(this, params, i, sharedData, benchmarkingClientGui));

            // Warten, bis der Client seinen Login abgeschlossen hat. Damit erfolgt
            // eine Serialisierung der Logins, damit die Anzahl der Login-Events genau
            // berechnet werden kann.
            while (sharedData.getNumberOfLoggedInClients() != i + 1) {
                try {
                    Thread.sleep(10);
                } catch (Exception e) {
                    ExceptionHandler.logException(e);
                }
            }
            log.debug("Client " + (i + 1) + " ist eingeloggt");
        }

        // Startwerte anzeigen
        UserInterfaceStartData startData = new UserInterfaceStartData();
        startData.setNumberOfRequests(numberOfAllRequests);
        startData.setStartTime(getCurrentTime(cal));

        /*
         * Maximal mögliche Events = ChatMessage-Events + Anzahl an Login-Events
         * (wenn alle Clients sich seriell hintereinander einloggen) + die Anzahl an
         * Logout-Events, wenn alle Clients bis zum letzten Logout arbeiten.
         */

        long numberOfPlannedLoginEvents = 0;
        for (int i = 1; i <= params.getNumberOfClients(); i++) {
            numberOfPlannedLoginEvents += i;
        }
        log.debug("Anzahl geplanter LoginEvent-Nachrichten: " + numberOfPlannedLoginEvents);

        long numberOfPlannedMessagesEvents = numberOfAllRequests * params.getNumberOfClients();
        log.debug(
                "Anzahl geplanter MessageEvent-Nachrichten: " + numberOfPlannedMessagesEvents);

        long numberOfPlannedLogoutEvents = (long) params.getNumberOfClients()
                * params.getNumberOfClients();

        log.debug("Anzahl geplanter LogoutEvent-Nachrichten: " + numberOfPlannedLogoutEvents);

        startData.setNumberOfPlannedEventMessages(numberOfPlannedMessagesEvents
                + numberOfPlannedLoginEvents + numberOfPlannedLogoutEvents);

        benchmarkingClientGui.showStartData(startData);

        benchmarkingClientGui.setMessageLine(
                "Alle " + params.getNumberOfClients() + " Clients-Threads gestartet");

        // Auf das Ende aller Clients warten
        executorService.shutdown();

        try {
            executorService.awaitTermination(120000, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            log.error("Das Beenden des ExecutorService wurde unterbrochen");
            ExceptionHandler.logException(e);
        }

        // Laufzeitzähler-Thread beenden
        timeCounterThread.stopThread();

        // Analyse der Ergebnisse durchführen, Statistikdaten berechnen und
        // ausgeben
        // sharedData.printStatistic();

        // Testergebnisse ausgeben
        benchmarkingClientGui.setMessageLine("Alle Clients-Threads beendet");

        UserInterfaceResultData resultData = getResultData(startTime);

        benchmarkingClientGui.showResultData(resultData);
        benchmarkingClientGui
                .setMessageLine(params.mapImplementationTypeToString(params.getChatServerImplementationType())
                        + ": Benchmark beendet");

        benchmarkingClientGui.testFinished();

        log.debug(
                "Anzahl aller erneuten Sendungen wegen Nachrichtenverlust (Übertragungswiederholungen): "
                        + sharedData.getSumOfAllRetries());

        // Datensatz für Benchmark-Lauf auf Protokolldatei schreiben
        sharedData.writeStatisticSet("Benchmarking-ChatApp-Protokolldatei",
                params.mapImplementationTypeToString(params.getChatServerImplementationType()),
                params.mapMeasurementTypeToString(params.getMeasurementType()), startTimeAsString,
                resultData.getEndTime(), cpuUtilisationWatch.getAverageCpuUtilisation());

        // In der GUI erneute Testläufe zulassen
        running = false;
    }

    @Override
    // Wird nicht genutzt, nur für ChatClientGUI relevant
    public synchronized void setUserList(Vector<String> names) {
    }

    @Override
    // Wird nicht genutzt, nur für ChatClientGUI relevant
    public synchronized void setMessageLine(String sender, String message) {
    }

    @Override
    // Wird nicht genutzt, nur für ChatClientGUI
    public void setErrorMessage(String sender, String errorMessage, long errorCode) {
    }

    @Override
    // Wird nicht genutzt, nur für BenchmarkingClientImpl relevant
    public void loginComplete() {
    }

    @Override
    // Wird nicht genutzt, nur für BenchmarkingClientImpl relevant
    public void logoutComplete() {
    }

    @Override
    // Wird nicht genutzt, nur für BenchmarkingClientImpl relevant
    public synchronized boolean getLock() {
        return false;
    }

    @Override
    // Wird nicht genutzt, nur für BenchmarkingClientImpl relevant
    public synchronized void setLock(boolean lock) {
    }

    @Override
    public synchronized void abortTest() {
        abortedFlag = true;
    }

    @Override
    public synchronized boolean isRunning() {
        return (this.running);
    }

    @Override
    public synchronized void releaseTest() {
        this.abortedFlag = false;
    }

    @Override
    public synchronized boolean isTestAborted() {
        return abortedFlag;
    }

    @Override
    // Wird nicht genutzt, nur für BenchmarkingClientImpl relevant
    public synchronized long getLastServerTime() {
        return 0;
    }

    @Override
    // Wird nicht genutzt, nur für BenchmarkingClientImpl relevant
    public synchronized void setLastServerTime(long lastServerTime) {
    }

    @Override
    // Wird nicht genutzt, nur für BenchmarkingClientImpl relevant
    public synchronized void setSessionStatisticsCounter(long numberOfSentEvents,
                                                         long numberOfReceivedConfirms, long numberOfLostConfirms, long numberOfRetries,
                                                         long numberOfReceivedChatMessages) {
    }

    @Override
    // Wird nicht genutzt, nur für BenchmarkingClientImpl relevant
    public synchronized long getNumberOfSentEvents() {
        return 0;
    }

    @Override
    // Wird nicht genutzt, nur für BenchmarkingClientImpl relevant
    public synchronized long getNumberOfReceivedConfirms() {
        return 0;
    }

    @Override
    // Wird nicht genutzt, nur für BenchmarkingClientImpl relevant
    public synchronized long getNumberOfLostConfirms() {
        return 0;
    }

    @Override
    // Wird nicht genutzt, nur für BenchmarkingClientImpl relevant
    public synchronized long getNumberOfRetries() {
        return 0;
    }

    @Override
    // Wird nicht genutzt, nur für BenchmarkingClientImpl relevant
    public synchronized long getNumberOfReceivedChatMessages() {
        return 0;
    }

    /**
     * Ergebnisdaten des Tests aufbereiten
     *
     * @param startTime Startzeit des Tests
     * @return Ergebnisdaten
     */
    private UserInterfaceResultData getResultData(long startTime) {
        Calendar cal;
        UserInterfaceResultData resultData = new UserInterfaceResultData();
        DistributionMetrics distributionMetrics = sharedData.calculateMetrics();

        resultData.setPercentile10(distributionMetrics.getPercentile10());
        resultData.setMean(distributionMetrics.getMean());
        resultData.setPercentile25(distributionMetrics.getPercentile25());
        resultData.setPercentile50(distributionMetrics.getPercentile50());
        resultData.setPercentile75(distributionMetrics.getPercentile75());
        resultData.setPercentile90(distributionMetrics.getPercentile90());
        resultData.setStandardDeviation(distributionMetrics.getStandardDeviation());
        resultData.setRange(distributionMetrics.getRange());
        resultData.setInterQuartilRange(distributionMetrics.getInterQuartilRange());
        resultData.setMinimum(distributionMetrics.getMinimum());
        resultData.setMaximum(distributionMetrics.getMaximum());

        resultData.setAvgServerTime(sharedData.getAverageServerTime() / 1000000.0);

        cal = Calendar.getInstance();
        resultData.setEndTime(getCurrentTime(cal));

        long elapsedTimeInSeconds = (cal.getTimeInMillis() - startTime) / 1000;
        resultData.setElapsedTime(elapsedTimeInSeconds);

        resultData.setMaxCpuUsage(cpuUtilisationWatch.getAverageCpuUtilisation());

        resultData.setMaxHeapSize(sharedData.getMaxHeapSize() / (1024 * 1024));

        resultData.setNumberOfResponses(sharedData.getSumOfAllReceivedMessages());
        resultData.setNumberOfSentRequests(sharedData.getNumberOfSentRequests());
        resultData.setNumberOfLostResponses(sharedData.getNumberOfLostResponses());
        resultData.setNumberOfRetries(sharedData.getSumOfAllRetries());
        resultData.setNumberOfSentEventMessages(sharedData.getSumOfAllSentEventMessages());
        resultData.setNumberOfReceivedConfirmEvents(sharedData.getSumOfAllReceivedConfirmEvents());
        resultData.setNumberOfLostConfirmEvents(sharedData.getSumOfAllLostConfirmEvents());
        resultData.setNumberOfRetriedEvents(sharedData.getSumOfAllRetriedEvents());
        return resultData;
    }
}