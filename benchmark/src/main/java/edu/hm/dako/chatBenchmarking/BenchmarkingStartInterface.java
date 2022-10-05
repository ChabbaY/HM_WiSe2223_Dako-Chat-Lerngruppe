package edu.hm.dako.chatBenchmarking;


/**
 * Schnittstelle zum Starten eines Benchmarks
 * @author Peter Mandl
 */

public interface BenchmarkingStartInterface {

    /**
     * Methode fuehrt den Benchmark aus
     * @param parm Input-Parameter
     * @param clientGui Schnittstelle zur GUI
     */
    void executeTest(UserInterfaceInputParameters parm,
                     BenchmarkingClientUserInterface clientGui);
}
