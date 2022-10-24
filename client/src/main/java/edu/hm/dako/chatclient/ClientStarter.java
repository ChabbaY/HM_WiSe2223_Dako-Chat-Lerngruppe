package edu.hm.dako.chatclient;

import edu.hm.dako.chatclient.gui.ClientFxGUI;
import edu.hm.dako.common.SystemConstants;
import edu.hm.dako.common.Tupel;
import edu.hm.dako.common.random.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * starts the chat client
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class ClientStarter {
    /**
     * referencing the logger
     */
    private static final Logger LOG = LogManager.getLogger(ClientStarter.class);

    private ClientImpl chatClient;

    private static boolean GUI = true;

    private String username;

    /**
     * spin lock for the loop, will be set to false after login and after receiving a message
     * will be set to true with every sent message
     */
    public static volatile boolean lock = true;

    /**
     * Flag, das angibt, ob der Client gestartet werden kann (alle Plausibilitätsprüfungen erfüllt)
     */
    private boolean startable = true;

    /**
     * starts the chat client
     *
     * @param args available args, please only use non-default
     *             --nogui disables the gui
     *             --server=127.0.0.1 (default)
     *             --port=50001 (default)
     *             --protocol=tcpsimple (default; tcpadvanced not implemented yet)
     *             --username=steve (will be chosen randomly if not specified)
     */
    public static void main(String[] args) {
        // Log4j2-Logging aus Datei konfigurieren
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        File file = new File("config/log4j/log4j2.chatClient.xml");
        context.setConfigLocation(file.toURI());

        ClientStarter starter = new ClientStarter(args);

        if (!GUI) {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("stop zum Beenden");

            while (!GUI) {
                while (lock) {
                    Thread.onSpinWait();
                }

                String input = "";
                try {
                    input = br.readLine();
                } catch (IOException e) {
                    LOG.error("Fehler bei Eingabe: " + e.getMessage());
                }

                switch (input) {
                    case "stop" -> {
                        starter.stopChatClient();
                        return;
                    }
                    default -> {
                        lock = true;
                        starter.sendMessage(input);
                    }
                }
            }
        }
    }

    /**
     * Konstruktor
     *
     * @param args available args, please only use non-default
     *             --nogui disables the gui
     *             --server=127.0.0.1 (default)
     *             --port=50001 (default)
     *             --protocol=tcpsimple (default; tcpadvanced not implemented yet)
     *             --username=steve (will be chosen randomly if not specified)
     */
    public ClientStarter(String[] args) {
        String host = "127.0.0.1";
        int port = 50001;
        String implType = SystemConstants.IMPL_TCP_SIMPLE;
        username = RandomValue.randomName();

        for(String s: args) {
            String[] values = s.split("=");
            switch (values[0]) {
                case "--nogui" -> GUI = false;
                case "--server" -> host = values[1];
                case "--port" -> {
                    Tupel<Integer, Boolean> result = validateServerPort(values[1]);
                    port = result.getX();
                    startable = result.getY();
                }
                case "--protocol" -> {
                    implType = values[1];
                    if ("tcpadvanced".equals(values[1])) {
                        implType = SystemConstants.IMPL_TCP_ADVANCED;
                    }
                }
                case "--username" -> username = values[1];
            }
        }

        if (GUI) {
            ClientFxGUI.main(args);
        } else {
            startChatClient(host, port, implType);
        }
    }

    private void startChatClient(String host, int port, String implType) {
        if (!startable) {
            LOG.error("Login konnte nicht zum Server gesendet werden, fehlerhafte Eingaben");
            return;
        }

        chatClient = new ClientImpl(null, port, host, implType);

        try {
            chatClient.login(username);
        } catch (Exception e) {
            // Benutzer mit dem angegebenen Namen schon angemeldet
            LOG.error("Login konnte nicht zum Server gesendet werden, Server aktiv?");
            // Verbindung zum Server wird wieder abgebaut
            chatClient.cancelConnection();
        }
    }
    private void stopChatClient() {
        try {
            chatClient.logout(username);
        } catch (IOException e) {
            LOG.error("Logout konnte nicht durchgeführt werden, Server aktiv?");
        }
    }

    private void sendMessage(String message) {
        try {
            chatClient.tell(username, message);
        } catch (IOException e) {
            // Senden funktioniert nicht, Server vermutlich nicht aktiv
            LOG.error("Senden konnte nicht durchgeführt werden, Server aktiv?");
        }
    }

    //-----VALIDATION-----------------------------------------

    /**
     * validate server port
     *
     * @param port port to validate
     * @return port
     */
    public static Tupel<Integer, Boolean> validateServerPort(String port) {
        int iServerPort = 0;
        boolean startable = true;
        if (port.matches("[0-9]+")) {
            iServerPort = Integer.parseInt(port);
            if ((iServerPort < 1) || (iServerPort > 65535)) {
                startable = false;
            } else {
                LOG.debug("Serverport: " + iServerPort);
            }
        } else {
            startable = false;
        }
        return new Tupel<>(iServerPort, startable);
    }
}