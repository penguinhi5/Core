package core.minecraft.timer;

import core.minecraft.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This manages the {@link core.minecraft.timer.event.TimerEvent} that is executed periodically.
 *
 * @author Preston Brown
 */
public class Timer extends Component {

    private TimerRunnable runnable;

    /**
     * Creates a new instance of Timer.
     *
     * @param plugin the main JavaPlugin instance
     */
    public Timer(JavaPlugin plugin)
    {
        super("Timer", plugin);
        runnable = new TimerRunnable(this);
    }

}
