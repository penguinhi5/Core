package core.minecraft.timer.event;

import core.minecraft.timer.TimerType;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called when one of the {@link TimerType}s reaches a milestone.
 *
 * @author Preston Brown
 */
public class TimerEvent extends Event {

    private static final HandlerList _handlers = new HandlerList();
    private TimerType _timer;

    /**
     * Creates a new instance of TimerEvent with the specified {@link TimerType}
     *
     * @param timer the {@link TimerType} that has reached a milestone
     */
    public TimerEvent(TimerType timer)
    {
        _timer = timer;
    }

    /**
     * @return the {@link TimerType} that was called in the event
     */
    public TimerType getType()
    {
        return _timer;
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
