package core.minecraft.region.flags;

import org.bukkit.entity.Player;

/**
 * This event is called when a player leaves a region with the {@Link PlayerLeaveRegionFlag}.
 */
public class PlayerLeaveRegionEvent extends BaseRegionFlagTriggerEvent {

    /**
     * The player that entered the region.
     */
    private Player _player;

    /**
     * Creates a new PlayerLeaveRegionEvent instance that is called when a player leaves a region.
     *
     * @param regionID the ID of the region that the flag was triggered in
     * @param player   the player that left the region
     */
    public PlayerLeaveRegionEvent(String regionID, Player player)
    {
        super(regionID);
        _player = player;
    }

    /**
     * @return the player that triggered the event
     */
    public Player getPlayer()
    {
        return _player;
    }
}
