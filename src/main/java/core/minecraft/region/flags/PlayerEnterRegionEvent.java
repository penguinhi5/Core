package core.minecraft.region.flags;

import org.bukkit.entity.Player;

/**
 * This events is called when a player enters a region with the PlayerEnterRegionFlag.
 */
public class PlayerEnterRegionEvent extends BaseRegionFlagTriggerEvent {

    /**
     * The player that entered the region.
     */
    private Player _player;

    /**
     * Creates a new PlayerEnterRegionEvent instance that is called when a player enters a region.
     *
     * @param regionID the ID of the region that the flag was triggered in
     * @param player   the player that entered the region
     */
    public PlayerEnterRegionEvent(String regionID, Player player)
    {
        super(regionID);
        _player = player;
    }

    /**
     * @return the player that triggered the events
     */
    public Player getPlayer()
    {
        return _player;
    }
}
