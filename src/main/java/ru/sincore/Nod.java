/*
 * Nod.java
 *
 * Created on 02 decembrie 2007, 11:48
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

import ru.sincore.util.TimeConv;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Pietricica
 */
public class Nod implements Serializable, Cloneable
{
    public String  CID; //
    public String  Password;
    public boolean key;
    //ClientHandler CH;
    public Nod     Next;
    public boolean isreg;
    public String  LastNI;
    public String  WhoRegged;
    public Long    CreatedOn;
    public Long    LastLogin;
    public Long    TimeOnline;
    public String  LastIP;

    public boolean HideShare;
    public boolean HideMe;

    public boolean overrideshare;
    public boolean overridespam;
    public boolean overridefull;
    public boolean kickable, renameable;
    public boolean accountflyable;
    public boolean opchataccess;

    public CommandMask myMask;
    public HelpFile    myHelp;
    public boolean     nickprotected;

    public boolean additionalModules;


    public Nod()
    {
        CID = null;
        Password = "";
        Next = null;
        key = false;

        isreg = false;
        LastNI = "";
        LastIP = "";
        HideShare = false;
        HideMe = false;
        WhoRegged = null;
        overrideshare = overridespam = false;
        overridefull = false;
        accountflyable = false;
        kickable = renameable = true;
        CreatedOn = 0L;
        LastLogin = 0L;
        TimeOnline = 0L;
        myMask = new CommandMask();
        myHelp = new HelpFile(this);
        nickprotected = true;
        additionalModules = false;
    }


    protected Nod clone()
    {
        try
        {
            return (Nod) super.clone();
        }
        catch (CloneNotSupportedException e)
        {
            System.out.println("Cloning not allowed.");
            return this;
        }
    }


    public String getRegInfo()
    {
        String retString;
        if (this.key)
        {
            retString = "\nOperator.";
        }
        else
        {
            retString = "\nRegistered.";
        }
        Date d = new Date(this.CreatedOn);
        retString = retString +
                    " Reg Info:\nLast Nick : " +
                    this.LastNI +
                    "\nLast IP: " +
                    this.LastIP +
                    "\nRegged by: " +
                    this.WhoRegged +
                    " on " +
                    d.toString();
        retString += "\nLast LogIn: " +
                     new Date(this.LastLogin).toString() +
                     "\nTime Online: " +
                     TimeConv.getStrTime(this.TimeOnline) +
                     "\nOverride share restrictions? " +
                     (this.overrideshare ? "yes" : "no")
                     +
                     "\nOverride spam settings? " +
                     (this.overridespam ? "yes" : "no") +
                     "\nCan be renamed? " +
                     (this.renameable ? "yes" : "no") +
                     "\nPassword set? " +
                     (this.Password.length() > 0 ? "yes" : "no") +
                     "\nIs hidden? " +
                     (this.HideMe ? "yes" : "no") +
                     "\nShare hidden? " +
                     (this.HideShare ? "yes" : "no") +
                     "\nFlyable? " +
                     (this.accountflyable ? "yes" : "no") +
                     "\n" +
                     "Last nick protected? " +
                     (this.nickprotected ? "yes" : "no") +
                     "\n" +
                     "---------------------Profile----------------------\n";
        String Help = retString;
        Nod curAcc = this;
        if (this.myMask.about)
        {
            Help += "+about";
        }
        else
        {
            Help += "-about";
        }

        if (curAcc.myMask.adc)
        {
            Help += "+adc";
        }
        else
        {
            Help += "-adc";
        }
        if (curAcc.myMask.adcs)
        {
            Help += "+adcs";
        }
        else
        {
            Help += "-adcs";
        }
        if (curAcc.myMask.backup)
        {
            Help += "+backup";
        }
        else
        {
            Help += "-backup";
        }
        if (curAcc.myMask.bancid)
        {
            Help += "+bancid";
        }
        else
        {
            Help += "-bancid";
        }
        if (curAcc.myMask.banip)
        {
            Help += "+banip";
        }
        else
        {
            Help += "-banip";
        }
        if (curAcc.myMask.bannick)
        {
            Help += "+bannick";
        }
        else
        {
            Help += "-bannick";
        }
        if (curAcc.myMask.cfg)
        {
            Help += "+cfg";
        }
        else
        {
            Help += "-cfg";
        }
        if (curAcc.myMask.chatcontrol)
        {
            Help += "+chatcontrol";
        }
        else
        {
            Help += "-chatcontrol";
        }
        if (curAcc.myMask.cmdhistory)
        {
            Help += "+cmdhistory";
        }
        else
        {
            Help += "-cmdhistory";
        }
        if (curAcc.myMask.drop)
        {
            Help += "+drop";
        }
        else
        {
            Help += "-drop";
        }
        if (curAcc.myMask.grant)
        {
            Help += "+grant";
        }
        else
        {
            Help += "-grant";
        }
        if (curAcc.myMask.gui)
        {
            Help += "+gui";
        }
        else
        {
            Help += "-gui";
        }
        if (curAcc.myMask.help)
        {
            Help += "+help";
        }
        else
        {
            Help += "-help";
        }
        if (curAcc.myMask.hideme)
        {
            Help += "+hideme";
        }
        else
        {
            Help += "-hideme";
        }
        if (curAcc.myMask.history)
        {
            Help += "+history";
        }
        else
        {
            Help += "-history";
        }
        if (curAcc.myMask.info)
        {
            Help += "+info";
        }
        else
        {
            Help += "-info";
        }
        if (curAcc.myMask.kick)
        {
            Help += "+kick";
        }
        else
        {
            Help += "-kick";
        }
        if (curAcc.myMask.listban)
        {
            Help += "+listban";
        }
        else
        {
            Help += "-listban";
        }
        if (curAcc.myMask.listreg)
        {
            Help += "+listreg";
        }
        else
        {
            Help += "-listreg";
        }
        if (curAcc.myMask.mass)
        {
            Help += "+mass";
        }
        else
        {
            Help += "-mass";
        }
        if (curAcc.myMask.mynick)
        {
            Help += "+mynick";
        }
        else
        {
            Help += "-mynick";
        }
        if (curAcc.myMask.password)
        {
            Help += "+password";
        }
        else
        {
            Help += "-password";
        }
        if (curAcc.myMask.plugmin)
        {
            Help += "+plugmin";
        }
        else
        {
            Help += "-plugmin";
        }
        if (curAcc.myMask.port)
        {
            Help += "+port";
        }
        else
        {
            Help += "-port";
        }
        if (curAcc.myMask.quit)
        {
            Help += "+quit";
        }
        else
        {
            Help += "-quit";
        }
        if (curAcc.myMask.redirect)
        {
            Help += "+redirect";
        }
        else
        {
            Help += "-redirect";
        }
        if (curAcc.myMask.reg)
        {
            Help += "+reg";
        }
        else
        {
            Help += "-reg";
        }
        if (curAcc.myMask.rename)
        {
            Help += "+rename";
        }
        else
        {
            Help += "-rename";
        }
        if (curAcc.myMask.restart)
        {
            Help += "+restart";
        }
        else
        {
            Help += "-restart";
        }
        if (curAcc.myMask.stats)
        {
            Help += "+stats";
        }
        else
        {
            Help += "-stats";
        }
        if (curAcc.myMask.topic)
        {
            Help += "+topic";
        }
        else
        {
            Help += "-topic";
        }
        if (curAcc.myMask.unban)
        {
            Help += "+unban";
        }
        else
        {
            Help += "-unban";
        }
        if (curAcc.myMask.ureg)
        {
            Help += "+ureg";
        }
        else
        {
            Help += "-ureg";
        }
        if (curAcc.myMask.usercount)
        {
            Help += "+usercount";
        }
        else
        {
            Help += "-usercount";
        }
        if (curAcc.accountflyable)
        {
            Help += "+flyable";
        }
        else
        {
            Help += "-flyable";
        }
        if (curAcc.key)
        {
            Help += "+key";
        }
        else
        {
            Help += "-key";
        }
        if (curAcc.kickable)
        {
            Help += "+kickable";
        }
        else
        {
            Help += "-kickable";
        }
        if (curAcc.additionalModules)
        {
            Help += "+modules";
        }
        else
        {
            Help += "-modules";
        }
        if (curAcc.nickprotected)
        {
            Help += "+nickprotected";
        }
        else
        {
            Help += "-nickprotected";
        }
        if (curAcc.opchataccess)
        {
            Help += "+opchataccess";
        }
        else
        {
            Help += "-opchataccess";
        }
        if (curAcc.overridefull)
        {
            Help += "+overridefull";
        }
        else
        {
            Help += "-overridefull";
        }
        if (curAcc.overrideshare)
        {
            Help += "+overrideshare";
        }
        else
        {
            Help += "-overrideshare";
        }
        if (curAcc.overridespam)
        {
            Help += "+overridespam";
        }
        else
        {
            Help += "-overridespam";
        }
        if (curAcc.renameable)
        {
            Help += "+renameable";
        }
        else
        {
            Help += "-renameable";
        }


        return Help;


    }


    public boolean setFlyable(boolean x)
    {
        if (x)
        {
            if (this.Password.length() < 1)
            {
                return false;
            }
            this.accountflyable = true;
            this.nickprotected = false;
        }
        else
        {
            this.accountflyable = false;
        }
        return true;
    }

}
