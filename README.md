# Ung dung quan ly cua hang do dien tu (Java Swing + MySQL XAMPP)

## 1. Yeu cau moi truong
- JDK 17+
- Maven 3.8+
- XAMPP (MySQL)

## 2. Tao CSDL tren XAMPP
1. Mo XAMPP va Start MySQL.
2. Mo phpMyAdmin.
3. Import file `sql/electro_store.sql`.

## 3. Cau hinh ket noi
Mac dinh app su dung:
- DB: `electro_store`
- User: `root`
- Password: rong
- URL: `jdbc:mysql://localhost:3306/electro_store?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC`

Neu can doi, sua file:
- `src/main/java/com/electrostore/config/DatabaseConfig.java`

## 4. Chay ung dung
Tu terminal tai thu muc project:

```bash
mvn clean compile
mvn exec:java
```

## 5. Chuc nang chinh
- Tong quan: thong ke so san pham, khach hang, hoa don, tong doanh thu.
- Quan ly san pham: them/sua/xoa/tim kiem.
- Quan ly khach hang: them/sua/xoa.
- Ban hang: tao gio hang, luu hoa don, tru ton kho tu dong.

## 6. Cau truc thu muc
- `src/main/java/com/electrostore/config`: cau hinh va ket noi DB.
- `src/main/java/com/electrostore/model`: model du lieu.
- `src/main/java/com/electrostore/dao`: truy van DB.
- `src/main/java/com/electrostore/service`: xu ly nghiep vu.
- `src/main/java/com/electrostore/ui`: giao dien Swing.
- `sql`: script khoi tao CSDL.
