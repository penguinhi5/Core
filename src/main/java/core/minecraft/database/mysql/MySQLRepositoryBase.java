package core.minecraft.database.mysql;

import core.minecraft.database.mysql.column.Column;

import javax.sql.DataSource;
import java.sql.*;

/**
 * This is a base for any {@link core.minecraft.Component} that uses a MySQL repository.
 *
 * @author Preston Brown
 */
public abstract class MySQLRepositoryBase {

    protected DataSource _dataSource;

    /**
     * Creates a new MySQLRepositoryBase with connections from the given {@link DataSource}.
     *
     * @param dataSource the {@link DataSource} that connections will be taken from
     */
    public MySQLRepositoryBase(DataSource dataSource)
    {
        _dataSource = dataSource;
    }

    /**
     * Executes the query that will be updating the database
     * If there are any values that must be replaced in the prepared statement they must be
     * entered in chronological order within the columns parameter.
     *
     * @return the number of affected rows
     */
    protected int executeUpdate(String query, Column[] columns)
    {
        return executeUpdate(query, null, columns);
    }

    /**
     * Executes the query that will be updating the database and executes the handleCommand with the generated keys.
     * If there are any values that must be replaced in the prepared statement they must be
     * entered in chronological order within the columns parameter.
     *
     * @return the number of affected rows
     */
    protected int executeUpdate(String query, ResultSetCallable callable, Column[] columns)
    {
        int affectedRows = 0;
        Connection connection = null; //getConnection();
        PreparedStatement preparedStatement = null;
        try
        {
            connection = _dataSource.getConnection();
            preparedStatement = connection.prepareStatement(query);
            for (int i = 0; i < columns.length; i++)
            {
                columns[i].updatePreparedStatement(preparedStatement, i + 1);
            }

            affectedRows = preparedStatement.executeUpdate();
            if (callable != null)
            {
                callable.call(preparedStatement.getGeneratedKeys());
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (preparedStatement != null)
            {
                try
                {
                    preparedStatement.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return affectedRows;
    }

    /**
     * Executes the query and updates the columns.
     * If there are any values that must be replaced in the prepared statement they must be
     * entered in chronological order within the columns parameter.
     *
     * @param query the query that will be ran
     * @return the {@link ResultSet} returned from the query
     */
    protected void executeQuery(String query, Column[] columns)
    {
        executeQuery(query, null, columns);
    }

    /**
     * Executes the query and then uses the ResultSet to run the handleCommand.
     * If there are any values that must be replaced in the prepared statement they must be
     * entered in chronological order within the columns parameter.
     *
     * @param query the query that will be ran
     * @param callable the handleCommand
     * @return the {@link ResultSet} returned from the query
     */
    protected void executeQuery(String query, ResultSetCallable callable, Column[] columns)
    {
        PreparedStatement preparedStatement = null;
        Connection connection = null;
        try
        {
            connection = _dataSource.getConnection();
            preparedStatement = connection.prepareStatement(query);
            executeQuery(preparedStatement, callable, columns);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (preparedStatement != null)
            {
                try
                {
                    preparedStatement.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            if (connection != null)
            {
                try
                {
                    connection.close();
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Executes the PreparedStatement and then uses the ResultSet to handleCommand handleCommand.
     * If there are any values that must be replaced in the prepared statement they must be
     * entered in chronological order within the columns parameter.
     *
     * @param preparedStatement the PreparedStatement that will be ran
     * @param callable the handleCommand
     * @return the {@link ResultSet} returned from the query
     */
    protected void executeQuery(PreparedStatement preparedStatement, ResultSetCallable callable, Column[] columns)
    {
        ResultSet resultSet = null;
        try
        {
            for(int i = 0; i < columns.length; i++)
            {
                columns[i].updatePreparedStatement(preparedStatement, i + 1);
            }

            resultSet = preparedStatement.executeQuery();
            if (callable != null)
            {
                callable.call(resultSet);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (resultSet != null) {
                    resultSet.close();
                }
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}
