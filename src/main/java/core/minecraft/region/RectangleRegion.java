package core.minecraft.region;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RectangleRegion extends Region {

    private Location corner1, corner2;
    private int centerX, centerY, centerZ, width, length, height;

    /**
     * Creates a new rectangular region using 2 corner locations.
     *
     * @param world The world this region is located in
     * @param corner1 The location of one corner in the region
     * @param corner2 The location of the opposite corner in the region
     */
    public RectangleRegion(List<String> flags, String name, String id, World world,
                           Location corner1, Location corner2)
    {
        super(flags, name, id, world);

        this.corner1 = corner1;
        this.corner2 = corner2;
        this.width = Math.abs(corner1.getBlockX() - corner2.getBlockX());
        this.length = Math.abs(corner1.getBlockZ() - corner2.getBlockZ());
        this.height = Math.abs(corner1.getBlockY() - corner2.getBlockY());
        this.centerX = (corner1.getBlockX() + corner2.getBlockX()) / 2;
        this.centerY = (corner1.getBlockY() + corner2.getBlockY()) / 2;
        this.centerZ = (corner1.getBlockZ() + corner2.getBlockZ()) / 2;
    }

    /**
     * Creates a new rectangular region using the center block and the width, length, and height of the region.
     *
     * @param world The world that this region is contained in
     * @param centerX The X coordinate of the center block
     * @param centerY The Y coordinate of the center block
     * @param centerZ The Z coordinate of the center block
     * @param width The width (X) of the region
     * @param length The length (Z) of the region
     * @param height The height (Y) of the region
     */
    public RectangleRegion(List<String> flags, String name, String id, World world,
                           int centerX, int centerY, int centerZ, int width, int length, int height)
    {
        super(flags, name, id, world);

        this.centerX = centerX;
        this.centerY = centerY;
        this.centerZ = centerZ;
        this.width = width;
        this.length = length;
        this.height = height;
        corner1 = new Location(world, centerX - width / 2, centerY - height / 2, centerZ - width / 2);
        corner2 = new Location(world, centerX + width / 2, centerY + height / 2, centerZ + width / 2);
    }

    @Override
    public int blockCount() {
        return width * length * height;
    }

    @Override
    public List<Block> getBlocks() {
        ArrayList<Block> blocks = new ArrayList<>();

        for (int x = centerX - width / 2; x < centerX + width / 2; x++)
        {
            for (int y = centerY - height / 2; y < centerY + height / 2; y++)
            {
                for (int z = centerZ - length / 2; z < centerZ + length / 2; z++)
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
    protected boolean containsLoc(Location loc)
    {
        // If the location's X value falls outside of the rectangle's max and min X value
        if (centerX - width > loc.getX() || centerX + width < loc.getX())
        {
            return false;
        }

        // If the location's Y value falls outside of the rectangle's max and min Y value
        if (centerY - height > loc.getY() || centerY + height < loc.getY())
        {
            return false;
        }

        // If the location's Z value falls outside of the rectangle's max and min Z value
        if (centerZ - length > loc.getZ() || centerZ + length < loc.getZ())
        {
            return false;
        }

        return true;
    }
}
