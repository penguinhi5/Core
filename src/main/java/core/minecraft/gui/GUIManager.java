package core.minecraft.gui;

import core.minecraft.Component;
import core.minecraft.gui.page.PageBase;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * This component is a base that should be used to create any component that relies on a GUI.
 *
 * @author Preston Brown
 */
public abstract class GUIManager extends Component implements Listener {

    protected HashMap<String, PageBase> _playerPageMap = new HashMap<>();

    /**
     * This creates a new Component with the given name under plugin.
     *
     * @param name   the name of the new component
     * @param plugin the main JavaPlugin instance
     */
    public GUIManager(String name, JavaPlugin plugin)
    {
        super(name, plugin);

        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    /**
     * Updates the player's page to the given page and opens it.
     *
     * @param player
     * @param page
     */
    public void setPlayerPage(Player player, PageBase page)
    {
        _playerPageMap.put(player.getName(), page);
    }

    /**
     * Opens the page for the player.
     *
     * @param player the player that is opening the page
     * @param page the page that is being opened
     */
    public void openPageForPlayer(Player player, PageBase page)
    {
        _playerPageMap.put(player.getName(), page);
        _playerPageMap.get(player.getName()).openInventory();
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event)
    {
        HumanEntity player = event.getPlayer();
        _playerPageMap.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event)
    {
        Inventory inventory = event.getInventory();
        String name = event.getInventory().getName();
        for (PageBase page : _playerPageMap.values())
        {
            if (name.equals(page.getInventoryName()))
            {
                page.onClick(event.getSlot(), event.getCurrentItem(), event.getClick());
                event.setCancelled(true);
            }
        }
    }
}
