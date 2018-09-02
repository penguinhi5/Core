package core.redis.message;

import redis.clients.jedis.JedisPubSub;

/**
 * Listens for messages from other servers and publishes messages for other servers.
 *
 * @author Preston Brown
 */
public class RedisMessageListener extends JedisPubSub {

    public void onMessage(String channel, String message)
    {

    }

    public void onSubscribe(String channel, int subscribedChannels)
    {

    }

    public void onUnsubscribe(String channel, int subscribedChannels)
    {

    }

    public void onPSubscribe(String pattern, int subscribedChannels)
    {

    }

    public void onPUnsubscribe(String pattern, int subscribedChannels)
    {

    }

    public void onPMessage(String pattern, String channel, String message)
    {
        String[] channelCommand = channel.split(":");
        if (channelCommand[0].equals("commands.minecraft"))
        {
            RedisMessageManager.getInstance().handleCommand(channelCommand[1], message);
        }
    }
}
