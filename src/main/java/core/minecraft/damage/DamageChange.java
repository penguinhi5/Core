package core.minecraft.damage;

/**
 * Represents a modification in the damage applied to an entity.
 */
public class DamageChange {

    private String _source;
    private double _mod;
    private String _reason;
    private boolean _useReason;

    /**
     * Creates a new DamageChange instance.
     *
     * @param source the source of the damage modification
     * @param mod the modification applied to the damage
     * @param reason the reason for the damage modification
     * @param useReason if the reason should be included with the weapon used to kill the damagee if they
     *                  were killed by a player
     */
    public DamageChange(String source, double mod, String reason, boolean useReason)
    {
        _source = source;
        _mod = mod;
        _reason = reason;
        _useReason = useReason;
    }

    /**
     * Returns the source of the damage modification.
     *
     * @return the source of the damage modification
     */
    public String getSource()
    {
        return _source;
    }

    /**
     * Returns the modification applied to the damage.
     *
     * @return the modification applied to the damage
     */
    public double getMod()
    {
        return _mod;
    }

    /**
     * Returns the reason for the damage modification.
     *
     * @return the reason for the damage modification
     */
    public String getReason()
    {
        return _reason;
    }

    /**
     * Returns whether or not the reason should be used in the death message with the weapon used to kill the damagee
     * if the damager is a player.
     *
     * @return true if the reason should be included, otherwise false
     */
    public boolean useReason()
    {
        return _useReason;
    }
}
