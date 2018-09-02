package core.minecraft.item.mysql;

import core.minecraft.common.Callback;
import core.minecraft.database.mysql.ConnectionPool;
import core.minecraft.database.mysql.MySQLRepositoryBase;
import core.minecraft.database.mysql.column.Column;
import core.minecraft.database.mysql.column.ColumnInt;
import core.minecraft.database.mysql.column.ColumnVarchar;
import core.minecraft.item.token.CosmeticQuantityToken;
import core.minecraft.item.token.OtherItemQuantityToken;
import core.minecraft.transaction.data.PlayerTransactions;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handles all of the database calls involving cosmetic items and other items.
 *
 * @author Preston Brown
 */
public class ItemRepository extends MySQLRepositoryBase {

    private final String UPDATE_PLAYER_COSMETIC_QUANTITY = "UPDATE player_owned_cosmetics SET quantity=quantity+? WHERE clientID=? AND itemID=?;";
    private final String UPDATE_PLAYER_OTHER_ITEM_QUANTITY = "UPDATE player_owned_other_items SET quantity=quantity+? WHERE clientID=? AND name=?;";

    /**
     * Creates a new ItemRepository instance.
     */
    public ItemRepository()
    {
        super(ConnectionPool.CLIENT_POOL);
    }

    /**
     * Gets all of the items that belong to the specified category.
     *
     * @param category the category if items you are retrieving
     * @return a hashmap containing all of the items
     */
    public HashMap<String, Integer> getItemsOfCategory(String category)
    {
        HashMap<String, Integer> categoryItems = new HashMap<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;
        try
        {
            connection = _dataSource.getConnection();
            String query = "SELECT cosmetic_items.id, cosmetic_items.name FROM cosmetic_items INNER JOIN cosmetic_categories ON cosmetic_items.categoryID=cosmetic_categories.id WHERE cosmetic_categories.name='" + category + "';";
            statement = connection.createStatement();
            results = statement.executeQuery(query);
            while (results.next())
            {
                categoryItems.put(results.getString("name"), results.getInt("id"));
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
        return categoryItems;
    }

    /**
     * Increases or decreases the player's cosmetic quantity by the amount specified.
     *
     * @param clientID the player's clientID
     * @param itemID the id of the item being purchased
     * @param changeInQuantity the change in quantity
     * @param callback the callback that will called once the sql statement has been executed
     */
    public void updatePlayerCosmeticQuantity(int clientID, int itemID, int changeInQuantity, Callback<Boolean> callback)
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;
        PreparedStatement preparedStatement = null;
        try
        {
            connection = _dataSource.getConnection();
            String query = "SELECT quantity FROM player_owned_cosmetics WHERE clientID=" + clientID + " AND itemID=" + itemID + ";";
            statement = connection.createStatement();
            results = statement.executeQuery(query);
            if (results.next()) {
                int initialQuantity = results.getInt(1);
                int affectedRows;

                if (initialQuantity + changeInQuantity >= 0)
                {
                    preparedStatement = connection.prepareStatement(UPDATE_PLAYER_COSMETIC_QUANTITY);
                    preparedStatement.setInt(1, changeInQuantity);
                    preparedStatement.setInt(2, clientID);
                    preparedStatement.setInt(3, itemID);
                    affectedRows = preparedStatement.executeUpdate();
                }
                else
                {
                    preparedStatement = connection.prepareStatement(UPDATE_PLAYER_COSMETIC_QUANTITY);
                    preparedStatement.setInt(1, 0);
                    preparedStatement.setInt(2, clientID);
                    preparedStatement.setInt(3, itemID);
                    affectedRows = preparedStatement.executeUpdate();
                }

                if (affectedRows > 0)
                {
                    callback.call(true);
                }
                else
                {
                    callback.call(false);
                }
            }
            else
            {
                callback.call(false);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            callback.call(false);
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
            if (preparedStatement != null)
            {
                try {preparedStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (connection != null)
            {
                try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
    }

    /**
     * Increases or decreases the player's item quantity by the amount specified.
     *
     * @param clientID the player's clientID
     * @param item the item being purchased
     * @param changeInQuantity the change in quantity
     * @param callback the callback that will called once the sql statement has been executed
     */
    public void updatePlayerOtherItemQuantity(int clientID, String item, int changeInQuantity, Callback<Boolean> callback)
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;
        PreparedStatement preparedStatement = null;
        try
        {
            connection = _dataSource.getConnection();
            String query = "SELECT quantity FROM player_owned_other_items WHERE clientID=" + clientID + " AND name='" + item + "';";
            statement = connection.createStatement();
            results = statement.executeQuery(query);
            if (results.next()) {
                int initialQuantity = results.getInt(1);
                int affectedRows;

                if (initialQuantity + changeInQuantity >= 0)
                {
                    preparedStatement = connection.prepareStatement(UPDATE_PLAYER_OTHER_ITEM_QUANTITY);
                    preparedStatement.setInt(1, changeInQuantity);
                    preparedStatement.setInt(2, clientID);
                    preparedStatement.setString(3, item);
                    affectedRows = preparedStatement.executeUpdate();
                }
                else
                {
                    preparedStatement = connection.prepareStatement(UPDATE_PLAYER_OTHER_ITEM_QUANTITY);
                    preparedStatement.setInt(1, 0);
                    preparedStatement.setInt(2, clientID);
                    preparedStatement.setString(3, item);
                    affectedRows = preparedStatement.executeUpdate();
                }

                if (affectedRows > 0)
                {
                    callback.call(true);
                }
                else
                {
                    callback.call(false);
                }
            }
            else
            {
                callback.call(false);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            callback.call(false);
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
            if (preparedStatement != null)
            {
                try {preparedStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (connection != null)
            {
                try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
    }

    /**
     * Updates the cosmetic quantity for every item in the cosmeticQuantityMap.
     *
     * @param cosmeticQuantityMap the map containing all of the items that need updated
     */
    public void updatePlayerCosmeticQuantityDelayed(HashMap<String, List<CosmeticQuantityToken>> cosmeticQuantityMap)
    {
        try (Connection connection = _dataSource.getConnection())
        {
            for (List<CosmeticQuantityToken> tokenList : cosmeticQuantityMap.values())
            {
                for (CosmeticQuantityToken token : tokenList)
                {
                    updatePlayerCosmeticQuantity(connection, token.getClientID(), token.getItemID(), token.getChangeInQuantity());
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Updates the quantity for every item in the otherItemQuantityMap.
     *
     * @param otherItemQuantityMap the map containing all of the items that need updated
     */
    public void updatePlayerOtherItemQuantityDelayed(HashMap<String, List<OtherItemQuantityToken>> otherItemQuantityMap)
    {
        try (Connection connection = _dataSource.getConnection())
        {
            for (List<OtherItemQuantityToken> tokenList : otherItemQuantityMap.values())
            {
                for (OtherItemQuantityToken token : tokenList)
                {
                    updatePlayerOtherItemQuantity(connection, token.getClientID(), token.getItemName(), token.getChangeInQuantity());
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Increases or decreases the player's cosmetic quantity by the amount specified.
     *
     * @param connection the connection
     * @param clientID the player's clientID
     * @param itemID the id of the item being purchased
     * @param changeInQuantity the change in quantity
     */
    public void updatePlayerCosmeticQuantity(Connection connection, int clientID, int itemID, int changeInQuantity)
    {
        Statement statement = null;
        ResultSet results = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String query = "SELECT quantity FROM player_owned_cosmetics WHERE clientID=" + clientID + " AND itemID=" + itemID + ";";
            statement = connection.createStatement();
            results = statement.executeQuery(query);
            if (results.next()) {
                int initialQuantity = results.getInt(1);
                int affectedRows;

                if (initialQuantity + changeInQuantity >= 0)
                {
                    preparedStatement = connection.prepareStatement(UPDATE_PLAYER_COSMETIC_QUANTITY);
                    preparedStatement.setInt(1, changeInQuantity);
                    preparedStatement.setInt(2, clientID);
                    preparedStatement.setInt(3, itemID);
                    affectedRows = preparedStatement.executeUpdate();
                }
                else
                {
                    preparedStatement = connection.prepareStatement(UPDATE_PLAYER_COSMETIC_QUANTITY);
                    preparedStatement.setInt(1, 0);
                    preparedStatement.setInt(2, clientID);
                    preparedStatement.setInt(3, itemID);
                    affectedRows = preparedStatement.executeUpdate();
                }
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
            if (preparedStatement != null)
            {
                try {preparedStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
    }

    /**
     * Increases or decreases the player's item quantity by the amount specified.
     *
     * @param connection the connection
     * @param clientID the player's clientID
     * @param item the item being purchased
     * @param changeInQuantity the change in quantity
     */
    public void updatePlayerOtherItemQuantity(Connection connection, int clientID, String item, int changeInQuantity)
    {
        Statement statement = null;
        ResultSet results = null;
        PreparedStatement preparedStatement = null;
        try
        {
            String query = "SELECT quantity FROM player_owned_other_items WHERE clientID=" + clientID + " AND name='" + item + "';";
            statement = connection.createStatement();
            results = statement.executeQuery(query);
            if (results.next()) {
                int initialQuantity = results.getInt(1);
                int affectedRows;

                if (initialQuantity + changeInQuantity >= 0)
                {
                    preparedStatement = connection.prepareStatement(UPDATE_PLAYER_OTHER_ITEM_QUANTITY);
                    preparedStatement.setInt(1, changeInQuantity);
                    preparedStatement.setInt(2, clientID);
                    preparedStatement.setString(3, item);
                    affectedRows = preparedStatement.executeUpdate();
                }
                else
                {
                    preparedStatement = connection.prepareStatement(UPDATE_PLAYER_OTHER_ITEM_QUANTITY);
                    preparedStatement.setInt(1, 0);
                    preparedStatement.setInt(2, clientID);
                    preparedStatement.setString(3, item);
                    affectedRows = preparedStatement.executeUpdate();
                }
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
            if (preparedStatement != null)
            {
                try {preparedStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
    }
}
