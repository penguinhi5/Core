package core.minecraft.region;

import core.minecraft.Component;
import core.minecraft.command.CommandManager;
import core.minecraft.region.flags.PlayerEnterRegionFlag;
import core.minecraft.region.flags.BaseRegionFlagTriggerEvent;
import core.minecraft.region.flags.PlayerLeaveRegionFlag;
import core.minecraft.region.type.Region;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * Manages all of the regions on this server instance.
 */
public class RegionManager extends Component {

    /**
     * All of the existing regions.
     */
    private HashMap<String, Region> _regions = new HashMap<String, Region>();

    /**
     * Creates a new RegionManager instance.
     *
     * @param plugin the main JavaPlugin instance
     * @param commandManager the main CommandManager instance
     */
    public RegionManager(JavaPlugin plugin, CommandManager commandManager)
    {
        super("Regions", plugin, commandManager);

        // registers all of the flags so they start listening to be triggered
        registerFlags();
    }

    /**
     * Creates a new region. If the region was successfully created true will be returned, if a region already exists
     * with this id, it will not be created and false will be returned.
     *
     * All regions are created with a default priority of 100.
     *
     * @param region The region
     * @return true if there is no pre-existing region with this name, otherwise false.
     */
    public boolean createRegion(Region region)
    {
        // Checks if a region already exists with the region's ID
        if (_regions.containsKey(region.getID()))
        {
            return false;
        }

        _regions.put(region.getID(), region);
        return true;
    }

    /**
     * Removes the region with the given ID. If a region previously existed with the ID the region will be returned,
     * otherwise if no region existed with the given ID null will be returned.
     *
     * @param region the ID of the region being removed
     * @return the removed region if a region with the ID previously existed, otherwise null
     */
    public Region removeRegion(String region)
    {
        return _regions.remove(region);
    }

    /**
     * @return a map containing all of the regions and their IDs
     */
    public HashMap<String, Region> getRegions()
    {
        // returns a copy of _regions
        return new HashMap<String, Region>(_regions);
    }

    /**
     * Returns the region with the given ID. If no region exists with the ID, null is returned.
     *
     * @param region the ID of the region
     * @return the region if it exists, otherwise null.
     */
    public Region getRegion(String region)
    {
        return _regions.get(region);
    }

    /**
     * Calls the BaseRegionFlagTriggerEvent when a flag has been triggered in a region.
     *
     * @param event the BaseRegionFlagTriggerEvent that will be called
     */
    public void callRegionFlagTriggerEvent(BaseRegionFlagTriggerEvent event)
    {
        getPlugin().getServer().getPluginManager().callEvent(event);
    }

    /**
     * Registers all of the flags so they begin to listen for them to be triggered. (Note: make sound better)
     */
    private void registerFlags()
    {
        // IMPORTANT: all flags must be instantiated here
        Bukkit.getPluginManager().registerEvents(new PlayerEnterRegionFlag(this), getPlugin());
        Bukkit.getPluginManager().registerEvents(new PlayerLeaveRegionFlag(this), getPlugin());
    }

}
