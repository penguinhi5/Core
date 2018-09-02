package core.minecraft.transaction.mysql;

import core.minecraft.client.repository.ClientSQLRepository;
import core.minecraft.common.Callback;
import core.minecraft.database.mysql.ConnectionPool;
import core.minecraft.database.mysql.MySQLRepositoryBase;
import core.minecraft.item.ItemManager;
import core.minecraft.transaction.TransactionManager;
import core.minecraft.transaction.TransactionResponse;
import core.minecraft.transaction.data.CrystalRewardToken;
import core.minecraft.transaction.data.PlayerTransactions;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


/**
 * Handles all of the MySQL transactions.
 *
 * @author Preston Brown
 */
public class TransactionRepository extends MySQLRepositoryBase {

    private final String CREATE_PLAYER_OWNED_OTHER_ITEMS_TABLE = "CREATE TABLE IF NOT EXISTS `client`.`player_owned_other_items` ( `id` INT NOT NULL AUTO_INCREMENT , `clientID` INT NOT NULL , `name` VARCHAR(45) NOT NULL , `quantity` INT NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private final String CREATE_COSMETIC_CATEGORIES_TABLE = "CREATE TABLE IF NOT EXISTS `client`.`cosmetic_categories` ( `id` INT NOT NULL AUTO_INCREMENT , `name` VARCHAR(45) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private final String CREATE_COSMETIC_ITEMS_TABLE = "CREATE TABLE IF NOT EXISTS `client`.`cosmetic_items` ( `id` INT NOT NULL AUTO_INCREMENT , `categoryID` INT NOT NULL , `name` VARCHAR(45) NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private final String CREATE_PLAYER_OWNED_COSMETICS_TABLE = "CREATE TABLE IF NOT EXISTS `client`.`player_owned_cosmetics` ( `id` INT NOT NULL AUTO_INCREMENT , `clientID` INT NOT NULL , `itemID` INT NOT NULL , `quantity` INT NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private final String CREATE_FOREIGN_KEY_CRYSTAL_TRANSACTIONS = "ALTER TABLE `crystal_transactions` ADD FOREIGN KEY (`clientID`) REFERENCES `clients`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
    private final String CREATE_FOREIGN_KEY_COSMETIC_ITEMS = "ALTER TABLE `cosmetic_items` ADD FOREIGN KEY (`categoryID`) REFERENCES `cosmetic_categories`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
    private final String CREATE_FOREIGN_KEY_PLAYER_OWNED_COSMETICS_1 = "ALTER TABLE `player_owned_cosmetics` ADD FOREIGN KEY (`clientID`) REFERENCES `clients`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
    private final String CREATE_FOREIGN_KEY_PLAYER_OWNED_COSMETICS_2 = "ALTER TABLE `player_owned_cosmetics` ADD FOREIGN KEY (`itemID`) REFERENCES `cosmetic_items`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
    private final String CREATE_FOREIGN_KEY_PLAYER_OWNED_OTHER_ITEMS = "ALTER TABLE `player_owned_other_items` ADD FOREIGN KEY (`clientID`) REFERENCES `clients`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;";

    private final String CREATE_CRYSTAL_TRANSACTIONS_TABLE = "CREATE TABLE IF NOT EXISTS `client`.`crystal_transactions` ( `id` INT NOT NULL AUTO_INCREMENT , `clientID` INT NOT NULL , `quantity` INT NOT NULL , `reason` VARCHAR(100) NOT NULL , `date` DATETIME NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private final String INSERT_CRYSTAL_TRANSACTION = "INSERT INTO crystal_transactions(clientID, quantity, reason, date) VALUES (?, ?, ?, NOW());";
    private final String UPDATE_CLIENT_CRYSTAL_COUNT = "UPDATE clients SET crystals=? WHERE id=?;";

    private final String INSERT_PLAYER_OWNED_COSMETIC = "INSERT INTO player_owned_cosmetics(clientID, itemID, quantity) VALUES (?, ?, ?);";
    private final String INSERT_PLAYER_OWNED_OTHER_ITEM = "INSERT INTO player_owned_other_items(clientID, name, quantity) VALUES (?, ?, ?);";
    private final String UPDATE_PLAYER_OWNED_COSMETIC_QUANTITY = "UPDATE player_owned_cosmetics SET quantity=quantity+? WHERE clientID=? AND itemID=?;";
    private final String UPDATE_PLAYER_OWNED_OTHER_ITEM_QUANTITY = "UPDATE player_owned_other_items SET quantity=quantity+? WHERE clientID=? AND name=?;";

    ClientSQLRepository _clientSQLRepository;
    TransactionManager _transactionManager;

    /**
     * Creates a new TransactionRepository instance.
     */
    public TransactionRepository(TransactionManager transactionManager, ClientSQLRepository clientSQLRepository)
    {
        super(ConnectionPool.CLIENT_POOL);
        _transactionManager = transactionManager;
        _clientSQLRepository = clientSQLRepository;

        Bukkit.getScheduler().runTaskAsynchronously(_transactionManager.getPlugin(), new Runnable() {
            @Override
            public void run()
            {
                generateTables();
            }
        });
    }

    /**
     * Generates all of the tables. TESTING PURPOSES ONLY!
     */
    private void generateTables()
    {
        try (Connection connection = _dataSource.getConnection())
        {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CRYSTAL_TRANSACTIONS_TABLE);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_PLAYER_OWNED_OTHER_ITEMS_TABLE);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_COSMETIC_CATEGORIES_TABLE);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_COSMETIC_ITEMS_TABLE);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_PLAYER_OWNED_COSMETICS_TABLE);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_FOREIGN_KEY_CRYSTAL_TRANSACTIONS);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_FOREIGN_KEY_COSMETIC_ITEMS);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_FOREIGN_KEY_PLAYER_OWNED_COSMETICS_1);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_FOREIGN_KEY_PLAYER_OWNED_COSMETICS_2);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_FOREIGN_KEY_PLAYER_OWNED_OTHER_ITEMS);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new PlayerTransactions object that contains all of the player transaction data.
     *
     * @param clientID the clientID of the player
     * @param playerName the name of the player
     * @return a {@link PlayerTransactions} object containing all of the playerdata
     */
    public PlayerTransactions onPlayerLogin(int clientID, String playerName)
    {
        PlayerTransactions playerTransactions = null;
        Statement crystalCountStatement = null, getCosmeticsStatement = null, getOtherItemsStatement = null;
        ResultSet crystalCountResults = null, getCosmeticsResults = null, getOtherItemsResults = null;
        Connection connection = null;
        try
        {
            connection = _dataSource.getConnection();
            int crystalCount = 0;
            String crystalCountQuery = "SELECT crystals FROM clients WHERE id='" + clientID + "';";
            crystalCountStatement = connection.createStatement();
            crystalCountResults = crystalCountStatement.executeQuery(crystalCountQuery);
            if (crystalCountResults.next())
            {
                crystalCount = crystalCountResults.getInt(1);
            }

            HashMap<String, Integer> playerOwnedCosmetics = new HashMap<>();
            String getCosmeticsQuery = "SELECT cosmetic_items.name, player_owned_cosmetics.quantity FROM player_owned_cosmetics INNER JOIN cosmetic_items ON player_owned_cosmetics.itemID=cosmetic_items.id WHERE player_owned_cosmetics.clientID=" + clientID + ";";
            getCosmeticsStatement = connection.createStatement();
            getCosmeticsResults = getCosmeticsStatement.executeQuery(getCosmeticsQuery);
            while (getCosmeticsResults.next())
            {
                playerOwnedCosmetics.put(getCosmeticsResults.getString("name"), getCosmeticsResults.getInt("quantity"));
            }

            HashMap<String, Integer> playerOwnedOtherItems = new HashMap<>();
            String getOtherItemsQuery = "SELECT name, quantity FROM player_owned_other_items WHERE clientID=" + clientID + ";";
            getOtherItemsStatement = connection.createStatement();
            getOtherItemsResults = getOtherItemsStatement.executeQuery(getOtherItemsQuery);
            while (getOtherItemsResults.next())
            {
                playerOwnedOtherItems.put(getOtherItemsResults.getString("name"), getOtherItemsResults.getInt("quantity"));
            }

            playerTransactions = new PlayerTransactions(playerName, crystalCount, playerOwnedCosmetics, playerOwnedOtherItems);
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
            if (getOtherItemsResults != null)
            {
                try {getOtherItemsResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (crystalCountStatement != null)
            {
                try {crystalCountStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (getCosmeticsStatement != null)
            {
                try {getCosmeticsStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (getOtherItemsStatement != null)
            {
                try {getOtherItemsStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (connection != null)
            {
                try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
        return playerTransactions;
    }

    /**
     * Creates a new transaction with the data provided.
     *
     * @param clientID the player that is receiving the item
     * @param itemID the id of the item the player is purchasing
     * @param cost the price of the item being purchased
     * @param quantity the quantity that is being purchased, if you want to lower the quantity that must be done in
     *                 the {@link ItemManager}
     * @param oneTimePurchase true if this item can only be purchased once, otherwise false
     * @param callback this callback will be called with the resulting TransactionResponse
     */
    public void processCosmeticTransaction(int clientID, int itemID, int cost, int quantity, boolean oneTimePurchase, Callback<TransactionResponse> callback)
    {
        Connection connection = null;
        Statement searchForItemStatement = null, crystalCountStatement = null, itemStatement = null;
        ResultSet searchForItemResults = null, crystalCountResults = null, itemResults = null;
        PreparedStatement updateCrystalsPS = null, updateQuantityPS = null, addItemPS = null, logTransactionPS = null;
        try
        {
            connection = _dataSource.getConnection();
            connection.setAutoCommit(false);
            String crystalCountQuery = "SELECT crystals FROM clients WHERE id=" + clientID + " FOR UPDATE;";
            String searchForItemQuery = "SELECT quantity FROM player_owned_cosmetics WHERE clientID=" + clientID + " AND itemID=" + itemID + ";";
            boolean exists = false;

            searchForItemStatement = connection.createStatement();
            searchForItemResults = searchForItemStatement.executeQuery(searchForItemQuery);
            if ((exists = searchForItemResults.next()) && oneTimePurchase)
            {
                callback.call(TransactionResponse.ALREADY_OWENED);
            }
            else
            {
                // Execute the transaction
                crystalCountStatement = connection.createStatement();
                crystalCountResults = crystalCountStatement.executeQuery(crystalCountQuery);
                crystalCountResults.next();
                int initialCrystalCount = crystalCountResults.getInt(1);
                int finalCrystalCount = initialCrystalCount - cost;
                if (finalCrystalCount >= 0)
                {
                    updateCrystalsPS = connection.prepareStatement(UPDATE_CLIENT_CRYSTAL_COUNT);
                    updateCrystalsPS.setInt(1, finalCrystalCount);
                    updateCrystalsPS.setInt(2, clientID);
                    updateCrystalsPS.executeUpdate();

                    if (exists)
                    {
                        updateQuantityPS = connection.prepareStatement(UPDATE_PLAYER_OWNED_COSMETIC_QUANTITY);
                        updateQuantityPS.setInt(1, quantity);
                        updateQuantityPS.setInt(2, clientID);
                        updateQuantityPS.setInt(3, itemID);
                        updateQuantityPS.executeUpdate();
                    }
                    else
                    {
                        addItemPS = connection.prepareStatement(INSERT_PLAYER_OWNED_COSMETIC);
                        addItemPS.setInt(1, clientID);
                        addItemPS.setInt(2, itemID);
                        addItemPS.setInt(3, quantity);
                        addItemPS.executeUpdate();
                    }


                    logTransactionPS = connection.prepareStatement(INSERT_CRYSTAL_TRANSACTION);
                    logTransactionPS.setInt(1, clientID);
                    logTransactionPS.setInt(2, (cost * -1));
                    logTransactionPS.setString(3, "cosmetic - itemID: " + itemID + " quantity: " + quantity);
                    logTransactionPS.executeUpdate();

                    itemStatement = connection.createStatement();
                    itemResults = itemStatement.executeQuery(searchForItemQuery);
                    if (itemResults.next())
                    {
                        connection.commit();
                        callback.call(TransactionResponse.SUCCESSFUL);
                    }
                    else
                    {
                        callback.call(TransactionResponse.FAILED);
                        connection.rollback();
                    }
                }
                else
                {
                    callback.call(TransactionResponse.INSUFFICIENT_FUNDS);
                    connection.rollback();
                }
            }
        }
        catch (SQLException e)
        {
            try
            {
                connection.rollback();
            }
            catch (SQLException exc)
            {
                exc.printStackTrace();
            }
            e.printStackTrace();
            callback.call(TransactionResponse.FAILED);
        }
        finally
        {
            if (searchForItemResults != null)
            {
                try {searchForItemResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (crystalCountResults != null)
            {
                try {crystalCountResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (itemResults != null)
            {
                try {itemResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (searchForItemStatement != null)
            {
                try {searchForItemStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (crystalCountStatement != null)
            {
                try {crystalCountStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (itemStatement != null)
            {
                try {itemStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (updateCrystalsPS != null)
            {
                try {updateCrystalsPS.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (updateQuantityPS != null)
            {
                try {updateQuantityPS.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (addItemPS != null)
            {
                try {addItemPS.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (logTransactionPS != null)
            {
                try {logTransactionPS.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (connection != null)
            {
                try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
    }

    /**
     * Processes the transaction created from the specified data. This is used for purchasing items that are not cosmetics.
     *
     * @param clientID the clientID of the player purchasing the item
     * @param name the name of the item being purchased
     * @param quantity the quantity that is being purchased, if you want to lower the quantity that must be done in
     *                 the {@link ItemManager}
     * @param cost the number of crystals being removed from the player's balance
     * @param callback this callback will be called with the resulting TransactionResponse
     */
    public void processOtherTransaction(int clientID, String name, int quantity, int cost, boolean oneTimePurchase, Callback<TransactionResponse> callback)
    {
        Connection connection = null;
        Statement searchForItemStatement = null, crystalCountStatement = null, crystalStatement = null, itemStatement = null;
        ResultSet searchForItemResults = null, crystalCountResults = null, crystalResults = null, itemResults = null;
        PreparedStatement updateCrystalsPS = null, updateQuantityPS = null, addItemPS = null, logTransactionPS = null;
        try
        {
            connection = _dataSource.getConnection();
            connection.setAutoCommit(false);
            String crystalCountQuery = "SELECT crystals FROM clients WHERE id=" + clientID + " FOR UPDATE;";
            String searchForItemQuery = "SELECT quantity FROM player_owned_other_items WHERE clientID=" + clientID + " AND name='" + name + "';";
            boolean exists = false;

            searchForItemStatement = connection.createStatement();
            searchForItemResults = searchForItemStatement.executeQuery(searchForItemQuery);
            if ((exists = searchForItemResults.next()) && oneTimePurchase)
            {
                callback.call(TransactionResponse.ALREADY_OWENED);
            }
            else
            {
                // Execute the transaction
                crystalCountStatement = connection.createStatement();
                crystalCountResults = crystalCountStatement.executeQuery(crystalCountQuery);
                crystalCountResults.next();
                int initialCrystalCount = crystalCountResults.getInt(1);
                int finalCrystalCount = initialCrystalCount - cost;
                if (finalCrystalCount >= 0)
                {
                    updateCrystalsPS = connection.prepareStatement(UPDATE_CLIENT_CRYSTAL_COUNT);
                    updateCrystalsPS.setInt(1, finalCrystalCount);
                    updateCrystalsPS.setInt(2, clientID);
                    updateCrystalsPS.executeUpdate();

                    if (exists)
                    {
                        updateQuantityPS = connection.prepareStatement(UPDATE_PLAYER_OWNED_OTHER_ITEM_QUANTITY);
                        updateQuantityPS.setInt(1, quantity);
                        updateQuantityPS.setInt(2, clientID);
                        updateQuantityPS.setString(3, name);
                    }
                    else
                    {
                        addItemPS = connection.prepareStatement(INSERT_PLAYER_OWNED_OTHER_ITEM);
                        addItemPS.setInt(1, clientID);
                        addItemPS.setString(2, name);
                        addItemPS.setInt(3, quantity);
                        addItemPS.executeUpdate();
                    }

                    logTransactionPS = connection.prepareStatement(INSERT_CRYSTAL_TRANSACTION);
                    logTransactionPS.setInt(1, clientID);
                    logTransactionPS.setInt(2, (cost * -1));
                    logTransactionPS.setString(3, "other item - itemName: " + name + " quantity: " + quantity);
                    logTransactionPS.executeUpdate();

                    crystalStatement = connection.createStatement();
                    crystalResults = crystalStatement.executeQuery(crystalCountQuery);
                    itemStatement = connection.createStatement();
                    itemResults = itemStatement.executeQuery(searchForItemQuery);
                    if (itemResults.next())
                    {
                        connection.commit();
                        callback.call(TransactionResponse.SUCCESSFUL);
                    }
                    else
                    {
                        callback.call(TransactionResponse.FAILED);
                        connection.rollback();
                    }
                }
                else
                {
                    callback.call(TransactionResponse.INSUFFICIENT_FUNDS);
                    connection.rollback();
                }
            }
        }
        catch (SQLException e)
        {
            try
            {
                connection.rollback();
            }
            catch (SQLException exc)
            {
                exc.printStackTrace();
            }
            e.printStackTrace();
            callback.call(TransactionResponse.FAILED);
        }
        finally
        {
            if (searchForItemResults != null)
            {
                try {searchForItemResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (crystalCountResults != null)
            {
                try {crystalCountResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (crystalResults != null)
            {
                try {crystalResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (itemResults != null)
            {
                try {itemResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (searchForItemStatement != null)
            {
                try {searchForItemStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (crystalCountStatement != null)
            {
                try {crystalCountStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (crystalStatement != null)
            {
                try {crystalStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (itemStatement != null)
            {
                try {itemStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (updateCrystalsPS != null)
            {
                try {updateCrystalsPS.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (updateQuantityPS != null)
            {
                try {updateQuantityPS.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (addItemPS != null)
            {
                try {addItemPS.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (logTransactionPS != null)
            {
                try {logTransactionPS.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (connection != null)
            {
                try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
    }

    /**
     * This removes the specified amount of crystals from the player's balance.
     *
     * @param clientID the clientID of the player making the purchase
     * @param cost the number of crystals being removed from the player's balance
     * @param reason the reason for the transaction
     * @param callback true will be called if the transaction was successful, otherwise false
     */
    public void processCrystalTransaction(int clientID, int cost, String reason, Callback<Boolean> callback)
    {
        Connection connection = null;
        Statement crystalCountStatement = null, crystalStatement = null;
        ResultSet crystalCountResults = null, crystalResults = null;
        PreparedStatement updateCrystalsPS = null, logTransactionPS = null;
        try
        {
            connection = _dataSource.getConnection();
            connection.setAutoCommit(false);
            String crystalCountQuery = "SELECT crystals FROM clients WHERE id=" + clientID + " FOR UPDATE;";
            crystalCountStatement = connection.createStatement();
            crystalCountResults = crystalCountStatement.executeQuery(crystalCountQuery);
            crystalCountResults.next();
            int initialCrystalCount = crystalCountResults.getInt(1);
            int finalCrystalCount = initialCrystalCount - cost;
            if (finalCrystalCount >= 0)
            {
                updateCrystalsPS = connection.prepareStatement(UPDATE_CLIENT_CRYSTAL_COUNT);
                updateCrystalsPS.setInt(1, finalCrystalCount);
                updateCrystalsPS.setInt(2, clientID);
                updateCrystalsPS.executeUpdate();

                logTransactionPS = connection.prepareStatement(INSERT_CRYSTAL_TRANSACTION);
                logTransactionPS.setInt(1, clientID);
                logTransactionPS.setInt(2, (cost * -1));
                logTransactionPS.setString(3, "crystal transaction - " + reason);
                logTransactionPS.executeUpdate();

                crystalStatement = connection.createStatement();
                crystalResults = crystalStatement.executeQuery(crystalCountQuery);
                if (crystalResults.next() && crystalResults.getInt(1) == finalCrystalCount)
                {
                    connection.commit();
                    callback.call(true);
                }
                else
                {
                    callback.call(false);
                    connection.rollback();
                }
            }
            else
            {
                callback.call(false);
                connection.rollback();
            }
        }
        catch (SQLException e)
        {
            try
            {
                connection.rollback();
            }
            catch (SQLException exc)
            {
                exc.printStackTrace();
            }
            e.printStackTrace();
            callback.call(false);
        }
        finally
        {
            if (crystalCountResults != null)
            {
                try {crystalCountResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (crystalResults != null)
            {
                try {crystalResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (crystalCountStatement != null)
            {
                try {crystalCountStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (crystalStatement != null)
            {
                try {crystalStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (updateCrystalsPS != null)
            {
                try {updateCrystalsPS.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (logTransactionPS != null)
            {
                try {logTransactionPS.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (connection != null)
            {
                try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
    }

    /**
     * Rewards crystals the the specified player.
     *
     * @param clientID the clientID of the player that is receiving the crystals
     * @param count the amount of crystals the player is receiving which is assumed to be positive
     * @param reason the reason the player is receiving these crystals
     */
    public void rewardCrystals(int clientID, int count, String reason, Callback<Boolean> callback)
    {
        Connection connection = null;
        Statement crystalCountStatement = null, updatedCrystalCountStatement = null;
        ResultSet crystalCountResults = null, updatedCrystalCountResults = null;
        PreparedStatement updateClientCrystalPS = null, insertCrystalTransactionPS = null;
        try
        {
            connection = _dataSource.getConnection();
            connection.setAutoCommit(false);
            String crystalCountQuery = "SELECT crystals FROM clients WHERE id=" + clientID + " FOR UPDATE;";
            crystalCountStatement = connection.createStatement();
            crystalCountResults = crystalCountStatement.executeQuery(crystalCountQuery);
            if (!crystalCountResults.next())
            {
                callback.call(false);
            }
            int initialCrystalCount = crystalCountResults.getInt(1);
            int finalCrystalCount = initialCrystalCount + count;

            updateClientCrystalPS = connection.prepareStatement(UPDATE_CLIENT_CRYSTAL_COUNT);
            updateClientCrystalPS.setInt(1, finalCrystalCount);
            updateClientCrystalPS.setInt(2, clientID);
            updateClientCrystalPS.executeUpdate();

            insertCrystalTransactionPS = connection.prepareStatement(INSERT_CRYSTAL_TRANSACTION);
            insertCrystalTransactionPS.setInt(1, clientID);
            insertCrystalTransactionPS.setInt(2, count);
            insertCrystalTransactionPS.setString(3, reason);
            insertCrystalTransactionPS.executeUpdate();

            updatedCrystalCountStatement = connection.createStatement();
            updatedCrystalCountResults = updatedCrystalCountStatement.executeQuery(crystalCountQuery);
            if (!updatedCrystalCountResults.next())
            {
                connection.rollback();
                callback.call(false);
            }
            if (updatedCrystalCountResults.getInt(1) == finalCrystalCount)
            {
                connection.commit();
                callback.call(true);
            }
            else
            {
                connection.rollback();
                callback.call(false);
            }
        }
        catch (SQLException e)
        {
            try
            {
                connection.rollback();
            }
            catch (SQLException exc)
            {
                exc.printStackTrace();
            }
            e.printStackTrace();
            callback.call(false);
        }
        finally
        {
            if (crystalCountResults != null)
            {
                try {crystalCountResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (updatedCrystalCountResults != null)
            {
                try {updatedCrystalCountResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (crystalCountStatement != null)
            {
                try {crystalCountStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (updatedCrystalCountStatement != null)
            {
                try {updatedCrystalCountStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (updateClientCrystalPS != null)
            {
                try {updateClientCrystalPS.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (insertCrystalTransactionPS != null)
            {
                try {insertCrystalTransactionPS.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (connection != null)
            {
                try {connection.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
    }

    public void rewardCrystalsDelayed(Iterator<CrystalRewardToken> tokenIterator)
    {
        try (Connection connection = _dataSource.getConnection())
        {
            connection.setAutoCommit(false);
            while (tokenIterator.hasNext())
            {
                CrystalRewardToken token = tokenIterator.next();
                rewardCrystalsDelayed(connection, token, tokenIterator);
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    private void rewardCrystalsDelayed(Connection connection, CrystalRewardToken token, Iterator<CrystalRewardToken> tokenIterator)
    {
        Statement crystalCountStatement = null, updatedCrystalCountStatement = null;
        ResultSet crystalCountResults = null, updatedCrystalCountResults = null;
        PreparedStatement updateClientCrystalPS = null, insertCrystalTransactionPS = null;
        try
        {
            int clientID = token.getClientID();
            int count = token.getCount();
            String reason = token.getReason();
            Callback<Boolean> callback = token.getCallback();

            String crystalCountQuery = "SELECT crystals FROM clients WHERE id=" + clientID + " FOR UPDATE;";
            crystalCountStatement = connection.createStatement();
            crystalCountResults = crystalCountStatement.executeQuery(crystalCountQuery);
            if (!crystalCountResults.next())
            {
                callback.call(false);
                tokenIterator.remove();
            }
            int initialCrystalCount = crystalCountResults.getInt(1);
            int finalCrystalCount = initialCrystalCount + count;

            updateClientCrystalPS = connection.prepareStatement(UPDATE_CLIENT_CRYSTAL_COUNT);
            updateClientCrystalPS.setInt(1, finalCrystalCount);
            updateClientCrystalPS.setInt(2, clientID);
            updateClientCrystalPS.executeUpdate();

            insertCrystalTransactionPS = connection.prepareStatement(INSERT_CRYSTAL_TRANSACTION);
            insertCrystalTransactionPS.setInt(1, clientID);
            insertCrystalTransactionPS.setInt(2, count);
            insertCrystalTransactionPS.setString(3, reason);
            insertCrystalTransactionPS.executeUpdate();

            updatedCrystalCountStatement = connection.createStatement();
            updatedCrystalCountResults = updatedCrystalCountStatement.executeQuery(crystalCountQuery);
            if (!updatedCrystalCountResults.next())
            {
                connection.rollback();
                callback.call(false);
                tokenIterator.remove();
            }
            if (updatedCrystalCountResults.getInt(1) == finalCrystalCount)
            {
                connection.commit();
                callback.call(true);
                tokenIterator.remove();
            }
            else
            {
                connection.rollback();
                callback.call(false);
                tokenIterator.remove();
            }
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
            if (updatedCrystalCountResults != null)
            {
                try {updatedCrystalCountResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (crystalCountStatement != null)
            {
                try {crystalCountStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (updatedCrystalCountStatement != null)
            {
                try {updatedCrystalCountStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (updateClientCrystalPS != null)
            {
                try {updateClientCrystalPS.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (insertCrystalTransactionPS != null)
            {
                try {insertCrystalTransactionPS.close();} catch (SQLException e) {e.printStackTrace();}
            }
        }
    }
}
