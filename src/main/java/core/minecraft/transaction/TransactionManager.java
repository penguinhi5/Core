package core.minecraft.transaction;

import core.minecraft.ClientComponent;
import core.minecraft.client.ClientManager;
import core.minecraft.client.data.Client;
import core.minecraft.command.CommandManager;
import core.minecraft.common.Callback;
import core.minecraft.common.CurrencyType;
import core.minecraft.transaction.data.CurrencyRewardToken;
import core.minecraft.transaction.data.PlayerWallet;
import core.minecraft.transaction.mysql.TransactionRepository;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Manages all of the inventory and currency transactions.
 *
 * @author Preston Brown
 */
public class TransactionManager extends ClientComponent<PlayerWallet> implements Listener {

    private Object _lock = new Object();
    private TransactionRepository _repository;
    private ClientManager _clientManager;
    private HashMap<String, CurrencyRewardToken> _rewardCrystalQueue = new HashMap<>();

    /**
     * This creates a new {@link TransactionManager} instance.
     *
     * @param plugin the main JavaPlugin instance
     * @param commandManager the main CommandManager instance
     */
    public TransactionManager(JavaPlugin plugin, ClientManager clientManager, CommandManager commandManager)
    {
        super("Transaction", plugin, commandManager);
        _clientManager = clientManager;
        _repository = new TransactionRepository(this, _clientManager.getRepository());

        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    /**
     * Gets the amount of every currency a player has when they join the server.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        HashMap<CurrencyType, Integer> playerCurrencies =
                _repository.onPlayerLogin(_clientManager.getPlayerData(event.getPlayer()).getClientID());

        setPlayerData(event.getPlayer().getName(), new PlayerWallet(event.getPlayer().getName(), playerCurrencies));
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        removePlayerData(event.getPlayer().getName());
    }

    /**
     * Gets the balance the player currently has of the specified CurrencyType. If there is no player online with
     * that name -1 will be returned.
     *
     * @param currencyType the type of currency
     * @param player the player being checked
     * @return the amount of currency the specified player has, if no player is currently online with that name -1 is returned
     */
    public int getPlayerBalance(CurrencyType currencyType, String player)
    {
        if (getPlayerData(player) == null)
        {
            return -1;
        }

        return getPlayerData(player).getCurrency(currencyType);
    }

    /**
     * Asynchronously adds the specified amount of the currency to the player's balance. If the player is not
     * currently online the item quantity will not be updated.
     *
     * @param playerName the name of the player making the purchase
     * @param count the change in the player's currency count
     * @param reason the reason for the transaction
     */
    public void processCurrencyTransaction(String playerName, CurrencyType currencyType, int count, String reason)
    {
        Client client = _clientManager.getPlayerData(playerName);

        // Ensures the player is currently online
        if (client == null)
        {
            return;
        }

        int clientID = _clientManager.getPlayerData(playerName).getClientID();

        // Processes the transaction
        processOfflinePlayerCurrencyTransaction(new Callback<Boolean>() {
            @Override
            public Boolean call(Boolean transactionCallback)
            {
                if (transactionCallback)
                {
                    getPlayerData(playerName).addCurrency(currencyType, count);
                }
                return transactionCallback;
            }
        }, clientID, currencyType, count, reason);
    }

    /**
     * Asynchronously adds the specified amount of the currency to the player's balance. If the player is not
     * currently online the item quantity will not be updated.
     *
     * <p>
     * The callback will call true if the update was successful, otherwise false will be called if it failed.
     * </p>
     *
     * @param callback this will call true if the transaction was successful, otherwise false
     * @param playerName the name of the player making the purchase
     * @param currencyType the {@Link CurrencyType} being added to the player's balance
     * @param count the change in the player's currency count
     * @param reason the reason for the transaction
     */
    public void processCurrencyTransaction(Callback<Boolean> callback, String playerName, CurrencyType currencyType, int count, String reason)
    {
        Client client = _clientManager.getPlayerData(playerName);

        // Ensures the player is currently online
        if (client == null)
        {
            callback.call(false);
            return;
        }

        int clientID = _clientManager.getPlayerData(playerName).getClientID();

        // Processes the transaction
        processOfflinePlayerCurrencyTransaction(new Callback<Boolean>() {
            @Override
            public Boolean call(Boolean transactionCallback)
            {
                if (transactionCallback)
                {
                    getPlayerData(playerName).addCurrency(currencyType, count);
                }
                callback.call(transactionCallback);
                return transactionCallback;
            }
        }, clientID, currencyType, count, reason);
    }

    /**
     * Asynchronously adds the specified amount of the currency to the offline player's balance.
     *
     * <p>
     *     This method should only be used when the player is offline because the change will not be shown live
     * on the server. You should also ensure that the player is not currently online on the network if you are
     * removing from the player's balance in order to prevent the player from spending money they don't have.
     *</p>
     *
     * The callback will call true if the update was successful, otherwise false will be called if it failed.
     *
     * @param callback this will call true if the transaction was successful, otherwise false
     * @param clientID the clientID of the player making the purchase
     * @param currencyType the {@Link CurrencyType} being added to the player's balance
     * @param count the amount of currency being added to the player's balance
     * @param reason the reason for the transaction
     */
    public void processOfflinePlayerCurrencyTransaction(Callback<Boolean> callback, int clientID, CurrencyType currencyType, int count, String reason)
    {
        synchronized (_lock)
        {
            Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
                @Override
                public void run()
                {
                    _repository.processCurrencyTransaction(clientID, currencyType, count, reason, new Callback<Boolean>() {
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
}
