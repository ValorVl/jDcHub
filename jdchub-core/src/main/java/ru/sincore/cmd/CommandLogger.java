/*
 * jDcHub
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

package ru.sincore.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.ConfigurationManager;
import ru.sincore.client.AbstractClient;
import ru.sincore.db.dao.CmdLogDAOImpl;

/**
 *  Class logging cmd execution act and store execution result.
 *	if logging in DB disabled, log must be written in file
 *
 *  @author Valor
 *  @author Alexey 'lh' Antonov
 */
public class CommandLogger
{
	private static final Logger log = LoggerFactory.getLogger(CommandLogger.class);

	public static void log(AbstractCommand cmd, String args, AbstractClient client, String commandResult)
    {
        if (ConfigurationManager.instance().getBoolean(ConfigurationManager.COMMAND_SAVE_LOG))
        {
            if (cmd.isLogs())
            {
                if (ConfigurationManager.instance().getBoolean(ConfigurationManager.COMMAND_SAVE_LOG_TO_DB))
                {
                    CmdLogDAOImpl cmdLog = new CmdLogDAOImpl();
                    cmdLog.putLog(cmd.getCmdName(), args, client.getNick(), commandResult);
                }

                log.info("Command \'" +
                         cmd.getCmdName() +
                         "\' with args \'" +
                         args +
                        "\' was executed by \'" +
                        client.getNick() +
                        "\' and gets result \'" +
                        commandResult +
                        "\'.");
            }
        }
    }

}
