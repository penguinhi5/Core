package core.minecraft.chat.command;

import core.minecraft.client.ClientManager;
import core.minecraft.command.CommandBase;
import core.minecraft.command.CommandManager;
import core.minecraft.common.F;
import core.minecraft.common.Rank;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HelpCommand extends CommandBase {

    private CommandManager _commandManager;

    public HelpCommand(CommandManager commandManager, ClientManager clientManager)
    {
        super(clientManager, "help", new String[] {"h"}, false, Rank.DEFAULT);
        _commandManager = commandManager;
    }

    @Override
    public void execute(Player player, String[] args)
    {
        if (args.length > 1)
        {
            player.sendMessage(getProperUsageMessage());
            return;
        }

        int page = 1;
        // Ensures the player's argument is a number
        if (args.length == 1)
        {
            try
            {
                page = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e)
            {
                player.sendMessage(getProperUsageMessage());
                return;
            }
        }

        _commandManager.sendCommandListToPlayer(player, page);
    }

    @Override
    public String getProperUsageMessage()
    {
        String usage = "/help [page]";
        String example = "/help";
        return F.properCommandUsageMessage(usage, example);
    }

    @Override
    public String getHelpCommandMessage()
    {
        String dsc = "Gets a description of all the available commands.";
        return F.helpCommandMessage("/help [page]", dsc);
    }
}
