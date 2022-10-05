package edu.hm.dako.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuditLogRmiInterface extends Remote {

    /**
     * Chat-Nachricht in das Audit-Log schreiben
     * @param pdu Chat-Nachricht
     * @throws RemoteException Fehler bei der Kommununikation ueber RMI
     */
    void audit(AuditLogPDU pdu) throws RemoteException;
}
