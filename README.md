# Store-Billing-System
Developed a Java-based Store Billing System to manage store items, customer purchases, shopping cart operations, billing, and sales records efficiently. Implemented separate Admin and Customer panels with item management, customer registration/login, cart management, checkout, bill generation, and sales tracking. Integrated Java, OOP, JDBC, MySQL, and Binary Search Tree (BST) for database management, business logic, and efficient item searching.

# Key Features
Admin and Customer panels, Add, view, search, restock, and remove store items, Customer registration and mobile-number based login, View available store items, Search items using Binary Search Tree, Add items to shopping cart, View and remove cart items, Automatic stock restocking when items are removed from cart, Checkout and total bill calculation, Generate and save customer bills, Store bills in MySQL database using BLOB, View recent bill, View all sales records, View all generated bills, Search and display bill by Bill ID, Customer details management, Input validation and error handling, MySQL database integration using JDBC, Technologies Used

# Technologies Used
**Frontend:**
Java, Console-Based User Interface, Scanner, Menu-Driven Interface

**Backend:**
Java, Object-Oriented Programming, JDBC, Business Logic, Service Classes

**Database:**
MySQL, JDBC, SQL Queries, Foreign Keys, BLOB

**Data Structures & Algorithms:**
Binary Search Tree (BST), ArrayList, Recursive Searching, Tree Traversal

**Core Concepts:**
Classes & Objects, Encapsulation, Abstraction, Inheritance, Methods, Loops, Conditional Statements, Exception Handling, Collections, SQL, CRUD Operations

# Project Structure
<pre>
Store-Billing-System/
│
├── src/
│   ├── Main_Package/
│   │   └── Main.java
│   │
│   ├── Item_Package/
│   │   └── Item.java
│   │
│   ├── BST_Package/
│   │   └── ItemBST.java
│   │
│   ├── Connection_Package/
│   │   └── DBConnection.java
│   │
│   └── Service_Package/
│       ├── BaseService.java
│       ├── ItemService.java
│       ├── CartService.java
│       ├── BillingService.java
│       └── Customer_Service.java
│
├── mysql-connector-j-8.1.0.jar
├── README.md
├── LICENSE
└── .gitignore
</pre>


# Database Tables
**items:**
Stores item name, price, and quantity.

**cart:**
Stores cart items and quantities.

**customers:**
Stores customer name and mobile number.

**sales:**
Stores customer purchase and sales information.

**bills:**
Stores generated bills as BLOB data.

