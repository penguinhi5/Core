package main;

import core.minecraft.client.ClientManager;
import core.minecraft.command.CommandBase;
import core.minecraft.common.Callback;
import core.minecraft.common.F;
import core.minecraft.common.Rank;
import core.minecraft.common.utils.ItemUtil;
import core.minecraft.item.ItemManager;
import core.minecraft.shop.ShopManager;
import core.minecraft.shop.packages.SalesItem;
import core.minecraft.shop.packages.SalesPackageBase;
import core.minecraft.transaction.TransactionManager;
import core.minecraft.transaction.TransactionResponse;
import core.redis.connection.RedisManager;
import core.redis.data.BungeeProxy;
import core.redis.data.DedicatedServer;
import core.redis.data.MinecraftServer;
import core.redis.data.ServerType;
import core.redis.repository.ServerRepository;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Created by MOTPe on 7/8/2018.
 */
public class TestCommand extends CommandBase<TestComponent>
{

    private String _servername;
    private TransactionManager _transactionManager;
    private ShopManager _salesManager;
    private ClientManager _clientManager;
    private ItemManager _itemManager;

    public TestCommand(TestComponent plugin, ClientManager clientManager, String serverName, TransactionManager transactionManager, ShopManager salesManager, ItemManager itemManager)
    {
        super(plugin, clientManager, "test", new String[] {"t"}, Rank.DEFAULT);
        _servername = serverName;
        _clientManager = clientManager;
        _transactionManager = transactionManager;
        _salesManager = salesManager;
        _itemManager = itemManager;
    }

    public void execute(Player player, String[] args)
    {
        //RedisMessageManager.getInstance().publishCommand(new TestRedisCommand(new String[] { "test-1" }, false, _servername));
        if (args[0].equalsIgnoreCase("testshop"))
        {
            thing2(player);
        }
        else
        {
            thing3(player, args);
        }
    }

    private class TestSalesPackage extends SalesPackageBase
    {

        public TestSalesPackage(ClientManager clientManager)
        {
            super(clientManager);
        }

        @Override
        public void purchaseSalesPackageForPlayer(Player player, Callback<Boolean> callback)
        {
            _transactionManager.rewardCrystals(player.getName(), 50, "test number 1", new Callback<Boolean>() {
                @Override
                public Boolean call(Boolean transactionCallback)
                {
                    System.out.println("rewarded: " + transactionCallback);
                    callback.call(transactionCallback);
                    return transactionCallback;
                }
            });
        }
    }

    private class TestSalesItem extends SalesItem
    {

        public TestSalesItem(ShopManager salesManager, ItemStack itemStack, SalesPackageBase salesPackage)
        {
            super(salesManager, itemStack, salesPackage);
        }
    }

    private void thing3(Player player, String[] args)
    {
        Bukkit.broadcastMessage("\n" + args[0]);
        switch (args[0])
        {
            case "getCosmeticQuantityForPlayer":
                Bukkit.broadcastMessage(_itemManager.getCosmeticQuantityForPlayer(player.getName(), args[1], Boolean.valueOf(args[2])) + "");
                break;

            case "getOtherItemQuantityForPlayer":
                Bukkit.broadcastMessage(_itemManager.getOtherItemQuantityForPlayer(player.getName(), args[1], Boolean.valueOf(args[2])) + "");
                break;

            case "getCosmeticItemID":
                Bukkit.broadcastMessage(_itemManager.getCosmeticItemID(args[1]) + "");
                break;

            case "isCosmeticOwnedByPlayer":
                Bukkit.broadcastMessage(_itemManager.isCosmeticOwnedByPlayer(player.getName(), args[1]) + "");
                break;

            case "isOtherItemOwnedByPlayer":
                Bukkit.broadcastMessage(_itemManager.isOtherItemOwnedByPlayer(player.getName(), args[1]) + "");
                break;

            case "updatePlayerCosmeticQuantity":
                _itemManager.updatePlayerCosmeticQuantity(player.getName(), Integer.parseInt(args[1]), args[2], new Callback<Boolean>() {
                    @Override
                    public Boolean call(Boolean callback) {
                        if (callback)
                        {
                            Bukkit.broadcastMessage("successful");
                        }
                        else
                        {
                            Bukkit.broadcastMessage("failed");
                        }
                        return callback;
                    }
                });
                break;

            case "updatePlayerOtherItemQuantity":
                _itemManager.updatePlayerOtherItemQuantity(player.getName(), Integer.parseInt(args[1]), args[2], new Callback<Boolean>() {
                    @Override
                    public Boolean call(Boolean callback) {
                        if (callback)
                        {
                            Bukkit.broadcastMessage("successful");
                        }
                        else
                        {
                            Bukkit.broadcastMessage("failed");
                        }
                        return callback;
                    }
                });
                break;

            case "updatePlayerCosmeticQuantityDelayed":
                _itemManager.updatePlayerCosmeticQuantityDelayed(player.getName(), Integer.parseInt(args[1]), args[2]);
                break;

            case "updatePlayerOtherItemQuantityDelayed":
                _itemManager.updatePlayerOtherItemQuantityDelayed(player.getName(), Integer.parseInt(args[1]), args[2]);
                break;
        }
    }

    private void thing2(Player player)
    {
        TestSalesPackage testSalesPackage = new TestSalesPackage(_clientManager);
        ItemStack itemStack = new ItemStack(Material.BED, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.WHITE + ChatColor.BOLD.toString() + "Test Cosmetic");
        itemStack.setItemMeta(itemMeta);
        ItemUtil.addItemGlow(itemStack);
        TestSalesItem testSalesItem = new TestSalesItem(_salesManager, itemStack, testSalesPackage);
        _salesManager.beginPurchase(testSalesItem, player);
    }

    private void thing1(Player player)
    {
        player.sendMessage(F.componentMessage("Test Command", "It works!"));
        ServerRepository repo = RedisManager.getInstance().getServerRepository();
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("getAllMinecraftServers");
        for (MinecraftServer server : repo.getAllMinecraftServers())
        {
            Bukkit.broadcastMessage(server.getPublicIP());
            Bukkit.broadcastMessage(server.getServerName());
            Bukkit.broadcastMessage(server.getPort() + "");
            Bukkit.broadcastMessage(server.getMotd());
            repo.removeMinecraftServer(server.getNameID());
        }
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("getAllBungeeProxies");
        for (BungeeProxy bungee : repo.getAllBungeeProxies())
        {
            Bukkit.broadcastMessage(bungee.getPublicIP() + ":" + bungee.getPort());
            Bukkit.broadcastMessage(bungee.getPlayerCount() + "");
            Bukkit.broadcastMessage(bungee.getRam() + "");
            repo.removeBungeeProxy(bungee.getNameID());
        }
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("getAllDedicatedServers");
        for (DedicatedServer server : repo.getAllDedicatedServers())
        {
            Bukkit.broadcastMessage(server.getPublicIP());
            Bukkit.broadcastMessage(server.getPrivateIP());
            Bukkit.broadcastMessage(server.getLocation().toString());
            repo.removeDedicatedServer(server.getNameID());
        }
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("getAllServerTypes");
        for (ServerType type : repo.getAllServerTypes())
        {
            Bukkit.broadcastMessage(type.getName());
            Bukkit.broadcastMessage(type.getMaxPlayerLimit() + "");
            Bukkit.broadcastMessage(type.getMinecraftServers().size() + "");
            repo.removeServerType(type.getNameID());
        }
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("REMOVED EVERYTHING");
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("getAllMinecraftServers");
        for (MinecraftServer server : repo.getAllMinecraftServers())
        {
            Bukkit.broadcastMessage(server.getPublicIP());
            Bukkit.broadcastMessage(server.getServerName());
            Bukkit.broadcastMessage(server.getPort() + "");
            Bukkit.broadcastMessage(server.getMotd());
        }
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("getAllBungeeProxies");
        for (BungeeProxy bungee : repo.getAllBungeeProxies())
        {
            Bukkit.broadcastMessage(bungee.getPublicIP() + ":" + bungee.getPort());
            Bukkit.broadcastMessage(bungee.getPlayerCount() + "");
            Bukkit.broadcastMessage(bungee.getRam() + "");
        }
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("getAllDedicatedServers");
        for (DedicatedServer server : repo.getAllDedicatedServers())
        {
            Bukkit.broadcastMessage(server.getPublicIP());
            Bukkit.broadcastMessage(server.getPrivateIP());
            Bukkit.broadcastMessage(server.getLocation().toString());
        }
        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("getAllServerTypes");
        for (ServerType type : repo.getAllServerTypes())
        {
            Bukkit.broadcastMessage(type.getName());
            Bukkit.broadcastMessage(type.getMaxPlayerLimit() + "");
            Bukkit.broadcastMessage(type.getMinecraftServers().size() + "");
        }
    }

    @Override
    public String getProperUsageMessage()
    {
        return F.properCommandUsageMessage("/main test", "/main test");
    }
}
