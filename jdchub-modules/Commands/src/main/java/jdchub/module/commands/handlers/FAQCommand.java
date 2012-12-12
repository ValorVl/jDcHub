/*
* FAQCommand.java
*
* Created on 14 06 2012, 11:57
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

package jdchub.module.commands.handlers;

import ru.sincore.BigTextManager;
import ru.sincore.client.AbstractClient;
import ru.sincore.cmd.AbstractCommand;
import ru.sincore.util.AdcUtils;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-06-14
 */
public class FAQCommand extends AbstractCommand
{
    @Override
    public String execute(String cmd, String args, AbstractClient commandOwner)
    {
        BigTextManager bigTextManager = new BigTextManager();
        commandOwner.sendMessageFromHub(AdcUtils.fromAdcString(bigTextManager.getText(BigTextManager.FAQ)));
        return "FAQ shown.";
    }
}
