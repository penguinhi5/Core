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
public class MuteCommand extends CommandBase<ChatManager> {

    public MuteCommand(ChatManager chatManager, ClientManager clientManager)
    {
        super(chatManager, clientManager, "mute", new String[] {}, Rank.SENIOR_MOD);
    }

    @Override
    public void execute(Player player, String[] args)
    {
        if (_plugin.isChatSilenced())
        {
            Bukkit.broadcastMessage(F.componentMessage("Chat", "Chat is no longer muted"));
            _plugin.unsilenceChat();
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
                _plugin.silenceChat(-1L);
                Bukkit.broadcastMessage(F.componentMessage("Chat", "Chat has been muted " + F.C_EMPHASIS + "forever"));
                return;
            }
            long silenceTime;
            try
            {
                silenceTime = Long.parseLong(args[0]) * 1000;
                _plugin.silenceChat(silenceTime);
                Bukkit.broadcastMessage(F.componentMessage("Chat", "Chat has been muted for " + F.C_EMPHASIS + SystemUtil.getWrittenTimeRemaining(silenceTime)));
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
