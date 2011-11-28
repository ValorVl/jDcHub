package ru.sincore.adc.action.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.action.actions.AbstractAction;
import ru.sincore.client.AbstractClient;
import ru.sincore.i18n.Messages;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

/**
 * Common class for ADC Action handling
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 22.11.11
 *         Time: 16:05
 */
public abstract class AbstractActionHandler<T extends AbstractAction>
{
    private final static Logger log = LoggerFactory.getLogger(AbstractActionHandler.class);

    protected AbstractClient sourceClient;
    protected AbstractClient targetClient;
    protected T action;

    public AbstractActionHandler(AbstractClient sourceClient,
                                 AbstractClient targetClient,
                                 T              action)
    {
        this.sourceClient = sourceClient;
        this.targetClient = targetClient;
        this.action       = action;
    }


    public AbstractActionHandler(AbstractClient sourceClient,
                                 T              action)
    {
        this(sourceClient, null, action);
    }


    protected boolean validate()
            throws CommandException, STAException
    {
        action.tryParse();

        if (!action.getSourceSID().equals(sourceClient.getSid()))
        {
            new STAError(sourceClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         Messages.WRONG_SID).send();
            return false;
        }

        return true;
    }

    public abstract void handle()
            throws STAException;
}
