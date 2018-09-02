package core.redis.repository;

import core.redis.data.BungeeProxy;
import core.redis.data.DedicatedServer;
import core.redis.data.MinecraftServer;
import core.redis.data.ServerType;

import java.util.Collection;
import java.util.List;

/**
 * This is the outline for the redis repository that stores server data for {@link core.redis.data.MinecraftServer}s,
 * {@link core.redis.data.DedicatedServer}s, {@link core.redis.data.BungeeProxy}s and {@link core.redis.data.ServerType}s.
 *
 * @author Preston Brown
 */
public interface IServerRepository {

    /**
     * Gets a {@link Collection} of every {@link MinecraftServer} instance found in redis.
     *
     * @return a {@link Collection} of every {@link MinecraftServer} instance found in redis
     */
    public Collection<MinecraftServer> getAllMinecraftServers();

    /**
     * Adds a {@link MinecraftServer} to the redis server repository. If one already exists with the same name
     * it will be replaced with the given {@link MinecraftServer}.
     *
     * @param minecraftServer the {@link MinecraftServer} that is being added to the redis server repository
     */
    public void addMinecraftServer(MinecraftServer minecraftServer);

    /**
     * Removes the {@link MinecraftServer} with the specified name.
     *
     * @param serverName the name of the {@link MinecraftServer} that is being removed
     */
    public void removeMinecraftServer(String serverName);

    /**
     * Gets the {@link MinecraftServer} instance with the given name if it exists.
     *
     * @param serverName the name of the {@link MinecraftServer} that is being retrieved
     * @return the {@link MinecraftServer} instance with the give name if it exists, otherwise null
     */
    public MinecraftServer getMinecraftServer(String serverName);

    /**
     * Gets the {@link ServerType} instance with the given prefix if it exists.
     *
     * @param type the prefix of the {@link ServerType} that is being retrieved
     * @return the {@link ServerType} instance with the given prefix if it exists, otherwise null
     */
    public ServerType getServerType(String type);

    /**
     * Gets a {@link Collection} of every {@link ServerType} instance found in the redis server repository.
     *
     * @return a {@link Collection} of every {@link ServerType} instance found in the redis server repository
     */
    public Collection<ServerType> getAllServerTypes();

    /**
     * Adds a {@link ServerType} to the redis server repository. If one already exists with the same prefix
     * it will be replaced with the given {@link ServerType}.
     *
     * @param type the {@link ServerType} that is being added to the redis server repository.
     */
    public void addServerType(ServerType type);

    /**
     * Removes the {@link ServerType} with the specified prefix if one exists.
     *
     * @param type the prefix of the {@link ServerType} that is being removed
     */
    public void removeServerType(String type);

    /**
     * Gets a {@link Collection} of every {@link BungeeProxy} instance found in the redis server repository.
     *
     * @return a {@link Collection} of every {@link BungeeProxy} instance found in the redis server repository
     */
    public Collection<BungeeProxy> getAllBungeeProxies();

    /**
     * Gets the {@link BungeeProxy} instance with the given public IP address if it exists.
     *
     * @param publicIP the public IP address of the {@link BungeeProxy} that is being retrieved
     * @return the {@link BungeeProxy} instance with the given public IP address if it exists, otherwise null
     */
    public BungeeProxy getBungeeProxy(String publicIP);

    /**
     * Adds the {@link BungeeProxy} to the redis server repository. If one already exists with the same public IP address
     * it will be replaced with the given {@link BungeeProxy}.
     *
     * @param bungee the {@link BungeeProxy} that is being added to the redis server repository
     */
    public void addBungeeProxy(BungeeProxy bungee);

    /**
     * Removes the {@link BungeeProxy} with the specified public IP address if one exists.
     *
     * @param publicIP the public IP address of the {@link BungeeProxy} that is being removed
     */
    public void removeBungeeProxy(String publicIP);

    /**
     * Gets a {@link Collection} of every {@link DedicatedServer} instance found in the redis server repository.
     *
     * @return a {@link Collection} of every {@link DedicatedServer} instance found in the redis server repository
     */
    public Collection<DedicatedServer> getAllDedicatedServers();

    /**
     * Gets the {@link DedicatedServer} instance with the given public IP address if it exists.
     *
     * @param publicIP the public IP address of the {@link DedicatedServer} that is being retrieved
     * @return the {@link DedicatedServer} instance with the given public IP address if it exists, otherwise null
     */
    public DedicatedServer getDedicatedServer(String publicIP);

    /**
     * Adds the {@link DedicatedServer} to the redis server repository. If one already exists with the same public IP address
     * it will be replaced with the given {@link DedicatedServer}.
     *
     * @param dedicatedServer the {@link DedicatedServer} that is being added to the redis server repository
     */
    public void addDedicatedServer(DedicatedServer dedicatedServer);

    /**
     * Removes the {@link DedicatedServer} with the specified public IP address if one exists.
     *
     * @param publicIP the public IP address of the {@link DedicatedServer} that is being removed
     */
    public void removeDedicatedServer(String publicIP);
}
