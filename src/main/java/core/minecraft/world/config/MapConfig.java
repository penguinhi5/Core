package core.minecraft.world.config;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Contains all of the MapConfig data for a specific world
 *
 * @author Preston Brown
 */
public class MapConfig {

    private String _worldDirectory;
    private World _world;
    private File _file;
    private YamlConfiguration _config;
    private HashMap<String, List<Location>> _spawnMap = new HashMap<>();
    private int _minX, _maxX, _minY, _maxY, _minZ, _maxZ;
    private String _name, _author;

    public MapConfig(String worldDirectory, World world)
    {
        _worldDirectory = worldDirectory;
        _world = world;
        readConfig();
    }

    public void readConfig()
    {
        _file = new File(_worldDirectory);
        if (_file.exists())
        {
            try
            {
                _config = new YamlConfiguration();
                _config.load(_file);

                // World borders
                _minX = _config.getInt("_minX");
                if (_minX == 0)
                {
                    _minX = -256;
                }
                _maxX = _config.getInt("_maxX");
                if (_maxX == 0)
                {
                    _maxX = 256;
                }
                _minY = _config.getInt("_minY");
                _maxY = _config.getInt("_maxY");
                if (_maxY == 0)
                {
                    _maxY = 256;
                }
                _minZ = _config.getInt("_minZ");
                if (_minZ == 0)
                {
                    _minZ = -256;
                }
                _maxZ = _config.getInt("_maxZ");
                if (_maxZ == 0)
                {
                    _maxZ = 256;
                }

                // Basic information
                _name = _config.getString("name");
                if (_name == null)
                {
                    _name = "that one map";
                    System.out.println("Failed to parse map name from mapconfig directory " + _worldDirectory);
                }
                _author = _config.getString("author");
                if (_author == null)
                {
                    _author = "that one guy";
                    System.out.println("Failed to parse map author from mapconfig directory " + _worldDirectory);
                }

                // Team spawn locations
                HashMap<String, Object> locMap = (HashMap<String, Object>) _config.getConfigurationSection("team_spawns").getValues(false);
                for (String key : locMap.keySet())
                {
                    ArrayList<Location> locationList = new ArrayList<>();
                    String[] locations = ((String)locMap.get(key)).split(";");
                    for (String location : locations)
                    {
                        Location loc = stringToLocation(location);
                        if (loc != null)
                        {
                            locationList.add(loc);
                        }
                        else
                        {
                            System.out.println("Failed to parse spawn location for team " + key + " from mapconfig directory " + _worldDirectory);
                        }
                    }
                    _spawnMap.put(key, locationList);
                }
            }
            catch (InvalidConfigurationException | IOException e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("Failed to read mapconfig at path " + _worldDirectory);
        }
    }

    /**
     * Returns a list containing all of the spawn locations for the given team.
     *
     * @param team the name of the team
     * @return a list containing all of the spawn locations
     */
    public List<Location> getTeamSpawnLocations(String team)
    {
        if (_spawnMap.containsKey(team))
        {
            return _spawnMap.get(team);
        }
        return new ArrayList<>();
    }

    /**
     * Converts a String to a Location. If the location cannot be converted into a String null is returned.
     *
     * @param locString the string that contains a location
     * @return the location
     */
    private Location stringToLocation(String locString)
    {
        String[] parts = locString.split(",");
        Location loc = null;
        if (parts.length == 5)
        {
            try
            {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                int z = Integer.parseInt(parts[2]);
                float yaw = Float.parseFloat(parts[3]);
                float pitch = Float.parseFloat(parts[4]);
                loc = new Location(_world, x + .5D, y + .5D, z + .5D, yaw, pitch);
            }
            catch (NumberFormatException | NullPointerException e)
            {
                e.printStackTrace();
            }
        }
        else if (parts.length == 3)
        {
            try
            {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                int z = Integer.parseInt(parts[2]);
                loc = new Location(_world, x + .5D, y + .5D, z + .5D);
            }
            catch (NumberFormatException | NullPointerException e)
            {
                e.printStackTrace();
            }
        }
        return loc;
    }

    /**
     * Gets the path to this world's directory
     *
     * @return the relative path to this world's directory
     */
    public String getWorldDirectory()
    {
        return _worldDirectory;
    }

    /**
     * Gets the World object for this map.
     *
     * @return this map's World instance
     */
    public World getWorld()
    {
        return _world;
    }

    /**
     * Gets a File instance of this world's directory.
     *
     * @return a File instance of the world's directory
     */
    public File getFile()
    {
        return _file;
    }

    /**
     * Gets the YamlConfiguration instance that stores all of this world's configuration settings.
     *
     * @return the YamlConfiguration instance
     */
    public YamlConfiguration getYamlConfig()
    {
        return _config;
    }

    /**
     * Gets the configuration setting from the specified path. If there is nothing found at this path null is returned.
     *
     * @param path the path to this configuration setting
     * @return the Object found in the YamlConfiguration at the specified path
     */
    public Object getConfigurationSetting(String path)
    {
        return _config.get(path);
    }

    /**
     * Gets a map that contains all of the spawn locations for every team.
     *
     * @return a HashMap containing the spawn locations for every team
     */
    public HashMap<String, List<Location>> getSpawnMap()
    {
        return _spawnMap;
    }

    /**
     * Gets a List containing all of the spawn locations for the specified team. If no team exists with the given
     * name, null is returned.
     *
     * @param team the team you are getting the spawn locations of
     * @return all of the spawn locations for the specified team if the team exists, otherwise null
     */
    public List<Location> getTeamSpawns(String team)
    {
        if (_spawnMap.containsKey(team.toLowerCase()))
        {
            return _spawnMap.get(team.toLowerCase());
        }
        return null;
    }

    /**
     * Gets the minimum x coordinate any player can reach.
     *
     * @return the minimum x coordinate
     */
    public int getMinX()
    {
        return _minX;
    }

    /**
     * Gets the maximum x coordinate any player can reach.
     *
     * @return the maximum x coordinate
     */
    public int getMaxX()
    {
        return _maxX;
    }

    /**
    * Gets the minimum y coordinate any player can reach.
    *
    * @return the minimum y coordinate
    */
    public int getMinY()
    {
        return _minY;
    }

    /**
     * Gets the maximum y coordinate any player can reach.
     *
     * @return the maximum y coordinate
     */
    public int getMaxY()
    {
        return _maxY;
    }

    /**
     * Gets the minimum z coordinate any player can reach.
     *
     * @return the minimum z coordinate
     */
    public int getMinZ()
    {
        return _minZ;
    }

    /**
     * Gets the maximum z coordinate any player can reach.
     *
     * @return the maximum z coordinate
     */
    public int getMaxZ()
    {
        return _maxZ;
    }

    /**
     * Gets the name of this map.
     *
     * @return the name of this map
     */
    public String getName()
    {
        return _name;
    }

    /**
     * Gets the author of this map.
     *
     * @return the author of this map
     */
    public String getAuthor()
    {
        return _author;
    }
}
