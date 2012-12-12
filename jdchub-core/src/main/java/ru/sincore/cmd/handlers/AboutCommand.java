/*
* AboutHandler.java
*
* Created on 11 11 2011, 10:15
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

package ru.sincore.cmd.handlers;

import ru.sincore.ClientManager;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-11-11
 */
public class AboutCommand extends AbstractCommand
{
    @Override
    public String execute(String cmd, String args, AbstractClient commandOwner)
    {
        AbstractClient clientAbout = ClientManager.getInstance().getClientByNick(args);

        StringBuilder about = new StringBuilder();
        about.append("\n");

        about.append("jDcHub is a full featured ADC hub written on Java.\n\n");

        about.append("Hub homepage: http://jdchub.sincore.ru/ \n");
        about.append("Hub issues page: http://jdchub.sincore.ru/projects/jdchub/issues \n");
        about.append("Hub source code page: https://github.com/ValorVl/jDcHub \n\n");


        about.append("This program is free software; you can redistribute it and/or\n");
        about.append("modify it under the terms of the GNU General Public License\n");
        about.append("as published by the Free Software Foundation; either version 2\n");
        about.append("of the License, or any later version.\n\n");

        about.append("This program is distributed in the hope that it will be useful,\n");
        about.append("but WITHOUT ANY WARRANTY; without even the implied warranty of\n");
        about.append("MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n");
        about.append("GNU General Public License for more details.\n\n");

        about.append("You should have received a copy of the GNU General Public License\n");
        about.append("along with this program; if not, write to the Free Software\n");
        about.append("Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.\n\n");


        about.append("jDcHub developers:\n");
        about.append("\tAlexander \'hatred\' Drozdov\n");
        about.append("\tAlexey \'lh\' Antonov\n");
        about.append("\tViktor \'Valor\' Maksimov\n\n");

        about.append("DSHub (father of jDcHub) developers:\n");
        about.append("\tEugen Hristev\n");
        about.append("\tPietricica\n");
        about.append("\tMr. Pretorian\n");

        commandOwner.sendMessageFromHub(about.toString());

        return null;
    }
}
