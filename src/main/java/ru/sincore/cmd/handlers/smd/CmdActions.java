package ru.sincore.cmd.handlers.smd;

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

import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCmd;
import ru.sincore.cmd.CmdContainer;
import ru.sincore.db.dao.CmdListDAOImpl;

import java.util.concurrent.ConcurrentHashMap;

public class CmdActions extends AbstractCmd
{

	private AbstractClient client;
	private String cmd;
	private String args;

	@Override
	public void execute(String cmd, String args, AbstractClient client)
	{

	}

	private void showHelp()
	{


		String description 	= getCmdDescription();
		String syntax  		= getCmdSyntax();

		StringBuilder complete = new StringBuilder();

		complete.append('\n');
		complete.append(description);
		complete.append('\n');
		complete.append(syntax);
		complete.append('\n');

		//client.getClientHandler().sendPrivateMessageFromChatBot(complete.toString());
	}

	private void addCmd(String 	name,
						int 	weight,
						String 	executorClass,
						String 	args,
						String 	description,
						String 	syntax,
						Boolean enabled,
						Boolean logged)
	{
		CmdListDAOImpl cmdListDAO = new CmdListDAOImpl();

		CmdContainer container = CmdContainer.getInstance();

		boolean register = container.registryCommand(cmd,executorClass);

		if (register)
		{
			boolean response = cmdListDAO.addCommand(name,weight,executorClass,args,description,syntax,enabled,logged);

			if (response)
			{
                client.sendPrivateMessageFromChatBot("Command : "+cmd+" registered.");
			}
		}

	}

	private void reloadCmd()
	{
		CmdContainer container = CmdContainer.getInstance();
		container.clearCommandList();
		container.buildList();

		ConcurrentHashMap<String, AbstractCmd> cmdList = container.getConteiner();

		StringBuilder cmds = new StringBuilder(cmdList.size());

		cmds.append('\n');

		for (AbstractCmd clazz : cmdList.values())
		{
			cmds.append(" [ ");
			cmds.append(clazz.getCmdNames());
			cmds.append(" ] - ");
			cmds.append(clazz.getClass().getName());
			cmds.append('\n');
		}

		client.sendPrivateMessageFromChatBot(cmds.toString());
		client.sendPrivateMessageFromChatBot("All classes successful reloaded !");
	}

	private void delCmd(String cmd)
	{

	}

	private void setLogged(String cmd, boolean state)
	{

	}

	private void setActiveState(String cmd, boolean state)
	{

	}

	private void error()
	{
		client.sendPrivateMessageFromChatBot("Error parse command arguments or unknown argument.");
	}
}
