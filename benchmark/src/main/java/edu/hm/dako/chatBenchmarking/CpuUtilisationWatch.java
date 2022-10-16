package edu.hm.dako.chatBenchmarking;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

/**
 * Ermitteln der durchschnittlich verbrauchten CPU-Zeit eines Prozesses
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class CpuUtilisationWatch {
    /**
     * platform specific os management interface
     */
    private static final OperatingSystemMXBean osBean =
            (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    /**
     * number of available processors
     */
    private static final int nCPUs = osBean.getAvailableProcessors();

    /**
     * start time
     */
    private final Long startWallClockTime;

    /**
     * start processor time
     */
    private final Long startCpuTime = osBean.getProcessCpuTime();

    /**
     * Konstruktor
     */
    public CpuUtilisationWatch() {
        startWallClockTime = System.nanoTime();
    }

    /**
     * getter
     *
     * @return average CPU utilisation
     */
    public float getAverageCpuUtilisation() {
        float wallClockTimeDelta = System.nanoTime() - startWallClockTime;
        float cpuTimeDelta = osBean.getProcessCpuTime() - startCpuTime;
        cpuTimeDelta = Math.max(cpuTimeDelta, 1);

        return (cpuTimeDelta / (float) nCPUs) / wallClockTimeDelta;
    }
}