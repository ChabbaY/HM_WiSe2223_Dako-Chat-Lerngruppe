package edu.hm.dako.chatBenchmarking;

import javafx.scene.control.ProgressBar;

import javax.swing.JProgressBar;

/**
 * Schnittstelle zum Benchmarking-Client
 * Interface zur Uebergabe von Daten fuer die Ausgabe im Benchmarking-Gui-Client
 * @author Peter Mandl
 */
public interface BenchmarkingClientUserInterface {

    /**
     * Uebergabe der Startdaten an die GUI
     *
     * @param data Startdaten
     */
    void showStartData(UserInterfaceStartData data);

    /**
     * Uebergabe der Ergebnisdaten an die GUI
     * @param data Testergebnisse
     */
    void showResultData(UserInterfaceResultData data);

    /**
     * Uebergabe einer Nachricht an die GUI zur Ausgabe in der Messagezeile
     * @param message Nachrichtentext
     */
    void setMessageLine(String message);

    /**
     * Zuruecksetzen des Laufzeitzaehlers auf 0
     */
    void resetCurrentRunTime();

    /**
     * Erhoehung des Laufzeitzaehlers
     *
     * @param sec Laufzeiterhoehung in Sekunden
     */
    void addCurrentRunTime(long sec);

    /**
     * Dem User-Interface mitteilen, dass der Testlauf abgeschlossen ist
     */
    void testFinished();

    /**
     * Uebergibt den Progressbar an die GUI
     *
     * @return Referenz auf ProgressBar
     */
    JProgressBar getProgressBar();

    /**
     * Uebergibt den Progressbar an die FX-GUI
     * @return Referenz auf ProgressBar
     */
    ProgressBar getProgressBarFx();

    /**
     * Stellt Verarbeitungsfortschritt im Progressbar dar
     */
    void countUpProgressTask();
}