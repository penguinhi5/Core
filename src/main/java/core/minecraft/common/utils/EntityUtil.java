package core.minecraft.common.utils;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

/**
 * Created by MOTPe on 7/27/2019.
 */
public class EntityUtil {

    /**
     * Returns the entity that dealt the damage in the EntityDamageEvent. If the entity was not damaged
     * by a living entity or a projectile null will be returned.
     *
     * @param event the EntityDamageEvent
     * @param includeRanged if projectiles shot by an entity should be counted
     * @return the entity that dealt the damage, or null if the entity isn't a LivingEntity or projectile
     */
    public static LivingEntity getDamagerFromEntityDamageEvent(EntityDamageEvent event, boolean includeRanged)
    {
        if (!(event instanceof EntityDamageByEntityEvent))
        {
            return null;
        }

        EntityDamageByEntityEvent eventE = (EntityDamageByEntityEvent)event;

        if (eventE.getDamager() instanceof LivingEntity)
        {
            return (LivingEntity)eventE.getDamager();
        }

        if (includeRanged && eventE.getDamager() instanceof Projectile)
        {
            Projectile projectile = (Projectile)eventE.getDamager();

            if (projectile.getShooter() != null && projectile.getShooter() instanceof LivingEntity)
            {
                return (LivingEntity)projectile.getShooter();
            }
        }

        return null;
    }

    /**
     * Returns whether or not the entity is currently grounded.
     *
     * @param entity the entity being checked
     * @return true if the entity is grounded, otherwise false
     */
    public static boolean isEntityGrounded(LivingEntity entity)
    {
        return entity.getLocation().getY() % 1.0D == 0.0D;
    }

    /**
     * Sets the velocity of a LivingEntity.
     *
     * @param entity the entity whose velocity is being set
     * @param trajectory the trajectory of the new velocity
     * @param speed the length of the velocity vector
     * @param addInitialVelocity if the player's initial velocity should be added to the new velocity
     * @param groundBoost adds a boost in the y direction if the entity is on the ground
     */
    public static void velocity(LivingEntity entity, Vector trajectory, double speed, double yAdd, double yMax, boolean setInitialY, double initialY, boolean addInitialVelocity, boolean groundBoost)
    {
        if (trajectory.length() == 0)
        {
            return;
        }

        if (setInitialY)
        {
            trajectory.setY(initialY);
        }

        // Gets the velocity of the trajectory at the specified speed
        Vector velocity = trajectory.normalize().multiply(speed);

        // Adds the yAdd
        velocity.setY(velocity.getY() + yAdd);

        // Ensures the velocity doesn't exceed the yMax
        velocity.setY(Math.min(velocity.getY(), yMax));

        if (groundBoost && isEntityGrounded(entity))
        {
            velocity.setY(velocity.getY() + 0.2D);
        }

        if (addInitialVelocity)
        {
            velocity.add(entity.getVelocity());
        }

        entity.setFallDistance(0.0F);

        entity.setVelocity(velocity);
    }
}
