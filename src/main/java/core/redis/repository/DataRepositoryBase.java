package core.redis.repository;

import com.sun.corba.se.spi.activation.ServerManager;
import core.redis.RedisUtil;
import core.redis.connection.RedisManager;
import core.redis.data.RedisData;
import redis.clients.jedis.*;
import redis.clients.jedis.exceptions.JedisException;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This is the base used to create a Redis data repository that stores {@link core.redis.data.RedisData}.
 *
 * @author Preston Brown
 */
public abstract class DataRepositoryBase<T extends RedisData> implements DataRepository<T> {

    protected final int DEFAULT_TIMEOUT = 86400;
    protected String _dataName;
    private Class<T> _classType;

    public DataRepositoryBase(String dataName, Class<T> classType)
    {
        _dataName = dataName;
        _classType = classType;
    }

    /**
     * Creates the key used to store the data object with the specified nameID.
     *
     * @param nameID the name ID of the object
     * @return the key used to store the data object
     */
    protected String getKey(String nameID)
    {
        return "data." + _dataName + "." + nameID;
    }

    /**
     * @return the zkey used to store the data object
     */
    protected String getZKey()
    {
        return "data.z" + _dataName;
    }

    @Override
    public T getData(T data)
    {
        return getData(data.getNameID());
    }

    @Override
    public T getData(String nameID)
    {
        Jedis jedis = null;
        T data = null;
        String ID = nameID.toLowerCase();
        try
        {
            jedis = RedisManager.getSlaveConnection();
            Transaction transaction = jedis.multi();
            Response<String> response = transaction.get(ID);
            transaction.exec();

            String serialized = response.get();
            data = deserializeData(serialized);
        }
        catch (Exception e)
        {
            System.out.println("[Redis-DataRepositoryBase] \'" + _dataName + "\' FAILED to execute getData(String nameID)");
            e.printStackTrace();
            return data;
        }
        finally
        {
            try
            {
                if (jedis != null) {
                    jedis.close();
                }
            }
            catch (JedisException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("[Redis-DataRepositoryBase] \'" + _dataName + "\' executed getData(String nameID)");
        return data;
    }

    @Override
    public void addData(T data)
    {
        addData(data, DEFAULT_TIMEOUT);
    }

    @Override
    public void addData(T data, int timeout)
    {
        Jedis jedis = null;
        String ID = data.getNameID().toLowerCase();
        try
        {
            jedis = RedisManager.getMasterConnection();
            String serializedData = serializeData(data);
            Transaction transaction = jedis.multi();
            transaction.set(getKey(ID), serializedData);
            transaction.expire(getKey(ID), timeout);
            transaction.exec();
        }
        catch (Exception e)
        {
            System.out.println("[Redis-DataRepositoryBase] \'" + _dataName + "\' FAILED to execute addData(T data, long timeout)");
            e.printStackTrace();
            return;
        }
        finally
        {
            try
            {
                if (jedis != null) {
                    jedis.close();
                }
            }
            catch (JedisException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("[Redis-DataRepositoryBase] \'" + _dataName + "\' executed addData(T data, long timeout)");
    }

    @Override
    public void removeData(T data)
    {
        removeData(data.getNameID());
    }

    @Override
    public void removeData(String nameID)
    {
        Jedis jedis = null;
        String ID = nameID.toLowerCase();
        try
        {
            jedis = RedisManager.getMasterConnection();
            Transaction transaction = jedis.multi();
            transaction.del(getKey(ID));
            transaction.exec();
        }
        catch (Exception e)
        {
            System.out.println("[Redis-DataRepositoryBase] \'" + _dataName + "\' FAILED to execute removeData(String nameID)");
            e.printStackTrace();
            return;
        }
        finally
        {
            try
            {
                if (jedis != null) {
                    jedis.close();
                }
            }
            catch (JedisException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("[Redis-DataRepositoryBase] \'" + _dataName + "\' executed removeData(String nameID)");
    }

    @Override
    public boolean exists(T data)
    {
        return exists(data.getNameID());
    }

    @Override
    public boolean exists(String nameID)
    {
        Jedis jedis = null;
        boolean exists = false;
        String ID = nameID.toLowerCase();
        try
        {
            jedis = RedisManager.getSlaveConnection();
            Transaction transaction = jedis.multi();
            Response<Boolean> response = transaction.exists(getKey(ID));
            transaction.exec();
            exists = response.get();
        }
        catch (Exception e)
        {
            System.out.println("[Redis-DataRepositoryBase] \'" + _dataName + "\' FAILED to execute exists(T data)");
            e.printStackTrace();
            return exists;
        }
        finally
        {
            try
            {
                if (jedis != null) {
                    jedis.close();
                }
            }
            catch (JedisException e)
            {
                e.printStackTrace();
            }
        }
        System.out.println("[Redis-DataRepositoryBase] \'" + _dataName + "\' executed exists(String nameID)");
        return exists;
    }

    /**
     * Serializes the given data into a {@link String}.
     *
     * @param data the data getting serialized
     * @return the serialized data
     */
    protected String serializeData(T data)
    {
        return RedisUtil.serialize(data);
    }

    /**
     * Deserializes the given {@link String} back into its original form.
     *
     * @param data the data being deserialized
     * @return the deserialized data
     */
    protected T deserializeData(String data)
    {
        return RedisUtil.deserialize(data, _classType);
    }
}
