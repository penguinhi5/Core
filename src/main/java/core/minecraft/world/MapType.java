package core.minecraft.world;

/**
 * Stores all of the different types of maps.
 *
 * @author Preston Brown
 */
public enum MapType {

    ALL(""),
    HUB("hub"),
    LOBBY("lobby"),
    TEAM_MINIGAME("team minigame"),
    SOLO_MINIGAME("solo minigame");

    private String _directoryName;

    /**
     * Creates a new MapType instance.
     *
     * @param directoryName the name of the directory worlds of this type are stored in
     */
    private MapType(String directoryName)
    {
        _directoryName = directoryName;
    }

    /**
     * This returns the name of the directory that the world of this MapType are stored in.
     *
     * @return the name of the directory worlds of this type are stored in
     */
    public String getDirectoryName()
    {
        return _directoryName;
    }
}
