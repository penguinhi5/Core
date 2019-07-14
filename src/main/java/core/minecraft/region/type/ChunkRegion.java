package core.minecraft.region.type;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a region made up of multiple chunks.
 */
public class ChunkRegion extends Region {

    /**
     * A list of all the chunks contained in this region serialized in the form "x,z"
     */
    private ArrayList<String> chunks = new ArrayList<>();

    /**
     * Creates a new chunk region defined by chunks.
     *
     * @param flags All of the flags associated with this region
     * @param name The name of this region
     * @param id The unique ID used to reference this region
     * @param world The world that this region is contained in
     * @param chunks The chunks being added to the region
     */
    public ChunkRegion(List<String> flags, String name, String id, World world, List<Chunk> chunks)
    {
        super(flags, name, id, world);

        for (Chunk chunk : chunks)
        {
            addChunk(chunk);
        }
    }

    /**
     * Adds the chunk at the specified chunk grid position to the region.
     *
     * @param chunk the chunk being added
     */
    public void addChunk(Chunk chunk)
    {
        chunks.add(chunk.getX() + "," + chunk.getZ());
    }

    /**
     * Removes the chunk at the specified chunk grid position from the region.
     *
     * @param chunk The chunk being removed
     */
    public void removeChunk(Chunk chunk)
    {
        chunks.remove(chunk.getX() + "," + chunk.getZ());
    }

    @Override
    public int blockCount()
    {
        return 65536 * chunks.size();
    }

    @Override
    public List<Block> getBlocks()
    {
        ArrayList<Block> blocks = new ArrayList<>();

        for (String chunkStr : chunks)
        {
            // De-serializes the chunk
            int xCoord = Integer.parseInt(chunkStr.split(",")[0]);
            int zCoord = Integer.parseInt(chunkStr.split(",")[1]);
            Chunk chunk = new Location(world, xCoord * 16, 0, zCoord * 16).getChunk();

            // Adds all of the blocks in the chunk
            for (int x = 0; x < 16; x++)
            {
                for (int y = 0; y < 256; y++)
                {
                    for (int z = 0; z < 16; z++)
                    {
                        blocks.add(chunk.getBlock(x, y, z));
                    }
                }
            }
        }

        return blocks;
    }

    /**
     * @return all of the chunks in this region
     */
    public List<Chunk> getChunks()
    {
        ArrayList<Chunk> chunkList = new ArrayList<>();

        for (String chunkStr : chunks)
        {
            // De-serializes the chunk
            int xCoord = Integer.parseInt(chunkStr.split(",")[0]);
            int zCoord = Integer.parseInt(chunkStr.split(",")[1]);

            // Adds the chunk to the list
            chunkList.add(new Location(world, xCoord * 16, 0, zCoord * 16).getChunk());
        }

        return chunkList;
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
        return chunks.contains(loc.getChunk().getX() + "," + loc.getChunk().getZ()) && loc.getWorld() == world;
    }
}
