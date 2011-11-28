package ru.sincore.adc.action_obsolete;

import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.client.AbstractClient;
import ru.sincore.client.Client;

/**
 * Create user: lh
 * Create date: 9/21/11 / 4:29 PM
 */

public class MSGTest
{
    public class MSGTester extends MSG
    {
        public MSGTester(MessageType messageType, int context, AbstractClient client, String params)
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
        AbstractClient client = new Client();
        client.setState(State.PROTOCOL);
        client.setSid(clientSid);

        MSG testMsg = new MSGTester(MessageType.F, Context.T, client, "FMSG " + clientSid + " +FI00+FI01+FI02-FI03-FI04 Test");
    }
}