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
 * Created by MOTPe on 7/20/2019.
 */
public class CustomDamageEvent extends Event implements Cancellable {

    private double _initialDamage;
    private DamageCause _damageCause;

    private HashMap<String, Double> _knockbackMod = new HashMap<>();
    private HashMap<String, DamageChange> _damageMult = new HashMap<>();
    private HashMap<String, DamageChange> _damageMod = new HashMap<>();
    private boolean _ignoreArmor = false;
    private boolean _knockback = true;
    private Location _kbOrigin;

    private Player _playerDamager;
    private LivingEntity _entityDamager;

    private Player _playerDamagee;
    private LivingEntity _entityDamagee;

    private Projectile _projectile;
    private ArrayList<String> _canceledReasons = new ArrayList<>();

    private static final HandlerList _handlers = new HandlerList();

    /**
     * Creates a new CustomDamageEvent instance.
     *
     * ignoreArmor currently is not supported!
     *
     * @param entityDamager the entity receiving damage
     * @param entityDamagee the entity applying the damage
     * @param projectile the projectile that damaged the entity
     * @param damageCause the cause of the damage
     * @param initialDamage the initial damage applied to the entity
     * @param reason the reason for the damage
     * @param ignoreArmor if the damage applied to the entity should ignore armor [UNSUPPORTED]
     * @param knockback if the damagee should receive knockback
     */
    public CustomDamageEvent(LivingEntity entityDamager, LivingEntity entityDamagee, Projectile projectile,
                             DamageCause damageCause, double initialDamage, String source, String reason,
                             boolean ignoreArmor, boolean knockback)
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
            _damageMod.put(source, new DamageChange(source, 0.0D, reason, true));
        }

        _projectile = projectile;

        _ignoreArmor = ignoreArmor;
        _knockback = knockback;
        if (_entityDamager != null)
        {
            _kbOrigin = _entityDamager.getLocation();
        }

        _damageCause = damageCause;
        _initialDamage = initialDamage;
    }

    public void addKnockback(String reason, double kb)
    {
        _knockbackMod.put(reason, kb);
    }

    public void addDamageMultiplier(String source, String reason, double multiplier, boolean useReason)
    {
        _damageMult.put(reason, new DamageChange(source, multiplier, reason, useReason));
    }

    public void addDamage(String source, String reason, double damage, boolean useReason)
    {
        _damageMod.put(reason, new DamageChange(source, damage, reason, useReason));
    }

    public HashMap<String, Double> getKnockback()
    {
        return _knockbackMod;
    }

    public HashMap<String, DamageChange> getDamageMultipliers()
    {
        return _damageMult;
    }

    public HashMap<String, DamageChange> getDamageMods()
    {
        return _damageMod;
    }

    public double getDamage()
    {
        double finalDamage = _initialDamage;

        for (DamageChange damageMod : _damageMod.values())
        {
            finalDamage += damageMod.getMod();
        }

        for (DamageChange damageMult : _damageMult.values())
        {
            finalDamage *= damageMult.getMod();
        }

        return finalDamage;
    }

    public String getReason()
    {
        String reason = "";

        for (DamageChange damageChange : _damageMod.values())
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

    public DamageCause getDamageCause()
    {
        return _damageCause;
    }

    public boolean isIgnoreArmor()
    {
        return _ignoreArmor;
    }

    public boolean isKnockback()
    {
        return _knockback;
    }

    public void setKnockback(boolean knockback)
    {
        _knockback = knockback;
    }

    public void setKnockbackOrigin(Location origin)
    {
        _kbOrigin = origin;
    }

    public Location getKnockbackOrigin()
    {
        return _kbOrigin;
    }

    public Player getPlayerDamager()
    {
        return _playerDamager;
    }

    public LivingEntity getEntityDamager()
    {
        return _entityDamager;
    }

    public Player getPlayerDamagee()
    {
        return _playerDamagee;
    }

    public LivingEntity getEntityDamagee()
    {
        return _entityDamagee;
    }

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

    @Override
    public void setCancelled(boolean isCanceled)
    {
        _canceledReasons.add("Just because");
    }
}
