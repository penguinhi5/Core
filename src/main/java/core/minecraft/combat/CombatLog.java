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

    // The player whose combat history is being tracked
    private CombatInstance _player;
    // The last instance that the player took damage
    private CombatInstance _lastDamager;
    // All recent instances that the player took damage
    private LinkedList<CombatInstance> _damagerList = new LinkedList<>();
    // The color of the player's name that killed this player
    private String _killerColor = ChatColor.GREEN.toString();
    // The color of the victim's name
    private String _victimColor = ChatColor.GREEN.toString();
    // That last time in milliseconds that this player took damage
    private long _lastDamageTime = 0L;
    // The last amount of damage that the player took
    private double _lastDamage = 0D;
    // The amount of time that must pass before a damage instance is removed from the log
    private long _damageTimeout = 15000L;

    /**
     * Creates a new CombatLog instance.
     *
     * @param playerName the name of the player whose combat history is being logged
     * @param player the player whose combat history is being logged
     */
    public CombatLog(String playerName, LivingEntity player)
    {
        _player = new CombatInstance(playerName, player);
    }

    /**
     * Adds a new log when the player was attacked.
     *
     * <p>
     *     If the reason parameter is equal to null, an empty string, or "-" the reason will be excluded from the death message.
     * </p>
     *
     * @param damagerName the name of the damager
     * @param damager the living entity applying the damage
     * @param reason the reason for the damage that will be displayed as the weapon in the death message
     * @param damage the amount of damage applied
     */
    public void addAttack(String damagerName, LivingEntity damager, String reason, double damage)
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

        combatInstance.addDamage(reason, damage);

        // Adds the damager to the beginning of the damager list
        _damagerList.addFirst(combatInstance);
        _lastDamager = combatInstance;
        _lastDamageTime = System.currentTimeMillis();
    }

    /**
     * Retrieves the simple death message with color codes.
     *
     * <p>
     *     (ex. "PenguinHi5 was killed by CaptainSparklez with Diamond Sword.")
     * </p>
     *
     * @return the simple death message
     */
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

            if (getKiller().getDamage().getFirst().getReason() == null)
            {
                content.append(F.C_CONTENT + ".");
            }
            else
            {
                content.append(F.C_CONTENT + " with " + F.C_EMPHASIS1 + getKiller().getDamage().getFirst().getReason() + F.C_CONTENT + ".");
            }
        }
        else
        {
            content.append(_killerColor + _lastDamager.getName());

            String reason = _lastDamager.getDamage().getFirst().getReason();
            if (reason != null && !reason.equals("") && !reason.equals("-"))
            {
                content.append(F.C_CONTENT + " with " + F.C_EMPHASIS1 + reason);
            }

            content.append(F.C_CONTENT + ".");
        }

        return F.componentMessage("Death", content.toString());
    }

    /**
     * Retrieves a list containing all of the death details intended to be sent to the victim. Every String
     * in the list describes the details of one instance where the player took damage within the last 15 seconds.
     *
     * <p>
     *     The list is ordered in reverse chronological order
     * </p>
     *
     * <p>
     *     (ex. "  1> PenguinHi5 [Wooden Sword] - 12.3 dmg - 1.5 seconds ago")
     * </p>
     *
     * @return the list of death details
     */
    public LinkedList<String> getDeathDetails()
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
            String reason = top.getDamage().getFirst().getReason();

            String message = "  " + F.C_PREFIX + i + "> " + F.C_PLAYER_EMPHASIS + top.getName() + " " +
                    F.C_CONTENT + "[" + F.C_SKILL + reason + F.C_CONTENT + "] - " +
                    ChatColor.GREEN + dmg + F.C_CONTENT + " - " + ChatColor.GREEN + time;
            details.addFirst(message);

            damageHistory.remove(top);

            i--;
        }

        return details;
    }

    /**
     * Removes all old instances where the player took damage.
     */
    public void cleanDamagerList()
    {
        // Removes old damage from the damager list
        for (CombatInstance combatInstance : _damagerList)
        {
            if (System.currentTimeMillis() > combatInstance.getLastDamageTime() + _damageTimeout)
            {
                _damagerList.remove(combatInstance);
                if (_lastDamager.equals(combatInstance))
                {
                    _lastDamager = null;
                }
            }
        }
    }

    /**
     * Counts how many players assisted in killing the player.
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

    /**
     * Gets the player who owns this log.
     *
     * @return the player who owns this log
     */
    public CombatInstance getPlayer()
    {
        return _player;
    }

    /**
     * Gets the last player that attacked this player.
     *
     * @return the last player that attacked this player
     */
    public CombatInstance getLastDamager()
    {
        return _lastDamager;
    }

    /**
     * Sets the last player that attacked this player
     *
     * @param lastDamager the last player that attacked this player
     */
    public void setLastDamager(CombatInstance lastDamager)
    {
        _lastDamager = lastDamager;
    }

    /**
     * Gets a reverse chronological list containing everything that damaged the player.
     *
     * @return a list containing all the damage the player recently took
     */
    public LinkedList<CombatInstance> getDamagerList()
    {
        cleanDamagerList();
        return _damagerList;
    }

    /**
     * Gets the color of the player's name that killed this player.
     *
     * @return the color of the killer's name
     */
    public String getKillerColor()
    {
        return _killerColor;
    }

    /**
     * Sets the color of the player's name that killed this player.
     *
     * @param killerColor the color of the killer's name
     */
    public void setKillerColor(String killerColor)
    {
        _killerColor = killerColor;
    }

    /**
     * Gets the color of the victim's name.
     *
     * @return the color of the victim's name
     */
    public String getVictimColor()
    {
        return _victimColor;
    }

    /**
     * Sets the color of the victim's name.
     *
     * @param victimColor the color fo the victim's name
     */
    public void setVictimColor(String victimColor)
    {
        _victimColor = victimColor;
    }

    /**
     * Gets the time in milliseconds that the player last took damage.
     *
     * @return the time in milliseconds that the player last took damage
     */
    public long getLastDamageTime()
    {
        return _lastDamageTime;
    }

    /**
     * Sets the time in milliseconds that the player last took damage.
     *
     * @param lastDamageTime the time in milliseconds that the player last took damage
     */
    public void setLastDamageTime(long lastDamageTime)
    {
        _lastDamageTime = lastDamageTime;
    }

    /**
     * Gets the last amount of damage that the player took.
     *
     * @return the new last amount of damage the player took
     */
    public double getLastDamage()
    {
        return _lastDamage;
    }

    /**
     * Sets the last amount of damage that the player took.
     *
     * @param lastDamage the new last amount of damage the player took
     */
    public void setLastDamage(double lastDamage)
    {
        _lastDamage = lastDamage;
    }

    /**
     * Gets the amount of time that must pass before damage is removed from the player's log.
     *
     * @return how much time must pass before a damage instance is removed from the player's log
     */
    public long getDamageTimeout()
    {
        return _damageTimeout;
    }

    /**
     * Updates the amount of time that must pass before damage is removed from the player's log.
     *
     * @param damageCooldown the new amount of time that must pass before a damage instance is removed from
     *                       the player's log
     */
    public void setDamageTimeout(long damageCooldown)
    {
        _damageTimeout = damageCooldown;
    }
}
