package edu.hm.dako.examples.rmiBeispiel;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * OneInterface
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public interface OneInterface extends Remote {
    /**
     * operation 1
     *
     * @param s just a parameter
     * @return an integer
     * @throws RemoteException communication related exception
     */
    int op1(String s) throws RemoteException;

    /**
     * operation 2
     *
     * @param s just a parameter
     * @return a long
     * @throws RemoteException communication related exception
     */
    long op2(String s) throws RemoteException;
}