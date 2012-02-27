/*
 * STAException.java
 *
 * Created on 02 decembrie 2007, 12:22
 *
 * Created on 17 martie 2007, 11:14
 *
 * jDcHub
 * Copyright (C) 2007,2008  Eugen Hristev
 * Copyright (C) 2011  Alexander 'hatred' Drozdov
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
 * @author Alexander 'hatred' Drozdov
 * @author Pietricica
 */
public class STAException extends Exception
{
    private int staCode = -1;


    public STAException()
    {
        super();
    }


    public STAException(int staCode, String messageId)
    {
        super(messageId);
        this.staCode = staCode;
    }


    public int getStaCode()
    {
        return this.staCode;
    }
}
