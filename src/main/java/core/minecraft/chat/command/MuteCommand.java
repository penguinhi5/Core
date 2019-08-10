package core.minecraft.chat.command;

import core.minecraft.chat.ChatManager;
import core.minecraft.client.ClientManager;
import core.minecraft.command.CommandBase;
import core.minecraft.common.F;
import core.minecraft.common.Rank;
import core.minecraft.common.utils.SystemUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * This command silences the chat.
 *
 * @author Preston Brown
 */
public class MuteCommand extends CommandBase {

    private ChatManager _chatManager;

    public MuteCommand(ChatManager chatManager, ClientManager clientManager)
    {
        super(clientManager, "mute", new String[] {}, Rank.SENIOR_MOD);
        _chatManager = chatManager;
    }

    @Override
    public void execute(Player player, String[] args)
    {
        if (_chatManager.isChatSilenced())
        {
            Bukkit.broadcastMessage(F.componentMessage("Chat", "Chat is no longer muted"));
            _chatManager.unsilenceChat();
        }
        else
        {
            if (args.length > 1)
            {
                player.sendMessage(getProperUsageMessage());
                return;
            }
            else if (args.length < 1)
            {
                _chatManager.silenceChat(-1L);
                Bukkit.broadcastMessage(F.componentMessage("Chat", "Chat has been muted " + F.C_EMPHASIS1 + "forever"));
                return;
            }
            long silenceTime;
            try
            {
                silenceTime = Long.parseLong(args[0]) * 1000;
                _chatManager.silenceChat(silenceTime);
                Bukkit.broadcastMessage(F.componentMessage("Chat", "Chat has been muted for " + F.C_EMPHASIS1 + SystemUtil.getWrittenTimeRemaining(silenceTime)));
            }
            catch (NumberFormatException e)
            {
                player.sendMessage(getProperUsageMessage());
                return;
            }
        }
    }

    @Override
    public String getProperUsageMessage()
    {
        String usage = "/silence [seconds]";
        String example = "/silence 60";
        return F.properCommandUsageMessage(usage, example);
    }
}
