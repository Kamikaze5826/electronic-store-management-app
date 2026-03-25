package com.electrostore.ui;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;

public final class ModernTheme {

    public static final Color BACKGROUND = new Color(243, 246, 251);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_ALT = new Color(248, 250, 254);
    public static final Color PRIMARY = new Color(15, 118, 110);
    public static final Color PRIMARY_DARK = new Color(13, 95, 89);
    public static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    public static final Color TEXT_SECONDARY = new Color(75, 85, 99);
    public static final Color BORDER = new Color(218, 226, 237);

    private ModernTheme() {
    }

    public static void applyGlobalTheme() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.foreground", TEXT_PRIMARY);
        UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("TabbedPane.selected", SURFACE);
        UIManager.put("TabbedPane.contentAreaColor", SURFACE);
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
    }

    public static Border createPanelBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );
    }

    public static void styleSectionPanel(JPanel panel) {
        panel.setBackground(SURFACE);
        panel.setBorder(createPanelBorder());
    }

    public static void stylePrimaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setRolloverEnabled(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(SURFACE_ALT);
        button.setForeground(TEXT_PRIMARY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
    }

    public static void styleSecondaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setRolloverEnabled(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(SURFACE_ALT);
        button.setForeground(TEXT_PRIMARY);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)
        ));
    }

    public static void styleInput(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(SURFACE);
    }

    public static void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
            styleInput(defaultEditor.getTextField());
        }
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER);
        table.setSelectionBackground(new Color(224, 242, 241));
        table.setSelectionForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(233, 239, 248));
        header.setForeground(TEXT_PRIMARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }
}
