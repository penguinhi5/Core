package core.minecraft.client.data;

import core.minecraft.common.Rank;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * This is a representation of a client object where the data is pulled from our mysql database.
 *
 * @author Preston Brown
 */
public class Client {

    private int _clientID;
    private String _name;
    private Player _player;
    private String _uuid;
    private Rank _rank;
    private Rank _purchasedRank;
    private long _lastLogin;
    private long _totalPlayTime;

    /**
     * Creates a new Client instance from the given {@link Player} object.
     *
     * @param player this clients player object
     */
    public Client(Player player)
    {
        _player = player;
        _name = player.getName();
    }

    /**
     * Creates a new Client instance for the player with the given name object.
     *
     * @param name this clients player object
     */
    public Client(String name)
    {
        _name = name;
    }

    /**
     * Sets the clientID.
     *
     * @param clientID the clientID that is being set
     */
    public void setClientID(int clientID)
    {
        _clientID = clientID;
    }

    /**
     * Sets the uuid.
     *
     * @param uuid the uuid that is being set
     */
    public void setUUID(String uuid)
    {
        _uuid = uuid;
    }

    /**
     * Sets the rank.
     *
     * @param rank the rank that is being set
     */
    public void setRank(Rank rank)
    {
        _rank = rank;
    }

    /**
     * Sets the purchasedRank.
     *
     * @param rank the rank that is being set
     */
    public void setPurchasedRank(Rank rank)
    {
        _purchasedRank = rank;
    }

    /**
     * Sets the lastLogin.
     *
     * @param lastLogin the lastLogin that is being set
     */
    public void setLastLogin(long lastLogin)
    {
        _lastLogin = lastLogin;
    }

    /**
     * Sets the totalPlayTime.
     *
     * @param totalPlayTime the totalPlayTime that is being set
     */
    public void setTotalPlayTime(long totalPlayTime)
    {
        _totalPlayTime = totalPlayTime;
    }

    /**
     * Sets the player.
     *
     * @param player the player that is being set
     */
    public void setPlayer(Player player)
    {
        _player = player;
    }

    /**
     * @return this clients clientID
     */
    public int getClientID()
    {
        return _clientID;
    }

    /**
     * @return this clients player object
     */
    public Player getPlayer()
    {
        return _player;
    }

    /**
     * @return this clients name
     */
    public String getName()
    {
        return _name;
    }

    /**
     * @return this clients UUID
     */
    public String getUUID()
    {
        return _uuid;
    }

    /**
     * @return this clients rank
     */
    public Rank getRank()
    {
        return _rank;
    }

    /**
     * @return this clients purchased rank
     */
    public Rank getPurchasedRank()
    {
        return _purchasedRank;
    }

    /**
     * @return this clients name last login date in milliseconds
     */
    public long getLastLogin()
    {
        return _lastLogin;
    }

    /**
     * @return this clients total play time
     */
    public long getTotalPlayTime()
    {
        return _totalPlayTime;
    }
}
