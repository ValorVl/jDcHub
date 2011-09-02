/**
 * BanList.java
 *
 * Created on 11 mai 2007, 18:59
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

/**
 * A non serialisable list of bans
 */
public class BanList
{


    public static Ban First;


    /**
     * Creates a new instance of BanList
     */
    public BanList()
    {
        First = null;
    }


    /**
     * @param bantype ==1 nick ban
     *                bantype==2 ip ban
     *                bantype==3 cid ban
     */
    static public void addban(int bantype, String whatever, long time, String banop, String reason)
    {

        Ban test = getban(bantype, whatever);
        if (test != null)
        {
            test.banop = banop;
            test.time = time;
            test.timeofban = System.currentTimeMillis();
            test.banreason = reason;
        }
        else
        {
            Ban BAN = new Ban(bantype, whatever, time, banop, reason);
            if (First == null)
            {
                First = BAN;
            }
            else
            {
                BAN.Next = First.Next;
                First.Next = BAN;
            }
        }
    }


    static public void addban(Ban BAN)
    {

        if (First == null)
        {
            First = BAN;
        }
        else
        {

            BAN.Next = First.Next;
            First.Next = BAN;
        }
    }


    static public boolean delban(int bantype, String whatever)
    {

        if (First == null)
        {
            return false;
        }
        Ban tempy = First;
        Ban tempyprev = null;
        while (tempy != null)
        {
            if (tempy.bantype == bantype)

            {
                if (bantype == 1 && whatever.equals(tempy.nick))
                {
                    if (tempy == First)
                    {
                        First = tempy.Next;//deleted
                    }
                    else
                    {
                        tempyprev.Next = tempy.Next;//deleted
                    }
                    return true;
                }
            }

            if (bantype == 2 && whatever.equals(tempy.ip))
            {
                if (tempy == First)
                {
                    First = tempy.Next;//deleted
                }
                else
                {
                    tempyprev.Next = tempy.Next;//deleted
                }
                return true;
            }
            if (bantype == 3 && whatever.equals(tempy.cid))
            {
                if (tempy == First)
                {
                    First = tempy.Next;//deleted
                }
                else
                {
                    tempyprev.Next = tempy.Next;//deleted
                }
                return true;
            }

            tempyprev = tempy;
            tempy = tempy.Next;

        }
        return false;
    }


    static public Ban getban(int bantype, String whatever)
    {
        Ban tempy = First;
        while (tempy != null)
        {
            if (tempy.bantype == bantype)

            {
                if (bantype == 1 && whatever.equalsIgnoreCase(tempy.nick))
                {
                    return tempy;
                }
                else if (bantype == 2 && whatever.equals(tempy.ip))
                {
                    return tempy;
                }
                else if (bantype == 3 && whatever.equals(tempy.cid))
                {
                    return tempy;
                }
            }
            tempy = tempy.Next;
        }
        return null;
    }

}

