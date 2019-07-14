package core.minecraft.inventory.data;

/**
 * This is a data that is used when a player's cosmetic inventory quantity is updated.
 *
 * @author Preston Brown
 */
public class ItemQuantityToken {

    /**
     * The player's clientID.
     */
    public int clientID;

    /**
     * The change in the quantity
     */
    public int quantity;

    /**
     * The item whose quantity is being changed.
     */
    public String item;

    /**
     * The category of item.
     */
    public String category;

    /**
     * Creates a new {@link ItemQuantityToken} instance.
     *
     * @param clientID the player's clientID
     * @param quantity the change in the quantity
     * @param item the item whose quantity is being changed
     * @param cateogry the category of item
     */
    public ItemQuantityToken(int clientID, int quantity, String item, String cateogry)
    {
        this.clientID = clientID;
        this.quantity = quantity;
        this.item = item;
        this.category = cateogry;
    }

    /**
     * Adds to the quantity of the existing token.
     *
     * @param quantity the change in the quantity
     */
    public void addToken(int quantity)
    {
        this.quantity += quantity;
    }
}
