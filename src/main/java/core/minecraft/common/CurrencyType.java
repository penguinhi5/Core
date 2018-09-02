package core.minecraft.common;

/**
 * These are all of the currency types.
 *
 * @author Preston Brown
 */
public enum CurrencyType {

    CRYSTAL("Crystal");

    private String _displayName;

    private CurrencyType(String displayName)
    {
        _displayName = displayName;
    }
}
