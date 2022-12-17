package edu.hm.dako.chatserver;

import edu.hm.dako.common.AuditLogPDU;
import edu.hm.dako.common.AuditLogPDUType;
import edu.hm.dako.common.AuditLogRMIInterface;
import edu.hm.dako.common.ChatPDU;
import edu.hm.dako.common.ExceptionHandler;
import edu.hm.dako.connection.tcp.TCPConnection;
import edu.hm.dako.connection.udp.UDPClientConnection;
import edu.hm.dako.connection.udp.UDPClientConnectionFactory;
import edu.hm.dako.connection.tcp.TCPConnectionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.rmi.Naming;

/**
 * Verwaltet eine logische Verbindung zum AuditLog-Server über UDP oder TCP
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class AuditLogConnection {
    /**
     * Verbindungstyp Konstante TCP
     */
    public static final int AUDIT_LOG_CONNECTION_TYPE_TCP = 1;

    /**
     * Verbindungstyp Konstante UDP
     */
    public static final int AUDIT_LOG_CONNECTION_TYPE_UDP = 2;

    /**
     * Verbindungstyp Konstante RMI
     */
    public static final int AUDIT_LOG_CONNECTION_TYPE_RMI = 3;

    /**
     * Puffergröße
     */
    static final int DEFAULT_SEND_BUFFER_AUDIT_LOG_SIZE = 400000;

    /**
     * Puffergröße
     */
    static final int DEFAULT_RECEIVE_BUFFER_AUDIT_LOG_SIZE = 40000;

    /**
     * Referenz auf den logger
     */
    private static final Logger LOG = LogManager.getLogger(AuditLogConnection.class);

    /**
     * Verbindungstyp (UDP, TCP oder RMI)
     */
    private final int connectionType;

    /**
     * UDP Verbindung zum Audit Log Server
     */
    protected UDPClientConnection udpConnectionToAuditLogServer = null;

    /**
     * TCP Verbindung zum Audit Log Server
     */
    protected TCPConnection tcpConnectionToAuditLogServer = null;

    /**
     * RMI Verbindung zum Audit Log Server
     */
    protected AuditLogRMIInterface auditLogRemoteObject = null;

    /**
     * Hostname des AuditLog-Servers
     */
    final String auditLogServer;

    /**
     * Port des AuditLog-Servers
     */
    final int auditLogPort;

    /**
     * Zählt abgehende AuditLog-Sätze
     */
    private long counter = 0;

    /**
     * Konstruktor
     *
     * @param connectionType Verbindungstyp (UDP, TCP, RMI)
     * @param auditLogServer Host des AuditLog-Servers
     * @param auditLogPort   Port für AuditLog-Server
     */
    public AuditLogConnection(int connectionType, String auditLogServer, int auditLogPort) {
        this.auditLogServer = auditLogServer;
        this.auditLogPort = auditLogPort;

        if ((connectionType != AUDIT_LOG_CONNECTION_TYPE_TCP) &&
                (connectionType != AUDIT_LOG_CONNECTION_TYPE_UDP) &&
                (connectionType != AUDIT_LOG_CONNECTION_TYPE_RMI)) {
            this.connectionType = AUDIT_LOG_CONNECTION_TYPE_TCP;
        } else {
            this.connectionType = connectionType;
        }
    }

    /**
     * Logische Verbindung zum AuditLog-Server aufbauen
     *
     * @throws Exception - Fehler im Socket, Verbindung kann nicht aufgebaut werden
     */
    public void connectToAuditLogServer() throws Exception {
        try {
            switch (connectionType) {
                case AUDIT_LOG_CONNECTION_TYPE_UDP -> {
                    // Verbindung zum AuditLog-Server und Verbindungsparameter
                    UDPClientConnectionFactory udpFactory = new UDPClientConnectionFactory();
                    udpConnectionToAuditLogServer = (UDPClientConnection) udpFactory.connectToServer(auditLogServer,
                            auditLogPort, 0, DEFAULT_SEND_BUFFER_AUDIT_LOG_SIZE,
                            DEFAULT_RECEIVE_BUFFER_AUDIT_LOG_SIZE);
                    LOG.debug("Verbindung zmu AuditLog-UDP-Server steht");
                }
                case AUDIT_LOG_CONNECTION_TYPE_TCP -> {
                    TCPConnectionFactory tcpFactory = new TCPConnectionFactory();
                    tcpConnectionToAuditLogServer = (TCPConnection) tcpFactory.connectToServer(auditLogServer,
                            auditLogPort, 0, DEFAULT_SEND_BUFFER_AUDIT_LOG_SIZE,
                            DEFAULT_RECEIVE_BUFFER_AUDIT_LOG_SIZE);
                    LOG.debug("Verbindung zum AuditLog-TCP-Server steht");
                }
                case AUDIT_LOG_CONNECTION_TYPE_RMI -> {
                    String rmiAddress = "rmi://" + auditLogServer + ":" + auditLogPort + "/" + "AuditLogRmiServer";
                    LOG.debug("Adresse des AuditLogRmiServers: {}", rmiAddress);

                    // RMI-Objekt-Referenz besorgen
                    auditLogRemoteObject = (AuditLogRMIInterface) Naming.lookup(rmiAddress);
                    LOG.debug("Lookup AuditLogRmiServer erfolgreich");
                    LOG.debug("Verbindung zum AuditLog-RMI-Server steht");
                }
                default -> System.out.println("Verbindung zum AuditLog-Server nicht möglich," +
                        "Verbindungstyp nicht korrekt");
            }
        } catch (Exception e) {
            LOG.error("Exception bei Verbindungsaufbau zum AuditLog-Server");
            ExceptionHandler.logException(e);
            throw new Exception();
        }
    }

    /**
     * Senden eines AuditLog-Satzes zum AuditLog-Server
     *
     * @param pdu  Chat-PDU zum Entnehmen von Parametern für den AuditLog-Satz
     * @param type Typ der AuditLog-PDU, der zu senden ist
     * @throws Exception Fehler beim Senden zum AuditLog-Server
     */
    public synchronized void send(ChatPDU pdu, AuditLogPDUType type) throws Exception {
        // AuditLog-Satz erzeugen
        AuditLogPDU auditLogPdu = createAuditLogPdu(pdu);
        auditLogPdu.setPduType(type);

        // AuditLog-Satz senden
        try {
            if (connectionType == AUDIT_LOG_CONNECTION_TYPE_UDP) {
                udpConnectionToAuditLogServer.send(auditLogPdu);
            } else if (connectionType == AUDIT_LOG_CONNECTION_TYPE_TCP) {
                tcpConnectionToAuditLogServer.send(auditLogPdu);
            } else if (connectionType == AUDIT_LOG_CONNECTION_TYPE_RMI) {
                auditLogRemoteObject.audit(auditLogPdu);
            }
            counter++;
            LOG.debug("AuditLog-Satz gesendet: {}", counter);
        } catch (Exception e) {
            LOG.error("Fehler beim Senden eines AuditLog-Satzes");
            ExceptionHandler.logException(e);
            throw new Exception();
        }
    }

    /**
     * Schliessen der Verbindung zum AuditLog-Server
     *
     * @throws Exception - Fehler beim Schliessen der Verbindung
     */
    public synchronized void close() throws Exception {
        try {
            AuditLogPDU closePdu = new AuditLogPDU();
            closePdu.setUserName("Chat-Server");
            closePdu.setPduType(AuditLogPDUType.FINISH_AUDIT_REQUEST);

            if (connectionType == AUDIT_LOG_CONNECTION_TYPE_UDP) {
                udpConnectionToAuditLogServer.send(closePdu);
                udpConnectionToAuditLogServer.close();
            } else if (connectionType == AUDIT_LOG_CONNECTION_TYPE_TCP) {
                tcpConnectionToAuditLogServer.send(closePdu);
                tcpConnectionToAuditLogServer.close();
            } else if (connectionType == AUDIT_LOG_CONNECTION_TYPE_RMI) {
                auditLogRemoteObject.audit(closePdu);
            }

            LOG.debug("Verbindung zum AuditLog-Server beendet, Gesendete AuditLog-Sätze: " + counter);
        } catch (Exception e) {
            LOG.error("Fehler beim Schliessen der Verbindung zum AuditLog-Server");
            ExceptionHandler.logException(e);
            throw new Exception();
        }
    }

    /**
     * AuditLog-PDU erzeugen
     *
     * @param chatPdu - Empfangene Chat-PDU, aus der Daten entnommen werden
     * @return Befüllte AuditLog-PDU
     */
    private AuditLogPDU createAuditLogPdu(ChatPDU chatPdu) {
        AuditLogPDU pdu = new AuditLogPDU();
        pdu.setPduType(AuditLogPDUType.UNDEFINED);
        pdu.setAuditTime(System.currentTimeMillis());
        pdu.setUserName(chatPdu.getUserName());
        pdu.setClientThreadName(chatPdu.getClientThreadName());
        pdu.setServerThreadName(Thread.currentThread().getName());
        pdu.setMessage(chatPdu.getMessage());
        return (pdu);
    }
}