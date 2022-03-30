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

    public Person getPerson(String email) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("SELECT * from user WHERE email == ?");
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                boolean gender_bool = rs.getBoolean(rs.getString("gender"));
                String gender = rs.wasNull() ? "Other" : (gender_bool ? "Male" : "Female");

                Person person = new Person(
                        rs.getString("email"),
                        rs.getString("firstname"), rs.getString("lastname"),
                        gender, rs.getDate("birthdate")
                );
                return person;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
