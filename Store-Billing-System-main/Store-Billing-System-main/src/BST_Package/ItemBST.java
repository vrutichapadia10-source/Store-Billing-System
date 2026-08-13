package BST_Package;
import Item_Package.Item;

class ItemNode {
    public Item item;
    public ItemNode left, right;

    public ItemNode(Item item) {
        this.item = item;
        left = right = null;
    }
}


public class ItemBST {
    private ItemNode root;

    public void insert(Item item) {
        root = insertRec(root, item);
    }

    private ItemNode insertRec(ItemNode root, Item item) {   //Insertion Recursive method
        if (root == null) return new ItemNode(item); // checks base case

        if (item.getName().compareToIgnoreCase(root.item.getName()) < 0)  //if value is lower then it goes left
            root.left = insertRec(root.left, item);
        else if (item.getName().compareToIgnoreCase(root.item.getName()) > 0) //if value is greater it goes right
            root.right = insertRec(root.right, item);

        return root;
    }

    public Item search(String name) {
        return searchRec(root, name);
    }

    private Item searchRec(ItemNode root, String name) { //Search Recursive method
        if (root == null) return null;       //base case
        if (name.equalsIgnoreCase(root.item.getName())) return root.item; //checks root if matches then returns

        if (name.compareToIgnoreCase(root.item.getName()) < 0)  //if value is lower goes left
            return searchRec(root.left, name);

        return searchRec(root.right, name); //otherwise it goes right
    }

    public void inOrder() {
        inOrderRec(root);
    }

    private void inOrderRec(ItemNode root) {
        if (root != null) {
            inOrderRec(root.left);
            System.out.println("ID: " + root.item.getID() +
                    " | Name: " + root.item.getName() +
                    " | Price: " + root.item.getPrice() +
                    " | Qty: " + root.item.getQuantity());
            inOrderRec(root.right);
        }
    }
}
