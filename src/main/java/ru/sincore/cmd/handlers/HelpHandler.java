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
 *
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Client;
import ru.sincore.cmd.AbstractCmd;
import ru.sincore.db.dao.CmdListDAOImpl;
import ru.sincore.db.pojo.CmdListPOJO;

/**
 *  Get, format and send available command list.
 */
public class HelpHandler extends AbstractCmd
{
	private static final Logger log = LoggerFactory.getLogger(HelpHandler.class);

	private Client client 	= null;
	private String args 	= "";
	private String cmd		= "";

	public HelpHandler()
	{
		this.setCmdWeight(0);

	}

	@Override
	public void execute(String cmd, String args, Client client)
	{
		this.client = client;
		this.args   = args;
		this.cmd	= cmd;

		if (log.isDebugEnabled())
		{
			log.debug("Command : [ "+cmd+" ] execute, args [ "+args+" ], " +
							  "from client :"+client.getClientHandler().getNI());
		}

		getCmdList();

	}

	private void getCmdList()
	{
		CmdListDAOImpl cmdList = new CmdListDAOImpl();

		StringBuilder complexCmdList = new StringBuilder();
		complexCmdList.append('\n');
		complexCmdList.append("| command | description | weight");
		complexCmdList.append('\n');

		for(CmdListPOJO entry : cmdList.getCommandList())
		{
			StringBuilder cmdRow = new StringBuilder();

			cmdRow.append(entry.getCommandName());
			cmdRow.append(" 		-                 ");
			cmdRow.append(entry.getCommandDescription());

			if (client.getClientHandler().getWeight() != 0)
			{
				cmdRow.append(" [ ");
				cmdRow.append(entry.getCommandWeight());
				cmdRow.append(" ] \n");
			}

			if (client.getClientHandler().getWeight() >= entry.getCommandWeight())
			{
				complexCmdList.append(cmdRow.toString());
			}
		}

		client.getClientHandler().sendFromBotPM(complexCmdList.toString());
	}
}
