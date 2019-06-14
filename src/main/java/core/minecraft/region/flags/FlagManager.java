package core.minecraft.region.flags;

import core.minecraft.region.Region;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class FlagManager implements Listener {

    private HashMap<String, Region> _regions;

    private JavaPlugin _plugin;

    public FlagManager(JavaPlugin plugin, HashMap<String, Region> regions)
    {
        _regions = regions;
        _plugin = plugin;
    }

    @EventHandler
    public void PlayerEnterExitRegion()
    {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(_plugin, new Runnable() {
            @Override
            public void run()
            {

            }
        }, 0L, 10L);
    }
}
