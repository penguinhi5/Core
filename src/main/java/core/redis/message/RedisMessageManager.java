package core.redis.message;

import core.redis.RedisUtil;
import core.redis.connection.RedisManager;
import core.redis.message.commands.RedisCommand;
import core.redis.message.commands.RedisCommandHandler;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.HashMap;

/**
 * Manages messages sent through redis between servers.
 *
 * @author Preston Brown
 */
public class RedisMessageManager {

    private static RedisMessageManager _instance;
    private HashMap<String, CommandType> _commandTypes = new HashMap<>();
    private String _thisServerName;

    /**
     * Creates a new instance of {@link RedisMessageManager}.
     */
    private RedisMessageManager()
    {
        initialize();
    }

    /**
     * Sets the name of this {@link core.redis.data.MinecraftServer}.
     *
     * @param thisServerName the name of this {@link core.redis.data.MinecraftServer}
     */
    public void initializeServer(String thisServerName)
    {
        _thisServerName = thisServerName;
    }

    /**
     * Returns the active instance of {@link RedisMessageManager}.
     *
     * @return the active instanc eof {@link RedisMessageManager}
     */
    public static RedisMessageManager getInstance()
    {
        if (_instance == null)
        {
            _instance = new RedisMessageManager();
        }
        return _instance;
    }

    /**
     * Initializes the thread that listens for messages through Redis.
     */
    private void initialize()
    {
        Jedis jedis = RedisManager.getMasterConnection();
        Thread thread = new Thread() {
            @Override
            public void run()
            {
                try
                {
                    jedis.psubscribe(new RedisMessageListener(), "commands.minecraft:*");
                }
                catch (JedisException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    if (jedis != null)
                    {
                        jedis.close();
                    }
                }
            }
        };
        thread.start();
    }

    /**
     * This processes a command when it is received by the {@link RedisMessageListener}.
     *
     * @param commandType the {@link CommandType} that it should turn into
     * @param message the Json message sent through Redis
     */
    public void handleCommand(String commandType, String message)
    {
        CommandType type;
        if ((type = _commandTypes.get(commandType)) != null)
        {
            RedisCommand command = RedisUtil.deserialize(message, type.getCommandType());
            if (command.isTargetServer(_thisServerName))
            {
                command.run();
            }
        }
    }

    /**
     * Publishes a command that will be sent to every server that is listening to the specified command channel.
     *
     * @param command the command that is being sent
     */
    public void publishCommand(RedisCommand command)
    {
        Jedis jedis = null;
        try
        {
            jedis = RedisManager.getMasterConnection();
            String serializedCommand = RedisUtil.serialize(command);
            jedis.publish("commands.minecraft:" + command.getClass().getSimpleName(), serializedCommand);
            System.out.println("[Redis-PubSub] published command \'" + command.getClass().getSimpleName() + "\'");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (jedis != null)
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
    }

    /**
     * Adds a new {@link CommandType} for the Redis messenger to accept commands from.
     *
     * @param command the {@link RedisCommand} that is being registered
     * @param commandHandler the {@link RedisCommandHandler} that will run the {@link RedisCommand}
     */
    public void addCommandType(Class<? extends RedisCommand> command, RedisCommandHandler commandHandler)
    {
        CommandType commandType = new CommandType(command, commandHandler);
        _commandTypes.put(commandType.getClassName(), commandType);
        System.out.println("[Redis-PubSub] registered Redis Command \'" + commandType.getClassName() + "\'");
    }

    /**
     * Returns the {@link HashMap} that contains all of the {@link CommandType}s with their class names.
     *
     * @return the {@link HashMap} containing all of the {@link CommandType}s with their class names.
     */
    public HashMap<String, CommandType> getCommandTypes()
    {
        return _commandTypes;
    }
}
