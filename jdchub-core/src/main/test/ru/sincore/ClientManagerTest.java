package ru.sincore;

import org.testng.annotations.Test;
import ru.sincore.TigerImpl.CIDGenerator;
import ru.sincore.TigerImpl.SIDGenerator;
import ru.sincore.client.AbstractClient;
import ru.sincore.client.Client;

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
        AbstractClient client = new Client();
        client.setSid(SIDGenerator.generateUnique());
        client.setNick("TestNick");
        client.setCid(CIDGenerator.generate());

        ClientManager.getInstance().addClient(client);

        if (ClientManager.getInstance().getClientsCount() != 1)
            throw new Exception("Invalid number of clients.");
    }


}
