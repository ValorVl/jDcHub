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
import ru.sincore.cmd.AbstractCmd;

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

	@Override
	public void execute(String cmd, String args, Client client)
	{
		this.client = client;
		this.cmd	= cmd;
		this.args	= args;




	}
}
