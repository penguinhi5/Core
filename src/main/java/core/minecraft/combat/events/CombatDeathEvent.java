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

    // The type of death message that will be dislayed
    private DeathMessageType _deathMessageType = DeathMessageType.DETAILED;
    // The color of the killers name in the death message
    private String _killerColor;
    // The color of the victim's name in the death message
    private String _victimColor;
    // The name of the player that dies
    private Player _player;
    // The name of the killer
    private String _killer;

    /**
     * Creates a new CombatDeathEvent instance.
     *
     * @param player the player that was killed
     * @param killer the name of the killer
     * @param killerColor the color of the killer name in chat
     * @param victimColor the color fo the victim's name in chat
     */
    public CombatDeathEvent(Player player, String killer, String killerColor, String victimColor)
    {
        _player = player;
        _killer = killer;
        _killerColor = killerColor;
        _victimColor = victimColor;
    }

    /**
     * Sets the DeathMessageType that will be displayed in chat.
     *
     * @param type the type of death message that should be displayed
     */
    public void setDeathMessageType(DeathMessageType type)
    {
        _deathMessageType = type;
    }

    /**
     * Gets the DeathMessageType that will be displayed.
     *
     * @return the type of death message that will be displayed
     */
    public DeathMessageType getDeathMessageType()
    {
        return _deathMessageType;
    }

    /**
     * Gets the player that was killed.
     *
     * @return the player that was killed
     */
    public Player getPlayer()
    {
        return _player;
    }

    /**
     * Gets the name of the killer.
     *
     * @return the name of the killer
     */
    public String getKiller()
    {
        return _killer;
    }

    /**
     * Gets the color of the victim's name in chat.
     *
     * @return the color of the victim's name
     */
    public String getVictimColor()
    {
        return _victimColor;
    }

    /**
     * Sets the color of the victim's name in chat.
     *
     * @param victimColor the new color of the victim's name in chat
     */
    public void setVictimColor(String victimColor)
    {
        _victimColor = victimColor;
    }

    /**
     * Gets the color of the killer's name in chat.
     *
     * @return the color of the killer's name
     */
    public String getKillerColor()
    {
        return _killerColor;
    }

    /**
     * Sets the color of the killer's name in chat.
     *
     * @param killerColor the new color of the killer's name in chat
     */
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
