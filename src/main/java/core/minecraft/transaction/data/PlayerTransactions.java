package core.minecraft.transaction.data;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Stores a player's transaction history.
 *
 * @author Preston Brown
 */
public class PlayerTransactions {

    private String _player;
    private int _crystalCount;
    private HashMap<String, Integer> _purchasedCosmeticItems = new HashMap<>();
    private HashMap<String, Integer> _purchasedOtherItems = new HashMap<>();

    /**
     * Creates a new PlayerTransaction instance that represents a player's transaction history.
     *
     * @param player the name of the player
     * @param crystalCount the number of crystals this player owns
     * @param purchasedCosmeticItems the cosmetic items that this player owns
     * @param purchasedOtherItems other items that this player owns
     */
    public PlayerTransactions(String player, int crystalCount, HashMap<String, Integer> purchasedCosmeticItems, HashMap<String, Integer> purchasedOtherItems)
    {
        _player = player;
        _crystalCount = crystalCount;
        _purchasedCosmeticItems = purchasedCosmeticItems;
        _purchasedOtherItems = purchasedOtherItems;
    }

    /**
     * Increases the player's crystal count.
     *
     * @param count the amount of crystals being rewarded
     */
    public void rewardCrystals(int count)
    {
        _crystalCount += count;
    }

    /**
     * Decreases the player's crystal count.
     *
     * @param count the amount of crystals being removed
     */
    public void removeCrystals(int count)
    {
        _crystalCount -= count;
    }

    /**
     * @return the player this instance is representing
     */
    public String getPlayer()
    {
        return _player;
    }

    /**
     * @return this player's crystal count
     */
    public int getCrystalCount()
    {
        return _crystalCount;
    }

    /**
     * @return the cosmetics this player has purchased
     */
    public HashMap<String, Integer> getPurchasedCosmeticItems()
    {
        return _purchasedCosmeticItems;
    }

    /**
     * @return the items this player has purchased
     */
    public HashMap<String, Integer> getPurchasedOtherItems()
    {
        return _purchasedOtherItems;
    }

    /**
     * Adds this item to the player's purchased cosmetic items.
     *
     * @param name the name of the cosmetic
     * @param quantity the updated quantity
     */
    public void purchasedCosmeticItem(String name, int quantity)
    {
        if (_purchasedCosmeticItems.containsKey(name))
        {
            _purchasedCosmeticItems.put(name, _purchasedCosmeticItems.get(name) + quantity);
        }
        else
        {
            _purchasedCosmeticItems.put(name, quantity);
        }
    }

    /**
     * Adds this item to the player's purchased other items.
     *
     * @param name the name of the item
     * @param quantity the updated quantity
     */
    public void purchasedOtherItem(String name, int quantity)
    {
        if (_purchasedOtherItems.containsKey(name))
        {
            _purchasedOtherItems.put(name, _purchasedOtherItems.get(name) + quantity);
        }
        else
        {
            _purchasedOtherItems.put(name, quantity);
        }
    }
}
