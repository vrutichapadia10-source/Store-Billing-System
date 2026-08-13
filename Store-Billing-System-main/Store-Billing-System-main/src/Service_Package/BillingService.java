package Service_Package;

import Item_Package.Item;
import Connection_Package.DBConnection;

import java.sql.*;
import java.util.*;
import java.io.*;


public class BillingService extends BaseService{

    public void showServiceInfo() {
        System.out.println("Billing Service: Manages Billing operations.");
    }

    public void saveBillToFile(Map<Item, Integer> cartItems, double total, int customerId) {
        String customer_name = "";
        String mobile_no = "";

        // Fetch customer details from DB
        String sql = "SELECT CustName, MoNo FROM customers WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                customer_name = rs.getString("CustName");
                mobile_no = rs.getString("MoNo");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Save bill to file
        try (FileWriter writer = new FileWriter("bill.txt", false)) {
            writer.write("----- New Bill -----\n");
            for (Map.Entry<Item, Integer> entry : cartItems.entrySet()) {
                Item item = entry.getKey();
                int qty = entry.getValue();
                writer.write(item.getName() + " x " + qty + " = ₹" + (item.getPrice() * qty) + "\n");
            }
            writer.write("Total: ₹" + total + "\n");
            writer.write("Customer name: " + customer_name + "\n");
            writer.write("Mobile no: " + mobile_no + "\n");
            writer.write("---------------------\n\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void uploadSalesToDatabase(Map<Item, Integer> billMap, int customerId) {
        String insertSQL = "INSERT INTO sales (customer_id, item_name, item_price, quantity, total_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(insertSQL)) {

            for (Map.Entry<Item, Integer> entry : billMap.entrySet()) {
                Item item = entry.getKey();
                int quantity = entry.getValue();
                double itemTotal = item.getPrice() * quantity;

                ps.setInt(1, customerId);
                ps.setString(2, item.getName());
                ps.setDouble(3, item.getPrice());
                ps.setInt(4, quantity);
                ps.setDouble(5, itemTotal);
                ps.addBatch();
            }
            ps.executeBatch();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void displaySalesTable() {
        String sql = """
        SELECT s.id, s.date, c.CustName, c.MoNo, s.item_name, s.item_price, s.quantity, s.total_price
        FROM sales s
        JOIN customers c ON s.customer_id = c.id
        ORDER BY s.date DESC
    """;

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("---- Sales Records ----");

            // Print header row
            System.out.printf("%-5s %-12s %-15s %-12s %-15s %-10s %-8s %-10s%n",
                    "ID", "Date", "Customer", "MoNo", "Item", "Price", "Qty", "Total");
            System.out.println("-------------------------------------------------------------------------------------------");

            // Print data rows
            while (rs.next()) {
                System.out.printf("%-5d %-12s %-15s %-12s %-15s %-10.2f %-8d %-10.2f%n",
                        rs.getInt("id"),
                        rs.getDate("date"),
                        rs.getString("CustName"),
                        rs.getString("MoNo"),
                        rs.getString("item_name"),
                        rs.getDouble("item_price"),
                        rs.getInt("quantity"),
                        rs.getDouble("total_price"));
            }

            System.out.println("-------------------------------------------------------------------------------------------");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void uploadBillToDatabase(int customerId) {
        String sql = "INSERT INTO bills (customer_id, bill) VALUES (?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            File file = new File("bill.txt");
            if (!file.exists()) {
                System.out.println("⚠️ bill.txt not found!");
                return;
            }

            try (FileInputStream fis = new FileInputStream(file)) {
                ps.setInt(1, customerId);
                ps.setBinaryStream(2, fis, (int) file.length());
                ps.executeUpdate();
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void displayAllBills() {
        String sql = """
        SELECT b.id, b.date, c.CustName, c.MoNo, b.bill
        FROM bills b
        JOIN customers c ON b.customer_id = c.id
        ORDER BY b.date DESC
    """;

        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("---- Bills ----");

            // Print header row
            System.out.printf("%-8s %-12s %-20s %-12s %-10s%n",
                    "BillID", "Date", "Customer", "MoNo", "Bill(BLOB)");
            System.out.println("-----------------------------------------------------------------");

            // Print rows
            while (rs.next()) {
                System.out.printf("%-8d %-12s %-20s %-12s %-10s%n",
                        rs.getInt("id"),
                        rs.getDate("date"),
                        rs.getString("CustName"),
                        rs.getString("MoNo"),
                        (rs.getBlob("bill") != null ? "[Stored]" : "NULL"));
            }

            System.out.println("-----------------------------------------------------------------");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void displayBillById(int billId) {
        try {
            Connection con = getConnection();

            String sql = "SELECT bill FROM bills WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, billId);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // Get BLOB as binary stream
                InputStream inputStream = rs.getBinaryStream("bill");
                if (inputStream != null) {
                    // Read the stream into a String (since it's a text file)
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    System.out.println("----- Bill Content for ID " + billId + " -----");
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                    reader.close();
                } else {
                    System.out.println("No bill found for ID " + billId);
                }
            } else {
                System.out.println("No record found for ID " + billId);
            }

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ShowRecentBillFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("bill.txt"))) {
            String line;
            System.out.println("----- Current Bill -----\n");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            System.out.println("\n--------------------------");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearBillFile() {
        try (FileWriter fw = new FileWriter("bill.txt", false)) {
            // false → overwrite file instead of appending
            fw.write("Bill Empty\n");
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
