package ru.sincore.adc.action_obsolete;

import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.client.AbstractClient;

/**
 * Create user: lh
 * Create date: 9/19/11 / 2:22 PM
 */
public class ActionTest
{
    class ActionImpl extends Action
    {

        protected ActionImpl(MessageType messageType,
                             int context,
                             AbstractClient fromClient,
                             AbstractClient toClient)
        {
            super(messageType, context, fromClient, toClient);

            super.availableContexts = Context.F;
            super.availableStates   = State.PROTOCOL;
        }


        @Override
        public String toString()
        {
            return null;
        }
    }

    @BeforeMethod
    public void setUp()
            throws Exception
    {
        PropertyConfigurator.configure(ConfigurationManager.instance().getHubConfigDir() + "/log4j.properties");
    }


    @Test
    public void testIsValid()
            throws Exception
    {
//        Client client = new Client();
//        client.getClientHandler().state = State.PROTOCOL;
//
//        Action action = new ActionImpl(MessageType.I, Context.F, null, client);
//        action.parse("");
//
//        assert action.isValid() : "In class Action invalid realization of isValid() method.";
    }
}
