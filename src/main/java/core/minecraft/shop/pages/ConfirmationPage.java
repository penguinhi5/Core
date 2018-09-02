package core.minecraft.shop.pages;

import core.minecraft.common.Callback;
import core.minecraft.gui.button.Button;
import core.minecraft.gui.button.DummyBtn;
import core.minecraft.gui.page.PageBase;
import core.minecraft.shop.ShopManager;
import core.minecraft.shop.packages.SalesItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Random;

/**
 * This page is used to execute a purchase for the page owner.
 *
 * @author Preston Brown
 */
public class ConfirmationPage extends PageBase implements Runnable{

    private boolean _startedPurchase;
    private SalesItem _salesItem;
    private ShopManager _salesManager;
    private int _taskID;
    private int _row;
    private Object _lock;

    /**
     * Creates a new confirmation page where a player should confirm their purchase.
     *
     * @param owner
     */
    public ConfirmationPage(Player owner, SalesItem salesItem, ShopManager salesManager)
    {
        super(salesManager, owner, "Please Confirm Your Purchase");
        _salesItem = salesItem;
        _salesManager = salesManager;
        _startedPurchase = false;
        buildPage();
    }

    @Override
    public void buildPage()
    {
        ItemStack backgroundItem = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)8);
        ItemMeta backgroundItemMeta = backgroundItem.getItemMeta();
        backgroundItemMeta.setDisplayName(" ");
        backgroundItem.setItemMeta(backgroundItemMeta);
        for (int i = 0; i <= 44; i++)
        {
            addButton(i, new DummyBtn(backgroundItem));
        }

        for (int i = 45; i <= 53; i++)
        {
            addButton(i, new CancelButton());
        }

        Random random = new Random();
        addButton(18 + random.nextInt(9), new ConfirmButton());
        addButton(27 + random.nextInt(9), new ConfirmButton());
        addButton(36 + random.nextInt(9), new ConfirmButton());

        addButton(13, new DummyBtn(_salesItem.getItem()));
    }

    /**
     * This will execute the purchase once the player has confirmed it.
     */
    private void handlePurchase()
    {
        if (_startedPurchase) return;
        _startedPurchase = true;

        clearPage();
        updateName("Processing...");
        _row = 0;
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)10);
        for (int i = 0; i <= 53; i++)
        {
            addButton(i, new DummyBtn(background));
        }
        _taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(_salesManager.getPlugin(), this, 0, 2);

        _salesItem.getSalesPackage().purchaseSalesPackageForPlayer(_owner, new Callback<Boolean>() {
            @Override
            public Boolean call(Boolean callback)
            {
                Bukkit.getScheduler().cancelTask(_taskID);
                clearPage();

                if (callback)
                {
                    updateName("Purchase Successful");
                    _owner.playSound(_owner.getLocation(), Sound.NOTE_PLING, 1F, 2F);
                    for (int i = 0; i <= 53; i++)
                    {
                        addButton(i, new SuccessButton());
                    }
                }
                else
                {
                    updateName("Purchase Failed");
                    _owner.playSound(_owner.getLocation(), Sound.FIREWORK_BLAST, 1F, 1F);
                    for (int i = 0; i <= 53; i++)
                    {
                        addButton(i, new FailedButton());
                    }
                }

                return callback;
            }
        });
    }

    /**
     * Animates the purchase processing page.
     */
    @Override
    public void run()
    {
        ItemStack background = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)10);
        ItemMeta itemMeta = background.getItemMeta();
        itemMeta.setDisplayName(" ");
        background.setItemMeta(itemMeta);
        ItemStack line = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)0);
        itemMeta = line.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Confirm Purchase");
        line.setItemMeta(itemMeta);
        for (int i = 9 * _row; i < 9 * _row + 9; i++)
        {
            addButton(i, new DummyBtn(background));
        }
        if (_row >= 5)
        {
            _row = 0;
        }
        else
        {
            _row++;
        }
        for (int i = 9 * _row; i < 9 * _row + 9; i++)
        {
            addButton(i, new DummyBtn(line));
        }
    }

    /**
     * Clears the page.
     */
    private void clearPage()
    {
        _buttonMap.clear();
        _inventory.clear();
    }



    private class ConfirmButton implements Button
    {
        private ItemStack _item;

        public ConfirmButton()
        {
            ItemStack itemStack = new ItemStack(Material.CAKE, 1);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Confirm Purchase");
            itemStack.setItemMeta(itemMeta);
            _item = itemStack;
        }

        @Override
        public void onClick(Player whoClicked, ClickType clickType)
        {
            handlePurchase();
        }

        @Override
        public ItemStack getItem()
        {
            return _item;
        }
    }

    private class CancelButton implements Button
    {
        private ItemStack _item;

        public CancelButton()
        {
            ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)14);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Cancel Purchase");
            itemStack.setItemMeta(itemMeta);
            _item = itemStack;
        }

        @Override
        public void onClick(Player whoClicked, ClickType clickType)
        {
            _salesManager.completePurchase(_owner);
        }

        @Override
        public ItemStack getItem()
        {
            return _item;
        }
    }

    private class SuccessButton implements Button
    {
        private ItemStack _item;

        public SuccessButton()
        {
            ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)5);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Success");
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_GRAY + ChatColor.ITALIC.toString() + "Click on me to return!");
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            _item = item;
        }

        @Override
        public void onClick(Player whoClicked, ClickType clickType)
        {
            _salesManager.completePurchase(_owner);
        }

        @Override
        public ItemStack getItem()
        {
            return _item;
        }
    }

    private class FailedButton implements Button
    {
        private ItemStack _item;

        public FailedButton()
        {
            ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)14);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + ChatColor.BOLD.toString() + "Failed");
            ArrayList<String> lore = new ArrayList<>();
            lore.add(ChatColor.DARK_GRAY + ChatColor.ITALIC.toString() + "Click on me to return!");
            itemMeta.setLore(lore);
            item.setItemMeta(itemMeta);
            _item = item;
        }

        @Override
        public void onClick(Player whoClicked, ClickType clickType)
        {
            _salesManager.handleFailedPurchase(_owner);
        }

        @Override
        public ItemStack getItem()
        {
            return _item;
        }
    }
}
