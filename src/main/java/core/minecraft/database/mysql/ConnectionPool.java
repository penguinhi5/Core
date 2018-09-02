package core.minecraft.database.mysql;

import org.apache.commons.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Stores and initializes the mysql connection pools
 *
 * @author Preston Brown
 */
public class ConnectionPool {

    public static final DataSource CLIENT_POOL = generateDataSource("client");

    private static DataSource generateDataSource(String database)
    {
        List<String> loginInfo = readConfig();

        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl("jdbc:mysql://" + loginInfo.get(0) + "/" + database);
        dataSource.setUsername(loginInfo.get(1));
        dataSource.setPassword(loginInfo.get(2));
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setMaxTotal(8);
        dataSource.setMaxIdle(8);
        dataSource.setSoftMinEvictableIdleTimeMillis(300000L);

        return dataSource;
    }

    private static List<String> readConfig()
    {
        File file = new File("mysql.dat");
        Scanner scanner = null;
        ArrayList<String> lines = new ArrayList<>();

        try
        {
            scanner = new Scanner(file);
            while (scanner.hasNextLine())
            {
                lines.add(scanner.nextLine());
            }
        }
        catch (FileNotFoundException e)
        {
            System.out.println("Failed to read mysql.dat - HUGE RIP");
        }
        finally
        {
            if (scanner != null)
            {
                scanner.close();
            }
        }

        return lines;
    }
}
