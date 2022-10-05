package edu.hm.dako.chatServer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Globale Zaehler fuer Logouts, gesendete Events und empfangene Confirms. Diese Zaehler dienen nur zum Test bzw. zur Kontrolle.
 * @author Peter Mandl
 */

public class SharedServerCounter {
    public AtomicInteger logoutCounter;
    public AtomicInteger eventCounter;
    public AtomicInteger confirmCounter;
}
