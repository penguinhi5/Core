package core.minecraft.command;

import core.minecraft.common.Rank;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * Represents a player command.
 *
 * @author Preston Brown
 */
public interface CommandInstance {

    /**
     * This is called when the command has been executed.
     *
     * @param player the player the executed the command
     * @param args the additional arguments included in the command
     */
    public void execute(Player player, String[] args);

    /**
     * Returns a list containing all the aliases that can run the command.
     *
     * @return a list of all of the aliases that can run the command
     */
    public Collection<String> getAliases();

    /**
     * Gets the command name used to execute the command
     *
     * @return the command name used to execute the command
     */
    public String getName();

    /**
     * Returns that rank that a player must have to execute this command.
     *
     * @return the rank that is required to execute this command.
     */
    public Rank getRequiredRank();

    /**
     * Checks if the specified player has the permissions necessary to execute the command.
     *
     * @param player the player executing the command
     * @return if the player has permission to execute the command
     */
    public boolean hasPermission(Player player);

    /**
     * Returns the proper usage of this command.
     *
     * @return the proper usage of this command
     */
    public String getProperUsageMessage();

    /**
     * Gets the full message the will show up in command lists.
     */
    public String getHelpCommandMessage();

    /**
     * If this command should be hidden from command lists.
     */
    public boolean isHidden();
}
