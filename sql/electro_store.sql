DROP DATABASE IF EXISTS electro_store;
CREATE DATABASE electro_store CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE electro_store;

CREATE TABLE products (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    brand VARCHAR(100) NOT NULL,
    category VARCHAR(100) NOT NULL,
    price DECIMAL(12,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE customers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(120),
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    customer_id INT NOT NULL,
    total_amount DECIMAL(12,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_orders_customers FOREIGN KEY (customer_id) REFERENCES customers(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE TABLE order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(12,2) NOT NULL,
    line_total DECIMAL(12,2) NOT NULL,
    CONSTRAINT fk_items_orders FOREIGN KEY (order_id) REFERENCES orders(id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_items_products FOREIGN KEY (product_id) REFERENCES products(id)
        ON UPDATE CASCADE ON DELETE RESTRICT
);

INSERT INTO products(name, brand, category, price, stock) VALUES
('Laptop IdeaPad 5', 'Lenovo', 'Laptop', 16990000, 15),
('iPhone 14 128GB', 'Apple', 'Dien thoai', 17490000, 20),
('Galaxy Tab S9 FE', 'Samsung', 'May tinh bang', 10990000, 8),
('Tai nghe WH-1000XM5', 'Sony', 'Phu kien', 7490000, 25),
('Man hinh UltraWide 34"', 'LG', 'Man hinh', 11990000, 10);

INSERT INTO customers(full_name, phone, email, address) VALUES
('Nguyen Van A', '0901000001', 'vana@gmail.com', 'Ha Noi'),
('Tran Thi B', '0901000002', 'thib@gmail.com', 'Da Nang'),
('Le Van C', '0901000003', 'vanc@gmail.com', 'TP HCM');
