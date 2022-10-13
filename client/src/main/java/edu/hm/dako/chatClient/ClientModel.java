package edu.hm.dako.chatClient;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Modelldaten für FX-GUI
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class ClientModel {
    private final StringProperty userName = new SimpleStringProperty();
    public final ObservableList<String> users = FXCollections.observableArrayList();
    public final ObservableList<String> chats = FXCollections.observableArrayList();
    public final BooleanProperty block = new SimpleBooleanProperty();

    public StringProperty userNameProperty() {
        return userName;
    }

    public String getUserName() {
        return userName.get();
    }

    public void setUserName(String name) {
        userName.set(name);
    }
}