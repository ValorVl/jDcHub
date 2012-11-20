package jdchub.module.commands.handlers;

/*
 * jDcHub ADC HubSoft
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.STAException;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.i18n.Messages;
import ru.sincore.util.ClientUtils;

import java.util.Date;


/**
 *  Class provides client registration function
 *
 *  @author Valor
 */
public class RegmeCommand extends AbstractCommand
{
	private AbstractClient client;
	private String cmd;
	private String args;

	private ConfigurationManager configInstance = ConfigurationManager.getInstance();


	@Override
	public String execute(String cmd, String args, AbstractClient client)
	{

		this.client = client;
		this.cmd	= cmd;
		this.args	= args;

        String result = null;

		// Check client weight and flag "isReg", if weight > 0 and flag true, registration procedure not allowed.
		if (client.isRegistred() && client.getWeight() > 0)
		{
            result = Messages.get(Messages.REG_FAIL_MESSAGE,
                                                          client.getNick(),
                                                          (String)client.getExtendedField("LC"));
            client.sendPrivateMessageFromHub(result);
		}
		else
		{
			result = regClient();
		}

        return result;
	}

	private String regClient()
	{
		String  hubName		= configInstance.getString(ConfigurationManager.HUB_NAME);
		int passwordMinLen  = configInstance.getInt(ConfigurationManager.MIN_PASSWORD_LEN);

        if ((passwordMinLen != 0) && (args == null))
        {
            client.sendPrivateMessageFromHub("\nYou cannot register without password.\n");
            return "You cannot register without password.";
        }
        else if (args.length() < passwordMinLen)
        {
            client.sendPrivateMessageFromHub("\nPassword length is too small. Min length : " +
                                             passwordMinLen +
                                             "\n");
            return "Password length is too small. Min length : " + passwordMinLen;
        }
        else if (args != null)
        {
            client.setPassword(args.trim());
        }
        else
        {
            client.setPassword("");
        }

        client.setRegistred(true);
        client.setWeight(10);
        client.setClientTypeByWeight(client.getWeight());
        client.setRegistratorNick(client.getNick());
        client.setRegistrationDate(new Date());

        Exception e = null;

        try
        {
            client.storeInfo();
        }
        catch (STAException ex)
        {
            e = ex;
        }

        if (e != null)
        {
            return "Error occured: " + e.toString();
        }

        client.sendPrivateMessageFromHub(Messages.get("core.client.registred",
                                                      new Object[]
                                                      {
                                                              client.getNick()
                                                      }));

        ClientUtils.sendMessageToOpChat(Messages.get("core.opchat.client_registred",
                                                     new Object[]
                                                     {
                                                             client.getNick()
                                                     }));

        return "Successfully registred";
    }
}
