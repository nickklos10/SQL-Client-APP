package com.accountantapp;

public class AccountantApplication {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            AccountantGUI accountantGUI = new AccountantGUI();
            accountantGUI.createAndShowGUI();
        });
    }
}

