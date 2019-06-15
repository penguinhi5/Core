package core.minecraft.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * This represents a region within the world.
 */
public abstract class Region {

    /**
     * The world this region exists in.
     */
    protected World world;

    /**
     * All of the sub-regions contained within this region.
     */
    private HashMap<String, Region> subRegions = new HashMap<>();

    /**
     * Contains all of the flags in the region.
     */
    private LinkedList<String> _flags = new LinkedList<>();

    /**
     * This region's priority compared to other regions when there are conflicting flags.
     *
     * The lower the number the higher the priority.
     */
    private int priority = 100;

    /**
     * The name of this region.
     */
    private String name;

    /**
     * The unique id of this region.
     */
    private String id;

    /**
     * Creates a new default region.
     */
    public Region(List<String> flags, String name, String id, World world)
    {
        _flags.addAll(flags);
        this.name = name;
        this.id = id;
        this.world = world;
    }

    /**
     * @return The number of blocks in the region
     */
    public abstract int blockCount();

    /**
     * @return A list of all the blocks in the region
     */
    public abstract List<Block> getBlocks();

    /**
     * Checks if the player with the given name is in the region.
     *
     * @param playerName The name of the player
     * @return true if the player is in the region, otherwise false.
     */
    public abstract boolean containsPlayer(String playerName);

    /**
     * Checks if the region contains the specified block.
     *
     * @param block The block being checked
     * @return true if the region contains the block, otherwise false.
     */
    public abstract boolean containsBlock(Block block);

    /**
     * Checks if the region contains the specified location.
     *
     * @param loc The location being checked
     * @return true if the region contains the location, otherwise false.
     */
    public abstract boolean containsLoc(Location loc);

    /**
     * Updates the priority of the region.
     *
     * @param priority The new priority
     */
    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    /**
     * Gets the priority of this region.
     *
     * @return This region's priority.
     */
    public int getPriority()
    {
        return priority;
    }

    /**
     * Checks if this region has the specified flag.
     *
     * @param flag The flag that is being searched for
     * @return true if the region contains the flag, otherwise false.
     */
    public boolean hasFlag(String flag)
    {
        return _flags.contains(flag.toLowerCase());
    }

    /**
     * Adds the flag to the existing list of flags.
     *
     * @param flag the flag that is being added
     */
    public void addFlag(String flag)
    {
        _flags.add(flag);
    }

    /**
     * @return The unique ID belonging to this region
     */
    public String getID()
    {
        return id;
    }

    /**
     * @return The name of this region
     */
    public String getName()
    {
        return name;
    }
}
