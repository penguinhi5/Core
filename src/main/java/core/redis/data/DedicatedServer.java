package core.redis.data;

import core.redis.Location;

/**
 * This represents a dedicated server.
 *
 * @author Preston Brown
 */
public class DedicatedServer implements RedisData {

    private String _publicIP;
    private String _privateIP;
    private int _totalRam;
    private int _totalCPU;
    private int _availableRam;
    private int _availableCPU;
    private Location _location;

    public DedicatedServer(String publicIP, String privateIP, int totalRam, int totalCPU, int availableRam, int availableCPU, Location location) {
        _publicIP = publicIP;
        _privateIP = privateIP;
        _totalRam = totalRam;
        _totalCPU = totalCPU;
        _availableRam = availableRam;
        _availableCPU = availableCPU;
        _location = location;
    }

    public String getPublicIP() {
        return _publicIP;
    }

    public String getPrivateIP()
    {
        return _privateIP;
    }

    public int getTotalRam() {
        return _totalRam;
    }

    public int getTotalCPU() {
        return _totalCPU;
    }

    public int getAvailableRam() {
        return _availableRam;
    }

    public int getAvailableCPU() {
        return _availableCPU;
    }

    public Location getLocation() {
        return _location;
    }

    @Override
    public String getNameID()
    {
        return _publicIP;
    }
}
