package core.minecraft;

import core.minecraft.command.CommandManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Set;

/**
 * This component is used to safely store player data while ensuring thread safety
 *
 * @author Preston Brown
 */
public abstract class ClientComponent<PlayerData> extends Component {

    protected HashMap<String, PlayerData> _playerData = new HashMap<>();
    protected Object _lock = new Object();

    /**
     * Creates a new instance of ClientComponent with the given name under the specified plugin
     *
     * @param name the name of the component
     * @param plugin the main JavaPlugin instance
     * @param commandManager the main CommandManager instance
     */
    public ClientComponent(String name, JavaPlugin plugin, CommandManager commandManager)
    {
        super(name, plugin, commandManager);
    }

    /**
     * Gets the playerdata of the player with the given name. If no player is currently online with that name
     * null is returned.
     *
     * @param playerName the name of the player that we are getting the PlayerData of
     * @return the PlayerData object the belongs to playerName, null if the player is not currently online
     */
    public PlayerData getPlayerData(String playerName)
    {
        PlayerData playerData;
        synchronized (_lock)
        {
            playerData = _playerData.get(playerName);
        }
        return playerData;
    }

    /**
     * Gets the playerdata of the given player. If the player is not currently online null is returned.
     *
     * @param player the player that we are getting the PlayerData of
     * @return the PlayerData object the belongs to player, null if the player is not currently online
     */
    public PlayerData getPlayerData(Player player)
    {
        PlayerData playerData;
        synchronized (_lock)
        {
            playerData = _playerData.get(player.getName());
        }
        return playerData;
    }

    /**
     * Sets the PlayerData of the player with the given name.
     *
     * @param playerName the name of the player that we are getting the PlayerData of
     * @param playerData the PlayerData object that should be stored
     */
    public void setPlayerData(String playerName, PlayerData playerData)
    {
        synchronized (_lock)
        {
            _playerData.put(playerName, playerData);
        }
    }

    /**
     * Removes the player with the given playerName from storage if it exists.
     *
     * @param playerName the name of the player whose player data is being removed
     */
    public void removePlayerData(String playerName)
    {
        synchronized (_lock)
        {
            _playerData.remove(playerName);
        }
    }

    /**
     * Returns a clone of the HashMap containing all of the player data.
     *
     * @return a clone of the playerData HashMap
     */
    public Set<String> getPlayerDataKeyset()
    {
        return _playerData.keySet();
    }
}
