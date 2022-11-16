package edu.hm.dako.auditlogserver.gui;

import edu.hm.dako.auditlogserver.ServerStartData;
import edu.hm.dako.chatserver.ServerInterface;
import edu.hm.dako.chatserver.ServerStarter;
import edu.hm.dako.common.AuditLogImplementationType;
import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.common.SystemConstants;
import edu.hm.dako.common.Tupel;
import edu.hm.dako.common.gui.FxGUI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GUI for the audit log server
 *
 * @author Linus Englert
 */
public class AuditLogFxGUI extends FxGUI implements ALServerGUIInterface {
    /**
     * Standard-Port des Servers
     */
    static final String DEFAULT_SERVER_PORT = "40001";

    /**
     * referencing the logger
     */
    private static final Logger LOG = LogManager.getLogger(AuditLogFxGUI.class);

    /**
     * Interface der Chat-Server-Implementierung
     */
    private static ServerInterface chatServer;

    /**
     * Zähler für die eingeloggten Clients und die empfangenen Request
     */
    private static AtomicInteger loggedInClientCounter, requestCounter;

    /**
     * Daten, die beim Start der GUI übergeben werden
     */
    private final ServerStartData data = new ServerStartData();

    /**
     * Mögliche Belegungen des Implementierungsfeldes in der GUI
     */
    final ObservableList<String> implTypeOptions = FXCollections.observableArrayList(
            SystemConstants.AUDIT_LOG_SERVER_TCP_IMPL,
            SystemConstants.AUDIT_LOG_SERVER_UDP_IMPL,
            SystemConstants.AUDIT_LOG_SERVER_RMI_IMPL);

    /**
     * Server-Startzeit als String
     */
    private String startTimeAsString;

    /**
     * Kalender zur Umrechnung der Startzeit
     */
    private Calendar cal;

    /**
     * Flag, das angibt, ob der Server gestartet werden kann (alle Plausibilitätsprüfungen erfüllt)
     */
    private boolean startable = true;

    /**
     * ComboBox für Eingabe des Implementierungstyps
     */
    private ComboBox<String> comboBoxImplType;

    /**
     * Testfelder, Buttons und Labels der ServerGUI
     */
    private TextField serverPort, sendBufferSize, receiveBufferSize;
    private Button startButton, stopButton, finishButton;
    private final TextField startTimeField, receivedRequests, loggedInClients;

    /**
     * starting the GUI
     *
     * @param args available args, please only use non-default
     *             --nogui disables the gui
     *             --protocol=tcp | udp | rmi (default; udp and rmi not implemented yet)
     *             --port=40001 (default)
     *             --send-buffer=300000 (default)
     *             --receive-buffer=300000 (default)
     */
    public static void main(String[] args) {
        // Log4j2-Logging aus Datei konfigurieren
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File file = new File("config/log4j/log4j2.auditLogTcpServer.xml");
        context.setConfigLocation(file.toURI());

        // Anwendung starten
        launch(args);
    }

    /**
     * Konstruktor
     */
    public AuditLogFxGUI() {
        super("AuditLogServerGUI", 400, 400);

        loggedInClientCounter = new AtomicInteger(0);
        requestCounter = new AtomicInteger(0);
        startTimeField = createNotEditableTextField();
        receivedRequests = createNotEditableTextField();
        loggedInClients = createNotEditableTextField();
    }

    @Override
    public void start(final Stage stage) throws IllegalArgumentException {
        super.start(stage);

        stage.setOnCloseRequest(event -> {
            try {
                chatServer.stop();
            } catch (Exception ex) {
                LOG.error("Fehler beim Stoppen des Chat-Servers");
                ExceptionHandler.logException(ex);
            }
        });

        pane.setPadding(new Insets(10, 10, 10, 10));

        HBox label_eingabe = createHeader("Eingabe");
        pane.getChildren().add(label_eingabe);
        pane.getChildren().add(createInputPane());

        HBox label_informationen = createHeader("Informationen");
        pane.getChildren().add(label_informationen);
        pane.getChildren().add(createInfoPane());

        pane.getChildren().add(createHeader(""));
        pane.getChildren().add(createButtonPane());

        reactOnStartButton();
        reactOnStopButton();
        reactOnFinishButton();
        stopButton.setDisable(true);
    }

    /**
     * Eingabe-Pane erzeugen
     *
     * @return pane
     */
    private GridPane createInputPane() {
        final GridPane inputPane = new GridPane();

        final Label label = createLabel("Serverauswahl");
        label.setMinSize(100, 25);
        label.setMaxSize(100, 25);

        Label serverPortLabel = createLabel("Serverport");
        Label sendBufferSizeLabel = createLabel("Sendepuffer in Byte");
        Label receiveBufferSizeLabel = createLabel("Empfangspuffer in Byte");
        sendBufferSize = createEditableTextField(SystemConstants.DEFAULT_SEND_BUFFER_SIZE);
        receiveBufferSize = createEditableTextField(SystemConstants.DEFAULT_RECEIVE_BUFFER_SIZE);

        inputPane.setPadding(new Insets(5, 5, 5, 5));
        inputPane.setVgap(1);

        comboBoxImplType = createComboBox(implTypeOptions);
        serverPort = createEditableTextField(DEFAULT_SERVER_PORT);

        inputPane.add(label, 1, 3);
        inputPane.add(comboBoxImplType, 3, 3);
        inputPane.add(serverPortLabel, 1, 5);
        inputPane.add(serverPort, 3, 5);

        inputPane.add(sendBufferSizeLabel, 1, 7);
        inputPane.add(sendBufferSize, 3, 7);
        inputPane.add(receiveBufferSizeLabel, 1, 9);
        inputPane.add(receiveBufferSize, 3, 9);

        return inputPane;
    }

    /**
     * Info-Pain erzeugen
     *
     * @return pane
     */
    private GridPane createInfoPane() {
        GridPane infoPane = new GridPane();

        infoPane.setPadding(new Insets(5, 5, 5, 5));
        infoPane.setVgap(1);

        infoPane.add(createLabel("Startzeit"), 1, 3);
        infoPane.add(startTimeField, 3, 3);

        infoPane.add(createLabel("Empfangene Requests"), 1, 5);
        infoPane.add(receivedRequests, 3, 5);

        infoPane.add(createLabel("Angemeldete Clients"), 1, 7);
        infoPane.add(loggedInClients, 3, 7);

        return infoPane;
    }

    /**
     * Pane für Buttons erzeugen
     *
     * @return HBox
     */
    private HBox createButtonPane() {
        final HBox buttonPane = new HBox(5);

        startButton = new Button("Server starten");
        stopButton = new Button("Server stoppen");
        finishButton = new Button("Beenden");

        buttonPane.getChildren().addAll(startButton, stopButton, finishButton);
        buttonPane.setAlignment(Pos.CENTER);
        return buttonPane;
    }

    /**
     * Gewählten Implementierungstyp aus GUI auslesen
     */
    private String readImplTypeComboBox() {
        return (comboBoxImplType.getValue());
    }

    /**
     * Serverport aus GUI auslesen und prüfen
     *
     * @return Verwendeter Serverport
     */
    private int readServerPort() {
        String port = serverPort.getText();
        Tupel<Integer, Boolean> result = ServerStarter.validateServerPort(port);
        startable = result.getY();
        return result.getX();
    }

    /**
     * Größe des Sendepuffers in Byte auslesen und prüfen
     *
     * @return Eingegebene Sendepuffer-Größe
     */
    private int readSendBufferSize() {
        String size = sendBufferSize.getText();
        Tupel<Integer, Boolean> result = ServerStarter.validateSendBufferSize(size);
        startable = result.getY();
        return result.getX();
    }

    /**
     * Größe des Empfangspuffers in Byte auslesen und prüfen
     *
     * @return Eingegebene Empfangspuffer-Größe
     */
    private int readReceiveBufferSize() {
        String size = receiveBufferSize.getText();
        Tupel<Integer, Boolean> result = ServerStarter.validateReceiveBufferSize(size);
        startable = result.getY();
        return result.getX();
    }

    private String getCurrentTime(Calendar cal) {
        if (cal == null) return "";
        return new SimpleDateFormat("dd.MM.yy HH:mm:ss:SSS").format(cal.getTime());
    }

    /**
     * Reaktion auf das Betätigen des Start-Buttons
     */
    private void reactOnStartButton() {
        startButton.setOnAction(event -> {
            startable = true;

            // CHat-Server-Port aus GUI lesen
            int serverPortInt = readServerPort();

            // Zähler in der GUI initialisieren
            receivedRequests.setText("0");
            loggedInClients.setText("0");

            // Puffergrößen für Verbindung zu Chat-Clients aus GUI lesen
            int sendBufferSizeInt = readSendBufferSize();
            int receiveBufferSizeInt = readReceiveBufferSize();

            if (startable) {
                // Implementierungstyp, der zu starten ist, ermitteln und Chat-Server starten
                String implType = readImplTypeComboBox();

                try {
                    startChatServer(implType, serverPortInt, sendBufferSizeInt, receiveBufferSizeInt);
                } catch (Exception e) {
                    setAlert(
                            "Der Server konnte nicht gestartet werden," +
                                    "eventuell läuft ein anderer Server mit dem Port");
                    return;
                }

                startButton.setDisable(true);
                stopButton.setDisable(false);
                finishButton.setDisable(true);

                // Startzeit ermitteln
                cal = Calendar.getInstance();
                startTimeAsString = getCurrentTime(cal);
                showStartData(data);
            } else {
                setAlert("Bitte korrigieren Sie die rot markierten Felder");
            }
        });
    }

    /**
     * Reaktion auf das Betätigen des Stop-Buttons
     */
    private void reactOnStopButton() {
        stopButton.setOnAction(event -> {
            try {
                chatServer.stop();
            } catch (Exception e) {
                LOG.error("Fehler beim Stoppen des AuditLog-Servers");
                ExceptionHandler.logException(e);
            }

            // Zähler für Clients und Requests auf 0 stellen
            requestCounter.set(0);
            loggedInClientCounter.set(0);

            startButton.setDisable(false);
            stopButton.setDisable(true);
            finishButton.setDisable(false);

            // GUI-Einstellungen wieder auf Standard setzen
            startTimeField.setText("");
            receivedRequests.setText("");
            loggedInClients.setText("");
            sendBufferSize.setText(SystemConstants.DEFAULT_SEND_BUFFER_SIZE);
            receiveBufferSize.setText(SystemConstants.DEFAULT_RECEIVE_BUFFER_SIZE);
        });
    }

    /**
     * Reaktion auf das Betätigen des Finish-Buttons
     */
    private void reactOnFinishButton() {
        finishButton.setOnAction(event -> {
            LOG.debug("Schliessen-Button betätigt");
            try {
                chatServer.stop();
            } catch (Exception var3) {
                LOG.debug("Fehler beim Stoppen des AuditLog-Servers, AuditLog-Server eventuell noch gar nicht aktiv");
            }
            System.out.println("AuditLogServer-GUI ordnungsgemäß beendet");
            super.exit();
        });
    }

    /**
     * Audit-Log-Server starten
     *
     * @param implType          Implementierungstyp, der zu starten ist
     * @param serverPort        Serverport, die der Server als Listener-Port nutzen soll
     * @param sendBufferSize    Sendepuffergröße, die der Server nutzen soll
     * @param receiveBufferSize Empfangspuffergröße, die der Server nutzen soll
     */
    private void startChatServer(String implType, int serverPort, int sendBufferSize, int receiveBufferSize)
            throws Exception {
        AuditLogImplementationType serverImpl;
        if (implType.equals(SystemConstants.AUDIT_LOG_SERVER_TCP_IMPL)) {
            serverImpl = AuditLogImplementationType.AuditLogServerTCPImplementation;
        } else if (implType.equals(SystemConstants.AUDIT_LOG_SERVER_UDP_IMPL)) {
            serverImpl = AuditLogImplementationType.AuditLogServerUDPImplementation;
        } else {
            serverImpl = AuditLogImplementationType.AuditLogServerRMIImplementation;
        }

        try {
            //chatServer = ServerFactory.getServer(serverImpl, serverPort, sendBufferSize, receiveBufferSize, this);TODO
        } catch (Exception e) {
            LOG.error("Fehler beim Starten des Chat-Servers: " + e.getMessage());
            ExceptionHandler.logException(e);
            throw new Exception(e);
        }
        if (!startable) {
            setAlert("Bitte Korrigieren sie die rot markierten Felder");
        } else {
            // Server starten
            //chatServer.start();TODO
        }
    }

    /**
     * GUI-Feld für eingeloggte Clients über Event-Liste des JavaFX-GUI-Threads aktualisieren
     */
    private void updateLoggedInClients() {
        Platform.runLater(() -> {
            LOG.debug("runLater: run-Methode wird ausgeführt");
            LOG.debug("runLater: Logged in Clients: {}", loggedInClientCounter.get());
            loggedInClients.setText(String.valueOf(loggedInClientCounter.get()));
        });
    }

    /**
     * GUI-Feld für Anzahl empfangener Requests über Event-Liste des JavaFX-GUI-Threads aktualisieren
     */
    private void updateNumberOfRequests() {
        Platform.runLater(() -> {
            LOG.debug("runLater: run-Methode wird ausgeführt");
            LOG.debug("runLater: Received Requests: " + requestCounter.get());
            receivedRequests.setText(String.valueOf(requestCounter.get()));
        });
    }

    @Override
    public void showStartData(ServerStartData data) {
        startTimeField.setText(startTimeAsString);
    }

    @Override
    public void increaseNumberOfLoggedInClients() {
        loggedInClientCounter.getAndIncrement();
        LOG.debug("Eingeloggte Clients: " + loggedInClientCounter.get());
        updateLoggedInClients();
    }

    @Override
    public void decreaseNumberOfLoggedInClients() {
        loggedInClientCounter.getAndDecrement();
        LOG.debug("Eingeloggte Clients: " + loggedInClientCounter.get());
        updateLoggedInClients();
    }

    @Override
    public void increaseNumberOfRequests() {
        requestCounter.getAndIncrement();
        LOG.debug(requestCounter.get() + " empfangene Message Requests");
        updateNumberOfRequests();
    }
}