package core.minecraft.database.mysql.column;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This is a MySQL column that stores a String.
 *
 * @author Preston Brown
 */
public class ColumnVarchar extends Column<String> {

    /**
     * Creates a new instance of Column with the given column name and data.
     *
     * @param name the name of the column
     * @param data the data that is stored in the column
     */
    public ColumnVarchar(String name, String data)
    {
        super(name, data);
    }

    @Override
    public void updatePreparedStatement(PreparedStatement preparedStatement, int location) throws SQLException
    {
        preparedStatement.setString(location, _data);
    }
}
