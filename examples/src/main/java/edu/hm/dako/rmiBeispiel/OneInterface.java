package edu.hm.dako.rmiBeispiel;


import java.rmi.Remote;
import java.rmi.RemoteException;

public interface OneInterface extends Remote
{
    int op1(String s) throws RemoteException;
    long op2(String s) throws RemoteException;
}
