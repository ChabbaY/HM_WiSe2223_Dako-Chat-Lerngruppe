package edu.hm.dako.api.data;

import java.util.Objects;

/**
 * data to be stored in the database
 *
 * @autor Linus Englert
 */
public class PDU {
    private int id;
    private String pduType, username, clientThread, serverThread, auditTime, content;

    public PDU() {
    }
    public PDU(int id, String pduType, String username, String clientThread, String serverThread, String auditTime, String content) {
        this.id = id;
        this.pduType = pduType;
        this.username = username;
        this.clientThread = clientThread;
        this.serverThread = serverThread;
        this.auditTime = auditTime;
        this.content = content;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getPduType() {
        return pduType;
    }
    public void setPduType(String pduType) {
        this.pduType = pduType;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getClientThread() {
        return clientThread;
    }
    public void setClientThread(String clientThread) {
        this.clientThread = clientThread;
    }

    public String getServerThread() {
        return serverThread;
    }
    public void setServerThread(String serverThread) {
        this.serverThread = serverThread;
    }

    public String getAuditTime() {
        return auditTime;
    }
    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PDU pdu = (PDU) o;
        return Objects.equals(id, pdu.id) &&
                Objects.equals(pduType, pdu.pduType) &&
                Objects.equals(username, pdu.username) &&
                Objects.equals(clientThread, pdu.clientThread) &&
                Objects.equals(serverThread, pdu.serverThread) &&
                Objects.equals(auditTime, pdu.auditTime) &&
                Objects.equals(content, pdu.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, pduType, username, clientThread, serverThread, auditTime, content);
    }

    @Override
    public String toString() {
        return "PDU{" +
                "id=" + id +
                ", pduType='" + pduType + '\'' +
                ", username='" + username + '\'' +
                ", clientThread='" + clientThread + '\'' +
                ", serverThread='" + serverThread + '\'' +
                ", auditTime='" + auditTime + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}