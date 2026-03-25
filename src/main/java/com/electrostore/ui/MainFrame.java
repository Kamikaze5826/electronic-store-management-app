package com.electrostore.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

public class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Quan ly cua hang do dien tu");
        setSize(1200, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();

        DashboardPanel dashboardPanel = new DashboardPanel();
        ProductPanel productPanel = new ProductPanel(dashboardPanel::refreshStats);
        CustomerPanel customerPanel = new CustomerPanel(dashboardPanel::refreshStats);
        OrderPanel orderPanel = new OrderPanel(dashboardPanel::refreshStats);

        tabbedPane.addTab("Tong quan", dashboardPanel);
        tabbedPane.addTab("San pham", productPanel);
        tabbedPane.addTab("Khach hang", customerPanel);
        tabbedPane.addTab("Ban hang", orderPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }
}