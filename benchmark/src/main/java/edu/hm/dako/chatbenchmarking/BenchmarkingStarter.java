package edu.hm.dako.chatbenchmarking;

import edu.hm.dako.chatbenchmarking.gui.BenchmarkingClientFxGUI;
import edu.hm.dako.chatbenchmarking.gui.UserInterfaceInputParameters;
import edu.hm.dako.common.ChatServerImplementationType;
import edu.hm.dako.common.SystemConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * starts the benchmarking server
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class BenchmarkingStarter {
    /**
     * referencing the logger
     */
    private static final Logger LOG = LogManager.getLogger(BenchmarkingStarter.class);

    /**
     * thread that runs the benchmarking
     */
    BenchmarkingClientCoordinator benchmarkingClient;

    /**
     * Flag, das angibt, ob der Server gestartet werden kann (alle Plausibilitätsprüfungen erfüllt)
     */
    private boolean startable = true;

    /**
     * flag that is true when a GUI is used
     */
    private static boolean GUI = true;

    /**
     * starts the benchmarking server
     *
     * @param args available args, please only use non-default
     *             --nogui disables the gui
     *             --protocol=tcpsimple (default; tcpadvanced not implemented yet)
     *             --num-clients=1 (default)
     *             --num-messages=10 (default)
     *             --max-retries=1 (default)
     *             --measurement=var-threads | var-length (default)
     *             --message-length=10 (default)
     *             --response-timeout=2000 (default)
     *             --think-time=100 (default)
     *             --port=50001 (default)
     *             --host=localhost (default)
     */
    public static void main(String[] args) {//TODO parametrize
        // Log4j2-Logging aus Datei konfigurieren
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File file = new File("config/log4j/log4j2.benchmarkingClient.xml");
        context.setConfigLocation(file.toURI());

        BenchmarkingStarter starter = new BenchmarkingStarter(args);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        while(!GUI) {
            String input = "";
            try {
                System.out.println("stop zum Beenden");
                input = br.readLine();
            } catch (IOException e) {
                LOG.error("Fehler bei Eingabe: " + e.getMessage());
            }

            switch(input) {
                case "stop" -> {
                    starter.stopBenchmarking();
                    return;
                }
            }
        }


    }

    /**
     * Konstruktor
     *
     * @param args available args, please only use non-default
     *             --nogui disables the gui
     *             --protocol=tcpsimple (default; tcpadvanced not implemented yet)
     *             --num-clients=1 (default)
     *             --num-messages=10 (default)
     *             --max-retries=1 (default)
     *             --measurement=var-threads | var-length (default)
     *             --message-length=10 (default)
     *             --response-timeout=2000 (default)
     *             --think-time=100 (default)
     *             --port=50001 (default)
     *             --host=localhost (default)
     */
    public BenchmarkingStarter(String[] args) {
        UserInterfaceInputParameters iParams = new UserInterfaceInputParameters();

        for(String s: args) {
            String[] values = s.split("=");
            switch (values[0]) {
                case "--nogui" -> GUI = false;
                case "--protocol" -> {
                    if ("tcpadvanced".equals(values[1])) {
                        iParams.setChatServerImplementationType(ChatServerImplementationType.TCPAdvancedImplementation);
                    }
                }
                case "--num-clients" -> iParams.setNumberOfClients(Integer.parseInt(values[1]));//TODO validate
                case "--num-messages" -> iParams.setNumberOfMessages(Integer.parseInt(values[1]));//TODO validate
                case "--max-retries" -> iParams.setNumberOfRetries(Integer.parseInt(values[1]));//TODO validate
                case "--measurement" -> {
                    if ("var-length".equals(values[1])) {
                        iParams.setMeasurementType(UserInterfaceInputParameters.MeasurementType.VarMsgLength);
                    }
                }
                case "--message-length" -> iParams.setMessageLength(Integer.parseInt(values[1]));//TODO validate
                case "--response-timeout" -> iParams.setResponseTimeout(Integer.parseInt(values[1]));//TODO validate
                case "--think-time" -> iParams.setClientThinkTime(Integer.parseInt(values[1]));//TODO validate
                case "--port" -> iParams.setRemoteServerPort(Integer.parseInt(values[1]));//TODO validate
                case "--host" -> iParams.setRemoteServerAddress(values[1]);
            }
        }

        if (GUI) {
            BenchmarkingClientFxGUI.main(args);
        } else {
            startBenchmarking(iParams);
        }
    }

    private void startBenchmarking(UserInterfaceInputParameters iParams) {
        benchmarkingClient = new BenchmarkingClientCoordinator();
        benchmarkingClient.executeTest(iParams, null);
    }

    private void stopBenchmarking() {

    }

    //----VALIDATION
    //TODO add validation
}