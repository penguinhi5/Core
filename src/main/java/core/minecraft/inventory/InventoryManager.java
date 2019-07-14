package core.minecraft.inventory;

import core.minecraft.ClientComponent;
import core.minecraft.client.ClientManager;
import core.minecraft.client.data.Client;
import core.minecraft.command.CommandManager;
import core.minecraft.common.Callback;
import core.minecraft.inventory.data.Category;
import core.minecraft.inventory.data.Item;
import core.minecraft.inventory.data.PlayerInventory;
import core.minecraft.inventory.mysql.InventoryRepository;
import core.minecraft.inventory.data.ItemQuantityToken;
import core.minecraft.timer.TimerType;
import core.minecraft.timer.event.TimerEvent;
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
public class InventoryManager extends ClientComponent<PlayerInventory> implements Listener {

    private Object _lock = new Object();
    private InventoryRepository _repository;
    private ClientManager _clientManager;
    private HashMap<String, Category> _categories = new HashMap<>();
    private HashMap<String, Item> _items = new HashMap<>();
    private HashMap<String, List<ItemQuantityToken>> _updateItemQuantity = new HashMap<>();

    /**
     * Creates a new instance of {@link InventoryManager}.
     *
     * @param plugin the main JavaPlugin instance
     * @param commandManager the main CommandManager instance
     */
    public InventoryManager(JavaPlugin plugin, ClientManager clientManager, CommandManager commandManager)
    {
        super("Item Manager", plugin, commandManager);
        _clientManager = clientManager;
        _repository = new InventoryRepository(this);
        updateCategories();
        updateItems();

        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    /**
     * Updates the list of existing categories.
     */
    private void updateCategories()
    {
        synchronized (_lock)
        {
            _categories.clear();
            for (Category category : _repository.retrieveCategories())
            {
                _categories.put(category.getCategoryName(), category);
            }
        }
    }

    /**
     * Updates the list of existing items.
     */
    private void updateItems()
    {
        synchronized (_lock)
        {
            _items.clear();
            for (Item item : _repository.retrieveItems())
            {
                _items.put(item.getItemName(), item);
            }
        }
    }

    /**
     * Returns whether or not the item is a valid item.
     *
     * @param item the item being searched for
     * @return true if the item is a valid item, otherwise false
     */
    public boolean isValidItem(String item)
    {
        return _items.containsKey(item);
    }

    /**
     * Returns whether or not the category is a valid category.
     *
     * @param category the category being searched for
     * @return true if the category is a valid category, otherwise false
     */
    public boolean isValidCategory(String category)
    {
        return _categories.containsKey(category);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        Player player = event.getPlayer();
        int clientID = _clientManager.getPlayerData(player).getClientID();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                PlayerInventory playerInventory = _repository.onPlayerLogin(clientID, player.getName());
                if (playerInventory == null)
                {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "There was an issue loading your player data. Please relog.");
                }
                else
                {
                    setPlayerData(player.getName(), playerInventory);
                }
            }
        });
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        removePlayerData(event.getPlayer().getName());
    }

    /**
     * Retrieves the item with the given name. If no item with the name exists, null is returned.
     *
     * @param name the name of the item
     * @return the item with the given name, null if the item wasn't found
     */
    public Item getItem(String name)
    {
        return _items.get(name);
    }

    /**
     * Retrieves the category with the given name. If no category with the name exists, null is returned.
     *
     * @param category the name of the category
     * @return the category with the given name, null if the category wasn't found
     */
    public Category getCategory(String category)
    {
        return _categories.get(category);
    }

    /**
     * Returns whether or not the player owns the specified item.
     *
     * @param player the player we are searching up
     * @param item the item we want to check if the player owns
     * @return true of the player owns the item, otherwise false
     */
    public boolean isItemOwnedByPlayer(String player, String item)
    {
        return getPlayerData(player).getPurchasedItems().containsKey(item);
    }

    /**
     * Gets the quantity of the specified item the player owns.
     *
     * @param player the player
     * @param item the item you want to search
     * @return the quantity that the player owns of the inventory
     */
    public int getItemQuantityForPlayer(String player, String item)
    {
        if (getPlayerData(player).getPurchasedItems().containsKey(item))
        {
            return getPlayerData(player).getPurchasedItems().get(item);
        }
        return 0;
    }

    /**
     * Alters the player's quantity of the item by the specified amount. If the player is not currently online
     * the item quantity will not be updated.
     *
     * If the change in quantity results in the player having a negative quantity the player's item quantity
     * will be set to 0.
     *
     * The callback will return true if the update was successful, otherwise false will be returned if it failed.
     *
     * @param player the player
     * @param changeInQuantity the change in the quantity (ex. 1 to add 1 or -1 to remove 1)
     * @param item the name of the item
     */
    public void updatePlayerItemQuantity(Callback<Boolean> callback, String player, int changeInQuantity, String item, String category)
    {
        // Gets the player's clientID if the player is currently online
        // If the player isn't currently online we return
        Client client = _clientManager.getPlayerData(player);
        if (client == null)
        {
            callback.call(false);
            return;
        }
        int clientID = client.getClientID();

        updateOfflinePlayerItemQuantity(new Callback<Boolean>() {
            @Override
            public Boolean call(Boolean transactionResponse)
            {
                if (transactionResponse && getPlayerData(player) != null)
                {
                    getPlayerData(player).addItem(item, Math.max(changeInQuantity, 0));
                }
                callback.call(transactionResponse);
                return transactionResponse;
            }
        }, clientID, item, category, changeInQuantity);
    }

    /**
     * Alters the player's quantity of the item by the specified amount. If the player is not currently online
     * the item quantity will not be updated.
     *
     * If the change in quantity results in the player having a negative quantity the player's item quantity
     * will be set to 0.
     *
     * @param player the player
     * @param quantity the change in quantity (ex. 1 to add 1 or -1 to remove 1)
     * @param item the name of the item
     * @param category the item's category
     */
    public void updatePlayerItemQuantity(String player, int quantity, String item, String category)
    {
        // Gets the player's clientID and PlayerInventory if the player is currently online
        // If the player isn't currently online we return
        Client client = _clientManager.getPlayerData(player);
        PlayerInventory inventory = getPlayerData(player);
        if (client == null || inventory == null)
        {
            return;
        }
        int clientID = client.getClientID();

        if (_updateItemQuantity.containsKey(player))
        {
            boolean updated = false;
            for (ItemQuantityToken token : _updateItemQuantity.get(player))
            {
                if (token.item == item)
                {
                    updated = true;
                    token.addToken(quantity);
                }
            }
            if (!updated)
            {
                _updateItemQuantity.get(player).add(new ItemQuantityToken(clientID, quantity, item, category));
            }
        }
        else
        {
            _updateItemQuantity.put(player, new ArrayList<ItemQuantityToken>());
            _updateItemQuantity.get(player).add(new ItemQuantityToken(clientID, quantity, item, category));
        }

        // Locally updates the change in quantity
        if (getPlayerData(player) != null)
        {
            getPlayerData(player).addItem(item, quantity);
        }
    }

    /**
     * Updates the player's quantity of the item by the specified amount for a player that is offline.
     *
     * If the change in quantity results in the player having a negative quantity the player's item quantity
     * will be set to 0.
     *
     * @param callback the callback that will call true if the update succeeded, or false if the update failed
     * @param clientID the clientID of the offline player
     * @param item the item whose quantity is being updated
     * @param category the category of inventory
     * @param changeInQuantity the difference in quantity
     */
    public void updateOfflinePlayerItemQuantity(Callback<Boolean> callback, int clientID, String item, String category, int changeInQuantity)
    {
        // Ensures the category exists in the database
        synchronized (_lock)
        {
            if (!_categories.containsKey(category))
            {
                _repository.addCategory(category);
            }
        }
        updateCategories();

        // Ensures the inventory exists in the database
        synchronized (_lock)
        {
            if (!_items.containsKey(item))
            {
                _repository.addItem(item, _categories.get(category).getCategoryID());
            }
        }
        updateItems();

        synchronized (_lock)
        {
            Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
                @Override
                public void run()
                {
                    _repository.updatePlayerItemQuantity(clientID, _items.get(item).getItemID(), changeInQuantity, new Callback<Boolean>() {
                        @Override
                        public Boolean call(Boolean transactionCallback)
                        {
                            callback.call(transactionCallback);
                            return transactionCallback;
                        }
                    });
                }
            });
        }
    }

    /**
     * Updates all the ItemQuantityTokens.
     */
    @EventHandler
    public void updateQuantities(TimerEvent event)
    {
        if (event.getType() != TimerType.SECOND || _updateItemQuantity.size() == 0)
        {
            return;
        }
        HashMap<String, List<ItemQuantityToken>> itemQuantityMap;
        itemQuantityMap = (HashMap<String, List<ItemQuantityToken>>) _updateItemQuantity.clone();
        _updateItemQuantity.clear();

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                for (String player : itemQuantityMap.keySet())
                {
                    for (ItemQuantityToken token : itemQuantityMap.get(player))
                    {
                        updateOfflinePlayerItemQuantity(new Callback<Boolean>() {

                            @Override
                            public Boolean call(Boolean transactionResponse)
                            {
                                // Removes the inventory from the player's inventory if the transaction failed
                                // and the player is still online
                                if (!transactionResponse && getPlayerData(player) != null)
                                {
                                    getPlayerData(player).addItem(token.item, -token.quantity);
                                    System.out.println("FAILED to add item - " + token.item + " for player - " + player);
                                }
                                return transactionResponse;
                            }

                        }, token.clientID, token.item, token.category, token.quantity);
                    }
                }
            }
        });

    }
}
