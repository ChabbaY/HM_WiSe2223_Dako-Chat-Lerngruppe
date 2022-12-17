package edu.hm.dako.auditlogserver;

import edu.hm.dako.auditlogserver.gui.ALServerGUIInterface;
import edu.hm.dako.auditlogserver.persistence.FileStorage;
import edu.hm.dako.auditlogserver.persistence.StorageInterface;
import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.common.AuditLogRMIInterface;
import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.connection.Connection;
import edu.hm.dako.connection.ServerSocketInterface;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.concurrent.ExecutorService;

public class AuditLogRmiImpl extends AbstractALServer {
    private static final Logger LOG = LogManager.getLogger(AuditLogRmiImpl.class);

    private int serverPort;

    public AuditLogRmiImpl(ALServerGUIInterface gui, int serverPort) {
        super();
        this.alServerGUIInterface = gui;
        this.serverPort = serverPort;

        LOG.debug("AuditLogServer konstruiert!");
    }

    @Override
    public void start() {
            try {
                Registry registry = LocateRegistry.createRegistry(serverPort);
                AuditLogRmiRemote impl = new AuditLogRmiRemote();
                registry.bind("AuditLogRmiServer", impl);
            } catch (Exception e) {
                LOG.error("Exception beim Entgegennehmen von Verbindungsaufbauwünschen: " + e);
                ExceptionHandler.logException(e);
            }
    }

    @Override
    public void stop() throws Exception {
        LOG.info("AuditLog beendet sich");
    }
}
