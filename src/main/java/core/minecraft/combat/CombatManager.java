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
 * Created by MOTPe on 8/6/2019.
 */
public class CombatManager extends Component implements Listener {

    private HashMap<Player, CombatLog> _combatLogs = new HashMap<>();
    private HashMap<String, CombatClient> _combatClients = new HashMap<>();

    public CombatManager(JavaPlugin plugin, CommandManager commandManager)
    {
        super("Combat", plugin, commandManager);
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

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
                getCombatLog((Player)event.getEntity()).addAttack(((Player)damager).getName(), damager, "Attack", event.getDamage());
            }
            getCombatLog((Player)event.getEntity()).addAttack(damager.getName(), damager, null, event.getDamage());
        }
        else // If the player wasn't damaged by an entity
        {
            String damage = "-";
            String source = "-";

            if (event.getCause() == DamageCause.BLOCK_EXPLOSION)
            {
                damage = "Explosion";
            }
            else if (event.getCause() == DamageCause.CONTACT)
            {
                damage = "Cactus";
            }
            else if (event.getCause() == DamageCause.CUSTOM)
            {
                damage = "Custom Damage";
            }
            else if (event.getCause() == DamageCause.DROWNING)
            {
                damage = "Drowning";
            }
            else if (event.getCause() == DamageCause.ENTITY_ATTACK)
            {
                damage = "Entity";
                source = "Attack";
            }
            else if (event.getCause() == DamageCause.ENTITY_EXPLOSION)
            {
                damage = "Explosion";
            }
            else if (event.getCause() == DamageCause.FALL)
            {
                damage = "Gravity";
            }
            else if (event.getCause() == DamageCause.FALLING_BLOCK)
            {
                damage = "Falling Block";
            }
            else if (event.getCause() == DamageCause.FIRE)
            {
                damage = "Fire";
            }
            else if (event.getCause() == DamageCause.FIRE_TICK)
            {
                damage = "Fire";
            }
            else if (event.getCause() == DamageCause.LAVA)
            {
                damage = "Lava";
            }
            else if (event.getCause() == DamageCause.LIGHTNING)
            {
                damage = "Zeus";
            }
            else if (event.getCause() == DamageCause.MAGIC)
            {
                damage = "Witchcraft";
            }
            else if (event.getCause() == DamageCause.MELTING)
            {
                damage = "Becoming a happy snowman";
            }
            else if (event.getCause() == DamageCause.POISON)
            {
                damage = "Poison";
            }
            else if (event.getCause() == DamageCause.PROJECTILE)
            {
                damage = "Projectile";
            }
            else if (event.getCause() == DamageCause.STARVATION)
            {
                damage = "Starvation";
            }
            else if (event.getCause() == DamageCause.SUFFOCATION)
            {
                damage = "Suffocation";
            }
            else if (event.getCause() == DamageCause.SUICIDE)
            {
                damage = "Suicide";
            }
            else if (event.getCause() == DamageCause.THORNS)
            {
                damage = "Thorns";
            }
            else if (event.getCause() == DamageCause.VOID)
            {
                damage = "Void";
            }
            else if (event.getCause() == DamageCause.WITHER)
            {
                damage = "Wither";
            }

            getCombatLog((Player)event.getEntity()).addAttack(damage, null, source, event.getFinalDamage());
        }
    }

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
            String damage = "-";
            String source = "-";

            if (event.getDamageCause() == DamageCause.BLOCK_EXPLOSION)
            {
                damage = "Explosion";
            }
            else if (event.getDamageCause() == DamageCause.CONTACT)
            {
                damage = "Cactus";
            }
            else if (event.getDamageCause() == DamageCause.CUSTOM)
            {
                damage = "Custom Damage";
            }
            else if (event.getDamageCause() == DamageCause.DROWNING)
            {
                damage = "Drowning";
            }
            else if (event.getDamageCause() == DamageCause.ENTITY_ATTACK)
            {
                damage = "Entity";
                source = "Attack";
            }
            else if (event.getDamageCause() == DamageCause.ENTITY_EXPLOSION)
            {
                damage = "Explosion";
            }
            else if (event.getDamageCause() == DamageCause.FALL)
            {
                damage = "Gravity";
            }
            else if (event.getDamageCause() == DamageCause.FALLING_BLOCK)
            {
                damage = "Falling Block";
            }
            else if (event.getDamageCause() == DamageCause.FIRE)
            {
                damage = "Fire";
            }
            else if (event.getDamageCause() == DamageCause.FIRE_TICK)
            {
                damage = "Fire";
            }
            else if (event.getDamageCause() == DamageCause.LAVA)
            {
                damage = "Lava";
            }
            else if (event.getDamageCause() == DamageCause.LIGHTNING)
            {
                damage = "Zeus";
            }
            else if (event.getDamageCause() == DamageCause.MAGIC)
            {
                damage = "Witchcraft";
            }
            else if (event.getDamageCause() == DamageCause.MELTING)
            {
                damage = "Becoming a happy snowman";
            }
            else if (event.getDamageCause() == DamageCause.POISON)
            {
                damage = "Poison";
            }
            else if (event.getDamageCause() == DamageCause.PROJECTILE)
            {
                damage = "Projectile";
            }
            else if (event.getDamageCause() == DamageCause.STARVATION)
            {
                damage = "Starvation";
            }
            else if (event.getDamageCause() == DamageCause.SUFFOCATION)
            {
                damage = "Suffocation";
            }
            else if (event.getDamageCause() == DamageCause.SUICIDE)
            {
                damage = "Suicide";
            }
            else if (event.getDamageCause() == DamageCause.THORNS)
            {
                damage = "Thorns";
            }
            else if (event.getDamageCause() == DamageCause.VOID)
            {
                damage = "Void";
            }
            else if (event.getDamageCause() == DamageCause.WITHER)
            {
                damage = "Wither";
            }

            getCombatLog(event.getPlayerDamagee()).addAttack(damage, null, source, event.getDamage());
        }
    }

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
        else if (messageEvent.getDeathMessageType() == DeathMessageType.DETAILED)
        {
            // Broadcasts the public death message
            Bukkit.broadcastMessage(log.getSimpleDeathMessage());

            // messages the dead player their death details
            CombatClient combatHistory = getCombatClient(event.getEntity().getName());
            String livingStats = F.C_EMPHASIS2 + combatHistory.getKillsSinceDeath() + " kills" + F.C_CONTENT + " - " +
                    F.C_EMPHASIS2 + combatHistory.getAssistsSinceDeath() + " assists";
            event.getEntity().sendMessage(F.componentMessage("Life Stats", livingStats));

            for (String detail : log.getDeathDetails())
            {
                event.getEntity().sendMessage(detail);
            }
        }

        // Logs player stats
        handleKillLog(log);

        // Resets a player's combat log
        _combatLogs.remove(event.getEntity());
    }

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
    public void handleKillLog(CombatLog log)
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

    public void setCombatLog(Player player, CombatLog combatLog)
    {
        _combatLogs.put(player, combatLog);
    }

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

    public void setCombatClient(String player, CombatClient combatClient)
    {
        _combatClients.put(player, combatClient);
    }

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
