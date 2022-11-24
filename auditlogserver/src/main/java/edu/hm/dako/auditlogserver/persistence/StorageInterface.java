package edu.hm.dako.auditlogserver.persistence;

import edu.hm.dako.common.AuditLogPDU;

/**
 * Common methods for storing audit log data
 *
 * @author Linus Englert
 */
public interface StorageInterface {
    void save(AuditLogPDU pdu);
}