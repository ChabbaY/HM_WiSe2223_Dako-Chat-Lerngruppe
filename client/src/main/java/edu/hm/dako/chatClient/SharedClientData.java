package edu.hm.dako.chatClient;

import edu.hm.dako.common.ClientConversationStatus;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Gemeinsame genutzte Daten, die sich der Chat-Client-Thread und die Message-Processing-Threads teilen
 *
 * @author Peter Mandl
 */
public class SharedClientData {

    // Zaehler fuer die Envents aller Clients fuer
    // Testausgaben
    public static AtomicInteger logoutEvents = new AtomicInteger(0);
    public static AtomicInteger loginEvents = new AtomicInteger(0);
    public static AtomicInteger messageEvents = new AtomicInteger(0);

    // Loginname des Clients
    public String userName;

    // Aktueller Zustand des Clients
    public ClientConversationStatus status;

    // Zaehler fuer gesendete Chat-Nachrichten des Clients
    public AtomicInteger messageCounter;

    // Zaehler fuer Logouts, empfangene Events und Confirms fuer
    // Testausgaben
    public AtomicInteger logoutCounter;
    public AtomicInteger eventCounter;
    public AtomicInteger confirmCounter;
}
