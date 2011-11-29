package ru.sincore.adc.action.actions;

import java.util.List;
import java.util.Vector;

import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;

/**
 * RCM (Reverse Connect To Me) action parsing and composing
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 28.11.11
 *         Time: 11:39
 */
public class RCM extends AbstractAction
{
    private String  protocol;
    private String  token;

    {
        actionName = "RCM";
    }


    public RCM()
    {
        super();
    }


    public RCM(String rawCommand)
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
        arguments.add(protocol);
        arguments.add(token);

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

        // parse here
        protocol = arguments.poll();
        token    = arguments.poll();

        return true;
    }


    public String getProtocol()
            throws CommandException, STAException
    {
        tryParse();
        return protocol;
    }


    public void setProtocol(String protocol)
            throws CommandException, STAException
    {
        tryParse();
        this.protocol = protocol;
    }


    public String getToken()
            throws CommandException, STAException
    {
        tryParse();
        return token;
    }


    public void setToken(String token)
            throws CommandException, STAException
    {
        tryParse();
        this.token = token;
    }

}
