package Service_Package;

import Item_Package.Item;

import java.sql.*;
import java.util.*;

public class CartService extends BaseService{

    public void showServiceInfo() {
        System.out.println("Cart Service: Manages cart operations.");
    }

    public void addToCart(int id1, int quantity) {
        try (Connection con = getConnection()) {
            String select = "SELECT * FROM items WHERE id = ?";  //select item from cart
            PreparedStatement ps = con.prepareStatement(select);
            ps.setInt(1, id1);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int available_quantity = rs.getInt("quantity");

                if (quantity > 0 && quantity <= available_quantity) { //checks valid quantity
                    // Step 1: Check if item already exists in cart
                    String checkCart = "SELECT quantity FROM cart WHERE item_id = ?";
                    PreparedStatement checkStmt = con.prepareStatement(checkCart);
                    checkStmt.setInt(1, id1);
                    ResultSet cartResult = checkStmt.executeQuery();

                    if (cartResult.next()) {  //if exists then update query
                        int existingQty = cartResult.getInt("quantity");
                        int newQty = existingQty + quantity;

                        String updateCart = "UPDATE cart SET quantity = ? WHERE item_id = ?";
                        PreparedStatement updateStmt = con.prepareStatement(updateCart);
                        updateStmt.setInt(1, newQty);
                        updateStmt.setInt(2, id1);
                        updateStmt.executeUpdate();
                        updateStmt.close();

                        System.out.println("Item quantity updated in Cart!!!");
                    } else {  //insert item into cart
                        String insertCart = "INSERT INTO cart(item_id, quantity) VALUES (?, ?)";
                        PreparedStatement insertStmt = con.prepareStatement(insertCart);
                        insertStmt.setInt(1, id1);
                        insertStmt.setInt(2, quantity);
                        insertStmt.executeUpdate();
                        insertStmt.close();

                        System.out.println("Item added to cart!!!");
                    }

                    // Step 2: Decrease stock   //(this operation is handled by trigger function)
                    /*String updateStock = "UPDATE items SET quantity = quantity - ? WHERE id = ?";
                    PreparedStatement stockStmt = con.prepareStatement(updateStock);
                    stockStmt.setInt(1, quantity);
                    stockStmt.setInt(2, id1);
                    stockStmt.executeUpdate();
                    stockStmt.close();*/

                } else {
                    System.out.println("Invalid Quantity!!");
                }
            } else {
                System.out.println("Item not found!!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean RemoveFromCart(int cartId) {
        String selectCart = "SELECT item_id, quantity FROM cart WHERE id = ?"; //gets fk(item_id) and quantity from cart

        String deleteCart = "DELETE FROM cart WHERE id = ?";  //delete query

        String restockItem = "UPDATE items SET quantity = quantity + ? WHERE id = ?"; //restock query

        try (Connection con = getConnection();
             PreparedStatement psCart = con.prepareStatement(selectCart)) {

            psCart.setInt(1, cartId);
            try (ResultSet rsCart = psCart.executeQuery()) {
                if (!rsCart.next()) {
                    return false; // No cart item found
                }

                int itemId = rsCart.getInt("item_id");
                int qty = rsCart.getInt("quantity");

                // Step 1: Restock the item
                try (PreparedStatement psRestock = con.prepareStatement(restockItem)) {
                    psRestock.setInt(1, qty);
                    psRestock.setInt(2, itemId);
                    psRestock.executeUpdate();
                }

                // Step 2: Delete from cart
                try (PreparedStatement psDelete = con.prepareStatement(deleteCart)) {
                    psDelete.setInt(1, cartId);
                    psDelete.executeUpdate();
                }

                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public ArrayList<Item> ViewCart() {
        ArrayList<Item> items = new ArrayList<>();

        try (Connection con = getConnection()) {
            String select = """
            SELECT c.id, i.name, i.price, c.quantity
            FROM cart c
            JOIN items i ON c.item_id = i.id
            """;
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(select);

            while (rs.next()) {

                //creates item object for 1st item
                Item item = new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
                items.add(item);  //stores object oof 1st item in arraylist
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public void ClearCart() {
        try (Connection con = getConnection();
             Statement st = con.createStatement()) {
            // 1. Delete all records
            st.executeUpdate("DELETE FROM cart");
            // 2. Reset auto-increment
            st.executeUpdate("ALTER TABLE cart AUTO_INCREMENT = 1");

        } catch (Exception e) {
            System.out.println("Error clearing cart: " + e.getMessage());
        }
    }

    public void ClearCartWithRestock() {
        try (Connection con = getConnection()) {
            // Step 1: Fetch all cart items
            String selectCart = "SELECT item_id, quantity FROM cart";
            PreparedStatement psSelect = con.prepareStatement(selectCart);
            ResultSet rs = psSelect.executeQuery();

            // Step 2: For each cart item, restock back into items table
            while (rs.next()) {
                int itemId = rs.getInt("item_id");
                int qty = rs.getInt("quantity");

                String restock = "UPDATE items SET quantity = quantity + ? WHERE id = ?";
                PreparedStatement psRestock = con.prepareStatement(restock);
                psRestock.setInt(1, qty);
                psRestock.setInt(2, itemId);
                psRestock.executeUpdate();
                psRestock.close();
            }

            rs.close();
            psSelect.close();

            // Step 3: Clear cart
            Statement st = con.createStatement();
            st.executeUpdate("DELETE FROM cart");
            st.executeUpdate("ALTER TABLE cart AUTO_INCREMENT = 1");

        } catch (Exception e) {
            System.out.println("Error clearing cart with restock: " + e.getMessage());
        }
    }


    public void checkout(Scanner sc, BillingService billingService, Customer_Service customerService) {
        System.out.println("--- Checkout ---");

        Map<Item, Integer> billMap = new LinkedHashMap<>();
        double total = 0.0;

        try (Connection con = getConnection()) {
            String select = """
            SELECT c.id, i.name, i.price, c.quantity
            FROM cart c
            JOIN items i ON c.item_id = i.id
            """;
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(select);

            boolean cartEmpty = true;
            while (rs.next()) {
                cartEmpty = false;
                int id = rs.getInt("id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                int quantity = rs.getInt("quantity");

                Item item = new Item(id, name, price, quantity);
                billMap.put(item, quantity);
                total += price * quantity;

                System.out.println("ID: " + id + " Name: " + name + " Price: ₹" + price + " Qty: " + quantity);
            }

            if (cartEmpty) {
                System.out.println("Cart is empty. Cannot checkout.");
                return;
            }

            System.out.println("Total amount: ₹" + total);
            System.out.print("Confirm checkout? (yes/no): ");
            String confirm = sc.next();

            if (confirm.equalsIgnoreCase("yes")) {
                //get customer_id
                int customerId = customerService.getCustomer_id();

                billingService.saveBillToFile(billMap, total, customerId);
                billingService.uploadSalesToDatabase(billMap, customerId);
                billingService.uploadBillToDatabase(customerId);

                ClearCart();

                System.out.println("Checkout complete. Thank you for shopping!");
            } else {
                System.out.println("Checkout canceled.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}