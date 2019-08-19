package core.minecraft.combat;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Keeps track of a player's combat history.
 */
public class CombatClient implements Listener {

    // All of the combat logs containing this client's deaths
    private LinkedList<CombatLog> _deaths = new LinkedList<>();
    // All of the combat logs containing this client's assists
    private LinkedList<CombatLog> _assists = new LinkedList<>();
    // All of the combat logs containing this client's kills
    private LinkedList<CombatLog> _kills = new LinkedList<>();

    // A list containing the last time an entity hurt the player
    private HashMap<LivingEntity, Long> _canBeHurtBy = new HashMap<>();
    // A list containing the last time this player hurt an entity
    private HashMap<LivingEntity, Long> _canHurt = new HashMap<>();
    // The last time that this player was hurt by the world
    private long _lastHurtByWorld = 0L;

    // How many kills this player has had since their last death
    private int _killsSinceDeath = 0;
    // How many assists this player has had since their last death
    private int _assistsSinceDeath = 0;

    // The total amount of kills this player has received
    private int _totalKills = 0;
    // The total amount of assists this player has received
    private int _totalAssists = 0;
    // The total amount of deaths this player has received
    private int _totalDeaths = 0;

    public CombatClient()
    {

    }

    /**
     * Returns whether or not this entity can be hurt by the damager. If they can be hurt by the damager the
     * last hit time will be updated and true will be returned.
     *
     * @param damager the damager hurting this entity
     * @return true if this entity can be hurt by the damager, otherwise false
     */
    public boolean canBeHurtBy(LivingEntity damager)
    {
        // If the damager is null the player was hurt by the world
        if (damager == null)
        {
            if (System.currentTimeMillis() >= _lastHurtByWorld + 400L)
            {
                _lastHurtByWorld = System.currentTimeMillis();
                return true;
            }
            return false;
        }

        // If the player can be hurt by the damager
        if (_canBeHurtBy.containsKey(damager))
        {
            if (System.currentTimeMillis() >= _canBeHurtBy.get(damager) + 500L)
            {
                _canBeHurtBy.put(damager, System.currentTimeMillis());
                return true;
            }
        }
        else
        {
            _canBeHurtBy.put(damager, System.currentTimeMillis());
            return true;
        }
        return false;
    }

    /**
     * Returns whether or not this entity can hurt the damagee. If they can hurt the damagee the last hit time
     * will be updated.
     *
     * @param damagee the entity being damaged
     * @return true if this entity can hurt the damagee, otherwise false
     */
    public boolean canHurt(LivingEntity damagee)
    {
        if (damagee == null)
        {
            return true;
        }

        if (_canHurt.containsKey(damagee))
        {
            if (System.currentTimeMillis() >= _canHurt.get(damagee) + 500L)
            {
                _canHurt.put(damagee, System.currentTimeMillis());
                return true;
            }
        }
        else
        {
            _canHurt.put(damagee, System.currentTimeMillis());
            return true;
        }
        return false;
    }

    /**
     * Logs when this player killed another player.
     *
     * @param log the death log for the player that was killed
     */
    public void logKill(CombatLog log)
    {
        _killsSinceDeath++;
        _totalKills++;
        _kills.add(log);
    }

    /**
     * Logs when this player assisted in a kill.
     *
     * @param log the death log for the player that was killed
     */
    public void logAssist(CombatLog log)
    {
        _assistsSinceDeath++;
        _totalAssists++;
        _assists.add(log);
    }

    /**
     * Logs this players death.
     *
     * @param log the log containing this player's death
     */
    public void logDeath(CombatLog log)
    {
        _killsSinceDeath = 0;
        _assistsSinceDeath = 0;
        _totalDeaths++;
        _deaths.add(log);
    }

    /**
     * Clears a player's combat history by removing all of their kills, assists and deaths.
     */
    public void clearCombatHistory()
    {
        _kills.clear();
        _assists.clear();
        _deaths.clear();
        _killsSinceDeath = 0;
        _totalKills = 0;
        _assistsSinceDeath = 0;
        _totalAssists = 0;
        _totalDeaths = 0;
    }

    /**
     * Gets the total number of kills this player has had since they last died.
     *
     * @return the number of kills this player has had since they last died
     */
    public int getKillsSinceDeath()
    {
        return _killsSinceDeath;
    }

    /**
     * Gets the total number of assists this player has had since they last died.
     *
     * @return the number of assists this player has had since they last died
     */
    public int getAssistsSinceDeath()
    {
        return _assistsSinceDeath;
    }

    /**
     * Gets the total number of kills this player has had.
     *
     * @return the total number of kills this player has had
     */
    public int getTotalKills()
    {
        return _totalKills;
    }

    /**
     * Gets the total number of assists this player has had.
     *
     * @return the total number of assists this player has had
     */
    public int getTotalAssists()
    {
        return _totalAssists;
    }

    /**
     * Gets the total number of deaths this player has had.
     *
     * @return the total number of time this player has died
     */
    public int getTotalDeaths()
    {
        return _totalDeaths;
    }

    /**
     * Gets all of the combat logs associated with this player's deaths.
     *
     * @return a list containing the combat logs associated with this player's deaths
     */
    public LinkedList<CombatLog> getDeaths()
    {
        return _deaths;
    }

    /**
     * Gets all of the combat logs associated with this player's assists.
     *
     * @return a list containing the combat logs associated with this player's assists
     */
    public LinkedList<CombatLog> getAssists()
    {
        return _assists;
    }

    /**
     * Gets all of the combat logs associated with this player's kill.
     *
     * @return a list containing the combat logs associated with this player's kills
     */
    public LinkedList<CombatLog> getKills()
    {
        return _kills;
    }

    /**
     * Gets the last time in milliseconds that this player was hurt by the world.
     *
     * @return the last time in milliseconds that this player was hurt by the world
     */
    public long getLastHurtByWorld()
    {
        return _lastHurtByWorld;
    }
}
