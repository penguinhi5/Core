package core.redis.message.commands;

import core.minecraft.common.Callback;
import core.redis.message.commands.RedisCommand;

/**
 * This method should be called when a {@link RedisCommand} has been called on this server.
 *
 * @author Preston Brown
 */
public abstract class RedisCommandHandler<T extends RedisCommand> {

    public abstract void handleCommand(T callback);
}
