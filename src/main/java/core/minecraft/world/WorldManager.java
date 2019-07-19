package core.minecraft.world;

import core.minecraft.Component;
import core.minecraft.command.CommandManager;
import core.minecraft.common.utils.FileUtil;
import core.minecraft.common.utils.PlayerUtil;
import core.minecraft.world.config.MapConfig;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Weather;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
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
    private HashSet<String> _loadedChunks = new HashSet<>();
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
        _mapContainer = new File(Bukkit.getWorldContainer().getPath() + File.separator + "maps");
        Bukkit.getPluginManager().registerEvents(this, getPlugin());

        //loads the main world
        File file = new File("world");
        if (!file.exists())
        {
            System.out.println("Failed to load world 'world'");
        }
        World world = Bukkit.createWorld(new WorldCreator("world"));
        _loadedWorlds.add("world");

        MapConfig mapConfig = new MapConfig("world", world);
        _activeMap = mapConfig;
        _mapConfigs.put("world", mapConfig);
        _weather.put("world", DEFAULT_WEATHER);
        _time.put("world", DEFAULT_TIME);
        loadChunks(mapConfig);
    }

    /**
     * Duplicates the world file from the map repository, collects all of the world data, and loads all of the chunks.
     *
     * @param worldFileName the file name of the world being loaded
     * @return true if the world was successfully loaded, otherwise false
     */
    public boolean loadWorld(String worldFileName)
    {
        return loadWorld(worldFileName, DEFAULT_WEATHER, DEFAULT_TIME);
    }

    /**
     * Duplicates the world file from the map repository, collects all of the world data, and loads all of the chunks.
     *
     * @param worldFileName the file name of the world being loaded
     * @param weather the weather
     * @param time the time of day (noon = 6000, midnight = 18000)
     * @return true if the world was successfully loaded, otherwise false
     */
    public boolean loadWorld(String worldFileName, WeatherType weather, long time)
    {
        String path = getWorldDirectory(worldFileName);
        if (path == null || !new File(path).exists())
        {
            System.out.println("Failed to load world '" + worldFileName + "'. World does not exist at path " + path);
            return false;
        }

        try
        {
            FileUtil.unzipFile(path, Bukkit.getWorldContainer().getPath());
        }
        catch (ZipException e)
        {
            System.out.println("Failed to unzip world '" + worldFileName + "'");
            e.printStackTrace();
            return false;
        }

        String worldName = FilenameUtils.removeExtension(worldFileName);

        World world = Bukkit.createWorld(new WorldCreator(worldName));
        _loadedWorlds.add(worldName);

        MapConfig mapConfig = new MapConfig(Bukkit.getWorldContainer() + File.separator + worldName, world);
        _mapConfigs.put(worldName, mapConfig);
        _weather.put(worldName, weather);
        _time.put(worldName, time);
        loadChunks(mapConfig);
        return true;
    }

    /**
     * Unloads the world with the specified name.
     *
     * @return true if the world was unloaded, otherwise false
     */
    public boolean unloadWorld(String world)
    {
        // Ensures this isn't the only loaded world
        if (_loadedWorlds.size() <= 1)
        {
            return false;
        }

        boolean successful = false;
        World unloadWorld;
        if ((unloadWorld = Bukkit.getWorld(world)) != null)
        {
            _loadedWorlds.remove(world);
            _mapConfigs.remove(world);
            _time.remove(world);
            _weather.remove(world);

            // Updates the active world if this is the active world
            // If world still exists that will be set the the active world, otherwise a random
            // world will be set as the active world
            if (_activeMap.getWorld().getName().equals(world))
            {
                if (_loadedWorlds.contains("world"))
                {
                    _activeMap = _mapConfigs.get("world");
                }
                else
                {
                    _activeMap = _mapConfigs.get(_loadedWorlds.get(0));
                }
            }

            // Teleports the players in the world to the lobby if the main world exists and is loaded
            for (Player player : Bukkit.getOnlinePlayers())
            {
                teleportPlayerToMap(player, "all", _activeMap);
            }

            // Removes all of the saved chunks
            for (String chunk : _loadedChunks)
            {
                if (deserializeChunk(chunk).getWorld().getName().equalsIgnoreCase(world))
                {
                    _loadedChunks.remove(chunk);
                }
            }

            // unloads and deletes the world
            File file = unloadWorld.getWorldFolder();
            successful = Bukkit.getServer().unloadWorld(unloadWorld, false);
            FileUtil.deleteFile(file);
        }
        System.out.println("DELETED world - " + world);
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
                for (int z = mapconfig.getMinZ(); z <= mapconfig.getMaxZ() + 16; z += 16)
                {
                    Location location = new Location(mapconfig.getWorld(), x, 0, z);
                    location.getChunk().load();
                    _loadedChunks.add(serializeChunk(location.getChunk()));
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

    public boolean setMapWeather(String map, WeatherType weatherType)
    {
        if (_loadedWorlds.contains(map))
        {
            if (Bukkit.getWorld(map) != null)
            {
                Bukkit.getWorld(map).setThundering(weatherType == WeatherType.DOWNFALL);
                _weather.put(map, weatherType);
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
        return _mapConfigs.get(map);
    }

    /**
     * Returns a list containing all of the maps that are currently loaded.
     *
     * @return a list containing all of the loaded maps
     */
    public List<String> getLoadedMaps()
    {
        return new ArrayList<>(_loadedWorlds);
    }

    /**
     * Teleports the player to the given map
     *
     * @param player the player being teleported
     * @param mapConfig the MapConfig of the world the player is being teleported to
     * @return true if the player was successfully teleported, otherwise false
     */
    public boolean teleportPlayerToMap(Player player, String team, MapConfig mapConfig)
    {
        List<Location> locations = mapConfig.getTeamSpawnLocations(team);

        // Teleports the player
        if (locations.size() > 0)
        {
            player.teleport(locations.get(new Random().nextInt(locations.size())));
            return true;
        }
        return false;
    }

    public boolean teleportPlayerToActiveMap(Player player, String team)
    {
        List<Location> locations = _activeMap.getTeamSpawnLocations(team);

        // Teleports the player
        if (locations.size() > 0)
        {
            player.teleport(locations.get(new Random().nextInt(locations.size())));
            return true;
        }
        return false;
    }

    /**
     * Teleports all of the players to the given map so every player spawns at a different spawn point. If there
     * isn't enough spawn points for every player there will be some spawn points with multiple players.
     *
     * @param mapConfig the MapConfig of the world the player is being teleported to
     */
    public boolean teleportAllPlayersToMap(List<Player> players, String team, MapConfig mapConfig)
    {
        List<Location> locations = mapConfig.getTeamSpawnLocations(team);

        if (locations.size() == 0)
        {
            return false;
        }

        // Teleports all of the players to different spawn locations
        for (int i = 0; i < players.size(); i++)
        {
            players.get(i).teleport(locations.get(i % locations.size()));
        }

        return true;
    }

    /**
     * Teleports all of the players to the given map so every player spawns at a different spawn point. If there
     * isn't enough spawn points for every player there will be some spawn points with multiple players.
     */
    public boolean teleportAllPlayersToActiveMap(List<Player> players, String team)
    {
        List<Location> locations = _activeMap.getTeamSpawnLocations(team);

        if (locations.size() == 0)
        {
            return false;
        }

        // Teleports all of the players to different spawn locations
        for (int i = 0; i < players.size(); i++)
        {
            players.get(i).teleport(locations.get(i % locations.size()));
        }

        return true;
    }

    /**
     * Gets the public name of the map with the specified world name. If no world with the given name exists
     * an empty string will be returned.
     *
     * @param world the name of the world directory
     * @return the public name of the map if the world exists, otherwise an empty string is returned
     */
    public String getMapName(String world)
    {
        if (_mapConfigs.containsKey(world))
        {
            return _mapConfigs.get(world).getName();
        }
        return "";
    }

    /**
     * This gets all of the world directory names that fall under the specified type of map. This includes
     * every map whether or not it is currently loaded.
     *
     * @param mapType the type of maps you are retrieving
     * @return all of the world directory names of the specified type
     */
    public List<String> getMapsOfType(MapType mapType)
    {
        ArrayList<String> maps = new ArrayList<>();

        if (mapType == MapType.ALL) // Retrieves all of the maps
        {
            for (File file : FileUtil.getFileContents(_mapContainer))
            {
                maps.add(file.getName());
            }
        }
        else // Gets all of the maps of the given type
        {
            File mapTypeContainer = new File(_mapContainer.getPath() + File.separator + mapType.getDirectoryName());
            if (mapTypeContainer.exists())
            {
                for (File file : FileUtil.getFileContents(mapTypeContainer))
                {
                    maps.add(file.getName());
                }
            }
        }
        return maps;
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
            Bukkit.broadcastMessage("exists1");
            return FileUtil.searchForFile(_mapContainer, worldFileName);
        }
        return null;
    }

    /**
     * Serializes the chunk to be stored as a string.
     *
     * @param chunk the chunk being serialized
     * @return the serialized chunk
     */
    private String serializeChunk(Chunk chunk)
    {
        return chunk.getWorld() + "," + chunk.getX() + "," + chunk.getZ();
    }

    /**
     * Deserializes the chunk. If an invalid serialized chunk is given null will be returned.
     *
     * @param serializedChunk the serialized chunk
     * @return the deserialized Chunk object or null if an invalid serialized chunk was entered
     */
    private Chunk deserializeChunk(String serializedChunk)
    {
        // Format = world,x,y
        String[] chunkArgs = serializedChunk.split(",");
        Chunk chunk = new Location(Bukkit.getWorld(chunkArgs[0]), Integer.parseInt(chunkArgs[1]), 0, Integer.parseInt(chunkArgs[2])).getChunk();
        return chunk;
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

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event)
    {
        Location pLoc = event.getPlayer().getLocation();
        MapConfig map = _mapConfigs.get(pLoc.getWorld().getName());
        if (pLoc.getX() > map.getMaxX() || pLoc.getX() < map.getMinX()
                || pLoc.getY() > map.getMaxY() || pLoc.getY() < map.getMinY()
                || pLoc.getZ() > map.getMaxZ() || pLoc.getZ() < map.getMinZ())
        {
            PlayerUtil.killPlayer(event.getPlayer(), "Border");
        }
    }
}
