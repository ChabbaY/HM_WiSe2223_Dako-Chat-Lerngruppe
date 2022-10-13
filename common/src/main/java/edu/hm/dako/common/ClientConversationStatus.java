package edu.hm.dako.common;

/**
 * @author Peter Mandl, edited by Lerngruppe
 */
public enum ClientConversationStatus {
    // Client nicht eingeloggt
    UNREGISTERED,
    // Client-Login in Arbeit
    REGISTERING,
    // Client eingeloggt
    REGISTERED,
    // Client-Logout in Arbeit
    UNREGISTERING
}