package ru.sincore.cmd.handlers;

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.ClientManager;
import ru.sincore.Exceptions.STAException;
import ru.sincore.client.AbstractClient;
import ru.sincore.client.Client;
import ru.sincore.cmd.AbstractCmd;
import ru.sincore.cmd.CmdUtils;
import ru.sincore.i18n.Messages;

/**
 * Command for manipulation user right weight
 *
 * @author Valor
 */
public class GrantHandler extends AbstractCmd
{
    private static final Logger log = LoggerFactory.getLogger(GrantHandler.class);
    private String marker = Marker.ANY_NON_NULL_MARKER;

    private AbstractClient  client;
    private String          cmd;
    private String          args;

    private String          nick;
    private Integer         weight;


	@Override
	public void execute(String cmd, String args, AbstractClient client)
	{
        this.client = client;
        this.cmd	= cmd;
        this.args	= args;

        this.nick	= null;
        this.weight	= null;

        LongOpt[] longOpts = new LongOpt[3];

        longOpts[0] = new LongOpt("nick", LongOpt.REQUIRED_ARGUMENT, null, 'n');
        longOpts[1] = new LongOpt("weight", LongOpt.REQUIRED_ARGUMENT, null, 'w');

        String[] argArray = CmdUtils.strArgToArray(args);

        Getopt getopt = new Getopt("grant", argArray, "n:w:", longOpts);

        //getopt.setOpterr(true);

        int c;

        while ((c = getopt.getopt()) != -1)
        {
            switch (c)
            {
                case 'n':
                    this.nick = getopt.getOptarg();
                    break;

                case ':':
                    sendError(Messages.get(Messages.ARGUMENT_REQUIRED,
                                           (char)getopt.getOptopt(),
                                           (String)client.getExtendedField("LC")));
                    break;

                case '?':
                    showHelp();
                    break;

                case 'w':
                    String argument = getopt.getOptarg();
                    try
                    {
                        this.weight = Integer.parseInt(argument);
                    }
                    catch (NumberFormatException nfe)
                    {
                        if (argument.equals("Moderator"))
                        {
                            this.weight = 50;
                        }
                        else if (argument.equals("Administrator") || argument.equals("Admin"))
                        {
                            this.weight = 60;
                        }
                        else
                        {
                            sendError(Messages.get(Messages.INVALID_WEIGHT,
                                                   (String)client.getExtendedField("LC")));
                        }
                    }
                    break;

                default:
                    showHelp();
                    break;
            }
        }

        changedWeight();
	}


	/**
	 *  Update weight
	 */
	private void changedWeight()
	{
        if (nick == null)
        {
            sendError(Messages.get(Messages.NICK_REQUIRED, (String)client.getExtendedField("LC")));
            return;
        }

        if (weight == null)
        {
            sendError(Messages.get(Messages.WEIGHT_REQUIRED, (String)client.getExtendedField("LC")));
            return;
        }

        if (this.client.getWeight() < this.weight)
        {
            sendError(Messages.get(Messages.LOW_WEIGHT, (String)client.getExtendedField("LC")));
            return;
        }


        AbstractClient toClient = ClientManager.getInstance().getClientByNick(nick);
        if (toClient == null)
        {
            sendError(Messages.get(Messages.NICK_NOT_EXISTS,
                                   nick,
                                   (String)client.getExtendedField("LC")));
            return;
        }

        if (client.getWeight() < toClient.getWeight())
        {
            sendError(Messages.get(Messages.LOW_WEIGHT, (String)client.getExtendedField("LC")));
            return;
        }

        toClient.setWeight(this.weight);
        try
        {
            toClient.storeInfo();
        }
        catch (STAException staex)
        {
            // ignore it
        }
	}


    private void sendError(String mess)
    {
        client.sendPrivateMessageFromHub(mess);
    }


    private void showHelp()
    {
        sendError(Messages.get("core.commands.grant.help_text",
                               (String)client.getExtendedField("LC")));
    }

}
