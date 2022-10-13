package edu.hm.dako.chatBenchmarking;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

/**
 * Ermitteln der durchschnittlich verbrauchten CPU-Zeit eines Prozesses
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class CpuUtilisationWatch {

    private static final OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory
            .getOperatingSystemMXBean();

    private static final int nCPUs = osBean.getAvailableProcessors();

    private final Long startWallClockTime;
    private final Long startCpuTime = osBean.getProcessCpuTime();

    public CpuUtilisationWatch() {
        startWallClockTime = System.nanoTime();
    }

    public float getAverageCpuUtilisation() {
        float wallClockTimeDelta = System.nanoTime() - startWallClockTime;
        float cpuTimeDelta = osBean.getProcessCpuTime() - startCpuTime;
        cpuTimeDelta = Math.max(cpuTimeDelta, 1);

        return (cpuTimeDelta / (float) nCPUs) / wallClockTimeDelta;
    }
}