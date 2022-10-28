package edu.hm.dako.chatbenchmarking;

import edu.hm.dako.chatbenchmarking.gui.BenchmarkingClientFxGUI;
import edu.hm.dako.chatbenchmarking.gui.UserInterfaceInputParameters;
import edu.hm.dako.common.ChatServerImplementationType;
import edu.hm.dako.common.Tupel;
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
 * @author Linus Englert
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
    public static void main(String[] args) {
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
                case "--num-clients" -> {
                    Tupel<Integer, Boolean> validation = isPositiveNumber(values[1]);
                    iParams.setNumberOfClients(validation.getX());
                    startable = validation.getY();
                }
                case "--num-messages" -> {
                    Tupel<Integer, Boolean> validation = isPositiveNumber(values[1]);
                    iParams.setNumberOfMessages(validation.getX());
                    startable = validation.getY();
                }
                case "--max-retries" -> {
                    Tupel<Integer, Boolean> validation = isPositiveNumber(values[1]);
                    iParams.setNumberOfRetries(validation.getX());
                    startable = validation.getY();
                }
                case "--measurement" -> {
                    if ("var-length".equals(values[1])) {
                        iParams.setMeasurementType(UserInterfaceInputParameters.MeasurementType.VarMsgLength);
                    }
                }
                case "--message-length" -> {
                    Tupel<Integer, Boolean> validation = isPositiveNumber(values[1]);
                    iParams.setMessageLength(validation.getX());
                    startable = validation.getY();
                }
                case "--response-timeout" -> {
                    Tupel<Integer, Boolean> validation = isPositiveNumber(values[1]);
                    iParams.setResponseTimeout(validation.getX());
                    startable = validation.getY();
                }
                case "--think-time" -> {
                    Tupel<Integer, Boolean> validation = isPositiveNumber(values[1]);
                    iParams.setClientThinkTime(validation.getX());
                    startable = validation.getY();
                }
                case "--port" -> {
                    Tupel<Integer, Boolean> validation = validateServerPort(values[1]);
                    iParams.setRemoteServerPort(validation.getX());
                    startable = validation.getY();
                }
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

    /**
     * validate server port
     *
     * @param port port to validate
     * @return port
     */
    public static Tupel<Integer, Boolean> validateServerPort(String port) {
        int iServerPort = 0;
        boolean startable = true;
        if (port.matches("[0-9]+")) {
            iServerPort = Integer.parseInt(port);
            if ((iServerPort < 1) || (iServerPort > 65535)) {
                startable = false;
            } else {
                LOG.debug("Serverport: " + iServerPort);
            }
        } else {
            startable = false;
        }
        return new Tupel<>(iServerPort, startable);
    }

    /**
     * validates a number to be a positive Integer
     *
     * @param input String that contains the number
     * @return Tupel (parsed number, startable)
     */
    public static Tupel<Integer, Boolean> isPositiveNumber(String input) {
        int output = 0;
        boolean startable = true;
        if (input.matches("[0-9]+")) {
            output = Integer.parseInt(input);
            if (output <= 0) {
                startable = false;
            }
        } else {
            startable = false;
        }
        return new Tupel<>(output, startable);
    }
}