package edu.hm.dako.common;

/**
 * Definiert systemweit gültige Konstanten
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class SystemConstants {
    // Bezeichnungen der verschiedenen Server-Implementierungen
    public static final String IMPL_TCP_ADVANCED = "TCPAdvanced";
    public static final String IMPL_UDP_ADVANCED = "UDPAdvanced";
    public static final String IMPL_TCP_SIMPLE = "TCPSimple";

    // Bezeichnungen für verschiedene Audit-Log-Server-Implementierungen
    public static final String AUDIT_LOG_SERVER_TCP_IMPL = "TCP";
    public static final String AUDIT_LOG_SERVER_UDP_IMPL = "UDP";
    public static final String AUDIT_LOG_SERVER_RMI_IMPL = "RMI";

    // Standard- und Maximal-Puffergrößen in Byte für Audit-Log-Verbindung
    public static final String DEFAULT_SEND_BUFFER_SIZE = "300000";
    public static final String DEFAULT_RECEIVE_BUFFER_SIZE = "300000";
    public static final String MAX_SEND_BUFFER_SIZE = "500000";
    public static final String MAX_RECEIVE_BUFFER_SIZE = "500000";

    // StandardValues for AuditLogServer
    public static final String DEFAULT_AUDIT_LOG_SERVER_NAME = "localhost";
    public static final String DEFAULT_AUDIT_LOG_SERVER_PORT = "40001";
    public static final String DEFAULT_AUDIT_LOG_SERVER_RMI_REGISTRY_PORT = "1099";

    // Farben für GUI:
    // Rot für die Darstellung falsch eingegebener Werte
    public static final String RED_COLOR = "#FF0000";
    // Schwarz für normale Eingaben
    public static final String BLACK_COLOR = "#000000";
}