package ru.sincore.adc.action.actions;

import java.util.*;

import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Flags;

/**
 * RES (search results) actions parsing and composing
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 29.11.11
 *         Time: 14:57
 */
public class RES extends AbstractAction
{
    private Map<String, Object> flags = new HashMap<String, Object>();

    {
        actionName = "RES";
    }

    public RES()
    {
        super();
    }


    public RES(String rawCommand)
    {
        super(rawCommand);
    }


    @Override
    public String getRawCommand()
            throws CommandException
    {
        // Small optimisation: check parsing state before do any action
        if (!isParsed)
        {
            return super.getRawCommand();
        }

        // Fill arguments list
        List<String> arguments = new Vector<String>();
        for (String flag : flags.keySet())
        {
            // Skip SU field (it will be filled below)
            if (flag.equals(Flags.FEATURES))
            {
                continue;
            }

            Object value = flags.get(flag);
            // Convert Boolean flags to integer form
            if (value instanceof Boolean)
            {
                Boolean bool = (Boolean) value;
                if (bool)
                {
                    value = 1;
                }
                else
                {
                    value = 0;
                }
            }

            arguments.add(flag + value);
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

            String flag = token.substring(0,2);
            String valueStr = token.substring(2);
            Object value    = valueStr;

            // Parse Long arguments
            if (flag.equals(Flags.RES_SIZE) || flag.equals(Flags.RES_SLOTS))
            {
                try
                {
                    value = Long.parseLong(valueStr);
                }
                catch (NumberFormatException e)
                {
                    value = 0L;
                }
            }

            flags.put(flag, value);
        }

        return true;
    }



    public boolean isFlagSet(String flag)
            throws CommandException, STAException
    {
        tryParse();
        return flags.containsKey(flag);
    }



    public <T> T getFlagValue(String flag)
            throws CommandException, STAException
    {
        tryParse();

        try
        {
            @SuppressWarnings("unchecked")
            T value = (T) flags.get(flag);
            return value;
        }
        catch (Exception e)
        {
            return null;
        }

    }


    public <T> T getFlagValue(String flag, T defaultValue)
            throws CommandException, STAException
    {
        T value = getFlagValue(flag);
        if (value == null)
        {
            value = defaultValue;
        }

        return  value;
    }


    public void setFlagValue(String flag, Object value)
            throws CommandException, STAException
    {
        tryParse();
        flags.put(flag, value);
    }


    public Map<String, Object> getFlags()
            throws CommandException, STAException
    {
        tryParse();
        return flags;
    }
}
