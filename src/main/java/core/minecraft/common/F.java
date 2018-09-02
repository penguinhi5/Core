package core.minecraft.common;

import net.md_5.bungee.api.ChatColor;

/**
 * Stores all chat formatting to ensure that in-game messages remain consistent.
 *
 * @author Preston Brown
 */
public class F {

    public static final String BOLD = ChatColor.BOLD.toString();

    public static final String C_PREFIX = ChatColor.GREEN.toString();
    public static final String C_ERROR_PREFIX = ChatColor.DARK_RED.toString();
    public static final String C_CONTENT = ChatColor.GRAY.toString();
    public static final String C_EMPHASIS = ChatColor.WHITE.toString();

    /**
     * Generates a message that will be sent by a component in the proper format
     *
     * @param prefix the prefix that will show up in chat
     * @param message the message that is being sent
     * @return the complete component message
     */
    public static String componentMessage(String prefix, String message)
    {
        String msg = C_PREFIX + "[" + prefix + "] " + C_CONTENT + message;
        return msg;
    }

    /**
     * Generates an error message in the proper format.
     *
     * @param message the error message that is being sent
     * @return the complete error message
     */
    public static String errorMessage(String message)
    {
        String msg = C_ERROR_PREFIX + BOLD + "(!) " + ChatColor.RESET + C_CONTENT + message;
        return msg;
    }

    /**
     * Generates a message informing the player that they have insufficient permissions
     * and tells them the rank required to perform the specified task.
     *
     * @param requiredRank
     * @return the complete insufficient rank message
     */
    public static String insufficientRankMessage(Rank requiredRank)
    {
        String msg = C_ERROR_PREFIX + BOLD + "(!) " + C_CONTENT + "Insufficient Rank - You must have the rank " +
                requiredRank.getDisplayName(false, true, true) +
                C_CONTENT + " to complete this action";
        return msg;
    }

    /**
     * Generates the message that should be sent to a player informing them of the proper usage of a command.
     *
     * @param usage the proper format to execute a command
     * @param example an example of the command in use
     * @return the complete proper command usage message
     */
    public static String properCommandUsageMessage(String usage, String example)
    {
        String msg = C_PREFIX + "[" + "Command" + "] " + C_CONTENT + "Proper Usage: " + C_EMPHASIS + usage + C_CONTENT + ".\n" +
                C_PREFIX + "[" + "Command" + "] " + C_CONTENT + "Example: " + C_EMPHASIS + example + C_CONTENT + ".";
        return msg;
    }
}