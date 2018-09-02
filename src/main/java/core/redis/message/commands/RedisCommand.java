package core.redis.message.commands;

/**
 * This is the base for a redis command. Once a command has been received through redis it should be executed
 * in a {@link RedisCommandHandler}.
 *
 * @author Preston Brown
 */
public abstract class RedisCommand {

    private String[] _targetServers;
    private boolean _global;

    /**
     * This creates a new instance of {@link RedisCommand} that will be sent to all of the target servers. If
     * global is set to true it will be executed by every {@link core.redis.data.MinecraftServer} that receives
     * this command.
     *
     * @param targetServers all of the servers that should execute this command
     * @param global if this command should be executed by every {@link core.redis.data.MinecraftServer} that receives this command
     */
    public RedisCommand(String[] targetServers, boolean global)
    {
        _targetServers = targetServers;
        _global = global;
    }

    /**
     * This method will run once a command has been received through redis.
     */
    public abstract void run();

    /**
     * Returns all of the servers being targeted by this command.
     *
     * @return all of the servers that should execute this command
     */
    public String[] getTargetServers()
    {
        return _targetServers;
    }

    /**
     * Returns true if this command should be executed by every {@link core.redis.data.MinecraftServer} that receives this command, otherwise
     * return false.
     *
     * @return true if this command should be executed by every {@link core.redis.data.MinecraftServer} that receives this command,
     * otherwise false
     */
    public boolean isGlobal()
    {
        return _global;
    }

    /**
     * If the server with the given name is being targeted by this command return true, otherwise return false.
     *
     * @param serverName the server name being searched
     * @return true if this the server with the given name is targeted by this command
     */
    public boolean isTargetServer(String serverName)
    {
        if (_global)
        {
            return true;
        }
        for (String name : _targetServers)
        {
            if (name.equals(serverName))
            {
                return true;
            }
        }
        return false;
    }
}
