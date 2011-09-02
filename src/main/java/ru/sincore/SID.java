package ru.sincore;
/*
 * SID.java
 *
 * Created on 12 martie 2007, 19:22
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

import ru.sincore.TigerImpl.Base32;

import java.util.Random;

/**
 * Sid class ensures there is a SID available for a connecting client.
 * created 3 bytes ( aka 5 base32 symbols) but use just first 4.
 *
 * @author Pietricica
 */
public class SID
{
    //static int values[]=new int[Vars.MAX_SIDS];
    byte[] cursid;
    static int    sessions = 0;
    static Random x        = new Random();


    /**
     * Creates a new instance of SID
     */
    public SID(ClientHandler cur_client)
    {


        cursid = new byte[3];
        //x.nextBytes (cursid);
        int ok = 0;
        while (ok == 0)
        {
            x.nextBytes(cursid);
            ok = 1;

            for (ClientNod temp : SimpleHandler.getUsers())
            {


                if (cursid.equals(temp.cur_client.sid))
                {
                    ok = 0;
                    continue;
                }


            }
            if (Base32.encode(cursid).substring(0, 4).equals("ABCD"))
            {
                ok = 0;
            }
            if (Base32.encode(cursid).substring(0, 4).equals("DCBA"))
            {
                ok = 0;
            }
        }

    }


}
