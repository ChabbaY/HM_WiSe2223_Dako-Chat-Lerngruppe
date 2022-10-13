package edu.hm.dako.connection.tcp;

import edu.hm.dako.connection.Connection;
import edu.hm.dako.connection.ConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Erzeugen von TCP-Verbindungen zum Server
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class TcpConnectionFactory implements ConnectionFactory {
    // Maximale Anzahl an Verbindungsaufbauversuchen zum Server, die ein Client
    // unternimmt, bevor er abbricht
    private static final int MAX_CONNECTION_ATTEMPTS = 3;
    private static final Logger log = LogManager.getLogger(TcpConnectionFactory.class);
    // Zählt die Verbindungsaufbauversuche, bis eine Verbindung vom Server
    // angenommen wird
    private long connectionTryCounter = 0;

    /**
     * Baut eine Verbindung zum Server auf. Der Verbindungsaufbau wird mehrmals versucht.
     */
    public Connection connectToServer(String remoteServerAddress, int serverPort,
                                      int localPort, int sendBufferSize, int receiveBufferSize) throws IOException {
        TcpConnection connection = null;
        boolean connected = false;

        String localHost = "0.0.0.0";

        int attempts = 0;
        while ((!connected) && (attempts < MAX_CONNECTION_ATTEMPTS)) {
            try {
                connectionTryCounter++;
                log.debug(connectionTryCounter + ". Verbindungsaufbauversuch");
                connection = new TcpConnection(remoteServerAddress, serverPort,
                        localHost, localPort, sendBufferSize, receiveBufferSize, false, true);
                connected = true;
            } catch (IOException e) {
                log.error("Exception beim Verbindungsaufbau " + e.getMessage());
                // Ein wenig warten und erneut versuchen, falls es nicht geklappt hat
                attempts++;
                try {
                    Thread.sleep(100);
                } catch (Exception e2) {
                    log.error("Sleep unterbrochen");
                }
            }
            if (attempts >= MAX_CONNECTION_ATTEMPTS) {
                throw new IOException();
            }
        }

        log.debug("Anzahl der Verbindungsaufbauversuche für die Verbindung zum Server: "
                + connectionTryCounter);
        return connection;
    }
}