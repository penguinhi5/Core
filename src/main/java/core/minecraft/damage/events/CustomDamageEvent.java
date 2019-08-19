package core.minecraft.damage.events;

import core.minecraft.damage.DamageChange;
import org.bukkit.Location;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Custom event used when damage is dealt to a living entity.
 */
public class CustomDamageEvent extends Event implements Cancellable {

    // The initial amount of damage applied to the damagee
    private double _initialDamage;
    // The cause of the damage
    private DamageCause _damageCause;

    // The knockback multipliers
    private HashMap<String, Double> _knockbackMult = new HashMap<>();
    // The damage multipliers
    private ArrayList<DamageChange> _damageMult = new ArrayList<>();
    // The damage modifications
    private ArrayList<DamageChange> _damageMod = new ArrayList<>();
    // If the damage should ignore armor absorption
    private boolean _ignoreArmor = false;
    // If the damage should ignore the hit/damage rate
    private boolean _ignoreRate = false;
    // If knockback should be applied
    private boolean _knockback = true;
    // The amount of fire ticks that will be applied to the entity due to the damage
    private int _fireTicks = 0;
    // The origion location used to calculate the knockback trajectory
    private Location _kbOrigin;

    // The player that dealt the damage
    private Player _playerDamager;
    // The living entity that dealt the damage
    private LivingEntity _entityDamager;

    // The player that is receiving the damage
    private Player _playerDamagee;
    // The living entity that is receiving the damage
    private LivingEntity _entityDamagee;

    // The projectile used to damage the player
    private Projectile _projectile;
    // The list containing all of the reasons the event was canceled
    private ArrayList<String> _canceledReasons = new ArrayList<>();

    private static final HandlerList _handlers = new HandlerList();

    /**
     * Creates a new CustomDamageEvent instance.
     *
     * <p>
     *     reason and source parameters should be set to null if you do not want to specify a weapon used to kill
     *     the damagee that will be displayed in the death message if the damager is a player.
     * </p>
     *
     * @param entityDamager The entity that damaged the damagee
     * @param entityDamagee the entity that took damage
     * @param projectile the projectile used to damage the damagee
     * @param damageCause the cause of the damage
     * @param initialDamage the initial damage applied to the entity
     * @param source the initial source of the damage
     * @param reason the initial reason for the damage, this will be displayed as the weapon used to kill
     *                      the damagee if the damager is a player and source is not null
     * @param knockbackOrigin the origin location used when calculation the knockback trajectory
     * @param ignoreArmor if the damage should ignore armor protection
     * @param ignoreRate if the damage should ignore the hit/damage rate
     * @param knockback if knockback should be applied
     */
    public CustomDamageEvent(LivingEntity entityDamager, LivingEntity entityDamagee, Projectile projectile,
                             DamageCause damageCause, double initialDamage, String source, String reason,
                             Location knockbackOrigin, boolean ignoreArmor, boolean ignoreRate, boolean knockback)
    {
        _entityDamager = entityDamager;
        if (_entityDamager instanceof Player)
        {
            _playerDamager = (Player)entityDamager;
        }

        _entityDamagee = entityDamagee;
        if (_entityDamagee instanceof Player)
        {
            _playerDamagee = (Player)entityDamagee;
        }

        if (source != null && reason != null)
        {
            _damageMod.add(new DamageChange(source, 0.0D, reason, true));
        }

        _projectile = projectile;

        _ignoreArmor = ignoreArmor;
        _ignoreRate = ignoreRate;
        _knockback = knockback;
        if (knockbackOrigin != null)
        {
            _kbOrigin = knockbackOrigin;
        }
        else if (_entityDamager != null)
        {
            _kbOrigin = _entityDamager.getLocation();
        }

        _damageCause = damageCause;
        _initialDamage = initialDamage;
    }

    /**
     * Adds a knockback multiplier to the final amount of knockback applied to the player.
     *
     * @param reason the reason for the multiplier being added
     * @param kb the knockback multiplier
     */
    public void addKnockback(String reason, double kb)
    {
        _knockbackMult.put(reason, kb);
    }

    /**
     * Adds a damage multiplier to the final amount of damage applied to the damagee.
     *
     * <p>
     *     Damage multipliers will be applied after the damage mods are added.
     * </p>
     *
     * @param source the source of the damage
     * @param reason the reason for the damage multiplier
     * @param multiplier the damage multiplier
     * @param useReason if the reason should be included with the weapon used to kill
     *                  the damagee if the damager is a player
     */
    public void addDamageMultiplier(String source, String reason, double multiplier, boolean useReason)
    {
        _damageMult.add(new DamageChange(source, multiplier, reason, useReason));
    }

    /**
     * Adds the damage to the final amount of damage applied to the damagee.
     *
     * <p>
     *     Damage mods are added before the damage multipliers are calculated.
     * </p>
     *
     * @param source the source of the damage
     * @param reason the reason for the damage
     * @param damage the damage modification
     * @param useReason if the reason should be included with the weapon used to kill
     *                  the damagee if the damager is a player
     */
    public void addDamage(String source, String reason, double damage, boolean useReason)
    {
        _damageMod.add(new DamageChange(source, damage, reason, useReason));
    }

    /**
     * Gets the initial amount of damage that was applied to the damagee before the damage alterations are applied.
     *
     * @return the initial amount of damage
     */
    public double getInitialDamage()
    {
        return _initialDamage;
    }

    /**
     * Sets the initial amount of damage before the damage modifications are applied.
     *
     * @param damage the new initial amount of damage
     */
    public void setInitialDamage(double damage)
    {
        _initialDamage = Math.max(damage, 0.0D);
    }

    /**
     * Gets all of the knockback multipliers.
     *
     * @return a hashmap containing the knockback multipliers and their reasons.
     */
    public HashMap<String, Double> getKnockbackMults()
    {
        return _knockbackMult;
    }

    /**
     * Gets an ArrayList containing all of the damage multipliers.
     *
     * @return all of the damage multipliers
     */
    public ArrayList<DamageChange> getDamageMults()
    {
        return _damageMult;
    }

    /**
     * Gets an ArrayList containing all of the damage modifications.
     *
     * @return all of the damage modifications
     */
    public ArrayList<DamageChange> getDamageMods()
    {
        return _damageMod;
    }

    /**
     * Gets the final amount of damage applied to the damagee after all the damage alterations are applied.
     *
     * @return the final amount of damage applied to the damagee
     */
    public double getDamage()
    {
        double finalDamage = _initialDamage;

        // Adds damage mods
        for (DamageChange damageMod : _damageMod)
        {
            finalDamage += damageMod.getMod();
        }

        // Calculates damage mults
        for (DamageChange damageMult : _damageMult)
        {
            // Ignores armor absorption if ignoreArmor is true
            if (isIgnoreArmor() && damageMult.getSource().equals("Armor Absorption"))
            {
                continue;
            }
            finalDamage *= damageMult.getMod();
        }

        return finalDamage;
    }

    /**
     * Gets the reason the damage was applied. This reason is also what will be included with the weapon in
     * the death message if the damager is a player.
     *
     * <p>
     *     If this is null no reason was specified initially or in any of the damage mods or mults.
     * </p>
     *
     * @return the reason the damage was applied if one was specified, if no reason was specified null is returned
     */
    public String getReason()
    {
        String reason = "";

        for (DamageChange damageChange : _damageMod)
        {
            if (damageChange.useReason())
            {
                reason = reason + damageChange.getReason() + ", ";
            }
        }
        for (DamageChange damageChange : _damageMult)
        {
            if (damageChange.useReason())
            {
                reason = reason + damageChange.getReason() + ", ";
            }
        }

        if (reason.length() > 0)
        {
            return reason.substring(0, reason.length() - 2);
        }
        return null;
    }

    /**
     * @return the cause of the damage
     */
    public DamageCause getDamageCause()
    {
        return _damageCause;
    }

    /**
     * Gets whether or not the damage should ignore armor absorption.
     *
     * @return true if the damage should ignore armor absorption, otherwise false
     */
    public boolean isIgnoreArmor()
    {
        return _ignoreArmor;
    }

    /**
     * Gets whether or not knockback should be applied to the damagee.
     *
     * @return true if knockback should be applied, otherwise false
     */
    public boolean isKnockback()
    {
        return _knockback;
    }

    /**
     * Sets whether or not knockback should be applied to the damagee.
     *
     * @param knockback if knockback should be applied ot the damagee
     */
    public void setKnockback(boolean knockback)
    {
        _knockback = knockback;
    }

    /**
     * Sets the origin location used to calculate the trajectory of the knockback.
     *
     * @param origin the origin location of the cause of the damage
     */
    public void setKnockbackOrigin(Location origin)
    {
        _kbOrigin = origin;
    }

    /**
     * Gets the origin location used to calculate the trajectory of the knockback.
     *
     * @return the knockback origin location
     */
    public Location getKnockbackOrigin()
    {
        return _kbOrigin;
    }

    /**
     * Gets the amount of fire ticks that will be applied to the damagee due to the damage.
     *
     * @return the amount of fire ticks that will be applied
     */
    public int getFireTicks()
    {
        return _fireTicks;
    }

    /**
     * Sets the amount of fire ticks that will be applied to the damagee due to the damage.
     *
     * @param ticks the amount of fire ticks that will be applied
     */
    public void setFireTicks(int ticks)
    {
        _fireTicks = ticks;
    }

    /**
     * Gets whether or not the hit/damage rate will be ignored.
     *
     * @return true if the hit/damage rate will be ignored, otherwise false
     */
    public boolean isIgnoreRate()
    {
        return _ignoreRate;
    }

    /**
     * Sets whether or not the hit/damage rate will be ignored.
     *
     * @param ignoreRate if the hit/damage rate should be ignored
     */
    public void setIgnoreRate(boolean ignoreRate)
    {
        _ignoreRate = ignoreRate;
    }

    /**
     * Gets the player that caused the damage to the damagee.
     *
     * @return the player that caused the damage if the damager is a player, if the damager is not a player
     * null will be returned
     */
    public Player getPlayerDamager()
    {
        return _playerDamager;
    }

    /**
     * Gets the living entity that caused the damage to the damagee if the damagee was damaged by a living
     * entity. If the damagee was not damaged by a living entity null will be returned.
     *
     * @return the living entity that caused the damage, if the damager was not caused by a living entity
     * null will be returned
     */
    public LivingEntity getEntityDamager()
    {
        return _entityDamager;
    }

    /**
     * Gets the player that is being damaged if the damagee is a player. If the damagee is not a player
     * null will be returned.
     *
     * @return the player that is being damaged if the damagee is a player, if the damagee is not a player
     * null will be returned
     */
    public Player getPlayerDamagee()
    {
        return _playerDamagee;
    }

    /**
     * Gets the living entity that is being damaged.
     *
     * @return the living entity that is being damaged
     */
    public LivingEntity getEntityDamagee()
    {
        return _entityDamagee;
    }

    /**
     * Gets the projectile that was used to damage the entity if the damagee was damaged by a projectile. If the
     * damage was not caused by a projectile null will be returned.
     *
     * @return the projectile that was used to damage the entity, if the damagee was not damaged by a projectile
     * null will be returned
     */
    public Projectile getProjectile()
    {
        return _projectile;
    }

    @Override
    public HandlerList getHandlers() {
        return _handlers;
    }

    public static HandlerList getHandlerList()
    {
        return _handlers;
    }

    /**
     * Retrieves a list containing all of reasons the event is being canceled.
     *
     * @return a list containing all of the reasons for the event being canceled
     */
    public List<String> getCanceledReasons()
    {
        return _canceledReasons;
    }

    @Override
    public boolean isCancelled()
    {
        return _canceledReasons.size() > 0;
    }

    public void setCancelled(String reason)
    {
        _canceledReasons.add(reason);
    }

    /**
     * Instead you should use the {@link #setCancelled(String) setCancelled} method which
     * requires a cancel reason.
     */
    @Override
    public void setCancelled(boolean isCanceled)
    {
        if (isCanceled)
        {
            _canceledReasons.add("Just because");
        }
        else
        {
            _canceledReasons.clear();
        }
    }
}
