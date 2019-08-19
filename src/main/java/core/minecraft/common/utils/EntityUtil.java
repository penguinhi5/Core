package core.minecraft.common.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Contains all of the utilities used on entities.
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

        if (groundBoost && isEntityGrounded(entity))
        {
            velocity.setY(velocity.getY() + 0.2D);
        }

        // Ensures the velocity doesn't exceed the yMax
        velocity.setY(Math.min(velocity.getY(), yMax));

        if (addInitialVelocity)
        {
            velocity.add(entity.getVelocity());
        }

        entity.setFallDistance(0.0F);

        entity.setVelocity(velocity);
    }

    /**
     * Returns the total armor point value of all the armor pieces the entity is wearing.
     *
     * @param entity the entity being checked
     * @return the amount of armor points the entity is wearing
     */
    public static int getArmorValue(LivingEntity entity)
    {
        int points = 0;
        for (ItemStack armor : entity.getEquipment().getArmorContents())
        {
            if (armor.getType() == Material.LEATHER_HELMET ||
                    armor.getType() == Material.LEATHER_BOOTS ||
                    armor.getType() == Material.GOLD_BOOTS ||
                    armor.getType() == Material.CHAINMAIL_BOOTS)
            {
                points += 1;
            }
            else if (armor.getType() == Material.LEATHER_LEGGINGS ||
                    armor.getType() == Material.GOLD_HELMET ||
                    armor.getType() == Material.CHAINMAIL_HELMET ||
                    armor.getType() == Material.IRON_HELMET ||
                    armor.getType() == Material.IRON_BOOTS)
            {
                points += 2;
            }
            else if (armor.getType() == Material.LEATHER_CHESTPLATE ||
                    armor.getType() == Material.GOLD_LEGGINGS ||
                    armor.getType() == Material.DIAMOND_HELMET ||
                    armor.getType() == Material.DIAMOND_BOOTS)
            {
                points += 3;
            }
            else if (armor.getType() == Material.CHAINMAIL_LEGGINGS)
            {
                points += 4;
            }
            else if (armor.getType() == Material.GOLD_CHESTPLATE ||
                    armor.getType() == Material.CHAINMAIL_CHESTPLATE ||
                    armor.getType() == Material.IRON_LEGGINGS)
            {
                points += 5;
            }
            else if (armor.getType() == Material.IRON_CHESTPLATE ||
                    armor.getType() == Material.DIAMOND_LEGGINGS)
            {
                points += 6;
            }
            else if (armor.getType() == Material.DIAMOND_CHESTPLATE)
            {
                points += 8;
            }
        }
        return points;
    }
}
