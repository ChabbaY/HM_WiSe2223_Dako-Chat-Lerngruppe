package edu.hm.dako.chatclient;

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

    /**
     * users
     */
    public ObservableList<String> users = FXCollections.observableArrayList();

    /**
     * chats
     */
    public ObservableList<String> chats = FXCollections.observableArrayList();

    /**
     * block
     */
    public BooleanProperty block = new SimpleBooleanProperty();

    /**
     * Konstruktor
     */
    public ClientModel() {
    }

    /**
     * getter
     *
     * @return username property
     */
    public StringProperty userNameProperty() {
        return userName;
    }

    /**
     * getter
     *
     * @return username
     */
    public String getUserName() {
        return userName.get();
    }

    /**
     * setter
     *
     * @param name username
     */
    public void setUserName(String name) {
        userName.set(name);
    }
}