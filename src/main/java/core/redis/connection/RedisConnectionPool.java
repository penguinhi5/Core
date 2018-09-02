package core.redis.connection;

import core.redis.RedisUtil;
import redis.clients.jedis.JedisPool;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Stores all of the {@link RedisServerData} objects that are used to create {@link  JedisPool}s.
 *
 * @author Preston Brown
 */
public class RedisConnectionPool {

    private static RedisConnectionPool _instance;
    private RedisServerData _master;
    private RedisServerData _slave;

    /**
     * Gets the existing instance of {@link RedisConnectionPool}. If this instant has not
     * yet been created a new instance will be created.
     *
     * @return this instance of {@link RedisConnectionPool}
     */
    public static RedisConnectionPool getInstance()
    {
        if (_instance == null)
        {
            _instance = new RedisConnectionPool();
        }
        return _instance;
    }

    /**
     * Creates a new instance of {@link RedisConnectionPool}.
     */
    public RedisConnectionPool()
    {
        readConfig();
    }

    /**
     * Reads the redis.dat config and creates a {@link RedisServerData} object for every connection.
     */
    private void readConfig()
    {
        File file = new File("redis.dat");
        Scanner scanner = null;
        ArrayList<String> lines = new ArrayList<>();

        try
        {
            scanner = new Scanner(file);
            while (scanner.hasNextLine())
            {
                lines.add(scanner.nextLine());
            }

            for (String line : lines)
            {
                RedisServerData serverData = RedisUtil.deserializeJedisServerData(line);
                if (serverData != null)
                {
                    if (serverData.isMaster())
                    {
                        _master = serverData;
                    }
                    else
                    {
                        _slave = serverData;
                    }
                }
                else
                {
                    System.out.println("Failed to create RedisServerData from line \"" + line + "\" in redis.dat");
                }
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("redis.dat cannot be found at " + file.getAbsolutePath());
            e.printStackTrace();
        }
        finally
        {
            if (scanner != null)
            {
                scanner.close();
            }
        }
    }

    /**
     * Returns the {@link RedisServerData} object for the master connection.
     *
     * @return the {@link RedisServerData} object for the master connection
     */
    public RedisServerData getMasterConnection()
    {
        return _master;
    }

    /**
     * Returns a {@link RedisServerData} object for the slave connection. If there is no slave connection
     * the master connection is returned.
     *
     * @return a {@link RedisServerData} object for the slave connection
     */
    public RedisServerData getSlaveConnection()
    {
        return _slave;
    }
}
