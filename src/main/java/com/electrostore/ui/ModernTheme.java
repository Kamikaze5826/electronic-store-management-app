package com.electrostore.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;

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

    public static final Color BACKGROUND = new Color(239, 244, 252);
    public static final Color BACKGROUND_ALT = new Color(228, 237, 249);
    public static final Color SURFACE = Color.WHITE;
    public static final Color SURFACE_ALT = new Color(246, 250, 255);
    public static final Color PRIMARY = new Color(24, 94, 224);
    public static final Color PRIMARY_DARK = new Color(19, 72, 171);
    public static final Color PRIMARY_SOFT = new Color(225, 235, 255);
    public static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    public static final Color TEXT_SECONDARY = new Color(75, 85, 99);
    public static final Color BORDER = new Color(205, 219, 240);

    private ModernTheme() {
    }

    public static void applyGlobalTheme() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("Viewport.background", SURFACE);
        UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("Button.foreground", TEXT_PRIMARY);
        UIManager.put("Button.background", SURFACE);
        UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.BOLD, 14));
        UIManager.put("TabbedPane.background", BACKGROUND);
        UIManager.put("TabbedPane.selected", SURFACE);
        UIManager.put("TabbedPane.contentAreaColor", SURFACE);
        UIManager.put("TabbedPane.focus", BACKGROUND);
        UIManager.put("TabbedPane.borderHightlightColor", BORDER);
        UIManager.put("TabbedPane.light", BORDER);
        UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
        UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
        UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 14));
        UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(BORDER));
    }

    public static Border createPanelBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        );
    }

    public static void styleSectionPanel(JPanel panel) {
        panel.setBackground(SURFACE_ALT);
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
        button.setMargin(new Insets(6, 14, 6, 14));
        button.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        button.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        button.setBackground(PRIMARY_SOFT);
        button.setForeground(PRIMARY_DARK);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_DARK, 1),
            BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        ensureButtonHeight(button);
    }

    public static void styleSecondaryButton(JButton button) {
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setRolloverEnabled(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setMargin(new Insets(6, 14, 6, 14));
        button.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
        button.setVerticalTextPosition(javax.swing.SwingConstants.CENTER);
        button.setBackground(SURFACE);
        button.setForeground(TEXT_PRIMARY);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1),
            BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        ensureButtonHeight(button);
    }

    private static void ensureButtonHeight(JButton button) {
        Dimension preferred = button.getPreferredSize();
        int minHeight = 40;
        int adjustedHeight = Math.max(preferred.height, minHeight);
        Dimension size = new Dimension(preferred.width, adjustedHeight);
        button.setPreferredSize(size);
    }

    public static void styleInput(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBackground(SURFACE);
        field.setForeground(TEXT_PRIMARY);
        field.setCaretColor(TEXT_PRIMARY);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
    }

    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        comboBox.setBackground(SURFACE);
        comboBox.setForeground(TEXT_PRIMARY);
        comboBox.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
    }

    public static void styleSpinner(JSpinner spinner) {
        spinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor defaultEditor) {
            styleInput(defaultEditor.getTextField());
        }
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(30);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setGridColor(BORDER);
        table.setBackground(SURFACE);
        table.setSelectionBackground(PRIMARY_SOFT);
        table.setSelectionForeground(TEXT_PRIMARY);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(222, 233, 251));
        header.setForeground(TEXT_PRIMARY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }
}
