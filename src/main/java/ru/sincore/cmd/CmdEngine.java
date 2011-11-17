/*
* CmdEngine.java
*
*
* Copyright (C) 2011 Valor
* Copyright (C) 2011 Alexey 'lh' Antonov
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package ru.sincore.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.client.AbstractClient;
import ru.sincore.db.dao.CmdListDAO;
import ru.sincore.db.dao.CmdListDAOImpl;
import ru.sincore.db.pojo.CmdListPOJO;

import java.util.concurrent.ConcurrentHashMap;

public class CmdEngine
{
    private static final Logger log    = LoggerFactory.getLogger(CmdEngine.class);

    private static CmdEngine instance = null;

    private static ConcurrentHashMap<String, AbstractCmd> commandContainer;


    public static CmdEngine getInstance()
    {
        if (instance == null)
        {
                instance = new CmdEngine();
                commandContainer = new ConcurrentHashMap<String, AbstractCmd>();
        }

        return instance;
    }


    private CmdEngine()
    {
        // empty constructor for singleton
    }

    /**
     * Execute command
     *
     * @param cmd    command name
     * @param args   command args
     * @param client client entity
     */
    public void executeCmd(String cmd, String args, AbstractClient client)
    {
        log.debug("Cmd : " + cmd + " | args : " + args + " | client : " + client);

        AbstractCmd cmdExecutor = commandContainer.get(cmd);

        if (cmdExecutor == null)
        {
            return;
        }

        if (!cmdExecutor.isEnabled())
        {
            return;
        }

        if (cmdExecutor.validateRights(client.getWeight()))
        {
            if (cmdExecutor.isLogs())
            {
                log.info("Command \'" + cmd + "\' executed by user \'" + client.getNick() + "\'.");
            }

            cmdExecutor.execute(cmd, args, client);
        }
        else
        {
            client.sendPrivateMessageFromHub("You don\'t have anough rights!");
        }
    }


    /**
     * Clear command container
     */
    public synchronized void removeAllCommands()
    {
        commandContainer.clear();
    }


    /**
     * Method check command exists in command container
     *
     * @param command command name
     *
     * @return true if and only if command exist, false otherwise.
     */
    public boolean commandExists(String command)
    {
        return commandContainer.containsKey(command);
    }


    /**
     * Class at runtime registers a new command hub.
     * <p/>
     * It is understood that all the arguments of the method is valid and is in no need of validation.
     * <p/>
     * Other arguments command, for example - the command arguments, description, syntax,
     * and activity logging, passed class-handler or a script ..
     *
     * @param name     name of command
     * @param executor object
     */
    public void registerCommand(String name, AbstractCmd executor)
    {
        if (name == null || name.isEmpty())
        {
            log.debug("registerCommand was called with null or empty name argument.");
            return;
        }

        CmdListDAO cmdListDAO = new CmdListDAOImpl();
        CmdListPOJO command = cmdListDAO.getCommand(name);

        if (command == null)
        {
            command = new CmdListPOJO();
            command.setCommandName(name);
        }

        executor.setCmdName(name);
        executor.setCmdArgs(command.getCommandArgs());
        executor.setCmdDescription(command.getCommandDescription());
        executor.setCmdSyntax(command.getCommandSyntax());
        executor.setEnabled(command.isEnabled());
        executor.setCmdWeight(command.getCommandWeight());
        executor.setLogs(command.isLogs());

        commandContainer.put(name, executor);

        log.debug("Command \'" + name + "\' was successfully registred.");
    }


    /**
     * Remove command from command container and db
     *
     * @param name command name
     *
     * @return true if command was successfully unregistred
     */
    public boolean unregisterCommand(String name)
    {
        try
        {
            commandContainer.remove(name);
        }
        catch (NullPointerException ex)
        {
            log.debug("unregisterCommand was called with null name argument.", ex);
            return false;
        }

        CmdListDAO cmdListDAO = new CmdListDAOImpl();

        return cmdListDAO.delCommand(name);
    }


    /**
     * Command will be still registred, but disabled
     *
     * @param name command name
     */
    public void disableCommand(String name)
    {
        AbstractCmd command = null;

        try
        {
            command = commandContainer.get(name);

            if (command == null)
            {
                return;
            }

            command.setEnabled(false);
        }
        catch (NullPointerException ex)
        {
            log.debug("disableCommand was called with null name argument.", ex);
            return;
        }

        CmdListDAO cmdListDAO = new CmdListDAOImpl();
        CmdListPOJO cmdListPOJO = cmdListDAO.getCommand(name);

        cmdListPOJO.setEnabled(false);
        if (!cmdListDAO.updateCommand(cmdListPOJO))
        {
            log.error("Command " + name + " was not been disabled.");
        }
    }

}
