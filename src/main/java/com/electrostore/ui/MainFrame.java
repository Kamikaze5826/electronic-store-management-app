package com.electrostore.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
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
        ModernTheme.applyGlobalTheme();
        setTitle("Quan ly cua hang do dien tu");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(ModernTheme.BACKGROUND);

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
        panel.setBackground(ModernTheme.BACKGROUND);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(70, 140, 70, 140));

        JPanel heroCard = new JPanel();
        heroCard.setLayout(new BoxLayout(heroCard, BoxLayout.Y_AXIS));
        heroCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        ModernTheme.styleSectionPanel(heroCard);
        heroCard.setBackground(ModernTheme.SURFACE);
        heroCard.setMaximumSize(new Dimension(760, 320));

        JLabel titleLabel = new JLabel("HE THONG QUAN LY CUA HANG DIEN TU");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titleLabel.setForeground(ModernTheme.TEXT_PRIMARY);

        JLabel subtitleLabel = new JLabel("Quan ly san pham, khach hang va ban hang");
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        subtitleLabel.setForeground(ModernTheme.TEXT_SECONDARY);

        JButton manageButton = new JButton("Quan ly cua hang");
        manageButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        manageButton.setPreferredSize(new Dimension(220, 46));
        manageButton.setMaximumSize(new Dimension(220, 46));
        ModernTheme.stylePrimaryButton(manageButton);
        manageButton.addActionListener(e -> openManagementScreen(cardLayout, rootPanel));

        JButton databaseButton = new JButton("Quan ly co so du lieu");
        databaseButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        databaseButton.setPreferredSize(new Dimension(220, 46));
        databaseButton.setMaximumSize(new Dimension(220, 46));
        ModernTheme.styleSecondaryButton(databaseButton);
        databaseButton.addActionListener(e -> cardLayout.show(rootPanel, DATABASE_CARD));

        panel.add(Box.createVerticalGlue());
        heroCard.add(titleLabel);
        heroCard.add(Box.createRigidArea(new Dimension(0, 14)));
        heroCard.add(subtitleLabel);
        heroCard.add(Box.createRigidArea(new Dimension(0, 28)));
        heroCard.add(manageButton);
        heroCard.add(Box.createRigidArea(new Dimension(0, 12)));
        heroCard.add(databaseButton);
        panel.add(heroCard);
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
        panel.setBackground(ModernTheme.BACKGROUND);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(ModernTheme.SURFACE);
        tabbedPane.setForeground(ModernTheme.TEXT_PRIMARY);

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
        topBar.setBackground(new Color(232, 240, 248));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.BORDER));
        JButton homeButton = new JButton("Trang chu");
        ModernTheme.styleSecondaryButton(homeButton);
        JLabel titleLabel = new JLabel("Khu vuc quan ly cua hang");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        homeButton.addActionListener(e -> cardLayout.show(rootPanel, HOME_CARD));
        topBar.add(homeButton);
        topBar.add(titleLabel);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(tabbedPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDatabasePanel(CardLayout cardLayout, JPanel rootPanel) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(ModernTheme.BACKGROUND);

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        topBar.setBackground(new Color(232, 240, 248));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernTheme.BORDER));
        JButton homeButton = new JButton("Trang chu");
        ModernTheme.styleSecondaryButton(homeButton);
        JLabel titleLabel = new JLabel("Quan ly ket noi va khoi tao CSDL");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(ModernTheme.TEXT_PRIMARY);
        homeButton.addActionListener(e -> cardLayout.show(rootPanel, HOME_CARD));
        topBar.add(homeButton);
        topBar.add(titleLabel);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(new DatabaseManagementPanel(), BorderLayout.CENTER);

        return panel;
    }
}
