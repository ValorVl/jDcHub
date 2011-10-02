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

import ru.sincore.Client;
import ru.sincore.cmd.AbstractCmd;
import ru.sincore.i18n.Messages;


/**
 *  Class provides client registration function
 *
 *  @author Valor
 */
public class ClientRegDefaultHandler extends AbstractCmd
{

	private Client client;
	private String cmd;
	private String args;

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
			regClient();
		}
	}

	private void regClient()
	{

	}
}
