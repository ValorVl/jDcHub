package ru.sincore.adc.action.actions;

import java.util.List;
import java.util.Vector;

import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.util.AdcUtils;

/**
 * Class/file description
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 28.11.11
 *         Time: 12:00
 */
public class STA extends AbstractAction
{
    private int          code;
    private String       description;
    private List<String> flags = new Vector<String>();

    {
        actionName = "STA";
    }


    public STA()
    {
        super();
    }


    public STA(String rawCommand)
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
        arguments.add(String.format("%03d", code));
        arguments.add(AdcUtils.toAdcString(description));

        for (String flag : flags)
        {
            arguments.add(flag);
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

        code = Integer.parseInt(arguments.poll());
        description = AdcUtils.fromAdcString(arguments.poll());

        for (String flag : arguments)
        {
            flags.add(flag);
        }

        return true;
    }


    public int getCode()
            throws CommandException, STAException
    {
        tryParse();
        return code;
    }


    public void setCode(int code)
            throws CommandException, STAException
    {
        tryParse();
        this.code = code;
    }


    public String getDescription()
            throws CommandException, STAException
    {
        tryParse();
        return description;
    }


    public void setDescription(String description)
            throws CommandException, STAException
    {
        tryParse();
        this.description = description;
    }


    public List<String> getFlags()
            throws CommandException, STAException
    {
        tryParse();
        return flags;
    }


    public void setFlags(List<String> flags)
            throws CommandException, STAException
    {
        tryParse();
        this.flags = flags;
    }
}
