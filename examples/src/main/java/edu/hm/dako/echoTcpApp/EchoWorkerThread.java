package edu.hm.dako.echoTcpApp;

import edu.hm.dako.connection.tcp.TcpConnection;

/**
 * WorkerThread für multithreaded Server
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class EchoWorkerThread extends Thread {
    private static int nrWorkerThread = 0;
    private final TcpConnection con;
    private boolean connect;

    public EchoWorkerThread(TcpConnection con) {
        this.con = con;
        connect = true;
        nrWorkerThread++;
        this.setName("WorkerThread-" + nrWorkerThread);
    }

    /**
     * ThreadImplementierung
     */
    public void run() {
        System.out.println(this.getName() + " gestartet");
        while (connect) {
            try {
                echo();
            } catch (Exception e1) {
                try {
                    System.out.println(this.getName() + ": Exception beim Empfang");
                    con.close();
                    connect = false;
                } catch (Exception e2) {
                    connect = false;
                    System.out.println(this.getName() + " : Exception bei Verbindungsabbau");
                }
            }
        }
    }

    /**
     * Nachricht vom Client empfangen und zurücksenden
     *
     * @throws Exception Fehler beim Nachrichtenempfang
     */
    private void echo() throws Exception {
        try {
            SimplePDU receivedPdu = (SimplePDU) con.receive();
            String message = receivedPdu.getMessage();
            System.out.println("PDU empfangen, Message-Länge = " + message.length());
            con.send(receivedPdu);
        } catch (Exception e) {
            System.out.println("Exception beim Empfang");
            throw new Exception();
        }
    }
}