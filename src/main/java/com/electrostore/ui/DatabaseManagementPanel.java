package com.electrostore.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.electrostore.config.DatabaseConfig;
import com.electrostore.config.DbConnection;
import com.electrostore.config.DbInitializer;

public class DatabaseManagementPanel extends JPanel {

    private final JTextField hostField;
    private final JFormattedTextField portField;
    private final JTextField dbNameField;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JTextArea logArea;

    public DatabaseManagementPanel() {
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(ModernTheme.BACKGROUND);

        JPanel formPanel = new JPanel(new GridBagLayout());
        ModernTheme.styleSectionPanel(formPanel);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        hostField = new JTextField(DatabaseConfig.getHost());
        portField = new JFormattedTextField(DatabaseConfig.getPort());
        dbNameField = new JTextField(DatabaseConfig.getDbName());
        usernameField = new JTextField(DatabaseConfig.getUsername());
        passwordField = new JPasswordField(DatabaseConfig.getPassword());

        int row = 0;
        addRow(formPanel, gbc, row++, "Host", hostField);
        addRow(formPanel, gbc, row++, "Port", portField);
        addRow(formPanel, gbc, row++, "Ten CSDL", dbNameField);
        addRow(formPanel, gbc, row++, "Tai khoan", usernameField);
        addRow(formPanel, gbc, row++, "Mat khau", passwordField);

        JPanel actionsPanel = new JPanel();
        actionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 8));
        actionsPanel.setOpaque(false);
        JButton testServerButton = new JButton("Kiem tra ket noi XAMPP");
        JButton testDbButton = new JButton("Kiem tra ket noi CSDL");
        JButton saveConfigButton = new JButton("Luu cau hinh ket noi");
        JButton createDbButton = new JButton("Tao CSDL + bang");
        JButton resetDbButton = new JButton("Reset CSDL (xoa het)");
        ModernTheme.styleSecondaryButton(testServerButton);
        ModernTheme.styleSecondaryButton(testDbButton);
        ModernTheme.stylePrimaryButton(saveConfigButton);
        ModernTheme.stylePrimaryButton(createDbButton);
        ModernTheme.styleSecondaryButton(resetDbButton);

        testServerButton.addActionListener(e -> testServerConnection());
        testDbButton.addActionListener(e -> testDatabaseConnection());
        saveConfigButton.addActionListener(e -> saveConnectionConfig());
        createDbButton.addActionListener(e -> createDatabaseSchema());
        resetDbButton.addActionListener(e -> resetDatabaseSchema());

        actionsPanel.add(testServerButton);
        actionsPanel.add(testDbButton);
        actionsPanel.add(saveConfigButton);
        actionsPanel.add(createDbButton);
        actionsPanel.add(resetDbButton);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setPreferredSize(new Dimension(600, 220));
        logScrollPane.setBorder(BorderFactory.createTitledBorder("Nhat ky thao tac"));

        ModernTheme.styleInput(hostField);
        ModernTheme.styleInput(portField);
        ModernTheme.styleInput(dbNameField);
        ModernTheme.styleInput(usernameField);
        ModernTheme.styleInput(passwordField);

        add(formPanel, BorderLayout.NORTH);
        add(actionsPanel, BorderLayout.CENTER);
        add(logScrollPane, BorderLayout.SOUTH);
    }

    private void addRow(JPanel formPanel, GridBagConstraints gbc, int row, String labelText, JTextField textField) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel label = new JLabel(labelText + ":");
        label.setForeground(ModernTheme.TEXT_SECONDARY);
        formPanel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        formPanel.add(textField, gbc);
    }

    private String getHost() {
        return hostField.getText().trim();
    }

    private String getPort() {
        return portField.getText().trim();
    }

    private String getDbName() {
        return dbNameField.getText().trim();
    }

    private String getUsername() {
        return usernameField.getText().trim();
    }

    private String getPassword() {
        return new String(passwordField.getPassword());
    }

    private boolean validateForm() {
        if (getHost().isEmpty() || getPort().isEmpty() || getDbName().isEmpty() || getUsername().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui long nhap day du Host, Port, Ten CSDL, Tai khoan", "Thieu thong tin", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void appendLog(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private void saveConnectionConfig() {
        if (!validateForm()) {
            return;
        }

        DatabaseConfig.update(getHost(), getPort(), getDbName(), getUsername(), getPassword());
        appendLog("Da luu cau hinh ket noi: " + DatabaseConfig.getUrl());
        JOptionPane.showMessageDialog(this, "Da luu cau hinh ket noi", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
    }

    private void testServerConnection() {
        if (!validateForm()) {
            return;
        }

        try (Connection connection = DbConnection.getServerConnection(getHost(), getPort(), getUsername(), getPassword())) {
            connection.isValid(2);
            appendLog("Ket noi XAMPP MySQL thanh cong tai " + DatabaseConfig.buildRootUrl(getHost(), getPort()));
            JOptionPane.showMessageDialog(this, "Ket noi XAMPP thanh cong", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            appendLog("Ket noi XAMPP that bai: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Khong the ket noi XAMPP: " + ex.getMessage(), "Loi ket noi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void testDatabaseConnection() {
        if (!validateForm()) {
            return;
        }

        try (Connection connection = DbConnection.getConnection(getHost(), getPort(), getDbName(), getUsername(), getPassword())) {
            connection.isValid(2);
            appendLog("Ket noi CSDL thanh cong: " + DatabaseConfig.buildDbUrl(getHost(), getPort(), getDbName()));
            JOptionPane.showMessageDialog(this, "Ket noi CSDL thanh cong", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            appendLog("Ket noi CSDL that bai: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Khong the ket noi CSDL: " + ex.getMessage(), "Loi ket noi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createDatabaseSchema() {
        if (!validateForm()) {
            return;
        }

        try {
            DbInitializer.initialize(getHost(), getPort(), getDbName(), getUsername(), getPassword());
            DatabaseConfig.update(getHost(), getPort(), getDbName(), getUsername(), getPassword());
            appendLog("Da tao (hoac cap nhat) CSDL va cac bang thanh cong: " + getDbName());
            JOptionPane.showMessageDialog(this, "Da tao CSDL va bang thanh cong", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            appendLog("Tao CSDL that bai: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Loi tao CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetDatabaseSchema() {
        if (!validateForm()) {
            return;
        }

        int firstConfirm = JOptionPane.showConfirmDialog(
                this,
                "Ban sap xoa toan bo du lieu trong CSDL \"" + getDbName() + "\". Tiep tuc?",
                "Xac nhan reset CSDL",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (firstConfirm != JOptionPane.YES_OPTION) {
            return;
        }

        int secondConfirm = JOptionPane.showConfirmDialog(
                this,
                "Hanh dong nay KHONG the hoan tac. Ban chac chan muon xoa het du lieu?",
                "Xac nhan lan 2",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.ERROR_MESSAGE
        );
        if (secondConfirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            DbInitializer.resetDatabase(getHost(), getPort(), getDbName(), getUsername(), getPassword());
            DatabaseConfig.update(getHost(), getPort(), getDbName(), getUsername(), getPassword());
            appendLog("Da reset CSDL thanh cong: " + getDbName() + " (da xoa het du lieu va tao lai bang)");
            JOptionPane.showMessageDialog(this, "Reset CSDL thanh cong", "Thanh cong", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            appendLog("Reset CSDL that bai: " + ex.getMessage());
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Loi reset CSDL", JOptionPane.ERROR_MESSAGE);
        }
    }
}
