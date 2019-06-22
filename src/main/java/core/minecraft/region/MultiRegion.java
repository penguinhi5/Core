package core.minecraft.region;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a region made up of multiple regions.
 */
public class MultiRegion extends Region {

    /**
     * All of the subregions that make up this region.
     */
    private ArrayList<Region> subregions = new ArrayList<>();

    /**
     * Creates a new spherical region using the center and radius of a sphere.
     *
     * @param flags All of the flags associated with this region
     * @param name The name of this region
     * @param id The unique ID used to reference this region
     * @param world The world that this region is contained in
     * @param subregions The subregions that create this region
     */
    public MultiRegion(List<String> flags, String name, String id, World world, List<Region> subregions)
    {
        super(flags, name, id, world);

        this.subregions.addAll(subregions);
    }

    /**
     * Adds a subregion to this MultiRegion.
     *
     * @param region The region being added to this MultiRegion
     */
    public void addRegion(Region region)
    {
        subregions.add(region);
    }

    /**
     * Removes a subregion to this MultiRegion
     *
     * @param region The region being removed from this MultiRegion
     */
    public void removeRegion(Region region)
    {
        subregions.remove(region);
    }

    @Override
    public int blockCount()
    {
        int count = 0;

        // Counts the blocks in all of the subregions
        for (Region region : subregions)
        {
            count += region.blockCount();
        }

        return count;
    }

    @Override
    public List<Block> getBlocks()
    {
        ArrayList<Block> blocks = new ArrayList<>();

        // Grabs all of the blocks from all of the subregions
        for (Region region : subregions)
        {
            blocks.addAll(region.getBlocks());
        }

        return blocks;
    }

    @Override
    public boolean containsPlayer(String playerName)
    {
        // Checks if any region contains the player
        for (Region region : subregions)
        {
            if (region.containsPlayer(playerName))
                return true;
        }

        return false;
    }

    @Override
    public boolean containsBlock(Block block)
    {
        // Checks if any region contains the Block
        for (Region region : subregions)
        {
            if (region.containsBlock(block))
                return true;
        }

        return false;
    }

    @Override
    public boolean containsLoc(Location loc)
    {
        // Checks if any region contains the location
        for (Region region : subregions)
        {
            if (region.containsLoc(loc))
                return true;
        }

        return false;
    }
}
