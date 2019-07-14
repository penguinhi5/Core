package core.minecraft.timer;

import core.minecraft.Component;
import core.minecraft.command.CommandManager;
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
     * @param commandManager the main CommandManager instance
     */
    public Timer(JavaPlugin plugin, CommandManager commandManager)
    {
        super("Timer", plugin, commandManager);
        runnable = new TimerRunnable(this);
    }

}
