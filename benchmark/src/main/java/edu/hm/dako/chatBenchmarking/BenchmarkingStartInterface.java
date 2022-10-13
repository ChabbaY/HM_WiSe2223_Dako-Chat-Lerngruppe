package edu.hm.dako.chatBenchmarking;

/**
 * Schnittstelle zum Starten eines Benchmarks
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public interface BenchmarkingStartInterface {
    /**
     * Methode führt den Benchmark aus
     *
     * @param param     Input-Parameter
     * @param clientGui Schnittstelle zur GUI
     */
    void executeTest(UserInterfaceInputParameters param,
                     BenchmarkingClientUserInterface clientGui);
}