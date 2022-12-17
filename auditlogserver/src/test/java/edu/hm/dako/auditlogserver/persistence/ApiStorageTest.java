package edu.hm.dako.auditlogserver.persistence;

import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.common.AuditLogPDUType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ApiStorageTest {
    private static AuditLogPDU pdu;

    @BeforeAll
    static void setUp() {
        pdu = new AuditLogPDU();
        pdu.setPduType(AuditLogPDUType.CHAT_MESSAGE_REQUEST);
        pdu.setUserName("chabbay");
        pdu.setClientThreadName("chabbay-1");
        pdu.setServerThreadName("server-1");
        pdu.setAuditTime(1L);
        pdu.setMessage("Testnachricht junit");
    }

    @Test
    void audit() {
        ApiStorage storage = new ApiStorage();
        storage.audit(pdu);
    }
}