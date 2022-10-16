package edu.hm.dako.common;

/**
 * Enumeration zur Definition der Chat-PDU-Typen
 *
 * @author Peter Mandl, Mathias Strobl, edited by Lerngruppe
 */
public enum PDUType {
    /**
     * undefined
     */
    UNDEFINED(0, "Undefined"),

    /**
     * login request
     */
    LOGIN_REQUEST(1, "Login-Request"),

    /**
     * login response
     */
    LOGIN_RESPONSE(2, "Login-Response"),

    /**
     * logout request
     */
    LOGOUT_REQUEST(3, "Logout-Request"),

    /**
     * logout response
     */
    LOGOUT_RESPONSE(4, "Logout-Response"),

    /**
     * chat message request
     */
    CHAT_MESSAGE_REQUEST(5, "Chat-Message-Request"),

    /**
     * chat message response
     */
    CHAT_MESSAGE_RESPONSE(6, "Chat-Message-Response"),

    /**
     * chat message event
     */
    CHAT_MESSAGE_EVENT(7, "Chat-Message-Event"),

    /**
     * login event
     */
    LOGIN_EVENT(8, "Login-Event"),

    /**
     * logout event
     */
    LOGOUT_EVENT(9, "Logout-Event"),

    /**
     * chat message event confirm
     */
    CHAT_MESSAGE_EVENT_CONFIRM(10, "Chat-Message-Event-Confirm"),

    /**
     * login event confirm
     */
    LOGIN_EVENT_CONFIRM(11, "Login-Event-Confirm"),

    /**
     * logout event confirm
     */
    LOGOUT_EVENT_CONFIRM(12, "Logout-Event-Confirm");

    /**
     * identifier
     */
    private final int id;

    /**
     * description
     */
    private final String description;

    /**
     * Konstruktor
     *
     * @param id identifier
     * @param description description
     */
    PDUType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * getter
     *
     * @param id id of enum value
     * @return enum value
     */
    public static PDUType getId(int id) {
        for (PDUType e : values()) {
            if (e.getId() == id) {
                return e;
            }
        }
        return null;
    }

    /**
     * getter
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return description;
    }
}