package edu.hm.dako.connection;

/**
 * Stellt beim Verbindungsaufbau sicher, dass eine {@link Connection} um Logging-Funktionen erweitert wird.
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class DecoratingConnectionFactory implements ConnectionFactory {
    // ConnectionFactory that should be logged
    private final ConnectionFactory wrappedFactory;

    /**
     * Konstruktor
     *
     * @param wrappedFactory ConnectionFactory that should be logged
     */
    public DecoratingConnectionFactory(ConnectionFactory wrappedFactory) {
        this.wrappedFactory = wrappedFactory;
    }

    @Override
    public Connection connectToServer(String remoteServerAddress,
                                      int serverPort, int localPort, int sendBufferSize, int receiveBufferSize)
            throws Exception {
        Connection wrappedConnection = wrappedFactory.connectToServer(remoteServerAddress, serverPort, localPort,
                sendBufferSize, receiveBufferSize);
        return new LoggingConnectionDecorator(wrappedConnection);
    }
}