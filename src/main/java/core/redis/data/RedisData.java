package core.redis.data;

/**
 * This represents a piece of data that is stored in a redis data repository.
 *
 * @author Preston Brown
 */
public interface RedisData {

    /**
     * Returns the name ID of this piece of data.
     *
     * @return the name ID of this piece of data
     */
    public String getNameID();
}
