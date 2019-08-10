package core.minecraft.combat.events;

import core.minecraft.ClientComponent;
import core.minecraft.combat.DeathMessageType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * This event is called when a player is killed in combat.
 */
public class CombatDeathEvent extends Event {

    private static final HandlerList _handlers = new HandlerList();

    private DeathMessageType _deathMessageType = DeathMessageType.DETAILED;
    private String _killerColor;
    private String _victimColor;
    private Player _player;
    private String _killer;

    public CombatDeathEvent(Player player, String killer, String killerColor, String victimColor)
    {
        _player = player;
        _killer = killer;
        _killerColor = killerColor;
        _victimColor = victimColor;
    }

    public void set_deathMessageType(DeathMessageType type)
    {
        _deathMessageType = type;
    }

    public DeathMessageType getDeathMessageType()
    {
        return _deathMessageType;
    }

    public Player getPlayer()
    {
        return _player;
    }

    public String getKiller()
    {
        return _killer;
    }

    public String getVictimColor()
    {
        return _victimColor;
    }

    public void setVictimColor(String victimColor)
    {
        _victimColor = victimColor;
    }

    public String getKillerColor()
    {
        return _killerColor;
    }

    public void setKillerColor(String killerColor)
    {
        _killerColor = killerColor;
    }

    @Override
    public HandlerList getHandlers() {
        return _handlers;
    }

    public static HandlerList getHandlerList()
    {
        return _handlers;
    }
}
