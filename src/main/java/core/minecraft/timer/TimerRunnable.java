package core.minecraft.timer;

import org.bukkit.Bukkit;

/**
 * Updates the time of every {@link TimerType}.
 *
 * @author Preston Brown
 */
public class TimerRunnable implements Runnable {

    /**
     * Creates a new instance of TimerRunnable.
     */
    public TimerRunnable(Timer plugin)
    {
        Bukkit.getScheduler().runTaskTimer(plugin.getPlugin(), this, 0L, 1L);
    }

    @Override
    public void run()
    {
        updateTimer();
    }

    private void updateTimer()
    {
        for (TimerType type : TimerType.values())
        {
            type.updateTime(type);
        }
    }
}
