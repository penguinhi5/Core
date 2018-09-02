package core.minecraft.gui.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * This is a button that does nothing when it is clicked.
 *
 * @author Preston Brown
 */
public class DummyBtn implements Button {

    ItemStack _item;

    public DummyBtn(ItemStack item)
    {
        _item = item;
    }

    @Override
    public void onClick(Player whoClicked, ClickType clickType) { }

    @Override
    public ItemStack getItem() {
        return _item;
    }
}
