package ru.sincore.cmd.handlers;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.STAException;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCmd;
import ru.sincore.cmd.CmdLogger;
import ru.sincore.i18n.Messages;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.Date;


/**
 *  Class provides client registration function
 *
 *  @author Valor
 */
public class ClientRegDefaultHandler extends AbstractCmd
{

	private static final Logger log = LoggerFactory.getLogger(ClientRegDefaultHandler.class);

	private AbstractClient client;
	private String cmd;
	private String args;

	private ConfigurationManager configInstance = ConfigurationManager.instance();

	public ClientRegDefaultHandler()
	{
		//
	}

	@Override
	public void execute(String cmd, String args, AbstractClient client)
	{

		this.client = client;
		this.cmd	= cmd;
		this.args	= args;

		// Check client weight and flag "isReg", if weight > 0 and flag true, registration procedure not allowed.
		if (client.isRegistred() && client.getWeight() > 0)
		{
            client.sendPrivateMessageFromHub(String.format(Messages.get(Messages.REG_FAIL_MESSAGE),
                                                           client.getNick()));
		}
		else
		{
			regClient();
		}
	}

	private void regClient()
	{
		String  hubName		= configInstance.getString(ConfigurationManager.HUB_NAME);
		int passwordMinLen  = configInstance.getInt(ConfigurationManager.MIN_PASSWORD_LEN);

		STAException ex = null;

		try
		{
			if (args == null || args.length() <= passwordMinLen)
			{
				new STAError(client, Constants.STA_SEVERITY_FATAL + Constants.STA_INVALID_PASSWORD,
							 Messages.get(Messages.REG_FAIL_MESSAGE));
			}
			else
			{
				client.setPassword(args.trim());
			}

			client.setClientType(2);
			client.setRegistratorNick(client.getNick());
			client.setRegistrationDate(new Date());
			client.storeInfo();

			client.sendPrivateMessageFromHub("\n "+client.getNick()+" You successful registered! \n Please reconnect to hub and enter your password.");

		} catch (STAException e)
		{
			ex = e;
		}

		CmdLogger.log(this, client, "Registered by "+hubName, args, ex);
	}
}
