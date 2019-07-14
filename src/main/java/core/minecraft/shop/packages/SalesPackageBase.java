package core.minecraft.shop.packages;

import core.minecraft.client.ClientManager;
import core.minecraft.common.Callback;
import core.minecraft.transaction.TransactionResponse;
import org.bukkit.entity.Player;

/**
 * This represents a package that can be purchased.
 *
 * @author Preston Brown
 */
public abstract class SalesPackageBase {

    protected ClientManager _clientManager;

    public SalesPackageBase(ClientManager clientManager)
    {
        _clientManager = clientManager;
    }

    /**
     * Purchases the sales package for the given player.
     *
     * @param player the player purchasing the sales package
     * @param callback the callback that will be called with the TransactionResponse
     */
    public abstract void purchaseSalesPackageForPlayer(Player player, Callback<TransactionResponse> callback);
}
