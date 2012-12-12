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
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.db.dao.CmdListDAOImpl;
import ru.sincore.db.pojo.CmdListPOJO;

/**
 *  Get, format and send available actionName list.
 */
public class HelpCommand extends AbstractCommand
{
	private static final Logger log = LoggerFactory.getLogger(HelpCommand.class);

	private AbstractClient client 	= null;
	private String args 	= "";
	private String cmd		= "";

	public HelpCommand()
	{
		this.setCmdWeight(0);
	}

	@Override
	public String execute(String cmd, String args, AbstractClient commandOwner)
	{
		this.client = commandOwner;
		this.args   = args;
		this.cmd	= cmd;

        getCmdList();

        return null;
	}

	private void getCmdList()
	{
		CmdListDAOImpl cmdList = new CmdListDAOImpl();

		StringBuilder complexCmdList = new StringBuilder();
		complexCmdList.append('\n');
		complexCmdList.append("actionName - description [weight]\n");
        complexCmdList.append('\n');

		for(CmdListPOJO entry : cmdList.getCommandList())
		{
            if (!entry.getEnabled() || (client.getWeight() < entry.getCommandWeight()))
            {
                continue;
            }

			StringBuilder cmdRow = new StringBuilder();

			cmdRow.append(entry.getCommandName());
			cmdRow.append(" - ");
			cmdRow.append(entry.getCommandDescription());

			if (client.getWeight() != 0)
			{
				cmdRow.append(" [");
				cmdRow.append(entry.getCommandWeight());
				cmdRow.append("]");
			}

            cmdRow.append("\n");
       		complexCmdList.append(cmdRow.toString());
		}

		client.sendPrivateMessageFromHub(complexCmdList.toString());
	}
}
