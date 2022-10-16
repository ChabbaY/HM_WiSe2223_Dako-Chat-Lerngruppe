package edu.hm.dako.chatbenchmarking;

import javafx.scene.control.ProgressBar;

import javax.swing.JProgressBar;

/**
 * Schnittstelle zum Benchmarking-Client
 * Interface zur Übergabe von Daten für die Ausgabe im Benchmarking-Gui-Client
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public interface BenchmarkingClientUserInterface {

    /**
     * Übergabe der Startdaten an die GUI
     *
     * @param data Startdaten
     */
    void showStartData(UserInterfaceStartData data);

    /**
     * Übergabe der Ergebnisdaten an die GUI
     *
     * @param data Testergebnisse
     */
    void showResultData(UserInterfaceResultData data);

    /**
     * Übergabe einer Nachricht an die GUI zur Ausgabe in der Message-Zeile
     *
     * @param message Nachrichtentext
     */
    void setMessageLine(String message);

    /**
     * Zurücksetzen des Laufzeitzählers auf 0
     */
    void resetCurrentRunTime();

    /**
     * Erhöhung des Laufzeitzählers
     *
     * @param sec Laufzeiterhöhung in Sekunden
     */
    void addCurrentRunTime(long sec);

    /**
     * Dem User-Interface mitteilen, dass der Testlauf abgeschlossen ist
     */
    void testFinished();

    /**
     * Übergibt den Progressbar an die GUI
     *
     * @return Referenz auf ProgressBar
     */
    JProgressBar getProgressBar();

    /**
     * Übergibt den Progressbar an die FX-GUI
     *
     * @return Referenz auf ProgressBar
     */
    ProgressBar getProgressBarFx();

    /**
     * Stellt Verarbeitungsfortschritt im Progressbar dar
     */
    void countUpProgressTask();
}