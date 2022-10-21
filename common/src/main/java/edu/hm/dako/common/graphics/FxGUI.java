package edu.hm.dako.common.graphics;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * super class for all used javafx GUIs
 *
 * @author Linus Englert
 */
public class FxGUI extends Application{
    /**
     * background styling
     */
    protected final static String DEFAULT_BACKGROUND =
            "-fx-background-color: linear-gradient(from 0% 100% to 100% 0%, #16a34a, #60a5fa)";

    /**
     * default label size
     */
    protected final static int DEFAULT_LABEL_FONT_SIZE = 13;

    /**
     * default label width
     */
    protected final static int DEFAULT_LABEL_WIDTH = 200;

    /**
     * default label height
     */
    protected final static int DEFAULT_LABEL_HEIGHT = 25;

    /**
     * title
     */
    protected final String TITLE;

    /**
     * window size
     */
    protected final int WIDTH, HEIGHT;

    /**
     * Layout
     */
    protected BorderPane layout;

    /**
     * Bildschirmauflösung
     */
    protected final Dimension dim;

    /**
     * display panel
     */
    protected final VBox pane = new VBox(5);

    /**
     * new GUI
     *
     * @param title title
     * @param width width
     * @param height height
     */
    public FxGUI(String title, int width, int height) {
        this.TITLE = title;
        this.WIDTH = width;
        this.HEIGHT = height;
        dim = Toolkit.getDefaultToolkit().getScreenSize();
    }

    @Override
    public void start(final Stage stage) throws IllegalArgumentException {
        if (stage == null) throw new IllegalArgumentException("null not allowed here");

        stage.setTitle(TITLE);

        // BorderPane für Layout anpassen
        layout = new BorderPane(pane);
        BorderPane.setAlignment(pane, Pos.CENTER);
        layout.setStyle(DEFAULT_BACKGROUND);

        stage.setScene(new Scene(layout, WIDTH, HEIGHT));
        stage.show();
    }

    //----Components----------------------------------------------

    /**
     * Erstellung von ComboBoxen
     *
     * @param options ComboBox-Optionen
     * @return comboBox Referenz auf erzeugte ComboBox
     */
    protected ComboBox<String> createComboBox(ObservableList<String> options) {
        ComboBox<String> comboBox = new ComboBox<>(options);
        comboBox.setMinSize(155, 28);
        comboBox.setMaxSize(155, 28);
        comboBox.setValue(options.get(0));
        comboBox.setStyle("-fx-background-color: white; -fx-border-color: lightgrey;" +
                "-fx-border-radius: 5px, 5px, 5px, 5px;");

        return comboBox;
    }

    /**
     * Erstellung editierbarer Textfelder
     *
     * @param value Inhalt
     * @return textField Referenz auf erzeugtes Textfeld
     */
    protected TextField createEditableTextField(String value) {
        final TextField textField = new TextField(value);
        textField.setMaxSize(155, 28);
        textField.setMinSize(155, 28);
        textField.setEditable(true);
        textField.setStyle("-fx-background-color: white; -fx-border-color: lightgrey;" +
                "-fx-border-radius: 5px, 5px, 5px, 5px");

        return textField;
    }

    /**
     * Erstellung editierbarer Textfelder
     *
     * @param pane Zu bearbeitende Pane (Fensterteil)
     * @param columnIndex Spaltenindex
     * @param rowIndex Zeilenindex
     * @param value Inhalt
     * @return textField Referenz auf erzeugtes Textfeld
     */
    protected TextField createEditableTextField(GridPane pane, int columnIndex, int rowIndex, String value) {
        final TextField textField = createEditableTextField(value);

        if (pane == null) return textField;
        pane.add(textField, columnIndex, rowIndex);

        return textField;
    }

    /**
     * Erstellung eines Headers, mit Defaultwerten
     *
     * @param value Wert für den Header
     * @return header
     */
    protected HBox createHeader(String value) {
        return createHeader(value, 18);
    }
    /**
     * Erstellung eines Headers
     *
     * @param value Wert für den Header
     * @param fontSize Schriftgröße
     * @return header
     */
    protected HBox createHeader(String value, int fontSize) {
        final HBox header = new HBox();
        final Label label = new Label(value);
        label.setFont(Font.font(fontSize));
        label.setStyle("-fx-font-weight: bold");

        header.getChildren().add(label);
        return header;
    }

    /**
     * Label erstellen, mit default Werten
     *
     * @param value Wert des Labels
     * @return label Referenz auf erzeugtes Label
     */
    protected Label createLabel(String value) {
        return createLabel(value, DEFAULT_LABEL_WIDTH, DEFAULT_LABEL_HEIGHT, DEFAULT_LABEL_FONT_SIZE);
    }
    /**
     * Label erstellen
     *
     * @param value Wert des Labels
     * @param width Breite
     * @param height Höhe
     * @param fontSize Schriftgröße
     * @return label Referenz auf erzeugtes Label
     */
    protected Label createLabel(String value, int width, int height, int fontSize) {
        Label label = new Label(value);

        label.setMinSize(width, height);
        label.setMaxSize(width, height);
        label.setFont(Font.font(fontSize));

        return label;
    }

    /**
     * Erstellung nicht editierbarer Textfelder
     *
     * @return textField Referenz auf erzeugtes Textfeld
     */
    protected TextField createNotEditableTextField() {
        final TextField textField = new TextField();
        textField.setMaxSize(155, 28);
        textField.setMinSize(155, 28);
        textField.setEditable(false);
        textField.setStyle("-fx-background-color: white; -fx-border-color: lightgrey;" +
                "-fx-border-radius: 5px, 5px, 5px, 5px");

        return textField;
    }

    /**
     * Erstellung nicht editierbarer Textfelder
     *
     * @param pane Zu bearbeitende Pane (Fensterteil)
     * @param columnIndex Spaltenindex
     * @param rowIndex Zeilenindex
     * @return textField Referenz auf erzeugtes Textfeld
     */
    protected TextField createNotEditableTextField(GridPane pane, int columnIndex, int rowIndex) {
        final TextField textField = createNotEditableTextField();

        if (pane == null) return textField;
        pane.add(textField, columnIndex, rowIndex);

        return textField;
    }

    /**
     * Erstellung einer Progressbar
     *
     * @return Progressbar
     */
    protected ProgressBar createProgressbar() {
        final ProgressBar progressBar = new ProgressBar(0);

        progressBar.setStyle(
                "-fx-background-color: white; -fx-border-color: lightgrey; -fx-border-radius: 50px, 50px, 50px, 50px;" +
                        "-fx-accent: green;");
        return progressBar;
    }

    //----Alerts-------------------------------------------------------

    /**
     * Öffnen eines Dialogfensters, wenn ein Fehler bei der Eingabe auftritt
     *
     * @param message Meldung für Dialogfenster
     */
    protected void setAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Fehler!");
        alert.setHeaderText("Bei den von ihnen eingegebenen Parametern ist ein Fehler aufgetreten:");
        alert.setContentText(message);
        alert.setResizable(true);
        Platform.runLater(alert::showAndWait);
    }

    //----EXIT-----------------------------------------------------------

    /**
     * use this instead of System.exit(0)
     */
    protected void exit() {
        Platform.exit();
    }
}