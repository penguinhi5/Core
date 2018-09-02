package core.minecraft.shop.packages;

import core.minecraft.client.ClientManager;
import core.minecraft.common.Callback;
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

    public abstract void purchaseSalesPackageForPlayer(Player player, Callback<Boolean> callback);
}
