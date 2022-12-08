package edu.hm.dako.api.dataobjects.objects;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Objects;

/**
 * definition of the PDU entity to store an Audit Log PDU
 *
 * @author Linus Englert
 */
@Entity
public class PDU {
    private @Id @GeneratedValue long id;
    private String pduType, username, clientThreadName, serverThreadName, auditTime, message;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
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

    public String getClientThreadName() {
        return clientThreadName;
    }
    public void setClientThreadName(String clientThreadName) {
        this.clientThreadName = clientThreadName;
    }

    public String getServerThreadName() {
        return serverThreadName;
    }
    public void setServerThreadName(String serverThreadName) {
        this.serverThreadName = serverThreadName;
    }

    public String getAuditTime() {
        return auditTime;
    }
    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PDU value)) return false;
        return Objects.equals(this.id, value.id) &&
                Objects.equals(this.pduType, value.pduType) &&
                Objects.equals(this.username, value.username) &&
                Objects.equals(this.clientThreadName, value.clientThreadName) &&
                Objects.equals(this.serverThreadName, value.serverThreadName) &&
                Objects.equals(this.auditTime, value.auditTime) &&
                Objects.equals(this.message, value.message);
    }
    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.pduType, this.username, this.clientThreadName,
                this.serverThreadName, this.auditTime, this.message);
    }
    @Override
    public String toString() {
        return String.format("PDU{id=%s, pduType=%s, username=%s, clientThreadName=%s, serverThreadName=%s," +
                        "auditTime=%s, message=%s}",
                this.id, this.pduType, this.username, this.clientThreadName, serverThreadName, auditTime, message);
    }
}