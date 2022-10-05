package edu.hm.dako.chatBenchmarking;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

/**
 * Ermitteln der durchschnittlich verbrauchten CPU-Zeit eines Prozesses
 */
public class CpuUtilisationWatch {

    private static final OperatingSystemMXBean osbean = (OperatingSystemMXBean) ManagementFactory
            .getOperatingSystemMXBean();


    private static final int nCPUs = osbean.getAvailableProcessors();

    private final Long startWallclockTime;
    private final Long startCpuTime = osbean.getProcessCpuTime();

    public CpuUtilisationWatch() {
        startWallclockTime = System.nanoTime();
    }

    public float getAverageCpuUtilisation() {
        float wallclockTimeDelta = System.nanoTime() - startWallclockTime;
        float cpuTimeDelta = osbean.getProcessCpuTime() - startCpuTime;
        cpuTimeDelta = Math.max(cpuTimeDelta, 1);

        return (cpuTimeDelta / (float) nCPUs) / wallclockTimeDelta;
    }
}
