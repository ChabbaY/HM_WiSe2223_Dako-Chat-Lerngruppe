package edu.hm.dako.chatclient.gui;

import edu.hm.dako.chatclient.ClientImpl;
import edu.hm.dako.chatclient.ClientModel;
import edu.hm.dako.chatclient.ClientUserInterface;
import edu.hm.dako.common.ExceptionHandler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Chat-GUI: Oberfläche für Chat-Nutzer
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class ClientFxGUI extends Application implements ClientUserInterface {
    /**
     * referencing the logger
     */
    private static final Logger LOG = LogManager.getLogger(ClientFxGUI.class);
    private final ClientModel model = new ClientModel();
    private Stage stage;
    private ClientImpl communicator;

    /**
     * saving args for further processing
     */
    static String[] args;

    /**
     * Chat-GUI: Oberfläche für Chat-Nutzer
     *
     * @param args currently ignored
     */
    public static void main(String[] args) {
        // Log4j2-Logging aus Datei konfigurieren
        LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
        File file = new File("config/log4j/log4j2.chatClient.xml");
        context.setConfigLocation(file.toURI());

        ClientFxGUI.args = args;

        launch(args);
    }

    /**
     * Konstruktor
     */
    public ClientFxGUI() {
    }

    /**
     * Kommunikationsschnittstelle zur Kommunikation mit dem Chat-Server aktivieren
     *
     * @param serverType serverType Servertyp
     * @param port       Serverport
     * @param host       Hostname oder IP-Adresse des Servers
     */
    public void createCommunicator(String serverType, int port, String host) {
        communicator = new ClientImpl(this, port, host, serverType);
    }

    /**
     * getter
     *
     * @return communicator
     */
    public ClientImpl getCommunicator() {
        return communicator;
    }

    /**
     * getter
     *
     * @return model
     */
    public ClientModel getModel() {
        return model;
    }

    /**
     * Diese Methode wird von Java FX bei Aufruf der launch-Methode implizit aufgerufen
     */
    @Override
    public void start(Stage stage) throws Exception {
        URL resource = getClass().getResource("LogInGUI.fxml");
        FXMLLoader loader;
        if (resource != null) {
            LOG.error("FXML-Datei gelesen: {}", resource);
            loader = new FXMLLoader(resource);
        } else {
            LOG.error("FXML-Datei nicht vorhanden");
            throw new Exception();
        }
        Parent root = loader.load();
        LogInGUIController lc = loader.getController();
        lc.setAppController(this);
        stage.setTitle("Anmelden");
        stage.setScene(new Scene(root, 280, 320));
        root.setStyle("-fx-background-color: linear-gradient(from 0% 100% to 100% 0%, #16a34a, #60a5fa)");
        this.stage = stage;
        stage.show();
    }

    /**
     * Benutzeroberfläche für Chat erzeugen
     */
    public void createNextGui() {
        URL resource = getClass().getResource("LoggedInGUI.fxml");
        FXMLLoader loader = null;
        if (resource != null) {
            LOG.error("FXML-Datei gelesen: {}", resource);
            loader = new FXMLLoader(resource);
        } else {
            LOG.error("FXML-Datei nicht vorhanden");
        }
        try {
            assert loader != null;
            Parent root = loader.load();
            LoggedInGUIController lc1 = loader.getController();
            lc1.setAppController(this);

            Platform.runLater(() -> {
                stage.setTitle("Angemeldet");
                stage.setScene(new Scene(root, 800, 500));
                root.setStyle("-fx-background-color: linear-gradient(from 0% 100% to 100% 0%, #16a34a, #60a5fa)");
            });
        } catch (Exception e) {
            ExceptionHandler.logException(e);
        }
        stage.setOnCloseRequest(event -> {
            try {
                getCommunicator().logout(getModel().getUserName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * close old and open new GUI
     */
    public void switchToLogInGui() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LogInGUI.fxml"));
            Parent root = loader.load();
            LogInGUIController lc = loader.getController();
            lc.setAppController(this);
            Platform.runLater(() -> {
                stage.setTitle("Anmelden");
                stage.setScene(new Scene(root, 280, 320));
                root.setStyle("-fx-background-color: linear-gradient(from 0% 100% to 100% 0%, #16a34a, #60a5fa)");
            });
        } catch (Exception e) {
            ExceptionHandler.logException(e);
        }

    }

    @Override
    public void setUserList(Vector<String> userList) {
        final List<String> users = new ArrayList<>();
        for (String anUserList : userList) {
            if (anUserList.equals(getModel().getUserName())) {
                users.add("*" + anUserList + "*");
            } else {
                users.add(anUserList);
            }
            Platform.runLater(() -> {
                getModel().users.setAll(users);
                LOG.debug(users);
            });
        }
    }

    @Override
    public void setMessageLine(String sender, String message) {
        String messageText;
        if (sender.equals(getModel().getUserName())) {
            messageText = "*" + sender + "*: " + message;
        } else {
            messageText = sender + ": " + message;
        }
        Platform.runLater(() -> getModel().chats.add(messageText));
    }

    @Override
    public boolean getLock() {
        return false;
    }

    @Override
    public void setLock(boolean lock) {
        getModel().block.set(lock);
    }

    @Override
    public boolean isTestAborted() {
        return false;
    }

    @Override
    public void abortTest() {
    }

    @Override
    public void releaseTest() {
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public long getLastServerTime() {
        return 0;
    }

    @Override
    public void setLastServerTime(long lastServerTime) {
    }

    @Override
    public void setSessionStatisticsCounter(long numberOfSentEvents, long numberOfReceivedConfirms,
                                            long numberOfLostConfirms, long numberOfRetries,
                                            long numberOfReceivedChatMessages) {
    }

    @Override
    public long getNumberOfSentEvents() {
        return 0;
    }

    @Override
    public long getNumberOfReceivedConfirms() {
        return 0;
    }

    @Override
    public long getNumberOfLostConfirms() {
        return 0;
    }

    @Override
    public long getNumberOfRetries() {
        return 0;
    }

    @Override
    public long getNumberOfReceivedChatMessages() {
        return 0;
    }

    @Override
    public void setErrorMessage(String sender, String errorMessage, long errorCode) {
        LOG.debug("errorMessage: {}", errorMessage);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Es ist ein Fehler im " + sender + " aufgetreten");
            alert.setHeaderText("Fehlerbehandlung (Fehlercode = " + errorCode + ")");
            alert.setContentText(errorMessage);
            alert.setResizable(true);
            alert.showAndWait();
        });
    }

    @Override
    public void loginComplete() {
        LOG.debug("Login erfolgreich");
        createNextGui();
    }

    @Override
    public void logoutComplete() {
        LOG.debug("Abmeldung durchgeführt");
        LOG.debug("Logout-Vorgang ist nun beendet");
        Platform.exit();
    }
}