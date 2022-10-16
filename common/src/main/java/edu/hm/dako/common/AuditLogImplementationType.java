package edu.hm.dako.common;

/**
 * Implementierungsvarianten des Lasttests mit verschiedenen Transportprotokollen
 *
 * @author Peter Mandl, edited by Lerngruppe
 */

public enum AuditLogImplementationType {
    /**
     * TCP Implementierung
     */
    AuditLogServerTCPImplementation,

    /**
     * UDP Implementierung
     */
    AuditLogServerUDPImplementation,

    /**
     * RMI Implementierung
     */
    AuditLogServerRMIImplementation
}