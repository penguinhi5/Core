package main;

import core.minecraft.shop.ShopManager;
import core.minecraft.transaction.TransactionManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by MOTPe on 8/8/2018.
 */
public class TestShopComponent extends ShopManager {

    /**
     * This creates a new {@link ShopManager} instance.
     *
     * @param plugin the main JavaPlugin instance
     * @param transactionManager
     */
    public TestShopComponent(JavaPlugin plugin, TransactionManager transactionManager) {
        super(plugin, transactionManager);
    }
}
