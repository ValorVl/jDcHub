/*
 * GrantCmd.java
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

package ru.sincore.cmd;

import java.util.StringTokenizer;

import ru.sincore.AccountsConfig;
import ru.sincore.Broadcast;
import ru.sincore.ClientHandler;
import ru.sincore.ClientNod;
import ru.sincore.HubServer;
import ru.sincore.Main;
import ru.sincore.Nod;
import ru.sincore.util.ADC;
import ru.sincore.SimpleHandler;

/**
 * The grant command, that is called either via client , either via tty.
 * Provides posiblity of adding/removing the registered properties dynamically.
 *
 * @author Pietricica
 */
public class GrantCmd
{

    /**
     * Creates a new instance of GrantCmd
     */
    public GrantCmd(ClientHandler cur_client, String cmd)
    {


        StringTokenizer curcmd = new StringTokenizer(cmd);
        curcmd.nextToken();
        if (!curcmd.hasMoreTokens())
        {
            String Help = "\nThe grant command:\n" +
                          "Usage grant user/CID [[+-]attribute1]*" +
                          "\n      +attribute adds the atribute to the registered user, - removes it." +
                          "\n      [+-]all adds all possible attributes." +
                          "\n    List of attributes: about adc adcs backup bancid banip bannick cfg chatcontrol cmdhistory drop grant gui help hideme history info kick listban listreg mass mynick password plugmin port quit redirect reg rename restart stats topic unban ureg usercount flyable key kickable modules nickprotected opchataccess overridefull overrideshare overridespam renameable";
            cur_client.sendFromBot(Help);
            return;
        }
        String who = curcmd.nextToken();
        Nod modnod = null;
        ClientNod temp = null;
        if (ADC.isCID(who))
        {
            modnod = AccountsConfig.getnod(who);
        }
        else
        {
            boolean ok = false;

            for (ClientNod tempp : SimpleHandler.getUsers())
            {
                if (tempp.cur_client.userok == 1)
                {
                    if (tempp.cur_client.NI.equalsIgnoreCase(who) && tempp.cur_client.reg.isreg)
                    {
                        modnod = AccountsConfig.getnod(tempp.cur_client.ID);
                        ok = true;
                        temp = tempp;
                    }
                }


            }
            if (!ok)
            {
                cur_client.sendFromBot(
                        "Invalid argument supplied. Use with no arguments to see usage tip.");
                return;
            }
        }
        if (!curcmd.hasMoreTokens())
        {
            cur_client.sendFromBot(modnod.getRegInfo());
            return;
        }
        String what = curcmd.nextToken();
        String aux = what;
        String toSend = "";
        toSend += "Editing Account: " + modnod.CID + "\n";
        int x = 1, y = 1;

        String UpdatedInfo = "Your profile has been changed by " + cur_client.NI + ".\n";
        while (aux != null)
        {
            if (x < 0 && y < 0 && !aux.startsWith("+"))
            {
                break;//over
            }
            what = aux;
            if (!what.startsWith("+") && !what.startsWith("-"))
            {
                toSend += "Invalid argument supplied. Use with no arguments to see usage tip.\n";
                cur_client.sendFromBot(toSend + "Done.");
                return;

            }
            boolean attribute = false;
            if (what.startsWith("+"))
            {
                attribute = true;
            }

            what = what.substring(1);
            x = what.indexOf('+');
            y = what.indexOf('-');
            int z = 0;
            if (x < 0 && y < 0)
            {
                z = what.length();//over
            }
            else if (x > 0 && y > 0)
            {
                if (x > y)
                {
                    z = y;
                }
                else
                {
                    z = x;
                }
            }
            else if (x > 0)
            {
                z = x;
            }
            else
            {
                z = y;
            }


            what = what.substring(0, z);
            aux = aux.substring(z + 1);
            what = what.toLowerCase();
            if (what.equalsIgnoreCase("adc"))
            {
                if (cur_client.reg.myMask.adc == false)
                {
                    toSend += " adc - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.adc = attribute;
                toSend += " adc modified to " + attribute + "\n";
                UpdatedInfo += " adc modified to " + attribute + "\n";
            }
            else if (what.equalsIgnoreCase("adcs"))
            {
                if (cur_client.reg.myMask.adcs == false)
                {
                    toSend += " adcs - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.adcs = attribute;
                toSend += " adcs modified to " + attribute + "\n";
                UpdatedInfo += " adcs modified to " + attribute + "\n";
            }

            else if (what.equals("about"))
            {
                if (cur_client.reg.myMask.about == false)
                {
                    toSend += " about - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.about = attribute;
                toSend += " about modified to " + attribute + "\n";
                UpdatedInfo += " about modified to " + attribute + "\n";
            }
            else if (what.equals("backup"))
            {
                if (cur_client.reg.myMask.backup == false)
                {
                    toSend += " backup - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.backup = attribute;
                toSend += " backup modified to " + attribute + "\n";
                UpdatedInfo += " backup modified to " + attribute + "\n";
            }
            else if (what.equals("bancid"))
            {
                if (cur_client.reg.myMask.bancid == false)
                {
                    toSend += " bancid - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.bancid = attribute;
                toSend += " bancid modified to " + attribute + "\n";
                UpdatedInfo += " bancid modified to " + attribute + "\n";
            }
            else if (what.equals("banip"))
            {
                if (cur_client.reg.myMask.banip == false)
                {
                    toSend += " banip - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.banip = attribute;
                toSend += " banip modified to " + attribute + "\n";
                UpdatedInfo += " banip modified to " + attribute + "\n";
            }
            else if (what.equals("bannick"))
            {
                if (cur_client.reg.myMask.bannick == false)
                {
                    toSend += " bannick - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.bannick = attribute;
                toSend += " bannick modified to " + attribute + "\n";
                UpdatedInfo += " bannick modified to " + attribute + "\n";
            }
            else if (what.equals("cfg"))
            {
                if (cur_client.reg.myMask.cfg == false)
                {
                    toSend += " cfg - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.cfg = attribute;
                toSend += " cfg modified to " + attribute + "\n";
                UpdatedInfo += " cfg modified to " + attribute + "\n";
            }
            else if (what.equals("chatcontrol"))
            {
                if (cur_client.reg.myMask.chatcontrol == false)
                {
                    toSend += " chatcontrol - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.chatcontrol = attribute;
                toSend += " chatcontrol modified to " + attribute + "\n";
                UpdatedInfo += " chatcontrol modified to " + attribute + "\n";
            }
            else if (what.equals("cmdhistory"))
            {
                if (cur_client.reg.myMask.cmdhistory == false)
                {
                    toSend += " cmdhistory - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.cmdhistory = attribute;
                toSend += " cmdhistory modified to " + attribute + "\n";
                UpdatedInfo += " cmdhistory modified to " + attribute + "\n";
            }
            else if (what.equals("drop"))
            {
                if (cur_client.reg.myMask.drop == false)
                {
                    toSend += " drop - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.drop = attribute;
                toSend += " drop modified to " + attribute + "\n";
                UpdatedInfo += " drop modified to " + attribute + "\n";
            }
            else if (what.equals("grant"))
            {
                if (cur_client.reg.myMask.grant == false)
                {
                    toSend += " grant - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.grant = attribute;
                toSend += " grant modified to " + attribute + "\n";
                UpdatedInfo += " grant modified to " + attribute + "\n";
            }
            else if (what.equals("gui"))
            {
                if (cur_client.reg.myMask.gui == false)
                {
                    toSend += " gui - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.gui = attribute;
                toSend += " gui modified to " + attribute + "\n";
                UpdatedInfo += " gui modified to " + attribute + "\n";
            }
            else if (what.equals("help"))
            {
                if (cur_client.reg.myMask.help == false)
                {
                    toSend += " help - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.help = attribute;
                toSend += " help modified to " + attribute + "\n";
                UpdatedInfo += " help modified to " + attribute + "\n";
            }
            else if (what.equals("hideme"))
            {
                if (cur_client.reg.myMask.hideme == false)
                {
                    toSend += " hideme - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.hideme = attribute;
                toSend += " hideme modified to " + attribute + "\n";
                UpdatedInfo += " hideme modified to " + attribute + "\n";
            }
            else if (what.equals("history"))
            {
                if (cur_client.reg.myMask.history == false)
                {
                    toSend += " history - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.history = attribute;
                toSend += " history modified to " + attribute + "\n";
                UpdatedInfo += " history modified to " + attribute + "\n";
            }
            else if (what.equals("info"))
            {
                if (cur_client.reg.myMask.info == false)
                {
                    toSend += " info - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.info = attribute;
                toSend += " info modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("kick"))
            {
                if (cur_client.reg.myMask.kick == false)
                {
                    toSend += " kick - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.kick = attribute;
                toSend += " kick modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("help"))
            {
                if (cur_client.reg.myMask.help == false)
                {
                    toSend += " help - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.help = attribute;
                toSend += " help modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("listban"))
            {
                if (cur_client.reg.myMask.listban == false)
                {
                    toSend += " listban - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.listban = attribute;
                toSend += " listban modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("listreg"))
            {
                if (cur_client.reg.myMask.listreg == false)
                {
                    toSend += " listreg - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.listreg = attribute;
                toSend += " listreg modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("mass"))
            {
                if (cur_client.reg.myMask.mass == false)
                {
                    toSend += " mass - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.mass = attribute;
                toSend += " mass modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("mynick"))
            {
                if (cur_client.reg.myMask.mynick == false)
                {
                    toSend += " mynick - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.mynick = attribute;
                toSend += " mynick modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }

            else if (what.equals("password"))
            {
                if (cur_client.reg.myMask.password == false)
                {
                    toSend += " password - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.password = attribute;
                toSend += " password modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("plugmin"))
            {
                if (cur_client.reg.myMask.plugmin == false)
                {
                    toSend += " plugmin - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.plugmin = attribute;
                toSend += " plugmin modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("port"))
            {
                if (cur_client.reg.myMask.port == false)
                {
                    toSend += " port - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.port = attribute;
                toSend += " port modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("quit"))
            {
                if (cur_client.reg.myMask.quit == false)
                {
                    toSend += " quit - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.quit = attribute;
                toSend += " quit modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("redirect"))
            {
                if (cur_client.reg.myMask.redirect == false)
                {
                    toSend += " redirect - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.redirect = attribute;
                toSend += " redirect modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("reg"))
            {
                if (cur_client.reg.myMask.reg == false)
                {
                    toSend += " reg - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.reg = attribute;
                toSend += " reg modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("rename"))
            {
                if (cur_client.reg.myMask.rename == false)
                {
                    toSend += " rename - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.rename = attribute;
                toSend += " rename modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("restart"))
            {
                if (cur_client.reg.myMask.restart == false)
                {
                    toSend += " restart - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.restart = attribute;
                toSend += " restart modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("stats"))
            {
                if (cur_client.reg.myMask.stats == false)
                {
                    toSend += " stats - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.stats = attribute;
                toSend += " stats modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }

            else if (what.equals("topic"))
            {
                if (cur_client.reg.myMask.topic == false)
                {
                    toSend += " topic - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.topic = attribute;
                toSend += " topic modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("unban"))
            {
                if (cur_client.reg.myMask.unban == false)
                {
                    toSend += " unban - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.unban = attribute;
                toSend += " unban modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("ureg"))
            {
                if (cur_client.reg.myMask.ureg == false)
                {
                    toSend += " ureg - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.ureg = attribute;
                toSend += " ureg modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("usercount"))
            {
                if (cur_client.reg.myMask.usercount == false)
                {
                    toSend += " usercount - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.usercount = attribute;
                toSend += " usercount modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("flyable"))
            {

                if (!modnod.setFlyable(true))
                {
                    toSend += " Error: To be flyable, account needs a password.\n";
                    continue;
                }
                modnod.accountflyable = attribute;
                if (attribute = true)
                {
                    modnod.nickprotected = false;
                }

                toSend += " flyable modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("key"))
            {
                if (cur_client.reg.key == false)
                {
                    toSend += " key - can't grant a feature you don't possess.\n";
                    continue;
                }
                if (attribute)
                {
                    if (!modnod.key)
                    {

                        for (ClientNod tempx : SimpleHandler.getUsers())
                        {
                            if (tempx.cur_client.userok == 1)
                            {
                                if (tempx.cur_client
                                        .ID
                                        .equals(modnod.CID)) //if registered guy is online
                                {
                                    Broadcast.getInstance()
                                             .broadcast("BINF " +
                                                        tempx.cur_client.SessionID +
                                                        " CT4");

                                    tempx.cur_client.CT = "4";
                                }
                            }

                        }


                    }
                    modnod.key = true;
                }
                else
                {
                    if (modnod.key)
                    {

                        for (ClientNod tempx : SimpleHandler.getUsers())
                        {
                            if (tempx.cur_client.userok == 1)
                            {
                                if (tempx.cur_client
                                        .ID
                                        .equals(modnod.CID)) //if registered guy is online
                                {
                                    Broadcast.getInstance()
                                             .broadcast("BINF " +
                                                        temp.cur_client.SessionID +
                                                        " CT2");

                                    tempx.cur_client.CT = "2";

                                }
                            }
                        }


                    }
                    modnod.key = false;
                }

                toSend += " key modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("kickable"))
            {
                if (cur_client.reg.kickable == true)
                {
                    toSend += " kickable - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.kickable = attribute;

                toSend += " kickable modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("modules"))
            {
                if (cur_client.reg.additionalModules == false)
                {
                    toSend += " modules - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.myMask.usercount = attribute;
                toSend += " modules modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("nickprotected"))
            {
                if (cur_client.reg.nickprotected == false)
                {
                    toSend += " nickprotected - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.nickprotected = attribute;

                toSend += " nickprotected modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("opchataccess"))
            {
                if (cur_client.reg.opchataccess == false)
                {
                    toSend += " opchataccess - can't grant a feature you don't possess.\n";
                    continue;
                }
                if (attribute)
                {
                    modnod.opchataccess = true;

                    for (ClientNod tempx : SimpleHandler.getUsers())
                    {
                        if (tempx.cur_client.userok == 1)
                        {
                            if (tempx.cur_client.ID.equals(modnod.CID))//if registered guy is online
                            {
                                tempx.cur_client.putOpchat(true);
                            }
                        }
                    }


                }
                else
                {


                    for (ClientNod tempx : SimpleHandler.getUsers())
                    {
                        if (tempx.cur_client.userok == 1)
                        {
                            if (tempx.cur_client.ID.equals(modnod.CID))//if registered guy is online
                            {
                                tempx.cur_client.putOpchat(false);
                            }
                        }

                    }


                    modnod.opchataccess = attribute;
                }

                toSend += " opchataccess modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("overridefull"))
            {
                if (cur_client.reg.overridefull == false)
                {
                    toSend += " overridefull - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.overridefull = attribute;

                toSend += " overridefull modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("overrideshare"))
            {
                if (cur_client.reg.overrideshare == false)
                {
                    toSend += " overrideshare - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.overrideshare = attribute;

                toSend += " overrideshare modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("overridespam"))
            {
                if (cur_client.reg.overridespam == false)
                {
                    toSend += " overridespam - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.overridespam = attribute;

                toSend += " overridespam modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("renameable"))
            {
                if (cur_client.reg.renameable == true)
                {
                    toSend += " renameable - can't grant a feature you don't possess.\n";
                    continue;
                }
                modnod.renameable = attribute;

                toSend += " renameable modified to " + attribute + "\n";
                UpdatedInfo += " " + what + " modified to " + attribute + "\n";
            }
            else if (what.equals("all"))
            {


                aux =
                        "+about+adc+adcs+backup+bancid+banip+bannick+cfg+chatcontrol+cmdhistory+drop+grant+gui+help+hideme+history+info+kick+listban+listreg+mass+mynick+password+plugmin+port+quit+redirect+reg+rename+restart+stats+topic+unban+ureg+usercount"
                        +
                        "+key-kickable+modules+nickprotected+opchataccess+overrideshare+overridespam+overridefull-renameable";
                //toSend+=" All granted.\n";
            }
            else
            {
                toSend += " unknown feature \n";
            }
            if (x < 0 && y < 0 && !aux.startsWith("+"))
            {
                break;//over
            }
        }

        cur_client.sendFromBot(toSend + "Done.");
        if (temp != null)
        {
            if (temp.cur_client.NI.equalsIgnoreCase(who)
                || temp.cur_client.ID.equals(who))
            {
                temp.cur_client.sendFromBotPM(UpdatedInfo);
            }
        }


        Main.Server.rewriteregs();
    }


    public GrantCmd(String cmd)
    {


        StringTokenizer curcmd = new StringTokenizer(cmd);
        curcmd.nextToken();
        if (!curcmd.hasMoreTokens())
        {
            String Help = "\nThe grant command:\n" +
                          "Usage grant user/CID [[+-]attribute1]*" +
                          "\n      +attribute adds the atribute to the registered user, - removes it." +
                          "\n      [+-]all adds all possible attributes." +
                          "\n    List of attributes: about adc adcs backup bancid banip bannick cfg chatcontrol cmdhistory drop grant gui help hideme history info kick listban listreg mass mynick password plugmin port quit redirect reg rename restart stats topic unban ureg usercount flyable key kickable modules nickprotected opchataccess overridefull overrideshare overridespam renameable";
            System.out.println(Help);
            return;
        }
        String who = curcmd.nextToken();
        Nod modnod = null;
        ClientNod temp = null;
        if (ADC.isCID(who))
        {
            modnod = AccountsConfig.getnod(who);
        }
        else
        {
            boolean ok = false;
            for (ClientNod tempp : SimpleHandler.getUsers())
            {
                if (tempp.cur_client.userok == 1)
                {
                    if (tempp.cur_client.NI.equalsIgnoreCase(who))
                    {
                        modnod = AccountsConfig.getnod(tempp.cur_client.ID);
                        temp = tempp;
                        ok = true;
                        break;
                    }
                }


            }
            if (!ok)
            {
                System.out
                        .println(
                                "Invalid argument supplied. Use with no arguments to see usage tip.");
                return;
            }
        }
        if (!curcmd.hasMoreTokens())
        {
            System.out.println(modnod.getRegInfo());
            return;
        }
        String what = curcmd.nextToken();
        String aux = what;
        String toSend = "";
        toSend += "Editing Account: " + modnod.CID + "\n";

        while (aux != null)
        {
            what = aux;
            if (!what.startsWith("+") && !what.startsWith("-"))
            {
                toSend += "Invalid argument supplied. Use with no arguments to see usage tipx.\n";
                return;

            }
            boolean attribute = false;
            if (what.startsWith("+"))
            {
                attribute = true;
            }

            what = what.substring(1);
            int x = what.indexOf('+');
            int y = what.indexOf('-');
            int z = 0;
            if (x < 0 && y < 0)
            {
                z = what.length();//over
            }
            else if (x > 0 && y > 0)
            {
                if (x > y)
                {
                    z = y;
                }
                else
                {
                    z = x;
                }
            }
            else if (x > 0)
            {
                z = x;
            }
            else
            {
                z = y;
            }


            what = what.substring(0, z);
            aux = aux.substring(z + 1);

            if (what.equalsIgnoreCase("adc"))
            {

                modnod.myMask.adc = attribute;
                toSend += " adc modified to " + attribute + "\n";
            }

            else if (what.equalsIgnoreCase("adcs"))
            {

                modnod.myMask.adcs = attribute;
                toSend += " adcs modified to " + attribute + "\n";
            }
            else if (what.equals("about"))
            {

                modnod.myMask.about = attribute;
                toSend += " about modified to " + attribute + "\n";
            }
            else if (what.equals("backup"))
            {

                modnod.myMask.backup = attribute;
                toSend += " backup modified to " + attribute + "\n";
            }
            else if (what.equals("bancid"))
            {

                modnod.myMask.bancid = attribute;
                toSend += " bancid modified to " + attribute + "\n";
            }
            else if (what.equals("banip"))
            {

                modnod.myMask.banip = attribute;
                toSend += " banip modified to " + attribute + "\n";
            }
            else if (what.equals("bannick"))
            {

                modnod.myMask.bannick = attribute;
                toSend += " bannick modified to " + attribute + "\n";
            }
            else if (what.equals("cfg"))
            {

                modnod.myMask.cfg = attribute;
                toSend += " cfg modified to " + attribute + "\n";
            }
            else if (what.equals("chatcontrol"))
            {

                modnod.myMask.chatcontrol = attribute;
                toSend += " chatcontrol modified to " + attribute + "\n";
            }
            else if (what.equals("cmdhistory"))
            {

                modnod.myMask.cmdhistory = attribute;
                toSend += " cmdhistory modified to " + attribute + "\n";
            }
            else if (what.equals("drop"))
            {

                modnod.myMask.drop = attribute;
                toSend += " drop modified to " + attribute + "\n";
            }
            else if (what.equals("grant"))
            {

                modnod.myMask.grant = attribute;
                toSend += " grant modified to " + attribute + "\n";
            }
            else if (what.equals("gui"))
            {

                modnod.myMask.gui = attribute;
                toSend += " gui modified to " + attribute + "\n";
            }
            else if (what.equals("help"))
            {

                modnod.myMask.help = attribute;
                toSend += " help modified to " + attribute + "\n";
            }
            else if (what.equals("hideme"))
            {

                modnod.myMask.hideme = attribute;
                toSend += " hideme modified to " + attribute + "\n";
            }
            else if (what.equals("history"))
            {

                modnod.myMask.history = attribute;
                toSend += " history modified to " + attribute + "\n";
            }
            else if (what.equals("info"))
            {

                modnod.myMask.info = attribute;
                toSend += " info modified to " + attribute + "\n";
            }
            else if (what.equals("kick"))
            {

                modnod.myMask.kick = attribute;
                toSend += " kick modified to " + attribute + "\n";
            }
            else if (what.equals("help"))
            {

                modnod.myMask.help = attribute;
                toSend += " help modified to " + attribute + "\n";
            }
            else if (what.equals("listban"))
            {

                modnod.myMask.listban = attribute;
                toSend += " listban modified to " + attribute + "\n";
            }
            else if (what.equals("listreg"))
            {

                modnod.myMask.listreg = attribute;
                toSend += " listreg modified to " + attribute + "\n";
            }
            else if (what.equals("mass"))
            {

                modnod.myMask.mass = attribute;
                toSend += " mass modified to " + attribute + "\n";
            }
            else if (what.equals("mynick"))
            {

                modnod.myMask.mynick = attribute;
                toSend += " mynick modified to " + attribute + "\n";
            }

            else if (what.equals("password"))
            {

                modnod.myMask.password = attribute;
                toSend += " password modified to " + attribute + "\n";
            }
            else if (what.equals("plugmin"))
            {

                modnod.myMask.plugmin = attribute;
                toSend += " plugmin modified to " + attribute + "\n";
            }
            else if (what.equals("port"))
            {

                modnod.myMask.port = attribute;
                toSend += " port modified to " + attribute + "\n";
            }
            else if (what.equals("quit"))
            {

                modnod.myMask.quit = attribute;
                toSend += " quit modified to " + attribute + "\n";
            }
            else if (what.equals("redirect"))
            {

                modnod.myMask.redirect = attribute;
                toSend += " redirect modified to " + attribute + "\n";
            }
            else if (what.equals("reg"))
            {

                modnod.myMask.reg = attribute;
                toSend += " reg modified to " + attribute + "\n";
            }
            else if (what.equals("rename"))
            {

                modnod.myMask.rename = attribute;
                toSend += " rename modified to " + attribute + "\n";
            }
            else if (what.equals("restart"))
            {

                modnod.myMask.restart = attribute;
                toSend += " restart modified to " + attribute + "\n";
            }
            else if (what.equals("stats"))
            {

                modnod.myMask.stats = attribute;
                toSend += " stats modified to " + attribute + "\n";
            }
            else if (what.equals("help"))
            {

                modnod.myMask.help = attribute;
                toSend += " help modified to " + attribute + "\n";
            }
            else if (what.equals("topic"))
            {

                modnod.myMask.topic = attribute;
                toSend += " topic modified to " + attribute + "\n";
            }
            else if (what.equals("unban"))
            {

                modnod.myMask.unban = attribute;
                toSend += " unban modified to " + attribute + "\n";
            }
            else if (what.equals("ureg"))
            {

                modnod.myMask.ureg = attribute;
                toSend += " ureg modified to " + attribute + "\n";
            }
            else if (what.equals("usercount"))
            {

                modnod.myMask.usercount = attribute;
                toSend += " usercount modified to " + attribute + "\n";
            }
            else if (what.equals("flyable"))
            {

                if (!modnod.setFlyable(true))
                {
                    toSend += "Error: To be flyable, account needs a password.\n";
                    continue;
                }
                modnod.accountflyable = attribute;
                if (attribute = true)
                {
                    modnod.nickprotected = false;
                }

                toSend += " flyable modified to " + attribute + "\n";
            }
            else if (what.equals("key"))
            {

                if (attribute)
                {
                    if (!modnod.key)
                    {

                        for (ClientNod tempx : SimpleHandler.getUsers())
                        {

                            if (tempx.cur_client.userok == 1)
                            {
                                if (tempx.cur_client
                                        .ID
                                        .equals(modnod.CID))//if registered guy is online
                                {
                                    Broadcast.getInstance()
                                             .broadcast("BINF " +
                                                        tempx.cur_client.SessionID +
                                                        " CT4");

                                    tempx.cur_client.CT = "4";
                                }
                            }
                        }


                    }
                    modnod.key = true;
                }
                else
                {
                    if (modnod.key)
                    {

                        for (ClientNod tempx : SimpleHandler.getUsers())
                        {
                            if (tempx.cur_client.userok == 1)
                            {
                                if (tempx.cur_client
                                        .ID
                                        .equals(modnod.CID))//if registered guy is online
                                {
                                    Broadcast.getInstance()
                                             .broadcast("BINF " +
                                                        temp.cur_client.SessionID +
                                                        " CT2");

                                    tempx.cur_client.CT = "2";

                                }
                            }
                        }


                    }
                    modnod.key = false;
                }

                toSend += " key modified to " + attribute + "\n";
            }
            else if (what.equals("kickable"))
            {

                modnod.kickable = attribute;

                toSend += " kickable modified to " + attribute + "\n";
            }
            else if (what.equals("modules"))
            {

                modnod.additionalModules = attribute;

                toSend += " modules modified to " + attribute + "\n";
            }
            else if (what.equals("nickprotected"))
            {

                modnod.nickprotected = attribute;

                toSend += " nickprotected modified to " + attribute + "\n";
            }
            else if (what.equals("opchataccess"))
            {

                if (attribute)
                {
                    modnod.opchataccess = true;

                    for (ClientNod tempx : SimpleHandler.getUsers())
                    {
                        if (tempx.cur_client.userok == 1)
                        {
                            if (tempx.cur_client.ID.equals(modnod.CID))//if registered guy is online
                            {
                                tempx.cur_client.putOpchat(true);
                            }
                        }
                    }


                }
                else
                {


                    for (ClientNod tempx : SimpleHandler.getUsers())
                    {
                        if (tempx.cur_client.userok == 1)
                        {
                            if (tempx.cur_client.ID.equals(modnod.CID))//if registered guy is online
                            {
                                tempx.cur_client.putOpchat(false);
                            }
                        }

                    }


                    modnod.opchataccess = attribute;
                }

                toSend += " opchataccess modified to " + attribute + "\n";
            }
            else if (what.equals("overridefull"))
            {

                modnod.overridefull = attribute;

                toSend += " overridefull modified to " + attribute + "\n";
            }
            else if (what.equals("overrideshare"))
            {

                modnod.overrideshare = attribute;

                toSend += " overrideshare modified to " + attribute + "\n";
            }
            else if (what.equals("overridespam"))
            {

                modnod.overridespam = attribute;

                toSend += " overridespam modified to " + attribute + "\n";
            }
            else if (what.equals("renameable"))
            {

                modnod.renameable = attribute;

                toSend += " renameable modified to " + attribute + "\n";
            }
            else if (what.equals("all"))
            {

                // modnod.renameable=modnod.kickable=modnod.accountflyable=false;
                // modnod.overridefull=modnod.key=modnod.nickprotected=modnod.overrideshare=modnod.overridespam=true;
                aux =
                        "+about+adc+adcs+backup+bancid+banip+bannick+cfg+chatcontrol+cmdhistory+drop+grant+gui+help+hideme+history+info+kick+listban+listreg+mass+mynick+password+plugmin+port+quit+redirect+reg+rename+restart+stats+topic+unban+ureg+usercount"
                        +
                        "+key-kickable+modules+nickprotected+opchataccess+overrideshare+overridespam+overridefull-renameable";
            }
            else
            {
                toSend += " unknown feature \n";
            }
            if (x < 0 && y < 0 && !aux.startsWith("+"))
            {
                break;//over
            }
        }

        System.out.println(toSend + "Done.");
        if (temp != null)
        {
            if (temp.cur_client.NI.equalsIgnoreCase(who) || temp.cur_client.ID.equals(who))
            {
                temp.cur_client
                        .sendFromBotPM(toSend.replaceAll(what + " is unknown feature \\\\n", ""));
            }
        }
        HubServer.rewriteregs();
    }

}
