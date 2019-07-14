package core.minecraft.client;

import core.minecraft.ClientComponent;
import core.minecraft.client.command.SetRank;
import core.minecraft.client.data.Client;
import core.minecraft.client.redis.RedisClient;
import core.minecraft.client.redis.ClientRedisRepository;
import core.minecraft.client.repository.ClientSQLRepository;
import core.minecraft.command.CommandManager;
import core.minecraft.common.Rank;
import core.minecraft.database.mysql.Row;
import core.minecraft.server.ServerManager;
import core.minecraft.timer.TimerType;
import core.minecraft.timer.event.TimerEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.util.HashMap;

/**
 * Manages all basic client information.
 *
 * @author Preston Brown
 */
public class ClientManager extends ClientComponent<Client> implements Listener {

    private ClientSQLRepository _sqlRepository;
    private ClientRedisRepository _redisRepository;
    private ServerManager _serverConfiguration;
    private HashMap<String, ClientLoginProcessor> _loginProcesses = new HashMap<>();

    /**
     * This creates a new instance of ClientManager using the specified {@link JavaPlugin}.
     *
     * @param plugin the main {@link JavaPlugin} instance
     * @param serverConfiguration the main ServerManager instance
     * @param commandManager the main CommandManager instance
     */
    public ClientManager(JavaPlugin plugin, ServerManager serverConfiguration, CommandManager commandManager)
    {
        super("Client Manager", plugin, commandManager);
        _sqlRepository = new ClientSQLRepository();
        _redisRepository = new ClientRedisRepository();
        _serverConfiguration = serverConfiguration;
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
        addCommands();
    }

    /**
     * This prepares the client data in all of the components when a new player logs into the connection.
     *
     * @see PlayerLoginEvent
     */
    @EventHandler
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event)
    {
        Row playerLoginData = _sqlRepository.playerLogin(event.getName(), event.getUniqueId().toString(), _loginProcesses);
        if (playerLoginData == null)
        {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "There was an issue loading your player data. Please relog.");
            return;
        }

        setPlayerData(event.getName(), new Client(event.getName()));
        Client client = getPlayerData(event.getName());
        client.setClientID((int)(playerLoginData.getColumn("id").getData()));
        client.setUUID(event.getUniqueId().toString());
        client.setTotalPlayTime((long)(playerLoginData.getColumn("totalPlayTime").getData()));

        Timestamp timestamp = (Timestamp)(playerLoginData.getColumn("lastLogin").getData());
        client.setLastLogin(timestamp.getTime());

        String rank = (String)(playerLoginData.getColumn("rank").getData());
        String purchasedRank = (String)(playerLoginData.getColumn("purchasedRank").getData());
        if (purchasedRank == null)
        {
            client.setPurchasedRank(Rank.DEFAULT);
        }
        else
        {
            client.setPurchasedRank(Rank.valueOf(purchasedRank));
        }
        if (rank == null)
        {
            client.setRank(client.getPurchasedRank());
        }
        else
        {
            client.setRank(Rank.valueOf(rank));
        }
    }

    /**
     * This ensures that the client data was properly loaded
     *
     * @param event the {@link PlayerLoginEvent}
     */
    @EventHandler (priority = EventPriority.LOWEST)
    public void onPlayerLogin(PlayerLoginEvent event)
    {
        if (getPlayerData(event.getPlayer().getName()) == null)
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "There was an issue loading your player data. Please relog.");
            return;
        }

        if (getPlayerData(event.getPlayer().getName()).getRank() == null || getPlayerData(event.getPlayer().getName()).getPurchasedRank() == null)
        {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "There was an issue loading your player data. Please relog.");
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {

            @Override
            public void run()
            {
                _redisRepository.playerJoin(new RedisClient(event.getPlayer().getName(), event.getPlayer().getUniqueId(), _serverConfiguration.getServerName()));
            }
        });
    }

    /**
     * Updates the player leave message and clears any data the player left behind.
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        removePlayerData(event.getPlayer().getName());

        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                _redisRepository.playerLeave(event.getPlayer().getName());
            }
        });
    }

    /**
     * Adds the loginProcessor to the list of {@link ClientLoginProcessor}s that will be ran every
     * time a player logs into the connection.
     *
     * @param loginProcessor the {@link ClientLoginProcessor} that will be ran when a player logs into the connection
     */
    public void addClientLoginProcessor(ClientLoginProcessor loginProcessor)
    {
        _loginProcesses.put(loginProcessor.getName(), loginProcessor);
    }

    /**
     * Gets the client repository used to store basic client data.
     *
     * @return returns the {@link ClientSQLRepository} reference
     */
    public ClientSQLRepository getRepository()
    {
        return _sqlRepository;
    }

    @EventHandler
    public void cleanPlayerData(TimerEvent event)
    {
        if (event.getType() != TimerType.FIVE_MINUTES)
        {
            return;
        }

        for (String name : _playerData.keySet())
        {
            if (Bukkit.getPlayer(name) == null)
            {
                removePlayerData(name);
            }
        }
    }

    /**
     * Initializes all of the commands
     */
    private void addCommands()
    {
        addCommand(new SetRank(this));
    }
}
