package core.minecraft.item;

import core.minecraft.Component;
import core.minecraft.client.ClientManager;
import core.minecraft.common.Callback;
import core.minecraft.item.mysql.ItemRepository;
import core.minecraft.item.token.CosmeticQuantityToken;
import core.minecraft.item.token.OtherItemQuantityToken;
import core.minecraft.timer.TimerType;
import core.minecraft.timer.event.TimerEvent;
import core.minecraft.transaction.TransactionManager;
import core.minecraft.transaction.TransactionResponse;
import core.minecraft.transaction.data.PlayerTransactions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Manages all of the items the player owns.
 *
 * @author Preston Brown
 */
public class ItemManager extends Component implements Listener {

    private Object _cosmeticLock = new Object();
    private Object _otherItemLock = new Object();
    private ItemRepository _repository = new ItemRepository();
    private ClientManager _clientManager;
    private TransactionManager _transactionManager;
    private HashMap<CosmeticCategory, HashMap<String, Integer>> _cosmeticItems = new HashMap<>();
    private HashMap<String, List<CosmeticQuantityToken>> _updateCosmeticQuantityLater = new HashMap<>();
    private HashMap<String, List<OtherItemQuantityToken>> _updateOtherItemQuantityLater = new HashMap<>();

    /**
     * Creates a new instance of {@link ItemManager}.
     *
     * @param plugin the main JavaPlugin instance
     */
    public ItemManager(JavaPlugin plugin, ClientManager clientManager, TransactionManager transactionManager)
    {
        super("Item Manager", plugin);
        _clientManager = clientManager;
        _transactionManager = transactionManager;
        populateCosmeticItems();

        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    /**
     * Populates the hashmap that contains all of the cosmetic items.
     */
    private void populateCosmeticItems()
    {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                for (CosmeticCategory category : CosmeticCategory.values())
                {
                    _cosmeticItems.put(category, _repository.getItemsOfCategory(category.toString()));
                }
            }
        });
    }

    /**
     * Retrieves the item id of the cosmetic with the given name.
     *
     * @param name the name of the cosmetic
     * @return the item id of the cosmetic, -1 if the item with the given name wasn't found
     */
    public int getCosmeticItemID(String name)
    {
        for (HashMap<String, Integer> itemMap : _cosmeticItems.values())
        {
            int id;
            if (itemMap.containsKey(name))
            {
                return itemMap.get(name);
            }
        }
        return -1;
    }

    /**
     * Returns whether or not the player owns the specified cosmetic.
     *
     * @param player the player we are searching up
     * @param cosmetic the cosmetic we want to check if the player owns
     * @return true of the player owns the cosmetic, otherwise false
     */
    public boolean isCosmeticOwnedByPlayer(String player, String cosmetic)
    {
        return _transactionManager.getPlayerData(player).getPurchasedCosmeticItems().containsKey(cosmetic);
    }

    /**
     * Returns whether or not the player owns the specified item.
     *
     * @param player the player we are searching up
     * @param item the item we want to check if the player owns
     * @return true of the player owns the item, otherwise false
     */
    public boolean isOtherItemOwnedByPlayer(String player, String item)
    {
        return _transactionManager.getPlayerData(player).getPurchasedOtherItems().containsKey(item);
    }

    /**
     * Gets the quantity of the specified cosmetic the player owns.
     *
     * @param player the player
     * @param cosmetic the cosmetic you want to search
     * @return the quantity that the player owns of the cosmetic
     */
    public int getCosmeticQuantityForPlayer(String player, String cosmetic, boolean createIfNotExists)
    {
        if (_transactionManager.getPlayerData(player).getPurchasedCosmeticItems().containsKey(cosmetic))
        {
            return _transactionManager.getPlayerData(player).getPurchasedCosmeticItems().get(cosmetic);
        }
        if (createIfNotExists)
        {
            int itemID = getCosmeticItemID(cosmetic);
            if (itemID != -1)
            {
                int clientID = _clientManager.getPlayerData(player).getClientID();
                Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
                    @Override
                    public void run()
                    {
                        _transactionManager.purchaseCosmeticItem(clientID, player, itemID, cosmetic, 0, 0, false, new Callback<TransactionResponse>() {
                            @Override
                            public TransactionResponse call(TransactionResponse callback)
                            {
                                return callback;
                            }
                        });
                    }
                });
            }
        }
        return 0;
    }

    /**
     * Gets the quantity of the specified item the player owns.
     *
     * @param player the player
     * @param name the name of the item you want to search
     * @return the quantity that the player owns of the item
     */
    public int getOtherItemQuantityForPlayer(String player, String name, boolean createIfNotExists)
    {
        if (_transactionManager.getPlayerData(player).getPurchasedOtherItems().containsKey(name))
        {
            return _transactionManager.getPlayerData(player).getPurchasedOtherItems().get(name);
        }
        if (createIfNotExists)
        {
            int clientID = _clientManager.getPlayerData(player).getClientID();
            Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
                @Override
                public void run()
                {
                    _transactionManager.purchaseOtherItem(clientID, player, name, 0, 0, false, new Callback<TransactionResponse>() {
                        @Override
                        public TransactionResponse call(TransactionResponse callback) {
                            return callback;
                        }
                    });
                }
            });
        }
        return 0;
    }

    /**
     * Alters the player's quantity of the cosmetic by the specified amount.
     *
     * @param player the player
     * @param changeInQuantity the change in the quantity (ex. 1 to add 1 or -1 to remove 1)
     * @param cosmetic the name of the cosmetic
     */
    public void updatePlayerCosmeticQuantity(String player, int changeInQuantity, String cosmetic, Callback<Boolean> callback)
    {
        int currentQuantity = getCosmeticQuantityForPlayer(player, cosmetic, true);
        if ((currentQuantity + changeInQuantity) >= 0)
        {
            int clientID = _clientManager.getPlayerData(player).getClientID();
            int itemID = getCosmeticItemID(cosmetic);
            Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
                @Override
                public void run()
                {
                    _repository.updatePlayerCosmeticQuantity(clientID, itemID, changeInQuantity, new Callback<Boolean>() {
                        @Override
                        public Boolean call(Boolean transactionCallback)
                        {
                            if (transactionCallback)
                            {
                                _transactionManager.getPlayerData(player).purchasedCosmeticItem(cosmetic, changeInQuantity);
                            }
                            callback.call(transactionCallback);
                            return transactionCallback;
                        }
                    });
                }
            });
        }
        else
        {
            callback.call(false);
        }
    }

    /**
     * Alters the player's quantity of the cosmetic by the specified amount after a short period of time. This should be
     * used if the quantity of the specified cosmetic is expected to change multiple times withing a short period of time.
     *
     * @param player the player
     * @param changeInQuantity the change in quantity (ex. 1 to add 1 or -1 to remove 1)
     * @param cosmetic the name of the cosmetic
     */
    public void updatePlayerCosmeticQuantityDelayed(String player, int changeInQuantity, String cosmetic)
    {
        int clientID = _clientManager.getPlayerData(player).getClientID();
        int itemID = getCosmeticItemID(cosmetic);
        getCosmeticQuantityForPlayer(player, cosmetic, true);
        synchronized (_cosmeticLock)
        {
            if (_updateCosmeticQuantityLater.containsKey(player))
            {
                boolean updated = false;
                for (CosmeticQuantityToken token : _updateCosmeticQuantityLater.get(player))
                {
                    if (token.getItemID() == itemID)
                    {
                        updated = true;
                        token.addToken(changeInQuantity);
                    }
                }
                if (!updated)
                {
                    _updateCosmeticQuantityLater.get(player).add(new CosmeticQuantityToken(clientID, changeInQuantity, itemID));
                }
            }
            else
            {
                _updateCosmeticQuantityLater.put(player, new ArrayList<CosmeticQuantityToken>());
                _updateCosmeticQuantityLater.get(player).add(new CosmeticQuantityToken(clientID, changeInQuantity, itemID));
            }
        }
    }

    /**
     * Alters the player's quantity of the item by the specified amount.
     *
     * @param player the player
     * @param changeInQuantity the difference in quantity (ex. 1 to add 1 or -1 to remove 1)
     * @param item the name of the item
     */
    public void updatePlayerOtherItemQuantity(String player, int changeInQuantity, String item, Callback<Boolean> callback)
    {
        int currentQuantity = getOtherItemQuantityForPlayer(player, item, true);
        if ((currentQuantity + changeInQuantity) >= 0)
        {
            int clientID = _clientManager.getPlayerData(player).getClientID();
            Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
                @Override
                public void run()
                {
                    _repository.updatePlayerOtherItemQuantity(clientID, item, changeInQuantity, new Callback<Boolean>() {
                        @Override
                        public Boolean call(Boolean transactionCallback)
                        {
                            if (transactionCallback)
                            {
                                _transactionManager.getPlayerData(player).purchasedOtherItem(item, changeInQuantity);
                            }
                            callback.call(transactionCallback);
                            return transactionCallback;
                        }
                    });
                }
            });
        }
        else
        {
            callback.call(false);
        }
    }

    /**
     * Alters the player's quantity of the cosmetic by the specified amount after a short period of time. This should be
     * used if the quantity of the specified cosmetic is expected to change multiple times withing a short period of time.
     *
     * @param player the player
     * @param changeInQuantity the difference in quantity (ex. 1 to add 1 or -1 to remove 1)
     * @param item the name of the cosmetic
     */
    public void updatePlayerOtherItemQuantityDelayed(String player, int changeInQuantity, String item)
    {
        int clientID = _clientManager.getPlayerData(player).getClientID();
        getOtherItemQuantityForPlayer(player, item, true);
        synchronized (_otherItemLock)
        {
            if (_updateOtherItemQuantityLater.containsKey(player))
            {
                boolean updated = false;
                for (OtherItemQuantityToken token : _updateOtherItemQuantityLater.get(player))
                {
                    if (token.getItemName() == item)
                    {
                        updated = true;
                        token.addToken(changeInQuantity);
                    }
                }
                if (!updated)
                {
                    _updateOtherItemQuantityLater.get(player).add(new OtherItemQuantityToken(clientID, changeInQuantity, item));
                }
            }
            else
            {
                _updateOtherItemQuantityLater.put(player, new ArrayList<>());
                _updateOtherItemQuantityLater.get(player).add(new OtherItemQuantityToken(clientID, changeInQuantity, item));
            }
        }
    }

    @EventHandler
    public void updateQuantities(TimerEvent event)
    {
        if (event.getType() != TimerType.MINUTE || (_updateCosmeticQuantityLater.size() == 0 && _updateOtherItemQuantityLater.size() == 0))
        {
            return;
        }
        HashMap<String, List<CosmeticQuantityToken>> cosmeticQuantityMap;
        HashMap<String, List<OtherItemQuantityToken>> otherItemQuantityMap;
        synchronized (_cosmeticLock)
        {
            cosmeticQuantityMap = (HashMap<String, List<CosmeticQuantityToken>>)_updateCosmeticQuantityLater.clone();
            _updateCosmeticQuantityLater.clear();
        }
        synchronized (_otherItemLock)
        {
            otherItemQuantityMap = (HashMap<String, List<OtherItemQuantityToken>>)_updateOtherItemQuantityLater.clone();
            _updateOtherItemQuantityLater.clear();
        }
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                _repository.updatePlayerCosmeticQuantityDelayed(cosmeticQuantityMap);
                _repository.updatePlayerOtherItemQuantityDelayed(otherItemQuantityMap);
            }
        });
        Bukkit.broadcastMessage("Updated Quantities");
    }
}
