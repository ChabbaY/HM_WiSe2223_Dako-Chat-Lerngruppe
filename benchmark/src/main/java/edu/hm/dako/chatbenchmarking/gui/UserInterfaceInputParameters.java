package edu.hm.dako.chatbenchmarking.gui;

import edu.hm.dako.common.ChatServerImplementationType;

/**
 * Konfigurationsparameter für Lasttest
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class UserInterfaceInputParameters {
    /**
     * Anzahl zu startender Client-Threads
     */
    private int numberOfClients;

    /**
     * Nachrichtenlänge
     */
    private int messageLength;

    /**
     * Denkzeit zwischen zwei Requests
     */
    private int clientThinkTime;

    /**
     * Anzahl der Nachrichten pro Client-Thread
     */
    private int numberOfMessages;

    /**
     * Maximale Anzahl an Übertragungswiederholungen bei verbindungslosen Protokollen
     */
    private int numberOfRetries;

    /**
     * Maximale Wartezeit in ms auf eine Antwort des Servers bei verbindungslosen Protokollen
     */
    private int responseTimeout;

    /**
     * Typ der Implementierung
     */
    private ChatServerImplementationType implementationType;

    /**
     * Typ der Messung für das Messprotokoll
     */
    private MeasurementType measurementType;

    /**
     * UDP- oder TCP-Port des Servers, Default: 50001
     */
    private int remoteServerPort;

    /**
     * Server-IP-Adresse, Default: "127.0.0.1"
     */
    private String remoteServerAddress;

    /**
     * Konstruktor: Belegung der InputParameter mit Standardwerten
     */
    public UserInterfaceInputParameters() {
        numberOfClients = 2;
        clientThinkTime = 1;
        messageLength = 100;
        numberOfMessages = 5;
        remoteServerPort = 50001;
        remoteServerAddress = "127.0.0.1";
        implementationType = ChatServerImplementationType.TCPSimpleImplementation;
        measurementType = MeasurementType.VarThreads;
    }

    /**
     * Abbildung der Implementierungstypen auf Strings
     *
     * @param type Implementierungstyp
     * @return Passender String für Implementierungstyp
     */
    public String mapImplementationTypeToString(ChatServerImplementationType type) {
        String returnString = null;

        switch (type) {
            case TCPAdvancedImplementation -> returnString = "TCPAdvanced-Implementation";
            case TCPSimpleImplementation -> returnString = "TCPSimple-Implementation";
            case UDPAdvancedImplementation -> returnString = "UDPAdvanced-Implementation";
            default -> {
            }
        }

        return returnString;
    }

    /**
     * Abbildung der Messung-Typen auf Strings
     *
     * @param type Messungstyp
     * @return Passender String für Messungstyp
     */
    public String mapMeasurementTypeToString(MeasurementType type) {
        String returnString = null;

        switch (type) {
            case VarThreads -> returnString = "VariationThreadAnzahl";
            case VarMsgLength -> returnString = "VariationNachrichtenlänge";
            default -> {
            }
        }

        return returnString;
    }

    /**
     * getter
     *
     * @return numberOfClients
     */
    public int getNumberOfClients() {
        return numberOfClients;
    }

    /**
     * setter
     *
     * @param numberOfClients numberOfClients
     */
    public void setNumberOfClients(int numberOfClients) {
        this.numberOfClients = numberOfClients;
    }

    /**
     * getter
     *
     * @return messageLength
     */
    public int getMessageLength() {
        return messageLength;
    }

    /**
     * setter
     *
     * @param messageLength messageLength
     */
    public void setMessageLength(int messageLength) {
        this.messageLength = messageLength;
    }

    /**
     * getter
     *
     * @return clientThinkTime
     */
    public int getClientThinkTime() {
        return clientThinkTime;
    }

    /**
     * setter
     *
     * @param clientThinkTime clientThinkTime
     */
    public void setClientThinkTime(int clientThinkTime) {
        this.clientThinkTime = clientThinkTime;
    }

    /**
     * getter
     *
     * @return numberOfMessages
     */
    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    /**
     * setter
     *
     * @param numberOfMessages numberOfMessages
     */
    public void setNumberOfMessages(int numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }

    /**
     * getter
     *
     * @return implementationType
     */
    public ChatServerImplementationType getChatServerImplementationType() {
        return implementationType;
    }

    /**
     * setter
     *
     * @param implementationType implementationType
     */
    public void setChatServerImplementationType(ChatServerImplementationType implementationType) {
        this.implementationType = implementationType;
    }

    /**
     * getter
     *
     * @return numberOfRetries
     */
    public int getNumberOfRetries() {
        return numberOfRetries;
    }

    /**
     * setter
     *
     * @param numberOfRetries numberOfRetries
     */
    public void setNumberOfRetries(int numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
    }

    /**
     * getter
     *
     * @return responseTimeout
     */
    public int getResponseTimeout() {
        return responseTimeout;
    }

    /**
     * setter
     *
     * @param responseTimeout responseTimeout
     */
    public void setResponseTimeout(int responseTimeout) {
        this.responseTimeout = responseTimeout;
    }

    /**
     * getter
     *
     * @return measurementType
     */
    public MeasurementType getMeasurementType() {
        return measurementType;
    }

    /**
     * setter
     *
     * @param measurementType measurementType
     */
    public void setMeasurementType(MeasurementType measurementType) {
        this.measurementType = measurementType;
    }

    /**
     * getter
     *
     * @return remoteServerPort
     */
    public int getRemoteServerPort() {
        return remoteServerPort;
    }

    /**
     * setter
     *
     * @param remoteServerPort remoteServerPort
     */
    public void setRemoteServerPort(int remoteServerPort) {
        this.remoteServerPort = remoteServerPort;
    }

    /**
     * getter
     *
     * @return remoteServerAddress
     */
    public String getRemoteServerAddress() {
        return remoteServerAddress;
    }

    /**
     * setter
     *
     * @param remoteServerAddress remoteServerAddress
     */
    public void setRemoteServerAddress(String remoteServerAddress) {
        this.remoteServerAddress = remoteServerAddress;
    }

    /**
     * Typen von unterstützten Messungen: nur für die Unterscheidung der Messung im Benchmarking-Protokoll
     *
     * @author Mandl
     */
    public enum MeasurementType {
        /**
         * Variation der ThreadAnzahl
         */
        VarThreads,

        /**
         * Variation der Nachrichtenlänge
         */
        VarMsgLength
    }
}