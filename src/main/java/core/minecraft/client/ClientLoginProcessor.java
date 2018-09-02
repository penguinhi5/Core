package core.minecraft.client;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This represents a MySQL process that should be ran anytime a client logs into the connection.
 *
 * @author Preston Brown
 */
public interface ClientLoginProcessor {

    /**
     * Gets the name of this login process.
     *
     * @return the name of this login process
     */
    public String getName();

    /**
     * Gets the query that should be ran every time a player logs in.
     *
     * @param name the players name
     * @param uuid the players uuid
     * @return the query that will be ran by the mysql database
     */
    public String getQuery(String name, String uuid);

    /**
     * This processes the {@link ResultSet} returned when a player logs in and the query is executed.
     *
     * @param resultSet the {@link ResultSet} that is being returned after the query executes
     * @throws SQLException
     */
    public void processResultSet(ResultSet resultSet) throws SQLException;
}
