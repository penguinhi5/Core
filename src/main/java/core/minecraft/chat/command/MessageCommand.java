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
 * This command sends a message from one player to another.
 *
 * @author Preston Brown
 */
public class MessageCommand extends CommandBase {

    private ChatManager _chatManager;

    public MessageCommand(ChatManager chatManager, ClientManager clientManager)
    {
        super(clientManager, "message", new String[] {"msg"}, false, Rank.DEFAULT);
        _chatManager = chatManager;
    }

    @Override
    public void execute(Player player, String[] args)
    {
        if (args.length < 2)
        {
            player.sendMessage(getProperUsageMessage());
            return;
        }

        String receiverName = args[0];
        Player receiver = Bukkit.getPlayer(receiverName);
        if (receiver != null)
        {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ChatColor.GREEN + ChatColor.BOLD.toString() + "-> " +
                    ChatColor.DARK_GREEN + ChatColor.BOLD + receiver.getName() + " " + ChatColor.GREEN + ChatColor.BOLD);
            for (int i = 1; i < args.length; i++)
            {
                stringBuilder.append(args[i] + " ");
            }
            player.sendMessage(stringBuilder.toString());

            stringBuilder = new StringBuilder();
            stringBuilder.append(ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + player.getName() + " " + ChatColor.GREEN + ChatColor.BOLD);
            for (int i = 1; i < args.length; i++)
            {
                stringBuilder.append(args[i] + " ");
            }
            receiver.sendMessage(stringBuilder.toString());
            _chatManager.updateMessageHistory(player.getName(), receiver.getName());
        }
        else
        {
            player.sendMessage(F.componentMessage("Message", "This player is not currently online"));
        }
    }

    @Override
    public String getProperUsageMessage()
    {
        String usage = "/msg <player> <message>";
        String example = "/msg rando1812 how are you doing?";
        return F.properCommandUsageMessage(usage, example);
    }

    @Override
    public String getHelpCommandMessage()
    {
        return F.helpCommandMessage("/message <player> <message>", "Sends a message that will only be seen by the specified player.");
    }
}
