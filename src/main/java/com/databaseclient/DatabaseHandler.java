package com.databaseclient;

import javax.swing.table.DefaultTableModel;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseHandler {
    private static final Logger LOGGER = Logger.getLogger(DatabaseHandler.class.getName());
    private Connection connection; // Connection to main database
    private String dbURL;
    private String userHost; // Stores login_username (e.g., client1@hostname)

    // Connect to the main database using user-supplied credentials
    public void connect(String dbPropertiesFile, String userPropertiesFile, String username, String password) throws IOException, SQLException {
        Properties dbProps = new Properties();

        try {
            // Load database properties
            String dbPropertiesFilePath = "properties/" + dbPropertiesFile;
            try (FileInputStream fis = new FileInputStream(dbPropertiesFilePath)) {
                dbProps.load(fis);
            }
            LOGGER.info("Loaded database properties from: " + dbPropertiesFilePath);

            dbURL = dbProps.getProperty("db.url");
            if (dbURL == null || dbURL.isEmpty()) {
                throw new IOException("Database URL is missing or empty in " + dbPropertiesFilePath);
            }
            LOGGER.info("Database URL: " + dbURL);

            // Use user-supplied credentials to connect to the main database
            LOGGER.info("Attempting to connect to main database with user: " + username);
            connection = DriverManager.getConnection(dbURL, username, password);
            LOGGER.info("Successfully connected to main database.");

            // Set userHost dynamically
            String hostname = InetAddress.getLocalHost().getHostName();
            userHost = username + "@" + "localhost";

            LOGGER.info("Set userHost to: " + userHost);

        } catch (IOException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in connect method: " + e.getMessage(), e);
            throw e;
        }
    }

    public String getDbURL() {
        return dbURL;
    }

    // Execute SQL command (query or update)
    public Object executeSQLCommand(String sqlCommand) throws SQLException {
        Statement stmt = null;
        Object result;

        try {
            stmt = connection.createStatement();

            // Check if the command is a SELECT query
            if (sqlCommand.trim().toUpperCase().startsWith("SELECT")) {
                ResultSet rs = stmt.executeQuery(sqlCommand);
                result = buildTableModel(rs);
            } else {
                // It's an update command
                int rowsAffected = stmt.executeUpdate(sqlCommand);
                result = rowsAffected;
            }

            // Log the operation
            logOperation(sqlCommand);

        } finally {
            if (stmt != null) {
                stmt.close();
            }
        }

        return result;
    }

    // Build a DefaultTableModel from a ResultSet
    private DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        Vector<String> columnNames = new Vector<>();
        Vector<Vector<Object>> data = new Vector<>();

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Column names
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnLabel(i));
        }

        // Data rows
        while (rs.next()) {
            Vector<Object> row = new Vector<>(columnCount);
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            data.add(row);
        }

        return new DefaultTableModel(data, columnNames);
    }

    // Log the operation to the operationslog database
    private void logOperation(String sqlCommand) {
        Connection logConnection = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Load properties for logging operations
            Properties props = new Properties();
            String logPropertiesFilePath = "properties/operationslog.properties";
            try (FileInputStream fis = new FileInputStream(logPropertiesFilePath)) {
                props.load(fis);
            }
            LOGGER.info("Loaded operationslog properties from: " + logPropertiesFilePath);

            String logDbURL = props.getProperty("db.url");
            String logDbUsername = props.getProperty("db.username");
            String logDbPassword = props.getProperty("db.password");

            if (logDbURL == null || logDbURL.isEmpty() || logDbUsername == null || logDbUsername.isEmpty() || logDbPassword == null) {
                throw new IOException("Logging database credentials are missing in " + logPropertiesFilePath);
            }

            // Connect to operationslog database using project3app credentials
            logConnection = DriverManager.getConnection(logDbURL, logDbUsername, logDbPassword);
            LOGGER.info("Connected to operationslog database for logging.");

            // Determine the operation type
            String operationType = sqlCommand.trim().toUpperCase().startsWith("SELECT") ? "num_queries" : "num_updates";

            // Check if the user already exists in the operationscount table
            String checkUserSQL = "SELECT * FROM operationscount WHERE login_username = ?";
            pstmt = logConnection.prepareStatement(checkUserSQL);
            pstmt.setString(1, userHost);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                // Update existing record
                String updateSQL = "UPDATE operationscount SET " + operationType + " = " + operationType + " + 1 WHERE login_username = ?";
                pstmt = logConnection.prepareStatement(updateSQL);
                pstmt.setString(1, userHost);
                int rowsUpdated = pstmt.executeUpdate();
                LOGGER.info("Updated " + operationType + " for user " + userHost + ". Rows affected: " + rowsUpdated);
            } else {
                // Insert new record
                String insertSQL = "INSERT INTO operationscount (login_username, num_queries, num_updates) VALUES (?, ?, ?)";
                pstmt = logConnection.prepareStatement(insertSQL);
                pstmt.setString(1, userHost);
                pstmt.setInt(2, operationType.equals("num_queries") ? 1 : 0);
                pstmt.setInt(3, operationType.equals("num_updates") ? 1 : 0);
                int rowsInserted = pstmt.executeUpdate();
                LOGGER.info("Inserted new record for user " + userHost + ". Rows inserted: " + rowsInserted);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error logging operation: " + e.getMessage(), e);
            // Optionally, handle the error as needed
        } finally {
            // Close resources
            try { if (rs != null) rs.close(); } catch (SQLException e) { /* ignored */ }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { /* ignored */ }
            try { if (logConnection != null) logConnection.close(); } catch (SQLException e) { /* ignored */ }
        }
    }

    // Close the database connection
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            LOGGER.info("Main database connection closed.");
        }
    }
}
