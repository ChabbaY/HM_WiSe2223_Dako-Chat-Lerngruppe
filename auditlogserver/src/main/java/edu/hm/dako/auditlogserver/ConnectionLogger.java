package edu.hm.dako.auditlogserver;

import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.connection.Connection;
import java.io.Serializable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Stattet ein {@link Connection} Objekt mit automatischem Logging aus. Umschliesst eine beliebige Connection-Instanz
 * und bietet dieselbe Schnittstelle an. Beim Aufruf einer Methode wird zunächst eine Log-Ausgabe getätigt und danach
 * die Methode der umschlossenen Connection aufgerufen. Anschliessend erfolgt eine weitere Log-Ausgabe.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class ConnectionLogger implements Connection {
    private static final Logger log = LogManager.getLogger(ConnectionLogger.class);

    private final Connection wrappedConnection;

    /**
     * Konstruktor
     *
     * @param wrappedConnection Connection für die geloggt werden soll
     */
    public ConnectionLogger(Connection wrappedConnection) {
        this.wrappedConnection = wrappedConnection;
    }

    @Override
    public synchronized void send(Serializable message) throws Exception {
        AuditLogPDU pdu = (AuditLogPDU) message;
        log.debug("Sende Nachricht, Chat-Inhalt: " + pdu.getMessage() + ", Chat-User: " + pdu.getUserName());
        wrappedConnection.send(message);
        log.trace(pdu);
        log.debug("Nachricht gesendet");
    }

    @Override
    public Serializable receive() throws Exception {
        log.debug("Empfange Nachricht...");
        AuditLogPDU pdu = (AuditLogPDU) wrappedConnection.receive();
        if (pdu != null) {
            log.debug("Nachricht empfangen, Chat-Inhalt: " + pdu.getMessage() + ", Chat-User: " + pdu.getUserName());
            log.trace(pdu);
        }
        return pdu;
    }

    @Override
    public Serializable receive(int timeout) throws Exception {
        log.debug("Empfange Nachricht...");
        AuditLogPDU pdu = (AuditLogPDU) wrappedConnection.receive(timeout);
        if (pdu != null) {
            log.debug("Nachricht empfangen, Chat-Inhalt: " + pdu.getMessage() + ", Chat-User: " + pdu.getUserName());
            log.trace(pdu);
        }
        return pdu;
    }

    @Override
    public void close() throws Exception {
        log.debug("Schliesse Connection...");
        wrappedConnection.close();
        log.debug("Connection geschlossen!");
    }
}