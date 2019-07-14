package core.minecraft.inventory.data;

import core.minecraft.common.CurrencyType;

import java.util.HashMap;

/**
 * Stores a player's transaction history.
 *
 * @author Preston Brown
 */
public class PlayerInventory {

    private String _player;
    private HashMap<String, Integer> _purchasedItems = new HashMap<>();

    /**
     * Creates a new PlayerTransaction instance that represents a player's transaction history.
     *
     * @param player the name of the player
     * @param purchasedItems the cosmetic items that this player owns
     */
    public PlayerInventory(String player, HashMap<String, Integer> purchasedItems)
    {
        _player = player;
        _purchasedItems = purchasedItems;
    }

    /**
     * @return the player this instance is representing
     */
    public String getPlayer()
    {
        return _player;
    }

    /**
     * @return the cosmetics this player has purchased
     */
    public HashMap<String, Integer> getPurchasedItems()
    {
        return new HashMap<>(_purchasedItems);
    }

    public int getItemQuantity(String item)
    {
        // if the item isn't in the map or the item's quantity is less than 0, return 0.
        // Otherwise return the item quantity.
        return _purchasedItems.get(item) == null || _purchasedItems.get(item) < 0 ? 0 : _purchasedItems.get(item);
    }

    /**
     * Adds this inventory to the player's purchased items.
     *
     * @param name the name of the cosmetic
     * @param quantity the updated quantity
     */
    public void addItem(String name, int quantity)
    {
        if (_purchasedItems.containsKey(name))
        {
            _purchasedItems.put(name, _purchasedItems.get(name) + quantity);
        }
        else
        {
            _purchasedItems.put(name, quantity);
        }
    }

    /**
     * Removes this inventory from the player's purchased items.
     *
     * If this would result in the player having a negative balance their balance is set to 0.
     *
     * @param name the name of the inventory given to play player
     * @param quantity
     */
    public void removeItem(String name, int quantity)
    {
        if (_purchasedItems.containsKey(name))
        {
            // Ensures the player never has a negative quantity
            _purchasedItems.put(name, Math.max(_purchasedItems.get(name) - quantity, 0));
        }
    }
}
