package core.minecraft.region.type;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a spherical region.
 */
public class SphereRegion extends Region {

    /**
     * The center location of this sphere region.
     */
    private Location center;

    /**
     * The radius of this sphere region.
     */
    private int radius;

    /**
     * Creates a new spherical region using the center and radius of a sphere.
     *
     * @param flags All of the flags associated with this region
     * @param name The name of this region
     * @param id The unique ID used to reference this region
     * @param world The world that this region is contained in
     * @param center The center location of this sphere region
     * @param radius The radius of this sphere region
     */
    public SphereRegion(List<String> flags, String name, String id, World world, Location center, int radius)
    {
        super(flags, name, id, world);

        this.center = center;
        this.radius = radius;
    }

    @Override
    public int blockCount()
    {
        return 0;
    }

    @Override
    public List<Block> getBlocks()
    {
        int x0 = center.getBlockX();
        int y0 = center.getBlockY();
        int z0 = center.getBlockZ();
        LinkedList<Block> blocks = new LinkedList<>();

        for (int x = x0 - radius; x <= x0 + radius; x++)
        {
            for (int y = y0 - radius; y <= y0 + radius; y++)
            {
                for (int z = z0 - radius; z <= z0 + radius; z++)
                {
                    // Computes the distance between the current coordinate and the center block using Pythagorean's Theorem
                    // Note: Subtract 0.5 because getBlock() moves the location 0.5 in all directions
                    double distance = ((x0 - x - 0.5) * (x0 - x - 0.5)) + ((y0 - y - 0.5) * (y0 - y - 0.5)) + ((z0 - z - 0.5) * (z0 - z - 0.5));

                    if (distance <= radius * radius)
                    {
                        blocks.add(new Location(world, x, y, z).getBlock());
                    }
                }
            }
        }
        return blocks;
    }

    @Override
    public boolean containsPlayer(String playerName)
    {
        Player player = Bukkit.getPlayer(playerName);

        // If a player exists with the given name
        if (player != null)
        {
            return containsLoc(player.getLocation());
        }

        return false;
    }

    @Override
    public boolean containsBlock(Block block)
    {
        return containsLoc(block.getLocation());
    }

    @Override
    public boolean containsLoc(Location loc)
    {
        // Ensures the location is in the same world
        if (loc.getWorld() != world)
        {
            return false;
        }

        // Computes the distance between the current coordinate and the center block
        int x0 = center.getBlockX();
        int y0 = center.getBlockY();
        int z0 = center.getBlockZ();

        // Pythagorean's theorem
        // Note: Subtract 0.5 because getBlock() moves the location 0.5 in all directions
        double distance = (x0 - loc.getBlockX() - 0.5) * (x0 - loc.getBlockX() - 0.5)
                + (y0 - loc.getBlockY() - 0.5) * (y0 - loc.getBlockY() - 0.5)
                + (z0 - loc.getBlockZ() - 0.5) * (z0 - loc.getBlockZ() - 0.5);

        if (distance <= radius * radius)
        {
            return true;
        }
        return false;
    }

    /**
     * Updates the size of the region.
     *
     * @param center The center location of this sphere region
     * @param radius The radius of this sphere region
     */
    public void updateSize(Location center, int radius)
    {
        this.center = center;
        this.radius = radius;
    }
}
