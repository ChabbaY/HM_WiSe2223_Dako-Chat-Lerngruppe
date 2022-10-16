package edu.hm.dako.common;

/**
 * Definiert systemweit gültige Konstanten
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class SystemConstants {
    /**
     * Bezeichnung einer Server-Implementierung
     */
    public static final String IMPL_TCP_ADVANCED = "TCPAdvanced";

    /**
     * Bezeichnung einer Server-Implementierung
     */
    public static final String IMPL_UDP_ADVANCED = "UDPAdvanced";

    /**
     * Bezeichnung einer Server-Implementierung
     */
    public static final String IMPL_TCP_SIMPLE = "TCPSimple";

    /**
     * Bezeichnung einer Audit-Log-Server-Implementierung
     */
    public static final String AUDIT_LOG_SERVER_TCP_IMPL = "TCP";

    /**
     * Bezeichnung einer Audit-Log-Server-Implementierung
     */
    public static final String AUDIT_LOG_SERVER_UDP_IMPL = "UDP";

    /**
     * Bezeichnung einer Audit-Log-Server-Implementierung
     */
    public static final String AUDIT_LOG_SERVER_RMI_IMPL = "RMI";

    /**
     * Standard- und Maximal-Puffergrößen in Byte für Audit-Log-Verbindung
     */
    public static final String DEFAULT_SEND_BUFFER_SIZE = "300000";

    /**
     * Standard- und Maximal-Puffergrößen in Byte für Audit-Log-Verbindung
     */
    public static final String DEFAULT_RECEIVE_BUFFER_SIZE = "300000";

    /**
     * Standard- und Maximal-Puffergrößen in Byte für Audit-Log-Verbindung
     */
    public static final String MAX_SEND_BUFFER_SIZE = "500000";

    /**
     * Standard- und Maximal-Puffergrößen in Byte für Audit-Log-Verbindung
     */
    public static final String MAX_RECEIVE_BUFFER_SIZE = "500000";

    /**
     * StandardValue for AuditLogServer
     */
    public static final String DEFAULT_AUDIT_LOG_SERVER_NAME = "localhost";

    /**
     * StandardValue for AuditLogServer
     */
    public static final String DEFAULT_AUDIT_LOG_SERVER_PORT = "40001";

    /**
     * StandardValue for AuditLogServer
     */
    public static final String DEFAULT_AUDIT_LOG_SERVER_RMI_REGISTRY_PORT = "1099";

    /**
     * Farben für GUI: Rot für die Darstellung falsch eingegebener Werte
     */
    public static final String RED_COLOR = "#FF0000";

    /**
     * ... und Schwarz für normale Eingaben
     */
    public static final String BLACK_COLOR = "#000000";

    /**
     * Konstruktor
     */
    public SystemConstants() {
    }
}