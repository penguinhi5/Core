package core.minecraft.region.flags;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created by Preston on 6/14/2019.
 *
 * This event should be called when a flag has been triggered within a region.
 *
 * When you listen for this event you should check to ensure the event has been called
 * on the appropriate region.
 */
public abstract class BaseRegionFlagTriggerEvent extends Event {

    private static final HandlerList _handlers = new HandlerList();

    /**
     * The ID of the region that the flag was triggered in.
     */
    private String _regionID;

    /**
     * Creates a new instance of BaseRegionFlagTriggerEvent with the specified relevant information.
     *
     * @param regionID the ID of the region that the flag was triggered in
     */
    public BaseRegionFlagTriggerEvent(String regionID)
    {
        _regionID = regionID;
    }

    /**
     * @return the ID of the region the flag was triggered in
     */
    public String getRegionID()
    {
        return _regionID;
    }

    @Override
    public HandlerList getHandlers() {
        return _handlers;
    }

    public static HandlerList getHandlerList()
    {
        return _handlers;
    }
}
