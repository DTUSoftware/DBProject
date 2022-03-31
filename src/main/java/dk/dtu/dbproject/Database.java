package dk.dtu.dbproject;

import com.google.common.io.Resources;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.util.ArrayList;

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
    
    private int executePreparedStatementUpdate(PreparedStatement pstmt) {
        System.out.println("Executing " + pstmt.toString().replace("com.mysql.cj.jdbc.ClientPreparedStatement: ", ""));
        try {
            return pstmt.executeUpdate();
        }
        catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Duplicate entry - " + e);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    // ============ Queries ============ //

    public boolean addUser(User user) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("INSERT INTO user (`email`, `firstname`, `lastname`, `address`, `birthdate`, `gender`) VALUES (?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getFirstname());
            pstmt.setString(3, user.getLastname());
            pstmt.setString(4, user.getAddress());
            pstmt.setDate(5, new java.sql.Date(user.getBirthdate().getTime()));
            Boolean gender = user.getGender();
            if (gender == null) {
                pstmt.setNull(6, Types.BOOLEAN);
            }
            else {
                pstmt.setBoolean(6, gender);
            }
            int res = executePreparedStatementUpdate(pstmt);
            return res != 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public User getUser(String email) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("SELECT * from user WHERE `email` = ?");
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Boolean gender = rs.getBoolean("gender");
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

    public Contender[] getEventContenders(Event event) {
        if (event.getEventID() == null) {
            return new Contender[0];
        }

        try {
            PreparedStatement pstmt = this.conn.prepareStatement("SELECT * from contender WHERE `event_id` = ?");
            pstmt.setInt(1, event.getEventID());
            System.out.println(pstmt.toString());
            ResultSet rs = pstmt.executeQuery();
            ArrayList<Contender> contenders = new ArrayList<>();
            while (rs.next()) {
                User user = getUser(rs.getString("user_email"));
                Event db_event = getEvent(rs.getInt("event_id"));

                Contender contender = new Contender(
                        user, db_event
                );
                contenders.add(contender);
            }
            return contenders.toArray(new Contender[0]);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Event getEvent(int eventID) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("SELECT * from event WHERE `ID` = ?");
            pstmt.setInt(1, eventID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Event event = new Event(
                        rs.getInt("ID"),
                        rs.getDate("date"),
                        getUnion(rs.getString("union_id")),
                        new EventType(rs.getString("event_type_id"))
                );
                return event;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    private Event getEvent(String unionID, java.util.Date eventDate) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("SELECT * from event WHERE `union_id` = ? && `date` = ?");
            pstmt.setString(1, unionID);
            pstmt.setDate(2, new java.sql.Date(eventDate.getTime()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Event event = new Event(
                        rs.getInt("ID"),
                        rs.getDate("date"),
                        getUnion(rs.getString("union_id")),
                        new EventType(rs.getString("event_type_id"))
                );
                return event;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Union getUnion(String unionID) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("SELECT * from sports_union WHERE `ID` = ?");
            pstmt.setString(1, unionID);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Union union = new Union(
                        rs.getString("ID"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("address"),
                        rs.getString("phone_number")
                );
                return union;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addContender(Contender contender) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("INSERT INTO contender (`unique_event_id`, `event_id`, `user_email`) VALUES (?, ?, ?)");

            pstmt.setInt(1, getEventContenders(contender.getEvent()).length+1);
            pstmt.setInt(2, contender.getEvent().getEventID());
            pstmt.setString(3, contender.getUser().getEmail());

            int res = executePreparedStatementUpdate(pstmt);
            return res != 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addUnion(Union union) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("INSERT INTO sports_union (`ID`, `name`, `email`, `address`, `phone_number`) VALUES (?, ?, ?, ?, ?)");

            pstmt.setString(1, union.getUnionID());
            pstmt.setString(2, union.getName());
            pstmt.setString(3, union.getEmail());
            pstmt.setString(4, union.getAddress());
            pstmt.setString(5, union.getPhoneNumber());

            int res = executePreparedStatementUpdate(pstmt);
            return res != 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addEventType(EventType eventType) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("INSERT INTO event_type (`ID`) VALUES (?)");

            pstmt.setString(1, eventType.getEventTypeID());

            int res = executePreparedStatementUpdate(pstmt);
            return res != 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addEvent(Event event) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement(
                    "INSERT INTO event (`date`, `union_id`, `event_type_id`) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS
            );

            pstmt.setDate(1, new java.sql.Date(event.getEventDate().getTime()));
            pstmt.setString(2, event.getUnion().getUnionID());
            pstmt.setString(3, event.getEventType().getEventTypeID());

            int res = executePreparedStatementUpdate(pstmt);

            if (res != 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    event.setEventID(generatedKeys.getInt(1));
                }
                else {
                    System.out.println("Could not get generated ID!");
                }
            }
            else {
                event = getEvent(event.getUnion().getUnionID(), event.getEventDate());
            }

            return res != 0;
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
