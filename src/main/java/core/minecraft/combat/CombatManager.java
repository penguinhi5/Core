package core.minecraft.combat;

import core.minecraft.Component;
import core.minecraft.combat.events.CombatDeathEvent;
import core.minecraft.command.CommandManager;
import core.minecraft.common.F;
import core.minecraft.common.utils.EntityUtil;
import core.minecraft.common.utils.ItemUtil;
import core.minecraft.damage.events.CustomDamageEvent;
import core.minecraft.timer.TimerType;
import core.minecraft.timer.event.TimerEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * Stores the combat history for all of the players on the server.
 */
public class CombatManager extends Component implements Listener {

    private HashMap<Player, CombatLog> _combatLogs = new HashMap<>();
    private HashMap<String, CombatClient> _combatClients = new HashMap<>();

    /**
     * Creates a new CombatManager instance.
     *
     * @param plugin the main JavaPlugin instance
     * @param commandManager the CommandManager instance
     */
    public CombatManager(JavaPlugin plugin, CommandManager commandManager)
    {
        super("Combat", plugin, commandManager);
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    /**
     * Logs the damage applied by the EntityDamageEvent.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void logDamage(EntityDamageEvent event)
    {
        if (event.isCancelled())
        {
            return;
        }

        // Only log player damage
        if (event.getEntity() == null || !(event.getEntity() instanceof Player))
        {
            return;
        }

        LivingEntity damager = EntityUtil.getDamagerFromEntityDamageEvent(event, true);

        if (damager != null)
        {
            if (damager instanceof Player)
            {
                Player damagerP = (Player)damager;
                ItemStack hand = damagerP.getItemInHand();

                // Customizes the source with the item used to damage the player
                if (hand == null || hand.getType() == Material.AIR) // If the player was damaged with fists
                {
                    getCombatLog((Player)event.getEntity()).addAttack(damager.getName(), damager, "Fists", event.getFinalDamage());
                }
                else if (event.getCause() == DamageCause.PROJECTILE && hand.getType() == Material.BOW) // If the damager used archery
                {
                    getCombatLog((Player)event.getEntity()).addAttack(damager.getName(), damager, "Archery", event.getFinalDamage());
                }
                else // If the damager has an item in their hand
                {
                    getCombatLog((Player)event.getEntity()).addAttack(damager.getName(), damager, ItemUtil.getItemDisplayName(hand), event.getFinalDamage());
                }
            }
            else // The player was killed by a mob
            {
                getCombatLog((Player)event.getEntity()).addAttack(damager.getName(), damager, null, event.getFinalDamage());
            }
        }
        else // If the player wasn't damaged by an entity
        {
            String source = "-";
            String reason = "-";

            if (event.getCause() == DamageCause.BLOCK_EXPLOSION)
            {
                source = "Explosion";
            }
            else if (event.getCause() == DamageCause.CONTACT)
            {
                source = "Cactus";
            }
            else if (event.getCause() == DamageCause.CUSTOM)
            {
                source = "Custom Damage";
            }
            else if (event.getCause() == DamageCause.DROWNING)
            {
                source = "Drowning";
            }
            else if (event.getCause() == DamageCause.ENTITY_ATTACK)
            {
                source = "Entity";
                reason = "Attack";
            }
            else if (event.getCause() == DamageCause.ENTITY_EXPLOSION)
            {
                source = "Explosion";
            }
            else if (event.getCause() == DamageCause.FALL)
            {
                source = "Gravity";
            }
            else if (event.getCause() == DamageCause.FALLING_BLOCK)
            {
                source = "Falling Block";
            }
            else if (event.getCause() == DamageCause.FIRE)
            {
                source = "Fire";
            }
            else if (event.getCause() == DamageCause.FIRE_TICK)
            {
                source = "Fire";
            }
            else if (event.getCause() == DamageCause.LAVA)
            {
                source = "Lava";
            }
            else if (event.getCause() == DamageCause.LIGHTNING)
            {
                source = "Zeus";
            }
            else if (event.getCause() == DamageCause.MAGIC)
            {
                source = "Witchcraft";
            }
            else if (event.getCause() == DamageCause.MELTING)
            {
                source = "Becoming a happy snowman";
            }
            else if (event.getCause() == DamageCause.POISON)
            {
                source = "Poison";
            }
            else if (event.getCause() == DamageCause.PROJECTILE)
            {
                source = "Projectile";
            }
            else if (event.getCause() == DamageCause.STARVATION)
            {
                source = "Starvation";
            }
            else if (event.getCause() == DamageCause.SUFFOCATION)
            {
                source = "Suffocation";
            }
            else if (event.getCause() == DamageCause.SUICIDE)
            {
                source = "Suicide";
            }
            else if (event.getCause() == DamageCause.THORNS)
            {
                source = "Thorns";
            }
            else if (event.getCause() == DamageCause.VOID)
            {
                source = "Void";
            }
            else if (event.getCause() == DamageCause.WITHER)
            {
                source = "Wither";
            }

            getCombatLog((Player)event.getEntity()).addAttack(source, null, reason, event.getFinalDamage());
        }
    }

    /**
     * Logs the damage caused by the CustomDamageEvent.
     */
    public void logDamage(CustomDamageEvent event)
    {
        if (event.getPlayerDamagee() == null || event.isCancelled())
        {
            return;
        }

        Player damager = event.getPlayerDamager();

        if (damager != null) // If the damager is a player
        {
            String reason = event.getReason();
            if (reason == null) // If a damage reason wasn't provided
            {
                ItemStack hand = damager.getItemInHand();

                // Customizes the source with the item used to damage the player
                if (hand == null || hand.getType() == Material.AIR) // If the player was damaged with fists
                {
                    getCombatLog(event.getPlayerDamagee()).addAttack(damager.getName(), damager, "Fists", event.getDamage());
                }
                else if (event.getDamageCause() == DamageCause.PROJECTILE && hand.getType() == Material.BOW) // If the damager used archery
                {
                    getCombatLog(event.getPlayerDamagee()).addAttack(damager.getName(), damager, "Archery", event.getDamage());
                }
                else // If the damager has an item in their hand
                {
                    getCombatLog(event.getPlayerDamagee()).addAttack(damager.getName(), damager, ItemUtil.getItemDisplayName(hand), event.getDamage());
                }
            }
            else // If a damage reason was provided
            {
                getCombatLog(event.getPlayerDamagee()).addAttack(damager.getName(), damager, reason, event.getDamage());
            }
        }
        else // If the player wasn't damaged by an entity
        {
            String source = "-";
            String reason = "-";

            if (event.getDamageCause() == DamageCause.BLOCK_EXPLOSION)
            {
                source = "Explosion";
            }
            else if (event.getDamageCause() == DamageCause.CONTACT)
            {
                source = "Cactus";
            }
            else if (event.getDamageCause() == DamageCause.CUSTOM)
            {
                source = "Custom Damage";
            }
            else if (event.getDamageCause() == DamageCause.DROWNING)
            {
                source = "Drowning";
            }
            else if (event.getDamageCause() == DamageCause.ENTITY_ATTACK)
            {
                source = "Entity";
                reason = "Attack";
            }
            else if (event.getDamageCause() == DamageCause.ENTITY_EXPLOSION)
            {
                source = "Explosion";
            }
            else if (event.getDamageCause() == DamageCause.FALL)
            {
                source = "Gravity";
            }
            else if (event.getDamageCause() == DamageCause.FALLING_BLOCK)
            {
                source = "Falling Block";
            }
            else if (event.getDamageCause() == DamageCause.FIRE)
            {
                source = "Fire";
            }
            else if (event.getDamageCause() == DamageCause.FIRE_TICK)
            {
                source = "Fire";
            }
            else if (event.getDamageCause() == DamageCause.LAVA)
            {
                source = "Lava";
            }
            else if (event.getDamageCause() == DamageCause.LIGHTNING)
            {
                source = "Zeus";
            }
            else if (event.getDamageCause() == DamageCause.MAGIC)
            {
                source = "Witchcraft";
            }
            else if (event.getDamageCause() == DamageCause.MELTING)
            {
                source = "Becoming a happy snowman";
            }
            else if (event.getDamageCause() == DamageCause.POISON)
            {
                source = "Poison";
            }
            else if (event.getDamageCause() == DamageCause.PROJECTILE)
            {
                source = "Projectile";
            }
            else if (event.getDamageCause() == DamageCause.STARVATION)
            {
                source = "Starvation";
            }
            else if (event.getDamageCause() == DamageCause.SUFFOCATION)
            {
                source = "Suffocation";
            }
            else if (event.getDamageCause() == DamageCause.SUICIDE)
            {
                source = "Suicide";
            }
            else if (event.getDamageCause() == DamageCause.THORNS)
            {
                source = "Thorns";
            }
            else if (event.getDamageCause() == DamageCause.VOID)
            {
                source = "Void";
            }
            else if (event.getDamageCause() == DamageCause.WITHER)
            {
                source = "Wither";
            }

            getCombatLog(event.getPlayerDamagee()).addAttack(source, null, reason, event.getDamage());
        }
    }

    /**
     * Displays the death message in chat when the player dies.
     */
    @EventHandler
    public void displayDeathMessage(PlayerDeathEvent event)
    {
        event.setDeathMessage(null);

        // If the player doesn't have a combat log
        if (!_combatLogs.containsKey(event.getEntity()))
        {
            return;
        }

        CombatLog log = getCombatLog(event.getEntity());

        CombatDeathEvent messageEvent = new CombatDeathEvent(event.getEntity(), log.getLastDamager().getName(), log.getKillerColor(), log.getVictimColor());
        Bukkit.getPluginManager().callEvent(messageEvent);
        log.setKillerColor(messageEvent.getKillerColor());
        log.setVictimColor(messageEvent.getVictimColor());

        if (messageEvent.getDeathMessageType() == DeathMessageType.SIMPLE)
        {
            Bukkit.broadcastMessage(log.getSimpleDeathMessage());
        }
        else if (messageEvent.getDeathMessageType() == DeathMessageType.VICTIM_AND_KILLER_ONLY)
        {
            for (Player player : Bukkit.getOnlinePlayers())
            {
                if (player.getName().equals(messageEvent.getKiller()) || player.getName().equals(messageEvent.getPlayer().getName()))
                {
                    player.sendMessage(log.getSimpleDeathMessage());
                }
            }
        }
        else if (messageEvent.getDeathMessageType() == DeathMessageType.DETAILED)
        {
            // Broadcasts the public death message
            Bukkit.broadcastMessage(log.getSimpleDeathMessage());

            // messages the dead player their death details
            for (String detail : log.getDeathDetails())
            {
                event.getEntity().sendMessage(detail);
            }

            CombatClient combatHistory = getCombatClient(event.getEntity().getName());
            String livingStats = F.C_EMPHASIS2 + combatHistory.getKillsSinceDeath() + " kills" + F.C_CONTENT + " - " +
                    F.C_EMPHASIS2 + combatHistory.getAssistsSinceDeath() + " assists";
            event.getEntity().sendMessage(F.componentMessage("Life Stats", livingStats));
        }

        // Logs player stats
        handleKillLog(log);

        // Resets a player's combat log
        _combatLogs.remove(event.getEntity());
    }

    /**
     * Gets the current combat log for the given player.
     *
     * @param player the player whose combat log is being retrieved
     * @return the CombatLog associated with the given player
     */
    public CombatLog getCombatLog(Player player)
    {
        if (_combatLogs.containsKey(player))
        {
            return _combatLogs.get(player);
        }
        else
        {
            CombatLog log = new CombatLog(player.getName(), player);
            _combatLogs.put(player, log);
            return log;
        }
    }

    /**
     * Logs the kill, death, and assists in all of the appropriate CombatClients.
     *
     * @param log the combat log for the player that was killed
     */
    private void handleKillLog(CombatLog log)
    {
        // Logs the death
        getCombatClient(log.getPlayer().getName()).logDeath(log);

        // Logs the kill
        CombatInstance killer = log.getKiller();
        if (killer == null)
        {
            return;
        }
        getCombatClient(killer.getName()).logKill(log);

        // Logs the assists
        for (CombatInstance damage : log.getDamagerList())
        {
            if (damage.isPlayer() && damage != killer)
            {
                getCombatClient(damage.getName()).logAssist(log);
            }
        }
    }

    /**
     * Sets the CombatLog for the given player.
     *
     * @param player the player whose CombatLog is being set
     * @param combatLog the player's new CombatLog
     */
    public void setCombatLog(Player player, CombatLog combatLog)
    {
        _combatLogs.put(player, combatLog);
    }

    /**
     * Gets the CombatClient associated with the given player
     *
     * @param player the player whose CombatClient is being retrieved
     * @return the CombatClient belonging to the player
     */
    public CombatClient getCombatClient(String player)
    {
        if (_combatClients.containsKey(player))
        {
            return _combatClients.get(player);
        }
        else
        {
            CombatClient client = new CombatClient();
            _combatClients.put(player, client);
            return client;
        }
    }

    /**
     * Clears the combat history for every player on the server.
     */
    public void wipeCombatHistory()
    {
        _combatLogs.clear();
        _combatClients.clear();
    }

    /**
     * Sets the CombatClient for the given player.
     *
     * @param player the player whose CombatClient is being set
     * @param combatClient the player's new CombatClient
     */
    public void setCombatClient(String player, CombatClient combatClient)
    {
        _combatClients.put(player, combatClient);
    }

    /**
     * Clears old damage from all of the combat logs.
     */
    @EventHandler
    public void clearOldDamage(TimerEvent event)
    {
        if (event.getType() == TimerType.SECOND)
        {
            for (CombatLog log : _combatLogs.values())
            {
                log.cleanDamagerList();
            }
        }
    }

    /**
     * Clears the combat history of any players that left the server.
     */
    @EventHandler
    public void cleanCombatHistory(TimerEvent event)
    {
        // Removes offline player combat logs
        if (event.getType() == TimerType.MINUTE)
        {
            // Removes offline players from _combatLogs
            for (Player player : _combatLogs.keySet())
            {
                if (!player.isOnline())
                {
                    _combatLogs.remove(player);
                }
            }

            // Removes offline players from _combatClients
            for (String playerName : _combatClients.keySet())
            {
                Player player = Bukkit.getPlayer(playerName);
                if (player == null)
                {
                    _combatClients.remove(player);
                }
            }
        }
    }
}
