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

    public PDU[] selectAllPDU() {
        List<PDU> lst = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM pdu;");
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
}
