package main;

import core.redis.data.MinecraftServer;
import core.redis.message.commands.RedisCommand;
import org.bukkit.Bukkit;

/**
 * Created by MOTPe on 7/28/2018.
 */
public class TestRedisCommand extends RedisCommand {

    public String _sentFrom;

    /**
     * This creates a new instance of {@link RedisCommand} that will be sent to all of the target servers. If
     * global is set to true it will be executed by every {@link MinecraftServer} that receives
     * this command.
     *
     * @param targetServers all of the servers that should execute this command
     * @param global        if this command should be executed by every {@link MinecraftServer} that receives this command
     */
    public TestRedisCommand(String[] targetServers, boolean global, String sentFrom) {
        super(targetServers, global);
        _sentFrom = sentFrom;
    }

    @Override
    public void run()
    {
        Bukkit.broadcastMessage("Ran test command!");
    }
}
