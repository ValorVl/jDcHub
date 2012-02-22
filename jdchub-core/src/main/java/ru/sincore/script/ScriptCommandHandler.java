/*
* ScriptCommand.java
*
* Created on 21 12 2011, 16:52
*
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

package ru.sincore.script;

import org.python.core.PyString;
import ru.sincore.ConfigurationManager;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.cmd.CommandUtils;
import ru.sincore.util.ClientUtils;

import java.io.File;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-12-21
 */
public class ScriptCommandHandler extends AbstractCommand
{
    private AbstractClient  client;
    private String          cmd;
    private String          args;

    private ScriptEngine    engine;


    public ScriptCommandHandler(ScriptEngine engine)
    {
        this.engine = engine;
    }


    @Override
    public String execute(String cmd, String args, AbstractClient client)
    {
        this.client = client;
        this.cmd	= cmd;
        this.args	= args;

        String[] argArray = CommandUtils.strArgToArray(args);

        if (argArray.length == 0)
        {
            showHelp();
            return null;
        }

        String command = argArray[0];
        
        if (command.equals("list"))
        {
            listAvailableScripts();
            return "Available scripts listed";
        }
        else if (command.equals("reload"))
        {
            restartScriptEngine();
            return "Script engine reloaded";
        }
        else if (command.equals("run"))
        {
            String[] commandArgs = null;
            if (argArray.length == 3)
            {
                commandArgs = CommandUtils.strArgToArray(argArray[2]);
            }

            return executeScript(argArray[1], commandArgs);
        }

        return "Invalid parameters";
    }


    private void listAvailableScripts()
    {
        StringBuilder scriptList = new StringBuilder();

        scriptList.append("\nList of all available scripts:\n");

        File scriptDirectory = new File(ConfigurationManager.getInstance()
                                                            .getString(ConfigurationManager.SCRIPTS_LOCATION));
        for (File enginesDir : scriptDirectory.listFiles())
        {
            scriptList.append(enginesDir.getName());

            if (!enginesDir.isDirectory())
            {
                scriptList.append("\n");
                continue;
            }

            scriptList.append("/:\n");

            for (File script : enginesDir.listFiles())
            {
                scriptList.append("\t" + script.getName() + "\n");
            }
        }

        client.sendPrivateMessageFromHub(scriptList.toString());
    }


    private void restartScriptEngine()
    {
        ClientUtils.sendMessageToOpChat("Script engine will be restarted!");

        this.engine.restart();
    }


    private String executeScript(String scriptName, String[] scriptArgs)
    {
        ScriptTask task = new ScriptTask();

        task.setScriptName(scriptName);
        task.setEngineType(scriptName.substring(scriptName.lastIndexOf('.') + 1));

        if (scriptArgs != null)
        {
            for (String arg : scriptArgs)
            {
                task.addArg(new PyString(arg));
            }
        }

        try
        {
            this.engine.addTask(task);
        }
        catch (Exception e)
        {
            return e.toString();
        }

        return "Script executed";
    }


    private void showHelp()
    {
        StringBuilder helpMessage = new StringBuilder();

        helpMessage.append("Script management command.\n");
        helpMessage.append("Available options:\n");
        helpMessage.append("\tlist - show all available scripts\n");
        helpMessage.append("\treload - restart script engine, load and execute all scripts\n");
        helpMessage.append("\trun <script_name> [<script_params>] - find in scripts folder script with a given name and executes it with given params (<script_params> must be placed between commas)\n");

        client.sendPrivateMessageFromHub(helpMessage.toString());
    }
}
