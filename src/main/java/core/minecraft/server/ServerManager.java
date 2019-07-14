package core.minecraft.server;

import core.minecraft.Component;
import core.minecraft.command.CommandManager;
import core.minecraft.timer.TimerType;
import core.minecraft.timer.event.TimerEvent;
import core.redis.connection.RedisManager;
import core.redis.data.MinecraftServer;
import core.redis.data.ServerType;
import core.redis.repository.ServerRepository;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Manages all of the major server settings.
 *
 * @author Preston Brown
 */
public class ServerManager extends Component implements Listener {

    private ServerRepository _serverRepository;
    private ServerType _serverType;
    private String _serverTypeName;
    private String _serverName;
    private String _publicIP;

    /**
     * Creates a new ServerManager instance.
     *
     * @param plugin The main JavaPlugin instance
     * @param commandManager the main CommandManager instance
     */
    public ServerManager(JavaPlugin plugin, CommandManager commandManager)
    {
        super("Server", plugin, commandManager);

        // Gets the redis server repository that stores the live data of every server
        _serverRepository = RedisManager.getInstance().getServerRepository();

        // Ensures a config exists
        generateConfig();

        // Gets the server name and type
        _serverName = getPlugin().getConfig().getString("servermanager.name");
        _serverTypeName = getPlugin().getConfig().getString("servermanager.type");

        // Gets the public IP
        try
        {
            URL stream = new URL("http://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(stream.openStream()));

            _publicIP = in.readLine();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Gets the server type
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {

            @Override
            public void run()
            {
                _serverType = _serverRepository.getServerType(_serverTypeName);
            }
        });

        // Begin to upload live server data to Redis
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    /**
     * Generates a default config if one does not already exist.
     *
     * If a config file doesn't already exist the server is assumed to be a test server.
     */
    private void generateConfig()
    {
        getPlugin().getConfig().addDefault("servermanager.name", "Test-1");
        getPlugin().getConfig().set("servermanager.name", getPlugin().getConfig().get("servermanager.name"));

        getPlugin().getConfig().addDefault("servermanager.type", "Test");
        getPlugin().getConfig().set("servermanager.type", getPlugin().getConfig().get("servermanager.type"));

        getPlugin().saveConfig();
    }

    /**
     * Updates the {@link MinecraftServer} data stored on Redis pertaining to
     * this server instance on a regular interval.
     */
    @EventHandler
    public void updateEvent(TimerEvent event)
    {
        if (event.getType() != TimerType.FIVE_SECONDS)
        {
            return;
        }

        MinecraftServer server = generateServer();
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {

            @Override
            public void run()
            {
                _serverRepository.addMinecraftServer(server);
            }
        });
    }

    /**
     * Generates a snapshot of this MinecraftServer instance.
     *
     * @return the snapshot of this MinecraftServer instance
     */
    public MinecraftServer generateServer()
    {
        String publicIP = Bukkit.getIp() == null ? _publicIP : Bukkit.getIp();
        int port = Bukkit.getPort();
        int playerCount = Bukkit.getOnlinePlayers().size();
        int playerLimit = _serverType == null ? Bukkit.getMaxPlayers() : _serverType.getMaxPlayerLimit();
        String motd = Bukkit.getMotd();
        long maxRam = Runtime.getRuntime().maxMemory();
        long freeRam = Runtime.getRuntime().freeMemory();
        String version = Bukkit.getVersion();
        MinecraftServer server = new MinecraftServer(publicIP, port, _serverName, _serverTypeName, playerCount, playerLimit, motd, maxRam, freeRam, version);
        return server;
    }

    /**
     * @return the public name of this MinecraftServer
     */
    public String getServerName()
    {
        return _serverName;
    }
}
