package dk.dtu.dbproject;

import com.google.common.io.Resources;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;

public class Database {
    private Connection conn;

    /**
     * Creates a new database connection.
     *
     * @param host          The address of the server
     * @param port          The port of the server
     * @param username      The MySQL username
     * @param password      The MySQL password
     * @throws SQLException
     */
    public Database(String host, int port, String username, String password) {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        }
        catch (SQLException e) {
            System.out.println("Could not register MySQL driver...");
            e.printStackTrace();
            return;
        }

        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://"+host+":"+port, username, password);
        }
        catch (SQLException e) {
            System.out.println("Could not connect to database...");
            e.printStackTrace();
            return;
        }
        System.out.println("Connected to MySQL server! Initializing...");
        init();
        System.out.println("Database initialized!");
    }

    public boolean connected() {
        return this.conn != null;
    }

    private void executeUpdate(String sql) {
        System.out.println("Executing: " + sql);

        if (!connected()) {
            System.out.println("Can not execute an update when the connection is closed!");
            return;
        }

        Statement stmt;
        try {
            stmt = this.conn.createStatement();
        }
        catch (SQLException e) {
            System.out.println("Could not create statement...");
            e.printStackTrace();
            return;
        }

        try {
            stmt.executeUpdate(sql);
        }
        catch (SQLException e) {
            System.out.println("Could not execute statement...");
            e.printStackTrace();
            return;
        }

        System.out.println("Query executed!");
    }

    private void init() {
        String sql_script;
        try {
            sql_script = String.join(" ", Files.readAllLines(new File(Resources.class.getClassLoader().getResource("database_init.sql").getPath()).toPath(), StandardCharsets.UTF_8)).replace("    ", "");
        }
        catch (Exception e) {
            System.out.println("fuck");
            return;
        }
        String[] sql = sql_script.split(";");
        for (int i = 0; i < sql.length; i++) {
            sql[i] = sql[i].trim();
            if (sql[i] != null && !sql[i].equals("")) {
                executeUpdate(sql[i]);
            }
        }
    }

    // ============ Queries ============ //

    public boolean addUser(User user) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("INSERT INTO user (email, firstname, lastname, address, birthdate, gender) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getFirstname());
            pstmt.setString(3, user.getLastname());
            pstmt.setString(4, user.getAddress());
            pstmt.setDate(5, (java.sql.Date) user.getBirthdate());
            Boolean gender = user.getGender();
            if (gender == null) {
                pstmt.setNull(6, Types.BOOLEAN);
            }
            else {
                pstmt.setBoolean(6, gender);
            }
            int res = pstmt.executeUpdate();
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public User getUser(String email) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("SELECT * from user WHERE email == ?");
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Boolean gender = rs.getBoolean(rs.getString("gender"));
                if (rs.wasNull()) {
                    gender = null;
                }

                User user = new User(
                        rs.getString("email"),
                        rs.getString("firstname"), rs.getString("lastname"),
                        rs.getString("address"),
                        gender, rs.getDate("birthdate")
                );
                return user;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
