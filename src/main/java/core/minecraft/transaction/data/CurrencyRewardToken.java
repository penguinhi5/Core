package core.minecraft.transaction.data;

import core.minecraft.common.Callback;

/**
 * This is a data that is used when a player is rewarded crystals.
 *
 * @author Preston Brown
 */
public class CurrencyRewardToken {

    public int _clientID;
    public int _count;
    public String _reason;
    private Callback<Boolean> _callback;

    /**
     * Creates a new CurrencyRewardToken instance.
     *
     * @param clientID the clientID receiving the crystals
     * @param count the number of crystals the player is receiving
     * @param reason the reason the player is receiving the crystals
     * @param callback the callback that will be called when the player received the crystals
     */
    public CurrencyRewardToken(int clientID, int count, String reason, Callback<Boolean> callback)
    {
        _clientID = clientID;
        _count = count;
        _reason = reason;
        _callback = callback;
    }

    public int getClientID()
    {
        return _clientID;
    }

    public Callback<Boolean> getCallback()
    {
        return _callback;
    }

    public String getReason()
    {
        return _reason;
    }

    public int getCount()
    {
        return _count;
    }
}
