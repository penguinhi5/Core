package core.minecraft.database.mysql.column;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Represents a column of MySQL data
 *
 * @author Preston Brown
 */
public abstract class Column<DataType> {

    public String _name;
    public DataType _data;

    /**
     * Creates a new instance of Column with the given column name and data.
     *
     * @param name the name of the column
     * @param data the data that is stored in the column
     */
    public Column(String name, DataType data)
    {
        _name = name;
        _data = data;
    }

    /**
     * @return the column name
     */
    public String getName()
    {
        return _name;
    }

    /**
     * @return the data stored in the column
     */
    public DataType getData()
    {
        return _data;
    }

    /**
     * Updates the location in the preparedStatment with the stored peice of data.
     *
     * @param preparedStatement the prepared statement that is being updated
     * @param location the location that the stored data should be placed
     * @throws SQLException
     */
    public abstract void updatePreparedStatement(PreparedStatement preparedStatement, int location) throws SQLException;
}
