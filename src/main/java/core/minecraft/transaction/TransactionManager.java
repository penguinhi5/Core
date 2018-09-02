package core.minecraft.transaction;

import core.minecraft.ClientComponent;
import core.minecraft.client.ClientManager;
import core.minecraft.common.Callback;
import core.minecraft.item.ItemManager;
import core.minecraft.timer.TimerType;
import core.minecraft.timer.event.TimerEvent;
import core.minecraft.transaction.data.CrystalRewardToken;
import core.minecraft.transaction.data.PlayerTransactions;
import core.minecraft.transaction.mysql.TransactionRepository;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Manages all of the item and currency transactions.
 *
 * @author Preston Brown
 */
public class TransactionManager extends ClientComponent<PlayerTransactions> implements Listener {

    private TransactionRepository _repository;
    private ClientManager _clientManager;
    private LinkedList<CrystalRewardToken> _rewardCrystalQueue = new LinkedList<>();

    /**
     * This creates a new {@link TransactionManager} instance.
     *
     * @param plugin the main JavaPlugin instance
     */
    public TransactionManager(JavaPlugin plugin, ClientManager clientManager)
    {
        super("Transaction", plugin);
        _clientManager = clientManager;
        _repository = new TransactionRepository(this, _clientManager.getRepository());

        Bukkit.getPluginManager().registerEvents(this, getPlugin());
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
                PlayerTransactions playerTransactions = _repository.onPlayerLogin(clientID, player.getName());
                if (playerTransactions == null)
                {
                    event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "There was an issue loading your player data. Please relog.");
                }
                else
                {
                    setPlayerData(player.getName(), playerTransactions);
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
     * Asynchronously processes the purchase of the item for the specified player.
     *
     * @param clientID the player that is receiving the item
     * @param itemID the id of the item the player is purchasing
     * @param cost the price of the item being purchased
     * @param quantity the quantity that is being purchased, if you want to lower the quantity that must be done in
     *                 the {@link ItemManager}
     * @param oneTimePurchase true if this item can only be purchased once, otherwise false
     * @param callback this callback will be called with the resulting TransactionResponse
     */
    public void purchaseCosmeticItem(int clientID, String playerName, int itemID, String itemName, int quantity, int cost, boolean oneTimePurchase, Callback<TransactionResponse> callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                _repository.processCosmeticTransaction(clientID, itemID, cost, quantity, oneTimePurchase, new Callback<TransactionResponse>() {
                    @Override
                    public TransactionResponse call(TransactionResponse transactionCallback)
                    {
                        if (transactionCallback == TransactionResponse.SUCCESSFUL)
                        {
                            getPlayerData(playerName).removeCrystals(cost);
                            getPlayerData(playerName).purchasedCosmeticItem(itemName, quantity);
                        }
                        callback.call(transactionCallback);
                        return transactionCallback;
                    }
                });
            }
        });
    }

    /**
     * Asynchronously processes the transaction created from the specified data. This is used for purchasing items that
     * are not cosmetics.
     *
     * @param clientID the clientID of the player purchasing the item
     * @param name the name of the item being purchased
     * @param quantity the quantity that is being purchased, if you want to lower the quantity that must be done in
     *                 the {@link ItemManager}
     * @param cost the number of crystals being removed from the player's balance
     * @param oneTimePurchase true if this is a one time purchase, otherwise false
     * @param callback this callback will be called with the resulting TransactionResponse
     */
    public void purchaseOtherItem(int clientID, String playerName, String name, int quantity, int cost, boolean oneTimePurchase, Callback<TransactionResponse> callback)
    {
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                _repository.processOtherTransaction(clientID, name, quantity, cost, oneTimePurchase, new Callback<TransactionResponse>() {
                    @Override
                    public TransactionResponse call(TransactionResponse transactionCallback)
                    {
                        if (transactionCallback == TransactionResponse.SUCCESSFUL)
                        {
                            getPlayerData(playerName).removeCrystals(cost);
                            getPlayerData(playerName).purchasedOtherItem(name, quantity);
                        }
                        callback.call(transactionCallback);
                        return transactionCallback;
                    }
                });
            }
        });
    }

    /**
     * Asynchronously executes the transaction by removes the specified number of crystals from the players balance.
     *
     * @param playerName the name of the player making the purchase
     * @param cost the number of crystals being removed from the player's balance
     * @param reason the reason for the transaction
     * @param callback this will call true if the transaction was successful, otherwise false
     */
    public void processCrystalTransaction(String playerName, int cost, String reason, Callback<Boolean> callback)
    {
        int clientID = _clientManager.getPlayerData(playerName).getClientID();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                _repository.processCrystalTransaction(clientID, cost, reason, new Callback<Boolean>() {
                    @Override
                    public Boolean call(Boolean transactionCallback)
                    {
                        if (transactionCallback)
                        {
                            getPlayerData(playerName).removeCrystals(cost);
                        }
                        callback.call(transactionCallback);
                        return transactionCallback;
                    }
                });
            }
        });
    }

    /**
     * Asynchronously rewards the player with the specified number of crystals.
     *
     * @param playerName the player that will be receiving these crystals
     * @param count the number of crystals the player is receiving
     * @param reason the reason the player is receiving these crystals
     * @param callback runs the Callback with the value true if the crystals were successfully rewarded, otherwise false
     */
    public void rewardCrystals(String playerName, int count, String reason, Callback<Boolean> callback)
    {
        int clientID = _clientManager.getRepository().getClientIdFromName(playerName);
        if (clientID == -1)
        {
            callback.call(false);
        }

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                _repository.rewardCrystals(clientID, count, reason, new Callback<Boolean>() {
                    @Override
                    public Boolean call(Boolean transactionCallback) {
                        if (transactionCallback)
                        {
                            getPlayerData(playerName).rewardCrystals(count);
                        }
                        callback.call(transactionCallback);
                        return transactionCallback;
                    }
                });
            }
        });
    }

    /**
     * Asynchronously rewards the player with the specified number of crystals after a short delay. This should be used if a large
     * amount of players are expected to be rewarded crystals within a short period of time.
     *
     * @param playerName the player that will be receiving these crystals
     * @param count the number of crystals the player is receiving
     * @param reason the reason the player is receiving these crystals
     * @param callback runs the Callback with the value true if the crystals were successfully rewarded, otherwise false
     */
    public void rewardCrystalsDelayed(String playerName, int count, String reason, Callback<Boolean> callback)
    {
        int clientID = _clientManager.getPlayerData(playerName).getClientID();
        CrystalRewardToken crystalRewardToken = new CrystalRewardToken(clientID, count, reason, new Callback<Boolean>() {
            @Override
            public Boolean call(Boolean transactionCallback)
            {
                if (transactionCallback)
                {
                    getPlayerData(playerName).rewardCrystals(count);
                }
                callback.call(transactionCallback);
                return transactionCallback;
            }
        });
        _rewardCrystalQueue.addLast(crystalRewardToken);
    }

    @EventHandler
    public void processCrystalRewardQueue(TimerEvent event)
    {
        if (event.getType() != TimerType.MINUTE || _rewardCrystalQueue.size() <= 0)
        {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                Iterator<CrystalRewardToken> tokenIterator = _rewardCrystalQueue.iterator();
                _repository.rewardCrystalsDelayed(tokenIterator);
            }
        });
    }
}
