package com.databaseclient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class ClientGUI {
    private JFrame frame;
    private JTextArea sqlCommandArea;
    private JTable resultsTable;
    private JLabel connectionStatusLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> dbPropertiesComboBox;
    private JComboBox<String> userPropertiesComboBox;
    private DatabaseHandler dbHandler;

    public void createAndShowGUI() {
        frame = new JFrame("SQL Client Application - CNT4714 - UCF - Project3");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        dbHandler = new DatabaseHandler();

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Upper Panel
        JPanel upperPanel = new JPanel(new GridLayout(1, 2));

        // Left Panel - Connection Details
        JPanel connectionPanel = createConnectionPanel();

        // Right Panel - SQL Command
        JPanel commandPanel = createCommandPanel();

        upperPanel.add(connectionPanel);
        upperPanel.add(commandPanel);

        // Middle Panel - Connection Status
        JPanel statusPanel = createStatusPanel();

        // Bottom Panel - Results
        JPanel resultsPanel = createResultsPanel();

        mainPanel.add(upperPanel, BorderLayout.NORTH);
        mainPanel.add(statusPanel, BorderLayout.CENTER);
        mainPanel.add(resultsPanel, BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);

        frame.pack();
        frame.setVisible(true);
    }

    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        // Connection Details Label
        JLabel connectionDetailsLabel = new JLabel("Connection Details");
        connectionDetailsLabel.setForeground(Color.BLACK);
        connectionDetailsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(connectionDetailsLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Add some vertical space
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // DB URL Properties
        JPanel dbPropertiesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel dbPropertiesLabel = new JLabel("DB URL Properties:");
        dbPropertiesComboBox = new JComboBox<>(new String[]{
                "project3.properties",
                "bikedb.properties"
        });
        dbPropertiesPanel.add(dbPropertiesLabel);
        dbPropertiesPanel.add(dbPropertiesComboBox);

        // User Properties
        JPanel userPropertiesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel userPropertiesLabel = new JLabel("User Properties:");
        userPropertiesComboBox = new JComboBox<>(new String[]{
                "root.properties",
                "client1.properties",
                "client2.properties"
        });
        userPropertiesPanel.add(userPropertiesLabel);
        userPropertiesPanel.add(userPropertiesComboBox);

        // Username Field
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(15);
        usernamePanel.add(usernameLabel);
        usernamePanel.add(usernameField);

        // Password Field
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(15);
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        contentPanel.add(dbPropertiesPanel);
        contentPanel.add(userPropertiesPanel);
        contentPanel.add(usernamePanel);
        contentPanel.add(passwordPanel);

        // Add some vertical space
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        panel.add(contentPanel, BorderLayout.CENTER);

        // Connect and Disconnect Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton connectButton = createStyledButton("Connect to Database", Color.BLUE);
        JButton disconnectButton = createStyledButton("Disconnect from Database", Color.RED);

        // Corrected ActionListener Implementation
        connectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToDatabase();
            }
        });

        disconnectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnectFromDatabase();
            }
        });

        buttonPanel.add(connectButton);
        buttonPanel.add(disconnectButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCommandPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel commandLabel = new JLabel("Enter SQL Command");
        commandLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(commandLabel, BorderLayout.NORTH);

        sqlCommandArea = new JTextArea(10, 30);
        JScrollPane scrollPane = new JScrollPane(sqlCommandArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clearButton = createStyledButton("Clear SQL Command", Color.YELLOW);
        JButton executeButton = createStyledButton("Execute SQL Command", Color.GREEN);

        executeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                executeSQLCommand();
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sqlCommandArea.setText("");
            }
        });
        buttonPanel.add(clearButton);
        buttonPanel.add(executeButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 30)); // Set a fixed height

        connectionStatusLabel = new JLabel("Not Connected");
        connectionStatusLabel.setForeground(Color.WHITE);
        connectionStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        panel.add(connectionStatusLabel);

        return panel;
    }

    private JPanel createResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel resultsLabel = new JLabel("SQL Execution Result Window");
        resultsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

        resultsTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(resultsTable);

        // Clear Results and Close Application Buttons
        JPanel buttonPanel = new JPanel(new BorderLayout());
        JButton clearResultsButton = createStyledButton("Clear Result Window", Color.YELLOW);
        JButton closeButton = createStyledButton("Close Application", Color.RED);

        clearResultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearResultsTable();
            }
        });
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeApplication();
            }
        });

        buttonPanel.add(clearResultsButton, BorderLayout.WEST);
        buttonPanel.add(closeButton, BorderLayout.EAST);

        panel.add(resultsLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.BLACK);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);  // This removes the focus ring
        return button;
    }

    private void connectToDatabase() {
        String dbPropertiesFile = (String) dbPropertiesComboBox.getSelectedItem();
        String userPropertiesFile = (String) userPropertiesComboBox.getSelectedItem();
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            System.out.println("Attempting to connect with:");
            System.out.println("DB Properties: " + dbPropertiesFile);
            System.out.println("User Properties: " + userPropertiesFile);
            System.out.println("Username: " + username);

            dbHandler.connect(dbPropertiesFile, userPropertiesFile, username, password);

            String dbURL = dbHandler.getDbURL();
            connectionStatusLabel.setText("CONNECTED TO: " + dbURL);
            connectionStatusLabel.setForeground(Color.WHITE);
            System.out.println("Successfully connected to: " + dbURL);
        } catch (Exception e) {
            connectionStatusLabel.setText("NOT CONNECTED - User Credentials Do not Match Properties File!");
            connectionStatusLabel.setForeground(Color.RED);
            StringBuilder errorMessage = new StringBuilder("Failed to connect to database: " + e.getMessage() + "\n\n");
            errorMessage.append("Exception type: ").append(e.getClass().getName()).append("\n\n");
            JOptionPane.showMessageDialog(frame, errorMessage.toString(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            System.err.println(errorMessage.toString());
        }
    }

    private void disconnectFromDatabase() {
        try {
            dbHandler.closeConnection();
            connectionStatusLabel.setText("Not Connected");
            connectionStatusLabel.setForeground(Color.WHITE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "Failed to disconnect: " + e.getMessage(),
                    "Disconnection Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void executeSQLCommand() {
        String sqlCommand = sqlCommandArea.getText().trim();

        if (sqlCommand.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter an SQL command.", "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Object result = dbHandler.executeSQLCommand(sqlCommand);

            if (result instanceof DefaultTableModel) {
                // It's a query result
                resultsTable.setModel((DefaultTableModel) result);
            } else if (result instanceof Integer) {
                // It's the number of rows affected
                int rowsAffected = (Integer) result;
                JOptionPane.showMessageDialog(frame, "Successful Update... " + rowsAffected + " row(s) updated.", "Execution Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                // No result (e.g., after a USE command)
                JOptionPane.showMessageDialog(frame, "Command executed successfully.", "Execution Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            // Logging is already handled in DatabaseHandler.executeSQLCommand()

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(frame, "SQL Error: " + e.getMessage(), "Execution Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearResultsTable() {
        resultsTable.setModel(new DefaultTableModel());
    }

    private void closeApplication() {
        try {
            dbHandler.closeConnection();
        } catch (SQLException e) {
            // Optionally, log the exception
            System.err.println("Error while closing connections: " + e.getMessage());
        }
        frame.dispose();
        System.exit(0);
    }
}
