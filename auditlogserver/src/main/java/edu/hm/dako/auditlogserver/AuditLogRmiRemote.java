package edu.hm.dako.auditlogserver;

import edu.hm.dako.auditlogserver.persistence.FileStorage;
import edu.hm.dako.auditlogserver.persistence.StorageInterface;
import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.common.AuditLogRMIInterface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuditLogRmiRemote implements AuditLogRMIInterface {
    private static final Logger LOG = LogManager.getLogger(AuditLogRmiRemote.class);

    private StorageInterface storage;

    public AuditLogRmiRemote() {
        String dateString = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                .format(Calendar.getInstance().getTime());
        storage = new FileStorage(dateString);
    }

    public void audit(AuditLogPDU pdu) {
        storage.save(pdu);
        LOG.debug("AuditLogPDU received!");
    }
}
