package dashboard.models;

import java.sql.*;

public class DatabaseManager {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/ads";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "2012";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
    }
}
