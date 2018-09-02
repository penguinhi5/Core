package core.minecraft.shop;

import core.minecraft.common.F;
import core.minecraft.gui.GUIManager;
import core.minecraft.gui.page.PageBase;
import core.minecraft.shop.packages.SalesItem;
import core.minecraft.shop.pages.ConfirmationPage;
import core.minecraft.timer.TimerType;
import core.minecraft.timer.event.TimerEvent;
import core.minecraft.transaction.TransactionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * The base for any component that will allow players to purchase items in a gui.
 *
 * @author Preston Brown
 */
public abstract class ShopManager extends GUIManager implements Listener {

    private TransactionManager _transactionManager;
    private HashMap<String, PageBase> _returnPageMap = new HashMap<>();
    private HashMap<Player, Long> _failedPurchases = new HashMap<>();

    /**
     * This creates a new {@link ShopManager} instance.
     *
     * @param plugin the main JavaPlugin instance
     */
    public ShopManager(JavaPlugin plugin, TransactionManager transactionManager)
    {
        super("Sales", plugin);
        _transactionManager = transactionManager;
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    /**
     * This beings the purchase process where the player is trying to purchase the SalesItem.
     *
     * @param salesItem the SalesItem that is being purchased
     * @param player the player purchasing the SalesItem
     */
    public void beginPurchase(SalesItem salesItem, Player player)
    {
        if (_failedPurchases.containsKey(player))
        {
            player.sendMessage(F.errorMessage("Please wait a few more seconds before trying to purchase this item again"));
            return;
        }

        if (_playerPageMap.containsKey(player.getName()))
        {
            _returnPageMap.put(player.getName(), _playerPageMap.get(player.getName()));
        }
        ConfirmationPage confirmationPage = new ConfirmationPage(player, salesItem, this);
        setPlayerPage(player, confirmationPage);
        openPageForPlayer(player, confirmationPage);
    }

    /**
     * Once a purchase is complete this will either return the player to their previous page or close their inventory if
     * they don't have a page to return to.
     *
     * @param player the player that purchased the shop package
     */
    public void completePurchase(Player player)
    {
        if (_returnPageMap.containsKey(player.getName()))
        {
            setPlayerPage(player, _returnPageMap.get(player.getName()));
            openPageForPlayer(player, _returnPageMap.get(player.getName()));
            _returnPageMap.remove(player.getName());
        }
        else
        {
            player.closeInventory();
        }
    }

    /**
     * If a player attempts to purchase an item but the purchase fails.
     *
     * @param player the player that attempted to purchase the item
     */
    public void handleFailedPurchase(Player player)
    {
        _failedPurchases.put(player, System.currentTimeMillis() + 5000L);
        player.sendMessage(F.errorMessage("Purchase Failed. Please wait 5 seconds and try again."));
        completePurchase(player);
    }

    @EventHandler
    public void updateFailedPurchases(TimerEvent event)
    {
        if (event.getType() != TimerType.SECOND)
        {
            return;
        }

        for (Player player : _failedPurchases.keySet())
        {
            if (_failedPurchases.get(player) < System.currentTimeMillis())
            {
                _failedPurchases.remove(player);
            }
        }
    }
}
