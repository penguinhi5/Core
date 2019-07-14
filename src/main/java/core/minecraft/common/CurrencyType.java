package core.minecraft.common;

/**
 * These are all of the currency types.
 *
 * @author Preston Brown
 */
public enum CurrencyType {

    CRYSTAL("Crystal", "crystal");

    /**
     * The currency's display name.
     */
    private String _displayName;

    /**
     * The name of the currency column in the database.
     */
    private String _databaseName;


    private CurrencyType(String displayName, String databaseName)
    {
        _displayName = displayName;
        _databaseName = databaseName;
    }

    /**
     * @return the display name of the currency type
     */
    public String getDisplayName()
    {
        return _displayName;
    }

    /**
     * Returns the database name of the specified CurrencyType.
     *
     * @return the CurrencyType's database name
     */
    public String getDatabaseName()
    {
        return _databaseName;
    }

    /**
     * Returns the CurrencyType with the given database name. If no CurrencyType exists with the given database name
     * null is returned.
     *
     * @param databaseName the database name of the CurrencyType
     * @return the CurrencyType with the given database name, if no CurrencyType exists with that name null is returned.
     */
    public CurrencyType getCurrencyTypeFromDBname(String databaseName)
    {
        for (CurrencyType currencyType : values())
        {
            if (currencyType.getDatabaseName().equalsIgnoreCase(databaseName))
            {
                return currencyType;
            }
        }
        return null;
    }
}
