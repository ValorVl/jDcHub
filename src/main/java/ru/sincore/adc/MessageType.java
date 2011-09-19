/*
 * MessageType.java
 *
 * Created on 16 september 2011, 12:00
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
 * Contains all available message types defined in protocol.
 * More info look at <a href="http://adc.sourceforge.net/ADC.html#_message_types">ADC#Message types</a>
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-16
 */
public enum MessageType
{
    INVALID_MESSAGE_TYPE,   // You can't find this in protocol. This is only for internal usage.
    B,  //BROADCAST,
    C,  //CLIENT,
    D,  //DIRECT,
    E,  //ECHO,
    F,  //FEATURE,
    H,  //HUB,
    I,  //INFO,
    U   //UDP;
}
