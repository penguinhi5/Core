package core.minecraft.item.token;

import core.minecraft.common.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a token that is used when a player's cosmetic item quantity is updated.
 *
 * @author Preston Brown
 */
public class CosmeticQuantityToken {

    public int _clientID;
    public int _changeInQuantity;
    public int _itemID;

    /**
     * Creates a new {@link CosmeticQuantityToken} instance.
     *
     * @param clientID the player's clientID
     * @param changeInQuantity the change in the quantity
     * @param itemID the id of the item whose quantity is being changed
     */
    public CosmeticQuantityToken(int clientID, int changeInQuantity, int itemID)
    {
        _clientID = clientID;
        _changeInQuantity = changeInQuantity;
        _itemID = itemID;
    }

    public void addToken(int changeInQuantity)
    {
        _changeInQuantity += changeInQuantity;
    }

    public int getClientID()
    {
        return _clientID;
    }

    public int getItemID()
    {
        return _itemID;
    }

    public int getChangeInQuantity()
    {
        return _changeInQuantity;
    }
}
