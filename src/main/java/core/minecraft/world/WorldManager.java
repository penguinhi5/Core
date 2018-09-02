package core.minecraft.world;

import core.minecraft.Component;
import core.minecraft.common.utils.FileUtil;
import core.minecraft.world.config.MapConfig;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Manages all of the worlds currently running on the server.
 *
 * @author Preston Brown
 */
public class WorldManager extends Component implements Listener {

    private final WeatherType DEFAULT_WEATHER = WeatherType.CLEAR;
    private final long DEFAULT_TIME = 6000;
    private HashMap<String, WeatherType> _weather = new HashMap<>();
    private HashMap<String, Long> _time = new HashMap<>();

    private File _mapContainer;
    private ArrayList<Location> _loadedChunks = new ArrayList<>();
    private HashMap<String, MapConfig> _mapConfigs = new HashMap<>();
    private ArrayList<String> _loadedWorlds = new ArrayList<>();

    /**
     * This creates a new Component with the given name under plugin.
     *
     * @param plugin the main JavaPlugin instance
     */
    public WorldManager(JavaPlugin plugin)
    {
        super("World", plugin);
        _mapContainer = new File(Bukkit.getWorldContainer().getPath() + File.pathSeparator + "maps");
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    public void unloadWorld()
    {

    }

    /**
     * Duplicates the world file from the map repository, collects all of the world data, and loads all of the chunks.
     *
     * @param worldName the file name of the world being loaded
     * @return true if the world was successfully loaded, otherwise false
     */
    public boolean loadWorld(String worldName)
    {
        String path = getWorldDirectory(worldName);
        if (!new File(path).exists())
        {
            System.out.println("Failed to load world '" + worldName + "'. World does not exist at path " + path);
            return false;
        }

        try
        {
            FileUtil.unzipFile(path, "");
        }
        catch (IOException e)
        {
            System.out.println("Failed to load world '" + worldName + "'");
            e.printStackTrace();
            return false;
        }

        File file = new File(worldName);
        if (!file.exists())
        {
            System.out.println("Failed to load world '" + worldName + "'");
            return false;
        }
        World world = Bukkit.createWorld(new WorldCreator(worldName));
        _loadedWorlds.add(worldName.toLowerCase());

        MapConfig mapConfig = new MapConfig(path, world);
        _mapConfigs.put(worldName.toLowerCase(), mapConfig);
        loadChunks(mapConfig);
        return true;
    }

    /**
     * Unloads the specified world name
     * @return
     */
    public boolean unloadWorld()
    {

    }

    private void loadChunks(MapConfig mapconfig)
    {

    }

    /**
     * Returns the {@link MapConfig} of the map with the specified name. If no map is currently loaded with that
     * name, null is returned.
     *
     * @param map the name of the map
     * @return the MapConfig of the specified map if it exists, otherwise null
     */
    public MapConfig getMapConfig(String map)
    {
        return _mapConfigs.get(map.toLowerCase());
    }

    /**
     * Returns a list containing all of the maps that are currently loaded.
     *
     * @return a list containing all of the loaded maps
     */
    public List<String> getLoadedMaps()
    {
        return (ArrayList<String>) _loadedWorlds.clone();
    }

    /**
     * Gets the public name of the map with the specified world name.
     *
     * @param world the name of the world directory
     * @return the name of the map
     */
    public String getMapName(String world)
    {

    }

    /**
     * This gets all of the world directory names that fall under the specified type of map.
     *
     * @param mapType the type of maps you are retrieving
     * @return all of the world directory names of the specified type
     */
    public List<String> getMapsOfType(MapType mapType)
    {
        if (_mapContainer == null)
        {

        }
        if (mapType == MapType.ALL) // Retrieves all of the maps
        {

        }
        else
        {

        }
    }

    /**
     * Returns whether or not the map is currently loaded.
     *
     * @param map the map being checked
     * @return true if the map is currently loaded, otherwise false
     */
    public boolean isMapLoaded(String map)
    {
        return _loadedWorlds.contains(map);
    }

    /**
     * Returns the directory of the world with the given name. If no world exists with the given name null will be
     * returned.
     *
     * @param worldFileName the file name of the world
     * @return the directory of the world if it exists, otherwise null
     */
    public String getWorldDirectory(String worldFileName)
    {
        if (_mapContainer != null && _mapContainer.exists())
        {
            return FileUtil.searchForFile(_mapContainer, worldFileName);
        }
        return null;
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event)
    {
        Location chunkLoc = new Location(event.getWorld(), event.getChunk().getX(), 0, event.getChunk().getZ());
        if (_loadedChunks.contains(chunkLoc))
        {
            event.setCancelled(true);
        }
    }
}
