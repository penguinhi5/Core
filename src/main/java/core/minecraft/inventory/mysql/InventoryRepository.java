package core.minecraft.inventory.mysql;

import core.minecraft.common.Callback;
import core.minecraft.common.CurrencyType;
import core.minecraft.database.mysql.ConnectionPool;
import core.minecraft.database.mysql.MySQLRepositoryBase;
import core.minecraft.database.mysql.column.Column;
import core.minecraft.database.mysql.column.ColumnInt;
import core.minecraft.database.mysql.column.ColumnVarchar;
import core.minecraft.inventory.InventoryManager;
import core.minecraft.inventory.data.Category;
import core.minecraft.inventory.data.Item;
import core.minecraft.inventory.data.PlayerInventory;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Handles all of the database calls involving cosmetic items and other items.
 *
 * @author Preston Brown
 */
public class InventoryRepository extends MySQLRepositoryBase {

    private final String CREATE_ITEM_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS `client`.`item_categories` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(45) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private final String CREATE_ITEMS_TABLE = "CREATE TABLE IF NOT EXISTS `client`.`items` ( `id` INT NOT NULL AUTO_INCREMENT , `categoryID` INT NOT NULL , `name` VARCHAR(45) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private final String CREATE_PLAYER_OWNED_ITEMS_TABLE = "CREATE TABLE IF NOT EXISTS `client`.`player_inventories` ( `id` INT NOT NULL AUTO_INCREMENT , `clientID` INT NOT NULL , `itemID` INT NOT NULL , `quantity` INT NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private final String CREATE_FOREIGN_KEY_CRYSTAL_TRANSACTIONS = "ALTER TABLE `crystal_transactions` ADD FOREIGN KEY (`clientID`) REFERENCES `clients`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
    private final String CREATE_FOREIGN_KEY_ITEMS = "ALTER TABLE `items` ADD FOREIGN KEY (`categoryID`) REFERENCES `item_categories`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
    private final String CREATE_FOREIGN_KEY_PLAYER_OWNED_ITEMS_1 = "ALTER TABLE `player_inventories` ADD FOREIGN KEY (`clientID`) REFERENCES `clients`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
    private final String CREATE_FOREIGN_KEY_PLAYER_OWNED_ITEMS_2 = "ALTER TABLE `player_inventories` ADD FOREIGN KEY (`itemID`) REFERENCES `items`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;";

    private final String RETRIEVE_ITEMS = "SELECT items.id, items.name, items.categoryID FROM items;";
    private final String RETRIEVE_CATEGORIES = "SELECT item_categories.id, item_categories.name FROM item_categories";

    private final String ADD_CATEGORY = "INSERT INTO item_categories(name) VALUES (?)";
    private final String ADD_ITEM = "INSERT INTO items(categoryID, name) VALUES (?, ?)";

    private final String INSERT_PLAYER_OWNED_ITEMS = "INSERT INTO player_inventories(clientID, itemID, quantity) VALUES (?, ?, ?);";
    private final String UPDATE_PLAYER_OWNED_ITEMS_QUANTITY = "UPDATE player_inventories SET quantity=GREATEST(quantity + ?, 0) WHERE clientID=? AND itemID=?;";

    private InventoryManager _inventoryManager;

    /**
     * Creates a new InventoryRepository instance.
     */
    public InventoryRepository(InventoryManager inventoryManager)
    {
        super(ConnectionPool.CLIENT_POOL);

        _inventoryManager = inventoryManager;

        Bukkit.getScheduler().runTaskAsynchronously(_inventoryManager.getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                generateTables();
            }
        });
    }

    /**
     * Generates all of the tables.
     */
    private void generateTables()
    {
        try (Connection connection = _dataSource.getConnection())
        {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_ITEM_CATEGORIES_TABLE);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_ITEMS_TABLE);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_PLAYER_OWNED_ITEMS_TABLE);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_FOREIGN_KEY_CRYSTAL_TRANSACTIONS);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_FOREIGN_KEY_ITEMS);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_FOREIGN_KEY_PLAYER_OWNED_ITEMS_1);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_FOREIGN_KEY_PLAYER_OWNED_ITEMS_2);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new PlayerInventory object that contains all of the player transaction data.
     *
     * @param clientID the clientID of the player
     * @param playerName the name of the player
     * @return a {@link PlayerInventory} object containing all of the playerdata
     */
    public PlayerInventory onPlayerLogin(int clientID, String playerName)
    {
        PlayerInventory playerInventory = null;
        Statement crystalCountStatement = null, getCosmeticsStatement = null;
        ResultSet crystalCountResults = null, getCosmeticsResults = null;
        Connection connection = null;
        try
        {
            connection = _dataSource.getConnection();

            HashMap<String, Integer> playerOwnedCosmetics = new HashMap<>();
            String getCosmeticsQuery = "SELECT items.name, player_inventories.quantity FROM player_inventories INNER JOIN items ON player_inventories.itemID=items.id WHERE player_inventories.clientID=" + clientID + ";";
            getCosmeticsStatement = connection.createStatement();
            getCosmeticsResults = getCosmeticsStatement.executeQuery(getCosmeticsQuery);
            while (getCosmeticsResults.next())
            {
                playerOwnedCosmetics.put(getCosmeticsResults.getString("name"), getCosmeticsResults.getInt("quantity"));
            }

            playerInventory = new PlayerInventory(playerName, playerOwnedCosmetics);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (crystalCountResults != null)
            {
                try {crystalCountResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (getCosmeticsResults != null)
            {
                try {getCosmeticsResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (crystalCountStatement != null)
            {
                try {crystalCountStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (getCosmeticsStatement != null)
            {
                try {getCosmeticsStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (connection != null)
            {
                try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
        return playerInventory;
    }

    /**
     * Adds the inventory to the database of items.
     *
     * @param item the name of the inventory being added
     * @param categoryID the id of the category the inventory falls under
     */
    public void addItem(String item, int categoryID)
    {
        executeUpdate(ADD_ITEM, new Column[] {new ColumnInt("categoryID", categoryID), new ColumnVarchar("name", item)});
    }

    /**
     * Adds the category to the database of categories.
     *
     * @param category the name of the category being added
     */
    public void addCategory(String category)
    {
        executeUpdate(ADD_CATEGORY, new Column[] {new ColumnVarchar("name", category)});
    }

    /**
     * Gets all of the items that currently exist in the database.
     *
     * @return a list containing all of the items
     */
    public List<Item> retrieveItems()
    {
        ArrayList<Item> items = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;
        try
        {
            connection = _dataSource.getConnection();
            statement = connection.createStatement();
            results = statement.executeQuery(RETRIEVE_ITEMS);
            while (results.next())
            {
                items.add(new Item(results.getInt("categoryID"), results.getInt("id"), results.getString("name")));
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
        return items;
    }

    /**
     * Gets all of the categories that currently exist in the database.
     *
     * @return a list containing all of the categories
     */
    public List<Category> retrieveCategories()
    {
        ArrayList<Category> categories = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;
        try
        {
            connection = _dataSource.getConnection();
            statement = connection.createStatement();
            results = statement.executeQuery(RETRIEVE_CATEGORIES);
            while (results.next())
            {
                categories.add(new Category(results.getInt("id"), results.getString("name")));
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
        return categories;
    }

    /**
     * Increases or decreases the player's cosmetic quantity by the amount specified.
     *
     * @param clientID the player's clientID
     * @param itemID the id of the inventory being purchased
     * @param changeInQuantity the change in quantity
     * @param callback the callback that will called once the sql statement has been executed
     */
    public void updatePlayerItemQuantity(int clientID, int itemID, int changeInQuantity, Callback<Boolean> callback)
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet results = null;
        PreparedStatement preparedStatement = null;
        try
        {
            connection = _dataSource.getConnection();
            String query = "SELECT quantity FROM player_inventories WHERE clientID=" + clientID + " AND itemID=" + itemID + ";";
            statement = connection.createStatement();
            results = statement.executeQuery(query);
            int affectedRows;
            if (results.next())
            {

                preparedStatement = connection.prepareStatement(UPDATE_PLAYER_OWNED_ITEMS_QUANTITY);
                preparedStatement.setInt(1, changeInQuantity);
                preparedStatement.setInt(2, clientID);
                preparedStatement.setInt(3, itemID);
                affectedRows = preparedStatement.executeUpdate();

                if (affectedRows > 0)
                {
                    callback.call(true);
                }
                else
                {
                    callback.call(false);
                }
            }
            else if (changeInQuantity >= 0)
            {
                preparedStatement = connection.prepareStatement(INSERT_PLAYER_OWNED_ITEMS);
                preparedStatement.setInt(1, clientID);
                preparedStatement.setInt(2, itemID);
                preparedStatement.setInt(3, changeInQuantity);
                affectedRows = preparedStatement.executeUpdate();

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

}
