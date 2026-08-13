package Main_Package;

import Service_Package.*;
import Connection_Package.DBConnection;
import Item_Package.Item;
import BST_Package.ItemBST;

import java.util.*;
import java.sql.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        CartService cartService = new CartService();
        BillingService billingService = new BillingService();
        Customer_Service customerService = new Customer_Service();
        Connection con = DBConnection.getConnection();

        String item_table = """
        CREATE TABLE IF NOT EXISTS items (
        id INT(11) NOT NULL AUTO_INCREMENT,
        name VARCHAR(50) NOT NULL UNIQUE,
        price DOUBLE NOT NULL,
        quantity INT(11) NOT NULL,
        PRIMARY KEY (id))
        """;
        Statement st1 = con.createStatement();
        st1.executeUpdate(item_table);

        String cart_table = """
        CREATE TABLE IF NOT EXISTS cart (
        id INT AUTO_INCREMENT PRIMARY KEY,
        item_id INT NOT NULL,
        quantity INT NOT NULL,
        FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE ON UPDATE CASCADE)
        """;
        Statement st2 = con.createStatement();
        st2.executeUpdate(cart_table);

        String customer_table = """
    CREATE TABLE IF NOT EXISTS customers (
        id INT AUTO_INCREMENT PRIMARY KEY,
        CustName VARCHAR(100) NOT NULL,
        MoNo Varchar(15) NOT NULL UNIQUE
    )
    """;
        Statement st3 = con.createStatement();
        st3.executeUpdate(customer_table);

        String sales_table = """
    CREATE TABLE IF NOT EXISTS sales (
        id INT AUTO_INCREMENT PRIMARY KEY,
        date DATE DEFAULT (CURRENT_DATE),
        customer_id INT,
        item_name VARCHAR(100),
        item_price DOUBLE,
        quantity INT,
        total_price DOUBLE,
        FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
    )
    """;
        Statement st4 = con.createStatement();
        st4.executeUpdate(sales_table);

        String bills_table = """
    CREATE TABLE IF NOT EXISTS bills (
        id INT AUTO_INCREMENT PRIMARY KEY,
        date DATE DEFAULT (CURRENT_DATE),
        customer_id INT,
        bill LONGBLOB,
        FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
    )
    """;
        Statement st5 = con.createStatement();
        st5.executeUpdate(bills_table);



        ItemService itemService = new ItemService();
        while (true) {
            System.out.println("\n--- Grocery Billing System ---");
            System.out.println("1. Admin Panel");
            System.out.println("2. Customer Panel");
            System.out.println("3. Exit");

            int choice1=0;  //checks valid input
            System.out.print("Choose option: ");
            if (sc.hasNextInt()) {
                choice1 = sc.nextInt();
                System.out.println("You entered: " + choice1);
            } else {
                sc.next(); // clear the invalid input from scanner
            }

            if(choice1==1){  // Admin panel
                boolean flag = true;
                while (flag){
                    System.out.println("\n--- Admin Panel ---");
                    System.out.println("1. Item Management");
                    System.out.println("2. Sales Management");
                    System.out.println("3. Back");

                    int choice2=0;  //checks valid input
                    System.out.print("Choose option: ");
                    if (sc.hasNextInt()) {
                        choice2 = sc.nextInt();
                        System.out.println("You entered: " + choice2);
                    } else {
                        sc.next(); // clear the invalid input from scanner
                    }

                    if(choice2==1){ // item management
                        boolean flag2 = true;
                        while (flag2){
                            System.out.println("\n--- Item Management ---");
                            System.out.println("1. Add Item");
                            System.out.println("2. View Items");
                            System.out.println("3. Search Item");
                            System.out.println("4. Restock Item");
                            System.out.println("5. Remove Item");
                            System.out.println("6. Back");

                            int choice3=0;  //checks valid input
                            System.out.print("Choose option: ");
                            if (sc.hasNextInt()) {
                                choice3 = sc.nextInt();
                                System.out.println("You entered: " + choice3);
                            } else {
                                sc.next(); // clear the invalid input from scanner
                            }

                            switch (choice3){
                                case 1:
                                    try {
                                        System.out.print("Enter item name: ");
                                        String name = sc.next();

                                        System.out.print("Enter price: ");
                                        double price = sc.nextDouble();   // might throw InputMismatchException

                                        System.out.print("Enter quantity: ");
                                        int quantity = sc.nextInt();      // might throw InputMismatchException

                                        itemService.addItem(name, price, quantity);
                                    } catch (InputMismatchException e) {
                                        System.out.println("Invalid input! Please enter correct values.");
                                        sc.nextLine(); // clear the invalid input from the buffer
                                    }
                                    break;

                                case 2:
                                    ArrayList<Item> items1 = itemService.viewItems();
                                    System.out.println("--- Items in Store ---");
                                    System.out.printf("%-5s %-15s %-10s %-10s%n", "ID", "Name", "Price", "Quantity");
                                    System.out.println("------------------------------------------------");

                                    for (Item item : items1) {
                                        System.out.printf("%-5d %-15s %-10.2f %-10d%n",
                                                item.getID(), item.getName(), item.getPrice(), item.getQuantity());
                                    }
                                    break;

                                case 3:
                                    ItemBST bst = new ItemBST();
                                    ArrayList<Item> itemList = itemService.viewItems(); // view item method returns arraylist of items
                                    for (Item item : itemList) {
                                        bst.insert(item);  //inserts all object of item in bst
                                    }

                                    System.out.print("Enter item name to search: ");  //asks name to search
                                    String searchName = sc.next();

                                    Item found = bst.search(searchName); // searches item object from bst

                                    if (found != null) {
                                        System.out.println("Found -> ID: " + found.getID() + " | Name: " + found.getName() +
                                                " | Price: " + found.getPrice() + " | Qty: " + found.getQuantity());
                                    } else {
                                        System.out.println("Item not found.");
                                    }
                                    break;

                                case 4:
                                    try {
                                        System.out.println("Enter Item ID to restock: ");
                                        int id1 = sc.nextInt();

                                        System.out.println("Enter Quantity to Restock: ");
                                        int stock = sc.nextInt();

                                        if (stock <= 0) {
                                            System.out.println("Invalid quantity! Please enter a positive number.");
                                        } else {
                                            itemService.restockItem(id1, stock);
                                            System.out.println("Item Restocked!");
                                        }
                                    } catch (InputMismatchException e) {
                                        System.out.println("Invalid input! Please enter numeric values only.");
                                        sc.nextLine(); // clear invalid input from scanner buffer
                                    }

                                    break;
                                case 5:
                                    try {
                                        System.out.println("Enter item ID: ");
                                        int id = sc.nextInt();

                                        if (id <= 0) {
                                            System.out.println("Invalid ID! Please enter a positive number.");
                                        } else {
                                            itemService.removeItem(id);
                                        }
                                    } catch (InputMismatchException e) {
                                        System.out.println("Invalid input! Please enter a valid numeric ID.");
                                        sc.nextLine(); // clear invalid input from scanner buffer
                                    }
                                    break;
                                case 6:
                                    flag2 = false;
                                    break;
                                default:
                                    System.out.println("Invalid Choice!!!");
                            }
                        }
                    }

                    else if (choice2==2) { //sales management
                        boolean flag2 =true;
                        while (flag2){
                            System.out.println("\n--- Sales Management ---");
                            System.out.println("1. View Sales");
                            System.out.println("2. View All Bills");
                            System.out.println("3. View Bill");
                            System.out.println("4. Back");

                            int choice3=0;  //checks valid input
                            System.out.print("Choose option: ");
                            if (sc.hasNextInt()) {
                                choice3 = sc.nextInt();
                                System.out.println("You entered: " + choice3);
                            } else {
                                sc.next(); // clear the invalid input from scanner
                            }

                            switch (choice3){
                                case 1:
                                    billingService.displaySalesTable();
                                    break;
                                case 2:
                                    billingService.displayAllBills();
                                    break;
                                case 3:
                                    billingService.displayAllBills();

                                    try {
                                        System.out.print("Enter Bill ID: ");
                                        int Bill_id = sc.nextInt();

                                        if (Bill_id <= 0) {
                                            System.out.println("Invalid Bill ID! Please enter a positive number.");
                                        } else {
                                            billingService.displayBillById(Bill_id);
                                        }
                                    } catch (InputMismatchException e) {
                                        System.out.println("Invalid input! Please enter a numeric Bill ID.");
                                        sc.nextLine(); // clear the invalid input from scanner buffer
                                    }

                                    break;
                                case 4:
                                    flag2 = false;
                                    break;
                                default:
                                    System.out.println("Invalid Choice!!!");
                            }
                        }
                    }
                    else if (choice2==3) {
                        flag=false;
                        break;
                    }
                    else {
                        System.out.println("Invalid Choice!!!");
                    }
                }
            }
            else if (choice1==2) {//customer panel
                System.out.println("----- Customer Portal -----\n");

                int customerId = -1;
                while (customerId == -1) {
                    customerId = customerService.startCustomerService();
                    if(customerId == -2){
                        break;
                    }
                }

                if (customerId == -2) {
                    continue;  // this will jump back to the Main menu loop
                }

                System.out.println("\n----------------------------");
                System.out.println("Customer ID: " + customerId + " logged in successfully!");


                boolean flag = true;
                while (flag){
                    System.out.println("\n--- Customer Panel ---");
                    System.out.println("1. View Items");
                    System.out.println("2. Search Item");
                    System.out.println("3. Add To Cart");
                    System.out.println("4. View Cart");
                    System.out.println("5. Remove From Cart");
                    System.out.println("6. Checkout");
                    System.out.println("7. View Bill");
                    System.out.println("8. Back");

                    int choice3=0;  //checks valid input
                    System.out.print("Choose option: ");
                    if (sc.hasNextInt()) {
                        choice3 = sc.nextInt();
                        System.out.println("You entered: " + choice3);
                    } else {
                        sc.next(); // clear the invalid input from scanner
                    }

                    switch (choice3){
                        case 1:
                            ArrayList<Item> items1 = itemService.viewItems();
                            System.out.println("--- Items in Store ---");
                            System.out.printf("%-5s %-15s %-10s %-10s%n", "ID", "Name", "Price", "Quantity");
                            System.out.println("------------------------------------------------");

                            for (Item item : items1) {
                                System.out.printf("%-5d %-15s %-10.2f %-10d%n",
                                        item.getID(), item.getName(), item.getPrice(), item.getQuantity());
                            }
                            break;
                        case 2:
                            ItemBST bst = new ItemBST();
                            ArrayList<Item> itemList = itemService.viewItems();
                            for (Item item : itemList) {
                                bst.insert(item);
                            }

                            System.out.print("Enter item name to search: ");
                            String searchName = sc.next();

                            Item found = bst.search(searchName);
                            if (found != null) {
                                System.out.println("Found -> ID: " + found.getID() + " | Name: " + found.getName() +
                                        " | Price: " + found.getPrice() + " | Qty: " + found.getQuantity());
                            } else {
                                System.out.println("Item not found.");
                            }
                            break;
                        case 3:

                            ArrayList<Item> items2 = itemService.viewItems();
                            System.out.println("--- Items in Store ---");
                            System.out.printf("%-5s %-15s %-10s %-10s%n", "ID", "Name", "Price", "Quantity");
                            System.out.println("------------------------------------------------");

                            for (Item item : items2) {
                                System.out.printf("%-5d %-15s %-10.2f %-10d%n",
                                        item.getID(), item.getName(), item.getPrice(), item.getQuantity());
                            }

                            try {
                                System.out.print("Enter item ID to add to cart: ");
                                int itemId1 = sc.nextInt();

                                System.out.print("Enter quantity: ");
                                int qty = sc.nextInt();

                                if (itemId1 <= 0) {
                                    System.out.println("Invalid Item ID! Please enter a positive number.");
                                } else if (qty <= 0) {
                                    System.out.println("Invalid quantity! Please enter a positive number.");
                                } else {
                                    cartService.addToCart(itemId1, qty);
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("Invalid input! Please enter numeric values only.");
                                sc.nextLine(); // clear invalid input from scanner buffer
                            }

                            break;
                        case 4:
                            ArrayList<Item> items3 = cartService.ViewCart();
                            if (items3.isEmpty()) {
                                System.out.println("Cart is empty!!!");
                            } else {
                                System.out.println("--- Items in Cart ---");
                                for (Item item : items3) {
                                    System.out.println("ID: " + item.getID() + " Name: " + item.getName()
                                            + " Price: " + item.getPrice() + " Qty: " + item.getQuantity());
                                }
                            }
                            break;
                        case 5:
                            try(
                                 Statement st = con.createStatement();
                                 ResultSet rs = st.executeQuery("SELECT COUNT(*) AS cnt FROM cart")) {

                                if (rs.next() && rs.getInt("cnt") == 0) {
                                    System.out.println("Cart is empty!");
                                    break; // exit case
                                }

                                System.out.println("Enter Cart ID to remove item: ");
                                int cartId = sc.nextInt();

                                if (cartId <= 0) {
                                    System.out.println("Invalid Cart ID! Please enter a positive number.");
                                } else {
                                    boolean removed = cartService.RemoveFromCart(cartId);
                                    if (removed) {
                                        System.out.println("Item removed from cart!");
                                    } else {
                                        System.out.println("No item found in cart with ID: " + cartId);
                                    }
                                }
                            } catch (InputMismatchException e) {
                                System.out.println("Invalid input! Please enter a numeric Cart ID.");
                                sc.nextLine(); // clear invalid input
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;


                        case 6:
                            ArrayList<Item> cartItems = cartService.ViewCart();
                            if (cartItems.isEmpty()) {
                                System.out.println("Cart is empty! Cannot checkout.");
                                break;
                            }
                            cartService.checkout(sc,billingService,customerService);
                            break;
                        case 7:
                            billingService.ShowRecentBillFromFile();
                            break;
                        case 8:
                            flag=false;
                            BillingService.clearBillFile();
                            cartService.ClearCartWithRestock();
                            break;
                        default:
                            System.out.println("Invalid Choice!!!");
                    }
                }

            }
            else if (choice1==3) {
                System.out.println("Exiting...");
                return;
            }
            else {
                System.out.println("Invalid Choice!!!");
            }
        }
    }
}
