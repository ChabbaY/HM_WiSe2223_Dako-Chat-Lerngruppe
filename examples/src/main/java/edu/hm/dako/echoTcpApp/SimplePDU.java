package edu.hm.dako.echoTcpApp;

import java.io.Serial;
import java.io.Serializable;

/**
 * Nachrichtenaufbau für ein Simple-Protokoll
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class SimplePDU implements Serializable {
    @Serial
    private static final long serialVersionUID = -6172619032079227589L;

    // Name des Client-Threads, der den Request absendet
    private String clientThreadName;

    // Name des Threads, der den Request im Server
    private String serverThreadName;

    // Nutzdaten (eigentliche Chat-Nachricht in Textform)
    private String message;

    public SimplePDU(String message) {
        this.message = message;
        clientThreadName = null;
        serverThreadName = null;
    }

    public static void printPdu(SimplePDU pdu) {
        System.out.println(pdu);
    }

    public String toString() {
        return "\n"
                + "SimplePdu ****************************************************************************************************"
                + "\n" + "clientThreadName: " + this.clientThreadName + ", " + "\n"
                + "serverThreadName: " + this.serverThreadName + ", " + "\n" + "message: "
                + this.message + "\n"
                + "**************************************************************************************************** SimplePdu"
                + "\n";
    }

    public void setClientThreadName(String threadName) {
        this.clientThreadName = threadName;
    }

    public void setServerThreadName(String threadName) {
        this.serverThreadName = threadName;
    }

    public String getMessage() {
        return (message);
    }

    public void setMessage(String msg) {
        this.message = msg;
    }
}