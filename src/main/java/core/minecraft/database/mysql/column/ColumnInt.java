package core.minecraft.database.mysql.column;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This is a MySQL column that stores an integer.
 *
 * @author Preston Brown
 */
public class ColumnInt extends Column<Integer> {

    /**
     * Creates a new instance of Column with the given column name and data.
     *
     * @param name the name of the column
     * @param data the data that is stored in the column
     */
    public ColumnInt(String name, int data)
    {
        super(name, data);
    }

    @Override
    public void updatePreparedStatement(PreparedStatement preparedStatement, int location) throws SQLException {
        preparedStatement.setInt(location, _data);
    }
}
