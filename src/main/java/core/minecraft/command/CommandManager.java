package core.minecraft.command;

import core.minecraft.common.F;
import core.minecraft.common.Rank;
import core.minecraft.common.utils.PlayerUtil;
import javafx.scene.layout.Priority;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * This class manages all of the player commands.
 *
 * @author Preston Brown
 */
public class CommandManager implements Listener {

    private static CommandManager _instance;
    private static JavaPlugin _plugin;
    private HashMap<String, CommandInstance> _commands;

    /**
     * Initializes the CommandManager
     */
    public static void initialize(JavaPlugin plugin)
    {
        _instance = new CommandManager(plugin);
        _plugin.getServer().getPluginManager().registerEvents(_instance, _plugin);
    }

    private CommandManager(JavaPlugin plugin)
    {
        _plugin = plugin;
        _commands = new HashMap<>();
    }

    /**
     * @return an instance of CommandManager
     */
    public static CommandManager getInstance()
    {
        return _instance;
    }

    @EventHandler
    public void playerCommandProcess(PlayerCommandPreprocessEvent event)
    {
        String message = event.getMessage().substring(1);
        String label = message.split(" ")[0];
        String[] args = new String[] {};
        if (message.contains(" "))
        {
            args = message.substring(label.length() + 1).split(" ");
        }

        for (String commandLabel : _commands.keySet())
        {
            if (commandLabel.equalsIgnoreCase(label))
            {
                event.setCancelled(true);
                CommandInstance command = _commands.get(label);

                if (!command.hasPermission(event.getPlayer()))
                {
                    event.getPlayer().sendMessage(F.insufficientRankMessage(command.getRequiredRank()));
                    return;
                }

                _commands.get(label).execute(event.getPlayer(), args);
                return;
            }
        }
    }

    /**
     * Initializes the CommandInstance
     *
     * @param command The CommandInstance that is being added
     */
    public void addCommand(CommandInstance command)
    {
        _commands.put(command.getName(), command);
        for (String commandName : command.getAliases())
        {
            _commands.put(commandName.toLowerCase(), command);
        }
    }

    /**
     * Uninitializes the CommandInstance
     *
     * @param command The CommandInstance that is being removed
     */
    public void removeCommand(CommandInstance command)
    {
        for (String commandName : command.getAliases())
        {
            _commands.remove(commandName);
        }
    }
}
