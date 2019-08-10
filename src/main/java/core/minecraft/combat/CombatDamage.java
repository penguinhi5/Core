package core.minecraft.combat;

import core.minecraft.damage.DamageChange;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an instance where a player was damaged by an entity.
 */
public class CombatDamage {

    private String _entityName;
    private String _source;
    private double _damage;

    public CombatDamage(String entityName, String source, double damage)
    {
        _entityName = entityName;
        _source = source;
        _damage = damage;
    }

    public String getName()
    {
        return _entityName;
    }

    public String getSource()
    {
        return _source;
    }

    public double getDamage()
    {
        return _damage;
    }
}
