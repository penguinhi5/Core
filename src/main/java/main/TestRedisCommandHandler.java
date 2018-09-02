package main;

import core.redis.message.commands.RedisCommand;
import core.redis.message.commands.RedisCommandHandler;
import org.bukkit.Bukkit;

/**
 * Created by MOTPe on 7/28/2018.
 */
public class TestRedisCommandHandler<T extends RedisCommand> extends RedisCommandHandler<T> {

    @Override
    public T call(T callback) {
        Bukkit.broadcastMessage("RECEIVED REDIS MESSAGE FROM: " + ((TestRedisCommand)callback)._sentFrom);
        return callback;
    }
}
