/*
* ZOF.java
*
* Created on 20 02 2012, 15:21
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

package ru.sincore.adc.action.actions;

import ru.sincore.adc.MessageType;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-20
 */
public class ZOF extends AbstractAction
{
    {
        actionName = "ZOF";
    }


    public ZOF()
    {
        super();
    }


    @Override
    public String getRawCommand()
    {
        if (messageType == MessageType.INVALID_MESSAGE_TYPE)
        {
            return "";
        }

        return messageType.toString() + actionName;
    }
}
