package edu.hm.dako.auditlogserver;

import edu.hm.dako.auditlogserver.persistence.FileStorage;
import edu.hm.dako.chatserver.SimpleChatWorkerThreadImpl;
import edu.hm.dako.common.AuditLogPDU;

import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.connection.Connection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/***
 * Pro verwalteten Client (ChatServer) wird ein WorkerThrad
 */
public class AuditlogWorkerThread extends Thread {


    private static final Logger LOG = LogManager.getLogger(SimpleChatWorkerThreadImpl.class);
    private boolean finished = false;

    Connection con;

    FileStorage speicher;

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
        LOG.debug("ChatWorker-Thread erzeugt, ThreadName: " + Thread.currentThread().getName());
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




    private void handleIncomingMessage(){

        AuditLogPDU receivedPDU;

        try {
            receivedPDU = (AuditLogPDU) con.receive();
            handleIncomingRequest(receivedPDU);

        }catch (Exception ex){
            finished =true;

        }

    }

    protected void handleIncomingRequest(AuditLogPDU receivedPdu) {
        speicher.save(receivedPdu);


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
