package core.redis.data;

import java.util.HashMap;
import java.util.List;

/**
 * This represents a group of servers with matching prefixes.
 *
 * @author Preston Brown
 */
public class ServerType implements RedisData {

    private String _name;
    private int _maxPlayerLimit;
    private int _minPlayerLimit;
    private int _ram;
    private List<MinecraftServer> _minecraftServers;

    /**
     * This creates a new {@link ServerType} instance with the provided values. The values are constantly changing
     * so they are provided in a {@link HashMap}.
     *
     * @param values all of the data in the key-value pair.
     * @param minecraftServers all of the {@link MinecraftServer}s that fall under this {@link ServerType}
     */
    public ServerType(HashMap<String, Object> values, List<MinecraftServer> minecraftServers)
    {
        _name = (String)(values.get("name"));
        _maxPlayerLimit = (int)(values.get("maxPlayerLimit"));
        _minPlayerLimit = (int)(values.get("minPlayerLimit"));
        _ram = (int)(values.get("ram"));
        _minecraftServers = minecraftServers;
    }

    public String getName() {
        return _name;
    }
    public int getMaxPlayerLimit() {
        return _maxPlayerLimit;
    }
    public int getMinPlayerLimit() {
        return _minPlayerLimit;
    }
    public int getRam() {
        return _ram;
    }

    public List<MinecraftServer> getMinecraftServers() {
        return _minecraftServers;
    }

    public void addMinecraftServer(MinecraftServer minecraftServer)
    {
        _minecraftServers.add(minecraftServer);
    }

    public void addMinecraftServers(MinecraftServer[] minecraftServers)
    {
        for (MinecraftServer server : minecraftServers)
        {
            _minecraftServers.add(server);
        }
    }

    @Override
    public String getNameID()
    {
        return _name;
    }
}
