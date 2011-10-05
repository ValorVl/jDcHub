package ru.sincore.adc.action;

import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.sincore.Client;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;

/**
 * Create user: lh
 * Create date: 9/21/11 / 4:29 PM
 */

public class MSGTest
{
    public class MSGTester extends MSG
    {
        public MSGTester(MessageType messageType, int context, Client client, String params)
                throws CommandException, STAException
        {
            super(messageType, context, client, params);
        }
    }

    @BeforeMethod
    public void setUp()
            throws Exception
    {
        PropertyConfigurator.configure("./etc/log4j.properties");
    }


    @Test
    public void testFMSG()
            throws Exception
    {
        String clientSid = "ABCD";
        Client client = new Client();
        client.getClientHandler().setState(State.PROTOCOL);
        client.getClientHandler().setSID(clientSid);

        MSG testMsg = new MSGTester(MessageType.F, Context.T, client, "FMSG " + clientSid + " +FI00+FI01+FI02-FI03-FI04 Test");
    }
}