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
public abstract class CommandBase<ComponentPlugin extends Component> implements CommandInstance {

    protected String _name;
    protected List<String> _aliases;
    protected ComponentPlugin _plugin;
    protected Rank _requiredRank;
    protected Rank[] _additionalRanks;
    protected ClientManager _clientManager;

    /**
     * Creates a new instance of CommandBase with the given data.
     *
     * @param plugin the Component initializing the command
     * @param name the name of the command that must be types in chat in order to execute the command
     * @param aliases any aliases can be typed in chat that will also execute the command
     * @param requiredRank the rank required to execute the command
     */
    public CommandBase(ComponentPlugin plugin, ClientManager clientManager, String name, String[] aliases, Rank requiredRank)
    {
        this(plugin, clientManager, name, aliases, requiredRank, new Rank[] {});
    }

    /**
     * Creates a new instance of CommandBase with the given data.
     *
     * @param plugin the Component initializing the command
     * @param name the name of the command that must be types in chat in order to execute the command
     * @param aliases any aliases can be typed in chat that will also execute the command
     * @param requiredRank the rank required to execute the command
     * @param additionalRanks any additional ranks that can execute the command
     */
    public CommandBase(ComponentPlugin plugin, ClientManager clientManager, String name, String[] aliases, Rank requiredRank, Rank[] additionalRanks)
    {
        _plugin = plugin;
        _clientManager = clientManager;
        _name = name;
        _aliases = Arrays.asList(aliases);
        _requiredRank = requiredRank;
        _additionalRanks = additionalRanks;
    }

    public abstract void execute(Player player, String[] args);

    /**
     * Checks if the specified player has the permissions necessary to execute the command.
     *
     * @param player the player executing the command
     * @return if the player has permission to execute the command
     */
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

    /**
     * Returns a list containing all the aliases that can run the command.
     *
     * @return a list of all of the aliases that can run the command
     */
    public Collection<String> getAliases()
    {
        return _aliases;
    }

    /**
     * Returns that rank that a player must have to execute this command.
     *
     * @return the rank that is required to execute this command.
     */
    public Rank getRequiredRank()
    {
        return _requiredRank;
    }

    /**
     * Gets the command name used to execute the command
     *
     * @return the command name used to execute the command
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Returns the proper usage of this command.
     *
     * @return the proper usage of this command
     */
    public abstract String getProperUsageMessage();
}
