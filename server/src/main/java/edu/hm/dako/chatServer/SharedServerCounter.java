package edu.hm.dako.chatServer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Globale Zähler für Logouts, gesendete Events und empfangene Confirms. Diese Zähler dienen nur zum Test bzw. zur Kontrolle.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */

public class SharedServerCounter {
    public AtomicInteger logoutCounter;
    public AtomicInteger eventCounter;
    public AtomicInteger confirmCounter;
}