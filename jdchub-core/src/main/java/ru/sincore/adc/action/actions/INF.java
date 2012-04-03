package ru.sincore.adc.action.actions;

import java.util.*;

import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Flags;

/**
 * Class/file description
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 23.11.11
 *         Time: 16:46
 */
public class INF extends AbstractAction
{
    private Map<String, Object> flags = new HashMap<String, Object>();
    private List<String> features     = new Vector<String>();

    {
        actionName = "INF";
    }


    public INF()
    {
        super();
    }


    public INF(String rawCommand)
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

        if (features.size() > 0)
        {
            StringBuffer argument = new StringBuffer(Flags.FEATURES);
            for (int i = 0; i < features.size(); i++)
            {
                String feature = features.get(i);
                if (i != 0)
                    argument.append(",");

                argument.append(feature);
            }

            arguments.add(argument.toString());
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

            if (flag.equals(Flags.FEATURES)) // Supported features
            {
                StringTokenizer featuresTokenizer = new StringTokenizer(valueStr, ",");
                while (featuresTokenizer.hasMoreTokens())
                {
                    features.add(featuresTokenizer.nextToken());
                }

                value = features;
            }
            // Parse Long arguments
            else if (flag.equals(Flags.SHARE_SIZE) ||
                     flag.equals(Flags.SHARED_FILES) ||
                     flag.equals(Flags.MAX_UPLOAD_SPEED) ||
                     flag.equals(Flags.MAX_DOWNLOAD_SPEED) ||
                     flag.equals(Flags.AUTOMATIC_SLOT_ALLOCATOR) ||
                     flag.equals(Flags.MIN_AUTOMATIC_SLOTS) ||
                     flag.equals(Flags.RX_BYTES) ||
                     flag.equals(Flags.TX_BYTES))
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
            // Parse Integer argument
            else if (flag.equals(Flags.OPENED_UPLOAD_SLOTS) ||
                     flag.equals(Flags.AMOUNT_HUBS_WHERE_NORMAL_USER) ||
                     flag.equals(Flags.AMOUNT_HUBS_WHERE_OP_USER) ||
                     flag.equals(Flags.AMOUNT_HUBS_WHERE_REGISTERED_USER) ||
                     flag.equals(Flags.AWAY) ||
                     flag.equals(Flags.CLIENT_TYPE))
            {
                try
                {
                    value = Integer.parseInt(valueStr);
                }
                catch (NumberFormatException e)
                {
                    value = 0;
                }
            }
            // Parse Boolean argument
            else if (flag.equals(Flags.HIDDEN))
            {
                // TODO: Check it!!!
                value = Boolean.parseBoolean(valueStr);
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


    public List<String> getFeatures()
            throws CommandException, STAException
    {
        tryParse();
        return features;
    }


    public void setFeatures(List<String> features)
            throws CommandException, STAException
    {
        tryParse();
        this.features = features;
    }


    public String getCid()
            throws CommandException, STAException
    {
        return getFlagValue(Flags.CID);
    }

    public void setCid(String cid)
            throws CommandException, STAException
    {
        setFlagValue(Flags.CID, cid);
    }


    public String getPid()
            throws CommandException, STAException
    {
        return getFlagValue(Flags.PID);
    }


    public void setPid(String pid)
            throws CommandException, STAException
    {
        setFlagValue(Flags.PID, pid);
    }


    public String getNick()
            throws CommandException, STAException
    {
        return getFlagValue(Flags.NICK);
    }

    public void setNick(String nick)
            throws CommandException, STAException
    {
        setFlagValue(Flags.NICK, nick);
    }
}
