/*
 * ADCConfig.java
 *
 * Created on 03 septembrie 2007, 18:17
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

package ru.sincore.conf;

import java.util.StringTokenizer;

import ru.sincore.ClientHandler;
import ru.sincore.util.ADC;

/**
 * The ADC advanced configuration panel main file ( for commands only, via client or tty )
 *
 * @author Pietricica
 */
public class ADCConfig
{
    ClientHandler cur_client;
    //String msg;


    /**
     * Creates a new instance of ADCConfig
     */
    public ADCConfig(ClientHandler CH, String msg)
    {
        cur_client = CH;
        //this.msg=msg;
        msg = ADC.retNormStr(msg.substring(1));
        StringTokenizer TK = new StringTokenizer(msg);

        TK.nextToken();
        if (!TK.hasMoreTokens())
        {
            cur_client.sendFromBot("\nADC Advanced Configuration Settings.\n" +
                                   "---------------------------------------------------------------------------\n" +
                                   "To modify a value use \" adc _context__name_ on/off \"\n" +
                                   "Example : \"adc bmsg off\", where \"b\" is the context,\n" +
                                   "\"msg\" is the name and off is the specifier of what to do.\n" +
                                   "Current Settings : \n" +
                                   " MSG :     STA:     CTM:      RCM:     INF:       SCH:       RES:      PAS:      SUP:\n" +
                                   "B " +
                                   (Vars.BMSG == 1 ? "on" : "off") +
                                   "        B " +
                                   (Vars.BSTA == 1 ? "on" : "off") +
                                   "       B " +
                                   (Vars.BCTM == 1 ? "on" : "off") +
                                   "       B " +
                                   (Vars.BRCM == 1 ? "on" : "off") +
                                   "       B " +
                                   (Vars.BINF == 1 ? "on" : "off") +
                                   "       B " +
                                   (Vars.BSCH == 1 ? "on" : "off") +
                                   "       B " +
                                   (Vars.BRES == 1 ? "on" : "off") +
                                   "       B " +
                                   (Vars.BPAS == 1 ? "on" : "off") +
                                   "       B " +
                                   (Vars.BSUP == 1 ? "on" : "off") +

                                   "\n" +

                                   "D " +
                                   (Vars.DMSG == 1 ? "on" : "off") +
                                   "        D " +
                                   (Vars.DSTA == 1 ? "on" : "off") +
                                   "      D " +
                                   (Vars.DCTM == 1 ? "on" : "off") +
                                   "       D " +
                                   (Vars.DRCM == 1 ? "on" : "off") +
                                   "       D " +
                                   (Vars.DINF == 1 ? "on" : "off") +
                                   "      D " +
                                   (Vars.DSCH == 1 ? "on" : "off") +
                                   "       D " +
                                   (Vars.DRES == 1 ? "on" : "off") +
                                   "      D " +
                                   (Vars.DPAS == 1 ? "on" : "off") +
                                   "       D " +
                                   (Vars.DSUP == 1 ? "on" : "off") +
                                   "\n" +

                                   "E " +
                                   (Vars.EMSG == 1 ? "on" : "off") +
                                   "        E " +
                                   (Vars.ESTA == 1 ? "on" : "off") +
                                   "      E " +
                                   (Vars.ECTM == 1 ? "on" : "off") +
                                   "        E " +
                                   (Vars.ERCM == 1 ? "on" : "off") +
                                   "       E " +
                                   (Vars.EINF == 1 ? "on" : "off") +
                                   "      E " +
                                   (Vars.ESCH == 1 ? "on" : "off") +
                                   "       E " +
                                   (Vars.ERES == 1 ? "on" : "off") +
                                   "       E " +
                                   (Vars.EPAS == 1 ? "on" : "off") +
                                   "       E " +
                                   (Vars.ESUP == 1 ? "on" : "off") +
                                   "\n" +
                                   "F " +
                                   (Vars.FMSG == 1 ? "on" : "off") +
                                   "        F " +
                                   (Vars.FSTA == 1 ? "on" : "off") +
                                   "       F " +
                                   (Vars.FCTM == 1 ? "on" : "off") +
                                   "        F " +
                                   (Vars.FRCM == 1 ? "on" : "off") +
                                   "        F " +
                                   (Vars.FINF == 1 ? "on" : "off") +
                                   "      F " +
                                   (Vars.FSCH == 1 ? "on" : "off") +
                                   "       F " +
                                   (Vars.FRES == 1 ? "on" : "off") +
                                   "       F " +
                                   (Vars.FPAS == 1 ? "on" : "off") +
                                   "        F " +
                                   (Vars.FSUP == 1 ? "on" : "off") +
                                   "\n" +
                                   "H " +
                                   (Vars.HMSG == 1 ? "on" : "off") +
                                   "       H " +
                                   (Vars.HSTA == 1 ? "on" : "off") +
                                   "       H " +
                                   (Vars.HCTM == 1 ? "on" : "off") +
                                   "       H " +
                                   (Vars.HRCM == 1 ? "on" : "off") +
                                   "       H " +
                                   (Vars.HINF == 1 ? "on" : "off") +
                                   "      H " +
                                   (Vars.HSCH == 1 ? "on" : "off") +
                                   "       H " +
                                   (Vars.HRES == 1 ? "on" : "off") +
                                   "      H " +
                                   (Vars.HPAS == 1 ? "on" : "off") +
                                   "       H " +
                                   (Vars.HSUP == 1 ? "on" : "off") +
                                   ""


                                  );
            return;
        }
        String nameValue = TK.nextToken();
        if (!TK.hasMoreTokens())
        {
            String tempStr = "\nADC Advanced Configuration Settings.\n" +
                             "---------------------------------------------------------------------------\n  " +
                             nameValue.toUpperCase() +
                             " is currently ";
            if (nameValue.equalsIgnoreCase("BMSG"))
            {
                tempStr += (Vars.BMSG == 1 ? "on." : "off.");
            }
            else if (nameValue.equalsIgnoreCase("DMSG"))
            {
                tempStr += Vars.DMSG == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("EMSG"))
            {
                tempStr += Vars.EMSG == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("FMSG"))
            {
                tempStr += Vars.FMSG == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("HMSG"))
            {
                tempStr += Vars.HMSG == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("BSTA"))
            {
                tempStr += (Vars.BSTA == 1 ? "on." : "off.");
            }
            else if (nameValue.equalsIgnoreCase("DSTA"))
            {
                tempStr += Vars.DSTA == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("ESTA"))
            {
                tempStr += Vars.ESTA == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("FSTA"))
            {
                tempStr += Vars.FSTA == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("HSTA"))
            {
                tempStr += Vars.HSTA == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("BCTM"))
            {
                tempStr += (Vars.BCTM == 1 ? "on." : "off.");
            }
            else if (nameValue.equalsIgnoreCase("DCTM"))
            {
                tempStr += Vars.DCTM == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("ECTM"))
            {
                tempStr += Vars.ECTM == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("FCTM"))
            {
                tempStr += Vars.FCTM == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("HCTM"))
            {
                tempStr += Vars.HCTM == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("BRCM"))
            {
                tempStr += (Vars.BRCM == 1 ? "on." : "off.");
            }
            else if (nameValue.equalsIgnoreCase("DRCM"))
            {
                tempStr += Vars.DRCM == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("ERCM"))
            {
                tempStr += Vars.ERCM == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("FRCM"))
            {
                tempStr += Vars.FRCM == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("HRCM"))
            {
                tempStr += Vars.HRCM == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("BINF"))
            {
                tempStr += (Vars.BINF == 1 ? "on." : "off.");
            }
            else if (nameValue.equalsIgnoreCase("DINF"))
            {
                tempStr += Vars.DINF == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("EINF"))
            {
                tempStr += Vars.EINF == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("FINF"))
            {
                tempStr += Vars.FINF == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("HINF"))
            {
                tempStr += Vars.HINF == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("BSCH"))
            {
                tempStr += (Vars.BSCH == 1 ? "on." : "off.");
            }
            else if (nameValue.equalsIgnoreCase("DSCH"))
            {
                tempStr += Vars.DSCH == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("ESCH"))
            {
                tempStr += Vars.ESCH == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("FSCH"))
            {
                tempStr += Vars.FSCH == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("HSCH"))
            {
                tempStr += Vars.HSCH == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("BRES"))
            {
                tempStr += (Vars.BRES == 1 ? "on." : "off.");
            }
            else if (nameValue.equalsIgnoreCase("DRES"))
            {
                tempStr += Vars.DRES == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("ERES"))
            {
                tempStr += Vars.ERES == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("FRES"))
            {
                tempStr += Vars.FRES == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("HRES"))
            {
                tempStr += Vars.HRES == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("BPAS"))
            {
                tempStr += (Vars.BPAS == 1 ? "on." : "off.");
            }
            else if (nameValue.equalsIgnoreCase("DPAS"))
            {
                tempStr += Vars.DPAS == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("EPAS"))
            {
                tempStr += Vars.EPAS == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("FPAS"))
            {
                tempStr += Vars.FPAS == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("HPAS"))
            {
                tempStr += Vars.HPAS == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("BSUP"))
            {
                tempStr += (Vars.BPAS == 1 ? "on." : "off.");
            }
            else if (nameValue.equalsIgnoreCase("DSUP"))
            {
                tempStr += Vars.DSUP == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("ESUP"))
            {
                tempStr += Vars.ESUP == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("FSUP"))
            {
                tempStr += Vars.FSUP == 1 ? "on." : "off.";
            }
            else if (nameValue.equalsIgnoreCase("HSUP"))
            {
                tempStr += Vars.HSUP == 1 ? "on." : "off.";
            }
            cur_client.sendFromBot(tempStr);
            return;
        }
        String Specifier = TK.nextToken();
        String tempStr = "\nADC Advanced Configuration Settings.\n" +
                         "---------------------------------------------------------------------------\n  " +
                         "Setting " +
                         nameValue.toUpperCase();
        if (nameValue.equalsIgnoreCase("BMSG"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.BMSG = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.BMSG = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("DMSG"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.DMSG = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.DMSG = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("EMSG"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.EMSG = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.EMSG = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("FMSG"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.FMSG = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.FMSG = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("HMSG"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.HMSG = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.HMSG = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("BSTA"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.BSTA = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.BSTA = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("DSTA"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.DSTA = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.DSTA = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("ESTA"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.ESTA = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.ESTA = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("FSTA"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.FSTA = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.FSTA = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("HSTA"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.HSTA = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.HSTA = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("BCTM"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.BCTM = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.BCTM = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("DCTM"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.DCTM = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.DCTM = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("ECTM"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.ECTM = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.ECTM = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("FCTM"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.FCTM = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.FCTM = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("HCTM"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.HCTM = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.HCTM = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }

        else if (nameValue.equalsIgnoreCase("BRCM"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.BRCM = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.BRCM = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("DRCM"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.DRCM = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.DRCM = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("ERCM"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.ERCM = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.ERCM = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("FRCM"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.FRCM = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.FRCM = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("HRCM"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.HRCM = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.HRCM = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("BINF"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.BINF = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.BINF = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("DINF"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.DINF = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.DINF = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("EINF"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.EINF = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.EINF = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("FINF"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.FINF = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.FINF = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("HINF"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.HINF = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.HINF = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("BSCH"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.BSCH = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.BSCH = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("DSCH"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.DSCH = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.DSCH = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("ESCH"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.ESCH = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.ESCH = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("FSCH"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.FSCH = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.FSCH = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("HSCH"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.HSCH = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.HSCH = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }

        else if (nameValue.equalsIgnoreCase("BRES"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.BRES = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.BRES = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("DRES"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.DRES = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.DRES = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("ERES"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.ERES = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.ERES = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("FRES"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.FRES = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.FRES = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("HRES"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.HRES = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.HRES = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("BPAS"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.BPAS = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.BPAS = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("DPAS"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.DPAS = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.DPAS = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("EPAS"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.EPAS = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.EPAS = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("FPAS"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.FPAS = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.FPAS = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("HPAS"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.HPAS = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.HPAS = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("BSUP"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.BSUP = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.BSUP = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("DSUP"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.DSUP = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.DSUP = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("ESUP"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.ESUP = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.ESUP = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("FSUP"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.FSUP = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.FSUP = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }
        else if (nameValue.equalsIgnoreCase("HSUP"))
        {
            if (Specifier.equalsIgnoreCase("on"))
            {
                Vars.HSUP = 1;
                tempStr += " on.";
            }
            else if (Specifier.equalsIgnoreCase("off"))
            {
                Vars.HSUP = 0;
                tempStr += " off.";
            }
            else
            {
                cur_client.sendFromBot("Invalid Specifier.");
                return;
            }
        }


        cur_client.sendFromBot(tempStr);

    }

}
