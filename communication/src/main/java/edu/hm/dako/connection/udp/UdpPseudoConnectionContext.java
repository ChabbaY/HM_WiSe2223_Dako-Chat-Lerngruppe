package edu.hm.dako.connection.udp;

import java.net.InetAddress;

/**
 * UDP ist ein zustandsloses Protokoll, in dem es eigentlich keine Verbindung gibt. Hier werden Verbindungsinformationen
 * der ankommenden Datagramme gespeichert, damit der Server seine Antwort an den richtigen Client schicken kann.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class UdpPseudoConnectionContext {
    // IP-Adresse
    private InetAddress remoteAddress;

    // Entfernter UDP-Port (Partnerport)
    private int remotePort;

    // Empfangenes Objekt
    private Object object;

    /**
     * Standardkonstruktor
     */
    public UdpPseudoConnectionContext() {
        this.remoteAddress = null;
        this.remotePort = 0;
        this.object = null;
    }

    /**
     * Aufbau einer Pseudoverbindung über UDP
     *
     * @param remoteAddress Entfernte Adresse
     * @param remotePort    Entfernter Port
     * @param object        Objekt, das übertragen wird
     */
    public UdpPseudoConnectionContext(InetAddress remoteAddress, int remotePort, Object object) {
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.object = object;
    }

    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}