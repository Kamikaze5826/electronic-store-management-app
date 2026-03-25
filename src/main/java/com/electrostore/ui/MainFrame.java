package com.electrostore.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import com.electrostore.config.DbConnection;

public class MainFrame extends JFrame {

    private static final String HOME_CARD = "home";
    private static final String MANAGEMENT_CARD = "management";
    private static final String DATABASE_CARD = "database";
    private final JPanel managementHost = new JPanel(new BorderLayout());
    private boolean managementInitialized;

    public MainFrame() {
        setTitle("Quan ly cua hang do dien tu");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        CardLayout cardLayout = new CardLayout();
        JPanel rootPanel = new JPanel(cardLayout);

        JPanel homePanel = createHomePanel(cardLayout, rootPanel);
        JPanel databasePanel = createDatabasePanel(cardLayout, rootPanel);

        rootPanel.add(homePanel, HOME_CARD);
        rootPanel.add(managementHost, MANAGEMENT_CARD);
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
        manageButton.addActionListener(e -> openManagementScreen(cardLayout, rootPanel));

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

    private void openManagementScreen(CardLayout cardLayout, JPanel rootPanel) {
        if (!isDatabaseReady()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Chua thiet lap ket noi co so du lieu. Vui long vao \"Quan ly co so du lieu\" de cau hinh.",
                    "Canh bao CSDL",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        if (!ensureManagementInitialized(cardLayout, rootPanel)) {
            return;
        }

        cardLayout.show(rootPanel, MANAGEMENT_CARD);
    }

    private boolean isDatabaseReady() {
        try (Connection ignored = DbConnection.getConnection()) {
            return true;
        } catch (SQLException ex) {
            return false;
        }
    }

    private boolean ensureManagementInitialized(CardLayout cardLayout, JPanel rootPanel) {
        if (managementInitialized) {
            return true;
        }

        try {
            managementHost.add(createManagementPanel(cardLayout, rootPanel), BorderLayout.CENTER);
            managementInitialized = true;
            managementHost.revalidate();
            managementHost.repaint();
            return true;
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Chua the mo giao dien quan ly cua hang. Vui long kiem tra lai ket noi co so du lieu.",
                    "Canh bao CSDL",
                    JOptionPane.WARNING_MESSAGE
            );
            return false;
        }
    }

    private JPanel createManagementPanel(CardLayout cardLayout, JPanel rootPanel) {
        JPanel panel = new JPanel(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        DashboardPanel dashboardPanel = new DashboardPanel();
        final OrderPanel[] orderPanelRef = new OrderPanel[1];
        Runnable onDataChanged = () -> {
            dashboardPanel.refreshStats();
            if (orderPanelRef[0] != null) {
                orderPanelRef[0].refreshData();
            }
        };

        ProductPanel productPanel = new ProductPanel(onDataChanged);
        CustomerPanel customerPanel = new CustomerPanel(onDataChanged);
        OrderPanel orderPanel = new OrderPanel(onDataChanged);
        orderPanelRef[0] = orderPanel;

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
