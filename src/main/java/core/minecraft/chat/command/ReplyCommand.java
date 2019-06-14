package core.minecraft.chat.command;

import core.minecraft.chat.ChatManager;
import core.minecraft.client.ClientManager;
import core.minecraft.command.CommandBase;
import core.minecraft.common.F;
import core.minecraft.common.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * This command sends a message to the last player that another player sent a message to.
 *
 * @author Preston Brown
 */
public class ReplyCommand extends CommandBase<ChatManager> {

    public ReplyCommand(ChatManager chatManager, ClientManager clientManager)
    {
        super(chatManager, clientManager, "reply", new String[] {"r"}, Rank.DEFAULT);
    }


    @Override
    public void execute(Player player, String[] args)
    {
        if (args.length <= 0)
        {
            player.sendMessage(getProperUsageMessage());
            return;
        }

        String receiverName = _plugin.getLastMessage(player.getName());
        if (receiverName.equals(""))
        {
            player.sendMessage(F.componentMessage("Message", "You haven't messaged anyone yet"));
            return;
        }
        Player receiver = Bukkit.getPlayer(receiverName);
        if (receiver != null)
        {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ChatColor.GREEN + ChatColor.BOLD.toString() + "-> " +
                    ChatColor.DARK_GREEN + ChatColor.BOLD + receiver.getName() + " " + ChatColor.GREEN + ChatColor.BOLD);
            for (int i = 0; i < args.length; i++)
            {
                stringBuilder.append(args[i] + " ");
            }
            player.sendMessage(stringBuilder.toString());

            stringBuilder = new StringBuilder();
            stringBuilder.append(ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + player.getName() + " " + ChatColor.GREEN + ChatColor.BOLD);
            for (int i = 0; i < args.length; i++)
            {
                stringBuilder.append(args[i] + " ");
            }
            receiver.sendMessage(stringBuilder.toString());
        }
        else
        {
            player.sendMessage(F.componentMessage("Message", "This player is no longer online"));
        }
    }

    @Override
    public String getProperUsageMessage()
    {
        String usage = "/r <message>";
        String example = "/r I'm doing great!!!";
        return F.properCommandUsageMessage(usage, example);
    }
}
