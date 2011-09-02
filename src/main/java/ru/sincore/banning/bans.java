/**
 * bans.java
 *
 * Created on 19 decembrie 2008, 21:47
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
 *
 */

package ru.sincore.banning;

import java.io.Serializable;

/**
 * A public class which holds a list of bans, and a Ban class for a singular Ban.
 *
 * @author Eugen Hristev
 */

public class bans implements Serializable
{


    public Ban[] bans;
    public int   i;


    public bans()
    {
        bans = new Ban[1000];


        if (BanList.First == null)
        {
            return;
        }
        Ban temp = BanList.First;
        i = 1;
        while (temp != null)
        {
            if (System.currentTimeMillis() - temp.timeofban - temp.time < 0 || temp.time == -1)
            {
                bans[i] = temp;

                i++;
            }
            temp = temp.Next;
        }


    }
}