package ru.sincore.adc.action.actions;

import java.util.List;
import java.util.Vector;

import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.MessageType;

/**
 * Implementation for parsind and composing SID action
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 23.11.11
 *         Time: 15:01
 */
public class SID extends AbstractAction
{
    {
        actionName = "SID";
    }


    public SID()
    {
        super();
    }


    public SID(String rawCommand)
    {
        super(rawCommand);
    }


    @Override
    protected boolean parse()
            throws CommandException, STAException
    {
        if (!super.parse())
        {
            return false;
        }

        if (messageType != MessageType.I)
        {
            throw new CommandException("Message type must be 'I' for SID actionName");
        }

        if (arguments.size() == 0)
        {
            // TODO: [hatred] incorrect command
            return false;
        }

        sourceSID = arguments.poll();

        unparsed.addAll(arguments);
        arguments.clear();

        return true;
    }


    @Override
    public String getRawCommand()
            throws CommandException
    {
        // Small optimisation: check parsing state before do any action
        if (isParsed == false)
        {
            return super.getRawCommand();
        }

        // Fill arguments list
        List<String> arguments = new Vector<String>();
        arguments.add(sourceSID);

        return super.getRawCommand(arguments);
    }
}
