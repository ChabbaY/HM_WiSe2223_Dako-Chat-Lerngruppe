package edu.hm.dako.connection;

import java.io.Serial;

/**
 * End of File bei Verbindung, verursacht durch einen Verbindungsabbau des Partners
 * @author Peter Mnadl
 */
public class EndOfFileException extends Exception {

    @Serial
    private static final long serialVersionUID = 2L;

    public EndOfFileException() {
        super("End of File Exception");
    }
}
