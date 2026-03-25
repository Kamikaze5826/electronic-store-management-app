package com.electrostore.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.electrostore.dao.ProductDao;
import com.electrostore.model.Product;

public class ProductPanel extends JPanel {
    private final ProductDao productDao = new ProductDao();
    private final Runnable onDataChanged;
    private final NumberFormat priceDisplayFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField brandField = new JTextField();
    private final JTextField categoryField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final JTextField stockField = new JTextField();
    private final JTextField searchField = new JTextField(20);

    private final DefaultTableModel tableModel = new DefaultTableModel(
        new String[]{"ID", "Ten", "Hang", "Loai", "Gia", "Ton kho"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    public ProductPanel(Runnable onDataChanged) {
        this.onDataChanged = onDataChanged;
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildBottomPanel(), BorderLayout.SOUTH);

        idField.setEditable(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> fillFormFromSelectedRow());
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Number numberValue) {
                    setText(priceDisplayFormat.format(numberValue.doubleValue()));
                } else {
                    setText(value == null ? "" : value.toString());
                }
            }
        });

        loadTable(productDao.findAll());
    }

    private JPanel buildFormPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 4, 8, 8));
        panel.add(new JLabel("ID"));
        panel.add(idField);
        panel.add(new JLabel("Ten"));
        panel.add(nameField);

        panel.add(new JLabel("Hang"));
        panel.add(brandField);
        panel.add(new JLabel("Loai"));
        panel.add(categoryField);

        panel.add(new JLabel("Gia"));
        panel.add(priceField);
        panel.add(new JLabel("Ton kho"));
        panel.add(stockField);
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
            loadTable(productDao.findAll());
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

        addBtn.addActionListener(e -> insertProduct());
        updateBtn.addActionListener(e -> updateProduct());
        deleteBtn.addActionListener(e -> deleteProduct());
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
            loadTable(productDao.findAll());
            return;
        }
        loadTable(productDao.searchByKeyword(keyword));
    }

    private void insertProduct() {
        try {
            Product product = readForm(false);
            productDao.insert(product);
            loadTable(productDao.findAll());
            clearForm();
            onDataChanged.run();
            JOptionPane.showMessageDialog(this, "Them san pham thanh cong");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Loi them san pham: " + ex.getMessage());
        }
    }

    private void updateProduct() {
        try {
            Product product = readForm(true);
            productDao.update(product);
            loadTable(productDao.findAll());
            onDataChanged.run();
            JOptionPane.showMessageDialog(this, "Cap nhat san pham thanh cong");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Loi cap nhat san pham: " + ex.getMessage());
        }
    }

    private void deleteProduct() {
        if (idField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Hay chon san pham can xoa");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Ban chac chan muon xoa?", "Xac nhan", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            int id = Integer.parseInt(idField.getText().trim());
            productDao.delete(id);
            loadTable(productDao.findAll());
            clearForm();
            onDataChanged.run();
            JOptionPane.showMessageDialog(this, "Da xoa san pham hoac chuyen sang trang thai ngung kinh doanh neu da co lich su ban");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "Loi xoa san pham: " + ex.getMessage());
        }
    }

    private Product readForm(boolean requireId) {
        String name = nameField.getText().trim();
        String brand = brandField.getText().trim();
        String category = categoryField.getText().trim();
        String priceText = priceField.getText().trim();
        String stockText = stockField.getText().trim();

        if (name.isEmpty() || brand.isEmpty() || category.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            throw new IllegalArgumentException("Hay nhap day du thong tin");
        }

        double price = Double.parseDouble(priceText.replace(",", ""));
        int stock = Integer.parseInt(stockText);
        if (price < 0 || stock < 0) {
            throw new IllegalArgumentException("Gia va ton kho khong duoc am");
        }

        Product product = new Product();
        if (requireId) {
            if (idField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Hay chon san pham can sua");
            }
            product.setId(Integer.parseInt(idField.getText().trim()));
        }
        product.setName(name);
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(price);
        product.setStock(stock);

        return product;
    }

    private void loadTable(List<Product> products) {
        tableModel.setRowCount(0);
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getBrand(), p.getCategory(), p.getPrice(), p.getStock()
            });
        }
    }

    private void fillFormFromSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }

        idField.setText(String.valueOf(tableModel.getValueAt(row, 0)));
        nameField.setText(String.valueOf(tableModel.getValueAt(row, 1)));
        brandField.setText(String.valueOf(tableModel.getValueAt(row, 2)));
        categoryField.setText(String.valueOf(tableModel.getValueAt(row, 3)));
        Object rawPrice = tableModel.getValueAt(row, 4);
        if (rawPrice instanceof Number numberValue) {
            priceField.setText(java.math.BigDecimal.valueOf(numberValue.doubleValue()).stripTrailingZeros().toPlainString());
        } else {
            priceField.setText(String.valueOf(rawPrice));
        }
        stockField.setText(String.valueOf(tableModel.getValueAt(row, 5)));
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        brandField.setText("");
        categoryField.setText("");
        priceField.setText("");
        stockField.setText("");
        table.clearSelection();
    }
}