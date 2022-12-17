package edu.hm.dako.auditlogserver.persistence;

import edu.hm.dako.common.AuditLogPDU;

/**
 * Common methods for storing audit log data
 *
 * @author Linus Englert
 */
public interface StorageInterface {
    /**
     * persists an audit log pdu
     *
     * @param pdu pdu to be persisted
     */
    void audit(AuditLogPDU pdu);
}