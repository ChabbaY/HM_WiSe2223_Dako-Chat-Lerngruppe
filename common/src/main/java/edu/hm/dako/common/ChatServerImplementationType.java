package edu.hm.dako.common;

/**
 * Implementierungsvarianten des ChatServers mit verschiedenen Transportprotokollen
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public enum ChatServerImplementationType {
    /**
     * TCP Advanced Implementation
     */
    TCPAdvancedImplementation,

    /**
     * TCP Simple Implementation
     */
    TCPSimpleImplementation,

    /**
     * UDP Advanced Implementation
     */
    UDPAdvancedImplementation
}