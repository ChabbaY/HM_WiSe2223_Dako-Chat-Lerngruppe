package edu.hm.dako.api.data;

import java.util.Objects;

/**
 * data to be stored in the database
 *
 * @author Linus Englert
 */
public class PDU {
    private int id;
    private String pduType, username, clientThread, serverThread, auditTime, content;

    /**
     * constructor
     */
    public PDU() {
    }

    /**
     * constructor
     *
     * @param id id for database
     * @param pduType type of pdu
     * @param username name of the user
     * @param clientThread client thread name
     * @param serverThread server thread name
     * @param auditTime timestamp
     * @param content chat message
     */
    public PDU(int id, String pduType, String username, String clientThread, String serverThread, String auditTime, String content) {
        this.id = id;
        this.pduType = pduType;
        this.username = username;
        this.clientThread = clientThread;
        this.serverThread = serverThread;
        this.auditTime = auditTime;
        this.content = content;
    }

    /**
     * getter
     *
     * @return id
     */
    public int getId() {
        return id;
    }
    /**
     * setter
     *
     * @param id id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * getter
     *
     * @return pduType
     */
    public String getPduType() {
        return pduType;
    }
    /**
     * setter
     *
     * @param pduType pduType
     */
    public void setPduType(String pduType) {
        this.pduType = pduType;
    }

    /**
     * getter
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }
    /**
     * setter
     *
     * @param username username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * getter
     *
     * @return client thread name
     */
    public String getClientThread() {
        return clientThread;
    }
    /**
     * setter
     *
     * @param clientThread client thread name
     */
    public void setClientThread(String clientThread) {
        this.clientThread = clientThread;
    }

    /**
     * getter
     *
     * @return server thread name
     */
    public String getServerThread() {
        return serverThread;
    }
    /**
     * setter
     *
     * @param serverThread server thread name
     */
    public void setServerThread(String serverThread) {
        this.serverThread = serverThread;
    }

    /**
     * getter
     *
     * @return audit time
     */
    public String getAuditTime() {
        return auditTime;
    }
    /**
     * setter
     *
     * @param auditTime audit time
     */
    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    /**
     * getter
     *
     * @return chat message
     */
    public String getContent() {
        return content;
    }
    /**
     * setter
     *
     * @param content chat message
     */
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