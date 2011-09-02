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

/**
 * @author Pietricica
 */
public class AccountsConfig
{
    public static int reg_count = 1;

    public static Nod First = null;


    /**
     * Creates a new instance of AccountsConfig
     */
    public AccountsConfig()
    {
        reg_count = 1;
        First = null;
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
        if (First == null)
        {
            First = newreg;
        }
        else
        {
            newreg.Next = First;
            First = newreg;
        }
        reg_count++;

    }


    static public void addReg(Nod n)
    {
        Nod newreg;
        //newreg=new Nod();
        newreg = n;
        if (n == null)
        {
            return;
        }

        if (First == null)
        {
            First = newreg;
            newreg.Next = null;
        }
        else
        {
            newreg.Next = First;
            First = newreg;
        }
        reg_count++;

    }


    static public int isReg(String CID)
    {
        if (First == null)
        {
            return 0;
        }
        Nod temp = First;
        while (temp != null)
        {
            if (temp.CID.equals(CID))
            {
                if (temp.key)
                {
                    return 2;
                }
                else
                {
                    return 1;
                }
            }
            temp = temp.Next;
        }
        return 0;
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
        if (First == null)
        {
            return null;
        }
        Nod temp = First;
        while (temp != null)
        {
            if (temp.CID.equals(CID))
            {
                return temp;
            }
            temp = temp.Next;
        }
        return null;
    }


    static public boolean unreg(String CID)
    {
        if (First == null)
        {
            return false;
        }
        Nod temp = First;
        if (First.CID.equals(CID))
        {

            First = First.Next;
            temp.Next = null;
            return true;
        }

        while (temp.Next != null && !temp.Next.CID.equals(CID))
        {
            temp = temp.Next;
        }
        if (temp.Next == null)
        {
            return false;
        }
        temp.Next = temp.Next.Next;
        reg_count--;
        return true;
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