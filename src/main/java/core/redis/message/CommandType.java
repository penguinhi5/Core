package core.redis.message;

import core.minecraft.common.Callback;
import core.redis.message.commands.RedisCommand;
import core.redis.message.commands.RedisCommandHandler;

/**
 * This is used to store a command type in order to distinguish all of the commands when they are sent.
 *
 * @author Preston Brown
 */
public class CommandType {

    private String _klazzName;
    private Class<? extends RedisCommand> _commandType;
    private RedisCommandHandler _commandHandler;

    /**
     * This creates a new class type using the given {@link RedisCommand} and command handler.
     *
     * @param commandType the {@link RedisCommand} that this command type represents
     * @param commandHandler the {@link RedisCommandHandler} that should be called when the command is executed
     */
    public CommandType(Class<? extends RedisCommand> commandType, RedisCommandHandler commandHandler)
    {
        _commandType = commandType;
        _klazzName = _commandType.getSimpleName();
        _commandHandler = commandHandler;
    }

    /**
     * This returns the simple class name of the given {@link RedisCommand}.
     *
     * @return the simple class name of the given {@link RedisCommand}
     */
    public String getClassName()
    {
        return _klazzName;
    }

    /**
     * This returns the given {@link RedisCommand}
     *
     * @return the given {@link RedisCommand}
     */
    public Class<? extends RedisCommand> getCommandType()
    {
        return _commandType;
    }

    /**
     * This returns the {@link RedisCommandHandler} that will run when this command type is executed.
     *
     * @return the {@link RedisCommandHandler} that is ran when this command type is executed
     */
    public RedisCommandHandler getCallback()
    {
        return _commandHandler;
    }
}
