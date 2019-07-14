package core.minecraft.inventory.data;

/**
 * This represents an item that exists in a player's virtual inventory.
 */
public class Item {

    private int _categoryID;
    private int _itemID;
    private String _itemName;

    /**
     * Creates a new Item.
     *
     * @param categoryID the item categoryID
     * @param itemID the item's ID
     * @param itemName the name of the item
     */
    public Item(int categoryID, int itemID, String itemName)
    {
        _categoryID = categoryID;
        _itemID = itemID;
        _itemName = itemName;
    }

    /**
     * Returns the categoryID this item falls under.
     *
     * @return this item's categoryID
     */
    public int getCategoryID()
    {
        return _categoryID;
    }

    /**
     * Returns this item's ID.
     *
     * @return this item's ID
     */
    public int getItemID()
    {
        return _itemID;
    }

    /**
     * Returns the name of this item.
     *
     * @return this item's name
     */
    public String getItemName()
    {
        return _itemName;
    }
}
