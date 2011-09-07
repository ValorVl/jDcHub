/*
 * AccountsConfig.java
 *
 * Created on 02 decembrie 2007, 11:51
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

package ru.sincore;

import java.util.HashMap;

/**
 * @author Pietricica
 */
public class AccountsConfig
{
    public static HashMap<String, Nod> nods;


    /**
     * Creates a new instance of AccountsConfig
     */
    public AccountsConfig()
    {
    }


    static public void addReg(String CID, String LastNI, String WhoRegged)
    {
        Nod newreg;
        newreg = new Nod();
        newreg.CID = CID;
        newreg.Password = "";
        newreg.key = false;
        newreg.isreg = true;
        newreg.LastNI = LastNI;
        newreg.WhoRegged = WhoRegged;
        newreg.CreatedOn = System.currentTimeMillis();

        nods.put(newreg.CID, newreg);
    }


    static public void addReg(Nod n)
    {
        nods.put(n.CID, n);
    }


    static public int isReg(String CID)
    {
        return (nods.containsKey(CID) ? 1 : 0);
    }


    static public Nod isNickRegFl(String nick)
    {
        Nod x = First;
        while (x != null)
        {
            if (x.LastNI != null)
            {
                if (x.LastNI.equalsIgnoreCase(nick) && x.accountflyable)
                {
                    return x;
                }
            }
            x = x.Next;
        }
        return null;
    }


    static public Nod getnod(String CID)
    {
        return nods.get(CID);
    }


    static public boolean unreg(String CID)
    {
        return (nods.remove(CID) != null);
    }


    static public boolean nickReserved(String nick, String CID)
    {
        Nod x = AccountsConfig.First;
        while (x != null)
        {
            if (!x.CID.equals(CID))
            {
                if (x.LastNI != null)
                {
                    if (x.nickprotected && x.LastNI.equalsIgnoreCase(nick))
                    {
                        return true;
                    }
                }
            }
            x = x.Next;
        }
        return false;
    }
}