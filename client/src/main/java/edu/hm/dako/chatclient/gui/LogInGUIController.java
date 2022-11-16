package edu.hm.dako.chatclient.gui;

import edu.hm.dako.chatclient.ClientStarter;
import edu.hm.dako.common.SystemConstants;
import edu.hm.dako.common.Tupel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * Controller für Login-GUI
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class LogInGUIController implements Initializable {
    private static final Pattern IPV6_PATTERN = Pattern
            .compile("^(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$");
    private static final Pattern IPV4_PATTERN = Pattern
            .compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
    /**
     * referencing the logger
     */
    private static final Logger log = LogManager.getLogger(LogInGUIController.class);
    private String userName;
    @FXML
    private TextField txtUsername, txtServername, txtServerPort;
    @FXML
    private ComboBox<String> comboServerType;
    @FXML
    private Label lblIP, lblServerPort;
    private ClientFxGUI appController;

    /**
     * login on enter pressed
     *
     * @param event KeyEvent
     */
    @FXML
    public void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            performLogin();
        }
    }

    /**
     * Konstruktor
     */
    public LogInGUIController() {
    }

    /**
     * setter
     *
     * @param appController appController
     */
    public void setAppController(ClientFxGUI appController) {
        this.appController = appController;
    }

    /**
     * Login-Eingaben entgegennehmen, prüfen und Anmeldung beim Server durchführen
     */
    public void performLogin() {
        // Benutzernamen prüfen
        userName = txtUsername.getText();
        if (userName.isEmpty()) {
            log.debug("Benutzername ist leer");
            appController.setErrorMessage("Chat-Client", "Benutzername fehlt", 1);
            return;
        } else {
            appController.getModel().setUserName(userName);
        }

        // IP-Adresse prüfen
        if (!ipCorrect()) {
            appController.setErrorMessage("Chat-Client",
                    "IP-Adresse nicht korrekt, z.B. 127.0.0.1, 192.168.178.2 oder localhost!", 3);
            lblIP.setTextFill(Color.web(SystemConstants.RED_COLOR));
            return;
        }
        // IP-Adresse ist korrekt
        lblIP.setTextFill(Color.web(SystemConstants.BLACK_COLOR));

        // Serverport prüfen
        int serverPort;
        String valueServerPort = txtServerPort.getText();
        if (valueServerPort.matches("[0-9]+")) {
            serverPort = Integer.parseInt(valueServerPort);
            if ((serverPort < 1) || (serverPort > 65535)) {
                appController.setErrorMessage("Chat-Client",
                        "Serverport ist nicht im Wertebereich von 1 bis 65535!", 2);
                log.debug("Serverport nicht im Wertebereich");
                lblServerPort.setTextFill(Color.web(SystemConstants.RED_COLOR));
                return;
            } else {
                // Serverport ist korrekt
                lblServerPort.setTextFill(Color.web(SystemConstants.BLACK_COLOR));
            }
        } else {
            appController.setErrorMessage("Chat-Client", "Serverport ist nicht numerisch!", 3);
            lblServerPort.setTextFill(Color.web(SystemConstants.RED_COLOR));
            return;
        }

        // Verbindung herstellen und beim Server anmelden
        appController.createCommunicator(comboServerType.getValue(), serverPort, txtServername.getText());
        try {
            appController.getCommunicator().login(userName);
        } catch (Exception e2) {
            // Benutzer mit dem angegebenen Namen schon angemeldet
            log.error("Login konnte nicht zum Server gesendet werden, Server aktiv?");
            appController.setErrorMessage("Chat-Client",
                    "Login konnte nicht gesendet werden, vermutlich ist der Server nicht aktiv", 4);
            // Verbindung zum Server wird wieder abgebaut
            appController.getCommunicator().cancelConnection();
        }
    }

    /**
     * getter
     *
     * @return userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * System exit
     */
    public void exitButtonReaction() {
        System.exit(0);
    }

    /**
     * Prüfen, ob IP-Adresse korrekt ist
     *
     * @return true - korrekt, false - nicht korrekt
     */
    private Boolean ipCorrect() {
        String testString = txtServername.getText();
        if (testString.equals("localhost")) {
            return true;
        } else if (IPV6_PATTERN.matcher(testString).matches()) {
            return true;
        } else return IPV4_PATTERN.matcher(testString).matches();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboServerType.getItems().addAll(SystemConstants.IMPL_TCP_SIMPLE,
                SystemConstants.IMPL_TCP_ADVANCED);

        for(String s: ClientFxGUI.args) {
            String[] values = s.split("=");
            switch (values[0]) {
                case "--server" -> txtServername.setText(values[1]);
                case "--port" -> {
                    Tupel<Integer, Boolean> result = ClientStarter.validateServerPort(values[1]);
                    if (result.getY()) txtServerPort.setText(result.getX().toString());
                }
                case "--protocol" -> {
                    if ("tcpadvanced".equals(values[1])) {
                        comboServerType.setValue(SystemConstants.IMPL_TCP_ADVANCED);
                    }
                }
                case "--username" -> txtUsername.setText(values[1]);
            }
        }
    }
}