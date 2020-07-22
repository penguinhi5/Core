package core.minecraft.command;

import core.minecraft.Component;
import core.minecraft.client.ClientManager;
import core.minecraft.command.CommandInstance;
import core.minecraft.common.Rank;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single command base.
 *
 * @author Preston Brown
 */
public abstract class CommandBase implements CommandInstance {

    protected String _name;
    protected List<String> _aliases;
    protected boolean _isHidden;
    protected Rank _requiredRank;
    protected Rank[] _additionalRanks;
    protected ClientManager _clientManager;

    /**
     * Creates a new instance of CommandBase with the given data.
     *
     * @param name the name of the command that must be types in chat in order to execute the command
     * @param aliases any aliases can be typed in chat that will also execute the command
     * @param isHidden if the command should be hidden from all command lists
     * @param requiredRank the rank required to execute the command
     */
    public CommandBase(ClientManager clientManager, String name, String[] aliases, boolean isHidden, Rank requiredRank)
    {
        this(clientManager, name, aliases, isHidden, requiredRank, new Rank[] {});
    }

    /**
     * Creates a new instance of CommandBase with the given data.
     *
     * @param name the name of the command that must be types in chat in order to execute the command
     * @param aliases any aliases can be typed in chat that will also execute the command
     * @param isHidden if the command should be hidden from all command lists
     * @param requiredRank the rank required to execute the command
     * @param additionalRanks any additional ranks that can execute the command
     */
    public CommandBase(ClientManager clientManager, String name, String[] aliases, boolean isHidden, Rank requiredRank, Rank[] additionalRanks)
    {
        _clientManager = clientManager;
        _name = name;
        _aliases = Arrays.asList(aliases);
        _isHidden = isHidden;
        _requiredRank = requiredRank;
        _additionalRanks = additionalRanks;
    }

    public abstract void execute(Player player, String[] args);

    public boolean hasPermission(Player player)
    {
        Rank playerRank = _clientManager.getPlayerData(player.getName()).getRank();
        if (playerRank.hasRank(player, _requiredRank))
        {
            return true;
        }

        for (Rank rank : _additionalRanks)
        {
            if (playerRank.hasRank(player, rank))
            {
                return true;
            }
        }
        return false;
    }

    public Collection<String> getAliases()
    {
        return _aliases;
    }

    public Rank getRequiredRank()
    {
        return _requiredRank;
    }

    public String getName()
    {
        return _name;
    }

    public boolean isHidden()
    {
        return _isHidden;
    }

    public abstract String getProperUsageMessage();

    public abstract String getHelpCommandMessage();
}
