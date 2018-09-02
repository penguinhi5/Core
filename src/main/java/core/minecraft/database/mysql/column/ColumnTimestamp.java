package core.minecraft.database.mysql.column;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * This is a MySQL column that stores a {@link Timestamp}.
 *
 * @author Preston Brown
 */
public class ColumnTimestamp extends Column<Timestamp> {

    /**
     * Creates a new instance of Column with the given column name and data.
     *
     * @param name the name of the column
     * @param data the data that is stored in the column
     */
    public ColumnTimestamp(String name, Timestamp data)
    {
        super(name, data);
    }

    @Override
    public void updatePreparedStatement(PreparedStatement preparedStatement, int location) throws SQLException {
        preparedStatement.setTimestamp(location, _data);
    }
}
