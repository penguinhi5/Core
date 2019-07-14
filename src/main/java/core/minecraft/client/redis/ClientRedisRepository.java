package core.minecraft.client.redis;

import core.redis.repository.DataRepositoryBase;
import org.bukkit.event.Listener;

/**
 * This redis repository stores the live client data of all the players currently on the network.
 *
 * @author Preston Brown
 */
public class ClientRedisRepository extends DataRepositoryBase<RedisClient> {

    /**
     * This creates a new {@link ClientRedisRepository} instance.
     */
    public ClientRedisRepository()
    {
        super("minecraftclient", RedisClient.class);
    }

    /**
     * Adds a player with the specified information to the redis client repository.
     *
     * @param client the client that is being added
     */
    public void playerJoin(RedisClient client)
    {
        addData(client);
    }

    /**
     * Removes a player with the specified username from the redis client repository.
     *
     * @param name the username of the player being removed from the redis client repository
     */
    public void playerLeave(String name)
    {
        removeData(name);
    }
}
