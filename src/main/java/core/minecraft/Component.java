package core.minecraft;

import core.minecraft.command.CommandInstance;
import core.minecraft.command.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is used to create components that complete a specific set of tasks.
 *
 * @author Preston Brown
 */
public abstract class Component {

    private String _name;
    private JavaPlugin _plugin;

    /**
     * This creates a new Component with the given name under plugin.
     *
     * @param name the name of the new component
     * @param plugin the main JavaPlugin instance
     */
    public Component(String name, JavaPlugin plugin)
    {
        _name = name;
        _plugin = plugin;
    }

    /**
     * Registers the command as a command through the {@link CommandManager}.
     *
     * @param command the command that is being registered
     */
    public void addCommand(CommandInstance command)
    {
        CommandManager.getInstance().addCommand(command);
    }

    /**
     * Unregisters the command through the {@link CommandManager}.
     *
     * @param command the command that is being unregistered
     */
    public void removeCommand(CommandInstance command)
    {
        CommandManager.getInstance().removeCommand(command);
    }

    /**
     * Returns the main JavaPlugin instance.
     *
     * @return the main JavaPlugin instance
     */
    public JavaPlugin getPlugin()
    {
        return _plugin;
    }
}
