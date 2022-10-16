package edu.hm.dako.chatbenchmarking;

import edu.hm.dako.chatclient.ClientImpl;
import edu.hm.dako.chatclient.AbstractChatClient;
import edu.hm.dako.chatclient.ClientUserInterface;
import edu.hm.dako.chatclient.SimpleMessageListenerThreadImpl;
import edu.hm.dako.common.ChatServerImplementationType;
import edu.hm.dako.common.ClientConversationStatus;
import edu.hm.dako.common.ExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Benchmarking-Client: Simuliert einen Chat-User
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class BenchmarkingClientImpl extends AbstractChatClient
        implements Runnable, ClientUserInterface {
    /**
     * referencing the logger
     */
    private static final Logger log = LogManager.getLogger(ClientImpl.class);

    /**
     * Kennzeichen, ob zuletzt erwartete Chat-Response-PDU des Clients angekommen ist
     */
    private final AtomicBoolean chatResponseReceived = new AtomicBoolean();

    /**
     * Serverzeit des letzten Chat-Message-Requests
     */
    private final AtomicLong lastServerTime = new AtomicLong(0);

    /**
     * Parameter für den Benchmarking-Lauf
     */
    protected final int clientNumber;

    /**
     * Parameter für den Benchmarking-Lauf
     */
    protected final int messageLength;

    /**
     * Parameter für den Benchmarking-Lauf
     */
    protected final int numberOfMessagesToSend;

    /**
     * Parameter für den Benchmarking-Lauf
     */
    protected final int responseTimeout;

    /**
     * Parameter für den Benchmarking-Lauf
     */
    protected final int nrOfRetries;

    /**
     * Parameter für den Benchmarking-Lauf
     */
    protected final int clientThinkTime;

    /**
     * Parameter für den Benchmarking-Lauf
     */
    protected final ChatServerImplementationType implementationType;

    /**
     * Schnittstelle zur BenchmarkingGui, um den Progressbar zu verändern
     */
    protected final BenchmarkingClientUserInterface benchmarkingGui;

    /**
     * Gemeinsame Daten aller Threads zur Erfassung statistischer Daten
     */
    protected final SharedClientStatistics sharedStatistics;

    /**
     * Statistik-Zählerstand für eine beendete Chat-Session
     */
    private long numberOfSentEvents;

    /**
     * Statistik-Zählerstand für eine beendete Chat-Session
     */
    private long numberOfReceivedConfirms;

    /**
     * Statistik-Zählerstand für eine beendete Chat-Session
     */
    private long numberOfLostConfirms;

    /**
     * Statistik-Zählerstand für eine beendete Chat-Session
     */
    private long numberOfRetries;

    /**
     * Statistik-Zählerstand für eine beendete Chat-Session
     */
    private long numberOfReceivedChatMessages;

    /**
     * Konstruktor für Benchmarking
     *
     * @param userInterface       Schnittstelle zur GUI
     * @param benchmarkingGui     Schnittstelle zur BenchmarkingGUI
     * @param implementationType  Typ der Implementierung
     * @param serverPort          Port des Servers
     * @param remoteServerAddress HostAdresse des Servers
     * @param numberOfClient      Anzahl der zu simulierenden Clients
     * @param messageLength       Länge der Chat-Nachrichten
     * @param numberOfMessages    Anzahl der Nachrichten pro Client
     * @param clientThinkTime     Maximale Denkzeit zwischen zwei Chat-Requests
     * @param numberOfRetries     Anzahl Wiederholungen bei Nachrichtenverlust
     * @param responseTimeout     Timeout bei Überwachung der Bestätigungen
     * @param sharedStatistics    Statistikdaten
     */
    public BenchmarkingClientImpl(ClientUserInterface userInterface,
                                  BenchmarkingClientUserInterface benchmarkingGui,
                                  ChatServerImplementationType implementationType, int serverPort,
                                  String remoteServerAddress, int numberOfClient, int messageLength,
                                  int numberOfMessages, int clientThinkTime, int numberOfRetries, int responseTimeout,
                                  SharedClientStatistics sharedStatistics) {

        super(userInterface, serverPort, remoteServerAddress);

        this.benchmarkingGui = benchmarkingGui;
        this.implementationType = implementationType;
        this.clientNumber = numberOfClient;
        this.messageLength = messageLength;
        this.numberOfMessagesToSend = numberOfMessages;
        this.clientThinkTime = clientThinkTime;
        this.nrOfRetries = numberOfRetries;
        this.responseTimeout = responseTimeout;
        this.sharedStatistics = sharedStatistics;
        startMessageListenerThread();
    }

    /**
     * Start des Message-Listener-Threads zur Bearbeitung von empfangenen Server-Nachrichten
     */
    private void startMessageListenerThread() {

        switch (implementationType) {

            case TCPSimpleImplementation:
            case TCPAdvancedImplementation:
                try {
                    messageListenerThread = new SimpleMessageListenerThreadImpl(this, connection,
                            sharedClientData);
                    messageListenerThread.start();
                } catch (Exception e) {
                    ExceptionHandler.logException(e);
                }
                break;

            default:
                break;
        }

        Thread.currentThread().setName("Client-Thread-" + (clientNumber + 1));
        threadName = Thread.currentThread().getName();
        messageListenerThread.setName("MessageListener-Thread-" + clientNumber);
        log.debug("Message-Processing-Thread gestartet: " + messageListenerThread.getName());
    }

    /**
     * Thread zur Simulation eines Chat-Users: User wird beim Server registriert, alle Requests werden gesendet,
     * Antworten werden gelesen und am Ende wird ein Logout ausgeführt. Der Vorgang wird abrupt abgebrochen, wenn dies
     * über die GUI gewünscht wird.
     */
    @Override
    public void run() {

        try {
            // Login ausführen und warten, bis Server bestätigt
            // eindeutigen Login-Namen generieren
            String userName = threadName.concat(UUID.randomUUID().toString());
            this.login(userName);

            while (sharedClientData.status != ClientConversationStatus.REGISTERED) {

                Thread.sleep(1);
                if (sharedClientData.status == ClientConversationStatus.UNREGISTERED) {
                    // Fehlermeldung vom Server beim Login-Vorgang
                    log.debug("User " + userName + " schon im Server angemeldet");
                    return;
                }
            }

            sharedStatistics.increaseNumberOfLoggedInClients();

            log.debug("User " + userName + " beim Server angemeldet");

            // Warten, bis alle Clients eingeloggt sind
            waitForLoggedInClients();

            // Alle Chat-Nachrichten senden
            int i = 0;
            while ((i < numberOfMessagesToSend) && (!userInterface.isTestAborted())) {

                sendMessageAndWaitForAck(i);
                try {
                    // Zufällige Zeit, aber maximal die angegebene Denkzeit
                    // warten
                    int randomThinkTime = (int) (Math.random() * clientThinkTime) + 1;
                    Thread.sleep(randomThinkTime);
                } catch (Exception e) {
                    ExceptionHandler.logException(e);
                }

                i++;
                log.debug("Gesendete Chat-Nachrichten von " + userName + ": " + i);
            }

            // Warten, bis alle Clients bereit zum Ausloggen sind (alle Clients
            // haben alle Chat-Nachrichten gesendet)
            waitForLoggingOutClients();

            // Logout ausführen und warten, bis Server bestätigt
            this.logout(threadName);
            while (sharedClientData.status != ClientConversationStatus.UNREGISTERED) {
                Thread.sleep(1);
            }

            sharedStatistics.increaseNumberOfLoggedOutClients();

            log.debug(
                    "Anzahl gesendeter Requests: " + sharedStatistics.getNumberOfSentRequests());
            log.debug("Anzahl empfangener Responses: "
                    + sharedStatistics.getSumOfAllReceivedMessages());
            log.debug(
                    "Anzahl vom Server empfangener Events: " + sharedClientData.eventCounter.get());
            log.debug("Anzahl an Server gesendeter Confirms: "
                    + sharedClientData.confirmCounter.get());
            log.debug("Durchschnittliche Serverbearbeitungszeit in ns: "
                    + sharedStatistics.getAverageServerTime() + ", = "
                    + sharedStatistics.getAverageServerTime() / 1000000 + " ms");

            // Nachbearbeitung für die Statistik
            postLogout();
            log.debug("User " + userName + " beim Server abgemeldet");

            // Transportverbindung zum Server abbauen
            connection.close();

        } catch (Exception e) {
            ExceptionHandler.logException(e);
        }
    }

    /**
     * Warten, bis Server eine Chat-Response als Antwort auf den letzten Chat-Request gesendet hat
     * (nur für Benchmarking)
     */
    private void waitUntilChatResponseReceived() {
        setLock(true);
        try {
            while (getLock()) {
                log.debug(userName + " wartet auf Chat-Message-Response-PDU");
                Thread.sleep(1);
                // Durch den Sleep wird die RTT beim Benchmark ein wenig verfälscht
            }
        } catch (Exception e) {
            ExceptionHandler.logException(e);
        }
    }

    /**
     * Chat-Nachricht an den Server senden und auf Antwort warten. Methode wird nur von Benchmarking-Client genutzt
     *
     * @param i Nummer des Clients
     */
    private void sendMessageAndWaitForAck(int i) {
        // Dummy-Nachricht zusammenbauen
        final StringBuilder chatMessage = new StringBuilder("");
        chatMessage.append("+".repeat(Math.max(0, messageLength)));

        // Senden der Nachricht und warten, bis Bestätigung vom Server da ist
        try {

            sharedStatistics.increaseSentMsgCounter(clientNumber);

            // RTT-Startzeit ermitteln
            long rttStartTime = System.nanoTime();
            tell(userName, chatMessage.toString());

            // Warten, bis Chat-Response empfangen wurde, dann erst nächsten
            // Chat Request senden
            waitUntilChatResponseReceived();

            // Response in Statistik aufnehmen
            long rtt = System.nanoTime() - rttStartTime;
            postReceive(i, getLastServerTime(), rtt);

        } catch (Exception e) {
            ExceptionHandler.logException(e);
        }
    }

    /**
     * Synchronisation mit allen anderen Client-Threads: Warten, bis alle Clients angemeldet sind und dann erst mit der
     * Lasterzeugung beginnen
     *
     * @throws InterruptedException falls sleep unterbrochen wurde
     */
    private void waitForLoggedInClients() throws InterruptedException {
        sharedStatistics.getLoginSignal().countDown();
        sharedStatistics.getLoginSignal().await();
    }

    /**
     * Synchronisation mit allen anderen Client-Threads: Warten, bis alle Clients angemeldet sind und dann erst mit der
     * Lasterzeugung beginnen
     *
     * @throws InterruptedException falls sleep unterbrochen wurde
     */
    private void waitForLoggingOutClients() throws InterruptedException {
        sharedStatistics.getLogoutSignal().countDown();
        sharedStatistics.getLogoutSignal().await();
        log.debug("Client " + threadName + " kann beendet werden");
    }

    /**
     * Nacharbeit nach Empfang einer PDU vom Server
     *
     * @param messageNumber Fortlaufende Nachrichtennummer
     * @param serverTime    Zeit, die der Server für die Bearbeitung des Chat-Message-Requests benötigt
     * @param rtt           Round Trip Time für den Request
     */
    private void postReceive(int messageNumber, long serverTime, long rtt) {
        // Response-Zähler und Serverbearbeitungszeit erhöhen
        sharedStatistics.increaseReceivedMsgCounter(clientNumber, rtt, serverTime);

        // Progressbar weiterschreiben
        benchmarkingGui.countUpProgressTask();

        if (rtt <= serverTime) {
            // Test, ob Messung plausibel ist, rtt muss größer als serverTime sein
            log.error(threadName + ": RTT für Request " + (messageNumber + 1) + ": " + rtt
                    + " ns = " + (rtt / 1000000) + " ms,  benötigte Serverzeit: " + serverTime
                    + " ns = " + (serverTime / 1000000) + " ms");
        }
    }

    /**
     * Nacharbeit nach Logout
     */
    private void postLogout() {
        // Zähler für Statistik eintragen
        sharedStatistics.setNumberOfSentEventMessages(clientNumber, getNumberOfSentEvents());
        sharedStatistics.setNumberOfReceivedConfirmEvents(clientNumber,
                getNumberOfReceivedConfirms());
        sharedStatistics.setNumberOfLostConfirmEvents(clientNumber,
                getNumberOfLostConfirms());
        sharedStatistics.setNumberOfRetriedEvents(clientNumber, getNumberOfRetries());

        log.debug(
                "Vom Server verarbeitete Chat-Nachrichten: " + getNumberOfReceivedChatMessages());
        log.debug("Vom Server gesendete Event-Nachrichten: " + getNumberOfSentEvents());
        log.debug("Dem Server bestätigte Event-Nachrichten (Confirms): "
                + getNumberOfReceivedConfirms());
        log.debug("Im Server nicht empfangene Bestätigungen: " + getNumberOfLostConfirms());
        log.debug("Vom Server initiierte Wiederholungen: " + getNumberOfRetries());
    }

    @Override
    // Wird nicht genutzt, nur für ClientGui relevant
    public void setUserList(Vector<String> names) {
    }

    @Override
    // Wird nicht genutzt, nur für ClientGui relevant
    public void setMessageLine(String sender, String message) {
    }

    @Override
    // Wird nicht genutzt, nur für ClientGui relevant
    public void setErrorMessage(String sender, String errorMessage, long errorCode) {
    }

    @Override
    // Wird nicht genutzt, nur für BenchmarkingClientCoordinator relevant
    public void loginComplete() {
    }

    @Override
    // Wird nicht genutzt, nur für BenchmarkingClientCoordinator relevant
    public void logoutComplete() {
    }

    @Override
    public synchronized boolean getLock() {
        if (chatResponseReceived.get()) {
            log.debug(Thread.currentThread().getName() + " wartet auf notify");
            try {
                wait();
                return false;
            } catch (Exception e) {
                return false;
            }
        } else {
            log.error(
                    Thread.currentThread().getName() + " muss nicht auf notify warten, Lock frei");
            return false;
        }
    }

    @Override
    public synchronized void setLock(boolean lock) {
        chatResponseReceived.getAndSet(lock);
        if (!chatResponseReceived.get()) {
            log.debug(Thread.currentThread().getName() + " sendet notify");
            // Antwort auf letzten Request erhalten, nächster Request kann gesendet
            // werden
            notifyAll();
        }
    }

    @Override
    public void abortTest() {
        userInterface.abortTest();
    }

    @Override
    public boolean isRunning() {
        return userInterface.isRunning();
    }

    @Override
    public void releaseTest() {
        userInterface.releaseTest();
    }

    @Override
    public boolean isTestAborted() {
        return userInterface.isTestAborted();
    }

    @Override
    public synchronized long getLastServerTime() {
        return lastServerTime.get();
    }

    @Override
    public void setLastServerTime(long lastServerTime) {
        this.lastServerTime.getAndSet(lastServerTime);
    }

    @Override
    public void setSessionStatisticsCounter(long numberOfSentEvents, long numberOfReceivedConfirms,
                                            long numberOfLostConfirms, long numberOfRetries,
                                            long numberOfReceivedChatMessages) {
        this.numberOfSentEvents = numberOfSentEvents;
        this.numberOfReceivedConfirms = numberOfReceivedConfirms;
        this.numberOfLostConfirms = numberOfLostConfirms;
        this.numberOfRetries = numberOfRetries;
        this.numberOfReceivedChatMessages = numberOfReceivedChatMessages;
    }

    @Override
    public long getNumberOfSentEvents() {
        return this.numberOfSentEvents;
    }

    @Override
    public long getNumberOfReceivedConfirms() {
        return this.numberOfReceivedConfirms;
    }

    @Override
    public long getNumberOfLostConfirms() {
        return this.numberOfLostConfirms;
    }

    @Override
    public long getNumberOfRetries() {
        return this.numberOfRetries;
    }

    @Override
    public long getNumberOfReceivedChatMessages() {
        return this.numberOfReceivedChatMessages;
    }
}