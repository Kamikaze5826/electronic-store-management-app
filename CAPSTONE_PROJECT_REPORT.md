# Capstone Project Report
## Electro Store Manager: Desktop UI and Database-Driven Information System

### Student Capstone Documentation
- Project Title: Electro Store Manager
- Project Type: Academic Capstone (Desktop Application + Relational Database)
- Technology Stack: Java 17, Java Swing, JDBC, MySQL, Maven
- Report Language: English

---

## Abstract
This capstone project presents the design and implementation of Electro Store Manager, a desktop-based management information system for an electronics retail shop. The system addresses day-to-day operational requirements in product management, customer administration, sales order processing, and business monitoring through a unified user interface and a relational database backend. The implementation uses Java Swing for the graphical user interface and MySQL for persistent data storage, connected via JDBC.

The project follows a layered architecture consisting of presentation, business, data access, and configuration layers. The user interface is organized into dedicated panels for dashboard analytics, product operations, customer operations, order processing, and database setup. The database schema includes core entities such as products, customers, orders, and order_items, with foreign key constraints to enforce referential integrity. The system supports stock validation, transactional order creation, and automatic stock deduction to ensure consistency.

A notable contribution of the project is practical integration between UI behavior and database reliability. The application includes database connection testing, schema initialization, and full database reset from within the desktop interface, reducing setup complexity for non-technical users. In addition, product removal is handled with a hybrid strategy: hard deletion when possible and soft deactivation when historical order references exist.

The outcome is a functional and extensible retail management solution suitable for small and medium electronics stores. Evaluation indicates that the system improves data organization, reduces manual errors in billing and stock management, and provides quick operational insights through a dashboard. The report concludes with limitations and recommendations for future enhancements such as authentication, reporting export, role-based access, and cloud deployment.

Keywords: Desktop Application, Java Swing, MySQL, JDBC, POS System, Inventory Management, Capstone Project

---

## Table of Contents
1. Introduction  
2. Problem Statement and Motivation  
3. Project Objectives and Scope  
4. Methodology and Development Approach  
5. System Requirements  
6. System Analysis and Design  
7. Database Design  
8. User Interface Design and Interaction Flow  
9. Implementation Details  
10. Testing and Validation  
11. Results and Discussion  
12. Limitations  
13. Future Work  
14. Conclusion  
15. References  
16. Appendix

---

## 1. Introduction
Retail businesses in the electronics domain handle a large number of products, frequent customer interactions, and high-velocity sales transactions. Manual management with spreadsheets or paper records often leads to stock mismatches, delayed billing, inaccurate reports, and weak traceability of customer purchases. As digital transformation becomes increasingly necessary, even small stores require a reliable software system that combines usability with data consistency.

Electro Store Manager was developed as a capstone project to address this practical need. The application is a desktop information system designed for local operation with a dedicated MySQL database. The system offers an intuitive user interface for managing products, customers, and orders while also generating business indicators such as total revenue, total orders, and top-selling products.

The project demonstrates complete integration between front-end desktop UI and back-end relational database design. Unlike purely theoretical assignments, this system emphasizes operational readiness: users can configure database connectivity, test server access, create the schema, and reset data without leaving the application interface. This design decision makes the software suitable for environments where technical support is limited.

From an academic perspective, the project illustrates core software engineering principles: modular architecture, separation of concerns, transaction handling, foreign key design, form validation, and iterative feature implementation. Therefore, this capstone is both an applied business solution and a demonstration of technical competence in full-stack desktop application development.

---

## 2. Problem Statement and Motivation
### 2.1 Problem Statement
Small electronics shops commonly face the following operational challenges:
- Product catalogs are not centralized, causing duplicate or outdated entries.
- Stock quantities are not synchronized with sales activities.
- Customer purchase history is difficult to trace.
- Invoice generation is manual and error-prone.
- Business performance indicators are unavailable in real time.

These challenges increase business risk and reduce decision quality. For example, inaccurate stock values can result in overselling, while missing customer history limits opportunities for loyalty campaigns and after-sales support.

### 2.2 Motivation
The motivation for this project is to provide a practical, affordable, and maintainable solution for local retail operations. The system aims to:
- Digitize core workflows with a user-friendly interface.
- Protect data integrity using relational constraints and transactions.
- Enable quick setup through integrated database management tools.
- Offer actionable insights through a dashboard.

The project is also motivated by educational goals: applying Java programming, database design, GUI development, and software architecture patterns in one coherent application.

---

## 3. Project Objectives and Scope
### 3.1 General Objective
To design and implement a desktop-based electronics store management system that integrates user interface operations with a relational database to support inventory, customer, and sales management.

### 3.2 Specific Objectives
- Implement product management features: create, read, update, delete, and search.
- Implement customer management features: create, read, update, delete, and search.
- Implement order processing with cart functionality and stock validation.
- Ensure transaction safety when creating orders and reducing stock.
- Build a dashboard with summary statistics and top metrics.
- Provide in-app database configuration, schema creation, connection testing, and reset.
- Maintain modularity via separate configuration, DAO, model, service, and UI packages.

### 3.3 Scope
Included scope:
- Single-desktop usage context.
- One shared MySQL database instance.
- Core business entities: product, customer, order, order item.
- Basic analytics and historical viewing.

Excluded scope:
- Multi-branch synchronization.
- User authentication and authorization roles.
- Advanced accounting and tax modules.
- Payment gateway integration.
- Cloud-native deployment and API services.

---

## 4. Methodology and Development Approach
### 4.1 Development Model
The project follows an iterative and incremental process:
1. Requirement identification from store operation scenarios.
2. Data model draft and relational schema design.
3. GUI prototype construction in Java Swing.
4. DAO and service layer implementation.
5. Integration of UI actions with database transactions.
6. Functional testing and bug refinement.

This approach allows early validation of key workflows such as adding products, creating orders, and verifying stock deduction.

### 4.2 Engineering Principles Applied
- Separation of concerns: UI, business logic, and data access are decoupled.
- Encapsulation: model classes represent data entities cleanly.
- Reusability: shared database configuration and connection utilities.
- Robustness: exception handling and validation at form and SQL levels.
- Data integrity: foreign keys and transactional order creation.

### 4.3 Toolchain
- Language: Java 17
- Build and dependency management: Maven
- Database engine: MySQL (commonly via XAMPP)
- JDBC driver: mysql-connector-java
- UI toolkit: Java Swing
- IDE: Visual Studio Code (or any Java-compatible IDE)

---

## 5. System Requirements
### 5.1 Functional Requirements
FR1: The system shall allow users to add, update, delete, and search products.  
FR2: The system shall allow users to add, update, delete, and search customers.  
FR3: The system shall allow users to create sales orders by selecting customer and products.  
FR4: The system shall validate quantity against current stock before saving orders.  
FR5: The system shall persist order and order items atomically in one transaction.  
FR6: The system shall update product stock after successful order creation.  
FR7: The system shall show dashboard statistics (counts, revenue, top customer, top product).  
FR8: The system shall display customer purchase history and invoice details.  
FR9: The system shall support database connection testing and schema initialization.  
FR10: The system shall allow database reset with user confirmation.

### 5.2 Non-Functional Requirements
- Usability: clear, tab-based interface for non-technical store staff.
- Performance: responsive behavior for small to medium datasets.
- Reliability: transactional consistency and SQL error handling.
- Maintainability: package-level modular architecture.
- Portability: runs on environments supporting Java 17 and MySQL.

### 5.3 Hardware and Software Requirements
- Operating system: Windows, Linux, or macOS
- Java Runtime: JDK 17+
- Database server: MySQL 8+
- Memory and storage: standard desktop requirements

---

## 6. System Analysis and Design
### 6.1 Architectural Overview
The software uses a layered architecture:
- Presentation Layer: Swing panels and frame classes.
- Business Layer: order service and business coordination.
- Data Access Layer: DAO classes for SQL operations.
- Configuration Layer: connection setup and schema initialization.
- Domain Model Layer: entity classes for structured data transfer.

This architecture supports maintainability and testability. UI components do not directly write SQL except via service or DAO abstractions.

### 6.2 Package Structure
- config: database parameters, JDBC connection helpers, schema initialization.
- model: Product, Customer, OrderItem, OrderSummary.
- dao: ProductDao, CustomerDao, DashboardDao.
- service: OrderService.
- ui: MainFrame and all operation panels.

### 6.3 Main Application Flow
1. Application starts through Main class.
2. MainFrame loads home interface.
3. User chooses management area or database area.
4. If management area is selected, database readiness is validated.
5. Management tabs provide operational functions.
6. Data-changing operations trigger dashboard refresh for synchronized insights.

### 6.4 Key Design Decisions
- CardLayout at top-level navigation between Home, Management, and Database panels.
- TabbedPane for organizing management modules.
- Runtime database configuration to avoid hard-coded deployment constraints.
- Reuse of callback function for data refresh across tabs.

---

## 7. Database Design
### 7.1 Data Model Overview
The relational schema contains four core tables:
- products
- customers
- orders
- order_items

The model follows one-to-many relationships:
- One customer can have many orders.
- One order can have many order items.
- One product can appear in many order items.

### 7.2 Table Definitions Summary
products:
- id (PK, auto-increment)
- name
- brand
- category
- price
- stock
- is_active
- created_at

customers:
- id (PK, auto-increment)
- full_name
- phone
- email
- address
- created_at

orders:
- id (PK, auto-increment)
- customer_id (FK -> customers.id)
- total_amount
- created_at

order_items:
- id (PK, auto-increment)
- order_id (FK -> orders.id)
- product_id (FK -> products.id)
- quantity
- unit_price
- line_total

### 7.3 Integrity Constraints
- Foreign key constraints enforce valid references.
- Restrictive deletion protects historical data consistency.
- Order-item relation cascades deletion with parent order where appropriate.

### 7.4 Data Consistency Strategy
- Order creation is transactional:
  - Insert order header.
  - Insert order items.
  - Update stock values with stock availability condition.
  - Commit only if all operations succeed.
- If any step fails, transaction is rolled back.

### 7.5 Soft Delete for Products
The system uses a hybrid delete strategy for products:
- Attempt physical delete first.
- If product is referenced by historical order_items, set is_active to 0 and stock to 0.

This preserves historical invoices while removing inactive products from operational views.

---

## 8. User Interface Design and Interaction Flow
### 8.1 UI Theme and Visual Consistency
The interface applies a consistent modern theme:
- light background and white panel surfaces
- unified typography using Segoe UI
- standardized button and input styles
- readable table headers and highlighted row selection

This design improves user comfort for prolonged daily operation.

### 8.2 Home Screen
The home screen provides two core options:
- Store Management
- Database Management

The user is guided clearly to either operational tasks or system configuration tasks.

### 8.3 Database Management Screen
Functions provided:
- Test connection to MySQL server
- Test connection to selected database
- Save connection parameters
- Create database and tables
- Reset entire database (double confirmation)

A live log area records operation outcomes for troubleshooting.

### 8.4 Management Tabs
Tab 1: Dashboard
- total products
- total customers
- total orders
- total revenue
- top customer by total spending
- top-selling product by quantity

Tab 2: Product Management
- form-based create and update
- table listing
- keyword search
- delete or deactivate behavior

Tab 3: Customer Management
- form-based CRUD
- keyword search
- purchase history table by selected customer
- invoice detail table by selected order

Tab 4: Order Processing
- customer selection with filters
- product selection with filters
- quantity input via spinner
- cart management and total calculation
- save order and refresh recent order list

### 8.5 Interaction Design Strengths
- Most actions include immediate feedback dialog.
- Invalid or missing input is rejected early.
- Table selection auto-fills forms for efficient editing.
- Data refresh callbacks keep modules synchronized.

---

## 9. Implementation Details
### 9.1 Entry Point and Application Bootstrap
The main class launches Swing UI on the Event Dispatch Thread and applies system look-and-feel before showing MainFrame. This aligns with Swing thread safety best practices.

### 9.2 Configuration and Connection Layer
DatabaseConfig:
- stores host, port, database name, username, password
- builds JDBC root URL and database URL
- supports runtime update of active parameters

DbConnection:
- provides server-level and database-level connections
- offers overloaded methods for both current config and explicit params

DbInitializer:
- creates database if missing
- creates all required tables
- ensures products table has is_active column
- provides database reset with database name validation

### 9.3 DAO Layer
ProductDao:
- supports list, search, insert, update, delete, findById
- hides SQL details from UI
- includes SQL error formatting for clearer diagnostics

CustomerDao:
- supports list, search, insert, update, delete, findById

DashboardDao:
- executes aggregation queries for metrics and rankings

### 9.4 Service Layer
OrderService handles business-critical workflows:
- loads active in-stock products
- loads customers for ordering
- creates orders in a transaction
- returns recent orders
- returns customer order history and order item details

This design centralizes transaction logic outside UI classes, reducing coupling and increasing reliability.

### 9.5 UI Layer
MainFrame orchestrates navigation and delayed initialization of heavy management components. Separate panels encapsulate each business domain. This keeps classes focused and easier to maintain.

### 9.6 Error Handling
Error handling strategy includes:
- try-with-resources for all JDBC operations
- conversion of SQL exceptions to runtime exceptions with context
- user-facing dialogs for operation results and failures
- safe rollback on transactional failure

---

## 10. Testing and Validation
### 10.1 Testing Strategy
Testing in this project is primarily functional and integration-oriented:
- validate UI actions against expected database changes
- validate constraint behavior under invalid operations
- validate transaction rollback on stock errors

### 10.2 Core Test Scenarios
Scenario A: Product CRUD
- add product with valid fields
- update existing product
- search by keyword
- delete product with and without historical references

Expected result: operations reflect correctly in table view and database.

Scenario B: Customer CRUD and History
- add and edit customer data
- remove customer without order references
- select customer and verify history table

Expected result: customer table and history panel stay synchronized.

Scenario C: Order Creation
- add items to cart within stock limits
- save order
- verify order header and line items persisted
- verify stock decreases correctly

Expected result: atomic save with correct totals and updated inventory.

Scenario D: Validation and Error Conditions
- submit empty form fields
- set negative price or stock
- exceed available stock in cart
- attempt connection with wrong DB credentials

Expected result: clear error message and no inconsistent data writes.

Scenario E: Dashboard Accuracy
- execute transactions
- refresh dashboard
- compare counts and totals with direct SQL checks

Expected result: dashboard values match underlying data.

### 10.3 Validation Outcome
Observed behavior confirms that the system correctly enforces main functional rules and preserves data consistency across typical usage scenarios. The most critical workflow, order creation with stock update, is transaction-safe.

---

## 11. Results and Discussion
### 11.1 Functional Results
The completed application achieves all essential objectives:
- complete product and customer management
- practical POS-like order workflow
- automatic stock control
- historical transaction traceability
- dashboard-level monitoring
- integrated database administration

### 11.2 Business Impact Perspective
For a small electronics shop, the system can reduce manual workload and improve operational accuracy by:
- preventing overselling through stock checks
- preserving customer and order history for service quality
- enabling rapid overview of business health through dashboard metrics

### 11.3 Technical Quality Discussion
Strengths:
- clean package separation
- transactional service logic
- SQL resource safety via try-with-resources
- usability-focused desktop interface

Trade-offs:
- direct JDBC approach requires manual SQL maintenance
- limited test automation
- no user authentication layer

### 11.4 Academic Contribution
This capstone demonstrates integration of core software engineering domains:
- GUI engineering
- relational modeling
- transaction control
- CRUD lifecycle management
- architecture-driven implementation

It also demonstrates practical deployment concerns by embedding database setup tools into the application itself.

---

## 12. Limitations
Although functional and stable for targeted use, the current version has limitations:
- no login system or role-based access control
- no export to PDF/Excel reports
- no image management for products
- no low-stock alert mechanism
- no audit log for user actions
- no distributed or multi-user conflict management
- limited automated unit/integration tests

These limitations are acceptable for an academic baseline but should be addressed for production-grade deployment.

---

## 13. Future Work
Planned and recommended enhancements:
1. Authentication and Authorization
- admin and staff roles
- permission-based action control

2. Reporting Module
- printable invoice templates
- periodic sales and inventory reports
- export to CSV, Excel, and PDF

3. Inventory Intelligence
- low-stock alerts
- reorder suggestion thresholds
- supplier management integration

4. Product Catalog Enrichment
- product image support
- barcode integration
- category hierarchy management

5. Deployment and Scalability
- REST API backend migration option
- web or mobile client extension
- cloud database and backup strategy

6. Quality Engineering
- unit tests for DAO and service logic
- integration tests for transaction scenarios
- continuous integration pipeline

---

## 14. Conclusion
This capstone project successfully delivers a desktop retail management system that unifies user interface workflows with a transactional relational database. Electro Store Manager solves key operational problems in electronics retail, especially in product tracking, customer management, and order processing.

The system shows that Java Swing remains a viable option for local business applications when paired with solid database design and disciplined architecture. Through layered implementation, transaction-safe order handling, and integrated database setup features, the project achieves both practical utility and academic value.

In summary, Electro Store Manager can serve as:
- a deployable baseline for small-store digitalization
- a reference architecture for desktop CRUD-plus-transaction systems
- a foundation for advanced enhancements in analytics, security, and multi-platform delivery

The project fulfills the capstone objective of producing an end-to-end software artifact that demonstrates analysis, design, implementation, and evaluation in a coherent and technically grounded manner.

---

## 15. References
1. Oracle. Java Platform, Standard Edition Documentation.  
2. Oracle. JDBC Basics and Best Practices.  
3. MySQL Documentation. Data Definition, Constraints, and Transactions.  
4. Apache Maven Project Documentation.  
5. Pressman, R. S., and Maxim, B. R. Software Engineering: A Practitioner’s Approach.  
6. Elmasri, R., and Navathe, S. B. Fundamentals of Database Systems.

---

## 16. Appendix
### Appendix A: Build and Run Instructions
1. Ensure Java 17+ and Maven are installed.  
2. Ensure MySQL server is running.  
3. Compile project:
   - mvn clean compile
4. Run application:
   - mvn exec:java

### Appendix B: SQL Initialization Scripts
The project includes SQL scripts to create schema and optional sample data:
- sql/electro_store.sql
- sql/data_populate.sql

### Appendix C: Source Code Module Map
- src/main/java/com/electrostore/Main.java
- src/main/java/com/electrostore/config
- src/main/java/com/electrostore/model
- src/main/java/com/electrostore/dao
- src/main/java/com/electrostore/service
- src/main/java/com/electrostore/ui

### Appendix D: Example Use-Case Walkthrough
Use-case: Create Order
1. Open management area and navigate to Ban hang tab.  
2. Select a customer.  
3. Filter/select products and set quantity.  
4. Add products to cart.  
5. Confirm and save invoice.  
6. System stores order, stores order items, updates stock, and refreshes dashboard.

### Appendix E: Suggested Evaluation Rubric Alignment
- Requirement fulfillment: achieved for all core CRUD and order functions.  
- Technical implementation quality: modular and transaction-aware.  
- Database correctness: normalized core model with referential constraints.  
- UI usability: consistent and task-focused panel design.  
- Extensibility: clear package structure for future upgrades.
