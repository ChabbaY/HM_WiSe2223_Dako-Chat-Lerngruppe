package edu.hm.dako.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Peter Mandl, edited by Lerngruppe
 */
public interface AuditLogRmiInterface extends Remote {
    /**
     * Chat-Nachricht in das Audit-Log schreiben
     *
     * @param pdu Chat-Nachricht
     * @throws RemoteException Fehler bei der Kommunikation über RMI
     */
    void audit(AuditLogPDU pdu) throws RemoteException;
}