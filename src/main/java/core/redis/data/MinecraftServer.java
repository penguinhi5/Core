package core.redis.data;

/**
 * This represents a Minecraft server.
 *
 * @author Preston Brown
 */
public class MinecraftServer implements RedisData {

    private String _publicIP;
    private int _port;
    private String _serverName;
    private String _serverType;
    private int _playerCount;
    private int _playerLimit;
    private String _motd;
    private long _maxRam;
    private long _freeRam;
    private String _version;

    /**
     * This creates a new instance of a {@link MinecraftServer} with the given data.
     *
     * @param publicIP the public IP address this Minecraft server is hosted on
     * @param port the port this Minecraft server is hosted on
     * @param serverName this Minecraft servers name
     * @param serverType the name of this Minecraft servers {@link ServerType}
     * @param playerCount the current player count on this server
     * @param playerLimit the maximim amount of players allowed on this server
     * @param motd this Minecraft servers MOTD
     * @param maxRam the amount of ram on this Minecraft server in megabytes
     * @param freeRam the amount of free ram on this Minecraft server in megabytes
     * @param version the version of Minecraft this server is running on
     */
    public MinecraftServer(String publicIP, int port, String serverName, String serverType, int playerCount, int playerLimit, String motd, long maxRam, long freeRam, String version) {
        _publicIP = publicIP;
        _port = port;
        _serverName = serverName;
        _serverType = serverType;
        _playerCount = playerCount;
        _playerLimit = playerLimit;
        _motd = motd;
        _maxRam = maxRam;
        _freeRam = freeRam;
        _version = version;
    }

    public String getPublicIP() {
        return _publicIP;
    }

    public int getPort() {
        return _port;
    }

    public String getServerName() {
        return _serverName;
    }

    public String getServerType() {
        return _serverType;
    }

    public int getPlayerCount() {
        return _playerCount;
    }

    public int getPlayerLimit() {
        return _playerLimit;
    }

    public String getMotd() {
        return _motd;
    }

    public long getMaxRam() {
        return _maxRam;
    }

    public long getFreeRam()
    {
        return _freeRam;
    }

    public String getVersion() {
        return _version;
    }

    @Override
    public String getNameID()
    {
        return _serverName;
    }
}
