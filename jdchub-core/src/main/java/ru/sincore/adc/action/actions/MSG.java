package ru.sincore.adc.action.actions;

import java.util.List;
import java.util.Vector;

import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.i18n.Messages;
import ru.sincore.util.AdcUtils;
import ru.sincore.util.Constants;

/**
 * Class/file description
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 25.11.11
 *         Time: 14:28
 */
public class MSG extends AbstractAction
{
    private String  message;
    private String  pmSid = null;
    private boolean toMe = false;

    {
        actionName = "MSG";
    }

    public MSG()
    {
        super();
    }


    public MSG(String rawCommand)
    {
        super(rawCommand);
    }


    @Override
    public String getRawCommand()
            throws CommandException
    {
        // Fill arguments list
        List<String> arguments = new Vector<String>();
        arguments.add(AdcUtils.toAdcString(message));

        if (toMe)
        {
            arguments.add("ME1");
        }

        if (pmSid != null)
        {
            arguments.add("PM" + pmSid);
        }

        return super.getRawCommand(arguments);
    }


    @Override
    protected boolean parse()
            throws CommandException, STAException
    {
        if (!super.parse())
        {
            return false;
        }

        message = AdcUtils.fromAdcString(arguments.poll());

        String str;
        while ((str = arguments.poll()) != null)
        {
            if (str.startsWith("PM")) // Private message, extract the SID
            {
                pmSid = str.substring(2, 4);
            }
            else if (str.startsWith("ME")) // /me IRC like message
            {
                if (str.substring(2).charAt(0) == '1')
                {
                    toMe = true;
                }
                else
                {
                    throw new STAException(Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                                           Messages.INVALID_FLAG);
                }
            }
            else
            {
                unparsed.add(str);
            }
        }

        return true;
    }


    public String getMessage()
            throws CommandException, STAException
    {
        tryParse();
        return message;
    }


    public void setMessage(String message)
            throws CommandException, STAException
    {
        tryParse();
        this.message = message;
    }


    public String getPmSid()
            throws CommandException, STAException
    {
        tryParse();
        return pmSid;
    }


    public void setPmSid(String pmSid)
            throws CommandException, STAException
    {
        tryParse();
        this.pmSid = pmSid;
    }


    public boolean isToMe()
            throws CommandException, STAException
    {
        tryParse();
        return toMe;
    }


    public void setToMe(boolean toMe)
            throws CommandException, STAException
    {
        tryParse();
        this.toMe = toMe;
    }
}
