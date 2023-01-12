package edu.hm.dako.auditlogserver.persistence;

import edu.hm.dako.auditlogserver.gui.ALServerGUIInterface;
import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.common.AuditLogRMIInterface;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * storing audit log data in a database via API-call
 *
 * @author Linus Englert
 */
class ApiStorage implements AuditLogRMIInterface, StorageInterface, Serializable {
    /**
     * referencing the logger
     */
    private static final Logger log = LogManager.getLogger(ApiStorage.class);

    ALServerGUIInterface counter;

    /**
     * constructor
     */
    public ApiStorage(ALServerGUIInterface serverGUIInterface) {
        counter = serverGUIInterface;
    }

    @Override
    public void audit(AuditLogPDU pdu) {
        Storage.updateCounter(pdu, counter);

        try {
            String encodedData =
                    "pduType=" + URLEncoder.encode(String.valueOf(pdu.getPduType()).trim(), StandardCharsets.UTF_8) +
                    "&username=" + URLEncoder.encode(String.valueOf(pdu.getUserName()).trim(), StandardCharsets.UTF_8) +
                    "&clientThread=" + URLEncoder.encode(String.valueOf(pdu.getClientThreadName()).trim(), StandardCharsets.UTF_8) +
                    "&serverThread=" + URLEncoder.encode(String.valueOf(pdu.getServerThreadName()).trim(), StandardCharsets.UTF_8) +
                    "&auditTime=" + URLEncoder.encode(String.valueOf(pdu.getAuditTime()).trim(), StandardCharsets.UTF_8) +
                    "&content=" + URLEncoder.encode(String.valueOf(pdu.getMessage()).trim(), StandardCharsets.UTF_8);
            System.out.println(encodedData);
            URL url = new URL("http://localhost:8080/api/pdus");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
            conn.getOutputStream().write(encodedData.getBytes());
            log.debug("API call with response code: " + conn.getResponseCode());
        } catch (MalformedURLException e) {
            log.error("wrong URL");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("could not open URL");
            e.printStackTrace();
        } catch (Exception e) {
            log.error("critical error");
            e.printStackTrace();
        }
    }
}