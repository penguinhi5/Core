package core.minecraft.damage;

import core.minecraft.Component;
import core.minecraft.combat.CombatManager;
import core.minecraft.command.CommandManager;
import core.minecraft.common.utils.DamageUtil;
import core.minecraft.common.utils.EntityUtil;
import core.minecraft.damage.events.CustomDamageEvent;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * Manages all aspects of living entity damage and alters specific aspects of PVP and PVE.
 */
public class DamageManager extends Component implements Listener {

    // If item durability should not be lost when it is used to damage an entity
    private boolean _disableItemDurabilityLoss = false;

    // If armor durability should not be lost
    private boolean _disableArmorDurabilityLoss = false;

    // If the plugin should use the CustomDamageEvent and damage customizations
    private boolean _enableCustomDamage = true;

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

    /**
     * Calls the CustomDamageEvent.
     *
     * <p>
     *     initialReason and initialSource parameters should be set to null if you do not want to specify a weapon
     *     used to kill the damagee that will be displayed in the death message if the damager is a player.
     * </p>
     *
     * @param damager The entity that damaged the damagee
     * @param damagee the entity that took damage
     * @param projectile the projectile used to damage the damagee
     * @param damageCause the cause of the damage
     * @param initialDamage the initial damage applied to the entity
     * @param initialSource the initial source of the damage
     * @param initialReason the initial reason for the damage, this will be displayed as the weapon used to kill
     *                      the damagee if the damager is a player and source is not null
     * @param ignoreArmor if the damage should ignore armor protection
     * @param ignoreRate if the damage should ignore the hit/damage rate
     * @param knockback if knockback should be applied
     * @return
     */
    public void customDamageEvent(LivingEntity damager, LivingEntity damagee, Projectile projectile,
                                               DamageCause damageCause, double initialDamage, String initialSource, String initialReason,
                                               boolean ignoreArmor, boolean ignoreRate, boolean knockback)
    {
        customDamageEvent(damager, damagee, projectile, damageCause, initialDamage, initialSource, initialReason, null, ignoreArmor, ignoreRate, knockback);
    }

    /**
     * Calls the CustomDamageEvent.
     *
     * <p>
     *     initialReason and initialSource parameters should be set to null if you do not want to specify a weapon
     *     used to kill the damagee that will be displayed in the death message if the damager is a player.
     * </p>
     *
     * @param damager The entity that damaged the damagee
     * @param damagee the entity that took damage
     * @param projectile the projectile used to damage the damagee
     * @param damageCause the cause of the damage
     * @param initialDamage the initial damage applied to the entity
     * @param initialSource the initial source of the damage
     * @param initialReason the initial reason for the damage, this will be displayed as the weapon used to kill
     *                      the damagee if the damager is a player and source is not null
     * @param knockbackOrigin the origin location used when calculation the knockback trajectory
     * @param ignoreArmor if the damage should ignore armor protection
     * @param ignoreRate if the damage should ignore the hit/damage rate
     * @param knockback if knockback should be applied
     */
    public void customDamageEvent(LivingEntity damager, LivingEntity damagee, Projectile projectile,
                                               DamageCause damageCause, double initialDamage, String initialSource, String initialReason,
                                               Location knockbackOrigin, boolean ignoreArmor, boolean ignoreRate, boolean knockback)
    {
        CustomDamageEvent event = new CustomDamageEvent(damager, damagee, projectile, damageCause, initialDamage, initialSource, initialReason, knockbackOrigin, ignoreArmor, ignoreRate, knockback);
        Bukkit.getPluginManager().callEvent(event);
    }

    /**
     * Uses the EntityDamageEvent to call the CustomDamageEvent if custom damage is enabled.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void entityDamageEvent(EntityDamageEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity) || !_enableCustomDamage)
        {
            return;
        }

        LivingEntity damager = EntityUtil.getDamagerFromEntityDamageEvent(event, true);
        LivingEntity damagee = (LivingEntity)event.getEntity();

        Projectile projectile = getProjectile(event);

        DamageCause cause = event.getCause();

        double damage = event.getDamage();

        if (projectile != null && (projectile instanceof Egg || projectile instanceof Snowball))
        {
            customDamageEvent(damager, damagee, projectile, cause, 0.25D, null, null, false, true, true);
        }
        else
        {
            customDamageEvent(damager, damagee, projectile, cause, damage, null, null, false, false, true);
        }

        event.setCancelled(true);
    }

    /**
     * Cancels the CustomDamageEvent when necessary.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void cancelDamage(CustomDamageEvent event)
    {
        Player damagee = event.getPlayerDamagee();

        if (damagee == null)
        {
            return;
        }

        if (damagee.getHealth() <= 0.0D)
        {
            event.setCancelled("0 Health");
        }

        if (damagee.getGameMode() != GameMode.SURVIVAL)
        {
            event.setCancelled("Damagee not in survival");
        }

        Player damager = event.getPlayerDamager();

        if (damager != null)
        {

            if (damager.getGameMode() != GameMode.SURVIVAL)
            {
                event.setCancelled("Damager not in survival");
            }
        }
    }

    /**
     * Handles projectiles thrown in the CustomDamageEvent.
     */
    @EventHandler
    public void handleProjectiles(CustomDamageEvent event)
    {
        Projectile projectile = event.getProjectile();

        if (projectile == null)
        {
            return;
        }

        if (projectile instanceof Egg || projectile instanceof Snowball)
        {
            event.addKnockback("Projectile", 0.2D);
        }

        if (projectile instanceof Arrow)
        {
            projectile.teleport(new Location(projectile.getWorld(), 0, 0, 0));
            projectile.remove();
        }
    }

    /**
     * Handles damage modifiers for the event such as resistance and enchantments.
     */
    @EventHandler(priority = EventPriority.LOW)
    public void handleDamageModifiers(CustomDamageEvent event)
    {
        Player damagee = event.getPlayerDamagee();
        Player damager = event.getPlayerDamager();
        DamageCause cause = event.getDamageCause();

        if (damagee == null)
        {
            return;
        }

        // Handles armor absorption
        if (EntityUtil.getArmorValue(damagee) > 0 && DamageUtil.canArmorReduceDamage(cause))
        {
            event.addDamageMultiplier("Armor Absorption", damagee.getName(), ((25.0D - (double)EntityUtil.getArmorValue(damagee)) / 25.0D), false);
        }

        // Handles blocking
        if (damagee instanceof Player && ((Player)damagee).isBlocking())
        {
            if (cause == DamageCause.BLOCK_EXPLOSION || cause == DamageCause.ENTITY_ATTACK ||
                    cause == DamageCause.ENTITY_EXPLOSION || (event.getProjectile() != null && event.getProjectile() instanceof Arrow))
            {
                event.addDamageMultiplier("Blocking", damagee.getName(), ((event.getDamage() + 1.0D) / 2.0D), false);
            }
        }

        // Handles resistance
        if (damagee.getActivePotionEffects().contains(PotionEffectType.DAMAGE_RESISTANCE))
        {
            if (cause != DamageCause.CUSTOM && cause != DamageCause.SUICIDE && cause != DamageCause.VOID)
            {
                int resistanceLevel = 0;
                for (PotionEffect type : damagee.getActivePotionEffects())
                {
                    if (type.getType() == PotionEffectType.DAMAGE_RESISTANCE)
                    {
                        resistanceLevel = type.getAmplifier();
                    }
                }
                event.addDamageMultiplier("Resistance", damagee.getName(), ((25.0D - resistanceLevel) / 25.0D), false);
            }
        }

        // Handle armor enchantments
        double epf = 0.0D;
        for (ItemStack armor : damagee.getInventory().getArmorContents())
        {
            for (Enchantment enchant : armor.getEnchantments().keySet())
            {
                if (enchant.equals(Enchantment.PROTECTION_ENVIRONMENTAL) &&
                        cause != DamageCause.VOID &&
                        cause != DamageCause.SUICIDE &&
                        cause != DamageCause.STARVATION)
                {
                    Math.floor(epf += ((6.0D + Math.pow(armor.getEnchantments().get(enchant), 2)) * 0.75D) / 3.0D);
                }
                else if (enchant.equals(Enchantment.PROTECTION_EXPLOSIONS) && (
                        cause == DamageCause.BLOCK_EXPLOSION ||
                        cause == DamageCause.ENTITY_EXPLOSION))
                {
                    Math.floor(epf += ((6.0D + Math.pow(armor.getEnchantments().get(enchant), 2)) * 1.5D) / 3.0D);
                }
                else if (enchant.equals(Enchantment.PROTECTION_FALL) &&
                        cause == DamageCause.FALL)
                {
                    Math.floor(epf += ((6.0D + Math.pow(armor.getEnchantments().get(enchant), 2)) * 2.5D) / 3.0D);
                }
                else if (enchant.equals(Enchantment.PROTECTION_FIRE) && (
                        cause == DamageCause.FIRE ||
                        cause == DamageCause.FIRE_TICK ||
                        cause == DamageCause.LAVA))
                {
                    Math.floor(epf += ((6.0D + Math.pow(armor.getEnchantments().get(enchant), 2)) * 1.25D) / 3.0D);
                }
                else if (enchant.equals(Enchantment.PROTECTION_PROJECTILE) &&
                        cause == DamageCause.PROJECTILE)
                {
                    Math.floor(epf += ((6.0D + Math.pow(armor.getEnchantments().get(enchant), 2)) * 1.5D) / 3.0D);
                }
            }
        }
        epf = Math.min(epf, 25.0D);
        double newVal = epf / 2.0D + ((Math.random() * epf) / 2.0D);
        newVal = Math.round(newVal);
        newVal = Math.min(newVal, 20.0D);
        double reducedDamageMult = (25.0D - newVal) / 25.0D;
        event.addDamageMultiplier("Enchant Protection", damagee.getName(), reducedDamageMult, false);

        if (damager == null)
        {
            return;
        }

        // Handle weapon enchantments
        ItemStack hand = damager.getItemInHand();
        for (Enchantment enchant : hand.getEnchantments().keySet())
        {
            // Knockback
            if (enchant.equals(Enchantment.KNOCKBACK) && cause == DamageCause.ENTITY_ATTACK)
            {
                event.addKnockback("Enchant Knockback", 1.0D + 0.5D * hand.getEnchantmentLevel(Enchantment.KNOCKBACK));
            }
            else if (enchant.equals(Enchantment.ARROW_KNOCKBACK) &&
                    event.getProjectile() != null && event.getProjectile() instanceof Arrow)
            {
                event.addKnockback("Enchant Knockback", 1.0D + 0.5D * hand.getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK));
            }

            // Fire
            if (enchant.equals(Enchantment.ARROW_FIRE) &&
                    event.getProjectile() != null && event.getProjectile() instanceof Arrow)
            {
                event.setFireTicks(20 * 5 * hand.getEnchantmentLevel(enchant));
            }
            else if (enchant.equals(Enchantment.FIRE_ASPECT) && cause == DamageCause.ENTITY_ATTACK)
            {
                event.setFireTicks(80 * hand.getEnchantmentLevel(enchant));
            }
        }
    }

    /**
     * Applies the damage in the CustomDamageEvent.
     */
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

        // Ensures the player isn't being damaged too fast
        if (!event.isIgnoreRate() && !_combatManager.getCombatClient(event.getEntityDamagee().getName()).canBeHurtBy(event.getEntityDamager()))
        {
            return;
        }
        if (event.getEntityDamager() != null)
        {
            if (!event.isIgnoreRate() && !_combatManager.getCombatClient(event.getEntityDamager().getName()).canHurt(event.getEntityDamagee()))
            {
                return;
            }
        }

        // Ensures the entity isn't dead and a positive amount of damage is being applied
        if (event.getEntityDamagee().getHealth() <= 0 || event.getDamage() <= 0.0F)
        {
            return;
        }

        Location damageeLoc = event.getEntityDamagee().getLocation();

        // Logs the damage in the CombatManager
        _combatManager.logDamage(event);
        // Damages the entity's armor
        if (!_disableArmorDurabilityLoss && DamageUtil.canArmorReduceDamage(event.getDamageCause()))
        {
            applyArmorDurabilityLoss(event.getEntityDamagee(), event.getDamage());
        }

        // Damages the damager's weapon
        if (!_disableItemDurabilityLoss && event.getPlayerDamager() != null && event.getDamageCause() == DamageCause.ENTITY_ATTACK)
        {
            applyItemDurabilityLoss(event.getPlayerDamager());
        }

        boolean entityDied = false;
        // Applies damage while also handling the noDamageTicks algorithm
        if (event.getEntityDamagee().getNoDamageTicks() > event.getEntityDamagee().getMaximumNoDamageTicks() / 2.0F)
        {
            if (event.getDamage() <= event.getEntityDamagee().getLastDamage())
            {
                return;
            }

            entityDied = applyDamage(event.getEntityDamagee(), event.getDamage() - event.getEntityDamagee().getLastDamage(), event.isIgnoreArmor(), event.getFireTicks());
            event.getEntityDamagee().setLastDamage(event.getDamage());
        }
        else
        {
            event.getEntityDamagee().setLastDamage(event.getDamage());
            entityDied = applyDamage(event.getEntityDamagee(), event.getDamage(), event.isIgnoreArmor(), event.getFireTicks());
        }

        // Handle thorns enchant damage
        if (event.getEntityDamager() != null)
        {
            for (ItemStack item : event.getEntityDamagee().getEquipment().getArmorContents())
            {
                if (item.getEnchantments().keySet().contains(Enchantment.THORNS))
                {
                    // Random chance the damager will take damage
                    int thornsLvl = item.getEnchantmentLevel(Enchantment.THORNS);
                    if (Math.random() < (thornsLvl * 0.15))
                    {
                        double damage = 0.0D;
                        if (thornsLvl > 10)
                        {
                            damage = thornsLvl - 10.0D;
                        }
                        else
                        {
                            // Randomly does 1 - 4 damage
                            damage = new Random().nextInt(4) + 1;
                        }
                        // Damages the damager
                        customDamageEvent(event.getEntityDamagee(), event.getEntityDamager(), null, DamageCause.THORNS,
                                damage, event.getEntityDamagee().getName(), "Thorns", damageeLoc, false, true, false);

                        // Damages the damagee's armor
                        if (!_disableArmorDurabilityLoss)
                        {
                            item.setDurability((short)(item.getDurability() - 3));
                        }
                    }
                    // Only thorns on the bottommost piece of armor is applied
                    break;
                }
            }
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
    private boolean applyDamage(LivingEntity entity, double damage, boolean ignoreArmor, int fireTicks)
    {
        double finalDamage = damage;

        // Plays the damage effect
        entity.damage(0.0D);

        // Handles absorption hearts
        EntityLiving entityLiving = ((CraftLivingEntity)entity).getHandle();
        double absorptionHealth = entityLiving.getAbsorptionHearts();
        entityLiving.setAbsorptionHearts(Math.max(0.0F, entityLiving.getAbsorptionHearts() - (float)finalDamage));

        // Applies damage if damage > absorption hearts update the player's health
        double newHealth = entity.getHealth();
        if (finalDamage >= absorptionHealth)
        {
            entity.removePotionEffect(PotionEffectType.ABSORPTION);
            newHealth = Math.max(0.0D, entity.getHealth() - (finalDamage - absorptionHealth));
            entity.setHealth(newHealth);
        }

        // Ignite players
        if (newHealth > 0.0D && fireTicks > 0)
        {
            Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
                @Override
                public void run() {
                    entity.setFireTicks(fireTicks);
                }
            }, 0L);
        }

        // Autorespawn
        if (entity instanceof Player && newHealth <= 0.0F)
        {
            respawn(((Player)entity));
            return true;
        }
        return false;
    }

    /**
     * Forces the player to respawn when they die.
     *
     * @param player the player that is respawning
     */
    public void respawn(Player player)
    {
        if (player.isDead())
        {
            ((CraftServer)Bukkit.getServer()).getHandle().moveToWorld(((CraftPlayer)player).getHandle(), 0, false);
        }

        // USED TO BYPASS PLAYER DEATH/RESPAWN ENTIRELY
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

    /**
     * Applies knockback to the player based on the parameters in the CustomDamageEvent.
     *
     * @param event the event that was called
     */
    public void applyKnockback(CustomDamageEvent event)
    {
        if (event.getKnockbackOrigin() != null)
        {
            Location origin = event.getKnockbackOrigin();

            double knockbackMult = Math.max(2.0D, event.getDamage());

            // The trajectory of the knockback
            Vector trajectory = event.getEntityDamagee().getLocation().toVector().subtract(origin.toVector()).normalize();
            trajectory.multiply(0.1D * Math.sqrt(knockbackMult * 3.0D));


            double sprintMultiplier = 1.0D;
            double knockbackEnchant = 1.0D;
            if (event.getPlayerDamager() != null)
            {
                if (event.getPlayerDamager().isSprinting())
                {
                    sprintMultiplier = 1.2D;
                }
            }
            for (double mult : event.getKnockbackMults().values())
            {
                knockbackEnchant *= mult;
            }

            double speed = 0.4D + trajectory.length() * 0.8D * sprintMultiplier * knockbackEnchant;
            double yAdd = Math.abs(0.15D * knockbackMult * knockbackEnchant);
            double yMax = 0.2D + 0.01D * knockbackMult * knockbackEnchant;

            Bukkit.getScheduler().runTaskLater(getPlugin(), new Runnable() {
                @Override
                public void run() {
                    EntityUtil.velocity(event.getEntityDamagee(), trajectory, speed, yAdd, yMax, false, 0.0D, true, true);
                }
            }, 0L);
        }
    }

    /**
     * Reduces the durability of the armor pieces the player is wearing
     *
     * @param entity the entity that took damage
     */
    public void applyArmorDurabilityLoss(LivingEntity entity, double damage)
    {
        for (ItemStack item : entity.getEquipment().getArmorContents())
        {
            // reduced is the amount of damage the armor reduced
            int reduce = (int)Math.ceil(((1 - ((25.0D - (double)EntityUtil.getArmorValue(entity)) / 25.0D)) * damage));

            if (item.containsEnchantment(Enchantment.DURABILITY))
            {
                for (int i = 0; i < reduce; i++)
                {
                    // Random chance that unbreaking will not lower the durability
                    if (new Random().nextInt(101) <= (60 + (40 / (item.getEnchantmentLevel(Enchantment.DURABILITY) + 1))))
                    {
                        item.setDurability((short)(item.getDurability() + 1));
                    }
                }
            }
            else
            {
                item.setDurability((short)(item.getDurability() + reduce));
            }

            // Breaks the item if the durability is 0
            if (item.getDurability() >=  (int)item.getType().getMaxDurability())
            {
                if (entity instanceof Player)
                {
                    Player player = ((Player)entity);
                    if (item.getType().name().contains("HELMET"))
                    {
                        player.getInventory().setHelmet(null);
                    }
                    else if (item.getType().name().contains("CHESTPLATE"))
                    {
                        player.getInventory().setChestplate(null);
                    }
                    else if (item.getType().name().contains("LEGGINGS"))
                    {
                        player.getInventory().setLeggings(null);
                    }
                    else if (item.getType().name().contains("BOOTS"))
                    {
                        player.getInventory().setBoots(null);
                    }
                    player.playSound(entity.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                }
            }
        }
    }

    /**
     * Reduces the durability of the item the player is holding assuming the
     * durability loss was caused by PVP or PVE.
     *
     * @param damager the player holding the item
     */
    public void applyItemDurabilityLoss(Player damager)
    {
        ItemStack item = damager.getItemInHand();

        if (item == null)
        {
            return;
        }

        // Gets the damage to the weapon
        int damage = 0;
        if (item.getType().name().contains("AXE") || item.getType().name().contains("PICKAXE") || item.getType().name().contains("SPADE"))
        {
            damage = 2;
        }
        else if (item.getType().name().contains("SWORD") || item.getType().name().contains("HOE"))
        {
            damage = 1;
        }
        Bukkit.broadcastMessage("itemdamage: " + damage);
        // Handles the damage to the weapon
        if (damage > 0)
        {
            if (item.containsEnchantment(Enchantment.DURABILITY))
            {
                for (int i = 0; i < damage; i++)
                {
                    // Random chance that unbreaking will not lower the durability
                    if (new Random().nextInt(101) <= (100 / (item.getEnchantmentLevel(Enchantment.DURABILITY) + 1)))
                    {
                        item.setDurability((short)Math.min((item.getDurability() + 1), (int)item.getType().getMaxDurability()));
                    }
                }
            }
            else
            {
                item.setDurability((short)Math.min((item.getDurability() + damage), (int)item.getType().getMaxDurability()));
            }

            if (item.getDurability() >=  (int)item.getType().getMaxDurability())
            {
                damager.setItemInHand(null);
                damager.playSound(damager.getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
            }
        }
    }

    /**
     * Handles item durability in case custom damage is disabled.
     */
    @EventHandler
    public void handleDurability(PlayerItemDamageEvent event)
    {
        ItemStack item = event.getItem();
        if (item == null)
        {
            return;
        }

        if (item.getType().name().contains("HELMET") || item.getType().name().contains("CHESTPLATE") ||
                item.getType().name().contains("LEGGINGS") || item.getType().name().contains("BOOTS") &&
                _disableArmorDurabilityLoss)
        {
            event.setCancelled(true);
        }
        else if (_disableItemDurabilityLoss)
        {
            event.setCancelled(true);
        }
    }

    /**
     * Gets whether or not item durability loss should be disabled when they attack an entity.
     *
     * @return true if item durability loss is disabled, otherwise false
     */
    public boolean isItemDurabilityLossDisabled()
    {
        return _disableItemDurabilityLoss;
    }

    /**
     * Sets whether or not item durability loss should be disabled when it is used to attack an entity.
     *
     * This is set to false by default.
     *
     * @param disableItemDurabilityLoss if item durability loss should be disabled when it is used to attack an entity
     */
    public void setIsItemDurabilityLossDisabled(boolean disableItemDurabilityLoss)
    {
        _disableItemDurabilityLoss = disableItemDurabilityLoss;
    }

    /**
     * Gets whether or not armor durability loss should be disabled.
     *
     * @return true if armor durability loss is disabled, otherwise false
     */
    public boolean isArmorDurabilityLossDisabled()
    {
        return _disableArmorDurabilityLoss;
    }

    /**
     * Sets whether or not armor duragbility loss should be disabled.
     *
     * This is set to false by default.
     *
     * @param disableArmorDurabilityLoss if armor durability loss should be disabled
     */
    public void setIsArmorDurabilityLossDisabled(boolean disableArmorDurabilityLoss)
    {
        _disableArmorDurabilityLoss = disableArmorDurabilityLoss;
    }

    /**
     * @return the working CombatManager instance
     */
    public CombatManager getCombatManager()
    {
        return _combatManager;
    }

    /**
     * Sets the working CombatManager instance.
     *
     * @param combatManager the working CombatManager instance
     */
    public void setCombatManager(CombatManager combatManager)
    {
        _combatManager = combatManager;
    }

    /**
     * Gets whether or not the custom damage is enabled.
     *
     * @return true if custom damage is enabled, otherwise false
     */
    public boolean isIsCustomDamageEnabled()
    {
        return _enableCustomDamage;
    }

    /**
     * Sets whether or not the custom damage is enabled.
     *
     * This is set to enabled by default.
     *
     * @param enableCustomDamage if custom damage should be enabled
     */
    public void setIsCustomDamageEnabled(boolean enableCustomDamage) {
        _enableCustomDamage = enableCustomDamage;
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
