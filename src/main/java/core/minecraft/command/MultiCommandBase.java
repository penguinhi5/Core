package core.minecraft.command;

import core.minecraft.Component;
import core.minecraft.client.ClientManager;
import core.minecraft.command.CommandInstance;
import core.minecraft.command.CommandBase;
import core.minecraft.common.F;
import core.minecraft.common.Rank;
import core.minecraft.common.utils.PlayerUtil;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Represents a command that has multiple subcommands.
 *
 * @author PenguinHi5
 */
public abstract class MultiCommandBase extends CommandBase {

    protected HashMap<String, CommandInstance> _subCommands;
    protected CommandInstance _defaultCommand;

    /**
     * Creates an instance of the MultiCommandBase
     *
     * @param name the main command label
     * @param aliases any additional command labels
     * @param requiredRank the rank required to execute the command
     */
    public MultiCommandBase(ClientManager clientManager, String name, String[] aliases, Rank requiredRank)
    {
        super(clientManager, name, aliases, requiredRank);
        _subCommands = new HashMap<>();
    }

    /**
     * Creates an instance of the MultiCommandBase
     *
     * @param name the main command label
     * @param aliases any additional command labels
     * @param requiredRank the rank required to execute the command
     * @param additionalRanks any additional ranks that can run the command
     */
    public MultiCommandBase(ClientManager clientManager, String name, String[] aliases, Rank requiredRank, Rank[] additionalRanks)
    {
        super(clientManager, name, aliases, requiredRank, additionalRanks);
        _subCommands = new HashMap<>();
    }

    /**
     * Executes the command
     *
     * @param player the player executing the command
     * @param args additional arguments
     */
    @Override
    public void execute(Player player, String[] args)
    {
        String label = "";
        String[] newArgs = new String[] {};

        // Checks if any additional arguments were given
        if (args.length >= 1)
        {
            label = args[0];
            newArgs = new String[args.length - 1];
            for (int i = 1; i < args.length; i++)
            {
                newArgs[i - 1] = args[i];
            }
        }
        else
        {
            if (_defaultCommand != null)
            {
                if (!_defaultCommand.hasPermission(player))
                {
                    player.sendMessage(F.insufficientRankMessage(_defaultCommand.getRequiredRank()));
                    return;
                }

                _defaultCommand.execute(player, newArgs);
            }
            else
            {
                player.sendMessage(getProperUsageMessage());
            }
            return;
        }

        // Searches for a command that matches label and executes it
        for (String commandLabel : _subCommands.keySet())
        {
            if (commandLabel.equalsIgnoreCase(label))
            {
                CommandInstance command = _subCommands.get(label);

                if (!command.hasPermission(player))
                {
                    player.sendMessage(F.insufficientRankMessage(command.getRequiredRank()));
                    return;
                }

                _subCommands.get(label).execute(player, newArgs);
                return;
            }
        }

        player.sendMessage(getProperUsageMessage());
    }

    /**
     * Adds a subcommand
     *
     * @param command the subcommand being added
     */
    public void addSubCommand(CommandInstance command)
    {
        _subCommands.put(command.getName(), command);

        for (String commandName : command.getAliases())
        {
            _subCommands.put(commandName, command);
        }
    }

    /**
     * Sets the command that will be executed when no additional arguments are entered
     *
     * @param command the command that will be executed
     */
    public void setDefaultCommand(CommandInstance command)
    {
        _defaultCommand = command;
    }
}
