package com.electrostore.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

public class MainFrame extends JFrame {

    private static final String HOME_CARD = "home";
    private static final String MANAGEMENT_CARD = "management";
    private static final String DATABASE_CARD = "database";

    public MainFrame() {
        setTitle("Quan ly cua hang do dien tu");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        CardLayout cardLayout = new CardLayout();
        JPanel rootPanel = new JPanel(cardLayout);

        JPanel homePanel = createHomePanel(cardLayout, rootPanel);
        JPanel managementPanel = createManagementPanel(cardLayout, rootPanel);
        JPanel databasePanel = createDatabasePanel(cardLayout, rootPanel);

        rootPanel.add(homePanel, HOME_CARD);
        rootPanel.add(managementPanel, MANAGEMENT_CARD);
        rootPanel.add(databasePanel, DATABASE_CARD);

        add(rootPanel, BorderLayout.CENTER);
    }

    private JPanel createHomePanel(CardLayout cardLayout, JPanel rootPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(80, 120, 80, 120));

        JLabel titleLabel = new JLabel("HE THONG QUAN LY CUA HANG DIEN TU");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 34));

        JLabel subtitleLabel = new JLabel("Quan ly san pham, khach hang va ban hang");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));

        JButton manageButton = new JButton("Quan ly cua hang");
        manageButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        manageButton.setPreferredSize(new Dimension(220, 46));
        manageButton.setMaximumSize(new Dimension(220, 46));
        manageButton.setFocusPainted(false);
        manageButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        manageButton.addActionListener(e -> cardLayout.show(rootPanel, MANAGEMENT_CARD));

        JButton databaseButton = new JButton("Quan ly co so du lieu");
        databaseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        databaseButton.setPreferredSize(new Dimension(220, 46));
        databaseButton.setMaximumSize(new Dimension(220, 46));
        databaseButton.setFocusPainted(false);
        databaseButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        databaseButton.addActionListener(e -> cardLayout.show(rootPanel, DATABASE_CARD));

        panel.add(Box.createVerticalGlue());
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        panel.add(subtitleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 36)));
        panel.add(manageButton);
        panel.add(Box.createRigidArea(new Dimension(0, 16)));
        panel.add(databaseButton);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createManagementPanel(CardLayout cardLayout, JPanel rootPanel) {
        JPanel panel = new JPanel(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        DashboardPanel dashboardPanel = new DashboardPanel();
        ProductPanel productPanel = new ProductPanel(dashboardPanel::refreshStats);
        CustomerPanel customerPanel = new CustomerPanel(dashboardPanel::refreshStats);
        OrderPanel orderPanel = new OrderPanel(dashboardPanel::refreshStats);

        tabbedPane.addTab("Tong quan", dashboardPanel);
        tabbedPane.addTab("San pham", productPanel);
        tabbedPane.addTab("Khach hang", customerPanel);
        tabbedPane.addTab("Ban hang", orderPanel);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        JButton homeButton = new JButton("Trang chu");
        homeButton.setFocusPainted(false);
        homeButton.addActionListener(e -> cardLayout.show(rootPanel, HOME_CARD));
        topBar.add(homeButton);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDatabasePanel(CardLayout cardLayout, JPanel rootPanel) {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        JButton homeButton = new JButton("Trang chu");
        homeButton.setFocusPainted(false);
        homeButton.addActionListener(e -> cardLayout.show(rootPanel, HOME_CARD));
        topBar.add(homeButton);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(new DatabaseManagementPanel(), BorderLayout.CENTER);

        return panel;
    }
}
