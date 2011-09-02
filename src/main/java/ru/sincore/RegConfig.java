/*
 * RegConfig.java
 *
 * Created on 28 aprilie 2007, 12:49
 *
 * DSHub ADC HubSoft
 * Copyright (C) 2007,2008  Eugen Hristev
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

package dshub;

import java.io.Serializable;


/**
 * Provides a chained list of registered users, and each account's information
 *
 * @author Pietricica
 */
public class RegConfig implements Serializable
{

    int reg_count;

    Nod[] nods;


    public RegConfig()
    {
        nods = new Nod[100];
        reg_count = AccountsConfig.reg_count;

        if (AccountsConfig.First == null)
        {
            return;
        }
        Nod temp = AccountsConfig.First;
        int i = 1;
        while (temp != null)
        {
            //nods[reg_count]=new Nod();
            nods[i] = temp;
            i++;
            temp = temp.Next;
        }


    }


}

