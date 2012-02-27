/*
* ClientProtectedException.java
*
* Created on 27 02 2012, 13:51
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

package ru.sincore.Exceptions;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-27
 */
public class ClientProtectedException extends Exception
{
    public ClientProtectedException(String nick, String fromWhat)
    {
        super("Client " + nick + " protected from " + fromWhat + ". Can\'t do this!");
    }
}
