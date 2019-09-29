package scylla;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Database {

    private String username;
    private String password;
    

    private final String host = "jdbc:derby:ScyllaBase;create=true";
    private final String DbClass = "org.apache.derby.jdbc.EmbeddedDriver";

    public Database() {

    }

    public Database(String username, String password) {
        this.username = username;
        this.password = password;
        
    }

    public Database(String username, String password, boolean newUser) {
        this.username = username;
        this.password = password;
       
    }

    public Database(String username) {
        this.username = username;
    }

    public void CreateProfiles() {// TABLE FOR USERS
        try {
            Class.forName(DbClass);
            Connection conn = DriverManager.getConnection(host, "Scylla", "Scylla");

            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet drs = dbm.getTables(null, null, "PROFILES", null);

            if (drs.next()) {
            } else {
                String createTable = "CREATE TABLE APP.PROFILES (USERNAME VARCHAR(100) NOT NULL, "
                        + "PASSWORD VARCHAR(100) NOT NULL, DATE_CREATED DATE, PRIMARY KEY (USERNAME))";

                PreparedStatement Cpst = conn.prepareStatement(createTable);
                Cpst.execute();

            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void CreateRecords() {  //TABLE FOR RECORDS
        try {
            Class.forName(DbClass);
            Connection conn = DriverManager.getConnection(host, "Scylla", "Scylla");

            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet drs = dbm.getTables(null, null, "RECORDS", null);

            if (drs.next()) {
            } else {
                String createTable = "CREATE TABLE APP.RECORDS (USERNAME VARCHAR(100) NOT NULL, RECORDNAME VARCHAR(100) NOT NULL, "
                        + "CONTENT VARCHAR(1000) NOT NULL)";

                PreparedStatement Cpst = conn.prepareStatement(createTable);
                Cpst.execute();

            }
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean Sign_in() {

        String query = "SELECT * FROM APP.PROFILES WHERE USERNAME=? AND PASSWORD=?";
        try {

            java.sql.Connection conn = DriverManager.getConnection(host, "Scylla", "Scylla");
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.err.println(e.toString());
            return false;
        }
    }

    public boolean userExists() {

        String query = "SELECT * FROM APP.PROFILES WHERE USERNAME=?";
        try {
            Class.forName(DbClass);
            java.sql.Connection conn = DriverManager.getConnection(host, "Scylla", "Scylla");
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.toString());
            return false;
        }

    }

    public boolean Sign_up() {

        try {
            String insertQuery = "INSERT INTO APP.PROFILES VALUES (?,?,?)";
            CreateProfiles();
            Class.forName(DbClass);
            java.sql.Connection conn = DriverManager.getConnection(host, "Scylla", "Scylla");
            PreparedStatement Ipst = conn.prepareStatement(insertQuery);
//---------------------------------------------------------------
// Get the system date and time.
            Date utilDate = new Date();
// Convert it to java.sql.Date
            java.sql.Date date = new java.sql.Date(utilDate.getTime());

//---------------------------------------------------------------
            Ipst.setString(1, username);
            Ipst.setString(2, password);
            Ipst.setDate(3, date);
            Ipst.executeUpdate();
            return true;
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.toString());
            return false;
        }
    }

    public boolean RecordsExists() {//that is, the table of records itself

        String query = "SELECT * FROM APP.RECORDS WHERE USERNAME=?";
        try {
            Class.forName(DbClass);
            java.sql.Connection conn = DriverManager.getConnection(host, "Scylla", "Scylla");
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.toString());
            return false;
        }

    }

    public boolean RecordExists(String recordName) {// that is, a particular record

        String query = "SELECT * FROM APP.RECORDS WHERE USERNAME=? AND RECORDNAME=?";
        try {
            Class.forName(DbClass);
            java.sql.Connection conn = DriverManager.getConnection(host, "Scylla", "Scylla");
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, recordName);
            ResultSet rs = pst.executeQuery();
            return rs.next();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.toString());
            return false;
        }
    }

    public void saveRecord(String recordName, String content) {

        String insertQuery = "INSERT INTO APP.RECORDS VALUES (?,?,?)";
        try {
            Class.forName(DbClass);
            java.sql.Connection conn = DriverManager.getConnection(host, "Scylla", "Scylla");
            PreparedStatement Ipst = conn.prepareStatement(insertQuery);

            Ipst.setString(1, username);
            Ipst.setString(2, recordName);
            Ipst.setString(3, content);
            Ipst.executeUpdate();

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.toString());

        }
    }

    public ArrayList getRecordNames() {
        String query = "SELECT RECORDNAME FROM APP.RECORDS WHERE USERNAME=?";
        try {
            Class.forName(DbClass);
            java.sql.Connection conn = DriverManager.getConnection(host, "Scylla", "Scylla");
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            ArrayList recordNames = new ArrayList();
            while (rs.next()) {
                recordNames.add(rs.getString("RECORDNAME"));
            }
            return recordNames;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.toString());
            ArrayList errorList = new ArrayList();
            return errorList;

        }
    }

    public String getContent(String selectedRecord) {
        String query = "SELECT CONTENT FROM APP.RECORDS WHERE USERNAME=? AND RECORDNAME=?";
        try {
            Class.forName(DbClass);
            java.sql.Connection conn = DriverManager.getConnection(host, "Scylla", "Scylla");
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, selectedRecord);
            ResultSet rs = pst.executeQuery();
            String content = "";
            if (rs.next()) {
                content = rs.getString("CONTENT");
            }
            return content;
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.toString());

            return "Scylla Error";

        }
    }

    public void updateRecord(String recordName, String content) {
        String updateQuery = "UPDATE APP.RECORDS SET CONTENT=? WHERE USERNAME=? AND RECORDNAME=?";
        try {
            Class.forName(DbClass);
            java.sql.Connection conn = DriverManager.getConnection(host, "Scylla", "Scylla");
            PreparedStatement Ipst = conn.prepareStatement(updateQuery);

            Ipst.setString(1, content);
            Ipst.setString(2, username);
            Ipst.setString(3, recordName);
            Ipst.executeUpdate();

        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.toString());

        }
    }

    public void deleteRecord(String selectedRecord) {
        String query = "DELETE FROM APP.RECORDS WHERE USERNAME=? AND RECORDNAME=?";
        try {
            Class.forName(DbClass);
            java.sql.Connection conn = DriverManager.getConnection(host, "Scylla", "Scylla");
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, selectedRecord);
            pst.executeUpdate();

        } catch (ClassNotFoundException | SQLException e) {
            System.out.println(e.toString());

        }
    }
}
