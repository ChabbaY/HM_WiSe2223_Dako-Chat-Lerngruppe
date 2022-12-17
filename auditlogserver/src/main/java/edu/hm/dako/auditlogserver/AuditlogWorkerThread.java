package edu.hm.dako.auditlogserver;

import edu.hm.dako.auditlogserver.persistence.FileStorage;
import edu.hm.dako.auditlogserver.persistence.StorageInterface;
import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.connection.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/***
 * Pro verwalteten Client (ChatServer) wird ein WorkerThread erzeugt
 *
 * @author Oskar Gruß
 */
public class AuditlogWorkerThread extends Thread {
    private static final Logger LOG = LogManager.getLogger(AuditlogWorkerThread.class);
    private boolean finished = false;

    Connection con;

    StorageInterface speicher;

    /**
     * constructor
     *
     * @param conn server connection
     */
    public AuditlogWorkerThread(Connection conn) {
        con = conn;
        String dateString = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                .format(Calendar.getInstance().getTime());
        String hash = ((Integer)conn.hashCode()).toString();
        String fileName = dateString + hash;
        speicher = new FileStorage(fileName);
    }

    @Override
    public void run() {
        LOG.debug("AuditlogWorkerthread erzeugt, ThreadName: " + Thread.currentThread().getName());
        while (!finished && !Thread.currentThread().isInterrupted()) {
            try {
                // Warte auf nächste Nachricht des Clients und führe entsprechende Aktion aus
                handleIncomingMessage();
            } catch (Exception e) {
                LOG.error("Exception während der Nachrichtenverarbeitung");
                ExceptionHandler.logException(e);
            }
        }
        LOG.debug(Thread.currentThread().getName() + " beendet sich");
        closeConnection();
    }

    /**
     * handles incoming messages
     */
    private void handleIncomingMessage() {
        AuditLogPDU receivedPDU;

        try {
            LOG.debug("Vallah");

            //System.out.println(con.receive().toString());
            receivedPDU = (AuditLogPDU) con.receive();
            LOG.debug(receivedPDU.toString());

            handleIncomingRequest(receivedPDU);
        } catch (Exception ex){
            LOG.debug(ex.getMessage());
            //finished =true;
        }
    }

    /**
     * handles incoming requests
     *
     * @param receivedPdu received audit log pdu that should be logged
     */
    protected void handleIncomingRequest(AuditLogPDU receivedPdu) {
        speicher.audit(receivedPdu);
    }

    private void closeConnection() {
        LOG.debug("Schliessen der AuditLogConnection " );

        try {
            con.close();
        } catch (Exception e) {
            LOG.debug("Exception bei close");
            // ExceptionHandler.logException(e);
        }
    }
}
