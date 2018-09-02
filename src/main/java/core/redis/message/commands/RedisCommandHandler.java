package core.redis.message.commands;

import core.minecraft.common.Callback;
import core.redis.message.commands.RedisCommand;

/**
 * This method should be called when a {@link RedisCommand} has been called on this server.
 *
 * @author Preston Brown
 */
public abstract class RedisCommandHandler<T extends RedisCommand> implements Callback<T> {

    public abstract T call(T callback);
}
