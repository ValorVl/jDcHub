package ru.sincore.adc.action.handlers;

import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.action.actions.RCM;
import ru.sincore.client.AbstractClient;
import ru.sincore.i18n.Messages;
import ru.sincore.util.Constants;
import ru.sincore.util.MessageUtils;
import ru.sincore.util.STAError;

/**
 * RCM (reverse connect to me) handler
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 28.11.11
 *         Time: 11:42
 */
public class RCMHandler extends AbstractActionHandler<RCM>
{


    public RCMHandler(AbstractClient sourceClient, RCM action)
    {
        super(sourceClient, action);
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
        if (client.checkBannedByShare() || client.checkNoTransfer())
        {
            return;
        }


        try
        {
            if (!validate())
            {
                return;
            }

            AbstractClient targetClient = ClientManager.getInstance().getClientBySID(action.getTargetSID());
            targetClient.sendRawCommand(action.getRawCommand());
            if (action.getMessageType() == MessageType.E)
            {
                client.sendRawCommand(action.getRawCommand());
            }
        }
        catch (CommandException e)
        {
            e.printStackTrace();
        }

    }
}
