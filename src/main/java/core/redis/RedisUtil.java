package core.redis;

import com.google.gson.Gson;
import core.redis.connection.RedisServerData;
import redis.clients.jedis.JedisPool;

/**
 * This utility is used to handle basic Jedis functions.
 *
 * @author Preston Brown
 */
public class RedisUtil {

    /**
     * This constructs a {@link JedisPool} using the configurations found inside of the {@link RedisServerData} object.
     * If the given jedisServerData is null, null will be returned.
     *
     * @param jedisServerData the {@link RedisServerData} that we are using to construct our {@link JedisPool}
     * @return a new instance of {@link JedisPool}
     */
    public static JedisPool generateJedisPool(RedisServerData jedisServerData)
    {
        if (jedisServerData == null)
        {
            return null;
        }

        JedisPool jedisPool = new JedisPool(jedisServerData.getJedisPoolConfig(), jedisServerData.getHost(), jedisServerData.getPort(), 1000000, jedisServerData.getPassword());
        return jedisPool;
    }

    /**
     * Creates a {@link RedisServerData} using a line of data in the redis.dat file. If line does not
     * contain a proper serialized RedisServerData line null will be returned.
     *
     * @param line a line of data from the redis.dat file
     * @return a new instance of {@link RedisServerData} using the line of data provided, unless an invalid line
     * is entered, then null will be returned
     */
    public static RedisServerData deserializeJedisServerData(String line)
    {
        String[] args = line.split(" ");
        if (args.length != 4)
        {
            return null;
        }
        boolean master = (args[0].equalsIgnoreCase("MASTER") ? true : false);
        String host = args[1];
        int port;
        try
        {
            port = Integer.parseInt(args[2]);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
        String password = args[3];
        return new RedisServerData(host, port, password, master);
    }

    /**
     * Serializes the specified object into a Json object and returns the serialized string representation of the class.
     *
     * @param object the object that is being serialized
     * @return the string containing the serialized Json object
     */
    public static String serialize(Object object)
    {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    /**
     * Deserialize the given Json representation of the class and turns it back into the given klass.
     *
     * @param json the Json string that is being deserialized into the given class
     * @param klazz the class that the Json string is being turned back into
     * @param <T> the object that the Json string should be turned back into
     * @return the deserialized object
     */
    public static <T> T deserialize(String json, Class<T> klazz)
    {
        Gson gson = new Gson();
        return gson.fromJson(json, klazz);
    }
}
