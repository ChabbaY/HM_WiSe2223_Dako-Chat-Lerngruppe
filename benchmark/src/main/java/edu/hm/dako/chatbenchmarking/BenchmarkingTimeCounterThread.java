package edu.hm.dako.chatbenchmarking;

import edu.hm.dako.chatbenchmarking.gui.BenchmarkingClientUserInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.concurrent.TimeUnit;

/**
 * Thread berechnet die Laufzeit eines Benchmarks und sendet diese zyklisch an die GIU
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class BenchmarkingTimeCounterThread extends Thread {
    private static final int SLEEP_TIME_IN_SECONDS = 1;
    private static final Logger log = LogManager.getLogger(BenchmarkingTimeCounterThread.class);
    private final BenchmarkingClientUserInterface out;

    private boolean running = true;

    /**
     * Konstruktor
     *
     * @param clientGui reference to benchmarking GUI
     */
    public BenchmarkingTimeCounterThread(BenchmarkingClientUserInterface clientGui) {
        setName("TimeCounterThread");
        this.out = clientGui;
    }

    /**
     * Run-Methode für den Thread: Erzeugt alle n Sekunden einen Zähler und sendet ihn an die Ausgabe
     */
    @Override
    public void run() {
        log.debug(getName() + " gestartet");

        if (out != null) out.resetCurrentRunTime();

        while (running) {
            try {
                TimeUnit.SECONDS.sleep(SLEEP_TIME_IN_SECONDS);
            } catch (InterruptedException e) {
                log.debug("Sleep unterbrochen");
            }

            if (out != null) out.addCurrentRunTime(SLEEP_TIME_IN_SECONDS);
        }
    }

    /**
     * Beenden des Threads
     */
    public void stopThread() {
        running = false;
        log.debug(getName() + " gestoppt");
    }
}