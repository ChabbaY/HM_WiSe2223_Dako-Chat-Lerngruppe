package edu.hm.dako.echoTcpSocketApp;

import java.io.Serial;
import java.io.Serializable;

/**
 * Nachrichtenaufbau für ein Simple-Protokoll
 * @author Peter Mandl, edited by Lerngruppe
 */
public class SimplePDU implements Serializable {
    @Serial
    private static final long serialVersionUID = -6172619032079227589L;

    /**
     * Name des Client-Threads, der den Request absendet
     */
    private String clientThreadName;

    /**
     * Name des Threads, der den Request im Server
     */
    private String serverThreadName;

    /**
     * Nutzdaten (eigentliche Chat-Nachricht in Textform)
     */
    private String message;

    /**
     * Konstruktor
     *
     * @param message Nachricht
     */
    public SimplePDU(String message) {
        this.message = message;
        clientThreadName = null;
        serverThreadName = null;
    }

    @Override
    public String toString() {
        return "\n"
                + "SimplePdu ****************************************************************************************************"
                + "\n" + "clientThreadName: " + this.clientThreadName + ", " + "\n"
                + "serverThreadName: " + this.serverThreadName + ", " + "\n" + "message: "
                + this.message + "\n"
                + "**************************************************************************************************** SimplePdu"
                + "\n";
    }

    /**
     * setter
     *
     * @param threadName client thread name
     */
    public void setClientThreadName(String threadName) {
        this.clientThreadName = threadName;
    }

    /**
     * setter
     *
     * @param threadName server thread name
     */
    public void setServerThreadName(String threadName) {
        this.serverThreadName = threadName;
    }

    /**
     * getter
     *
     * @return message
     */
    public String getMessage() {
        return (message);
    }

    /**
     * setter
     *
     * @param message message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}