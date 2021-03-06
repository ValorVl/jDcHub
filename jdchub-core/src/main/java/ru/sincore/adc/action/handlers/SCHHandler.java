package ru.sincore.adc.action.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Broadcast;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.action.actions.SCH;
import ru.sincore.client.AbstractClient;
import ru.sincore.i18n.Messages;
import ru.sincore.signalservice.Signal;
import ru.sincore.util.ClientUtils;

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
    private static final Logger log = LoggerFactory.getLogger(SCHHandler.class);


    public SCHHandler(AbstractClient sourceClient, SCH action)
    {
        super(sourceClient, action);
    }


    private boolean detectSpamFlood()
    {
        // detect search message flood
        if (this.getMessageRecieveTime() - client.getLastSearch() <
            ConfigurationManager.getInstance().getLong(ConfigurationManager.SEARCH_BASE_INTERVAL))
        {
            // Error send disabled. Let's clients be quiete =)
//            client.sendPrivateMessageFromHub(Messages.get(Messages.TOO_FAST_SEARCHING,
//                                                          client.getExtendedField("LC")));
            return true;
        }

        // save message timestamp
        client.setLastSearch(this.getMessageRecieveTime());
        return false;
    }


    @Override
    public void handle()
            throws STAException
    {
        if (client.checkBannedByShare() || client.checkNoSearch())
        {
            return;
        }

        if (detectSpamFlood())
        {
            return;
        }

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
            log.error(e.toString());
        }
    }
}
