/*
 * Ban.java
 *
 * Created on 11 mai 2007, 18:53
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

package ru.sincore.banning;

import java.io.Serializable;

import ru.sincore.util.TimeConv;

/**
 * Serializable ban class that allows bans to be kept in linked list in memory
 * and to be written to file .
 *
 * @author Pietricica
 */
public class Ban implements Serializable
{
    /**
     * 0 -- no ban
     * 1 -- nick ban
     * 2 -- ip ban
     * 3 -- cid ban
     */
    public int    bantype;
    public String banreason;
    public String banop;
    /**
     * millis till ban over
     * -1 == permban
     */
    public long   time;

    public String nick;
    public String ip;
    public String cid;

    public long timeofban;

    public Ban Next;


    /**
     * Creates a new instance of ban
     */
    public Ban(int bantype, String whatever, long time, String banop, String banreason)
    {
        this.bantype = bantype;
        nick = cid = ip = null;
        if (bantype == 1)
        {
            nick = whatever.toLowerCase();
        }
        else if (bantype == 2)
        {
            ip = whatever;
        }
        else if (bantype == 3)
        {
            cid = whatever;
        }
        this.time = time;
        this.banop = banop;
        this.banreason = banreason;
        Next = null;
        timeofban = System.currentTimeMillis();
    }


    public String getTimeLeft()
    {
        long TL = System.currentTimeMillis() - timeofban - time;

        if (time == -1)
        {
            return "Permanent";
        }
        if (TL < 0)
        {
            return TimeConv.getStrTime(-TL);
        }

        return "Expired";
    }

}
