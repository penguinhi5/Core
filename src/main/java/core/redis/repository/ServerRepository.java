package core.redis.repository;

import com.sun.corba.se.spi.activation.ServerManager;
import core.redis.RedisUtil;
import core.redis.connection.RedisManager;
import core.redis.data.BungeeProxy;
import core.redis.data.DedicatedServer;
import core.redis.data.MinecraftServer;
import core.redis.data.ServerType;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Manages the redis server repository that manages the server data for all of the different types of servers.
 *
 * @author Preston Brown
 */
public class ServerRepository implements IServerRepository{

    /**
     * This creates a new {@link ServerRepository} instance.
     */
    public ServerRepository()
    {

    }

    /**
     * Concatenates the array of strings into a single string.
     *
     * @param strings the array of strings that is being concatenated
     * @return the concatenated string
     */
    private String concatenate(String[] strings)
    {
        StringBuilder conjoined = new StringBuilder();
        conjoined.append("server");
        for (int i = 0; i < strings.length; i++)
        {
            conjoined.append("." + strings[i]);
        }
        return conjoined.toString();
    }

    @Override
    public Collection<MinecraftServer> getAllMinecraftServers()
    {
        Jedis jedis = null;
        Collection<MinecraftServer> serverList = new LinkedList<>();
        try
        {
            jedis = RedisManager.getSlaveConnection();
            Pipeline pipeline = jedis.pipelined();
            String key = concatenate(new String[] {"minecraft"});
            Response<Map<String, String>> response = pipeline.hgetAll(key);
            pipeline.sync();

            Map<String, String> serializedList = response.get();
            for (String value : serializedList.values())
            {
                serverList.add(deserializeData(value, MinecraftServer.class));
            }
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to execute getAllMinecraftServers()");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return serverList;
    }

    @Override
    public void addMinecraftServer(MinecraftServer server)
    {
        Jedis jedis = null;
        try
        {
            jedis = RedisManager.getMasterConnection();
            String key = concatenate(new String[] {"minecraft"});
            String serializedServer = serializeData(server);
            Transaction transaction = jedis.multi();
            transaction.hset(key, server.getNameID(), serializedServer);
            transaction.exec();
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to add MinecraftServer \'" + server.getServerName() + "\' at " + server.getPublicIP() + ":" + server.getPort());
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (JedisException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeMinecraftServer(String serverName)
    {
        Jedis jedis = null;
        try
        {
            jedis = RedisManager.getMasterConnection();
            String key = concatenate(new String[] {"minecraft"});
            Transaction transaction = jedis.multi();
            transaction.hdel(key, serverName);
            transaction.exec();
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to remove MinecraftServer \'" + serverName + "\'");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (JedisException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public MinecraftServer getMinecraftServer(String serverName)
    {
        Jedis jedis = null;
        MinecraftServer server = null;
        try
        {
            jedis = RedisManager.getSlaveConnection();
            String key = concatenate(new String[] {"minecraft"});
            Transaction transaction = jedis.multi();
            Response<String> response = transaction.hget(key, serverName);
            transaction.exec();

            server = deserializeData(response.get(), MinecraftServer.class);
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to retrieve MinecraftServer \'" + serverName + "\'");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (JedisException e)
            {
                e.printStackTrace();
            }
        }
        return server;
    }

    @Override
    public ServerType getServerType(String type)
    {
        Jedis jedis = null;
        ServerType serverType = null;
        try
        {
            jedis = RedisManager.getSlaveConnection();
            String key = concatenate(new String[] {"servertype"});
            Transaction transaction = jedis.multi();
            Response<String> response = transaction.hget(key, type);
            transaction.exec();

            serverType = deserializeData(response.get(), ServerType.class);
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to retrieve ServerType \'" + type + "\'");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return serverType;
    }

    @Override
    public Collection<ServerType> getAllServerTypes()
    {
        Jedis jedis = null;
        Collection<ServerType> serverTypes = new LinkedList<>();
        try
        {
            jedis = RedisManager.getSlaveConnection();
            String key = concatenate(new String[] {"servertype"});
            Pipeline pipeline = jedis.pipelined();
            Response<Map<String, String>> response = pipeline.hgetAll(key);
            pipeline.sync();
            Map<String, String> serializedTypes = response.get();

            for (String type : serializedTypes.values())
            {
                serverTypes.add(deserializeData(type, ServerType.class));
            }
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to execute getAllServerTypes()");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return serverTypes;
    }

    @Override
    public void addServerType(ServerType type)
    {
        Jedis jedis = null;
        try
        {
            jedis = RedisManager.getMasterConnection();
            String key = concatenate(new String[] {"servertype"});
            String serializedType = serializeData(type);
            Transaction transaction = jedis.multi();
            transaction.hset(key, type.getNameID(), serializedType);
            transaction.exec();
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to add ServerType \'" + type.getNameID() + "\'");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeServerType(String type)
    {
        Jedis jedis = null;
        try
        {
            jedis = RedisManager.getMasterConnection();
            String key = concatenate(new String[] {"servertype"});
            Transaction transaction = jedis.multi();
            transaction.hdel(key, type);
            transaction.exec();
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to remove ServerType \'" + type + "\'");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Collection<BungeeProxy> getAllBungeeProxies()
    {
        Jedis jedis = null;
        Collection<BungeeProxy> bungeeProxies = new LinkedList<>();
        try
        {
            jedis = RedisManager.getMasterConnection();
            String key = concatenate(new String[] {"bungee"});
            Pipeline pipeline = jedis.pipelined();
            Response<Map<String, String>> response = pipeline.hgetAll(key);
            pipeline.sync();
            Map<String, String> serializedProxies = response.get();

            for (String proxy : serializedProxies.values())
            {
                bungeeProxies.add(deserializeData(proxy, BungeeProxy.class));
            }
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to execute getAllBungeeProxies()");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return bungeeProxies;
    }

    @Override
    public BungeeProxy getBungeeProxy(String ID)
    {
        Jedis jedis = null;
        BungeeProxy bungee = null;
        try
        {
            jedis = RedisManager.getSlaveConnection();
            String key = concatenate(new String[] {"bungee"});
            Transaction transaction = jedis.multi();
            Response<String> response = transaction.hget(key, ID);
            transaction.exec();
            bungee = deserializeData(response.get(), BungeeProxy.class);
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to retrieve BungeeProxy \'" + ID + "\'");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return bungee;
    }

    @Override
    public void addBungeeProxy(BungeeProxy bungee)
    {
        Jedis jedis = null;
        try
        {
            jedis = RedisManager.getMasterConnection();
            String key = concatenate(new String[] {"bungee"});
            String serialized = serializeData(bungee);
            Transaction transaction = jedis.multi();
            transaction.hset(key, bungee.getNameID(), serialized);
            transaction.exec();
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to add BungeeProxy \'" + bungee.getNameID() + "\'");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeBungeeProxy(String ID)
    {
        Jedis jedis = null;
        try
        {
            jedis = RedisManager.getMasterConnection();
            String key = concatenate(new String[] {"bungee"});
            Transaction transaction = jedis.multi();
            transaction.hdel(key, ID);
            transaction.exec();
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to remove BungeeProxy \'" + ID + "\'");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Collection<DedicatedServer> getAllDedicatedServers()
    {
        Jedis jedis = null;
        Collection<DedicatedServer> dedicatedServers = new LinkedList<>();
        try
        {
            jedis = RedisManager.getSlaveConnection();
            String key = concatenate(new String[] {"dedicated"});
            Pipeline pipeline = jedis.pipelined();
            Response<Map<String, String>> response = pipeline.hgetAll(key);
            pipeline.sync();
            Map<String, String> serializedServers = response.get();

            for (String server : serializedServers.values())
            {
                dedicatedServers.add(deserializeData(server, DedicatedServer.class));
            }
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to execute getAllDedicatedServers()");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return dedicatedServers;
    }

    @Override
    public DedicatedServer getDedicatedServer(String publicIP)
    {
        Jedis jedis = null;
        DedicatedServer server = null;
        try
        {
            jedis = RedisManager.getSlaveConnection();
            String key = concatenate(new String[] {"dedicated"});
            Transaction transaction = jedis.multi();
            Response<String> response = transaction.hget(key, publicIP);
            transaction.exec();
            server = deserializeData(response.get(), DedicatedServer.class);
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to retrieve DedicatedServer \'" + publicIP + "\'");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return server;
    }

    @Override
    public void addDedicatedServer(DedicatedServer dedicatedServer)
    {
        Jedis jedis = null;
        try
        {
            jedis = RedisManager.getMasterConnection();
            String key = concatenate(new String[] {"dedicated"});
            String serialized = serializeData(dedicatedServer);
            Transaction transaction = jedis.multi();
            transaction.hset(key, dedicatedServer.getNameID(), serialized);
            transaction.exec();
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to add DedicatedServer \'" + dedicatedServer.getPublicIP() + "\'");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeDedicatedServer(String publicIP)
    {
        Jedis jedis = null;
        try
        {
            jedis = RedisManager.getMasterConnection();
            String key = concatenate(new String[] {"dedicated"});
            Transaction transaction = jedis.multi();
            transaction.hdel(key, publicIP);
            transaction.exec();
        }
        catch (Exception e)
        {
            System.out.println("[Redis-ServerRepository] FAILED to remove DedicatedServer \'" + publicIP + "\'");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                jedis.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Serializes the given data into a {@link String}.
     *
     * @param data the data getting serialized
     * @return the serialized data
     */
    protected <T> String serializeData(T data)
    {
        return RedisUtil.serialize(data);
    }

    /**
     * Deserializes the given {@link String} back into its original form.
     *
     * @param data the data being deserialized
     * @param klazz the {@link Class} reference
     * @return the deserialized data
     */
    protected <T> T deserializeData(String data, Class<T> klazz)
    {
        return RedisUtil.deserialize(data, klazz);
    }
}
