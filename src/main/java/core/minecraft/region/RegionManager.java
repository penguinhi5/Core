package core.minecraft.region;

import core.minecraft.Component;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;

public class RegionManager extends Component {

    private HashMap<String, Region> _regions = new HashMap<String, Region>();

    private static RegionManager _instance;

    public RegionManager(JavaPlugin plugin)
    {
        super("Regions", plugin);
    }

    /**
     * Initializes this class.
     */
    public static void initialize(JavaPlugin plugin)
    {
        if (_instance == null)
        {
            _instance = new RegionManager(plugin);
        }
    }

    /**
     * Creates a new region.
     * If a region already exists with this id, it will not be created.
     * All _regions are created with a default priority of 100.
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

    public RegionManager getInstance()
    {
        return _instance;
    }

    public List<Region>()
    {

    }

    // TODO also make it so another class checks for different flags such as enter and exit
    // TODO and it checks by seeing if all _regions has that flag and if they do they compare the
    // TODO player location to the region
    // TODO ALSO make this class static so you can create a region without a reference to the class
}
