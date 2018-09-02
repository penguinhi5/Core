package core.redis.connection;

import core.redis.RedisUtil;
import core.redis.repository.ServerRepository;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;

/**
 * This manages all of the {@link JedisPool} instances and should be used to retrieve {@link Jedis} connections.
 *
 * @author Preston Brown
 */
public class RedisManager {

    private static final JedisPool MASTER_POOL = RedisUtil.generateJedisPool(RedisConnectionPool.getInstance().getMasterConnection());
    private static final JedisPool SLAVE_POOL = RedisUtil.generateJedisPool(RedisConnectionPool.getInstance().getSlaveConnection());
    private ServerRepository _serverRepository;
    private static RedisManager _instance;

    private RedisManager()
    {
        _serverRepository = new ServerRepository();
    }

    /**
     * Returns the existing instance of {@link RedisManager}.
     *
     * @return the existing instance of {@link RedisManager}
     */
    public static RedisManager getInstance()
    {
        if (_instance == null)
        {
            _instance = new RedisManager();
        }
        return _instance;
    }

    /**
     * This returns a {@link Jedis} object from the master connection.
     *
     * @return a {@link Jedis} object from the master connection
     */
    public static Jedis getMasterConnection()
    {
        return MASTER_POOL.getResource();
    }

    /**
     * This returns a {@link Jedis} object from the slave pool. If the slave pool is null a {@link Jedis} object is
     * returned from the master connection.
     *
     * @return a {@link Jedis} object from the slave connection
     */
    public static Jedis getSlaveConnection()
    {
        if (SLAVE_POOL == null)
        {
            return getMasterConnection();
        }

        Jedis jedis = SLAVE_POOL.getResource();
        if (RedisConnectionPool.getInstance().getMasterConnection() != null)
        {
            jedis.slaveof(RedisConnectionPool.getInstance().getMasterConnection().getHost(), RedisConnectionPool.getInstance().getMasterConnection().getPort());
        }
        return jedis;
    }

    /**
     * This will either return a {@link Jedis} object, and depending on whether writable is true or false,
     * a master or slave connection will be returned.
     *
     * @param writable if you require write access
     * @return a {@link Jedis} object with the specified write permissions
     */
    public static Jedis getConnection(boolean writable)
    {
        return (writable ? getMasterConnection() : getSlaveConnection());
    }

    /**
     * This will return the active {@link ServerRepository} instance that manages all of the redis server repository data.
     *
     * @return the active {@link ServerRepository} instance
     */
    public ServerRepository getServerRepository()
    {
        return _serverRepository;
    }
}
