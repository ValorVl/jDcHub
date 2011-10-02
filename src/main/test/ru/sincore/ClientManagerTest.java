package ru.sincore;

import org.testng.annotations.Test;
import ru.sincore.TigerImpl.CIDGenerator;
import ru.sincore.TigerImpl.SIDGenerator;

/**
 * Create user: lh
 * Create date: 9/26/11 / 2:28 PM
 */
public class ClientManagerTest
{
    @Test
    public void testClientManager()
            throws Exception
    {
        Client client = new Client();
        client.getClientHandler().setID(SIDGenerator.generate());
        client.getClientHandler().setNI("TestNick");
        client.getClientHandler().setID(CIDGenerator.generate());

        ClientManager.getInstance().addClient(client);

        if (ClientManager.getInstance().getClientsCount() != 1)
            throw new Exception("Invalid number of clients.");
    }


}
