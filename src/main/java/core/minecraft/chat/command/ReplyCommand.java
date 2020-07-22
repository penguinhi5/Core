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
public class ReplyCommand extends CommandBase {

    private ChatManager _chatManager;

    public ReplyCommand(ChatManager chatManager, ClientManager clientManager)
    {
        super(clientManager, "reply", new String[] {"r"}, false, Rank.DEFAULT);
        _chatManager = chatManager;
    }


    @Override
    public void execute(Player player, String[] args)
    {
        if (args.length <= 0)
        {
            player.sendMessage(getProperUsageMessage());
            return;
        }

        String receiverName = _chatManager.getLastMessage(player.getName());
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

    @Override
    public String getHelpCommandMessage()
    {
        String dsc = "Sends a response to the last player that you messaged.";
        return F.helpCommandMessage("/r <message>", dsc);
    }
}
