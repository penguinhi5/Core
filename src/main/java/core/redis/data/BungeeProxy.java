package core.redis.data;

/**
 * This represents a Bungee Proxy.
 *
 * @author Preston Brown
 */
public class BungeeProxy implements RedisData {

    private String _publicIP;
    private int _port;
    private long _maxRam;
    private long _freeRam;
    private int _playerCount;

    public BungeeProxy(String publicIP, int port, long maxRam, long freeRam, int playerCount)
    {
        _publicIP = publicIP;
        _port = port;
        _maxRam = maxRam;
        _freeRam = freeRam;
        _playerCount = playerCount;Runtime.getRuntime().availableProcessors();
    }

    public String getPublicIP() {
        return _publicIP;
    }

    public int getPort() {
        return _port;
    }

    public long getMaxRam() {
        return _maxRam;
    }

    public long getFreeRam()
    {
        return _freeRam;
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
