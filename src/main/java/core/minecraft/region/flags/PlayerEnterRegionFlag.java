package core.minecraft.region.flags;

import core.minecraft.region.Region;
import core.minecraft.region.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * This flag listens for players entering a region.
 */
public class PlayerEnterRegionFlag implements Listener {

    /**
     * The unique ID used to keep track of this flag.
     */
    private static String _flagID = "playerenterregion";

    /**
     * The main RegionManager instance.
     */
    private RegionManager _regionManager;

    /**
     * Creates a new PlayerEnterRegionFlag instance.
     */
    public PlayerEnterRegionFlag(RegionManager regionManager)
    {
        _regionManager = regionManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        for(Region region : _regionManager.getRegions().values())
        {
            // if the region has this flag, contains the player, and didn't previously contain the player
            if (region.hasFlag(_flagID) && region.containsLoc(event.getTo()) && !region.containsLoc(event.getFrom()))
            {
                _regionManager.callRegionFlagTriggerEvent(new PlayerEnterRegionEvent(region.getID(), event.getPlayer()));
            }
        }
    }

    /**
     * @return the unique global name of this flag
     */
    public static String getFlagID()
    {
        return _flagID;
    }
}
