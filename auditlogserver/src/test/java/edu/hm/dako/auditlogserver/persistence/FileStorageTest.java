package edu.hm.dako.auditlogserver.persistence;

import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.common.AuditLogPDUType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class FileStorageTest {
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
    void save() {
        FileStorage storage = new FileStorage("ChatAuditLog.dat");
        storage.save(pdu);

        try (BufferedReader br = new BufferedReader(new FileReader("ChatAuditLog.dat"))) {
            String[] entry = br.readLine().split("\\|");
            assert entry[0].trim().equals(pdu.getPduType().toString().trim());
            assert entry[1].trim().equals(pdu.getUserName().trim());
            assert entry[2].trim().equals(pdu.getClientThreadName().trim());
            assert entry[3].trim().equals(pdu.getServerThreadName().trim());
            assert entry[4].trim().equals((pdu.getAuditTime() + "").trim());
            assert entry[5].trim().equals(pdu.getMessage().trim());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}