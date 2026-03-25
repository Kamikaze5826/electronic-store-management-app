-- 1. Thêm 10 sản phẩm đa dạng chủng loại
INSERT INTO products (name, brand, category, price, stock, is_active, created_at) VALUES
('iPhone 15 Pro Max 256GB', 'Apple', 'Smartphone', 30000000.00, 45, 1, NOW()),
('Samsung Galaxy S24 Ultra', 'Samsung', 'Smartphone', 32000000.00, 30, 1, NOW()),
('MacBook Pro M3 14-inch', 'Apple', 'Laptop', 40000000.00, 15, 1, NOW()),
('ThinkPad X1 Carbon Gen 11', 'Lenovo', 'Laptop', 35000000.00, 10, 1, NOW()),
('iPad Air 5 WiFi 64GB', 'Apple', 'Tablet', 15000000.00, 25, 1, NOW()),
('AirPods Pro 2', 'Apple', 'Accessories', 6000000.00, 100, 1, NOW()),
('Sony WF-1000XM5', 'Sony', 'Accessories', 6500000.00, 60, 1, NOW()),
('Garmin Fenix 7', 'Garmin', 'Wearable', 18000000.00, 20, 1, NOW()),
('LG UltraGear 27 inch 144Hz', 'LG', 'Monitor', 8000000.00, 40, 1, NOW()),
('Bàn phím cơ Keychron Q1 Pro', 'Keychron', 'Accessories', 4500000.00, 50, 1, NOW());

-- 2. Thêm 10 khách hàng ngẫu nhiên
INSERT INTO customers (full_name, phone, email, address, created_at) VALUES
('Nguyen Van An', '0901112233', 'an.nguyen@email.com', '123 Le Duan, Q.1, TP.HCM', NOW()),
('Tran Thi Binh', '0912223344', 'binh.tran@email.com', '456 Tran Phu, Ba Dinh, Ha Noi', NOW()),
('Le Hoang Cuong', '0923334455', 'cuong.le@email.com', '789 Nguyen Van Linh, Da Nang', NOW()),
('Pham My Dung', '0934445566', 'dung.pham@email.com', '321 30/4, Ninh Kieu, Can Tho', NOW()),
('Hoang Tuan Em', '0945556677', 'em.hoang@email.com', '654 Le Loi, Vung Tau', NOW()),
('Vu Thi Phuong', '0956667788', 'phuong.vu@email.com', '987 Quang Trung, Go Vap, TP.HCM', NOW()),
('Doan Van Giang', '0967778899', 'giang.doan@email.com', '111 Hai Ba Trung, Q.3, TP.HCM', NOW()),
('Bui Thi Hoa', '0978889900', 'hoa.bui@email.com', '222 Ly Thuong Kiet, Hoan Kiem, Ha Noi', NOW()),
('Dinh Cong It', '0989990011', 'it.dinh@email.com', '333 Vo Van Ngan, Thu Duc, TP.HCM', NOW()),
('Ngo Thi Kieu', '0990001122', 'kieu.ngo@email.com', '444 Nguyen Trai, Thanh Xuan, Ha Noi', NOW());

-- 3. Procedure tự động tạo đơn hàng ngẫu nhiên từ TẤT CẢ khách hàng và sản phẩm hiện có
DELIMITER //

CREATE PROCEDURE AppendRandomOrders(IN num_orders INT)
BEGIN
    DECLARE i INT DEFAULT 0;
    DECLARE new_order_id INT;
    DECLARE random_customer_id INT;
    DECLARE random_product_id INT;
    DECLARE random_qty INT;
    DECLARE current_price DECIMAL(12,2);
    DECLARE items_count INT;
    DECLARE j INT;
    DECLARE order_total DECIMAL(12,2);

    WHILE i < num_orders DO
        -- Chọn ngẫu nhiên 1 khách hàng từ database
        SELECT id INTO random_customer_id FROM customers ORDER BY RAND() LIMIT 1;
        
        -- Tạo đơn hàng với tổng tiền ban đầu = 0, ngày mua lùi ngẫu nhiên trong 90 ngày qua
        INSERT INTO orders (customer_id, total_amount, created_at) 
        VALUES (random_customer_id, 0, DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 90) DAY));
        
        SET new_order_id = LAST_INSERT_ID();
        
        -- Mỗi đơn hàng sẽ mua từ 1 đến 3 sản phẩm khác nhau
        SET items_count = FLOOR(1 + RAND() * 3);
        SET j = 0;
        SET order_total = 0;
        
        WHILE j < items_count DO
            -- Chọn ngẫu nhiên 1 sản phẩm
            SELECT id, price INTO random_product_id, current_price FROM products ORDER BY RAND() LIMIT 1;
            
            -- Mua từ 1 đến 2 cái cho mỗi sản phẩm
            SET random_qty = FLOOR(1 + RAND() * 2);
            
            INSERT INTO order_items (order_id, product_id, quantity, unit_price, line_total)
            VALUES (new_order_id, random_product_id, random_qty, current_price, (random_qty * current_price));
            
            -- Cộng dồn vào biến tổng tiền
            SET order_total = order_total + (random_qty * current_price);
            
            SET j = j + 1;
        END WHILE;
        
        -- Cập nhật tổng tiền đơn hàng
        UPDATE orders SET total_amount = order_total WHERE id = new_order_id;
        
        SET i = i + 1;
    END WHILE;
END //

DELIMITER ;

-- 4. Tạo 30 đơn hàng ngẫu nhiên (chạy từ dữ liệu cũ + 20 dòng mới thêm ở trên)
CALL AppendRandomOrders(30);

-- 5. Xóa procedure sau khi chạy xong
DROP PROCEDURE IF EXISTS AppendRandomOrders;