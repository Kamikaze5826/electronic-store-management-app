package com.electrostore.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.electrostore.model.Customer;
import com.electrostore.model.OrderItem;
import com.electrostore.model.OrderSummary;
import com.electrostore.model.Product;
import com.electrostore.service.OrderService;

public class OrderPanel extends JPanel {

    private final OrderService orderService = new OrderService();
    private final Runnable onDataChanged;
    private final NumberFormat vnCurrency = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    private final JComboBox<Customer> customerCombo = new JComboBox<>();
    private final JComboBox<Product> productCombo = new JComboBox<>();
    private final JSpinner quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
    private final JTextField customerCodeSearchField = new JTextField(10);
    private final JTextField customerNameSearchField = new JTextField(14);
    private final JTextField productCodeSearchField = new JTextField(10);
    private final JTextField productNameSearchField = new JTextField(14);

    private List<Customer> allCustomers = new ArrayList<>();
    private List<Product> allProducts = new ArrayList<>();

    private final DefaultTableModel cartModel = new DefaultTableModel(
            new String[]{"ProductID", "Ten san pham", "So luong", "Don gia", "Thanh tien"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel recentOrderModel = new DefaultTableModel(
            new String[]{"Ma HD", "Khach hang", "Tong tien", "Thoi gian"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable cartTable = new JTable(cartModel);
    private final JTable recentOrderTable = new JTable(recentOrderModel);
    private final JLabel totalLabel = new JLabel("0 VND");

    public OrderPanel(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setBackground(ModernTheme.BACKGROUND);

        add(buildTopPanel(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 12, 12));
        centerPanel.setOpaque(false);
        centerPanel.add(new JScrollPane(cartTable));
        centerPanel.add(new JScrollPane(recentOrderTable));
        add(centerPanel, BorderLayout.CENTER);

        add(buildBottomPanel(), BorderLayout.SOUTH);

        DefaultTableCellRenderer currencyRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Number numberValue) {
                    setText(vnCurrency.format(numberValue.doubleValue()));
                } else {
                    setText(value == null ? "" : value.toString());
                }
            }
        };
        currencyRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        ModernTheme.styleTable(cartTable);
        ModernTheme.styleTable(recentOrderTable);
        cartTable.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);
        cartTable.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);

        ModernTheme.styleComboBox(customerCombo);
        ModernTheme.styleComboBox(productCombo);
        ModernTheme.styleInput(customerCodeSearchField);
        ModernTheme.styleInput(customerNameSearchField);
        ModernTheme.styleInput(productCodeSearchField);
        ModernTheme.styleInput(productNameSearchField);
        ModernTheme.styleSpinner(quantitySpinner);

        initializeData();
    }

    private void initializeData() {
        refreshData();
    }

    private JPanel buildTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        ModernTheme.styleSectionPanel(topPanel);

        JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line1.setOpaque(false);
        line1.add(new JLabel("Khach hang:"));
        line1.add(customerCombo);
        line1.add(new JLabel("Ma KH:"));
        line1.add(customerCodeSearchField);
        line1.add(new JLabel("Ten KH:"));
        line1.add(customerNameSearchField);

        JButton searchCustomerBtn = new JButton("Loc KH");
        JButton clearCustomerFilterBtn = new JButton("Reset dieu kien KH");
        ModernTheme.stylePrimaryButton(searchCustomerBtn);
        ModernTheme.styleSecondaryButton(clearCustomerFilterBtn);
        searchCustomerBtn.addActionListener(e -> applyCustomerFilter(true));
        clearCustomerFilterBtn.addActionListener(e -> {
            customerCodeSearchField.setText("");
            customerNameSearchField.setText("");
            applyCustomerFilter(false);
        });
        line1.add(searchCustomerBtn);
        line1.add(clearCustomerFilterBtn);

        JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line2.setOpaque(false);
        line2.add(new JLabel("San pham:"));
        line2.add(productCombo);
        line2.add(new JLabel("Ma SP:"));
        line2.add(productCodeSearchField);
        line2.add(new JLabel("Ten SP:"));
        line2.add(productNameSearchField);

        JButton searchProductBtn = new JButton("Loc SP");
        JButton clearProductFilterBtn = new JButton("Reset dieu kien SP");
        ModernTheme.stylePrimaryButton(searchProductBtn);
        ModernTheme.styleSecondaryButton(clearProductFilterBtn);
        searchProductBtn.addActionListener(e -> applyProductFilter(true));
        clearProductFilterBtn.addActionListener(e -> {
            productCodeSearchField.setText("");
            productNameSearchField.setText("");
            applyProductFilter(false);
        });
        line2.add(searchProductBtn);
        line2.add(clearProductFilterBtn);

        line2.add(new JLabel("So luong:"));
        line2.add(quantitySpinner);

        JButton addItemBtn = new JButton("Them vao gio");
        JButton removeItemBtn = new JButton("Xoa hang duoc chon");
        JButton reloadBtn = new JButton("Tai lai du lieu");
        ModernTheme.stylePrimaryButton(addItemBtn);
        ModernTheme.styleSecondaryButton(removeItemBtn);
        ModernTheme.styleSecondaryButton(reloadBtn);

        addItemBtn.addActionListener(e -> addItemToCart());
        removeItemBtn.addActionListener(e -> removeSelectedCartItem());
        reloadBtn.addActionListener(e -> refreshData());

        JPanel line3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line3.setOpaque(false);
        line3.add(addItemBtn);
        line3.add(removeItemBtn);
        line3.add(reloadBtn);

        topPanel.add(line1);
        topPanel.add(line2);
        topPanel.add(line3);

        return topPanel;
    }

    public void refreshData() {
        reloadCombos();
        loadRecentOrders();
    }

    private JPanel buildBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        ModernTheme.styleSectionPanel(bottomPanel);
        totalLabel.setText(vnCurrency.format(0));
        totalLabel.setForeground(ModernTheme.PRIMARY_DARK);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setOpaque(false);
        left.add(new JLabel("Tong hoa don:"));
        left.add(totalLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        JButton saveOrderBtn = new JButton("Thanh toan / Luu hoa don");
        JButton clearCartBtn = new JButton("Xoa gio hang");
        ModernTheme.stylePrimaryButton(saveOrderBtn);
        ModernTheme.styleSecondaryButton(clearCartBtn);

        saveOrderBtn.addActionListener(e -> saveOrder());
        clearCartBtn.addActionListener(e -> clearCart());

        right.add(clearCartBtn);
        right.add(saveOrderBtn);

        bottomPanel.add(left, BorderLayout.WEST);
        bottomPanel.add(right, BorderLayout.EAST);

        return bottomPanel;
    }

    private void reloadCombos() {
        allCustomers = orderService.getCustomers();
        allProducts = orderService.getInStockProducts();
        applyCustomerFilter(false);
        applyProductFilter(false);
    }

    private void applyCustomerFilter(boolean showNotFoundMessage) {
        String customerCodeKeyword = customerCodeSearchField.getText().trim();
        String customerNameKeyword = customerNameSearchField.getText().trim().toLowerCase();

        customerCombo.removeAllItems();
        int matchedCount = 0;
        for (Customer c : allCustomers) {
            boolean matchedCode = customerCodeKeyword.isEmpty() || String.valueOf(c.getId()).contains(customerCodeKeyword);
            boolean matchedName = customerNameKeyword.isEmpty() || c.getFullName().toLowerCase().contains(customerNameKeyword);
            boolean matched = matchedCode && matchedName;
            if (matched) {
                customerCombo.addItem(c);
                matchedCount++;
            }
        }

        if (showNotFoundMessage && matchedCount == 0) {
            JOptionPane.showMessageDialog(this, "Khong tim thay khach hang phu hop");
        }
    }

    private void applyProductFilter(boolean showNotFoundMessage) {
        String productCodeKeyword = productCodeSearchField.getText().trim();
        String productNameKeyword = productNameSearchField.getText().trim().toLowerCase();

        productCombo.removeAllItems();
        int matchedCount = 0;
        for (Product p : allProducts) {
            boolean matchedCode = productCodeKeyword.isEmpty() || String.valueOf(p.getId()).contains(productCodeKeyword);
            boolean matchedName = productNameKeyword.isEmpty() || p.getName().toLowerCase().contains(productNameKeyword);
            boolean matched = matchedCode && matchedName;
            if (matched) {
                productCombo.addItem(p);
                matchedCount++;
            }
        }

        if (showNotFoundMessage && matchedCount == 0) {
            JOptionPane.showMessageDialog(this, "Khong tim thay san pham phu hop");
        }
    }

    private void addItemToCart() {
        Product selectedProduct = (Product) productCombo.getSelectedItem();
        if (selectedProduct == null) {
            JOptionPane.showMessageDialog(this, "Khong co san pham trong kho");
            return;
        }

        int quantity = (Integer) quantitySpinner.getValue();
        if (quantity > selectedProduct.getStock()) {
            JOptionPane.showMessageDialog(this, "So luong vuot ton kho");
            return;
        }

        for (int row = 0; row < cartModel.getRowCount(); row++) {
            int productId = (Integer) cartModel.getValueAt(row, 0);
            if (productId == selectedProduct.getId()) {
                int existingQty = (Integer) cartModel.getValueAt(row, 2);
                int newQty = existingQty + quantity;
                if (newQty > selectedProduct.getStock()) {
                    JOptionPane.showMessageDialog(this, "Tong so luong vuot ton kho");
                    return;
                }

                double unitPrice = (Double) cartModel.getValueAt(row, 3);
                cartModel.setValueAt(newQty, row, 2);
                cartModel.setValueAt(unitPrice * newQty, row, 4);
                updateTotalLabel();
                return;
            }
        }

        double unitPrice = selectedProduct.getPrice();
        cartModel.addRow(new Object[]{
            selectedProduct.getId(),
            selectedProduct.getName(),
            quantity,
            unitPrice,
            unitPrice * quantity
        });

        updateTotalLabel();
    }

    private void removeSelectedCartItem() {
        int row = cartTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Hay chon dong trong gio hang");
            return;
        }

        cartModel.removeRow(row);
        updateTotalLabel();
    }

    private void updateTotalLabel() {
        double total = 0;
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            total += (Double) cartModel.getValueAt(i, 4);
        }
        totalLabel.setText(vnCurrency.format(total));
    }

    private void saveOrder() {
        Customer customer = (Customer) customerCombo.getSelectedItem();
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Vui long tao khach hang truoc khi ban hang");
            return;
        }

        List<OrderItem> items = new ArrayList<>();
        for (int i = 0; i < cartModel.getRowCount(); i++) {
            int productId = (Integer) cartModel.getValueAt(i, 0);
            String productName = (String) cartModel.getValueAt(i, 1);
            int qty = (Integer) cartModel.getValueAt(i, 2);
            double unitPrice = (Double) cartModel.getValueAt(i, 3);
            items.add(new OrderItem(productId, productName, qty, unitPrice));
        }

        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Gio hang rong");
            return;
        }

        try {
            orderService.createOrder(customer.getId(), items);
            JOptionPane.showMessageDialog(this, "Luu hoa don thanh cong");

            clearCart();
            onDataChanged.run();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Loi tao hoa don: " + ex.getMessage());
        }
    }

    private void clearCart() {
        cartModel.setRowCount(0);
        updateTotalLabel();
    }

    private void loadRecentOrders() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        recentOrderModel.setRowCount(0);
        List<OrderSummary> recentOrders = orderService.getRecentOrders();
        for (OrderSummary o : recentOrders) {
            String createdAt = o.getCreatedAt() == null ? "" : o.getCreatedAt().format(fmt);
            recentOrderModel.addRow(new Object[]{
                o.getOrderId(),
                o.getCustomerName(),
                vnCurrency.format(o.getTotalAmount()),
                createdAt
            });
        }
    }
}
