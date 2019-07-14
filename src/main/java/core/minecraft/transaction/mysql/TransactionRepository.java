package core.minecraft.transaction.mysql;

import core.minecraft.client.repository.ClientSQLRepository;
import core.minecraft.common.Callback;
import core.minecraft.common.CurrencyType;
import core.minecraft.database.mysql.ConnectionPool;
import core.minecraft.database.mysql.MySQLRepositoryBase;
import core.minecraft.database.mysql.ResultSetCallable;
import core.minecraft.database.mysql.column.Column;
import core.minecraft.database.mysql.column.ColumnInt;
import core.minecraft.database.mysql.column.ColumnVarchar;
import core.minecraft.transaction.TransactionManager;
import core.minecraft.transaction.TransactionResponse;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Handles all of the MySQL transactions.
 *
 * @author Preston Brown
 */
public class TransactionRepository extends MySQLRepositoryBase {

    private final String CREATE_CURRENCY_TABLE = "CREATE TABLE IF NOT EXISTS `client`.`player_currencies` ( `id` INT NOT NULL AUTO_INCREMENT , `clientID` INT NOT NULL , `crystal` INT NOT NULL DEFAULT '0' , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private final String CREATE_FOREIGN_KEY_CURRENCY_TABLE = "ALTER TABLE `player_currencies` ADD FOREIGN KEY (`clientID`) REFERENCES `clients`(`id`) ON DELETE RESTRICT ON UPDATE RESTRICT;";
    private final String INSERT_INTO_CURRENCY_TABLE = "INSERT INTO player_currencies(clientID) VALUES (?);";
    private final String SELECT_FROM_CURRENCY_TABLE = "SELECT * FROM player_currencies WHERE clientID = ?";

    private final String CREATE_TRANSACTIONS_TABLE = "CREATE TABLE IF NOT EXISTS `client`.`transactions` ( `id` INT NOT NULL AUTO_INCREMENT , `clientID` INT NOT NULL , `currency` VARCHAR(15) NOT NULL , `quantity` INT NOT NULL , `reason` VARCHAR(100) NOT NULL , `date` DATETIME NOT NULL , PRIMARY KEY (`id`)) ENGINE = InnoDB;";
    private final String INSERT_TRANSACTION_LOG = "INSERT INTO transactions(clientID, currency, quantity, reason, date) VALUES (?, ?, ?, ?, NOW());";

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
     * Generates all of the tables.
     */
    private void generateTables()
    {
        try (Connection connection = _dataSource.getConnection())
        {
            PreparedStatement preparedStatement = connection.prepareStatement(CREATE_TRANSACTIONS_TABLE);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_CURRENCY_TABLE);
            preparedStatement.executeUpdate();
            preparedStatement = connection.prepareStatement(CREATE_FOREIGN_KEY_CURRENCY_TABLE);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Gets the amount of each currency the player currently owns. If this is the player's first time playing on the
     * network the player will be inserted into the player_currencies table.
     *
     * @param clientID
     * @return
     */
    public HashMap<CurrencyType, Integer> onPlayerLogin(int clientID)
    {
        HashMap<CurrencyType, Integer> playerCurrencies = new HashMap<>();

        executeQuery(SELECT_FROM_CURRENCY_TABLE, new ResultSetCallable() {
            @Override
            public void call(ResultSet resultSet) throws SQLException
            {
                // If the player_currencies table contains the player
                if (resultSet.next())
                {
                    for (CurrencyType currencyType : CurrencyType.values())
                    {
                        playerCurrencies.put(currencyType, resultSet.getInt(currencyType.getDatabaseName()));
                    }
                }
                else
                {
                    // Adds the player to the player_currencies table
                    executeUpdate(INSERT_INTO_CURRENCY_TABLE, new Column[] {new ColumnInt("clientID", clientID)});

                    // Gets the freshly created currency values
                    executeQuery(SELECT_FROM_CURRENCY_TABLE, new ResultSetCallable() {
                        @Override
                        public void call(ResultSet resultSet) throws SQLException
                        {
                            resultSet.next();
                            for (CurrencyType currencyType : CurrencyType.values())
                            {
                                playerCurrencies.put(currencyType, resultSet.getInt(currencyType.getDatabaseName()));
                            }
                        }
                    }, new Column[] {new ColumnInt("clientID", clientID)});
                }
            }
        }, new Column[] {new ColumnInt("clientID", clientID)});

        return playerCurrencies;
    }

    /**
     * This removes the specified amount of crystals from the player's balance.
     *
     * @param clientID the clientID of the player making the purchase
     * @param quantity the number of crystals being added to the player's balance
     * @param reason the reason for the transaction
     * @param callback true will be called if the transaction was successful, otherwise false
     */
    public void processCurrencyTransaction(int clientID, CurrencyType currencyType, int quantity, String reason, Callback<Boolean> callback)
    {
        Connection connection = null;
        Statement currencyCountStatement = null, currencyStatement = null;
        ResultSet currencyCountResults = null, currencyResults = null;
        PreparedStatement updateCurrencyPS = null, logTransactionPS = null;
        try
        {
            connection = _dataSource.getConnection();
            connection.setAutoCommit(false);

            // Gets the player's current currency balance
            String currencyCountQuery = "SELECT " + currencyType.getDatabaseName() + " FROM player_currencies WHERE clientID=" + clientID + " FOR UPDATE;";
            currencyCountStatement = connection.createStatement();
            currencyCountResults = currencyCountStatement.executeQuery(currencyCountQuery);

            // Ensures the player exists in the `player_currencies` table
            if (!currencyCountResults.next())
            {
                callback.call(false);
                connection.rollback();
            }

            // Computes what the player's final balance should be
            int initialCurrencyCount = currencyCountResults.getInt(1);
            int finalCurrencyCount = Math.max(initialCurrencyCount + quantity, 0);

            updateCurrencyPS = connection.prepareStatement("UPDATE player_currencies SET " + currencyType.getDatabaseName() + "=? WHERE clientID=?;");
            updateCurrencyPS.setInt(1, finalCurrencyCount);
            updateCurrencyPS.setInt(2, clientID);
            updateCurrencyPS.executeUpdate();

            logTransactionPS = connection.prepareStatement(INSERT_TRANSACTION_LOG);
            logTransactionPS.setInt(1, clientID);
            logTransactionPS.setString(2, currencyType.getDatabaseName());
            logTransactionPS.setInt(3, quantity);
            logTransactionPS.setString(4, reason);
            logTransactionPS.executeUpdate();

            currencyStatement = connection.createStatement();
            currencyResults = currencyStatement.executeQuery(currencyCountQuery);
            if (currencyResults.next() && currencyResults.getInt(1) == finalCurrencyCount)
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
            if (currencyCountResults != null)
            {
                try {currencyCountResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (currencyResults != null)
            {
                try {currencyResults.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (currencyCountStatement != null)
            {
                try {currencyCountStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (currencyStatement != null)
            {
                try {currencyStatement.close();} catch (SQLException e) {e.printStackTrace();}
            }
            if (updateCurrencyPS != null)
            {
                try {updateCurrencyPS.close();} catch (SQLException e) {e.printStackTrace();}
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
}
