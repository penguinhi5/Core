package core.minecraft.gui.page;

import core.minecraft.gui.GUIManager;
import core.minecraft.gui.button.Button;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * This is the base for pages that can be used by an instance of {@link core.minecraft.gui.GUIManager}.
 *
 * @author Preston Brown
 */
public abstract class PageBase {

    protected HashMap<Integer, Button> _buttonMap = new HashMap<>();
    protected Inventory _inventory;
    protected Player _owner;
    protected GUIManager _guiManager;

    /**
     * Creates a new {@link PageBase} instance.
     */
    public PageBase(GUIManager guiManager, Player owner, String inventoryName)
    {
        _guiManager = guiManager;
        _owner = owner;
        _inventory = Bukkit.createInventory(null, 54, inventoryName);
    }

    /**
     * Builds this inventory page.
     */
    public abstract void buildPage();

    /**
     * This is ran when an inventory in this inventory has been clicked.
     *
     * @param slot the slot that was clicked
     * @param item the inventory that was clicked
     * @param clickType the type of click
     */
    public void onClick(int slot, ItemStack item, ClickType clickType)
    {
        if (_buttonMap.containsKey(slot))
        {
            _buttonMap.get(slot).onClick(_owner, clickType);
        }
    }

    /**
     * Adds the button to the specified slot.
     *
     * @param slot the slot in the inventory where the button will be placed
     * @param button the button that is being placed
     */
    public void addButton(int slot, Button button)
    {
        _buttonMap.put(slot, button);
        _inventory.setItem(slot, button.getItem());
    }

    /**
     * Removes the button in the specified slot.
     *
     * @param slot the slot in the inventory where the button will be removed
     */
    public void removeButton(int slot)
    {
        _buttonMap.remove(slot);
        _inventory.setItem(slot, new ItemStack(Material.AIR));
    }

    /**
     * Updates the name of the inventory.
     * If the inventory is currently open the inventory will be reopened with the new name.
     *
     * @param name the new name of the inventory
     */
    public void updateName(String name)
    {
        boolean isInventoryOpen = _guiManager.getCurrentlyOpenedPageForPlayer(_owner).equals(_inventory.getName());
        _inventory = Bukkit.createInventory(null, 54, name);

        // If the inventory is currently open the inventory is reopened so the new name will be displayed
        if (isInventoryOpen)
        {
            _owner.closeInventory();
            _guiManager.openPageForPlayer(this);
        }
    }

    /**
     * Returns this page as an inventory.
     *
     * @return this inventory
     */
    public Inventory getInventory()
    {
        return _inventory;
    }

    /**
     * Returns the name of this page.
     *
     * @return this page's name
     */
    public String getInventoryName()
    {
        return _inventory.getName();
    }

    /**
     * Returns the owner of this page.
     *
     * @return the owner of this page
     */
    public Player getOwner()
    {
        return _owner;
    }

    /**
     * Opens the inventory for the owner.
     *
     * <p>You should only open the inventory through the {@link GUIManager}. If you open the player's inventory
     * directly through this method the {@link org.bukkit.event.inventory.InventoryClickEvent} will not be canceled.
     */
    public void openInventory()
    {
        _owner.openInventory(_inventory);
    }
}
