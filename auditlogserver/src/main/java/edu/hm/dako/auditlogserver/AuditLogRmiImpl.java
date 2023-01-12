package edu.hm.dako.auditlogserver;

import edu.hm.dako.auditlogserver.gui.ALServerGUIInterface;
import edu.hm.dako.auditlogserver.persistence.FileStorage;
import edu.hm.dako.common.ExceptionHandler;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final String RMI_KEY = "AuditLogRmiServer";

    /**
     * server port
     */
    private int port;

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

        LOG.debug("AuditLogServer konstruiert!");
    }

    @Override
    public void start() {
        // create the FileStorage object
        FileStorage storage = new FileStorage("ChatAuditLog.dat", alServerGUIInterface);
        try {
            // start the RMI registry
            startRmiRegistry(port);
            exportObject(storage, port, RMI_KEY);
        } catch (RemoteException e) {
            LOG.error("RMI Export ist fehlgeschlagen", e);
            ExceptionHandler.logExceptionAndTerminate(e);
        }
        LOG.info("AuditLogRmiServer exportiert");
    }

    @Override
    public void stop() throws Exception {
        LocateRegistry.getRegistry(port).unbind(RMI_KEY);

        LOG.info("AuditLog beendet");
    }

    public static void startRmiRegistry(int port) throws RemoteException {
        try {
            LocateRegistry.createRegistry(port);
        } catch (ExportException e) {
            LOG.debug("RMI Registry existiert bereit");
        }
    }

    public static void exportObject(Remote remote, int port, String name) throws RemoteException {
        // export the object
        Remote remote_Stub = UnicastRemoteObject.exportObject(remote, port);
        Registry registry = LocateRegistry.getRegistry(port);
        // bind the object to the registry
        registry.rebind(name, remote_Stub);
    }
}