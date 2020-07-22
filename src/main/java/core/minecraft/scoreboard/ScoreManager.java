package core.minecraft.scoreboard;

import core.minecraft.ClientComponent;
import core.minecraft.Component;
import core.minecraft.command.CommandManager;
import core.minecraft.scoreboard.type.SideBarScoreboard;
import core.minecraft.timer.TimerType;
import core.minecraft.timer.event.TimerEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;

public class ScoreManager extends ClientComponent<SideBarScoreboard> implements Listener {

    private ScoreboardManager _scoreManager;
    private SideBarScoreboard _globalSideBarScoreboard;
    private HashMap<Player, SideBarScoreboard> _playerSideBarScoreboards = new HashMap<>();
    private TimerType _updateSpeed;

    public ScoreManager(JavaPlugin plugin, CommandManager commandManager)
    {
        super("Scoreboard", plugin, commandManager);
        Bukkit.getPluginManager().registerEvents(this, getPlugin());
        _scoreManager = Bukkit.getScoreboardManager();
    }

    /**
     * Sets the global scoreboard that shows up for all players without a custom scoreboard.
     * @param scoreboard
     */
    public void setGlobalSideBarScoreboard(SideBarScoreboard scoreboard)
    {
        _globalSideBarScoreboard = scoreboard;
    }

    public void updateScoreboardRefreshRate(TimerType type)
    {
        _updateSpeed = type;
    }

    public TimerType getRefreshRate()
    {
        return _updateSpeed;
    }

    /**
     * Gets the display name at the top of the scoreboard.
     */
    public String getDisplayName()
    {
        return "" + ChatColor.AQUA + ChatColor.BOLD + "SERVER NAME";
    }

    @EventHandler
    public void refreshScoreboards(TimerEvent event)
    {
        if (_updateSpeed != null && _updateSpeed == event.getType())
        {
            for (Player player : Bukkit.getOnlinePlayers())
            {
                if (getPlayerData(player) != null)
                {
                    player.setScoreboard(getPlayerData(player).getScoreboard());
                }
                else
                {
                    player.setScoreboard(_globalSideBarScoreboard.getScoreboard());
                }
            }
        }
    }
}
