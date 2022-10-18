package edu.hm.dako.examples.rmiBeispiel;

import java.io.Serial;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Beispiel für eine RMI RemoteObject-Implementierung
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class OneServerImpl extends UnicastRemoteObject implements OneInterface {
    @Serial
    private static final long serialVersionUID = -12345L;

    /**
     * Standardkonstruktor muss RemoteException werfen
     *
     * @throws RemoteException communication related exception
     */
    public OneServerImpl() throws RemoteException {
    }

    /**
     * Operation 1
     */
    @Override
    public int op1(String s) throws RemoteException {
        try {
            System.out.println("op1 aufgerufen mit Parameter: " + s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (1);
    }

    /**
     * Operation 2
     */
    @Override
    public long op2(String s) throws RemoteException {
        try {
            System.out.println("op2 aufgerufen mit Parameter: " + s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (2);
    }
}