package edu.hm.dako.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * Nachrichtenaufbau für das AuditLog-Protokoll
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class AuditLogPDU implements Serializable {
    @Serial
    private static final long serialVersionUID = -6172619032079227589L;

    /**
     * referencing the logger
     */
    private static final Logger log = LogManager.getLogger(AuditLogPDU.class);

    /**
     * Kommandos bzw. PDU-Typen
     */
    private AuditLogPDUType pduType;

    /**
     * Login-Name des Clients
     */
    private String userName;

    /**
     * Name des Chat-Client-Threads, der den Request absendet
     */
    private String clientThreadName;

    /**
     * Name des Worker-Threads, der den Request im Chat-Server verarbeitet
     */
    private String serverThreadName;

    /**
     * Zeitstempel zum Zeitpunkt des Audit-Logs im Chat-Server
     */
    private long auditTime;

    /**
     * Nutzdaten (eigentliche Chat-Nachricht in Textform)
     */
    private String message;

    /**
     * Konstruktor
     */
    public AuditLogPDU() {
        this.pduType = AuditLogPDUType.UNDEFINED;
        this.clientThreadName = null;
        this.serverThreadName = null;
        this.userName = null;
        this.auditTime = 0;
    }

    /**
     * PDU ausgeben ins Logfile
     *
     * @param pdu Auszugebende PDU
     */
    public static void printPdu(AuditLogPDU pdu) {
        // System.out.println(pdu);
        log.debug(pdu);
    }

    /**
     * Umwandlung einer Chat-PDU in eine AuditLogPDU
     *
     * @param chatPDUtoConvert Zu konvertierende Chat-PDU
     * @return AuditLog-PDU
     */
    public static AuditLogPDU convertChatPDUtoAuditLogPDU(ChatPDU chatPDUtoConvert) {
        AuditLogPDU resultAuditLogPDU = new AuditLogPDU();
        resultAuditLogPDU.pduType = convertChatPDUTypeToAuditLogPDUType(chatPDUtoConvert.getPduType());
        resultAuditLogPDU.userName = chatPDUtoConvert.getUserName();
        resultAuditLogPDU.clientThreadName = chatPDUtoConvert.getClientThreadName();
        resultAuditLogPDU.serverThreadName = chatPDUtoConvert.getServerThreadName();
        resultAuditLogPDU.auditTime = chatPDUtoConvert.getServerTime();
        resultAuditLogPDU.message = chatPDUtoConvert.getMessage();
        return resultAuditLogPDU;
    }

    /**
     * Umwandlung eines Chat-PDU-Typs in einen AuditLog-PDU-Typ
     *
     * @param pduType Zu konvertierender PDU-Typ
     * @return AuditLog-PDU-Typ
     */
    private static AuditLogPDUType convertChatPDUTypeToAuditLogPDUType(PDUType pduType) {
        for (AuditLogPDUType auditLogPDUTypeItem : AuditLogPDUType.values()) {
            if (auditLogPDUTypeItem.toString().equals(pduType.toString())) {
                return auditLogPDUTypeItem;
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * Umwandlung einer Chat-PDU in einem String
     *
     * @return Umgewandelte PDU als String
     */
    @Override
    public String toString() {
        Date dateAndTime = new Date(this.auditTime);

        return "\n"
                + "AuditLogPdu ****************************************************************************************************"
                + "\n" + "AuditLogType: " + pduType + "\n" + "userName: " + this.userName + ", "
                + "\n" + "clientThreadName: " + this.clientThreadName + "\n"
                + "serverThreadName: " + this.serverThreadName + "\n" + "auditTime: "
                + dateAndTime + "\n" + "message: " + this.message + "\n"
                + "**************************************************************************************************** SimplePdu"
                + "\n";
    }

    /**
     * getter
     *
     * @return pduType
     */
    public AuditLogPDUType getPduType() {
        return pduType;
    }

    /**
     * setter
     *
     * @param pduType pduType
     */
    public void setPduType(AuditLogPDUType pduType) {
        this.pduType = pduType;
    }

    /**
     * getter
     *
     * @return userName
     */
    public String getUserName() {
        return (this.userName);
    }

    /**
     * setter
     *
     * @param userName userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * getter
     *
     * @return clientThreadName
     */
    public String getClientThreadName() {
        return (this.clientThreadName);
    }

    /**
     * setter
     *
     * @param clientThreadName clientThreadName
     */
    public void setClientThreadName(String clientThreadName) {
        this.clientThreadName = clientThreadName;
    }

    /**
     * getter
     *
     * @return serverThreadName
     */
    public String getServerThreadName() {
        return (this.serverThreadName);
    }

    /**
     * setter
     *
     * @param serverThreadName serverThreadName
     */
    public void setServerThreadName(String serverThreadName) {
        this.serverThreadName = serverThreadName;
    }

    /**
     * getter
     *
     * @return auditTime
     */
    public long getAuditTime() {
        return (this.auditTime);
    }

    /**
     * setter
     *
     * @param auditTime auditTime
     */
    public void setAuditTime(long auditTime) {
        this.auditTime = auditTime;
    }

    /**
     * getter
     *
     * @return message
     */
    public String getMessage() {
        return (this.message);
    }

    /**
     * setter
     *
     * @param message message
     */
    public void setMessage(String message) {
        this.message = message;
    }
}