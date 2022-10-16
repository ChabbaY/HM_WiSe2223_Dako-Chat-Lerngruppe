package edu.hm.dako.chatserver;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Globale Zähler für Logouts, gesendete Events und empfangene Confirms. Diese Zähler dienen nur zum Test
 * bzw. zur Kontrolle.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class SharedServerCounter {
    /**
     * Konstruktor
     */
    public SharedServerCounter() {
    }

    /**
     * global counter for logouts
     */
    public AtomicInteger logoutCounter;

    /**
     * global counter for events
     */
    public AtomicInteger eventCounter;

    /**
     * global counter for confirmations
     */
    public AtomicInteger confirmCounter;
}