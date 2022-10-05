package edu.hm.dako.common;

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