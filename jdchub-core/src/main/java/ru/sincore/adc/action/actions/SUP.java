package ru.sincore.adc.action.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;


/**
 * Implementation for parsind and composing SUP action
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 22.11.11
 *         Time: 15:33
 */
public class SUP extends AbstractAction
{
    {
        actionName = "SUP";
    }

    private Map<String, Boolean> features = new HashMap<String, Boolean>();


    public SUP()
    {
        super();
    }


    public SUP(String rawCommand)
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

        String argument;
        while ((argument = arguments.poll()) != null)
        {
            String feature = argument.substring(2);

            if (argument.startsWith("AD"))
            {
                features.put(feature, true);
            }
            else if (argument.startsWith("RM"))
            {
                features.put(feature, false);
            }
            else
            {
                unparsed.add(argument);
            }
        }

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

        for (String feature : features.keySet())
        {
            if (features.get(feature) == true)
            {
                arguments.add("AD" + feature);
            }
            else
            {
                arguments.add("RM" + feature);
            }
        }

        //noinspection unchecked
        return super.getRawCommand(arguments);
    }


    public Map<String, Boolean> getFeatures()
            throws CommandException, STAException
    {
        tryParse();
        return features;
    }


    public void setFeatures(Map<String, Boolean> features)
            throws CommandException, STAException
    {
        tryParse();
        this.features = features;
    }
}
