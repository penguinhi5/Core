package core.minecraft.common.utils;

import core.minecraft.common.F;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.ArrayList;
import java.util.List;

/**
 * This stores basic player utilities.
 *
 * @author Preston Brown
 */
public class PlayerUtil implements PluginMessageListener {

    /**
     * Searches for name in matchingNames. If matchingNames contains name than true will be returned,
     * otherwise if inform is true a message will be sent to caller informing them of all the matching names.
     *
     * @param caller the player searching for the matching name
     * @param name the name of the player being searched
     * @param matchingNames a list of names matching the specified name being searched for
     * @param inform whether or not we should inform the player of the matching names
     * @return true if matchingNames contains the specified name, otherwise false will be returned
     */
    public static boolean findExactMatch(Player caller, String name, List<String> matchingNames, boolean inform)
    {
        if (matchingNames.contains(name))
        {
            return true;
        }

        if (inform)
        {
            StringBuilder similarNames = new StringBuilder();
            similarNames.append(F.C_PREFIX + "No exact match Found! [Matching Names: " + F.C_CONTENT);
            for (String matchedName : matchingNames)
            {
                similarNames.append(matchedName + " ");
            }
            similarNames.append("| volume = " + matchingNames.size() + F.C_PREFIX + "]");

            caller.sendMessage(similarNames.toString());
        }
        return false;
    }

    /**
     * Kills the player.
     *
     * @param player the player that is being killed
     */
    public static void killPlayer(Player player, String causeOfDeath)
    {
        player.sendMessage("Consider yourself killed by " + causeOfDeath);
        //TODO Handle deaths in damage component
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {

    }
}
