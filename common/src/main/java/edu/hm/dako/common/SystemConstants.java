package edu.hm.dako.common;

/**
 * Definiert systemweit gueltige Konstanten
 */
public class SystemConstants {

    // Bezeichnungen der verschiedenen Server-Implementierungen
    public static final String IMPL_TCP_ADVANCED = "TCPAdvanced";
    public static final String IMPL_UDP_ADVANCED = "UDPAdvanced";
    public static final String IMPL_TCP_SIMPLE = "TCPSimple";

    // Bezeichnungen fuer verschiedene Audit-Log-Server-Implementierungen
    public static final String AUDIT_LOG_SERVER_TCP_IMPL = "TCP";
    public static final String AUDIT_LOG_SERVER_UDP_IMPL = "UDP";
    public static final String AUDIT_LOG_SERVER_RMI_IMPL = "RMI";


    // Standard- und Maximal-Puffergroessen in Byte fuer Audit-Log-Verbindung
    public static final String DEFAULT_SENDBUFFER_SIZE = "300000";
    public static final String DEFAULT_RECEIVEBUFFER_SIZE = "300000";
    public static final String MAX_SENDBUFFER_SIZE = "500000";
    public static final String MAX_RECEIVEBUFFER_SIZE = "500000";

    // Standardvalues for AuditLogServer
    public static final String DEFAULT_AUDIT_LOG_SERVER_NAME = "localhost";
    public static final String DEFAULT_AUDIT_LOG_SERVER_PORT = "40001";
    public static final String DEFAULT_AUDIT_LOG_SERVER_RMI_REGISTRY_PORT = "1099";

    // Farben fuer GUI:
    // Rot fuer die Darstellung falsch eingegebener Werte
    public static final String RED_COLOR = "#FF0000";
    // Schwarz fuer normale Eingaben
    public static final String BLACK_COLOR = "#000000";
}
