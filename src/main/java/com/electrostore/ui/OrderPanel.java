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

        add(buildTopPanel(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 12, 12));
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
        cartTable.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);
        cartTable.getColumnModel().getColumn(4).setCellRenderer(currencyRenderer);

        refreshData();
    }

    private JPanel buildTopPanel() {
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line1.add(new JLabel("Khach hang:"));
        line1.add(customerCombo);

        JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        line2.add(new JLabel("San pham:"));
        line2.add(productCombo);
        line2.add(new JLabel("So luong:"));
        line2.add(quantitySpinner);

        JButton addItemBtn = new JButton("Them vao gio");
        JButton removeItemBtn = new JButton("Xoa hang duoc chon");
        JButton reloadBtn = new JButton("Tai lai du lieu");

        addItemBtn.addActionListener(e -> addItemToCart());
        removeItemBtn.addActionListener(e -> removeSelectedCartItem());
        reloadBtn.addActionListener(e -> refreshData());

        JPanel line3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
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
        totalLabel.setText(vnCurrency.format(0));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(new JLabel("Tong hoa don:"));
        left.add(totalLabel);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveOrderBtn = new JButton("Thanh toan / Luu hoa don");
        JButton clearCartBtn = new JButton("Xoa gio hang");

        saveOrderBtn.addActionListener(e -> saveOrder());
        clearCartBtn.addActionListener(e -> clearCart());

        right.add(clearCartBtn);
        right.add(saveOrderBtn);

        bottomPanel.add(left, BorderLayout.WEST);
        bottomPanel.add(right, BorderLayout.EAST);

        return bottomPanel;
    }

    private void reloadCombos() {
        customerCombo.removeAllItems();
        productCombo.removeAllItems();

        List<Customer> customers = orderService.getCustomers();
        for (Customer c : customers) {
            customerCombo.addItem(c);
        }

        List<Product> products = orderService.getInStockProducts();
        for (Product p : products) {
            productCombo.addItem(p);
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
