package ru.sincore.util;
/*
 * ADC.java
 *
 * Created on 04 martie 2007, 13:20
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

import ru.sincore.ConfigLoader;
import ru.sincore.Main;
import ru.sincore.SessionManager;
import ru.sincore.TigerImpl.Base32;

import static ru.sincore.ConfigLoader.MAX_USERS;

/**
 * This class is the main storage place for ADC command that hub has to send.
 * Also contains functions to modify strings from normal to ADC type and viceversa.
 *
 * @author Pietricica
 */
abstract public class ADC
{


    /**
     * First string to send to connecting client ;)
     */
    public static final String Init = "ISUP ADBASE ADTIGR ADUCM0 ADPING ADADC0";
            //adding basic ucmds, adding tiger hash support, adding PING, adding basic ADCS

    /**
     * ISID = session id string for connecting client
     */
    public static final String ISID = "ISID";

    /**
     * The default motd.
     */
    public static String MOTD =""; //TODO Load in file


    public static String GreetingMsg = MOTD;


    public static String retNormStr(String blah)
    {

        return blah.replaceAll("\\\\s", " ")
                   .replaceAll("\\\\n", "\n")
                   .replaceAll("\\\\\\\\", "\\\\")
                   .replaceAll("\\\\ ", "\\\\s")
                   .replaceAll("\\\\\\n", "\\\\n");
    }


    public static String retADCStr(String blah)
    {
        return blah.replaceAll("\\\\", "\\\\\\\\")
                   .replaceAll(" ", "\\\\s")
                   .replaceAll("\n", "\\\\n");
    }


    public static boolean isIP(String blah)
    {
        return blah.matches(
                "\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");


    }


    public static boolean isCID(String blah)
    {
        if (blah.length() != 39)
        {
            return false;
        }
        try
        {
            Base32.decode(blah);
        }
        catch (IllegalArgumentException iae)
        {
            return false;
        }
        return true;

    }


    public static String getPingString()
    {
        return " HH" + ConfigLoader.HUB_LISTEN + " UC" + SessionManager.getUserCount() + " SS" +
               SessionManager.getTotalShare() + " SF" + SessionManager.getTotalFileCount() +
               " MS" + 1024 * 1024 * ConfigLoader.MIN_SHARE_SIZE + " XS" + 1024 * 1024 * ConfigLoader.MAX_SHARE_SIZE +
               " ML" + ConfigLoader.MIN_SLOT_COUNT + " XL" + ConfigLoader.MAX_SLOT_COUNT + " XU" + ConfigLoader.MAX_HUBS_USERS +
               " XR" + ConfigLoader.MAX_HUBS_REGISTERED + " XO" + ConfigLoader.MAX_OP_IN_HUB +
               " MC" + ConfigLoader.MAX_USERS + " UP" + (System.currentTimeMillis() - Main.curtime);
    }


}