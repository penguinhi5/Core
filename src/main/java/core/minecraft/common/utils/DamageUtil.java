package core.minecraft.common.utils;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

/**
 * Contains all of the entities used involving damage.
 */
public class DamageUtil {

    /**
     * Returns whether or not the armor will reduce the amount of damage dealt to the player for the given DamageCause.
     *
     * @param cause the cause of the damage
     * @return true if the armor will reduce the damage, otherwise false
     */
    public static boolean canArmorReduceDamage(DamageCause cause)
    {
        if (cause == null)
        {
            return false;
        }

        if (cause == DamageCause.ENTITY_ATTACK ||
                cause == DamageCause.PROJECTILE ||
                cause == DamageCause.LAVA ||
                cause == DamageCause.BLOCK_EXPLOSION ||
                cause == DamageCause.CONTACT ||
                cause == DamageCause.ENTITY_EXPLOSION ||
                cause == DamageCause.FALLING_BLOCK ||
                cause == DamageCause.FIRE ||
                cause == DamageCause.LIGHTNING)
        {
            return true;
        }
        return false;
    }
}
