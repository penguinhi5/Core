package core.minecraft.client.command;

import core.minecraft.client.ClientManager;
import core.minecraft.command.CommandBase;
import core.minecraft.common.F;
import core.minecraft.common.Rank;
import core.minecraft.common.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * This command is used to update a specified player's rank.
 *
 * @author Preston Brown
 */
public class SetRank extends CommandBase<ClientManager> {

    /**
     * Creates a new instance of the SetRank command with the provide {@link ClientManager}.
     *
     * @param plugin the {@link ClientManager} object
     */
    public SetRank(ClientManager plugin)
    {
        super(plugin, plugin, "setrank", new String[] {"updaterank"}, Rank.ADMIN);
    }

    @Override
    public void execute(Player caller, String[] args)
    {
        if (args.length != 2)
        {
            caller.sendMessage(getProperUsageMessage());
            return;
        }

        String name = args[0];
        Rank callerRank = _clientManager.getPlayerData(caller.getName()).getRank();
        Rank newRank = null;
        try
        {
            newRank = Rank.valueOf(args[1]);
        }
        catch (IllegalArgumentException e)
        {
            caller.sendMessage(F.componentMessage("Command", "You entered an invalid rank!"));
            caller.sendMessage(getProperUsageMessage());
            return;
        }

        // You can only set rank less than or equal to your own
        if (callerRank.compareTo(newRank) <= 0)
        {
            UUID uuid = _plugin.getRepository().getUUIDFromName(name);
            if (uuid == null)
            {
                caller.sendMessage(F.componentMessage("Command", "Player " + name + " does not exist"));
                //TODO this is to test this set of features but change this to just say caller doesn't exist
                List<String> matchingNames = _plugin.getRepository().getMatchingPlayers(name);
                PlayerUtil.findExactMatch(caller, name, matchingNames, true);
                return;
            }

            boolean updated;
            if (newRank.isPurchased())
            {
                updated = _plugin.getRepository().updatePurchasedRank(uuid.toString(), newRank);
                _plugin.getRepository().updateRank(uuid.toString(), Rank.DEFAULT);

            }
            else
            {
                updated = _plugin.getRepository().updateRank(uuid.toString(), newRank);
            }

            if (updated)
            {
                caller.sendMessage(F.componentMessage("Command",
                        F.C_EMPHASIS + name + F.C_CONTENT + "'s rank was successfully updated to " + newRank.getDisplayName(false, true, true) + F.C_CONTENT + ".")
                );

                Player player;
                if ((player = Bukkit.getPlayer(name)) != null)
                {
                    if (newRank.isPurchased())
                    {
                        _plugin.getPlayerData(player).setPurchasedRank(newRank);
                        _plugin.getPlayerData(player).setRank(Rank.DEFAULT);
                    }
                    else
                    {
                        _plugin.getPlayerData(player).setRank(newRank);
                    }
                    player.sendMessage(F.componentMessage("Rank", "Your rank has been updated to " + newRank.getDisplayName(false, true, true) + F.C_CONTENT + "."));
                }
            }
            else
            {
                caller.sendMessage(F.componentMessage("Command", "There was an error updating " + F.C_EMPHASIS + name + F.C_CONTENT + "'s rank."));
            }
        }
        else
        {
            caller.sendMessage(F.insufficientRankMessage(newRank));
        }
    }

    @Override
    public String getProperUsageMessage()
    {
        return F.properCommandUsageMessage("/setrank <player> <rank>", "/setrank penguinhi5 MOD");
    }
}
