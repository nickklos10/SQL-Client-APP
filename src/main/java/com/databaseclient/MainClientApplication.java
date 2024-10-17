package com.databaseclient;

public class MainClientApplication {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            ClientGUI clientGUI = new ClientGUI();
            clientGUI.createAndShowGUI();
        });

    }
}

