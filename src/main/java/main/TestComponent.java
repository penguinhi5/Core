package main;

import core.minecraft.Component;
import core.minecraft.client.ClientManager;
import core.minecraft.item.ItemManager;
import core.minecraft.shop.ShopManager;
import core.minecraft.transaction.TransactionManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by MOTPe on 7/8/2018.
 */
public class TestComponent extends Component {

    private ClientManager _clientManager;

    public TestComponent(JavaPlugin plugin, ClientManager clientManager, String serverName, TransactionManager transactionManager, ShopManager salesManager, ItemManager itemManager)
    {
        super("Test Component", plugin);
        _clientManager = clientManager;
        TestMulticommand command = new TestMulticommand(this, clientManager);
        TestCommand subCommand = new TestCommand(this, clientManager, serverName, transactionManager, salesManager, itemManager);
        command.addSubCommand(subCommand);
        command.setDefaultCommand(subCommand);
        addCommand(command);
    }
}
