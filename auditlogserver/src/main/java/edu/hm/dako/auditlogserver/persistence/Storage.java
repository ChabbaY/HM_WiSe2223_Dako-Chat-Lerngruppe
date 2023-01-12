package edu.hm.dako.auditlogserver.persistence;

import edu.hm.dako.auditlogserver.gui.ALServerGUIInterface;
import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.common.AuditLogPDUType;
import edu.hm.dako.common.AuditLogRMIInterface;
import java.io.IOException;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * storing audit log data in a database via API-call if possible
 * else storage in text file
 *
 * @author Linus Englert
 */
public class Storage implements AuditLogRMIInterface, StorageInterface, Serializable {
    /**
     * referencing the logger
     */
    private static final Logger log = LogManager.getLogger(Storage.class);

    /**
     * storage in a file
     */
    FileStorage fileStorage;

    /**
     * storage via api (database)
     */
    ApiStorage apiStorage;

    /**
     * storage that will be used
     */
    StorageInterface activeStorage;

    /**
     * constructor
     *
     * @param fileName file name for text file storage, ignored if API is used
     */
    public Storage(String fileName, ALServerGUIInterface serverGUIInterface) {
        fileStorage = new FileStorage(fileName, serverGUIInterface);
        apiStorage = new ApiStorage(serverGUIInterface);

        if (hasApiConnection()) {
            activeStorage = apiStorage;
            log.info("using api storage");
        } else {
            activeStorage = fileStorage;
            log.info("api not available - using file storage");
        }
    }

    @Override
    public void audit(AuditLogPDU pdu) {
        activeStorage.audit(pdu);
    }

    public static boolean hasApiConnection() {
        try {
            URL url = new URL("http://localhost:8080/api/");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            return (conn.getResponseCode() == 200);
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
        return false;
    }
    
    public static void updateCounter(AuditLogPDU pdu, ALServerGUIInterface serverGUIInterface) {
        // Counter hochzählen
        serverGUIInterface.increaseNumberOfRequests();

        // Client-Counter
        if (pdu.getPduType().equals(AuditLogPDUType.LOGIN_REQUEST)) serverGUIInterface.increaseNumberOfLoggedInClients();
        if (pdu.getPduType().equals(AuditLogPDUType.LOGOUT_REQUEST)) serverGUIInterface.decreaseNumberOfLoggedInClients();
    }
}