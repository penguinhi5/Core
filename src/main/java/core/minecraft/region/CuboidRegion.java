package core.minecraft.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a cuboid region.
 */
public class CuboidRegion extends Region {

    private Location corner1, corner2, center;
    private int width, length, height;

    /**
     * Creates a new rectangular region using 2 corner locations.
     *
     * @param world The world this region is located in
     * @param corner1 The location of one corner in the region
     * @param corner2 The location of the opposite corner in the region
     */
    public CuboidRegion(List<String> flags, String name, String id, World world, Location corner1, Location corner2)
    {
        super(flags, name, id, world);

        this.corner1 = corner1;
        this.corner2 = corner2;
        this.width = Math.abs(corner1.getBlockX() - corner2.getBlockX());
        this.length = Math.abs(corner1.getBlockZ() - corner2.getBlockZ());
        this.height = Math.abs(corner1.getBlockY() - corner2.getBlockY());
        int centerX = (corner1.getBlockX() + corner2.getBlockX()) / 2;
        int centerY = (corner1.getBlockY() + corner2.getBlockY()) / 2;
        int centerZ = (corner1.getBlockZ() + corner2.getBlockZ()) / 2;
        center = new Location(world, centerX, centerY, centerZ);
    }

    /**
     * Creates a new rectangular region using the center block and the width, length, and height of the region.
     *
     * @param flags All of the flags associated with this region
     * @param name The name of this region
     * @param id The unique ID used to reference this region
     * @param world The world that this region is contained in
     * @param center The location of the center block
     * @param width The width (X) of the region
     * @param length The length (Z) of the region
     * @param height The height (Y) of the region
     */
    public CuboidRegion(List<String> flags, String name, String id, World world, Location center, int width, int length, int height)
    {
        super(flags, name, id, world);

        this.center = center;
        this.width = width;
        this.length = length;
        this.height = height;
        corner1 = new Location(world, center.getBlockX() - width / 2, center.getBlockY() - height / 2, center.getBlockZ() - width / 2);
        corner2 = new Location(world, center.getBlockX() + width / 2, center.getBlockY() + height / 2, center.getBlockZ() + width / 2);
    }

    @Override
    public int blockCount() {
        return width * length * height;
    }

    @Override
    public List<Block> getBlocks() {
        ArrayList<Block> blocks = new ArrayList<>();

        for (int x = center.getBlockX() - width / 2; x < center.getBlockX() + width / 2; x++)
        {
            for (int y = center.getBlockY() - height / 2; y < center.getBlockY() + height / 2; y++)
            {
                for (int z = center.getBlockZ() - length / 2; z < center.getBlockZ() + length / 2; z++)
                {
                    blocks.add(new Location(world, x, y, z).getBlock());
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
        // If the location is in the right world
        if (loc.getWorld() != world)
        {
            return false;
        }

        // If the location's X value falls outside of the rectangle's max and min X value
        if (center.getBlockX() - width / 2 > loc.getX() || center.getBlockX() + width / 2 < loc.getX())
        {
            return false;
        }

        // If the location's Y value falls outside of the rectangle's max and min Y value
        if (center.getBlockY() - height / 2 > loc.getY() || center.getBlockY() + height / 2 < loc.getY())
        {
            return false;
        }

        // If the location's Z value falls outside of the rectangle's max and min Z value
        if (center.getBlockZ() - length / 2 > loc.getZ() || center.getBlockZ() + length / 2 < loc.getZ())
        {
            return false;
        }
        return true;
    }

    /**
     * Updates the size of this region.
     *
     * @param center The location of the center block
     * @param width The width (X) of the region
     * @param length The length (Z) of the region
     * @param height The height (Y) of the region
     */
    public void updateSize(Location center, int width, int length, int height)
    {
        // Updates all of the values
        this.center = center;
        this.width = width;
        this.length = length;
        this.height = height;
        corner1 = new Location(world, center.getBlockX() - width / 2, center.getBlockY() - height / 2, center.getBlockZ() - width / 2);
        corner2 = new Location(world, center.getBlockX() + width / 2, center.getBlockY() + height / 2, center.getBlockZ() + width / 2);
    }

    /**
     * Updates the size of this region.
     *
     * @param corner1 The location of one corner in the region
     * @param corner2 The location of the opposite corner in the region
     */
    public void updateSize(Location corner1, Location corner2)
    {
        // Updates all of the values
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.width = Math.abs(corner1.getBlockX() - corner2.getBlockX());
        this.length = Math.abs(corner1.getBlockZ() - corner2.getBlockZ());
        this.height = Math.abs(corner1.getBlockY() - corner2.getBlockY());
        int centerX = (corner1.getBlockX() + corner2.getBlockX()) / 2;
        int centerY = (corner1.getBlockY() + corner2.getBlockY()) / 2;
        int centerZ = (corner1.getBlockZ() + corner2.getBlockZ()) / 2;
        center = new Location(world, centerX, centerY, centerZ);
    }
}
