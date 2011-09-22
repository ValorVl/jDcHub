package ru.sincore.adc.action;

import ru.sincore.Client;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.MessageType;

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

}