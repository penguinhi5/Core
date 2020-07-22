package core.minecraft.command;

import core.minecraft.chat.command.HelpCommand;
import core.minecraft.client.ClientManager;
import core.minecraft.common.F;
import core.minecraft.common.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class manages all of the player commands.
 *
 * @author Preston Brown
 */
public class CommandManager implements Listener {

    private static JavaPlugin _plugin;
    private HashMap<String, CommandInstance> _commands;
    private HashMap<CommandInstance, ArrayList<String>> _parentCommands;

    /**
     * Creates a new instance of CommandManager.
     *
     * @param plugin The main JavaPlugin instance.
     */
    public CommandManager(JavaPlugin plugin)
    {
        _plugin = plugin;
        _commands = new HashMap<>();
        _parentCommands = new HashMap<>();
        _plugin.getServer().getPluginManager().registerEvents(this, _plugin);
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
        _commands.put(command.getName().toLowerCase(), command);
        ArrayList<String> children = new ArrayList<>();
        for (String commandName : command.getAliases())
        {
            _commands.put(commandName.toLowerCase(), command);
            children.add(commandName);
        }
        _parentCommands.put(command, children);
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

    public void sendCommandListToPlayer(Player player, int page)
    {
        // Gets all of the commands the player has access to
        ArrayList<String> commands = new ArrayList<>();
        int skipCount = 0;
        for (CommandInstance command : _parentCommands.keySet())
        {
            if (command.hasPermission(player) && !command.isHidden())
            {
                if (_parentCommands.get(command).size() > 0) // If the command has aliases
                {
                    String dsc = "";
                    dsc = command.getHelpCommandMessage() + " Aliases: ";
                    for (String child : _parentCommands.get(command))
                    {
                        dsc += "/" + child;
                    }
                    commands.add(dsc);
                }
                else // If the command hos no aliases
                {
                    commands.add(command.getHelpCommandMessage());
                }
            }
        }

        // Ensures the requested page is not large than the page count
        double commandsPerPage = 5.0D;
        int pageCount = (int) Math.ceil((double) commands.size() / commandsPerPage);
        if (page > pageCount)
            page = pageCount;

        // Gets the specified page
        String initialMessage = F.C_PREFIX + "[Help] " + F.C_EMPHASIS1 + "/help [page]";
        StringBuilder stringBuilder = new StringBuilder(initialMessage);
        for (int i = (int)commandsPerPage * (page - 1); i < Math.min(commands.size(), commandsPerPage * page); i++)
        {
            stringBuilder.append("\n" + commands.get(i));
        }
        stringBuilder.append("\n" + F.C_PREFIX + "[Help]" + F.C_EMPHASIS1 + " Page " + page + " of " + pageCount);
        player.sendMessage(stringBuilder.toString());
    }
}
