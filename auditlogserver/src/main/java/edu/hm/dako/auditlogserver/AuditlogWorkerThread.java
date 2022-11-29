package edu.hm.dako.auditlogserver;

import edu.hm.dako.auditlogserver.persistence.FileStorage;
import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.common.ChatPDU;
import edu.hm.dako.connection.Connection;

import javax.swing.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AuditlogWorkerThread extends Thread {

    Connection con;

    FileStorage speicher;

    public AuditlogWorkerThread(Connection conn) {
        con = conn;
        String dateString = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                .format(Calendar.getInstance().getTime());
        String hash = ((Integer) conn.hashCode()).toString();
        String fileName = dateString + hash;
        speicher = new FileStorage(fileName);

    }

    protected void handleIncomingRequest(AuditLogPDU receivedPdu) {
        speicher.save(receivedPdu);

    }

    ;

}
