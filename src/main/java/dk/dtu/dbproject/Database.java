package dk.dtu.dbproject;

import com.google.common.io.Resources;
import org.checkerframework.checker.units.qual.A;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Database {
    private Connection conn;

    /**
     * Creates a new database connection.
     *
     * @param host     The address of the server
     * @param port     The port of the server
     * @param username The MySQL username
     * @param password The MySQL password
     * @throws SQLException
     */
    public Database(String host, int port, String username, String password) {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
        } catch (SQLException e) {
            System.out.println("Could not register MySQL driver...");
            e.printStackTrace();
            return;
        }

        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port, username, password);
        } catch (SQLException e) {
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

    public void startTransaction() {
        try {
            Statement stmt = this.conn.createStatement();
            stmt.executeQuery("START TRANSACTION");
        } catch (SQLException e) {
            System.out.println("Could not start transaction...");
            e.printStackTrace();
            return;
        }
    }

    public void endTransaction() {
        try {
            Statement stmt = this.conn.createStatement();
            stmt.executeQuery("COMMIT");
        } catch (SQLException e) {
            System.out.println("Could not start transaction...");
            e.printStackTrace();
            return;
        }
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
        } catch (SQLException e) {
            System.out.println("Could not create statement...");
            e.printStackTrace();
            return;
        }

        try {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Could not execute statement...");
            e.printStackTrace();
            return;
        }

        System.out.println("Query executed!");
    }

    private void init() {
        String sql_script;
        try {
            URL url = Resources.class.getClassLoader().getResource("database_init.sql");
            List<String> sql_lines = new BufferedReader(new InputStreamReader(url.openConnection().getInputStream(), StandardCharsets.UTF_8)).lines().collect(Collectors.toList());

            sql_script = String.join(" ", sql_lines).replaceAll("\\s\\s+", " ");
        } catch (Exception e) {
            System.out.println("fuck");
            e.printStackTrace();
            return;
        }

        String delimiter = ";";
        String[] sqlDelimited = {sql_script};
        if (sql_script.toUpperCase().contains("DELIMITER")) {
            sqlDelimited = sql_script.split("DELIMITER ");
        }

        for (String sqlScript : sqlDelimited) {
            String possibleDelimiter = sqlScript.split(" ")[0].toUpperCase();
            if (!possibleDelimiter.equals("SET") && !possibleDelimiter.equals("CREATE") &&
                    !possibleDelimiter.equals("USE") && !possibleDelimiter.equals("COMMIT")
                    && !possibleDelimiter.equals("START")) {
                delimiter = possibleDelimiter.trim();
                System.out.println("Changed delimiter to: " + delimiter);
                ArrayList<String> regexArray = new ArrayList<>();
                for (int i = 0; i < delimiter.length(); i++) {
                    regexArray.add("\\"+delimiter.charAt(i));
                }
                delimiter = String.join("", regexArray);
                sqlScript = sqlScript.replaceFirst(delimiter, "");
            }


            String[] sql = sqlScript.split(delimiter);
            for (int i = 0; i < sql.length; i++) {
                sql[i] = sql[i].trim();
                if (sql[i] != null && !sql[i].equals("")) {
                    executeUpdate(sql[i]);
                }
            }
        }
    }

    private int executePreparedStatementUpdate(PreparedStatement pstmt) {
        System.out.println("Executing " + pstmt.toString().replace("com.mysql.cj.jdbc.ClientPreparedStatement: ", ""));
        try {
            return pstmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Duplicate entry - " + e);
        } catch (SQLException e) {
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
            } else {
                pstmt.setBoolean(6, gender);
            }
            int res = executePreparedStatementUpdate(pstmt);
            return res != 0;
        } catch (SQLException e) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Contender[] getEventContenders(Event event) {
        if (event == null) {
            return new Contender[0];
        }

        try {
            PreparedStatement pstmt = this.conn.prepareStatement("SELECT * from contender WHERE `event_date` = ? && `union_id` = ? && `event_type_id` = ?");
            pstmt.setDate(1, new java.sql.Date(event.getEventDate().getTime()));
            pstmt.setString(2, event.getUnion().getUnionID());
            pstmt.setString(3, event.getEventType().getEventTypeID());
            System.out.println(pstmt.toString());
            ResultSet rs = pstmt.executeQuery();
            ArrayList<Contender> contenders = new ArrayList<>();
            while (rs.next()) {
                User user = getUser(rs.getString("user_email"));

                Contender contender = new Contender(
                        user, event
                );
                contenders.add(contender);
            }
            return contenders.toArray(new Contender[0]);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public Contender[] getContenders() {
        try {
            Statement stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from contender");
            ArrayList<Contender> contenders = new ArrayList<>();
            while (rs.next()) {
                User user = getUser(rs.getString("user_email"));
                Event event = getEvent(rs.getDate("event_date"), getUnion(rs.getString("union_id")), new EventType(rs.getString("event_type_id")));

                Contender contender = new Contender(
                        user, event
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

    public EventType[] getEventTypes() {
        try {
            Statement stmt = this.conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * from event_type");
            ArrayList<EventType> eventTypes = new ArrayList<>();
            while (rs.next()) {
                eventTypes.add(new EventType(rs.getString("ID")));
            }
            return eventTypes.toArray(new EventType[0]);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Event getEvent(java.util.Date date, Union union, EventType eventType) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("SELECT * from event WHERE `date` = ? && `union_id` = ? && `event_type_id` = ?");
            pstmt.setDate(1, new java.sql.Date(date.getTime()));
            pstmt.setString(2, union.getUnionID());
            pstmt.setString(3, eventType.getEventTypeID());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Event event = new Event(
                        rs.getDate("date"),
                        getUnion(rs.getString("union_id")),
                        new EventType(rs.getString("event_type_id"))
                );
                return event;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


//    private Event getEvent(String unionID, java.util.Date eventDate) {
//        try {
//            PreparedStatement pstmt = this.conn.prepareStatement("SELECT * from event WHERE `union_id` = ? && `date` = ?");
//            pstmt.setString(1, unionID);
//            pstmt.setDate(2, new java.sql.Date(eventDate.getTime()));
//            ResultSet rs = pstmt.executeQuery();
//            if (rs.next()) {
//                Event event = new Event(
//                        rs.getDate("date"),
//                        getUnion(rs.getString("union_id")),
//                        new EventType(rs.getString("event_type_id"))
//                );
//                return event;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }

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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addContender(Contender contender) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("INSERT INTO contender (`unique_event_id`, `user_email`, `event_date`, `union_id`, `event_type_id`) VALUES (?, ?, ?, ?, ?)");

            pstmt.setInt(1, getEventContenders(contender.getEvent()).length + 1);
            pstmt.setString(2, contender.getUser().getEmail());
            pstmt.setDate(3, new java.sql.Date(contender.getEvent().getEventDate().getTime()));
            pstmt.setString(4, contender.getEvent().getUnion().getUnionID());
            pstmt.setString(5, contender.getEvent().getEventType().getEventTypeID());

            int res = executePreparedStatementUpdate(pstmt);
            return res != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean updateContenderTime(Contender contender) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("UPDATE contender SET `time` = ? WHERE `user_email` = ? && `event_date` = ? && `union_id` = ? && `event_type_id` = ?");

            pstmt.setInt(1, contender.getTime());
            pstmt.setString(2, contender.getUser().getEmail());
            pstmt.setDate(3, new java.sql.Date(contender.getEvent().getEventDate().getTime()));
            pstmt.setString(4, contender.getEvent().getUnion().getUnionID());
            pstmt.setString(5, contender.getEvent().getEventType().getEventTypeID());

            int res = executePreparedStatementUpdate(pstmt);
            return res != 0;
        } catch (SQLException e) {
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
        } catch (SQLException e) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addEvent(Event event) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("INSERT INTO event (`date`, `union_id`, `event_type_id`) VALUES (?, ?, ?)");

            pstmt.setDate(1, new java.sql.Date(event.getEventDate().getTime()));
            pstmt.setString(2, event.getUnion().getUnionID());
            pstmt.setString(3, event.getEventType().getEventTypeID());

            int res = executePreparedStatementUpdate(pstmt);
            return res != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addAgeGroup(AgeGroup ageGroup) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("INSERT INTO age_group (`lower_age`, `upper_age`) VALUES (?, ?)");

            pstmt.setInt(1, ageGroup.getLowerAge());
            pstmt.setInt(2, ageGroup.getUpperAge());

            int res = executePreparedStatementUpdate(pstmt);
            return res != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteContender(Contender contender) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("DELETE FROM contender WHERE `user_email` = ? AND `event_data` = ? AND `union_id` = ? AND `event_type_id` = ?");
            pstmt.setString(1, contender.getUser().getEmail());
            pstmt.setDate(2, new java.sql.Date(contender.getEvent().getEventDate().getTime()));
            pstmt.setString(3, contender.getEvent().getUnion().getUnionID());
            pstmt.setString(4, contender.getEvent().getEventType().getEventTypeID());
            ResultSet rs = pstmt.executeQuery();
            int res = executePreparedStatementUpdate(pstmt);
            return res != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean addEventTypeAgeGroup(EventType eventType, AgeGroup ageGroup, Boolean gender) {
        try {
            PreparedStatement pstmt = this.conn.prepareStatement("INSERT INTO event_type_age_group (`event_type_id`, `lower_age`, `upper_age`, `gender`) VALUES (?, ?, ?, ?)");

            pstmt.setString(1, eventType.getEventTypeID());
            pstmt.setInt(2, ageGroup.getLowerAge());
            pstmt.setInt(3, ageGroup.getUpperAge());
            if (gender == null) {
                pstmt.setNull(4, Types.BOOLEAN);
            }
            else {
                pstmt.setBoolean(4, gender);
            }

            int res = executePreparedStatementUpdate(pstmt);
            return res != 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteUser(User user){
        try{
            PreparedStatement pstmt = this.conn.prepareStatement("DELETE FROM contender WHERE `email` = ?");
            pstmt.setString(1, user.getEmail());
            ResultSet rs = pstmt.executeQuery();
            int res = executePreparedStatementUpdate(pstmt);
            return res != 0;
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    public AgeGroup getAgeGroup(Contender contender) {
        java.util.Date date = new java.util.Date();
        java.util.Date birthDate = contender.getUser().getBirthdate();
        LocalDate birthDay = Instant.ofEpochMilli(birthDate.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate currentDay = Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        int age = Period.between(birthDay, currentDay).getYears();

        //reparedStatement pstmt = this.conn.prepareStatement("");


        AgeGroup ag = new AgeGroup(10, 50);

        return null;
    }

}
