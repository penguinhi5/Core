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

    public void execute(Player player, String[] args);

    public Collection<String> getAliases();

    public String getName();

    public Rank getRequiredRank();

    public boolean hasPermission(Player player);

    public String getProperUsageMessage();
}
