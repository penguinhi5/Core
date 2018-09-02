package main;

import core.minecraft.chat.ChatManager;
import core.minecraft.client.ClientManager;
import core.minecraft.command.CommandManager;
import core.minecraft.cooldown.Cooldown;
import core.minecraft.item.ItemManager;
import core.minecraft.server.ServerManager;
import core.minecraft.timer.Timer;
import core.minecraft.transaction.TransactionManager;
import core.redis.Location;
import core.redis.connection.RedisManager;
import core.redis.data.BungeeProxy;
import core.redis.data.DedicatedServer;
import core.redis.data.MinecraftServer;
import core.redis.data.ServerType;
import core.redis.message.RedisMessageManager;
import core.redis.repository.ServerRepository;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

/**
 * Created by MOTPe on 7/7/2018.
 */
public class Main extends JavaPlugin {

    public void onEnable()
    {
        CommandManager.initialize(this);
        Cooldown.initializeCooldown(this);
        String name = generateServers();
        ServerManager serverConfiguration = new ServerManager(this);
        ClientManager clientManager = new ClientManager(this, serverConfiguration);
        TransactionManager transactionManager = new TransactionManager(this, clientManager);
        ItemManager itemManager = new ItemManager(this, clientManager, transactionManager);
        TestShopComponent testShopComponent = new TestShopComponent(this, transactionManager);
        new Timer(this);
        ChatManager chatManager = new ChatManager(this, clientManager);
        new TestComponent(this, clientManager, name, transactionManager, testShopComponent, itemManager);
    }

    private String generateServers()
    {
        String ip = "";
        try
        {
            URL whatismyip = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));

            ip = in.readLine();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        List<String> list = readTempConfig();
        String name = list.get(0);

        RedisMessageManager.getInstance().initializeServer(name);

        HashMap<String, Object> data = new HashMap<>();
        data.put("name", "test");
        data.put("maxPlayerLimit", 16);
        data.put("minPlayerLimit", 0);
        data.put("ram", 1024);
        ServerType type = new ServerType(data, new ArrayList<MinecraftServer>());

        MinecraftServer mcServer = new MinecraftServer(ip, 2020, name, "test", 0, 16, "test server", 1024, 1024, "1.8");
        type.addMinecraftServer(mcServer);

        BungeeProxy bungee = new BungeeProxy(ip, 25565, 1024, 1000);

        DedicatedServer ded = new DedicatedServer(ip, "localhost", 10000, 10000, 10000, 10000, Location.US);

        ServerRepository repo = RedisManager.getInstance().getServerRepository();
        repo.addServerType(type);
        repo.addBungeeProxy(bungee);
        repo.addDedicatedServer(ded);
        repo.addMinecraftServer(mcServer);



        RedisMessageManager.getInstance().addCommandType(TestRedisCommand.class, new TestRedisCommandHandler<TestRedisCommand>());
        return name;
    }

    private List<String> readTempConfig()
    {
        File file = new File("tempdata.dat");
        Scanner scanner = null;
        ArrayList<String> lines = new ArrayList<>();

        try
        {
            scanner = new Scanner(file);
            while (scanner.hasNextLine())
            {
                lines.add(scanner.nextLine());
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("temp cannot be found at " + file.getAbsolutePath());
            e.printStackTrace();
        }
        finally
        {
            if (scanner != null)
            {
                scanner.close();
            }
        }
        return lines;
    }

}
