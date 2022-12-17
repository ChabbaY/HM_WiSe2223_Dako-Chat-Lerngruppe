package edu.hm.dako.auditlogserver;

import edu.hm.dako.auditlogserver.gui.ALServerGUIInterface;
import edu.hm.dako.auditlogserver.persistence.FileStorage;
import edu.hm.dako.common.AuditLogRMIInterface;
import edu.hm.dako.common.ExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * audit log RMI implementation
 *
 * @author Kilian Brandner
 */
public class AuditLogRmiImpl extends AbstractALServer {
    /**
     * referencing the logger
     */
    private static final Logger LOG = LogManager.getLogger(AuditLogRmiImpl.class);

    /**
     * server port
     */
    private int port;

    /**
     * rmi registry
     */
    private Registry registry;

    /**
     * constructor
     *
     * @param gui interface to audit log server gui
     * @param serverPort port of the audit log server
     */
    public AuditLogRmiImpl(ALServerGUIInterface gui, int serverPort) {
        super();
        this.alServerGUIInterface = gui;
        port = serverPort;
        try {
            registry = LocateRegistry.createRegistry(serverPort);
        } catch (Exception e) {
            LOG.error("Exception bei RMI-Registry-Erstellung: " + e);
            ExceptionHandler.logException(e);
        }

        LOG.debug("AuditLogServer konstruiert!");
    }

    @Override
    public void start() {
            try {
                String dateString = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                        .format(Calendar.getInstance().getTime());
                AuditLogRMIInterface remote = (AuditLogRMIInterface) UnicastRemoteObject.exportObject(new FileStorage(dateString), port);
                registry.bind("AuditLogRmiServer", remote);
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
