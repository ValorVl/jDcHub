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

import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Client;
import ru.sincore.cmd.AbstractCmd;
import ru.sincore.cmd.CmdContainer;
import ru.sincore.cmd.CmdUtils;
import ru.sincore.db.dao.CmdListDAOImpl;

/**
 *  A class managed command engine.
 *  <br>
 *  Implements function e.g. help,add, del, update, list, redeploy, etc..
 *
 *  @author Valor
 */
public class CmdHandler extends AbstractCmd
{

	private static final Logger log = LoggerFactory.getLogger(CmdHandler.class);

	private Client client;
	private String cmd;
	private String args;

	private CmdUtils argTransformer = null;

	@Override
	public void execute(String cmd, String args, Client client)
	{
		this.client = client;
		this.cmd	= cmd;
		this.args	= args;

		argTransformer = new CmdUtils();

		LongOpt[] longOpts = new LongOpt[1];

		longOpts[0] = new LongOpt("help",LongOpt.OPTIONAL_ARGUMENT,null,'h');

		Getopt getopt = new Getopt("cmd",argTransformer.strArgToArray(args),"-:c",longOpts);
		getopt.setOpterr(false);

		int c;

		log.info(String.valueOf(getopt.getopt()));

		String optionArg = "";

		while ((c = getopt.getopt()) != -1)
		{
			switch (c)
			{
				case 0:
					log.info("option called -> "+getopt.getOptarg());
					showHelp();
					break;

		 		case 'c':
					log.info("called {c}");
					break;

				default:
					showHelp();
					break;
			}
		}

		log.info("Cmd : " +cmd+" args : "+argTransformer.strArgToArray(args));
	}

	private void showHelp()
	{

		String description 	= this.getCmdDescription();
		String syntax  		= this.getCmdSyntax();

		StringBuilder complete = new StringBuilder();

		complete.append('\n');
		complete.append(description);
		complete.append('\n');
		complete.append(syntax);
		complete.append('\n');

		client.getClientHandler().sendFromBotPM(complete.toString());
	}

	/**
	 *  Command syntax - cmd add -n [name] -c [executor] -w [weight] -d [description] -l (logged) -e (enabled)
	 *  <br>
	 *  Additional info must be set to coming soon...
	 */
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
				client.getClientHandler().sendFromBotPM("Command : "+cmd+" registered.");
			}
		}

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
}
