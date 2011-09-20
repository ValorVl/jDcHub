/*
 * State.java
 *
 * Created on 16 september 2011, 15:30
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

package ru.sincore.adc;

/**
 * Contains all available client states defined in protocol.
 * More info look at <a href="http://adc.sourceforge.net/AdcUtils.html#_client_hub_communication">AdcUtils#Client states</a>
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-16
 */
public class State
{
    public static final int INVALID_STATE = 0x0;  // You can't find this in protocol. This is only for internal usage.
    public static final int PROTOCOL = 0x1;
    public static final int IDENTIFY = 0x2;
    public static final int VERIFY = 0x4;
    public static final int NORMAL = 0x8;
    public static final int DATA = 0x16;


    // TODO Remove this code when all actions will be written
    public static String toString(int state)
    {
        switch (state)
        {
            case PROTOCOL:
                return "PROTOCOL";
            case IDENTIFY:
                return "IDENTIFY";
            case VERIFY:
                return "VERIFY";
            case NORMAL:
                return "NORMAL";
            case DATA:
                return "DATA";
            default:
                return "INVALID_STATE";
        }
    }
}
