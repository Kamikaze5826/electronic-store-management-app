package com.electrostore.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import com.electrostore.dao.CustomerDao;
import com.electrostore.model.Customer;
import com.electrostore.model.OrderItem;
import com.electrostore.model.OrderSummary;
import com.electrostore.service.OrderService;

public class CustomerPanel extends JPanel {
    private final CustomerDao customerDao = new CustomerDao();
    private final OrderService orderService = new OrderService();
    private final Runnable onDataChanged;
    private final NumberFormat vnCurrency = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final JTextField idField = new JTextField();
    private final JTextField fullNameField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField addressField = new JTextField();
    private final JTextField searchField = new JTextField(20);

    private final DefaultTableModel tableModel = new DefaultTableModel(
        new String[]{"ID", "Ho ten", "Dien thoai", "Email", "Dia chi"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    private final DefaultTableModel orderHistoryModel = new DefaultTableModel(
        new String[]{"Ma HD", "Tong tien", "Thoi gian"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel invoiceDetailModel = new DefaultTableModel(
        new String[]{"San pham", "So luong", "Don gia", "Thanh tien"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable orderHistoryTable = new JTable(orderHistoryModel);
    private final JTable invoiceDetailTable = new JTable(invoiceDetailModel);
    private final JLabel historySummaryLabel = new JLabel("Lich su mua hang: 0 hoa don");
    private final JLabel invoiceSummaryLabel = new JLabel("Tong hoa don: " + vnCurrency.format(0));

    public CustomerPanel(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildFormPanel(), BorderLayout.NORTH);

        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(table), buildHistoryPanel());
        centerSplit.setResizeWeight(0.52);
        add(centerSplit, BorderLayout.CENTER);

        add(buildBottomPanel(), BorderLayout.SOUTH);

        idField.setEditable(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                fillFormFromSelectedRow();
                loadHistoryBySelectedCustomer();
            }
        });

        orderHistoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderHistoryTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadInvoiceForSelectedOrder();
            }
        });

        loadTable();
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 8, 8));
        panel.add(new JLabel("ID"));
        panel.add(idField);
        panel.add(new JLabel("Ho ten"));
        panel.add(fullNameField);

        panel.add(new JLabel("Dien thoai"));
        panel.add(phoneField);
        panel.add(new JLabel("Email"));
        panel.add(emailField);

        panel.add(new JLabel("Dia chi"));
        panel.add(addressField);

        return panel;
    }

    private JPanel buildHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel top = new JPanel(new BorderLayout());
        top.add(historySummaryLabel, BorderLayout.WEST);

        JPanel invoicePanel = new JPanel(new BorderLayout(8, 8));
        invoicePanel.add(invoiceSummaryLabel, BorderLayout.NORTH);
        invoicePanel.add(new JScrollPane(invoiceDetailTable), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(orderHistoryTable), invoicePanel);
        splitPane.setResizeWeight(0.5);

        panel.add(top, BorderLayout.NORTH);
        panel.add(splitPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton searchBtn = new JButton("Tim");
        JButton clearSearchBtn = new JButton("Tat tim");

        searchBtn.addActionListener(e -> doSearch());
        clearSearchBtn.addActionListener(e -> {
            searchField.setText("");
            loadTable();
        });

        searchPanel.add(new JLabel("Tu khoa:"));
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearSearchBtn);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addBtn = new JButton("Them");
        JButton updateBtn = new JButton("Sua");
        JButton deleteBtn = new JButton("Xoa");
        JButton clearBtn = new JButton("Lam moi form");

        addBtn.addActionListener(e -> insertCustomer());
        updateBtn.addActionListener(e -> updateCustomer());
        deleteBtn.addActionListener(e -> deleteCustomer());
        clearBtn.addActionListener(e -> clearForm());

        actionPanel.add(addBtn);
        actionPanel.add(updateBtn);
        actionPanel.add(deleteBtn);
        actionPanel.add(clearBtn);

        panel.add(searchPanel, BorderLayout.WEST);
        panel.add(actionPanel, BorderLayout.EAST);
        return panel;
    }

    private void doSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadTable();
            return;
        }

        List<Customer> customers = customerDao.searchByKeyword(keyword);
        loadTable(customers);
    }

    private void insertCustomer() {
        try {
            Customer customer = readForm(false);
            customerDao.insert(customer);
            loadTable();
            clearForm();
            onDataChanged.run();
            JOptionPane.showMessageDialog(this, "Them khach hang thanh cong");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Loi them khach hang: " + ex.getMessage());
        }
    }

    private void updateCustomer() {
        try {
            Customer customer = readForm(true);
            customerDao.update(customer);
            loadTable();
            onDataChanged.run();
            JOptionPane.showMessageDialog(this, "Cap nhat khach hang thanh cong");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Loi cap nhat khach hang: " + ex.getMessage());
        }
    }

    private void deleteCustomer() {
        if (idField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hay chon khach hang can xoa");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Ban chac chan muon xoa?", "Xac nhan", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int id = Integer.parseInt(idField.getText().trim());
            customerDao.delete(id);
            loadTable();
            clearForm();
            onDataChanged.run();
            JOptionPane.showMessageDialog(this, "Xoa khach hang thanh cong");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Loi xoa khach hang: " + ex.getMessage());
        }
    }

    private Customer readForm(boolean requireId) {
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        if (fullName.isEmpty() || phone.isEmpty()) {
            throw new IllegalArgumentException("Ho ten va dien thoai la bat buoc");
        }

        Customer customer = new Customer();
        if (requireId) {
            if (idField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Hay chon khach hang can sua");
            }
            customer.setId(Integer.parseInt(idField.getText().trim()));
        }

        customer.setFullName(fullName);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setAddress(address);

        return customer;
    }

    private void loadTable() {
        List<Customer> customers = customerDao.findAll();
        loadTable(customers);
    }

    private void loadTable(List<Customer> customers) {
        tableModel.setRowCount(0);

        for (Customer c : customers) {
            tableModel.addRow(new Object[]{
                c.getId(),
                c.getFullName(),
                c.getPhone(),
                c.getEmail(),
                c.getAddress()
            });
        }

        clearHistoryAndInvoice();
    }

    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }

        idField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        fullNameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        phoneField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        emailField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        addressField.setText(String.valueOf(tableModel.getValueAt(row, 4)));
    }

    private void clearForm() {
        idField.setText("");
        fullNameField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressField.setText("");
        table.clearSelection();
        clearHistoryAndInvoice();
    }

    private void loadHistoryBySelectedCustomer() {
        int row = table.getSelectedRow();
        if (row < 0) {
            clearHistoryAndInvoice();
            return;
        }

        int customerId = Integer.parseInt(String.valueOf(tableModel.getValueAt(row, 0)));
        List<OrderSummary> orders = orderService.getOrdersByCustomer(customerId);

        orderHistoryModel.setRowCount(0);
        double totalSpent = 0;

        for (OrderSummary order : orders) {
            String createdAt = order.getCreatedAt() == null ? "" : order.getCreatedAt().format(dateFormatter);
            totalSpent += order.getTotalAmount();
            orderHistoryModel.addRow(new Object[]{
                order.getOrderId(),
                vnCurrency.format(order.getTotalAmount()),
                createdAt
            });
        }

        String customerName = String.valueOf(tableModel.getValueAt(row, 1));
        historySummaryLabel.setText("Lich su mua hang: " + customerName + " - " + orders.size() + " hoa don, tong chi " + vnCurrency.format(totalSpent));
        clearInvoiceDetail();
    }

    private void loadInvoiceForSelectedOrder() {
        int row = orderHistoryTable.getSelectedRow();
        if (row < 0) {
            clearInvoiceDetail();
            return;
        }

        int orderId = Integer.parseInt(String.valueOf(orderHistoryModel.getValueAt(row, 0)));
        List<OrderItem> items = orderService.getOrderItemsByOrder(orderId);

        invoiceDetailModel.setRowCount(0);
        double total = 0;

        for (OrderItem item : items) {
            double lineTotal = item.getLineTotal();
            total += lineTotal;
            invoiceDetailModel.addRow(new Object[]{
                item.getProductName(),
                item.getQuantity(),
                vnCurrency.format(item.getUnitPrice()),
                vnCurrency.format(lineTotal)
            });
        }

        invoiceSummaryLabel.setText("Hoa don #" + orderId + " - Tong: " + vnCurrency.format(total));
    }

    private void clearHistoryAndInvoice() {
        orderHistoryModel.setRowCount(0);
        historySummaryLabel.setText("Lich su mua hang: 0 hoa don");
        clearInvoiceDetail();
    }

    private void clearInvoiceDetail() {
        invoiceDetailModel.setRowCount(0);
        invoiceSummaryLabel.setText("Tong hoa don: " + vnCurrency.format(0));
    }
}