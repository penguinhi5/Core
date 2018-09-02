package core.minecraft.client.redis;

import core.redis.data.RedisData;

import java.util.UUID;

/**
 * This will be stored in the {@link ClientRedisRepository} in order to keep track of players
 * that are currently online throughout the entire network.
 *
 * @author PenguinHi5
 */
public class RedisClient implements RedisData {

    private String _name;
    private UUID _uuid;
    private String _server;

    /**
     * This creates a new {@link RedisClient} instance using the provided data.
     *
     * @param name the username of this client
     * @param uuid the uuid of this client
     * @param server the current server this client is on
     */
    public RedisClient(String name, UUID uuid, String server)
    {
        _name = name;
        _uuid = uuid;
        _server = server;
    }

    /**
     * @return this clients username
     */
    public String getName() {
        return _name;
    }

    /**
     * Sets this clients username to the specified name
     *
     * @param name the new username being assigned to this client
     */
    public void setName(String name) {
        _name = name;
    }

    /**
     * @return this clients uuid
     */
    public UUID getUUID() {
        return _uuid;
    }

    /**
     * @return the server that this client is currently on
     */
    public String getServer() {
        return _server;
    }

    /**
     * Updates the server that this client is currently on
     *
     * @param server the server that this client is currently on
     */
    public void setServer(String server) {
        _server = server;
    }

    @Override
    public String getNameID()
    {
        return _name;
    }
}
