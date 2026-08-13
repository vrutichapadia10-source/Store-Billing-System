package Item_Package;

public class Item {

    private int ID;
    private String Name;
    private Double Price;
    private int Quantity;

    public Item(int ID, String name, Double price, int quantity) {
        this.ID = ID;
        Name = name;
        Price = price;
        Quantity = quantity;
    }

    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public String getName() { return Name; }
    public void setName(String name) { Name = name; }

    public Double getPrice() { return Price; }
    public void setPrice(Double price) { Price = price; }

    public int getQuantity() { return Quantity; }
    public void setQuantity(int quantity) { Quantity = quantity; }
}
