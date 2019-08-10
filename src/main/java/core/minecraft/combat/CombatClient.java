package core.minecraft.combat;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Keeps track of a player's combat history.
 */
public class CombatClient {

    private LinkedList<CombatLog> _deaths = new LinkedList<>();
    private LinkedList<CombatLog> _assists = new LinkedList<>();
    private LinkedList<CombatLog> _kills = new LinkedList<>();

    private HashMap<LivingEntity, Long> _canBeHurtBy = new HashMap<>();
    private HashMap<LivingEntity, Long> _canHurt = new HashMap<>();
    private long _lastHurtByWorld = 0L;

    private int _killsSinceDeath = 0;
    private int _assistsSinceDeath = 0;

    private int _totalKills = 0;
    private int _totalAssists = 0;
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

    public int getKillsSinceDeath()
    {
        return _killsSinceDeath;
    }

    public int getAssistsSinceDeath()
    {
        return _assistsSinceDeath;
    }

    public int getTotalKills()
    {
        return _totalKills;
    }

    public int getTotalAssists()
    {
        return _totalAssists;
    }

    public int getTotalDeaths()
    {
        return _totalDeaths;
    }

    public LinkedList<CombatLog> getDeaths()
    {
        return _deaths;
    }

    public LinkedList<CombatLog> getAssists()
    {
        return _assists;
    }

    public LinkedList<CombatLog> getKills()
    {
        return _kills;
    }

    public long getLastHurtByWorld()
    {
        return _lastHurtByWorld;
    }
}
