package core.redis.data;

/**
 * This represents a Bungee Proxy.
 *
 * @author Preston Brown
 */
public class BungeeProxy implements RedisData {

    private String _publicIP;
    private int _port;
    private int _ram;
    private int _playerCount;

    public BungeeProxy(String publicIP, int port, int ram, int playerCount)
    {
        _publicIP = publicIP;
        _port = port;
        _ram = ram;
        _playerCount = playerCount;
    }

    public String getPublicIP() {
        return _publicIP;
    }

    public int getPort() {
        return _port;
    }

    public int getRam() {
        return _ram;
    }

    public int getPlayerCount() {
        return _playerCount;
    }

    @Override
    public String getNameID()
    {
        return _publicIP + "," + _port;
    }
}
