package core.redis.connection;

import redis.clients.jedis.JedisPoolConfig;

/**
 * Stores the settings for a {@link redis.clients.jedis.JedisPool}.
 *
 * @author Preston Brown
 */
public class RedisServerData {

    private String _host;
    private int _port;
    private String _password;
    private boolean _master;
    private JedisPoolConfig _jedisPoolConfig;

    /**
     * Creates a new instance of {@link RedisServerData} using the given data.
     *
     * @param host the hostname of the Redis server
     * @param port the port number of the Redis server
     * @param password the password used to connect to the Redis server
     * @param master if this JedisPool is a master connection
     */
    public RedisServerData(String host, int port, String password, boolean master)
    {
        _host = host;
        _port = port;
        _password = password;
        _master = master;
        _jedisPoolConfig = new JedisPoolConfig();
        configureJedisPoolConfig();
    }

    /**
     * Sets the configuration settings for JedisPoolConfig.
     */
    private void configureJedisPoolConfig()
    {
        _jedisPoolConfig.setSoftMinEvictableIdleTimeMillis(120000L);
        _jedisPoolConfig.setMaxIdle(8);
        _jedisPoolConfig.setMaxTotal(10);
    }

    /**
     * Returns the hostname that this instance of Jedis should run on.
     *
     * @return the hostname of this JedisPool
     */
    public String getHost()
    {
        return _host;
    }

    /**
     * Returns the port that this instance of Jedis should run on.
     *
     * @return the port of this JedisPool
     */
    public int getPort()
    {
        return _port;
    }

    /**
     * Returns the password that this instance of Jedis should run on.
     *
     * @return the password of this JedisPool
     */
    public String getPassword()
    {
        return _password;
    }

    /**
     * Returns the {@link JedisPoolConfig} object with preconfigured settings.
     *
     * @return the {@link JedisPoolConfig} object
     */
    public JedisPoolConfig getJedisPoolConfig()
    {
        return _jedisPoolConfig;
    }

    /**
     * Returns true if this is the master connection, otherwise it is a slave connection and false is returned.
     *
     * @return true if this is the master connection, otherwise false.
     */
    public boolean isMaster()
    {
        return _master;
    }
}
