/*
* ModuleCommand.java
*
* Created on 29 03 2012, 10:41
*
* Copyright (C) 2012 Alexey 'lh' Antonov
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

package ru.sincore.cmd.handlers;

import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.i18n.Messages;
import ru.sincore.modules.ModulesManager;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-03-29
 */
public class ModuleCommand extends AbstractCommand
{
    private AbstractClient  client;
    private String          cmd;
    private String          args;


    @Override
    public String execute(String cmd, String args, AbstractClient client)
    {
        this.client = client;
        this.cmd	= cmd;
        this.args	= args;

        if (args.isEmpty() || args.equals(""))
        {
            client.sendPrivateMessageFromHub(Messages.get("core.commands.module.help_text",
                                                          (String) client.getExtendedField("LC")));
            return "Help shown";
        }
        
        String[] commandArgs = args.split(" ");

        String commandAction = commandArgs[0];
        
        if (commandAction.equals("list"))
        {
            // list modules
            StringBuilder message = new StringBuilder();
            message.append("List of loaded modules:\n");

            for (String moduleName : ModulesManager.instance().getModulesNames())
            {
                message.append("\t");
                message.append(moduleName);
                message.append("\n");
            }

            client.sendPrivateMessageFromHub(message.toString());
            return "Modules listed";
        }
        else if (commandAction.equals("start"))
        {
            // load and start (or restart) module with specific name
            if (commandArgs.length < 2)
            {
                client.sendPrivateMessageFromHub(Messages.get("core.commands.argument_required",
                                                              new Object[]
                                                              {
                                                                      "start"
                                                              },
                                                              (String) client.getExtendedField("LC")));
                return "Not enough arguments";
            }

            String moduleName = commandArgs[1];
            
            if (ModulesManager.instance().isModuleLoaded(moduleName))
            {
                if (!stopModule(moduleName))
                {
                    return "Module not started, cause was not stopped";
                }
            }

            return startModule(moduleName) ? "Module started" : "Module not started";
        }
        else if (commandAction.equals("stop"))
        {
            // unload module with specific name
            if (commandArgs.length < 2)
            {
                client.sendPrivateMessageFromHub(Messages.get("core.commands.argument_required",
                                                              new Object[]
                                                              {
                                                                      "stop"
                                                              },
                                                              (String) client.getExtendedField("LC")));
                return "Not enough arguments";
            }

            return stopModule(commandArgs[1]) ? "Module stopped" : "Module not stopped";
        }

        
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


    private boolean startModule(String moduleName)
    {
        if (!ModulesManager.instance().loadModule(moduleName))
        {
            client.sendPrivateMessageFromHub(Messages.get("core.commands.module.module_not_started",
                                                          new Object[]
                                                          {
                                                                  moduleName
                                                          },
                                                          (String) client.getExtendedField("LC")));
            return false;
        }
        else
        {
            client.sendPrivateMessageFromHub(Messages.get("core.commands.module.module_started",
                                                          new Object[]
                                                          {
                                                                  moduleName
                                                          },
                                                          (String) client.getExtendedField("LC")));
        }

        return true;
    }
    
    
    private boolean stopModule(String moduleName)
    {
        if (!ModulesManager.instance().unloadModule(moduleName))
        {
            client.sendPrivateMessageFromHub(Messages.get("core.commands.module.module_not_stopped",
                                                          new Object[]
                                                          {
                                                                  moduleName
                                                          },
                                                          (String) client.getExtendedField("LC")));
            return false;
        }
        else
        {
            client.sendPrivateMessageFromHub(Messages.get("core.commands.module.module_stopped",
                                                          new Object[]
                                                          {
                                                                  moduleName
                                                          },
                                                          (String) client.getExtendedField("LC")));
        }

        return true;
    }
}
