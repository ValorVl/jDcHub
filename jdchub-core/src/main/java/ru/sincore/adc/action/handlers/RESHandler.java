package ru.sincore.adc.action.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.ClientManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.action.actions.RES;
import ru.sincore.client.AbstractClient;
import ru.sincore.i18n.Messages;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

/**
 * RES (search result) handling
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 29.11.11
 *         Time: 15:08
 */
public class RESHandler extends AbstractActionHandler<RES>
{
    private static final Logger log = LoggerFactory.getLogger(RESHandler.class);


    public RESHandler(AbstractClient client, RES action)
    {
        super(client, action);
    }


    @Override
    protected boolean validate()
            throws CommandException, STAException
    {
        if (!super.validate())
        {
            return false;
        }

        if (action.getMessageType() != MessageType.D &&
            action.getMessageType() != MessageType.E)
        {
            new STAError(client,
                         Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         Messages.INCORRECT_MESSAGE_TYPE).send();
            return false;
        }

        // looking for client by target sid
        AbstractClient targetClient = ClientManager.getInstance().getClientBySID(action.getTargetSID());
        if (targetClient == null)
        {
            //talking to inexisting client
            //not kick, maybe the other client just left after he sent the msg;
            new STAError(targetClient,
                         Constants.STA_SEVERITY_RECOVERABLE,
                         Messages.WRONG_TARGET_SID).send();

            return false;
        }

        return true;
    }


    @Override
    public void handle()
            throws STAException
    {
        try
        {
            if (!validate())
            {
                return;
            }

            AbstractClient targetClient = ClientManager.getInstance().getClientBySID(action.getTargetSID());
            targetClient.sendAdcAction(action);
            if (action.getMessageType() == MessageType.E)
            {
                client.sendAdcAction(action);
            }
        }
        catch (CommandException e)
        {
            log.error(e.toString());
        }
    }
}
