package Service_Package;

import Connection_Package.DBConnection;

import java.util.Scanner;

import java.sql.*;

public class Customer_Service extends BaseService {

    private String Customer_name;
    private String Customer_mobile;
    private int customer_id;


    @Override
    public void showServiceInfo() {
        System.out.println("Cart Service: Manages Customer login & registration.");
    }

    public int startCustomerService() {
        Scanner sc = new Scanner(System.in);

        System.out.println("1. Register");
        System.out.println("2. Login");

        int choice=-1;
        System.out.print("Choose option(or type 0 to cancel): ");
        if (sc.hasNextInt()) {  //checks valid int input
            choice = sc.nextInt();
            System.out.println("You entered: " + choice);
        } else {
            sc.next(); // clear the invalid input from scanner
            System.out.println("❌ Invalid input! Please enter a number.");
        }

        if(choice==0){
            System.out.println("↩️  Returning to main menu...");
            return -2;
        }
        if (choice == 1) {
            return registerCustomer(sc); // returns id or -1
        } else if (choice == 2) {
            return loginCustomer(sc); // returns id or -1
        } else {
            System.out.println("Invalid choice!");
            return -1;
        }
    }

    private int registerCustomer(Scanner sc) {
        System.out.print("Enter Customer Name: ");
        sc.nextLine();
        String name = sc.nextLine().trim();

        String mobile;
        while (true) {
            System.out.print("Enter Mobile Number (or type Exit to cancel): ");
            mobile = sc.nextLine().trim();

            if (mobile.equalsIgnoreCase("Exit")) {
                System.out.println("↩️  Registration cancelled. Returning to main menu...");
                return -1;  // or handle according to your menu flow
            }

            // Validate mobile number (10 digits, starts with 9)
            if (mobile.matches("^9\\d{9}$")) {
                break; // valid -> exit loop
            } else {
                System.out.println("Invalid Mobile Number! Must be 10 digits and start with 9.");
            }
        }

        try (Connection con = getConnection()) {
            // Check if already exists
            String checkSql = "SELECT id, CustName FROM customers WHERE MoNo = ?";
            try (PreparedStatement checkPs = con.prepareStatement(checkSql))
            {
                checkPs.setString(1, mobile);
                try (ResultSet rs = checkPs.executeQuery())
                {
                    if (rs.next()) {
                        this.customer_id = rs.getInt("id");
                        this.Customer_name = rs.getString("CustName");
                        this.Customer_mobile = mobile;
                        System.out.println("❌ Mobile already registered! Logging you in as " + Customer_name);
                        return customer_id;
                    }
                }
            }

            // Insert new
            String insertSql = "INSERT INTO customers (CustName, MoNo) VALUES (?, ?)";

            //returns new auto generated id from the customer table
            try (PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.setString(2, mobile);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        this.customer_id = rs.getInt(1);
                        this.Customer_name = name;
                        this.Customer_mobile = mobile;
                        System.out.println("✅ Registration successful!");
                        return customer_id;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }



    private int loginCustomer(Scanner sc) {
        String mobile;
        sc.nextLine();

        while (true) {
            System.out.print("Enter Mobile Number (or type Exit to cancel): ");
            mobile = sc.nextLine().trim();

            if (mobile.equalsIgnoreCase("exit")) {
                System.out.println("↩️  Login cancelled. Returning to main menu...");
                return -1; // cancel
            }

            // Validate mobile number (10 digits, starts with 9)
            if (mobile.matches("^9\\d{9}$")) {
                break; // valid → exit loop
            } else {
                System.out.println("❌ Invalid Mobile Number! Must be 10 digits and start with 9.");
            }
        }

        try (Connection con = getConnection()) {
            String sql = "SELECT id, CustName FROM customers WHERE MoNo = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, mobile);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    this.customer_id = rs.getInt("id");
                    this.Customer_name = rs.getString("CustName");
                    this.Customer_mobile = mobile;
                    System.out.println("✅ Login successful! Welcome, " + Customer_name);
                    return customer_id;
                } else {
                    System.out.println("❌ Mobile number not found. Please register first.");
                    return -1;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }


    public int getCustomer_id() {
        return customer_id;
    }

    public String getCustomer_name() {
        return Customer_name;
    }

    public String getCustomer_mobile() {
        return Customer_mobile;
    }
}
