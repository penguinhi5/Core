package core.minecraft.database.mysql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MySQL handleCommand that returns a {@link ResultSet} when a MySQL query is ran.
 *
 * @author Preston Brown
 */
public interface ResultSetCallable {

    /**
     * The handleCommand that is ran when a {@link ResultSet} is returned.
     *
     * @param resultSet the {@link ResultSet} that is being returned
     * @throws SQLException
     */
    public void call(ResultSet resultSet) throws SQLException;
}
