package core.minecraft.transaction.data;

import core.minecraft.common.CurrencyType;

import java.util.HashMap;

/**
 * This represents a player's wallet.
 */
public class PlayerWallet {

    /**
     * The name of the player this wallet belongs to.
     */
    private String _player;

    /**
     * The amount of each currency the player owns.
     */
    private HashMap<CurrencyType, Integer> _currencyCount = new HashMap<>();

    /**
     * Creates a new PlayerWallet.
     *
     * @param player the player this wallet belongs to
     * @param currencyCount the amount of each currency the player owns
     */
    public PlayerWallet(String player, HashMap<CurrencyType, Integer> currencyCount)
    {
        _player = player;
        _currencyCount = currencyCount;
    }

    /**
     * Alters the player's currency count by the specified amount.
     *
     * @param currencyType the type of currency being altered
     * @param count the difference in quantity
     */
    public void addCurrency(CurrencyType currencyType, int count)
    {
        if (_currencyCount.containsKey(currencyType))
        {
            _currencyCount.put(currencyType, _currencyCount.get(currencyType) + count);
        }
        else
        {
            _currencyCount.put(currencyType, count);
        }
    }

    /**
     * @return the player this instance is representing
     */
    public String getPlayer()
    {
        return _player;
    }

    /**
     * @return this player's crystal count
     */
    public int getCurrency(CurrencyType currencyType)
    {
        return _currencyCount.containsKey(currencyType) ? _currencyCount.get(currencyType) : 0;
    }
}
