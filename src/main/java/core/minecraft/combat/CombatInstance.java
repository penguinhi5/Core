package core.minecraft.combat;

import core.minecraft.damage.DamageChange;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * This represents the recent combat history between 2 players
 */
public class CombatInstance {

    // The name of the entity
    private String _entityName;
    // If the entity is a player
    private boolean _isPlayer = false;
    // The entity
    private LivingEntity _entity;
    // All of the damage instances in reverse chronological order
    private LinkedList<CombatDamage> _damage = new LinkedList<>();
    // The last time in milliseconds that damage was dealt
    private long _lastDamageTime = 0L;
    // The last amount of damage that was dealt
    private double _lastDamage = 0D;

    /**
     * Creates a new CombatInstance.
     *
     * @param entityName the name of the entity
     * @param entity the entity
     */
    public CombatInstance(String entityName, LivingEntity entity)
    {
        _entityName = entityName;
        _entity = entity;
        if (entity != null && entity instanceof Player)
        {
            _isPlayer = true;
        }
    }

    /**
     * Adds a new damage instance.
     *
     * @param reason the reason for the damage
     * @param damage the amount of damage dealt
     */
    public void addDamage(String reason, double damage)
    {
        _damage.addFirst(new CombatDamage(_entityName, reason, damage));
        _lastDamageTime = System.currentTimeMillis();
        _lastDamage = damage;
    }

    /**
     * Gets the name of the entity in this combat instance.
     *
     * @return the name of the entity.
     */
    public String getName()
    {
        return _entityName;
    }

    /**
     * Gets a list of all the damage instances in reverse chronological order.
     *
     * @return a list of all the damage instances
     */
    public LinkedList<CombatDamage> getDamage()
    {
        return _damage;
    }

    /**
     * If the entity in this combat instance is a player or not.
     *
     * @return true if the entity in this combat instance is a player, otherwise false
     */
    public boolean isPlayer() {
        return _isPlayer;
    }

    /**
     * Gets the time in milliseconds that the last damage instance occurred.
     *
     * @return the time in milleseconds that the last damage instance occurred
     */
    public long getLastDamageTime() {
        return _lastDamageTime;
    }

    /**
     * Gets the amount of damage applied in the last damage instance.
     *
     * @return the amount of damage applied in the last damage instance
     */
    public double getLastDamage() {
        return _lastDamage;
    }

    /**
     * Gets the entity in this combat instance.
     *
     * @return the entity in this combat instance
     */
    public LivingEntity getEntity()
    {
        return _entity;
    }

    /**
     * Gets the total amount of damage that was dealt by the entity.
     *
     * @return the total amount of damage dealt by the entity
     */
    public double getTotalDamage()
    {
        double damage = 0.0D;
        for (CombatDamage dam : _damage)
        {
            damage += dam.getDamage();
        }
        return damage;
    }
}
