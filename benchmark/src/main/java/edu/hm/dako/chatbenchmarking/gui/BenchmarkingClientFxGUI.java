package edu.hm.dako.chatbenchmarking.gui;

import edu.hm.dako.chatbenchmarking.BenchmarkingClientCoordinator;
import edu.hm.dako.chatbenchmarking.BenchmarkingConstants;
import edu.hm.dako.common.ChatServerImplementationType;
import edu.hm.dako.common.SystemConstants;
import edu.hm.dako.common.gui.FxGUI;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import javax.swing.JProgressBar;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Formatter;
import java.util.regex.Pattern;

/**
 * GUI für den Benchmarking-Client in JavaFX-GUI-Technologie
 * Hinweis: Um die Größe der Objekte einheitlich zu gestalten, wird jedes Objekt durch eine eigene Methode erstellt,
 * in der die Größe festgelegt wird
 *
 * @author Paul Mandl, edited by Lerngruppe
 */

public class BenchmarkingClientFxGUI extends FxGUI implements BenchmarkingClientUserInterface {
    /**
     * min screen size for bigger window version, window will be smaller if not reached
     */
    protected final static int MIN_SCREEN_SIZE = 900;

    /**
     * Patterns für die Prüfung der eingegebenen IP-Adressen
     */
    private static final Pattern IPV6_PATTERN = Pattern
            .compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
    private static final Pattern IPV4_PATTERN = Pattern.compile(
            "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");

    /**
     * Message Area erstellen
     */
    final Label messageArea = new Label();

    /**
     * Buttons erstellen
     */
    final Button abortButton = new Button("Abbrechen"), startButton = new Button("Starten"),
            newButton = new Button("Neustart"), finishButton = new Button("Beenden");

    final VBox box = new VBox();

    private final StringProperty labelString = new SimpleStringProperty();

    /**
     * BenchmarkingClient
     */
    BenchmarkingClientCoordinator benchClient;

    /**
     * Eingabeparameter für GUI erzeugen
     */
    final UserInterfaceInputParameters iParam = new UserInterfaceInputParameters();

    /**
     * Auswahl für ComboBoxen
     */
    final ObservableList<String> implTypeOptions = FXCollections.observableArrayList(
            SystemConstants.IMPL_TCP_SIMPLE, SystemConstants.IMPL_TCP_ADVANCED);
    final ObservableList<String> measureTypeOptions = FXCollections
            .observableArrayList("Variable Threads", "Variable Length");

    /**
     * ComboBoxen
     */
    private ComboBox<String> optionListImplType;
    private ComboBox<String> optionListMeasureType;

    /**
     * Eingabefelder
     */
    private TextField textFieldNumberOfClientThreads, textFieldNumberOfMessagesPerClients, textFieldServerport,
            textFieldThinkTime, textFieldServerIpAddress, textFieldMessageLength, textFieldNumberOfMaxRetries,
            textFieldResponseTimeout;

    /**
     * Ausgabefelder für Laufzeitdaten
     */
    private TextField textFieldPlannedRequests, textFieldTestBegin, textFieldSentRequests, textFieldTestEnd,
            textFieldReceivedResponses, textFieldTestDuration, textFieldPlannedEventMessages,
            textFieldSentEventMessages, textFieldReceivedConfirmEvents, textFieldLostConfirmEvents,
            textFieldRetriedEvents, textFieldNumberOfRetries;

    /**
     * Ausgabefelder für Messergebnisse
     */
    private TextField textFieldAvgRTT, textFieldAvgServerTime, textFieldMaxRTT, textFieldMaxHeapUsage, textFieldMinRTT,
            textFieldAvgCpuUsage;

    /**
     * Statistische Ausgabefelder
     */
    private TextField textField10Percentile, textField25Percentile, textField50Percentile, textField75Percentile,
            textField90Percentile, textFieldRange, textFieldInterquartilRange, textFieldStandardDeviation;

    /**
     * Fortschrittsbalken
     */
    private ProgressBar progressBarFx;

    /**
     * Labels Eingabefelder
     */
    private Label implType, numberOfClientThreads, numberOfMessagesPerClients, serverport, thinkTime, serverIpAddress,
            messageLength, numberOfMaxRetries, responseTimeout;

    /**
     * Scroller für Message Area
     */
    private ScrollPane scrollPane;

    /**
     * Task für Progressbar
     */
    private Task<Boolean> task;
    private Label progressIndicator;
    private int progressCounter;

    /**
     * Laufzeitzähler erzeugen
     */
    private Long timeCounter = 0L;

    /**
     * Kennzeichen, ob alle Parameter ordnungsgemäß eingegeben wurden, um den Benchmark zu starten
     */
    private boolean startable = true;

    /**
     * Start der GUI
     * @param args - nicht verwendet
     */
    public static void main(String[] args) {//TODO parametrize
        // Log4j2-Logging aus Datei konfigurieren
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        File file = new File("config/log4j/log4j2.benchmarkingClient.xml");
        context.setConfigLocation(file.toURI());
        launch(args);
    }

    /**
     * Konstruktor
     */
    public BenchmarkingClientFxGUI() {
        super("Benchmarking Client", 1150, 650);
    }

    /**
     * Startmethode für FX-Application
     */
    @Override
    public void start(final Stage stage) throws IllegalArgumentException {
        super.start(stage);

        // Window vergrößern, falls möglich
        if (dim.getHeight() >= MIN_SCREEN_SIZE) {
            stage.setMinWidth(1235);
            stage.setMaxWidth(1235);
            stage.setMinHeight(850);
            stage.setMaxHeight(850);
        }

        // Progressbar erzeugen
        progressBarFx = createProgressbar();
        progressIndicator = new Label();
        progressIndicator.setMinWidth(550);
        progressIndicator.setMaxWidth(550);

        // GUI-Pane erzeugen
        createGuiPane();

        if (dim.getHeight() >= MIN_SCREEN_SIZE) {
            progressBarFx.setMinSize(1210, 30);
            progressBarFx.setMaxSize(1210, 30);
        } else {
            progressBarFx.setMinSize(1050, 30);
            progressBarFx.setMaxSize(1050, 30);
        }

        // Reaktionsroutinen für Buttons einrichten
        reactOnStartButton();
        reactOnNewButton();
        reactOnFinishButton();
        reactOnAbortButton();
    }

    /**
     * Pane für das Layout der GUI erzeugen
     */
    private void createGuiPane() {
        scrollPane = createScrollPane();

        int fontSize = dim.getHeight() >= MIN_SCREEN_SIZE ? (18) : (12);

        pane.getChildren().add(createHeader("Eingabeparameter", fontSize));
        pane.getChildren().add(createInputPane());

        pane.getChildren().add(createHeader("Laufzeitdaten", fontSize));
        pane.getChildren().add(createRunTimePane());

        pane.getChildren().add(createHeader("Messergebnisse", fontSize));
        pane.getChildren().add(createResultPane());
        pane.getChildren().add(scrollPane);
        pane.getChildren().add(createProgressPane());

        pane.getChildren().add(createHeader("", fontSize));
        pane.getChildren().add(createButtonPane());

        pane.setPadding(new Insets(5, 5, 5, 5));
        pane.setAlignment(Pos.CENTER);
    }

    /**
     * Pane für die Eingabeparameter erstellen
     * @return inputPane
     */
    private Pane createInputPane() {
        final GridPane inputPane = new GridPane();

        optionListImplType = createComboBox(implTypeOptions);
        optionListMeasureType = createComboBox(measureTypeOptions);
        optionListImplType.setValue(SystemConstants.IMPL_TCP_SIMPLE);
        optionListMeasureType.setValue("Variable Threads");

        // ComboBoxen zum Pane hinzufügen und Labels ergänzen
        implType = createLabel("Implementierungstyp");
        inputPane.add(implType, 1, 1);
        inputPane.add(optionListImplType, 3, 1);
        inputPane.add(createLabel("Art der Messung"), 5, 1);
        inputPane.add(optionListMeasureType, 7, 1);

        // Textfelder zum Pane hinzufügen und Labels ergänzen
        numberOfClientThreads = createLabel("Anzahl Client-Threads");
        inputPane.add(numberOfClientThreads, 1, 3);
        textFieldNumberOfClientThreads = createEditableTextField(inputPane, 3, 3, "1");

        numberOfMessagesPerClients = createLabel("Anzahl Nachrichten je Client");
        inputPane.add(numberOfMessagesPerClients, 1, 5);
        textFieldNumberOfMessagesPerClients = createEditableTextField(inputPane, 3, 5, "10");

        numberOfMaxRetries = createLabel("Max. Anzahl Wiederholungen");
        inputPane.add(numberOfMaxRetries, 1, 7);
        textFieldNumberOfMaxRetries = createEditableTextField(inputPane, 3, 7, "1");

        messageLength = createLabel("Nachrichtenl\u00e4nge in Byte");
        inputPane.add(messageLength, 5, 3);
        textFieldMessageLength = createEditableTextField(inputPane, 7, 3, "10");

        responseTimeout = createLabel("Response-Timeout in ms");
        inputPane.add(responseTimeout, 5, 5);
        textFieldResponseTimeout = createEditableTextField(inputPane, 7, 5, "2000");

        thinkTime = createLabel("Denkzeit in ms");
        inputPane.add(thinkTime, 5, 7);
        textFieldThinkTime = createEditableTextField(inputPane, 7, 7, "100");

        serverport = createLabel("Serverport");
        inputPane.add(serverport, 9, 3);
        textFieldServerport = createEditableTextField(inputPane, 11, 3, "50001");

        serverIpAddress = createLabel("Server-IP-Adresse");
        inputPane.add(serverIpAddress, 9, 5);
        textFieldServerIpAddress = createEditableTextField(inputPane, 11, 5, "localhost");

        return fillPane(inputPane);
    }

    /**
     * Pane mit Werten belegen
     * @param inputPane zur Bearbeitung
     * @return Referenz auf Pane
     */
    private Pane fillPane(GridPane inputPane) {
        if (dim.getHeight() >= MIN_SCREEN_SIZE) {
            inputPane.add(createLabel("", 230, 20, 13), 1, 9);
            // Abstände hinzufügen
            inputPane.setHgap(5);
            inputPane.setVgap(3);
        } else {
            // Abstände hinzufügen
            inputPane.setHgap(2);
            inputPane.setVgap(2);
        }
        inputPane.setAlignment(Pos.CENTER);
        return inputPane;
    }

    /**
     * Pane für die Laufzeitdaten erstellen
     * @return runTimePane
     */
    private Pane createRunTimePane() {

        final GridPane runTimePane = new GridPane();

        // Textfelder zum Pane hinzufügen und Label ergänzen

        runTimePane.add(createLabel("Geplante Requests"), 1, 1);
        textFieldPlannedRequests = createNotEditableTextField(runTimePane, 3, 1);

        runTimePane.add(createLabel("Gesendete Requests"), 5, 1);
        textFieldSentRequests = createNotEditableTextField(runTimePane, 7, 1);

        runTimePane.add(createLabel("Empfangene Responses"), 9, 1);
        textFieldReceivedResponses = createNotEditableTextField(runTimePane, 11, 1);

        runTimePane.add(createLabel("Testbeginn"), 1, 3);
        textFieldTestBegin = createNotEditableTextField(runTimePane, 3, 3);

        runTimePane.add(createLabel("Testende"), 5, 3);
        textFieldTestEnd = createNotEditableTextField(runTimePane, 7, 3);

        runTimePane.add(createLabel("Testdauer in s"), 9, 3);
        textFieldTestDuration = createNotEditableTextField(runTimePane, 11, 3);

        runTimePane.add(createLabel("Kalkulierte Event-Nachrichten"), 1, 5);
        textFieldPlannedEventMessages = createNotEditableTextField(runTimePane, 3, 5);

        runTimePane.add(createLabel("Gesendete Event-Nachrichten"), 5, 5);
        textFieldSentEventMessages = createNotEditableTextField(runTimePane, 7, 5);

        runTimePane.add(createLabel("Wiederholte Event-Nachrichten"), 9, 5);
        textFieldRetriedEvents = createNotEditableTextField(runTimePane, 11, 5);

        runTimePane.add(createLabel("Empfangene Confirm-Nachrichten"), 1, 7);
        textFieldReceivedConfirmEvents = createNotEditableTextField(runTimePane, 3, 7);

        runTimePane.add(createLabel("Verlorene Confirm-Nachrichten"), 5, 7);
        textFieldLostConfirmEvents = createNotEditableTextField(runTimePane, 7, 7);

        runTimePane.add(createLabel("Anzahl \u00dcbertragungswiederholungen"), 9, 7);
        textFieldNumberOfRetries = createNotEditableTextField(runTimePane, 11, 7);

        return fillPane(runTimePane);
    }

    /**
     * Pane für die Messergebnisse erstellen
     * @return resultPane
     */
    private Pane createResultPane() {
        // Pane initialisieren
        final GridPane resultPane = new GridPane();

        // Textfelder zum Pane hinzufügen und Label ergänzen
        resultPane.add(createLabel("Mittlere RTT in ms"), 1, 1);
        textFieldAvgRTT = createNotEditableTextField(resultPane, 3, 1);

        resultPane.add(createLabel("Maximale RTT in ms"), 5, 1);
        textFieldMaxRTT = createNotEditableTextField(resultPane, 7, 1);

        resultPane.add(createLabel("Minimale RTT in ms"), 9, 1);
        textFieldMinRTT = createNotEditableTextField(resultPane, 11, 1);

        resultPane.add(createLabel("Interquartilsabstand in ms"), 1, 3);
        textFieldInterquartilRange = createNotEditableTextField(resultPane, 3, 3);

        resultPane.add(createLabel("Spannweite in ms"), 5, 3);
        textFieldRange = createNotEditableTextField(resultPane, 7, 3);

        resultPane.add(createLabel("Standardabweichung in ms"), 9, 3);
        textFieldStandardDeviation = createNotEditableTextField(resultPane, 11, 3);

        resultPane.add(createLabel("10%-Percentile in ms"), 1, 5);
        textField10Percentile = createNotEditableTextField(resultPane, 3, 5);

        resultPane.add(createLabel("25%-Percentile in ms"), 5, 5);
        textField25Percentile = createNotEditableTextField(resultPane, 7, 5);

        resultPane.add(createLabel("50%-Percentile in ms"), 9, 5);
        textField50Percentile = createNotEditableTextField(resultPane, 11, 5);

        resultPane.add(createLabel("75%-Percentile in ms"), 1, 7);
        textField75Percentile = createNotEditableTextField(resultPane, 3, 7);

        resultPane.add(createLabel("90%-Percentile in ms"), 5, 7);
        textField90Percentile = createNotEditableTextField(resultPane, 7, 7);

        resultPane.add(createLabel("Mittlere Serverzeit in ms"), 1, 11);
        textFieldAvgServerTime = createNotEditableTextField(resultPane, 3, 11);

        resultPane.add(createLabel("Maximale Heap-Belegung in MiB"), 5, 11);
        textFieldMaxHeapUsage = createNotEditableTextField(resultPane, 7, 11);

        resultPane.add(createLabel("Mittlere CPU-Auslastung in %"), 9, 11);
        textFieldAvgCpuUsage = createNotEditableTextField(resultPane, 11, 11);

        if (dim.getHeight() >= MIN_SCREEN_SIZE) {
            resultPane.add(createLabel("", 230, 20, 13), 1, 13);
            // Abstände hinzufügen
            resultPane.setHgap(5);
            resultPane.setVgap(3);
        } else {
            // Abstände hinzufügen
            resultPane.setHgap(2);
            resultPane.setVgap(2);
        }
        resultPane.setAlignment(Pos.CENTER);

        return resultPane;
    }

    /**
     * Pane für Buttons erzeugen
     * @return buttonPane
     */
    private HBox createButtonPane() {
        final HBox buttonPane = new HBox(10);

        if (dim.getHeight() >= MIN_SCREEN_SIZE) {
            abortButton.setFont(Font.font(15));
            abortButton.setStyle(
                    "-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
            startButton.setFont(Font.font(15));
            startButton.setStyle(
                    "-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
            newButton.setFont(Font.font(15));
            newButton.setStyle(
                    "-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
            finishButton.setFont(Font.font(15));
        } else {
            abortButton.setFont(Font.font(12));
            abortButton.setStyle(
                    "-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
            startButton.setFont(Font.font(12));
            startButton.setStyle(
                    "-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
            newButton.setFont(Font.font(12));
            newButton.setStyle(
                    "-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
            finishButton.setFont(Font.font(12));
        }
        finishButton.setStyle(
                "-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
        buttonPane.getChildren().addAll(abortButton, startButton, newButton, finishButton);
        buttonPane.setAlignment(Pos.CENTER);
        return buttonPane;
    }

    /**
     * Fortschrittsbalken mit Fortschrittsanzeige erstellen
     * @return gridPane
     */
    private StackPane createProgressPane() {
        final StackPane pane = new StackPane();
        pane.getChildren().addAll(progressBarFx, progressIndicator);
        progressIndicator.setAlignment(Pos.CENTER);
        pane.setAlignment(Pos.CENTER);
        return pane;
    }

    /**
     * Erstellung einer MessageArea in einem ScrollPane
     * @return scrollPane
     */
    private ScrollPane createScrollPane() {
        final ScrollPane scrollPane = new ScrollPane();
        if (dim.getHeight() >= MIN_SCREEN_SIZE) {
            scrollPane.setMinSize(1210, 55);
            scrollPane.setMaxSize(1210, 55);
        } else {
            scrollPane.setMinSize(1050, 55);
            scrollPane.setMaxSize(1050, 55);
        }

        scrollPane.setContent(box);
        box.heightProperty().addListener((observable, oldValue, newValue) -> scrollPane.setVvalue((Double) newValue));
        scrollPane.setStyle(
                "-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
        return scrollPane;
    }

    /**
     * Es wird ein Task erstellt, der bei Erhöhung des progressCounter den Progressbar aktualisiert
     *
     * @return Task
     */
    private Task<Boolean> progressTask() {
        return new Task<>() {
            @Override
            protected Boolean call() {
                // Anzahl der erwarteten ProgressCounter-Erhöhungen berechnen:
                // alle Message-Requests ohne Logins und ohne Logouts
                int maxMessages = iParam.getNumberOfMessages() * iParam.getNumberOfClients();

                for (int i = 0; i < maxMessages; i = progressCounter) {
                    updateProgress(i + 1, maxMessages);
                }
                return Boolean.TRUE;
            }
        };
    }

    /**
     * Der ProgressBar wird an diesen Task gebunden
     */
    private void countUpProgressBar() {
        Platform.runLater(() -> {
            final NumberFormat format = new DecimalFormat("0.00 %");
            task.progressProperty().addListener((observable, oldValue, newValue) ->
                    labelString.setValue(format.format(newValue)));
            progressIndicator.textProperty().bind(labelString);
            if (dim.getHeight() >= MIN_SCREEN_SIZE) {
                progressIndicator.setFont(Font.font(13));
            } else {
                progressIndicator.setFont(Font.font(10));
            }
            progressBarFx.progressProperty().unbind();
            progressBarFx.progressProperty().bind(task.progressProperty());
            new Thread(task).start();
        });
    }

    /**
     * Reaktion auf Betätigen des Start-Buttons
     */
    private synchronized void reactOnStartButton() {
        startButton.setOnAction(event -> {
            abortButton.setDisable(false);
            newButton.setDisable(true);
            startButton.setDisable(true);
            Platform.runLater(() -> {
                task = progressTask();
                benchClient = new BenchmarkingClientCoordinator();
                startGui();
            });
        });
    }

    /**
     * Reaktion auf Betätigen des New-Buttons
     */
    private synchronized void reactOnNewButton() {
        newButton.setOnAction(event -> {
            newAction();
            startButton.setDisable(false);
            newButton.setDisable(true);
            abortButton.setDisable(true);

        });
    }

    /**
     * Reaktion auf Betätigen des Finish-Buttons
     */
    private synchronized void reactOnFinishButton() {
        finishButton.setOnAction(event -> {
            setMessageLine("Programm wird beendet...");
            super.exit();
        });
    }

    /**
     * Reaktion auf Betätigen des Abort-Buttons
     */
    private synchronized void reactOnAbortButton() {
        abortButton.setOnAction(event -> {
            benchClient.abortTest();
            setMessageLine("Testlauf wird abgebrochen...");
            try {
                benchClient.join();
            } catch (Exception ignored) {
            }
            setMessageLine("Testlauf beendet");
            if (benchClient.isTestAborted()) {
                benchClient.releaseTest();
            }
        });
    }

    /**
     * Die Benchmarking-GUI wird gestartet
     */
    private void startGui() {
        startable = true;
        scrollPane.setContent(box);

        readComboBoxes();
        setServerPort();
        setThinkTime();
        setNumberOfClientThreads();
        setNumberOfMessagesPerClient();
        setMessageLength();
        setResponseTimeOut();
        setNumberOfMaxRetries();
        setServerIpAddress();

        if (!startable) {
            setAlert("Bitte korrigieren Sie die rot markierten Felder!");
            startButton.setDisable(false);
        } else {
            benchClient.executeTest(iParam, this);
            countUpProgressBar();
        }
    }

    /**
     * Prüfen der Eingabe des Serverports
     */
    private void setServerPort() {
        String testString = textFieldServerport.getText();
        if (testString.matches("[0-9]+")) {
            int iServerPort = Integer.parseInt(textFieldServerport.getText());
            if ((iServerPort < 1) || (iServerPort > 65535)) {
                // Nicht im Wertebereich; Aktualisieren des Frames auf dem Bildschirm
                startable = false;
                serverport.setTextFill(Color.web(SystemConstants.RED_COLOR));
            } else {
                System.out.println("Serverport: " + iServerPort);
                iParam.setRemoteServerPort(iServerPort);
                serverport.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
            }
        } else {
            startable = false;
            serverport.setTextFill(Color.web(SystemConstants.RED_COLOR));
        }
    }

    /**
     * Prüfen der Eingabe der Denkzeit
     */
    private void setThinkTime() {
        String testString2 = textFieldThinkTime.getText();
        if (!testString2.matches("[0-9]+")) {
            // Nicht numerisch; Aktualisieren des Frames auf dem Bildschirm
            startable = false;
            thinkTime.setTextFill(Color.web(SystemConstants.RED_COLOR));
        } else {
            int iThinkTime = Integer.parseInt(textFieldThinkTime.getText());
            System.out.println("Denkzeit: " + iThinkTime + " ms");
            iParam.setClientThinkTime(iThinkTime);
            thinkTime.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
        }
    }

    /**
     * Prüfen der Eingabe der Anzahl an Clients
     */
    private void setNumberOfClientThreads() {
        String testString3 = textFieldNumberOfClientThreads.getText();
        if (testString3.matches("[0-9]+")) {
            int iClientThreads = Integer.parseInt(textFieldNumberOfClientThreads.getText());
            if (iClientThreads < 1 || iClientThreads > BenchmarkingConstants.MAX_CLIENTS) {
                // Nicht im Wertebereich; Aktualisieren des Frames auf dem Bildschirm
                startable = false;
                numberOfClientThreads.setTextFill(Color.web(SystemConstants.RED_COLOR));
            } else {
                System.out.println("Anzahl Client-Threads:" + iClientThreads);
                iParam.setNumberOfClients(iClientThreads);
                numberOfClientThreads.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
            }
        } else {
            startable = false;
            numberOfClientThreads.setTextFill(Color.web(SystemConstants.RED_COLOR));
        }
    }

    /**
     * Prüfen der Eingabe der Anzahl an Nachrichten pro Client
     */
    private void setNumberOfMessagesPerClient() {
        String testString4 = textFieldNumberOfMessagesPerClients.getText();
        if (testString4.matches("[0-9]+")) {
            int iNumberOfMessages = Integer.parseInt(textFieldNumberOfMessagesPerClients.getText());
            if (iNumberOfMessages < 1
                    || iNumberOfMessages > BenchmarkingConstants.MAX_MESSAGES_PER_CLIENT) {
                // nicht numerisch; Aktualisieren des Frames auf dem Bildschirm
                startable = false;
                numberOfMessagesPerClients.setTextFill(Color.web(SystemConstants.RED_COLOR));
            } else {
                System.out.println("Anzahl Nachrichten:" + iNumberOfMessages);
                iParam.setNumberOfMessages(iNumberOfMessages);
                numberOfMessagesPerClients.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
            }
        } else {
            startable = false;
            numberOfMessagesPerClients.setTextFill(Color.web(SystemConstants.RED_COLOR));
        }
    }

    /**
     * Prüfen der Eingabe der Nachrichtenlänge
     */
    private void setMessageLength() {
        String testString5 = textFieldMessageLength.getText();
        if (testString5.matches("[0-9]+")) {
            int iMessageLength = Integer.parseInt(textFieldMessageLength.getText());
            if ((iMessageLength < 1)
                    || (iMessageLength > BenchmarkingConstants.MAX_MESSAGE_LENGTH)) {
                // nicht im Wertebereich; Aktualisieren des Frames auf dem Bildschirm
                startable = false;
                messageLength.setTextFill(Color.web(SystemConstants.RED_COLOR));
            } else {
                System.out.println("Nachrichtenlänge:" + iMessageLength + " Byte");
                iParam.setMessageLength(iMessageLength);
                messageLength.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
            }
        } else {
            startable = false;
            messageLength.setTextFill(Color.web(SystemConstants.RED_COLOR));
        }
    }

    /**
     * Prüfen der Eingabe für den Response-Timeout
     */
    private void setResponseTimeOut() {
        String testString6 = textFieldResponseTimeout.getText();
        if (!testString6.matches("[0-9]+")) {
            // nicht numerisch; Aktualisieren des Frames auf dem Bildschirm
            startable = false;
            responseTimeout.setTextFill(Color.web(SystemConstants.RED_COLOR));
        } else {
            int iResponseTimeout = Integer.parseInt(textFieldResponseTimeout.getText());
            System.out.println("Response-Timeout:" + iResponseTimeout);
            iParam.setResponseTimeout(iResponseTimeout);
            responseTimeout.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
        }
    }

    /**
     * Prüfen der Eingabe für die maximale Anzahl an Wiederholungen
     */
    private void setNumberOfMaxRetries() {
        String testString7 = textFieldNumberOfMaxRetries.getText();
        if (testString7.matches("[0-9]+")) {
            int iNumberOfMaxRetries = Integer.parseInt(textFieldNumberOfMaxRetries.getText());
            if (iNumberOfMaxRetries > 5000) {
                // nicht im Wertebereich; Aktualisieren des Frames auf dem Bildschirm
                startable = false;
                numberOfMaxRetries.setTextFill(Color.web(SystemConstants.RED_COLOR));
            } else {
                System.out.println("Maximale Anzahl Wiederholungen:" + iNumberOfMaxRetries);
                iParam.setNumberOfRetries(iNumberOfMaxRetries);
                numberOfMaxRetries.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
            }
        } else {
            // nicht numerisch; Aktualisieren des Frames auf dem Bildschirm
            startable = false;
            numberOfMaxRetries.setTextFill(Color.web(SystemConstants.RED_COLOR));
        }
    }

    /**
     * Prüfen der Eingabe für die IP-Adresse
     */
    private void setServerIpAddress() {
        String testString = textFieldServerIpAddress.getText();
        if (testString.equals("localhost")) {
            System.out.println("RemoteServerAddress:" + testString);
            iParam.setRemoteServerAddress(testString);

            serverIpAddress.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
        } else if (IPV6_PATTERN.matcher(testString).matches()) {
            System.out.println("RemoteServerAddress:" + testString);
            iParam.setRemoteServerAddress(testString);

            serverIpAddress.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
        } else if (IPV4_PATTERN.matcher(testString).matches()) {
            System.out.println("RemoteServerAddress:" + testString);
            iParam.setRemoteServerAddress(testString);

            serverIpAddress.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
        } else {
            startable = false;
            serverIpAddress.setTextFill(Color.web(SystemConstants.RED_COLOR));
        }
    }

    /**
     * Eingabe über die ComboBoxen auslesen
     */
    private void readComboBoxes() {
        // ComboBoxen auslesen
        String item = optionListImplType.getValue();
        switch (item) {
            case SystemConstants.IMPL_TCP_ADVANCED -> {
                iParam.setChatServerImplementationType(
                        ChatServerImplementationType.TCPAdvancedImplementation);
                implType.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
            }
            case SystemConstants.IMPL_TCP_SIMPLE -> {
                iParam.setChatServerImplementationType(
                        ChatServerImplementationType.TCPSimpleImplementation);
                implType.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
            }
            case SystemConstants.IMPL_UDP_ADVANCED -> {
                setAlert("Der Typ UDPAdvanced wurde noch nicht Implementiert");
                startable = false;
                implType.setTextFill(Color.web(SystemConstants.RED_COLOR));
            }
            default -> {
                setAlert("Kein Implementierungstyp ausgewählt!");
                startable = false;
                implType.setTextFill(Color.web(SystemConstants.RED_COLOR));
            }
        }
        String item1 = optionListMeasureType.getValue();
        if (item1.equals("Variable Threads")) {
            iParam.setMeasurementType(UserInterfaceInputParameters.MeasurementType.VarThreads);
        } else if (item1.equals("Variable Length")) {
            iParam
                    .setMeasurementType(UserInterfaceInputParameters.MeasurementType.VarMsgLength);
        } else {
            setAlert("Art der Messung nicht festgelegt!");
            startable = false;
        }
    }

    /**
     * Reaktion auf das Betätigen des Neu-Buttons
     */
    private void newAction() {
        // Löschen bzw. initialisieren der Ausgabefelder
        textFieldPlannedRequests.setText("");
        textFieldTestBegin.setText("");
        textFieldSentRequests.setText("");
        textFieldTestEnd.setText("");
        textFieldReceivedResponses.setText("");
        textFieldTestDuration.setText("");
        textFieldAvgRTT.setText("");
        textFieldAvgServerTime.setText("");
        textFieldMaxRTT.setText("");
        textFieldAvgCpuUsage.setText("");
        textFieldMaxHeapUsage.setText("");
        textFieldMinRTT.setText("");
        textField10Percentile.setText("");
        textField25Percentile.setText("");
        textField50Percentile.setText("");
        textField75Percentile.setText("");
        textField90Percentile.setText("");
        textFieldRange.setText("");
        textFieldStandardDeviation.setText("");
        textFieldInterquartilRange.setText("");
        textFieldNumberOfRetries.setText("");
        textFieldPlannedEventMessages.setText("");
        textFieldSentEventMessages.setText("");
        textFieldReceivedConfirmEvents.setText("");
        textFieldLostConfirmEvents.setText("");
        textFieldRetriedEvents.setText("");

        Platform.runLater(() -> {
            progressBarFx.progressProperty().unbind();
            progressIndicator.textProperty().unbind();
            progressBarFx.setProgress(0);
            progressIndicator.setText("");
            messageArea.textProperty().unbind();
            messageArea.setText("");
            progressCounter = 0;
        });
    }

    /**
     * Anzeige der übergebenen Startdaten
     */
    @Override
    public void showStartData(UserInterfaceStartData data) {
        String strNumberOfRequests = String.valueOf(data.getNumberOfRequests());

        String strNumberOfPlannedEventMessages = String.valueOf(
                data.getNumberOfPlannedEventMessages());

        Platform.runLater(() -> {
            textFieldPlannedRequests.setText(strNumberOfRequests);
            textFieldPlannedEventMessages.setText(strNumberOfPlannedEventMessages);
            textFieldTestBegin.setText(data.getStartTime());
        });

    }

    /**
     * Ergebnisse eines Testlaufs anzeigen
     * @param data Testergebnisse
     */
    @Override
    public void showResultData(UserInterfaceResultData data) {
        Platform.runLater(() -> {
            textFieldSentRequests
                    .setText(String.valueOf(data.getNumberOfSentRequests()));
            textFieldTestEnd.setText(data.getEndTime());
            textFieldReceivedResponses
                    .setText(String.valueOf(data.getNumberOfResponses()));
            textFieldMaxHeapUsage.setText(String.valueOf(data.getMaxHeapSize()));
            textFieldNumberOfRetries
                    .setText(String.valueOf(data.getNumberOfRetries()));

            Formatter formatter = new Formatter();
            textFieldAvgRTT
                    .setText(formatter.format("%.2f", (data.getMean())).toString());
            formatter.close();

            formatter = new Formatter();
            textFieldAvgServerTime.setText(
                    formatter.format("%.2f", (data.getAvgServerTime())).toString());
            formatter.close();

            formatter = new Formatter();
            textFieldMaxRTT.setText(
                    formatter.format("%.2f", (data.getMaximum())).toString());
            formatter.close();

            formatter = new Formatter();
            textFieldMinRTT.setText(
                    formatter.format("%.2f", (data.getMinimum())).toString());
            formatter.close();

            formatter = new Formatter();
            textField10Percentile.setText(
                    formatter.format("%.2f", (data.getPercentile10())).toString());
            formatter.close();

            formatter = new Formatter();
            textField25Percentile.setText(
                    formatter.format("%.2f", (data.getPercentile25())).toString());
            formatter.close();

            formatter = new Formatter();
            textField50Percentile.setText(
                    formatter.format("%.2f", (data.getPercentile50())).toString());
            formatter.close();

            formatter = new Formatter();
            textField75Percentile.setText(
                    formatter.format("%.2f", (data.getPercentile75())).toString());
            formatter.close();

            formatter = new Formatter();
            textField90Percentile.setText(
                    formatter.format("%.2f", (data.getPercentile90())).toString());
            formatter.close();

            formatter = new Formatter();
            textFieldRange
                    .setText(formatter.format("%.2f", (data.getRange())).toString());
            formatter.close();

            formatter = new Formatter();
            textFieldInterquartilRange.setText(formatter
                    .format("%.2f", (data.getInterQuartilRange())).toString());
            formatter.close();

            formatter = new Formatter();
            textFieldStandardDeviation.setText(formatter
                    .format("%.2f", (data.getStandardDeviation())).toString());
            formatter.close();

            formatter = new Formatter();
            textFieldAvgCpuUsage.setText(formatter
                    .format("%.2f", (data.getMaxCpuUsage() * 100)).toString());

            textFieldSentEventMessages
                    .setText(String.valueOf(data.getNumberOfSentEventMessages()));
            textFieldReceivedConfirmEvents
                    .setText(String.valueOf(data.getNumberOfReceivedConfirmEvents()));
            textFieldLostConfirmEvents
                    .setText(String.valueOf(data.getNumberOfLostConfirmEvents()));
            textFieldRetriedEvents
                    .setText(String.valueOf(data.getNumberOfRetriedEvents()));
            formatter.close();
        });
    }

    /**
     * Nachrichtenzeile in der Message Area der GUI ergänzen
     * @param message Nachrichtentext Auszugebende Nachricht
     */
    @Override
    public void setMessageLine(String message) {
        Label value = new Label();
        value.setText(message);
        Platform.runLater(() -> box.getChildren().add(value));
    }

    /**
     * Laufzeitfeld in der GUI zurücksetzen, GUI-Aktion wird in die Queue des Event-Dispatching-Thread eingetragen
     */
    @Override
    public void resetCurrentRunTime() {
        timeCounter = 0L;
        String strTimeCounter = String.valueOf(timeCounter);
        Platform.runLater(() -> textFieldTestDuration.setText(strTimeCounter));
    }

    /**
     * GUI-Feld für die Ausgabe der Laufzeit hinzufügen, GUI-Aktion wird in die Queue des Event-Dispatching-Thread
     * eingetragen
     */
    @Override
    public void addCurrentRunTime(long sec) {
        timeCounter += sec;
        String strTimeCounter = (timeCounter).toString();
        Platform.runLater(() ->
                textFieldTestDuration.setText(strTimeCounter));
    }

    /**
     * Buttons nach Testende aktiv schalten
     */
    @Override
    public void testFinished() {
        abortButton.setDisable(true);
        startButton.setDisable(true);
        newButton.setDisable(false);
        finishButton.setDisable(false);
    }

    /**
     * Progressbar für FX-GUI
     */
    @Override
    public ProgressBar getProgressBarFx() {
        return progressBarFx;
    }

    /**
     * Methode zum Aktualisieren des Progressbar. Die Methode wird immer aufgerufen wird, wenn eine Response-Nachricht
     * empfangen oder ein Client ein- oder ausgeloggt wird
     */
    @Override
    public void countUpProgressTask() {
        progressCounter++;
    }

    /**
     * Dummy-Methode: Progressbar für Swing-GUI
     */
    @Override
    public JProgressBar getProgressBar() {
        return null;
    }

    @Override
    protected ComboBox<String> createComboBox(ObservableList<String> options) {
        ComboBox<String> comboBox = super.createComboBox(options);
        if (dim.getHeight() >= MIN_SCREEN_SIZE) {
            comboBox.setStyle("-fx-font-size: 10px;");
        }

        return comboBox;
    }
    @Override
    protected TextField createEditableTextField(GridPane pane, int columnIndex, int rowIndex, String value) {
        final TextField textField = super.createEditableTextField(pane, columnIndex, rowIndex, value);
        if (dim.getHeight() >= MIN_SCREEN_SIZE) {
            textField.setStyle("-fx-font-size: 10px;");
        }

        return textField;
    }
    @Override
    protected Label createLabel(String value) {
        int labelWidth = dim.getHeight() >= MIN_SCREEN_SIZE ? (230) : (190);
        int fontSize = dim.getHeight() >= MIN_SCREEN_SIZE ? (13) : (10);
        return createLabel(value, labelWidth, 20, fontSize);
    }
    @Override
    protected Label createLabel(String value, int width, int height, int fontSize) {
        Label label = super.createLabel(value, width, height, fontSize);
        label.setAlignment(Pos.BASELINE_RIGHT);
        return label;
    }
    @Override
    protected TextField createNotEditableTextField(GridPane pane, int columnIndex, int rowIndex) {
        final TextField textField = super.createNotEditableTextField(pane, columnIndex, rowIndex);
        if (dim.getHeight() >= MIN_SCREEN_SIZE) {
            textField.setStyle("-fx-font-size: 10px;");
        }

        return textField;
    }
}