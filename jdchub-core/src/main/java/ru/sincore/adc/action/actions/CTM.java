package ru.sincore.adc.action.actions;

import java.util.List;
import java.util.Vector;

import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;

/**
 * CTM (Connect To Me) command parsing and composing class
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 28.11.11
 *         Time: 10:53
 */
public class CTM extends AbstractAction
{
    private String  protocol;
    private Integer port;
    private String  token;

    {
        actionName = "CTM";
    }

    public CTM()
    {
        super();
    }


    public CTM(String rawCommand)
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
        arguments.add(port.toString());
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
        port     = Integer.parseInt(arguments.poll());
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


    public Integer getPort()
            throws CommandException, STAException
    {
        tryParse();
        return port;
    }


    public void setPort(Integer port)
            throws CommandException, STAException
    {
        tryParse();
        this.port = port;
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
