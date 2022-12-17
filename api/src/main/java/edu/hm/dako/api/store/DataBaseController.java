package edu.hm.dako.api.store;

import edu.hm.dako.api.data.PDU;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * connection to a local SQlite data base
 *
 * @author Linus Englert
 */
public class DataBaseController {
    private static final DataBaseController instance = new DataBaseController();
    private static Connection connection;
    private static final String DB_PATH = "database.sqlite";

    /**
     * this class is a singleton and should not be instantiated directly!
     *
     * @return DataBaseController instance
     */
    public static DataBaseController getInstance() {
        return instance;
    }

    /**
     * private constructor so people know to use the getInstance() function instead
     */
    private DataBaseController() {
    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("Fehler beim Laden des JDBC-Treibers");
            e.printStackTrace();
        }
    }

    /**
     * initializes a database connection and creates the table on first call
     */
    public void init() {
        try {
            if (connection != null) return;
            System.out.println("Creating Connection to Database...");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_PATH);
            if (!connection.isClosed()) System.out.println("...Connection established");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (!connection.isClosed() && connection != null) {
                    connection.close();
                    if (connection.isClosed()) System.out.println("Connection to Database closed");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }));

        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS pdu(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "pdutype VARCHAR(100)," +
                    "username VARCHAR(100)," +
                    "clientthread VARCHAR(100)," +
                    "serverthread VARCHAR(100)," +
                    "audittime VARCHAR(100)," +
                    "content VARCHAR(100));");
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        }
    }

    //-----PDU----------------------------------------------------------------

    /**
     * selects all PDUs from database
     *
     * @return PDU[] with the newest first
     */
    public PDU[] selectAllPDU() {
        List<PDU> lst = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM pdu ORDER BY audittime DESC;");
            while (rs.next()) {
                lst.add(new PDU(rs.getInt("id"),
                        rs.getString("pdutype"),
                        rs.getString("username"),
                        rs.getString("clientthread"),
                        rs.getString("serverthread"),
                        rs.getString("audittime"),
                        rs.getString("content"))
                );
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        }
        return lst.toArray(PDU[]::new);
    }

    /**
     * selects one PDU from database
     *
     * @param id specifies the result
     * @return specified PDU
     */
    public PDU selectPDU(int id) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM pdu WHERE id=?;");
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new PDU(rs.getInt("id"),
                        rs.getString("pdutype"),
                        rs.getString("username"),
                        rs.getString("clientthread"),
                        rs.getString("serverthread"),
                        rs.getString("audittime"),
                        rs.getString("content"));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * inserts a new PDU into database
     *
     * @param pdu PDU to insert
     * @return true if inserted successfully
     */
    public boolean insertPDU(PDU pdu) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("INSERT INTO pdu (pdutype, username," +
                    "clientthread, serverthread, audittime, content) VALUES (?, ?, ?, ?, ?, ?);");
            pstmt.setString(1, pdu.getPduType());
            pstmt.setString(2, pdu.getUsername());
            pstmt.setString(3, pdu.getClientThread());
            pstmt.setString(4, pdu.getServerThread());
            pstmt.setString(5, pdu.getAuditTime());
            pstmt.setString(6, pdu.getContent());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * updates a PDU in database
     *
     * @param id specifies the PDU to be updated
     * @param pdu new PDU
     * @return true if successfully updated
     */
    public boolean updatePDU(int id, PDU pdu) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("UPDATE pdu SET pdutype=?, username=?, " +
                    "clientthread=?, serverthread=?, audittime=?, content=? WHERE id=?;");
            pstmt.setString(1, pdu.getPduType());
            pstmt.setString(2, pdu.getUsername());
            pstmt.setString(3, pdu.getClientThread());
            pstmt.setString(4, pdu.getServerThread());
            pstmt.setString(5, pdu.getAuditTime());
            pstmt.setString(6, pdu.getContent());
            pstmt.setInt(7, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * deletes a PDU from database
     *
     * @param id specifies the PDU to be deleted
     * @return true if successfully deleted
     */
    public boolean deletePDU(int id) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("DELETE FROM pdu WHERE id=?;");
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        }
        return false;
    }

    //-----CLIENT----------------------------------------------------------------------------------------

    /**
     * selects all PDUs from a specific user from database
     *
     * @param username specific user
     * @return PDU[] with the newest first
     */
    public PDU[] selectPDU(String username) {
        List<PDU> lst = new ArrayList<>();
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM pdu WHERE username=? " +
                    "ORDER BY audittime DESC;");
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                lst.add(new PDU(rs.getInt("id"),
                        rs.getString("pdutype"),
                        rs.getString("username"),
                        rs.getString("clientthread"),
                        rs.getString("serverthread"),
                        rs.getString("audittime"),
                        rs.getString("content"))
                );
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        }
        return lst.toArray(PDU[]::new);
    }

    /**
     * counts the number of chat messages a single client has sent
     *
     * @param username the specific client
     * @return number of chat messages
     */
    public int selectClientChatMessagesCount(String username) {
        try {
            PreparedStatement pstmt = connection.prepareStatement("SELECT COUNT(*) FROM pdu WHERE username=? " +
                    "AND pdutype=?;");
            pstmt.setString(1, username);
            pstmt.setString(2, "Chat");
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        }
        return 0;
    }

    //-----STATISTICS-----------------------------------------------------------------------------------

    /**
     * counts the occurrence of specific pdu types:
     * 0 Undefined, 1 Login, 2 Logout, 3 Chat, 4 Finish
     *
     * @return array that contains all counters with content as defined above
     */
    public int[] selectPDUTypeCount() {
        int[] result = new int[] {0, 0, 0, 0, 0};
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT pdutype, COUNT(*) AS count FROM pdu GROUP BY pdutype;");
            while (rs.next()) {
                switch (rs.getString("pdutype")) {
                    case "Login" -> result[1] = rs.getInt("count");
                    case "Logout" -> result[2] = rs.getInt("count");
                    case "Chat" -> result[3] = rs.getInt("count");
                    case "Finish" -> result[4] = rs.getInt("count");
                    default -> result[0] += rs.getInt("count");
                }
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Couldn't handle DB-Query");
            e.printStackTrace();
        }
        return result;
    }
}
