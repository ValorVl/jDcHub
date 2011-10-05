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
import ru.sincore.Client;
import ru.sincore.ConfigurationManager;
import ru.sincore.cmd.AbstractCmd;
import ru.sincore.cmd.CmdLogger;
import ru.sincore.db.dao.ClientListDAOImpl;
import ru.sincore.db.pojo.ClientListPOJO;
import ru.sincore.i18n.Messages;


/**
 *  Class provides client registration function
 *
 *  @author Valor
 */
public class ClientRegDefaultHandler extends AbstractCmd
{

	private static final Logger log = LoggerFactory.getLogger(ClientRegDefaultHandler.class);

	private Client client;
	private String cmd;
	private String args;

	private ConfigurationManager confgInstance = ConfigurationManager.instance();

	public ClientRegDefaultHandler()
	{

	}

	@Override
	public void execute(String cmd, String args, Client client)
	{

		this.client = client;
		this.cmd	= cmd;
		this.args	= args;

		// Check client weight and flag "isReg", if weight > 0 and flag true, registration procedure not allowed.
		if (client.getClientHandler().isReg() && client.getClientHandler().getWeight() > 0)
		{
			client.getClientHandler().sendFromBotPM(String.format(Messages.REG_FAIL_MESSAGE,
																  client.getClientHandler().getNI()));
			return;
		}
		else {

			if(getLogged())
			{
				CmdLogger.log(this, client,"Successful registered",args);
			}

			regClient();
		}
	}

	private void regClient()
	{

		int 	passMinLen 	= confgInstance.getInt(ConfigurationManager.MIN_PASSWORD_LEN);
		String  hubName		= confgInstance.getString(ConfigurationManager.HUB_NAME);

		ClientListDAOImpl clientDao = new ClientListDAOImpl();

		ClientListPOJO clientEntity = clientDao.getClientByNick(client.getClientHandler().getNI());

		log.debug(cmd+" >>>> "+ clientEntity.getNickName());

		if (clientEntity.getId() != null)
		{
			 if (cmd.length() >= passMinLen)
			 {
				 client.getClientHandler().setPassword(args);
				 client.getClientHandler().setReg(true);
				 client.getClientHandler().setWeight(10);
				 client.getClientHandler().setWhoRegged(hubName);

				 client.storeInfo();
			 }
		}

	}
}
