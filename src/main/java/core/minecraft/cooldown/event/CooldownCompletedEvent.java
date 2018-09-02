package core.minecraft.cooldown.event;


import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is executed when a cooldown is completed.
 *
 * @author Preston Brown
 */
public class CooldownCompletedEvent extends Event {

    private static final HandlerList _handlers = new HandlerList();
    private String _name;

    public CooldownCompletedEvent(String name)
    {
        _name = name;
    }

    /**
     * Returns the name of the cooldown that was completed.
     *
     * @return the name of the cooldown
     */
    public String getName()
    {
        return _name;
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
