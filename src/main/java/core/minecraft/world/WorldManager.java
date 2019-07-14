package core.minecraft.world;

import core.minecraft.Component;
import core.minecraft.command.CommandManager;
import core.minecraft.common.utils.FileUtil;
import core.minecraft.world.config.MapConfig;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

    private MapConfig _activeMap;
    private File _mapContainer;
    private HashSet<Location> _loadedChunks = new HashSet<>();
    private HashMap<String, MapConfig> _mapConfigs = new HashMap<>();
    private ArrayList<String> _loadedWorlds = new ArrayList<>();

    /**
     * This creates a new Component with the given name under plugin.
     *
     * @param plugin the main JavaPlugin instance
     * @param commandManager the main CommandManager instance
     */
    public WorldManager(JavaPlugin plugin, CommandManager commandManager)
    {
        super("World", plugin, commandManager);
        _mapContainer = new File(Bukkit.getWorldContainer().getPath() + File.pathSeparator + "maps");
        Bukkit.getPluginManager().registerEvents(this, getPlugin());

        //loads the main world
        File file = new File("world");
        if (!file.exists())
        {
            System.out.println("Failed to load world 'world'");
        }
        World world = Bukkit.createWorld(new WorldCreator("world"));
        _loadedWorlds.add("world");

//        MapConfig mapConfig = new MapConfig("world", world);
//        _activeMap = mapConfig;
//        _mapConfigs.put("world", mapConfig);
//        _weather.put("world", DEFAULT_WEATHER);
//        _time.put("world", DEFAULT_TIME);
//        loadChunks(mapConfig);
    }

    /**
     * Duplicates the world file from the map repository, collects all of the world data, and loads all of the chunks.
     *
     * @param worldName the file name of the world being loaded
     * @return true if the world was successfully loaded, otherwise false
     */
    public boolean loadWorld(String worldName)
    {
        return loadWorld(worldName, DEFAULT_WEATHER, DEFAULT_TIME);
    }

    /**
     * Duplicates the world file from the map repository, collects all of the world data, and loads all of the chunks.
     *
     * @param worldName the file name of the world being loaded
     * @param weather the weather
     * @param time the time of day (noon = 6000, midnight = 18000)
     * @return true if the world was successfully loaded, otherwise false
     */
    public boolean loadWorld(String worldName, WeatherType weather, long time)
    {
        String path = getWorldDirectory(worldName);
        if (path == null || !new File(path).exists())
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
        _loadedWorlds.add(worldName);

//        MapConfig mapConfig = new MapConfig(path, world); // TODO might have to change path to the world file's config
//        _mapConfigs.put(worldName, mapConfig);
//        _weather.put(worldName, weather);
//        _time.put(worldName, time);
//        loadChunks(mapConfig);
        return true;
    }

    /**
     * Unloads the world with the specified name.
     *
     * @return true if the world was unloaded, otherwise false
     */
    public boolean unloadWorld(String world)
    {
        boolean successful = false;
        World unloadWorld;
        if ((unloadWorld = Bukkit.getWorld(world)) != null)
        {
            _loadedWorlds.remove(world);
            _mapConfigs.remove(world);
            _time.remove(world);
            _weather.remove(world);

            // teleports the players to the lobby if the main world exists and is loaded
            // if the world is not found or is not loaded they will be transported to the hub
            if (Bukkit.getWorld("world") != null && _mapConfigs.containsKey("world"))
            {
                MapConfig mapConfig = _mapConfigs.get("world");
                teleportAllPlayersToMap(mapConfig);
            }
            else
            {
                //TODO Send all players to the hub
            }

            // removes all of the saved chunks
            for (Location loc : _loadedChunks)
            {
                if (loc.getWorld().getName().equalsIgnoreCase(world))
                {
                    _loadedChunks.remove(loc);
                }
            }

            // unloads and deletes the world
            File file = unloadWorld.getWorldFolder();
            successful = Bukkit.getServer().unloadWorld(unloadWorld, false);
            FileUtil.deleteFile(file);
        }
        return successful;
    }

    /**
     * Loads all of the chunks in the map.
     *
     * @param mapconfig the config object of the map that is loading the chunks of
     */
    private void loadChunks(MapConfig mapconfig)
    {
        if (mapconfig != null)
        {
            for (int x = mapconfig.getMinX(); x <= mapconfig.getMaxX() + 16; x += 16)
            {
                for (int z = mapconfig.getMinZ(); x <= mapconfig.getMaxZ() + 16; z += 16)
                {
                    Location location = new Location(mapconfig.getWorld(), x, 0, z);
                    Location chunkLoc = new Location(mapconfig.getWorld(), location.getChunk().getX() * 16, 0, location.getChunk().getZ() * 16);
                    _loadedChunks.add(chunkLoc);
                    chunkLoc.getChunk().load();
                }
            }
        }
    }

    /**
     * Sets the map that all of the players should spawn in when they join the server.
     *
     * @param map the name of the map
     * @return true if the map exists, otherwise false
     */
    public boolean setActiveMap(String map)
    {
        if (_mapConfigs.containsKey(map))
        {
            _activeMap = _mapConfigs.get(map);
            return true;
        }
        return false;
    }

    /**
     * Gets the MapConfig of the currently active map.
     *
     * @return the MapConfig of the active map
     */
    public MapConfig getActiveMap()
    {
        return _activeMap;
    }

    /**
     * Updates the time in the specified map.
     *
     * @param map the name of the map
     * @param time the new time
     * @return true if the time was successfully updated, otherwise false
     */
    public boolean setMapTime(String map, long time)
    {
        if (_loadedWorlds.contains(map))
        {
            if (Bukkit.getWorld(map) != null)
            {
                Bukkit.getWorld(map).setTime(time);
                _time.put(map, time);
                return true;
            }
        }
        return false;
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
     * Teleports the player to the given map
     *
     * @param player the player being teleported
     * @param mapConfig the MapConfig of the world the player is being teleported to
     */
    public void teleportPlayerToMap(Player player, String team, MapConfig mapConfig)
    {
        mapConfig.getTeamSpawnLocations("");
    }

    /**
     * Teleports all players to the given map so every player spawns at a different spawn point. If there
     * isn't enough spawn points for every player there will be some spawn points with multiple players.
     *
     * @param mapConfig the MapConfig of the world the player is being teleported to
     */
    public void teleportAllPlayersToMap(MapConfig mapConfig)
    {

    }

    /**
     * Gets the public name of the map with the specified world name.
     *
     * @param world the name of the world directory
     * @return the name of the map
     */
    public String getMapName(String world)
    {
        return "";
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
        return new ArrayList<>();
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
        Location chunkLoc = new Location(event.getWorld(), event.getChunk().getX() * 16, 0, event.getChunk().getZ() * 16);
        if (_loadedChunks.contains(chunkLoc))
        {
            event.setCancelled(true);
        }
    }
}
