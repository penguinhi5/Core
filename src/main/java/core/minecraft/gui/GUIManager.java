package core.minecraft.gui;

import core.minecraft.Component;
import core.minecraft.command.CommandManager;
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
     * @param commandManager the main CommandManager instance
     */
    public GUIManager(String name, JavaPlugin plugin, CommandManager commandManager)
    {
        super(name, plugin, commandManager);

        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    /**
     * Opens the page for the player.
     *
     * @param page the page that is being opened
     */
    public void openPageForPlayer(PageBase page)
    {
        _playerPageMap.put(page.getOwner().getName(), page);
        _playerPageMap.get(page.getOwner().getName()).openInventory();
    }

    /**
     * Returns the name of the page that is currently opened by the player. If the player does not currently have
     * a page opened an empty string is returned.
     *
     * @param player the player object that we are checking if they have a page currently opened
     * @return the name of the page the player currently has opened, if the player doesn't have a page open an
     * empty string is returned.
     */
    public String getCurrentlyOpenedPageForPlayer(Player player)
    {
        // Checks if the player has a GUI opened
        if (_playerPageMap.get(player.getName()) != null)
        {
            return _playerPageMap.get(player.getName()).getInventoryName();
        }

        return "";
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
