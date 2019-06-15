package core.minecraft.region.flags;

import core.minecraft.region.Region;
import core.minecraft.region.RegionManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * This flag listens for players leaving a region.
 */
public class PlayerLeaveRegionFlag implements Listener {

    /**
     * The unique ID used to keep track of this flag.
     */
    private static String _flagID = "playerleaveregion";

    /**
     * The main RegionManager instance.
     */
    private RegionManager _regionManager;

    /**
     * Creates a new PlayerLeaveRegionFlag instance.
     */
    public PlayerLeaveRegionFlag(RegionManager regionManager)
    {
        _regionManager = regionManager;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        for(Region region : _regionManager.getRegions().values())
        {
            // if the region has this flag, previously contain the player, and no longer contains the player
            if (region.hasFlag(_flagID) && region.containsLoc(event.getFrom()) && !region.containsLoc(event.getTo()))
            {
                _regionManager.callRegionFlagTriggerEvent(new PlayerLeaveRegionEvent(region.getID(), event.getPlayer()));
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
