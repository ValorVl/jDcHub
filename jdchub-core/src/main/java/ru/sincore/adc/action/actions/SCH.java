package ru.sincore.adc.action.actions;

import java.util.*;

import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Flags;

/**
 * SCH (search) action implementation
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 25.11.11
 *         Time: 17:03
 */
public class SCH extends AbstractAction
{
    private Map<String, List<Object>> flags = new HashMap<String, List<Object>>();

    {
        actionName = "SCH";
    }


    public SCH()
    {
        super();
    }


    public SCH(String rawCommand)
    {
        super(rawCommand);
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
        for (String flag : flags.keySet())
        {
            for (Object value : flags.get(flag))
            {
                arguments.add(flag + value);
            }
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

        String token;
        while ((token = arguments.poll()) != null)
        {

            String flag     = token.substring(0,2);
            String valueStr = token.substring(2);
            Object value    = valueStr;

            if (flag.equals(Flags.SCH_EXACT_SIZE)   ||
                flag.equals(Flags.SCH_GREATER_THAN) ||
                flag.equals(Flags.SCH_LESS_THAN))
            {
                value = Long.parseLong(valueStr);
            }
            else if (flag.equals(Flags.SCH_SEGA_GROUP) ||
                     flag.equals(Flags.SCH_FILE_TYPE))
            {
                value = Integer.parseInt(valueStr);
            }

            if (flags.containsKey(flag))
            {
                flags.get(flag).add(value);
            }
            else
            {
                List<Object> list = new ArrayList<Object>();
                list.add(value);
                flags.put(flag, list);
            }

        }

        return true;
    }


    public boolean isFlagSet(String flag)
            throws CommandException, STAException
    {
        tryParse();
        return flags.containsKey(flag);
    }



    public <T> List<T> getFlagValues(String flag)
            throws CommandException, STAException
    {
        tryParse();

        try
        {
            @SuppressWarnings("unchecked")
            List<T> value = (List<T>) flags.get(flag);
            return value;
        }
        catch (Exception e)
        {
            return null;
        }

    }


    public <T> List<T> getFlagValues(String flag, List<T> defaultValue)
            throws CommandException, STAException
    {
        List<T> value = getFlagValues(flag);
        if (value == null)
        {
            value = defaultValue;
        }

        return  value;
    }


    public void setFlagValues(String flag, List<Object> value)
            throws CommandException, STAException
    {
        tryParse();
        flags.put(flag, value);
    }
}
