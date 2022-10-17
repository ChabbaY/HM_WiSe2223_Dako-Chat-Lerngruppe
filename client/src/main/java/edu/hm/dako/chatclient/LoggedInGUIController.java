package edu.hm.dako.chatclient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Controller für Chat-GUI
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class LoggedInGUIController {
    private static final Logger LOG = LogManager.getLogger(LoggedInGUIController.class);

    @FXML
    private Button btnSubmit;
    @FXML
    private TextField txtChatMessage;
    @FXML
    private ListView<String> usersList;
    @FXML
    private ListView<String> chatList;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ScrollPane chatPane;

    private ClientFxGUI appController;

    /**
     * submit on enter pressed
     *
     * @param event KeyEvent
     */
    @FXML
    public void handleEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            btnSubmit_OnAction();
        }
    }

    /**
     * Konstruktor
     */
    public LoggedInGUIController() {
    }

    /**
     * Controller initialisieren
     *
     * @param appController Controller
     */
    public void setAppController(ClientFxGUI appController) {
        this.appController = appController;

        usersList.maxWidthProperty().bind(scrollPane.widthProperty().subtract(2));
        usersList.minWidthProperty().bind(scrollPane.widthProperty().subtract(2));
        usersList.maxHeightProperty().bind(scrollPane.heightProperty().subtract(2));
        usersList.minHeightProperty().bind(scrollPane.heightProperty().subtract(2));

        usersList.setItems(appController.getModel().users);
        usersList.scrollTo(appController.getModel().users.size());

        chatList.maxWidthProperty().bind(chatPane.widthProperty().subtract(2));
        chatList.minWidthProperty().bind(chatPane.widthProperty().subtract(2));
        chatList.maxHeightProperty().bind(chatPane.heightProperty().subtract(2));
        chatList.minHeightProperty().bind(chatPane.heightProperty().subtract(2));

        chatList.setItems(appController.getModel().chats);
        btnSubmit.disableProperty().bind(appController.getModel().block);
    }

    /**
     * Aktion beim Betätigen des Logout-Buttons
     */
    public void btnLogOut_OnAction() {
        try {
            appController.getCommunicator().logout(appController.getModel().getUserName());
        } catch (IOException e) {
            LOG.error("Logout konnte nicht durchgeführt werden, Server aktiv?");
            appController.setErrorMessage("Chat-Client",
                    "Abmelden beim Server nicht erfolgreich, da der Server vermutlich nicht aktiv ist." +
                            "Sie werden abgemeldet...",
                    5);
            appController.switchToLogInGui();
        }

        // Bei Abschluss des Logout-Vorgangs wird dies über die Callback-Methode
        // logoutComplete an die GUI gemeldet. Dort wird dann das Programm beendet
    }

    /**
     * Aktion beim Betätigen des Submit-Buttons
     */
    public void btnSubmit_OnAction() {
        try {
            // Eingegebene Chat-Nachricht an Server senden
            appController.getCommunicator().tell(appController.getModel().getUserName(), txtChatMessage.getText());
            Platform.runLater(() -> {
                txtChatMessage.setText("");
                chatList.scrollTo(appController.getModel().chats.size() - 1);
            });
        } catch (IOException e) {
            // Senden funktioniert nicht, Server vermutlich nicht aktiv
            LOG.error("Senden konnte nicht durchgeführt werden, Server aktiv?");
            appController.setErrorMessage("Chat-Client",
                    "Die Nachricht konnte nicht gesendet werden, da der Server unter Umständen" +
                            "nicht mehr läuft. Sie werden abgemeldet...",
                    6);
            appController.switchToLogInGui();
            Platform.runLater(() -> txtChatMessage.setText(""));
        }
    }
}