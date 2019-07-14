package core.minecraft.client.repository;

import core.minecraft.client.ClientLoginProcessor;
import core.minecraft.common.Rank;
import core.minecraft.database.mysql.ConnectionPool;
import core.minecraft.database.mysql.ResultSetCallable;
import core.minecraft.database.mysql.MySQLRepositoryBase;
import core.minecraft.database.mysql.Row;
import core.minecraft.database.mysql.column.*;
import org.bukkit.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * This repository is used to manage client data
 *
 * @author Preston Brown
 */
public class ClientSQLRepository extends MySQLRepositoryBase {

    private final String CREATE_CLIENTS_TABLE = "CREATE TABLE IF NOT EXISTS client.clients ( id INT NOT NULL AUTO_INCREMENT , name VARCHAR(16) NOT NULL , uuid VARCHAR(36) NOT NULL , rank VARCHAR(16) NULL DEFAULT NULL , purchasedRank VARCHAR(16) NULL DEFAULT NULL , lastLogin DATETIME NULL DEFAULT NULL , totalPlayTime BIGINT NOT NULL DEFAULT 0 , PRIMARY KEY (id) , UNIQUE (uuid)) ENGINE = InnoDB;";
    private final String INSERT_NEW_LOGIN = "INSERT INTO clients(name, uuid, lastLogin) VALUES (?, ?, NOW());";
    private final String UPDATE_LOGIN = "UPDATE clients SET name=?, lastLogin=NOW() WHERE uuid=?;";
    private final String UPDATE_RANK = "UPDATE clients SET rank=? WHERE uuid=?;";
    private final String UPDATE_PURCHASED_RANK = "UPDATE clients SET purchasedRank=? WHERE uuid=?;";

    /**
     * Generates a new ClientSQLRepository object
     */
    public ClientSQLRepository()
    {
        super(ConnectionPool.CLIENT_POOL);
        executeUpdate(CREATE_CLIENTS_TABLE, new Column[] {});
    }

    /**
     * Completes all of the mysql processes the must be completed when a player logs in and returns
     * a {@link Row} containing all of the data stored in the 'clients' table.
     *
     * @param name the name of the player that joined
     * @param playerUUID the uuid of the player that joined
     * @return a {@link Row} containing all of the data in the 'clients' table
     */
    public Row playerLogin(String name, String playerUUID, HashMap<String, ClientLoginProcessor> loginProcesses)
    {
        Connection connection = null;
        Statement loginStatement = null, playerDataStatement = null, statement = null;
        ResultSet loginResults = null, playerDataResults = null;
        PreparedStatement loginPS = null;
        Row row = null;
        try
        {
            connection = _dataSource.getConnection();
            String loginQuery = "SELECT id FROM clients WHERE uuid=\"" + playerUUID + "\";";
            loginStatement = connection.createStatement();
            loginResults = loginStatement.executeQuery(loginQuery);

            if (!loginResults.next()) {
                loginPS = connection.prepareStatement(INSERT_NEW_LOGIN);
                loginPS.setString(1, name);
                loginPS.setString(2, playerUUID);
                loginPS.executeUpdate();
            } else {
                loginPS = connection.prepareStatement(UPDATE_LOGIN);
                loginPS.setString(1, name);
                loginPS.setString(2, playerUUID);
                loginPS.executeUpdate();
            }

            String playerDataQuery = "SELECT * FROM clients WHERE uuid=\"" + playerUUID + "\";";
            playerDataStatement = connection.createStatement();
            playerDataResults = playerDataStatement.executeQuery(playerDataQuery);
            if (playerDataResults.next())
            {
                row = new Row(new Column[]{
                        new ColumnInt("id", playerDataResults.getInt("id")), new ColumnVarchar("rank", playerDataResults.getString("rank")),
                        new ColumnVarchar("purchasedRank", playerDataResults.getString("purchasedRank")), new ColumnTimestamp("lastLogin", playerDataResults.getTimestamp("lastLogin")),
                        new ColumnBigInt("totalPlayTime", playerDataResults.getLong("totalPlayTime"))
                });
            }

            if (row != null)
            {
                for (ClientLoginProcessor loginProcess : loginProcesses.values())
                {
                    statement = connection.createStatement();
                    statement.execute(loginProcess.getQuery(name, playerUUID));
                    loginProcess.processResultSet(statement.getResultSet());
                    statement.close();
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (loginResults != null)
            {
                try {loginResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (playerDataResults != null)
            {
                try {playerDataResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (loginStatement != null)
            {
                try {loginStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (playerDataStatement != null)
            {
                try {playerDataStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (statement != null)
            {
                try {statement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (loginPS != null)
            {
                try {loginPS.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (connection != null)
            {
                try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
        return row;
    }

    /**
     * Returns a list of player names that start with the given name. If there are no players that
     * start with the given name an empty list of strings will be returned.
     *
     * @param name the name that is being searched
     * @return a list of names that start with the given name
     */
    public List<String> getMatchingPlayers(String name)
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;
        ArrayList<String> matchingNames = new ArrayList<>();
        try
        {
            connection = _dataSource.getConnection();
            String query = "SELECT name FROM clients WHERE name LIKE '" + name + "%' LIMIT 5000;";
            statement = connection.createStatement();
            results = statement.executeQuery(query);

            while (results.next())
            {
                matchingNames.add(results.getString(1));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (results != null)
            {
                try {results.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (statement != null)
            {
                try {statement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (connection != null)
            {
                try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
        return matchingNames;
    }

    /**
     * This will return the UUID that belongs to the player with the given name.
     *
     * @param name
     * @return the UUID of the player with the given name. If no player exists with the given name null is returned.
     */
    public UUID getUUIDFromName(String name)
    {
        UUID uuid = null;
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;
        try
        {
            connection = _dataSource.getConnection();
            String query = "SELECT uuid FROM clients WHERE name = '" + name + "' ORDER BY lastLogin DESC;";
            statement = connection.createStatement();
            results = statement.executeQuery(query);

            if (results.next())
            {
                uuid = UUID.fromString(results.getString(1));
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (results != null)
            {
                try {results.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (statement != null)
            {
                try {statement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (connection != null)
            {
                try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
        return uuid;
    }

    /**
     * This will return the clientID that belongs to the player with the given name.
     *
     * @param name the name of the player
     * @return the clientID of the player with the given name. If no player exists with the given name -1 is returned.
     */
    public int getClientIdFromName(String name)
    {
        int clientID = -1;
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;
        try
        {
            connection = _dataSource.getConnection();
            String query = "SELECT id FROM clients WHERE name = '" + name + "' ORDER BY lastLogin DESC;";
            statement = connection.createStatement();
            results = statement.executeQuery(query);

            if (results.next())
            {
                clientID = results.getInt(1);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (results != null)
            {
                try {results.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (statement != null)
            {
                try {statement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (connection != null)
            {
                try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
        return clientID;
    }

    /**
     * This will return the clientID that belongs to the player with the given uuid.
     *
     * @param uuid the uuid of the player
     * @return the clientID of the player with the given uuid. If no player exists with the given uuid -1 is returned.
     */
    public int getClientIdFromUUID(UUID uuid)
    {
        int clientID = -1;
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;
        try
        {
            connection = _dataSource.getConnection();
            String query = "SELECT id FROM clients WHERE uuid = '" + uuid.toString() + "' ORDER BY lastLogin DESC;";
            statement = connection.createStatement();
            results = statement.executeQuery(query);

            if (results.next())
            {
                clientID = results.getInt(1);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (results != null)
            {
                try {results.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (statement != null)
            {
                try {statement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (connection != null)
            {
                try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
        return clientID;
    }

    /**
     * Updates the rank of the player with the given uuid. If the rank was successfully updated
     * true will be returned, otherwise if there was an issue updating the rank false will be returned.
     *
     * @param uuid the uuid of the player whose rank should be updated
     * @param rank the rank that is being set
     * @return true if the rank was successfully updated, otherwise false
     */
    public boolean updateRank(String uuid, Rank rank)
    {
        executeUpdate(UPDATE_RANK, new Column[] {new ColumnVarchar("rank", rank.toString()), new ColumnVarchar("uuid", uuid)});

        // Ensure that the rank was updated
        String query = "SELECT rank FROM clients WHERE uuid = '" + uuid + "';";
        boolean updated = false;
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;
        try
        {
            connection = _dataSource.getConnection();
            statement = connection.createStatement();
            results = statement.executeQuery(query);

            if (results.next() && rank == Rank.valueOf(results.getString(1)));
            {
                updated = true;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (results != null)
            {
                try {results.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (statement != null)
            {
                try {statement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (connection != null)
            {
                try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
        return updated;
    }

    /**
     * Updates the purchased rank of the player with the given uuid. If the purchased rank was successfully updated
     * true will be returned, otherwise if there was an issue updating the purchased rank false will be returned.
     *
     * @param uuid the uuid of the player whose purchased rank should be updated
     * @param rank the purchased rank that is being set
     * @return true if the purchased rank was successfully updated, otherwise false
     */
    public boolean updatePurchasedRank(String uuid, Rank rank)
    {
        executeUpdate(UPDATE_PURCHASED_RANK, new Column[] {new ColumnVarchar("purchasedRank", rank.toString()), new ColumnVarchar("uuid", uuid)});

        // Ensure that the rank was updated
        String query = "SELECT purchasedRank FROM clients WHERE uuid = '" + uuid + "';";
        boolean updated = false;
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;
        try
        {
            connection = _dataSource.getConnection();
            statement = connection.createStatement();
            results = statement.executeQuery(query);

            if (results.next() && rank == Rank.valueOf(results.getString(1)));
            {
                updated = true;
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (results != null)
            {
                try {results.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (statement != null)
            {
                try {statement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (connection != null)
            {
                try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
        return updated;
    }
}
