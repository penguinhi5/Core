package core.minecraft.database.mysql;

import core.minecraft.database.mysql.column.Column;

import java.util.Collection;
import java.util.HashMap;

/**
 * This represents a row of MySQL data.
 *
 * @author Preston Brown
 */
public class Row {

    private HashMap<String, Column> _columns = new HashMap<>();

    /**
     * Creates a new row.
     */
    public Row() {}

    /**
     * Creates a new row and adds the given columns.
     *
     * @param columns the columns that will be added to the row
     */
    public Row(Column[] columns)
    {
        for (Column column : columns)
        {
            _columns.put(column.getName(), column);
        }
    }

    /**
     * Gets the {{@link Column} object with the specified name. If no column exists with
     * this name null will be returned;
     *
     * @return the {@link Column} object with the specified name
     */
    public Column getColumn(String columnName)
    {
        return _columns.get(columnName);
    }

    /**
     * Returns a collection of all the {@link Column}s in this row.
     *
     * @return a collection of all the {@link Column}s in this row
     */
    public Collection<Column> getColumns()
    {
        return _columns.values();
    }
}
