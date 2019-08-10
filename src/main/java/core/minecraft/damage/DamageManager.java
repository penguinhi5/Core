package core.minecraft.damage;

import core.minecraft.Component;
import core.minecraft.combat.CombatManager;
import core.minecraft.command.CommandManager;
import core.minecraft.common.utils.EntityUtil;
import core.minecraft.damage.events.CustomDamageEvent;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

/**
 * Created by MOTPe on 7/19/2019.
 */
public class DamageManager extends Component implements Listener {

    private CombatManager _combatManager;

    /**
     * Creates a new DamageManager instance.
     */
    public DamageManager(CombatManager combatManager, JavaPlugin plugin, CommandManager commandManager)
    {
        super("Damage", plugin, commandManager);
        _combatManager = combatManager;

        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    public CustomDamageEvent customDamageEvent(LivingEntity damager, LivingEntity damagee, Projectile projectile,
                                               DamageCause damageCause, double initialDamage, String initialSource, String initialReason,
                                               boolean ignoreArmor, boolean knockback)
    {
        CustomDamageEvent event = new CustomDamageEvent(damager, damagee, projectile, damageCause, initialDamage, initialSource, initialReason, ignoreArmor, knockback);
        Bukkit.getPluginManager().callEvent(event);
        return event;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDamageEvent(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity))
        {
            return;
        }

        LivingEntity damager = EntityUtil.getDamagerFromEntityDamageEvent(event, true);
        LivingEntity damagee = (LivingEntity)event.getEntity();

        Projectile projectile = getProjectile(event);

        DamageCause cause = event.getCause();

        double damage = event.getFinalDamage();

        customDamageEvent(damager, damagee, projectile, cause, damage, null, null, false, true);

        event.setCancelled(true);
    }

    @EventHandler
    public void cancelDamage(CustomDamageEvent event)
    {
        Player damagee = event.getPlayerDamagee();

        if (damagee == null)
        {
            return;
        }

        if (damagee.getGameMode() != GameMode.SURVIVAL)
        {
            event.setCancelled("Damagee not in survival");
        }

        if (!_combatManager.getCombatClient(damagee.getName()).canBeHurtBy(event.getEntityDamager()))
        {
            event.setCancelled("Entity/World damage rate");
        }

        Player damager = event.getPlayerDamager();

        if (damager != null)
        {
            if (!_combatManager.getCombatClient(damager.getName()).canHurt(damagee))
            {
                event.setCancelled("PVP damage rate");
            }

            if (damager.getGameMode() != GameMode.SURVIVAL)
            {
                event.setCancelled("Damager not in survival");
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleDamage(CustomDamageEvent event)
    {
        if (event.isCancelled())
        {
            return;
        }

        if (event.getEntityDamagee() == null)
        {
            return;
        }

        // Ensures the entity isn't dead and a positive amount of damage is being applied
        if (event.getEntityDamagee().getHealth() <= 0 || event.getDamage() <= 0.0F)
        {
            return;
        }

        // Logs the damage in the CombatManager
        _combatManager.logDamage(event);

        boolean entityDied = false;
        // Applies damage while also handling the noDamageTicks algorithm
        if (event.getEntityDamagee().getNoDamageTicks() > event.getEntityDamagee().getMaximumNoDamageTicks() / 2.0F)
        {
            if (event.getDamage() <= event.getEntityDamagee().getLastDamage())
            {
                return;
            }

            entityDied = applyDamage(event.getEntityDamagee(), event.getDamage() - event.getEntityDamagee().getLastDamage());
            event.getEntityDamagee().setLastDamage(event.getDamage());
        }
        else
        {
            event.getEntityDamagee().setLastDamage(event.getDamage());
            entityDied = applyDamage(event.getEntityDamagee(), event.getDamage());
        }

        // Applies knockback if the entity didn't die
        if (!entityDied)
        {
            applyKnockback(event);
        }

        // Plays arrow hit sound effect for damager
        if (event.getProjectile() != null && event.getProjectile() instanceof Arrow)
        {
            Player damager = event.getPlayerDamager();
            if (damager != null)
            {
                damager.playSound(damager.getLocation(), Sound.ORB_PICKUP, 0.5F, 0.5F);
            }
        }
    }

    /**
     * Applies damage to the entity.
     *
     * @param entity the entity being damaged
     * @param damage the amount of damage being applied
     * @return true if the player died from the damage, otherwise false
     */
    private boolean applyDamage(LivingEntity entity, double damage)
    {
        double newHealth = Math.max(entity.getHealth() - damage, 0.0F);

        // Plays the damage effect
        entity.damage(0.0D);

        // Applies the damage
        entity.setHealth(newHealth);

        // Autorespawn
        if (entity instanceof Player && newHealth <= 0.0F)
        {
            respawn(((Player)entity));
            return true;
        }
        return false;
    }

    public void respawn(Player player)
    {
        ((CraftServer)Bukkit.getServer()).getHandle().moveToWorld(((CraftPlayer)player).getHandle(), 0, false);

//        player.setHealth(player.getMaxHealth());
//        player.setFoodLevel(20);
//        for (PotionEffect potionEffect : player.getActivePotionEffects())
//        {
//            player.removePotionEffect(potionEffect.getType());
//        }
//        player.setFireTicks(0);
//        player.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
//        player.setFallDistance(0.0F);
//
//        // Respawn the player and teleport them to the respawn location
//        PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(player, player.getWorld().getSpawnLocation(), false);
//        Bukkit.getPluginManager().callEvent(respawnEvent);
//        player.teleport(respawnEvent.getRespawnLocation());
    }

    public void applyKnockback(CustomDamageEvent event)
    {
        // Handles knockback (default 1.8.8 knockback)
        if (event.getKnockbackOrigin() != null)
        {
            Location origin = event.getKnockbackOrigin();

            double knockbackMult = Math.max(2.0D, event.getDamage() / 1.2D);
            for (double mult : event.getKnockback().values())
            {
                knockbackMult *= mult;
            }

            // The initial velocity of the damagee
            Vector initialVelocity = event.getEntityDamagee().getVelocity();

            // The trajectory of the knockback
            Vector trajectory = event.getEntityDamagee().getLocation().toVector().subtract(origin.toVector());
            trajectory.multiply(0.06D * knockbackMult);
            trajectory.setY(Math.abs(trajectory.getY()));


            double sprintMultiplier = 1.0D;
            if (event.getPlayerDamager() != null && event.getPlayerDamager().isSprinting())
            {
                sprintMultiplier = 1.2D;
            }

            double speed = 0.2D + trajectory.length() * 0.6D * sprintMultiplier;
            double yAdd = Math.abs(0.1D * knockbackMult);
            double yMax = 0.1D + 0.02D * knockbackMult;

            EntityUtil.velocity(event.getEntityDamagee(), trajectory, speed, yAdd, yMax, false, 0.0D, true, true);
        }
    }

    /**
     * Gets the Projectile that damaged the entity in the EntityDamageEvent. If the entity wasn't damaged by a
     * projectile null will be returned.
     *
     * @param event the EntityDamageEvent
     * @return the projectile the damaged the entity if a projectile did damage the entity, otherwise null
     */
    private Projectile getProjectile(EntityDamageEvent event)
    {
        if (!(event instanceof EntityDamageByEntityEvent))
        {
            return null;
        }

        EntityDamageByEntityEvent eventE = (EntityDamageByEntityEvent)event;

        // Checks if the damager was a projectile
        if (eventE.getDamager() instanceof Projectile)
        {
            return (Projectile)eventE.getDamager();
        }

        return null;
    }
}
