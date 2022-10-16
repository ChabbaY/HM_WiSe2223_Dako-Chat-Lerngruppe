package edu.hm.dako.connection.udp;

import java.net.InetAddress;

/**
 * UDP ist ein zustandsloses Protokoll, in dem es eigentlich keine Verbindung gibt. Hier werden Verbindungsinformationen
 * der ankommenden Datagramme gespeichert, damit der Server seine Antwort an den richtigen Client schicken kann.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class UDPPseudoConnectionContext {
    // IP-Adresse
    private InetAddress remoteAddress;

    // Entfernter UDP-Port (Partnerport)
    private int remotePort;

    // Empfangenes Objekt
    private Object object;

    /**
     * Standardkonstruktor
     */
    public UDPPseudoConnectionContext() {
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
    public UDPPseudoConnectionContext(InetAddress remoteAddress, int remotePort, Object object) {
        this.remoteAddress = remoteAddress;
        this.remotePort = remotePort;
        this.object = object;
    }

    /**
     * getter
     *
     * @return remoteAddress: entfernte Adresse
     */
    public InetAddress getRemoteAddress() {
        return remoteAddress;
    }

    /**
     * setter
     *
     * @param remoteAddress entfernte Adresse
     */
    public void setRemoteAddress(InetAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    /**
     * getter
     *
     * @return remotePort: entfernter Port
     */
    public int getRemotePort() {
        return remotePort;
    }

    /**
     * setter
     *
     * @param remotePort entfernter Port
     */
    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    /**
     * getter
     *
     * @return object: Objekt, das übertragen wird
     */
    public Object getObject() {
        return object;
    }

    /**
     * setter
     *
     * @param object Objekt, das übertragen wird
     */
    public void setObject(Object object) {
        this.object = object;
    }
}