package core.minecraft.cooldown;

import core.minecraft.Component;
import core.minecraft.cooldown.event.CooldownCompletedEvent;
import core.minecraft.timer.TimerType;
import core.minecraft.timer.event.TimerEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * Manages all of the cooldowns happening on the server.
 *
 * @author Preston Brown
 */
public class Cooldown implements Listener {

    private HashMap<String, Long> _cooldownMap = new HashMap<>();
    private static Cooldown _instance;

    public Cooldown(JavaPlugin plugin)
    {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static void initializeCooldown(JavaPlugin plugin)
    {
        if (_instance == null)
        {
            _instance = new Cooldown(plugin);
        }
    }

    public static Cooldown getInstance()
    {
        return _instance;
    }

    /**
     * Returns whether or not there is an active cooldown with the given name.
     *
     * @param name the name of the cooldown
     * @return true if there is an active cooldown with the given name, otherwise false
     */
    public boolean hasCooldown(String name)
    {
        return _cooldownMap.containsKey(name);
    }

    /**
     * Gets the amount of time left in the cooldown. If there is no cooldown with the given name -1 will be returned.
     *
     * @param name the name of the cooldown
     * @return the amount of time left in the cooldown in milliseconds if the cooldown exists, otherwise -1
     */
    public long getCooldownTime(String name)
    {
        if (_cooldownMap.containsKey(name))
        {
            return _cooldownMap.get(name) - System.currentTimeMillis();
        }
        return -1L;
    }

    /**
     * Creates a new cooldown. If a cooldown is already running with the same name it will be replaced.
     *
     * @param name
     * @param duration The duration of the cooldown in milliseconds
     */
    public void createCooldown(String name, long duration)
    {
        _cooldownMap.put(name, System.currentTimeMillis() + duration);
    }

    /**
     * Cancels the cooldown with the given name if present.
     *
     * @param name the name of the cooldown that is being canceled
     */
    public void cancelCooldown(String name)
    {
        _cooldownMap.remove(name);
    }

    @EventHandler
    public void updateCooldownMap(TimerEvent event)
    {
        if (event.getType() != TimerType.TICK)
        {
            return;
        }

        for (String key : _cooldownMap.keySet())
        {
            if (_cooldownMap.get(key) <= System.currentTimeMillis())
            {
                _cooldownMap.remove(key);
                CooldownCompletedEvent cooldownEvent = new CooldownCompletedEvent(key);
                Bukkit.getPluginManager().callEvent(cooldownEvent);
            }
        }
    }
}
