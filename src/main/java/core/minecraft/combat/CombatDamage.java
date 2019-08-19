package core.minecraft.combat;

/**
 * Represents an instance where a player was damaged by an entity.
 */
public class CombatDamage {

    // The name of the entity that caused the damage
    private String _entityName;
    // The reason the damage was dealt
    private String _reason;
    // The amount of damage that was dealt
    private double _damage;

    /**
     * Creates a new combatDamage instance.
     *
     * @param entityName the name of the entity that caused the damage
     * @param reason the reason the damage was dealt
     * @param damage the amount of damage that was dealt
     */
    public CombatDamage(String entityName, String reason, double damage)
    {
        _entityName = entityName;
        _reason = reason;
        _damage = damage;
    }

    /**
     * Gets the name of the entity that caused the damage.
     *
     * @return the name of the cause of the damage
     */
    public String getName()
    {
        return _entityName;
    }

    /**
     * Gets the reason the damage was dealt.
     *
     * @return the reason the damage was dealt
     */
    public String getReason()
    {
        return _reason;
    }

    /**
     * Gets the amount of damage that was dealt.
     *
     * @return the amount of damage that was dealt
     */
    public double getDamage()
    {
        return _damage;
    }
}
