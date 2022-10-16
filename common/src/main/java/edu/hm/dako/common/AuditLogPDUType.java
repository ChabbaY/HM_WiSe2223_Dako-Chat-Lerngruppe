package edu.hm.dako.common;

/**
 * Enumeration zur Definition der Audit-PDU-Typen
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public enum AuditLogPDUType {
    /**
     * undefined
     */
    UNDEFINED(0, "Undefined"),

    /**
     * finish
     */
    FINISH_AUDIT_REQUEST(4, "Finish"),

    /**
     * login
     */
    LOGIN_REQUEST(1, "Login "),

    /**
     * logout
     */
    LOGOUT_REQUEST(2, "Logout"),

    /**
     * chat message request
     */
    CHAT_MESSAGE_REQUEST(3, "Chat  ");

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
    AuditLogPDUType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    /**
     * getter
     *
     * @param id id of enum value
     * @return enum value
     */
    public static AuditLogPDUType getValue(int id) {
        for (AuditLogPDUType e : values()) {
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