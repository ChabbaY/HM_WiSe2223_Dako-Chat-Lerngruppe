package edu.hm.dako.chatServer;

import edu.hm.dako.common.AuditLogImplementationType;
import edu.hm.dako.common.ChatServerImplementationType;
import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.common.SystemConstants;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Benutzeroberflaeche zum Starten des Chat-Servers
 * @author Paul Mandl
 */
public class ChatServerGUI extends Application implements ChatServerGuiInterface {

    // Standard-Port des Servers
    static final String DEFAULT_SERVER_PORT = "50001";

    private static final Logger LOG = LogManager.getLogger(ChatServerGUI.class);

    // Interface der Chat-Server-Implementierung
    private static ChatServerInterface chatServer;

    // Zaehler fuer die eingeloggten Clients und die empfangenen Request
    private static AtomicInteger loggedInClientCounter;
    private static AtomicInteger requestCounter;

    // Daten, die beim Start der GUI uebergeben werden
    private final ServerStartData data = new ServerStartData();

    // Moegliche Belegungen des Implementierungsfeldes in der GUI
    ObservableList<String> implTypeOptions = FXCollections.observableArrayList(
            SystemConstants.IMPL_TCP_SIMPLE, SystemConstants.IMPL_TCP_ADVANCED);
    ObservableList<String> auditLogServerImplTypeOptions = FXCollections.observableArrayList(
            SystemConstants.AUDIT_LOG_SERVER_TCP_IMPL, SystemConstants.AUDIT_LOG_SERVER_UDP_IMPL, SystemConstants.AUDIT_LOG_SERVER_RMI_IMPL);

    // Server-Startzeit als String
    private String startTimeAsString;

    // Kalender zur Umrechnung der Startzeit
    private Calendar cal;

    // Flag, das angibt, ob der Server gestartet werden kann (alle
    // Plausibilitaetspruefungen erfuellt)
    private boolean startable = true;

    // Combobox fuer Eingabe des Implementierungstyps
    private ComboBox<String> comboBoxImplType;

    // Combobox fuer AuditLogServer-Implementierung
    private ComboBox<String> comboBoxAuditLogServerType;

    // Testfelder, Buttons und Labels der ServerGUI

    private final VBox pane = new VBox(5);
    private TextField serverPort;
    private TextField sendBufferSize;
    private TextField receiveBufferSize;
    private TextField auditLogServerHostnameOrIp;
    private TextField auditLogServerPort;
    private Label serverPortLabel;
    private Label sendBufferSizeLabel;
    private Label receiveBufferSizeLabel;
    private Label auditLogServerPortLabel;
    private CheckBox enableAuditLogServerCheckbox;
    private Button startButton;
    private Button stopButton;
    private Button finishButton;
    private final TextField startTimeField;
    private final TextField receivedRequests;
    private final TextField loggedInClients;

    /**
     * Konstruktion der ServerGUI
     */
    public ChatServerGUI() {
        loggedInClientCounter = new AtomicInteger(0);
        requestCounter = new AtomicInteger(0);
        startTimeField = createNotEditableTextfield();
        receivedRequests = createNotEditableTextfield();
        loggedInClients = createNotEditableTextfield();
    }

    public static void main(String[] args) {

        // Log4j2-Logging aus Datei konfigurieren
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File file = new File("config/log4j/log4j2.chatServer.xml");
        context.setConfigLocation(file.toURI());

        // Anwendung starten
        launch(args);
    }

    @Override
    public void start(final Stage stage) throws Exception {

        stage.setTitle("ChatServerGUI");
        stage.setScene(new Scene(pane, 415, 465));
        stage.show();

        stage.setOnCloseRequest(event -> {
            try {
                ChatServerGUI.chatServer.stop();
            } catch (Exception ex) {
                LOG.error("Fehler beim Stoppen des Chat-Servers");
                ExceptionHandler.logException(ex);
            }
        });

        pane.setStyle("-fx-background-color: cornsilk");
        pane.setPadding(new Insets(10, 10, 10, 10));

        pane.getChildren().add(createSeparator("Eingabe", 315));
        pane.getChildren().add(createInputPane());

        pane.getChildren().add(createSeparator("Informationen", 285));
        pane.getChildren().add(createInfoPane());

        pane.getChildren().add(createSeparator("", 360));
        pane.getChildren().add(createButtonPane());

        reactOnStartButton();
        reactOnStopButton();
        reactOnFinishButton();
        stopButton.setDisable(true);
    }

    /**
     * Eingabe-Pane erzeugen
     * @return pane
     */
    private GridPane createInputPane() {
        final GridPane inputPane = new GridPane();

        final Label label = new Label("Serverauswahl");
        label.setMinSize(100, 25);
        label.setMaxSize(100, 25);
        Label auditLogServerHostnameOrIpLabel = createLabel("AuditLogServer Hostname/IP-Adr.");

        serverPortLabel = createLabel("Serverport");
        sendBufferSizeLabel = createLabel("Sendepuffer in Byte");
        receiveBufferSizeLabel = createLabel("Empfangspuffer in Byte");
        auditLogServerPortLabel = createLabel("AuditLogServer/RMI-Registry Port");
        sendBufferSize = createEditableTextfield(SystemConstants.DEFAULT_SENDBUFFER_SIZE);
        receiveBufferSize = createEditableTextfield(SystemConstants.DEFAULT_RECEIVEBUFFER_SIZE);

        Label auditLogActivate = createLabel("AuditLog aktivieren");
        Label auditLogConnectionType = createLabel("AuditLog-Server Verbindungstyp");

        inputPane.setPadding(new Insets(5, 5, 5, 5));
        inputPane.setVgap(1);

        comboBoxImplType = createImplTypeComboBox(implTypeOptions);
        serverPort = createEditableTextfield(DEFAULT_SERVER_PORT);
        auditLogServerHostnameOrIp = createEditableTextfield(SystemConstants.DEFAULT_AUDIT_LOG_SERVER_NAME);
        auditLogServerPort = createEditableTextfield(SystemConstants.DEFAULT_AUDIT_LOG_SERVER_PORT);
        comboBoxAuditLogServerType = createAuditLogTypeComboBox(auditLogServerImplTypeOptions);

        enableAuditLogServerCheckbox = new CheckBox();
        enableAuditLogServerCheckbox.setSelected(true);

        enableAuditLogServerCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (enableAuditLogServerCheckbox.isSelected()) {
                auditLogServerHostnameOrIp.setEditable(true);
                auditLogServerHostnameOrIp.setStyle("-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
                auditLogServerPort.setEditable(true);
                auditLogServerPort.setStyle("-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
                comboBoxAuditLogServerType.setEditable(true);
                comboBoxAuditLogServerType.setStyle("-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
            } else {
                auditLogServerHostnameOrIp.setEditable(false);
                auditLogServerHostnameOrIp.setStyle("-fx-background-color: gray;");
                auditLogServerPort.setEditable(false);
                auditLogServerPort.setStyle("-fx-background-color: gray;");
                comboBoxAuditLogServerType.setEditable(false);
                comboBoxAuditLogServerType.setStyle("-fx-background-color: gray;");
            }
        });

        inputPane.add(label, 1, 3);
        inputPane.add(comboBoxImplType, 3, 3);
        inputPane.add(serverPortLabel, 1, 5);
        inputPane.add(serverPort, 3, 5);

        inputPane.add(sendBufferSizeLabel, 1, 7);
        inputPane.add(sendBufferSize, 3, 7);
        inputPane.add(receiveBufferSizeLabel, 1, 9);
        inputPane.add(receiveBufferSize, 3, 9);

        inputPane.add(auditLogActivate, 1, 11);
        inputPane.add(enableAuditLogServerCheckbox, 3, 11);
        inputPane.add(auditLogServerHostnameOrIpLabel, 1, 13);
        inputPane.add(auditLogServerHostnameOrIp, 3, 13);
        inputPane.add(auditLogServerPortLabel, 1, 15);
        inputPane.add(auditLogServerPort, 3, 15);
        inputPane.add(auditLogConnectionType, 1, 17);
        inputPane.add(comboBoxAuditLogServerType, 3, 17);

        return inputPane;
    }

    /**
     * Info-Pain erzeugen
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
     * Pane fuer Buttons erzeugen
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
     * Label erzeugen
     * @param value Wert fuer das Label
     * @return Label Referenz auf das Label
     */
    private Label createLabel(String value) {
        final Label label = new Label(value);
        label.setMinSize(200, 25);
        label.setMaxSize(200, 25);
        return label;
    }

    /**
     * Aufbau der Combobox fuer die Serverauswahl in der GUI
     * @param options Optionen fuer Implementierungstyp
     * @return Combobox
     */
    private ComboBox<String> createImplTypeComboBox(ObservableList<String> options) {
        return getStringComboBox(options);
    }

    /**
     * Aufbau der Combobox fuer Strings in der GUI
     * @param options Optionen fuer String
     * @return Combobox
     */
    private ComboBox<String> getStringComboBox(ObservableList<String> options) {
        ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setMinSize(155, 28);
        comboBox.setMaxSize(155, 28);
        comboBox.setValue(options.get(0));
        comboBox.setStyle(
                "-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
        return comboBox;
    }

    /**
     * Aufbau der Combobox fuer die AuditLog-Server Verbindung
     * @param options Optionen fuer Verbindungstyp
     * @return Combobox
     */
    private ComboBox<String> createAuditLogTypeComboBox(ObservableList<String> options) {
        return getStringComboBox(options);
    }

    /**
     * Trennlinie erstellen
     * @param value Text der Trennlinie
     * @param size  Groesse der Trennlinie
     * @return Trennlinie
     */
    private HBox createSeparator(String value, int size) {
        // Separator erstellen
        final HBox labeledSeparator = new HBox();
        final Separator rightSeparator = new Separator(Orientation.HORIZONTAL);
        final Label textOnSeparator = new Label(value);

        textOnSeparator.setFont(Font.font(12));

        rightSeparator.setMinWidth(size);
        rightSeparator.setMaxWidth(size);

        labeledSeparator.getChildren().add(textOnSeparator);
        labeledSeparator.getChildren().add(rightSeparator);
        labeledSeparator.setAlignment(Pos.BASELINE_LEFT);

        return labeledSeparator;
    }

    /**
     * Nicht editierbares Feld erzeugen
     * @return Textfeld
     */
    private TextField createNotEditableTextfield() {
        TextField textField = new TextField("");
        textField.setMaxSize(155, 28);
        textField.setMinSize(155, 28);
        textField.setEditable(false);
        textField.setStyle(
                "-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
        return textField;
    }

    /**
     * Erstellung editierbarer Textfelder
     * @param value Feldinhalt
     * @return textField
     */
    private TextField createEditableTextfield(String value) {
        TextField textField = new TextField(value);
        textField.setMaxSize(155, 28);
        textField.setMinSize(155, 28);
        textField.setEditable(true);
        textField.setStyle(
                "-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 5px, 5px, 5px, 5px");
        return textField;
    }


    /**
     * Reaktion auf das Betaetigen des Start-Buttons
     */
    private void reactOnStartButton() {
        startButton.setOnAction(event -> {
            startable = true;

            // CHat-Server-Port aus GUI lesen
            int serverPortInt = readServerPort();

            // Zaehler in der GUI initialisieren
            receivedRequests.setText("0");
            loggedInClients.setText("0");

            // Puffergroessen fuer Verbindung zu Chat-Clients aus GUI lesen
            int sendBufferSizeInt = readSendBufferSize();
            int receiveBufferSizeInt = readReceiveBufferSize();

            // Hostname fuer AuditLog-Server
            String auditLogServerHostname;

            String auditLogServerImplType = readAuditLogComboBox();
            if (Objects.equals(auditLogServerImplType, SystemConstants.AUDIT_LOG_SERVER_RMI_IMPL)) {
                // RMI fuer AuditLog-Server ausgewaehlt, GUI-Einstellungen fuer RMI-AuditLog-Server anpassen
                auditLogServerPort.setText(SystemConstants.DEFAULT_AUDIT_LOG_SERVER_RMI_REGISTRY_PORT);
            }

            if (startable) {
                // Implementierungstyp, der zu starten ist, ermitteln und
                // Chat-Server starten
                String implType = readImplTypeComboBox();

                if (enableAuditLogServerCheckbox.isSelected()) {
                    auditLogServerHostname = readAuditLogServerHostnameOrIp();
                    auditLogServerImplType = readAuditLogComboBox();
                    int auditLogServerPortInt = readAuditLogServerPort(auditLogServerImplType);

                    try {
                        startChatServerWithAuditLogServer(implType, serverPortInt, sendBufferSizeInt, receiveBufferSizeInt,
                                auditLogServerHostname, auditLogServerPortInt, auditLogServerImplType);
                    } catch (Exception e) {
                        setAlert(
                                "Der Server konnte nicht gestartet werden oder die Verbindung zum AuditLogServer konnte " +
                                        "nicht hergestellt werden, evtl. laeuft ein anderer Server mit dem Port");
                        return;
                    }
                } else {
                    try {
                        startChatServer(implType, serverPortInt, sendBufferSizeInt, receiveBufferSizeInt);
                    } catch (Exception e) {
                        setAlert(
                                "Der Server konnte nicht gestartet werden, evtl. laeuft ein anderer Server mit dem Port");
                        return;
                    }
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
     * Reaktion auf das Betaetigen des Stop-Buttons
     */
    private void reactOnStopButton() {

        stopButton.setOnAction(event -> {
            try {
                chatServer.stop();
            } catch (Exception e) {
                LOG.error("Fehler beim Stoppen des Chat-Servers");
                ExceptionHandler.logException(e);
            }

            // Zaehler fuer Clients und Requests auf 0 stellen
            requestCounter.set(0);
            loggedInClientCounter.set(0);

            startButton.setDisable(false);
            stopButton.setDisable(true);
            finishButton.setDisable(false);
            enableAuditLogServerCheckbox.setDisable(false);

            // GUI-Einstellungen wieder auf Standard setzen
            startTimeField.setText("");
            receivedRequests.setText("");
            loggedInClients.setText("");
            auditLogServerPort.setText(SystemConstants.DEFAULT_AUDIT_LOG_SERVER_PORT);
            sendBufferSize.setText(SystemConstants.DEFAULT_SENDBUFFER_SIZE);
            receiveBufferSize.setText(SystemConstants.DEFAULT_RECEIVEBUFFER_SIZE);
        });
    }

    /**
     * Reaktion auf das Betaetigen des Finish-Buttons
     */
    private void reactOnFinishButton() {
        LOG.debug("Schliessen-Button betaetigt");
        finishButton.setOnAction(event -> {
            try {
                ChatServerGUI.chatServer.stop();
            } catch (Exception var3) {
                LOG.debug("Fehler beim Stoppen des Chat-Servers, Chat-Server evtl. noch gar nicht aktiv");
            }
            System.out.println("ChatServer-GUI ordnungsgemaess beendet");
            Platform.exit();
        });
    }

    /**
     * AuditLogServer-Typ aus GUI auslesen
     */
    private String readAuditLogComboBox() {
        String implType;
        if (comboBoxAuditLogServerType.getValue() == null) {
            implType = SystemConstants.AUDIT_LOG_SERVER_TCP_IMPL;
        } else {
            implType = comboBoxAuditLogServerType.getValue();
        }

        return (implType);
    }

    /**
     * Gewaehlten Implementierungstyp aus GUI auslesen
     */
    private String readImplTypeComboBox() {
        return (new String(comboBoxImplType.getValue().toString()));
    }



    private void startChatServerWithAuditLogServer(String implType, int serverPort, int sendBufferSize,
                                                   int receiveBufferSize, String auditLogServerHostname, int auditLogServerPort,
                                                   String auditLogServerImplType) throws Exception {

        ChatServerImplementationType serverImpl;
        if (implType.equals(SystemConstants.IMPL_TCP_ADVANCED)) {
            serverImpl = ChatServerImplementationType.TCPAdvancedImplementation;
        } else {
            serverImpl = ChatServerImplementationType.TCPSimpleImplementation;
        }

        AuditLogImplementationType auditLogImplementationType;
        if (auditLogServerImplType.equals(SystemConstants.AUDIT_LOG_SERVER_TCP_IMPL)) {
            auditLogImplementationType = AuditLogImplementationType.AuditLogServerTCPImplementation;
        } else if (auditLogServerImplType.equals(SystemConstants.AUDIT_LOG_SERVER_UDP_IMPL)) {
            auditLogImplementationType = AuditLogImplementationType.AuditLogServerUDPImplementation;
        } else if (auditLogServerImplType.equals(SystemConstants.AUDIT_LOG_SERVER_RMI_IMPL)) {
            auditLogImplementationType = AuditLogImplementationType.AuditLogServerRMIImplementation;
        } else {
            auditLogImplementationType = AuditLogImplementationType.AuditLogServerTCPImplementation;
        }

        try {
            LOG.debug("ChatServer soll mit AuditLog gestartet werden");
            chatServer = ServerFactory.getServerWithAuditLog(serverImpl, serverPort, sendBufferSize,
                    receiveBufferSize, this,
                    auditLogImplementationType, auditLogServerHostname, auditLogServerPort);
        } catch (Exception e) {
            LOG.error("Fehler beim Starten des Chat-Servers: {}", e.getMessage());
            ExceptionHandler.logException(e);
            throw new Exception(e);
        }

        if (!startable) {
            setAlert("Bitte Korrigieren sie die rot markierten Felder");
        } else {
            if (!ServerFactory.isAuditLogServerConnected()) {
                // AuditLog-Server Verbindung nicht vorhanden, in der GUI zeigen
                enableAuditLogServerCheckbox.setSelected(false);
                auditLogServerHostnameOrIp.setEditable(false);
                auditLogServerHostnameOrIp.setStyle("-fx-background-color: gray;");
                comboBoxAuditLogServerType.setEditable(false);
                comboBoxAuditLogServerType.setStyle("-fx-background-color: gray;");
            }

            // Server starten
            chatServer.start();
        }
    }

    /**
     * Chat-Server starten
     * @param implType Implementierungstyp, der zu starten ist
     * @param serverPort Serverport, die der Server als Listener-Port nutzen soll
     * @param sendBufferSize Sendpuffergroesse, die der Server nutzen soll
     * @param receiveBufferSize Empfangspuffergroesse, die der Server nutzen soll
     */
    private void startChatServer(String implType, int serverPort, int sendBufferSize,
                                 int receiveBufferSize) throws Exception {

        ChatServerImplementationType serverImpl;
        if (implType.equals(SystemConstants.IMPL_TCP_ADVANCED)) {
            serverImpl = ChatServerImplementationType.TCPAdvancedImplementation;
        } else {
            serverImpl = ChatServerImplementationType.TCPSimpleImplementation;
        }

        try {
            chatServer = ServerFactory.getServer(serverImpl, serverPort, sendBufferSize,
                    receiveBufferSize, this);
        } catch (Exception e) {
            LOG.error("Fehler beim Starten des Chat-Servers: " + e.getMessage());
            ExceptionHandler.logException(e);
            throw new Exception(e);
        }
        if (!startable) {
            setAlert("Bitte Korrigieren sie die rot markierten Felder");
        } else {
            // Server starten
            chatServer.start();
        }
    }

    /**
     * Lesen des Hostnamens oder der Serveradresse aus der GUI als String
     * @return Hostname oder IP-Adresse als String
     */
    private String readAuditLogServerHostnameOrIp() {
        return auditLogServerHostnameOrIp.getText();
    }

    /**
     * Lesen des Ports des AuditLog-Servers aus der GUI
     * @return Port
     */
    private int readAuditLogServerPort(String auditLogServerImplType) {
        String item = auditLogServerPort.getText();
        int iServerPort = 0;
        if (item.matches("[0-9]+")) {
            iServerPort = Integer.parseInt(auditLogServerPort.getText());
            if ((iServerPort < 1) || (iServerPort > 65535)) {
                startable = false;
                auditLogServerPortLabel.setTextFill(Color.web(SystemConstants.RED_COLOR));
            } else if (auditLogServerImplType.equals(SystemConstants.AUDIT_LOG_SERVER_RMI_IMPL)) {
                // Falls RMI ausgewaehlt wurde, wird standardmaessig der RMI-Registry-Port 1099 verwendet
                iServerPort = Integer.parseInt(SystemConstants.DEFAULT_AUDIT_LOG_SERVER_RMI_REGISTRY_PORT);
                LOG.debug("Standard-Port fuer RMI-Registry: {}", iServerPort);
            } else
                LOG.debug("Port fuer AuditLog-Server: {}", iServerPort);
            auditLogServerPortLabel.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
        } else {
            startable = false;
            auditLogServerPortLabel.setTextFill(Color.web(SystemConstants.RED_COLOR));

        }
        return (iServerPort);
    }

    /**
     * Serverport aus GUI auslesen und pruefen
     * @return Verwendeter Serverport
     */
    private int readServerPort() {
        String item = serverPort.getText();
        int iServerPort = 0;
        if (item.matches("[0-9]+")) {
            iServerPort = Integer.parseInt(serverPort.getText());
            if ((iServerPort < 1) || (iServerPort > 65535)) {
                startable = false;
                serverPortLabel.setTextFill(Color.web(SystemConstants.RED_COLOR));
            } else {
                System.out.println("Serverport: " + iServerPort);
                serverPortLabel.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
            }
        } else {
            startable = false;
            serverPortLabel.setTextFill(Color.web(SystemConstants.RED_COLOR));

        }
        return (iServerPort);
    }

    /**
     * Groesse des Sendepuffers in Byte auslesen und pruefen
     * @return Eingegebene Sendpuffer-Groesse
     */
    private int readSendBufferSize() {

        String item = sendBufferSize.getText();
        int iSendBufferSize = 0;
        if (item.matches("[0-9]+")) {
            iSendBufferSize = Integer.parseInt(sendBufferSize.getText());
            if ((iSendBufferSize <= 0)
                    || (iSendBufferSize > Integer.parseInt(SystemConstants.MAX_SENDBUFFER_SIZE))) {
                startable = false;
                sendBufferSizeLabel.setTextFill(Color.web(SystemConstants.RED_COLOR));
            } else {
                sendBufferSizeLabel.setTextFill(Color.web(SystemConstants.BLACK_COLOR));

            }
        } else {
            startable = false;
            sendBufferSizeLabel.setTextFill(Color.web(SystemConstants.RED_COLOR));
        }
        return (iSendBufferSize);
    }

    /**
     * Groesse des Empfangspuffers in Byte auslesen und pruefen
     * @return Eingegebene Empfangspuffer-Groesse
     */
    private int readReceiveBufferSize() {

        String item = receiveBufferSize.getText();
        LOG.debug("Empfangspuffergroesse: {}",receiveBufferSize);
        int iReceiveBufferSize = 0;
        if (item.matches("[0-9]+")) {
            iReceiveBufferSize = Integer.parseInt(receiveBufferSize.getText());
            if ((iReceiveBufferSize <= 0)
                    || (iReceiveBufferSize > Integer.parseInt(SystemConstants.MAX_RECEIVEBUFFER_SIZE))) {
                startable = false;
                receiveBufferSizeLabel.setTextFill(Color.web(SystemConstants.RED_COLOR));
            } else {
                receiveBufferSizeLabel.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
            }
        } else {
            startable = false;
            receiveBufferSizeLabel.setTextFill(Color.web(SystemConstants.RED_COLOR));
        }
        return (iReceiveBufferSize);
    }

    private String getCurrentTime(Calendar cal) {
        return new SimpleDateFormat("dd.MM.yy HH:mm:ss:SSS").format(cal.getTime());
    }

    /**
     * GUI-Feld fuer eingeloggte CLients ueber Event-Liste des JavaFX-GUI-Threads aktualisieren
     */
    private void updateLoggedInClients() {

        Platform.runLater(() -> {
            LOG.debug("runLater: run-Methode wird ausgefuehrt");
            LOG.debug("runLater: Logged in Clients: {}", loggedInClientCounter.get());
            loggedInClients.setText(String.valueOf(loggedInClientCounter.get()));
        });
    }

    /**
     * GUI-Feld fuer Anzahl empfangener Requests ueber Event-Liste des JavaFX-GUI-Threads aktualisieren
     */
    private void updateNumberOfRequests() {

        Platform.runLater(() -> {

            LOG.debug("runLater: run-Methode wird ausgefuehrt");
            LOG.debug("runLater: Received Requests: " + requestCounter.get());
            receivedRequests.setText(String.valueOf(requestCounter.get()));
        });
    }

    /**
     * Oeffnen eines Dialogfensters, wenn ein Fehler bei der Eingabe auftritt
     * @param message Meldung fuer Bildschirmanzeige
     */
    private void setAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Fehler!");
        alert.setHeaderText(
                "Bei den von ihnen eingegebenen Parametern ist ein Fehler aufgetreten:");
        alert.setContentText(message);
        Platform.runLater(alert::showAndWait);
    }

    @Override
    public void showStartData(ServerStartData data) {
        startTimeField.setText(startTimeAsString);
    }

    @Override
    public void incrNumberOfLoggedInClients() {

        loggedInClientCounter.getAndIncrement();
        LOG.debug("Eingeloggte Clients: " + loggedInClientCounter.get());
        updateLoggedInClients();
    }

    @Override
    public void decrNumberOfLoggedInClients() {
        loggedInClientCounter.getAndDecrement();
        LOG.debug("Eingeloggte Clients: " + loggedInClientCounter.get());
        updateLoggedInClients();
    }

    @Override
    public void incrNumberOfRequests() {
        requestCounter.getAndIncrement();
        LOG.debug(requestCounter.get() + " empfangene Message Requests");
        updateNumberOfRequests();
    }
}
