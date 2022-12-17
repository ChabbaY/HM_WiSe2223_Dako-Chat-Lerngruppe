package edu.hm.dako.auditlogserver.persistence;

import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.common.AuditLogRMIInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;

/**
 * storing audit log data in a text file (e.g. ChatAuditLog.dat)
 *
 * @author Linus Englert
 */
public class FileStorage implements AuditLogRMIInterface, StorageInterface, Serializable {
    /**
     * referencing the logger
     */
    private static final Logger log = LogManager.getLogger(FileStorage.class);

    /**
     * file to save to
     */
    private String fileName; //"ChatAuditLog.dat";//wollen wir den Namen lieber parametrisieren, um pro Verbindung mit einem ChatServer eine Datei zu haben?


    public FileStorage(String name){
        this.fileName = name;


    }
    @Override
    public void audit(AuditLogPDU pdu) {
        File file = new File(fileName);
        log.debug("Die file heißt: "+ fileName + "und liegt in: "+ file.toPath().toString());

        //create file if necessary
        try {
            boolean exist = file.createNewFile();
            if (!exist) {
                log.error("Datei " + fileName + " existierte bereits");
               // throw new IllegalArgumentException("Datei existiert bereits");
            } else {
                log.debug("Datei " + fileName + " erfolgreich angelegt");
            }

            // Datei zum Erweitern öffnen
            FileWriter fileWriter = new FileWriter(fileName, StandardCharsets.UTF_8, true);
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
            System.out.println("Audit Log PDU in Datei " + fileName + " geschrieben");
            out.flush();
            out.close();
        } catch (IOException e) {
            log.error("Fehler beim Schreiben von Audit Log PDU in Datei " + fileName);
        }
    }





}