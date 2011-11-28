package ru.sincore.adc.action.actions;

import java.util.List;
import java.util.Vector;

import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;

/**
 * GPA action parsing and composing
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 25.11.11
 *         Time: 12:03
 */
public class GPA extends AbstractAction
{
    private String password;

    {
        actionName = "GPA";
    }


    public GPA()
    {
        super();
    }


    public GPA(String rawCommand)
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

        password = arguments.poll();

        return true;
    }


    @Override
    public String getRawCommand()
            throws CommandException
    {
        // Fill arguments list
        List<String> arguments = new Vector<String>();
        arguments.add(password);

        return super.getRawCommand(arguments);
    }


    public String getPassword()
            throws CommandException, STAException
    {
        tryParse();
        return password;
    }


    public void setPassword(String password)
            throws CommandException, STAException
    {
        tryParse();
        this.password = password;
    }
}
