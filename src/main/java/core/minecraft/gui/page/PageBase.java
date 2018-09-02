package core.minecraft.gui.page;

import core.minecraft.gui.GUIManager;
import core.minecraft.gui.button.Button;
import org.bukkit.Bukkit;
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
     * This is ran when an item in this inventory has been clicked.
     *
     * @param slot the slot that was clicked
     * @param item the item that was clicked
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
    }

    /**
     * Removes the specified button from the inventory if it exists.
     *
     * @param button the button that is being removed
     */
    public void removeButton(Button button)
    {
        if (_buttonMap.containsValue(button))
        {
            _buttonMap.values().remove(button);
            _inventory.remove(button.getItem());
        }
    }

    /**
     * Updates the name of the inventory.
     *
     * @param name the new name of the inventory
     */
    public void updateName(String name)
    {
        _inventory = Bukkit.createInventory(null, 54, name);
        _owner.closeInventory();
        _guiManager.openPageForPlayer(_owner, this);
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
