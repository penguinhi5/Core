package core.minecraft.shop.packages;

import core.minecraft.gui.button.Button;
import core.minecraft.shop.ShopManager;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

/**
 * This will store a SalesPackageBase as well as the inventory that is being presented.
 *
 * @author Preston Brown
 */
public abstract class SalesItem implements Button {

    protected ShopManager _salesManager;
    protected SalesPackageBase _salesPackage;
    protected ItemStack _displayItem;

    public SalesItem(ShopManager salesManager, ItemStack displayItem, SalesPackageBase salesPackage)
    {
        _salesManager = salesManager;
        _displayItem = displayItem;
        _salesPackage = salesPackage;
    }

    @Override
    public void onClick(Player whoClicked, ClickType clickType)
    {
        _salesManager.beginPurchase(this, whoClicked);
    }

    @Override
    public ItemStack getItem()
    {
        return _displayItem;
    }

    /**
     * Returns the shop package that this inventory represents.
     *
     * @return the shop package
     */
    public SalesPackageBase getSalesPackage()
    {
        return _salesPackage;
    }
}
