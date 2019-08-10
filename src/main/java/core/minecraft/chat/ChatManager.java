package core.minecraft.chat;

import core.minecraft.Component;
import core.minecraft.chat.command.MessageCommand;
import core.minecraft.chat.command.ReplyCommand;
import core.minecraft.chat.command.MuteCommand;
import core.minecraft.client.ClientManager;
import core.minecraft.command.CommandManager;
import core.minecraft.common.F;
import core.minecraft.common.Rank;
import core.minecraft.common.utils.SystemUtil;
import core.minecraft.cooldown.Cooldown;
import core.minecraft.cooldown.event.CooldownCompletedEvent;
import core.minecraft.timer.TimerType;
import core.minecraft.timer.event.TimerEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

/**
 * Manages the Minecraft chat.
 *
 * @author Preston Brown
 */
public class ChatManager extends Component implements Listener {

    private ClientManager _clientManager;
    private boolean _permSilenced;
    private HashMap<String, String> _messageHistory = new HashMap<>();

    /**
     * This creates a new {@link ChatManager} instance.
     *
     * @param plugin the main JavaPlugin instance
     */
    public ChatManager(JavaPlugin plugin, ClientManager clientManager, CommandManager commandManager)
    {
        super("Chat", plugin, commandManager);
        _clientManager = clientManager;
        addCommands();

        Bukkit.getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event)
    {
        Player speaker = event.getPlayer();
        Rank rank = _clientManager.getPlayerData(speaker).getRank();
        event.setFormat(rank.getDisplayName() + ChatColor.RESET + " " + speaker.getName() + F.BOLD + "≫ " + ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', event.getMessage()));

        long silencedTime = Cooldown.getInstance().getCooldownTime("silence chat");
        if (!rank.hasRank(speaker, Rank.MOD))
        {
            if (_permSilenced)
            {
                speaker.sendMessage(F.componentMessage("Chat", "Chat is muted " + F.C_EMPHASIS1 + "forever"));
                event.setCancelled(true);
                return;
            }
            else if (silencedTime > 0)
            {
                speaker.sendMessage(F.componentMessage("Chat", "Chat is muted for " + F.C_EMPHASIS1 + SystemUtil.getWrittenTimeRemaining(silencedTime)));
                event.setCancelled(true);
                return;
            }
        }

    }

    /**
     * Silences the chat for the specified amount of time in milliseconds. If time < 1 chat will be silenced forever.
     *
     * @param time the amount of time chat will be silenced in milliseconds
     */
    public void silenceChat(long time)
    {
        if (time < 1)
        {
            _permSilenced = true;
        }
        else
        {
            Cooldown.getInstance().createCooldown("silence chat", time);
        }
    }

    /**
     * Silences the chat forever.
     */
    public void silenceChat()
    {
        _permSilenced = true;
    }

    /**
     * Unsilences the chat
     */
    public void unsilenceChat()
    {
        _permSilenced = false;
        Cooldown.getInstance().cancelCooldown("silence chat");
    }

    /**
     * Returns whether or not chat is currently muted.
     *
     * @return true if chat is currently muted, otherwise false
     */
    public boolean isChatSilenced()
    {
        if (_permSilenced)
        {
            return true;
        }
        return Cooldown.getInstance().hasCooldown("silence chat");
    }

    /**
     * Updates the player join message.
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        event.setJoinMessage(ChatColor.GREEN + ChatColor.BOLD.toString() + "+ " + ChatColor.RESET + ChatColor.GRAY + event.getPlayer().getName());
    }

    /**
     * Updates the player leave message and clears any data the player left behind.
     */
    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event)
    {
        event.setQuitMessage(ChatColor.RED + ChatColor.BOLD.toString() + "- " + ChatColor.RESET + ChatColor.GRAY + event.getPlayer().getName());
        _messageHistory.remove(event.getPlayer());
    }

    private void addCommands()
    {
        addCommand(new MessageCommand(this, _clientManager));
        addCommand(new ReplyCommand(this, _clientManager));
        addCommand(new MuteCommand(this, _clientManager));
    }

    /**
     * Sets the last player that sender messaged.
     *
     * @param sender the player that sent the message
     * @param receiver the player that received the message
     */
    public void updateMessageHistory(String sender, String receiver)
    {
        _messageHistory.put(sender, receiver);
    }

    /**
     * Gets the last player that sender messaged if there is one. If sender hasn't sent a message before an empty String
     * will be returned.
     *
     * @param sender the sender's name
     * @return the last player that sender messaged if there is one, otherwise an empty string is returned
     */
    public String getLastMessage(String sender)
    {
        String receiver = "";
        if (_messageHistory.containsKey(sender))
        {
            receiver = _messageHistory.get(sender);
        }
        return receiver;
    }

    @EventHandler
    public void cleanMessageHistory(TimerEvent event)
    {
        if (event.getType() != TimerType.FIVE_MINUTES)
        {
            return;
        }

        for (String player : _messageHistory.keySet())
        {
            if (!Bukkit.getOnlinePlayers().contains(player))
            {
                _messageHistory.remove(player);
            }
        }
    }

    @EventHandler
    public void chatUnsilenceEvent(CooldownCompletedEvent event)
    {
        if (event.getName().equals("silence chat"))
        {
            Bukkit.broadcastMessage(F.componentMessage("Chat", "Chat is no longer muted"));
            unsilenceChat();
        }
    }
}
