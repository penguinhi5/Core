package core.minecraft.combat;

import core.minecraft.common.F;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

/**
 * Logs a player's combat history since the last time they've died.
 */
public class CombatLog {

    private CombatInstance _player;
    private CombatInstance _lastDamager;
    private LinkedList<CombatInstance> _damagerList = new LinkedList<>();
    private String _killerColor = ChatColor.GREEN.toString();
    private String _victimColor = ChatColor.GREEN.toString();
    private long _lastDamageTime = 0L;
    private double _lastDamage = 0D;
    private long _damageTimeout = 15000L;

    public CombatLog(String playerName, LivingEntity player)
    {
        _player = new CombatInstance(playerName, player);
    }

    public void addAttack(String damagerName, LivingEntity damager, String source, double damage)
    {
        // Cleans the damager list
        cleanDamagerList();

        // Searches for the CombatInstance if it already exists
        CombatInstance combatInstance = null;
        for (CombatInstance cI : _damagerList)
        {
            if (cI.getName().equals(damagerName))
            {
                combatInstance = cI;
                _damagerList.remove(cI);
            }
        }
        // Creates a new CombatInstance if
        if (combatInstance == null)
        {
            combatInstance = new CombatInstance(damagerName, damager);
        }

        combatInstance.addDamage(source, damage);

        // Adds the damager to the beginning of the damager list
        _damagerList.addFirst(combatInstance);
        _lastDamager = combatInstance;
        _lastDamageTime = System.currentTimeMillis();
    }

    public String getSimpleDeathMessage()
    {
        StringBuilder content = new StringBuilder();

        content.append(_victimColor + _player.getName() + F.C_CONTENT + " was killed by ");

        if (getKiller() != null)
        {
            content.append(_killerColor + getKiller().getName());
            int assistants = countAssistants();
            if (assistants > 0)
            {
                content.append(" + " + assistants);
            }

            content.append(F.C_CONTENT + " with " + F.C_EMPHASIS1 + getKiller().getDamage().getFirst().getSource() + F.C_CONTENT + ".");
        }
        else
        {
            content.append(_killerColor + _lastDamager.getName() + F.C_CONTENT + ".");
        }

        return F.componentMessage("Death", content.toString());
    }

    public List<String> getDeathDetails()
    {
        LinkedList<String> details = new LinkedList<>();
        LinkedList<CombatInstance> damageHistory = new LinkedList<>(_damagerList);

        // Orders details in reverse chronological order
        int i = damageHistory.size();
        while (i > 0)
        {
            // Searches for the oldest damage
            long topTime = 0L;
            CombatInstance top = null;
            for (CombatInstance combat : damageHistory)
            {
                long time = System.currentTimeMillis() - combat.getLastDamageTime();
                if (time > topTime || top == null)
                {
                    top = combat;
                    topTime = time;
                }
            }

            String time = new DecimalFormat("##.#").format(Double.valueOf(topTime) / 1000D) + " seconds ago";
            String dmg = new DecimalFormat("##.#").format(top.getTotalDamage()) + " dmg";
            String source = top.getDamage().getFirst().getSource();

            String message = "  " + F.C_PREFIX + i + "> " + F.C_PLAYER_EMPHASIS + top.getName() + " " +
                    F.C_CONTENT + "[" + F.C_SKILL + source + F.C_CONTENT + "] - " +
                    ChatColor.GREEN + dmg + F.C_CONTENT + " - " + ChatColor.GREEN + time;
            details.addFirst(message);

            damageHistory.remove(top);

            i--;
        }

        // Resets the combat log

        return details;
    }

    public void cleanDamagerList()
    {
        // Removes old damage from the damager list
        for (CombatInstance combatInstance : _damagerList)
        {
            if (System.currentTimeMillis() > combatInstance.getLastDamageTime() + _damageTimeout)
            {
                _damagerList.remove(combatInstance);
            }
        }
    }

    /**
     * Counts how many players assisted in killing the player
     *
     * @return the number of players that assisted in killing the player
     */
    public int countAssistants()
    {
        int assistants = 0;
        // Counts the number of players that recently damaged the player
        for (CombatInstance combatInstance : _damagerList)
        {
            if (combatInstance.isPlayer())
            {
                assistants++;
            }
        }
        // Removes the killer if they were killed by a player
        if (getKiller().isPlayer())
        {
            assistants--;
        }
        return assistants;
    }

    /**
     * Returns the last player that damaged the entity. If no player recently damaged the entity null is returned.
     *
     * @return the last playe rthat damaged the entity, if no player recently damaged the entity null is returned
     */
    public CombatInstance getKiller()
    {
        // Checks if the last damager is a player
        if (_lastDamager.isPlayer())
        {
            return _lastDamager;
        }

        // Searches for the last player that damaged this player
        for (CombatInstance combatInstance : _damagerList)
        {
            if (combatInstance.isPlayer())
            {
                return combatInstance;
            }
        }
        // No player recently damaged the player
        return null;
    }

    public CombatInstance getPlayer()
    {
        return _player;
    }

    public CombatInstance getLastDamager()
    {
        return _lastDamager;
    }

    public void setLastDamager(CombatInstance lastDamager)
    {
        _lastDamager = lastDamager;
    }

    public LinkedList<CombatInstance> getDamagerList()
    {
        return _damagerList;
    }

    public String getKillerColor()
    {
        return _killerColor;
    }

    public void setKillerColor(String killerColor)
    {
        _killerColor = killerColor;
    }

    public String getVictimColor()
    {
        return _victimColor;
    }

    public void setVictimColor(String victimColor)
    {
        _victimColor = victimColor;
    }

    public long getLastDamageTime()
    {
        return _lastDamageTime;
    }

    public void setLastDamageTime(long lastDamageTime)
    {
        _lastDamageTime = lastDamageTime;
    }

    public double getLastDamage()
    {
        return _lastDamage;
    }

    public void setLastDamage(double lastDamage)
    {
        _lastDamage = lastDamage;
    }

    public long getDamageCooldown()
    {
        return _damageTimeout;
    }

    public void setDamageCooldown(long damageCooldown)
    {
        _damageTimeout = damageCooldown;
    }
}
