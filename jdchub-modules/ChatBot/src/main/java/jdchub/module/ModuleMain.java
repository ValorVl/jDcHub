/*
* ModuleMain.java
*
* Created on 03 02 2012, 14:33
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

package jdchub.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.ClientManager;
import ru.sincore.modules.Module;

/**
 * Chat bot.
 * Features:
 *     none
 *
 * @author Alexey 'lh' Antonov
 * @since 2012-02-03
 */
public class ModuleMain extends Module
{
    private static final Logger log = LoggerFactory.getLogger(ModuleMain.class);

    private static final String moduleName    = "ChatBot";
    private static final String moduleVersion = "0.1.0";

    private ChatBot bot = null;

    @Override
    public boolean init()
    {
        bot = new ChatBot();

        ClientManager.getInstance().addClient(bot);

        log.info("Module " + moduleName + " inited");
        return true;
    }


    @Override
    public boolean deinit()
    {
        bot.setMustBeDisconnected(true);
        bot = null;

        log.info("Module " + moduleName + " deinited");
        return true;
    }


    @Override
    public String getName()
    {
        return moduleName;
    }


    @Override
    public String getVersion()
    {
        return moduleVersion;
    }
}
