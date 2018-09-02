package core.minecraft.timer;

import core.minecraft.timer.event.TimerEvent;
import org.bukkit.Bukkit;

/**
 * Stores all of the different types of timers.
 *
 * @author Preston Brown
 */
public enum TimerType {

    FIVE_MINUTES(300000L),
    MINUTE(60000L),
    THIRTY_SECONDS(30000L),
    FIVE_SECONDS(5000L),
    SECOND(1000L),
    TICK(49L);

    private long _time;
    private long _last;

    private TimerType(long time)
    {
        _time = time;
        _last = System.currentTimeMillis();
    }

    /**
     * Updates the time of the specified type of timer. If enough time has passed an event
     * of the specified TimerType will be called.
     *
     * @param type the TimerType that is being updated
     */
    public void updateTime(TimerType type)
    {
        if (System.currentTimeMillis() - _last >= _time)
        {
            Bukkit.getPluginManager().callEvent(new TimerEvent(type));
            _last = System.currentTimeMillis();
        }
    }
}
