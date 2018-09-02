package main;

import core.minecraft.client.ClientManager;
import core.minecraft.command.MultiCommandBase;
import core.minecraft.common.F;
import core.minecraft.common.Rank;

/**
 * Created by MOTPe on 7/9/2018.
 */
public class TestMulticommand extends MultiCommandBase<TestComponent> {

    public TestMulticommand(TestComponent plugin, ClientManager clientManager)
    {
        super(plugin, clientManager, "maincmd", new String[] {"main"}, Rank.DEFAULT);
    }

    @Override
    public String getProperUsageMessage()
    {
        return F.properCommandUsageMessage("/main test", "/main test");
    }
}
