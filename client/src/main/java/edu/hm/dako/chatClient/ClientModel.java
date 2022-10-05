package edu.hm.dako.chatClient;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Modelldaten fuer FX-GUI
 * @author Paul Mandl
 */
public class ClientModel {

    private final StringProperty userName = new SimpleStringProperty();
    public ObservableList<String> users = FXCollections.observableArrayList();
    public ObservableList<String> chats = FXCollections.observableArrayList();
    public BooleanProperty block = new SimpleBooleanProperty();

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
