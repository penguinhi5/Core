package core.redis.repository;

import core.redis.data.RedisData;

import java.util.List;

/**
 * This is the outline for a redis repository that stores some type of data.
 *
 * @author Preston Brown
 */
public interface DataRepository<DataType extends RedisData> {

    /**
     * Returns the most up to date {@link DataType} object.
     *
     * @param data the data that is being retrieved from the server
     * @return the most up to date {@link DataType} object
     */
    public DataType getData(DataType data);

    /**
     * Returns the {@link DataType} object with the given data ID.
     *
     * @param ID the ID that is being looked up in this repository
     * @return the {@link DataType} object with the given ID, if no DataType object exists with the ID null is returned
     */
    public DataType getData(String ID);

    /**
     * Adds the given {@link DataType} to this redis repository. The timeout will be set to whatever
     * the default timeout time is for this {@link DataType}. Once the default timeout time has been
     * reached data will be removed from the repository.
     *
     * <p>The default timeout time is 1 day.</p>
     *
     * @param data the data that is being added into the repository
     */
    public void addData(DataType data);

    /**
     * Adds the given {@link DataType} to this redis repository with the given timeout time. Once the specified
     * amount of time has been reached data will be removed from the repository.
     *
     * @param data the data this is being added into the repository
     * @param timeout the amount of time in seconds that must pass in order to automatically remove data from the repository
     */
    public void addData(DataType data, int timeout);

    /**
     * Removes the given {@link DataType} object from this repository if it exists.
     *
     * @param data the data that is being removed from this repository
     */
    public void removeData(DataType data);

    /**
     * Removes the {@link DataType} object from this repository if it exists.
     *
     * @param ID the ID that is being removed from this repository
     */
    public void removeData(String ID);

    /**
     * Checks if the specified data exists in this repository. Returns true if the specified data exists in this
     * repository, otherwise false.
     *
     * @param data the {@link DataType} that is being looked up in this repository
     * @return true if data exists in this repository, otherwise false
     */
    public boolean exists(DataType data);

    /**
     * Checks if the specified ID exists in this repository. Returns true if the specified ID exists in this
     * repository, otherwise false.
     *
     * @param ID the ID that is being looked up in this repository
     * @return true if ID exists in this repository, otherwise false
     */
    public boolean exists(String ID);
}
