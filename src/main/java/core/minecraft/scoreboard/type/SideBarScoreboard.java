package core.minecraft.scoreboard.type;

import core.minecraft.scoreboard.ScoreManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

/**
 * This represents a generic scoreboard that is placed on the sidebar.
 */
public class SideBarScoreboard {

    private ScoreManager _scoreManager;
    private ScoreboardManager _scoreboardManager;
    private Scoreboard _scoreboard;
    private Objective _objective;
    private Score[] _scores;

    public void SideBarScoreboard(ScoreManager scoreManager, int lines)
    {
        _scoreManager = scoreManager;
        _scoreboardManager = Bukkit.getScoreboardManager();
        _scoreboard = _scoreboardManager.getNewScoreboard();
        _objective = _scoreboard.registerNewObjective("sidebar", "dummy");
        _objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        _objective.setDisplayName(_scoreManager.getDisplayName());
        _scores = new Score[lines];
        // Adds all of the lines to the scoreboard
        String scoreName = "";
        for (int i = 0; i < lines; i++)
        {
            _scores[i] = _objective.getScore(scoreName);
            scoreName += ChatColor.RESET;
            _scores[i].setScore(i);
        }
    }

    /**
     * Updates the size of the scoreboard.
     *
     * <p>
     * The updated scoreboard will not be shown until {@link #showToPlayer(Player)} or {@link #showToAllPlayers()}
     * are called.
     * </p>
     * @param size the new size of the scoreboard
     * @param reset if the scoreboard should be reset with the updated size
     */
    public void updateSize(int size, boolean reset)
    {
        Score[] oldScores = _scores;
        _scores = new Score[size];
        if (reset)
        {
            String scoreName = "";
            for (int i = 0; i < size; i++)
            {
                if (i < oldScores.length) // Updates the existing line if it already exists
                {
                    updateLine(i, "");
                }
                else // Creates a new line if it doesn't already exist
                {
                    _scores[i] = _objective.getScore(scoreName);
                    _scores[i].setScore(i);
                }
                scoreName += ChatColor.RESET;
            }
        }
        else
        {
            String scoreName = "";
            for (int i = 0; i < size; i++)
            {
                if (i < oldScores.length) // Updates the existing line if it already exists
                {
                    _scores[i] = oldScores[i];
                }
                else // Creates a new line if it doesn't already exist
                {
                    _scores[i] = _objective.getScore(scoreName);
                    _scores[i].setScore(i);
                }
                scoreName += ChatColor.RESET;
            }
        }
    }

    /**
     * Updates the specified line of text.
     *
     * <p>
     * The updated line of text will not be shown until {@link #showToPlayer(Player)} or {@link #showToAllPlayers()}
     * are called.
     * </p>
     * @param line the line number the text is being added to
     * @param text the text being placed on the line
     */
    public void updateLine(int line, String text)
    {
        // Ensures the line exists in the scoreboard
        if (line < _scores.length && line >= 0)
        {
            // Removes existing line in the scoreboard
            _scoreboard.resetScores(_scores[line].getEntry());
            // Adds reset characters to prevent duplicate entries
            String updText = text;
            for (int i = 0; i < line; i++)
            {
                updText += ChatColor.RESET;
            }
            // Adds new line to scoreboard
            _scores[line] = _objective.getScore(updText);
            _scores[line].setScore(line);
        }
    }

    /**
     * Displays the scoreboard to the specified player.
     *
     * @param player the player whose scoreboard is being updated
     */
    public void showToPlayer(Player player)
    {
        if (player.isOnline())
        {
            player.setScoreboard(_scoreboard);
        }
    }

    /**
     * Displays the scoreboard to all players.
     */
    public void showToAllPlayers()
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            player.setScoreboard(_scoreboard);
        }
    }

    public Scoreboard getScoreboard()
    {
        return _scoreboard;
    }
}
