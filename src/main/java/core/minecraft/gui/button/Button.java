package core.minecraft.gui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a gui button.
 *
 * @author Preston Brown
 */
public interface Button {

    /**
     * This will be called when a player clicks on a button in an inventory.
     *
     * @param whoClicked the player that clicked the button
     * @param clickType the type of click
     */
    public void onClick(Player whoClicked, ClickType clickType);

    /**
     * Gets the item that will be shown to the player in the gui that represents this button.
     *
     * @return the item that represents this button
     */
    public ItemStack getItem();
}
