/*
 * Context.java
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
 * Contains all available contexts defined in protocol.
 * More info look at <a href="http://adc.sourceforge.net/AdcUtils.html#_base_messages">AdcUtils#Contexts</a>
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-16
 */
public class Context
{
    public static final int INVALID_CONTEXT = 0x0;    // You can't find this in protocol. This is only for internal usage.
    public static final int F = 0x1;
    public static final int T = 0x2;
    public static final int C = 0x4;
    public static final int U = 0x8;

}
