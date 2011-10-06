package ru.sincore.cmd;

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
import org.slf4j.Marker;
import ru.sincore.Client;
import ru.sincore.db.dao.CmdLogDAOImpl;

/**
 *  Class logging cmd execution act and store execution result.
 *	if logging in DB disabled, log must be written in file
 *
 *  @author Valor
 */
public class CmdLogger
{

	private static final Logger loger = LoggerFactory.getLogger(CmdLogger.class);

	private String marker = Marker.ANY_NON_NULL_MARKER;

	public static void log(AbstractCmd cmd, Client client, String cmdResult, String realArgs, Exception e)
	{
		if (cmd.enabled)
		{
			CmdLogDAOImpl cmdLog = new CmdLogDAOImpl();

			String cmdExecutionResult;

			if (e != null)
			{
				cmdExecutionResult = cmdResult + e.getLocalizedMessage();
			}
			else
			{
				cmdExecutionResult = cmdResult;
			}

			cmdLog.putLog(client.getClientHandler().getNI(), cmd.getCmdNames(), cmdExecutionResult, realArgs);

		}
		else
		{
			loger.warn("Logging into DB has DISABLED! CMD : "+cmd.getCmdNames()+" Args : "+realArgs+" CmdResult : "+cmdResult,e);
		}
	}

}
