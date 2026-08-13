package Service_Package;

import Item_Package.Item;

import java.sql.*;
import java.util.ArrayList;

public class ItemService extends BaseService{

    public void showServiceInfo() {
        System.out.println("Cart Service: Manages cart operations.");
    }

    public void addItem(String name, double price, int quantity) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("Item name cannot be empty!");
            return;
        }
        if (price <= 0) {
            System.out.println("Price must be positive!");
            return;
        }
        if (quantity <= 0) {
            System.out.println("Quantity must be positive!");
            return;
        }

        String query = "INSERT INTO items (name, price, quantity) VALUES (?, ?, ?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, quantity);
            ps.executeUpdate();
            System.out.println("Item added!");
        } catch (Exception e) {
            System.out.println("Item Already Exist!!");
        }
    }

    public void restockItem(int ID, int Quantity) {
        String updateQuery = "UPDATE items SET quantity = quantity + ? WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(updateQuery)) {
            ps.setInt(1, Quantity); // this will add to existing stock
            ps.setInt(2, ID);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeItem(int ID) {
        String query = "DELETE FROM items WHERE id = ?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, ID);
            int rowsAffected = ps.executeUpdate();  // returns number of rows deleted

            if (rowsAffected > 0) {
                System.out.println("Item Removed!");
            } else {
                System.out.println("No item found with ID: " + ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Item> viewItems() {
        ArrayList<Item> items = new ArrayList<>();
        String query = "SELECT * FROM items";
        try (Connection con = getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Item item = new Item(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity")
                );
                items.add(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }
}
