package com.accountantapp;

import javax.swing.table.DefaultTableModel;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.sql.*;
import java.util.Properties;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountantDatabaseHandler {
    private static final Logger LOGGER = Logger.getLogger(AccountantDatabaseHandler.class.getName());
    private Connection connection;
    private String dbURL;
    private String userHost;

    public void connect(String username, String password) throws IOException, SQLException {
        Properties dbProps = new Properties();

        try {
            // Load database properties
            String dbPropertiesFilePath = "properties/operationslog.properties";
            try (FileInputStream fis = new FileInputStream(dbPropertiesFilePath)) {
                dbProps.load(fis);
            }
            LOGGER.info("Loaded database properties from: " + dbPropertiesFilePath);

            dbURL = dbProps.getProperty("db.url");
            if (dbURL == null || dbURL.isEmpty()) {
                throw new IOException("Database URL is missing or empty in " + dbPropertiesFilePath);
            }
            LOGGER.info("Database URL: " + dbURL);

            // Connect to the database
            LOGGER.info("Attempting to connect to database with user: " + username);
            connection = DriverManager.getConnection(dbURL, username, password);
            LOGGER.info("Successfully connected to database.");

            // Set userHost
            String hostname = InetAddress.getLocalHost().getHostName();
            userHost = username + "@" + hostname;
            LOGGER.info("Set userHost to: " + userHost);

        } catch (IOException | SQLException e) {
            LOGGER.log(Level.SEVERE, "Error in connect method: " + e.getMessage(), e);
            throw e;
        }
    }

    public String getDbURL() {
        return dbURL;
    }

    public Object executeSQLCommand(String sqlCommand) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            if (sqlCommand.trim().toUpperCase().startsWith("SELECT")) {
                ResultSet rs = stmt.executeQuery(sqlCommand);
                return buildTableModel(rs);
            } else {
                int rowsAffected = stmt.executeUpdate(sqlCommand);
                logOperation(sqlCommand);
                return rowsAffected;
            }
        }
    }

    private DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        Vector<String> columnNames = new Vector<>();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnLabel(i));
        }

        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> row = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                row.add(rs.getObject(i));
            }
            data.add(row);
        }

        return new DefaultTableModel(data, columnNames);
    }

    private void logOperation(String sqlCommand) {
        // Implementation for logging operations
        // This method would typically insert a record into a logging table
        // For this example, we'll just log to the console
        LOGGER.info("Executed SQL command: " + sqlCommand + " by user: " + userHost);
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            LOGGER.info("Database connection closed.");
        }
    }
}