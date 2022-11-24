package edu.hm.dako.auditlogserver.persistence;

import edu.hm.dako.common.AuditLogPDU;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;

/**
 * storing audit log data in a text file (e.g. ChatAuditLog.dat)
 *
 * @author Linus Englert
 */
public class FileStorage implements StorageInterface {
    /**
     * referencing the logger
     */
    private static final Logger log = LogManager.getLogger(FileStorage.class);

    /**
     * file to save to
     */
    private static final String FILE_NAME = "ChatAuditLog.dat";

    @Override
    public void save(AuditLogPDU pdu) {
        File file = new File(FILE_NAME);

        //create file if necessary
        try {
            boolean exist = file.createNewFile();
            if (!exist) {
                log.debug("Datei " + FILE_NAME + " existierte bereits");
            } else {
                log.debug("Datei " + FILE_NAME + " erfolgreich angelegt");
            }

            // Datei zum Erweitern öffnen
            FileWriter fileWriter = new FileWriter(FILE_NAME, StandardCharsets.UTF_8, true);
            BufferedWriter out = new BufferedWriter(fileWriter);

            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter();

            sb.append(formatter.format("%s | %s | %s | %s | %s | %s\n",
                    pdu.getPduType(),
                    pdu.getUserName(),
                    pdu.getClientThreadName(),
                    pdu.getServerThreadName(),
                    pdu.getAuditTime(),
                    pdu.getMessage()));

            out.append(sb);
            formatter.close();
            System.out.println("Audit Log PDU in Datei " + FILE_NAME + " geschrieben");
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error("Fehler beim Schreiben von Audit Log PDU in Datei " + FILE_NAME);
        }
    }
}