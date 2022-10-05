package edu.hm.dako.connection;

import java.io.Serial;

/**
 * Timeout-Exception bei Verbindung
 * @author Peter Mandl
 */
public class ConnectionTimeoutException extends Exception {

    @Serial
    private static final long serialVersionUID = 1L;

    public ConnectionTimeoutException() {
        super("Timeout bei Verbindung aufgetreten");
    }
}
