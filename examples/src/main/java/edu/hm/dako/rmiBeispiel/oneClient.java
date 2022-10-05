package edu.hm.dako.rmiBeispiel;

import java.rmi.Naming;

/**
 * Beispiel fuer einen RMI Client
 * @author P.Mandl
 *
 */
public class oneClient {

    public static void main(String[] args)
    {
       try {
          Thread.sleep(200);
       } catch (InterruptedException ignored)  {
       }

       try
       {
          System.out.println("Test beginnt");
          OneInterface remoteObject = (OneInterface) Naming.lookup("rmi://localhost:1099/oneServer");
          System.out.println("Returnwert: " + remoteObject.op1("Test1"));
          System.out.println("Returnwert: " + remoteObject.op2("Test2"));
          System.out.println("Returnwert: " + remoteObject.op1("Test3"));
          System.out.println("Returnwert: " + remoteObject.op2("Test4"));
          System.out.println("Test beendet");
       }
       // wirft NotBoundException und MalformedURLException und RemoteException
       catch(Exception ex)
       {
          System.out.println("Exception = " + ex ) ;
       }
    }

}
