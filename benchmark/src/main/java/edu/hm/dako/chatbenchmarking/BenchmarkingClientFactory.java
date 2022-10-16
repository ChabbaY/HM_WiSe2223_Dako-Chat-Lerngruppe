package edu.hm.dako.chatbenchmarking;

import edu.hm.dako.chatclient.ClientUserInterface;
import edu.hm.dako.connection.ConnectionFactory;

/**
 * Übernimmt die Konfiguration und die Erzeugung bestimmter Client-Typen für das Benchmarking.
 * Siehe {@link UserInterfaceInputParameters}
 * dies beinhaltet die {@link ConnectionFactory}, die Adressen, Ports, Denkzeit etc.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public final class BenchmarkingClientFactory {
    /**
     * Konstruktor
     */
    private BenchmarkingClientFactory() {
    }

    /**
     * Übernimmt die Konfiguration und die Erzeugung bestimmter Client-Typen für das Benchmarking.
     *
     * @param userInterface GUI
     * @param param input parameters
     * @param numberOfClient client count
     * @param sharedData shared client data
     * @param benchmarkingGui GUI
     * @return new client implementation
     */
    public static Runnable getClient(ClientUserInterface userInterface,
                                     UserInterfaceInputParameters param, int numberOfClient,
                                     SharedClientStatistics sharedData,
                                     BenchmarkingClientUserInterface benchmarkingGui) {
        try {
            // Derzeit sind TCPAdvancedImplementation, UDPAdvancedImplementation nicht implementiert
            return switch (param.getChatServerImplementationType()) {
                case TCPSimpleImplementation -> new BenchmarkingClientImpl(userInterface,
                        benchmarkingGui, param.getChatServerImplementationType(), param.getRemoteServerPort(),
                        param.getRemoteServerAddress(), numberOfClient, param.getMessageLength(),
                        param.getNumberOfMessages(), param.getClientThinkTime(),
                        param.getNumberOfRetries(), param.getResponseTimeout(), sharedData);
                default -> throw new RuntimeException(
                        "Unbekannter Implementierungstyp: " + param.getChatServerImplementationType());
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}