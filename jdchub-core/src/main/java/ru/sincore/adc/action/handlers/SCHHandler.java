package ru.sincore.adc.action.handlers;

import ru.sincore.Broadcast;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.action.actions.SCH;
import ru.sincore.client.AbstractClient;

/**
 * SCH (search) action handler
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 25.11.11
 *         Time: 17:36
 */
public class SCHHandler extends AbstractActionHandler<SCH>
{


    public SCHHandler(AbstractClient sourceClient, SCH action)
    {
        super(sourceClient, action);
    }


    @Override
    public void handle()
            throws STAException
    {
        try
        {
            switch (action.getMessageType())
            {
                case B:
                    Broadcast.getInstance().broadcast(action.getRawCommand(), client);
                    break;

                case F:
                    // send message dependent from features
                    Broadcast.getInstance().featuredBroadcast(action.getRawCommand(),
                                                              client,
                                                              action.getRequiredFeatureList(),
                                                              action.getExcludedFeatureList());
                    break;
            }
        }
        catch (CommandException e)
        {
            e.printStackTrace();
        }
    }
}
