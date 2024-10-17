package com.accountantapp;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

public class AccountantGUI {
    private JFrame frame;
    private JTextArea sqlCommandArea;
    private JTable resultsTable;
    private JLabel connectionStatusLabel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel dbPropertiesLabel;
    private JLabel userPropertiesLabel;
    private AccountantDatabaseHandler dbHandler;

    public void createAndShowGUI() {
        frame = new JFrame("Accountant Database Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        dbHandler = new AccountantDatabaseHandler();

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel upperPanel = new JPanel(new GridLayout(1, 2));

        upperPanel.add(createConnectionPanel());
        upperPanel.add(createCommandPanel());

        mainPanel.add(upperPanel, BorderLayout.NORTH);
        mainPanel.add(createStatusPanel(), BorderLayout.CENTER);
        mainPanel.add(createResultsPanel(), BorderLayout.SOUTH);

        frame.getContentPane().add(mainPanel);
        frame.pack();
        frame.setVisible(true);
    }

    private JPanel createConnectionPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel connectionDetailsLabel = new JLabel("Connection Details");
        connectionDetailsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(connectionDetailsLabel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // DB URL Properties (non-editable)
        JPanel dbPropertiesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        dbPropertiesPanel.add(new JLabel("DB URL Properties:"));
        dbPropertiesLabel = new JLabel("operationslog.properties");
        dbPropertiesLabel.setForeground(Color.BLUE);
        dbPropertiesPanel.add(dbPropertiesLabel);

        // User Properties (non-editable)
        JPanel userPropertiesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPropertiesPanel.add(new JLabel("User Properties:"));
        userPropertiesLabel = new JLabel("theaccountant.properties");
        userPropertiesLabel.setForeground(Color.BLUE);
        userPropertiesPanel.add(userPropertiesLabel);

        // Username Field
        JPanel usernamePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        usernamePanel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        usernamePanel.add(usernameField);

        // Password Field
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        passwordPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        passwordPanel.add(passwordField);

        contentPanel.add(dbPropertiesPanel);
        contentPanel.add(userPropertiesPanel);
        contentPanel.add(usernamePanel);
        contentPanel.add(passwordPanel);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Connect and Disconnect Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton connectButton = createStyledButton("Connect to Database", Color.BLUE);
        JButton disconnectButton = createStyledButton("Disconnect from Database", Color.RED);

        connectButton.addActionListener(e -> connectToDatabase());
        disconnectButton.addActionListener(e -> disconnectFromDatabase());

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
        panel.add(new JScrollPane(sqlCommandArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clearButton = createStyledButton("Clear SQL Command", Color.YELLOW);
        JButton executeButton = createStyledButton("Execute SQL Command", Color.GREEN);

        executeButton.addActionListener(e -> executeSQLCommand());
        clearButton.addActionListener(e -> sqlCommandArea.setText(""));

        buttonPanel.add(clearButton);
        buttonPanel.add(executeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        panel.setPreferredSize(new Dimension(panel.getPreferredSize().width, 30));

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

        JPanel buttonPanel = new JPanel(new BorderLayout());
        JButton clearResultsButton = createStyledButton("Clear Result Window", Color.YELLOW);
        JButton closeButton = createStyledButton("Close Application", Color.RED);

        clearResultsButton.addActionListener(e -> clearResultsTable());
        closeButton.addActionListener(e -> closeApplication());

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
        button.setFocusPainted(false);
        return button;
    }

    private void connectToDatabase() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try {
            dbHandler.connect(username, password);
            connectionStatusLabel.setText("CONNECTED TO: " + dbHandler.getDbURL());
            connectionStatusLabel.setForeground(Color.GREEN);
        } catch (Exception e) {
            connectionStatusLabel.setText("Connection Failed");
            connectionStatusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(frame, "Failed to connect to database: " + e.getMessage(),
                    "Connection Error", JOptionPane.ERROR_MESSAGE);
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
                resultsTable.setModel((DefaultTableModel) result);
            } else if (result instanceof Integer) {
                int rowsAffected = (Integer) result;
                JOptionPane.showMessageDialog(frame, "Successful Update... " + rowsAffected + " row(s) updated.",
                        "Execution Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(frame, "Command executed successfully.", "Execution Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
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
            System.err.println("Error while closing connections: " + e.getMessage());
        }
        frame.dispose();
        System.exit(0);
    }
}