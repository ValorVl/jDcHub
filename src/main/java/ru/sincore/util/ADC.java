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

import ru.sincore.Main;
import ru.sincore.SimpleHandler;
import ru.sincore.TigerImpl.Base32;
import ru.sincore.conf.Vars;

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
    public static String MOTD =
            "\n       CONGRATULATIONS you succesfully run DSHub and you are now connected to it.\n" +
            "Some reminders :\n" +
            "DSHub is ADC software so you need an ADC compatibile client. More info about ADC on http://en.wikipedia.org/wiki/Advanced_Direct_Connect\n" +
            "At the moment of this release ( October 2008 ), the following ADC clients were available:\n" +
            "dc++ 0.702, AirDC 2.01,  apexdc 1.00, strongdc  2.13 ,BCDC 0.705, or ANY later version of those will be ADC compatible.\n" +
            "So after you start the Hub, try connecting to adc://127.0.0.1:411\n" +
            "Some ADC reminders:\n" +
            "-- You need to connect to address adc:// ( or adcs:// if you use ADC Secure )\n" +
            "-- There is no default port, every time one must be specified ( like 411 on NMDC)\n" +
            "-- Accounts are on CID not nick ( you can use what nick you want )\n" +
            "-- Clients that are not ADC compat or dont use the address correctly will just hang up and you will see them at Connecting Users in stats command.\n" +
            "Oh and another thing, NMDC hublists dont work with ADC, so i got 2 fine lists that support ADC for you:\n" +
            "  www.hubtracker.com\n" +
            "  www.adchublist.com\n" +
            "Thanks for using DSHub and I hope you will have as much fun using it as I had creating it ;)\n" +
            "Also, I have been receiveing some complains lately that DSHub doesn't work in I-don't-know-what good way on some machines\n" +
            "Out of my experience, DSHub works at least acceptable, and probably those persons can't make it work good enough. I want to point our " +
            "that I do this for pleasure and to make people happy. Being accused that I don't provide a high quality software or documentation " +
            "is just something that makes me sad about continuing this project. Remember that I don't get anything for doing this, it's my free time" +
            " my work and my energy without any real benefit... (If you feel like helping in any way, e-mail me)\n\n" +

            "For latest version, updates, any suggestions, information, or just anything visit www.death-squad.ro/dshub\n" +

            "Homepage  		http://www.death-squad.ro/dshub\n" +


            "Launchpad links\n\n" +

            "Overview  		https://launchpad.net/dshub/\n" +
            "Code  			https://code.launchpad.net/dshub/\n" +
            "Bugs  			https://bugs.launchpad.net/dshub/\n" +
            "Q&A  			https://answers.launchpad.net/dshub/\n\n" +

            "Sourceforge links\n\n" +

            "Overview               http://sourceforge.net/projects/dshub/\n" +
            "Features               http://sourceforge.net/tracker/?group_id=197166&atid=960378\n" +
            "Plugins		http://sourceforge.net/tracker/?group_id=197166&atid=1020439\n" +
            "Translations		http://sourceforge.net/tracker/?group_id=197166&atid=1035463\n" +
            "SVN Checkout		https://dshub.svn.sf.net/svnroot/dshub/trunk\n" +
            "Mailing List		http://sourceforge.net/mail/?group_id=197166\n\n" +

            "Other Links\n\n" +

            "ADCPortal		http://www.adcportal.com\n\n" +


            "Have a nice hubbing !";


    public static String GreetingMsg = MOTD;

    ;


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
        return " HH" + Vars.Hub_Host + " UC" + SimpleHandler.getUserCount() + " SS" +
               SimpleHandler.getTotalShare() + " SF" + SimpleHandler.getTotalFileCount() +
               " MS" + 1024 * 1024 * Vars.min_share + " XS" + 1024 * 1024 * Vars.max_share +
               " ML" + Vars.min_sl + " XL" + Vars.max_sl + " XU" + Vars.max_hubs_user +
               " XR" + Vars.max_hubs_reg + " XO" + Vars.max_hubs_op +
               " MC" + Vars.max_users + " UP" + (System.currentTimeMillis() - Main.curtime);
    }


}