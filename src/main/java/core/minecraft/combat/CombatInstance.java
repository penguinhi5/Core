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

    private String _entityName;
    private boolean _isPlayer = false;
    private LivingEntity _entity;
    private LinkedList<CombatDamage> _damage = new LinkedList<>();
    private long _lastDamageTime = 0L;
    private double _lastDamage = 0D;

    public CombatInstance(String entityName, LivingEntity entity)
    {
        _entityName = entityName;
        _entity = entity;
        if (entity != null && entity instanceof Player)
        {
            _isPlayer = true;
        }
    }

    public void addDamage(String source, double damage)
    {
        _damage.addFirst(new CombatDamage(_entityName, source, damage));
        _lastDamageTime = System.currentTimeMillis();
        _lastDamage = damage;
    }

    public String getName()
    {
        return _entityName;
    }

    public LinkedList<CombatDamage> getDamage()
    {
        return _damage;
    }

    public boolean isPlayer() {
        return _isPlayer;
    }

    public long getLastDamageTime() {
        return _lastDamageTime;
    }

    public double getLastDamage() {
        return _lastDamage;
    }

    public LivingEntity getEntity()
    {
        return _entity;
    }

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
