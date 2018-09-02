package core.minecraft.database.mysql.column;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * This is a MySQL column that stores a BigInteger.
 *
 * @author Preston Brown
 */
public class ColumnBigInt extends Column<Long> {

    /**
     * Creates a new instance of Column with the given column name and data.
     *
     * @param name the name of the column
     * @param data the data that is stored in the column
     */
    public ColumnBigInt(String name, long data)
    {
        super(name, data);
    }

    @Override
    public void updatePreparedStatement(PreparedStatement preparedStatement, int location) throws SQLException {
        preparedStatement.setLong(location, _data);
    }
}
