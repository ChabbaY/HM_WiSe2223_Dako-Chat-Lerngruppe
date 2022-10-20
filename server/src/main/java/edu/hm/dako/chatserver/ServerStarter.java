package edu.hm.dako.chatserver;

import java.util.Arrays;

/**
 * starts the chat server
 *
 * @author Peter Mandl, edited by Lerngruppe
 */
public class ServerStarter {
    /**
     * starts the chat server
     *
     * @param args available args:
     *             --nogui disables the gui
     *             --protocol=tcpsimple (default; tcpadvanced not implemented yet)
     *             --port=50001 (default)
     *             --send-buffer=300000 (default)
     *             --receive-buffer=300000 (default)
     *             --auditlog=true | false (default true)
     *             --auditlog-host=localhost (default)
     *             --auditlog-port=40001 (default)
     *             --auditlog-protocol=tcp | udp | rmi (default tcp)
     */
    public static void main(String[] args) {//TODO parametrize
        boolean GUI = true;
        for(String s: args) {
            String[] values = s.split("=");
            switch (values[0]) {
                case "--nogui" -> GUI = false;
                case "--protocol" -> System.out.println("protocol: " + values[1]);
                case "--port" -> System.out.println("port: " + values[1]);
                case "--send-buffer" -> System.out.println("send-buffer: " + values[1]);
                case "--receive-buffer" -> System.out.println("receive-buffer: " + values[1]);
                case "--auditlog" -> System.out.println("auditlog: " + values[1]);
                case "--auditlog-host" -> System.out.println("auditlog-host: " + values[1]);
                case "--auditlog-port" -> System.out.println("auditlog-port: " + values[1]);
                case "--auditlog-protocol" -> System.out.println("auditlog-protocol: " + values[1]);
            }
        }
        ServerGUI.main(args);
    }

    /**
     * Konstruktor
     */
    public ServerStarter() {
    }
}