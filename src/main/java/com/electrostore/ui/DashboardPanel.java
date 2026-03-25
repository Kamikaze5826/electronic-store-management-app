package com.electrostore.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.electrostore.dao.DashboardDao;

public class DashboardPanel extends JPanel {

    private final DashboardDao dashboardDao = new DashboardDao();

    private final JLabel productCountLabel = createValueLabel();
    private final JLabel customerCountLabel = createValueLabel();
    private final JLabel orderCountLabel = createValueLabel();
    private final JLabel totalRevenueLabel = createValueLabel();
    private final JLabel topCustomerLabel = createValueLabel();
    private final JLabel topProductLabel = createValueLabel();

    public DashboardPanel() {
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 247, 250));

        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 16, 16));
        statsPanel.setOpaque(false);
        statsPanel.add(createCard("So san pham", productCountLabel));
        statsPanel.add(createCard("So khach hang", customerCountLabel));
        statsPanel.add(createCard("So hoa don", orderCountLabel));
        statsPanel.add(createCard("Tong doanh thu", totalRevenueLabel));
        statsPanel.add(createCard("Khach mua nhieu nhat (theo tien)", topCustomerLabel));
        statsPanel.add(createCard("Hang ban chay nhat", topProductLabel));
        add(statsPanel, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Lam moi thong ke");
        refreshBtn.addActionListener(e -> refreshStats());

        JPanel buttonHost = new JPanel();
        buttonHost.setOpaque(false);
        buttonHost.add(refreshBtn);
        add(buttonHost, BorderLayout.SOUTH);

        loadStats();
    }

    public void refreshStats() {
        loadStats();
    }

    private void loadStats() {
        try {
            NumberFormat vnCurrency = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            productCountLabel.setText(String.valueOf(dashboardDao.countProducts()));
            customerCountLabel.setText(String.valueOf(dashboardDao.countCustomers()));
            orderCountLabel.setText(String.valueOf(dashboardDao.countOrders()));
            totalRevenueLabel.setText(vnCurrency.format(dashboardDao.totalRevenue()));
            topCustomerLabel.setText(dashboardDao.topCustomerByTotalSpent());
            topProductLabel.setText(dashboardDao.topProductBySoldQuantity());
        } catch (Exception ex) {
            productCountLabel.setText("--");
            customerCountLabel.setText("--");
            orderCountLabel.setText("--");
            totalRevenueLabel.setText("--");
            topCustomerLabel.setText("Chua co du lieu");
            topProductLabel.setText("Chua co du lieu");
        }
    }

    private JPanel createCard(String title, JLabel valueLabel) {
        JPanel card = new JPanel(new GridLayout(2, 1));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 226, 235)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        card.setPreferredSize(new Dimension(260, 140));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        card.add(titleLabel);
        card.add(valueLabel);

        return card;
    }

    private JLabel createValueLabel() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        return label;
    }
}
