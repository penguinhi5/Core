package core.minecraft.item.token;

import core.minecraft.common.Callback;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a token that is used when a player's item quantity is updated.
 *
 * @author Preston Brown
 */
public class OtherItemQuantityToken {

    public int _clientID;
    public int _changeInQuantity;
    public String _itemName;

    /**
     * Creates a new {@link OtherItemQuantityToken} instance.
     *
     * @param clientID the player's clientID
     * @param changeInQuantity the change in the quantity
     * @param itemName the name of the item whose quantity is being changed
     */
    public OtherItemQuantityToken(int clientID, int changeInQuantity, String itemName)
    {
        _clientID = clientID;
        _changeInQuantity = changeInQuantity;
        _itemName = itemName;
    }

    public void addToken(int changeInQuantity)
    {
        _changeInQuantity += changeInQuantity;
    }

    public int getClientID()
    {
        return _clientID;
    }

    public String getItemName()
    {
        return _itemName;
    }

    public int getChangeInQuantity()
    {
        return _changeInQuantity;
    }

}
