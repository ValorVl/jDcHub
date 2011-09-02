/*
 * MSG.java
 *
 * Created on 27 septembrie 2007, 12:14
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

package dshub.ProtoCmds;

import dshub.*;
import dshub.Exceptions.STAException;
import dshub.conf.ADCConfig;
import dshub.conf.Vars;
import dshub.util.ADC;
import dshub.util.Constants;
import dshub.util.STAError;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * Implementation of the MSG command in ADC protocol.
 *
 * @author Pietricica
 */
public class MSG
{

    /**
     * Creates a new instance of MSG
     */
    public MSG(ClientHandler cur_client, String State, String Issued_Command)
            throws STAException
    {
        if (State.equals("IDENTIFY") || State.equals("VERIFY") || State.equals("PROTOCOL"))
        {
            new STAError(cur_client,
                         200 + Constants.STA_INVALID_STATE,
                         "MSG Invalid State.",
                         "FC",
                         Issued_Command.substring(0, 4));
            return;
        }
        if (!cur_client.reg.overridespam)
        {
            switch (Issued_Command.charAt(0))
            {
                case 'B':
                    if (Vars.BMSG != 1)
                    {
                        new STAError(cur_client, 100, "MSG Invalid Context B");
                        return;
                    }
                    break;
                case 'E':
                    if (Vars.EMSG != 1)
                    {
                        new STAError(cur_client, 100, "MSG Invalid Context E");
                        return;
                    }
                    break;
                case 'D':
                    if (Vars.DMSG != 1)
                    {
                        new STAError(cur_client, 100, "MSG Invalid Context D");
                        return;
                    }
                    break;
                case 'F':
                    if (Vars.FMSG != 1)
                    {
                        new STAError(cur_client, 100, "MSG Invalid Context F");
                        return;
                    }
                    break;
                case 'H':
                    if (Vars.HMSG != 1)
                    {
                        new STAError(cur_client, 100, "MSG Invalid Context H");
                        return;
                    }

            }
        }
        //System.out.println (Issued_Command) ;
        StringTokenizer tok = new StringTokenizer(Issued_Command, " ");
        String aux = tok.nextToken();
        if (Issued_Command.charAt(0) == 'H') //for hub only, special check
        {
            if (aux.equals("Test"))
            {
                cur_client.sendFromBot("Test OK.");
            }
            return;
        }

        if (!tok.nextToken().equals(cur_client.SessionID))
        {
            new STAError(cur_client, 200, "Protocol Error. Wrong SID supplied.");
            return;
        }
        String pmsid = null;
        if (Issued_Command.charAt(0) == 'D' || Issued_Command.charAt(0) == 'E')
        {
            pmsid = tok.nextToken();
        }
        String message = tok.nextToken();

        if (message.length() > Vars.max_chat_msg)
        {
            if (
                    !(cur_client.reg.overridespam))
            {
                new STAError(cur_client, 100, "Message exceeds maximum lenght.");
                return;
            }
        }

        if (!cur_client.reg.overridespam &&
            !(message.substring(1).toLowerCase().startsWith("chatcontrol")))
        {
            int index = Main.listaBanate.isOK(message);


            //  System.out.println(index);
            if (index != -1)//not ok
            {
                if (!((Issued_Command.startsWith("E") || Issued_Command.startsWith("D")) //if pm
                      && (Main.listaBanate.getPrAt(index) & BannedWord.privatechat) == 0))
                //and private chat control activated
                {
                    System.out.println("ok");
                    long what = Main.listaBanate.getPrAt(index);
                    /*
                                     static final long dropped=1;
                static final long kicked=2;
                static final long noAction=4;
                static final long hidden=8;
                static final long replaced=16;
                static final long modified=32;
                static final long allclient=7;
                static final long allword=56;
                public static final long privatechat=64;
                public static final long notify=128;  194=128+64+2
                public static final long searches=256;*/
                    //  long what=56;
                    //System.out.println(what);
                    boolean ret = false;
                    boolean kick = false;
                    if (what % 2 == 1)
                    {

                        ret = true;
                        kick = true;

                        // System.out.println("flag contine 1");

                    }
                    what /= 2;
                    if (what % 2 == 1)
                    {
                        //System.out.println("flag contine 2");
                        cur_client.myNod.kickMeByBot("You typed forbidden word.", 3);
                    }
                    what /= 2;
                    if (what % 2 == 1)
                    {
                        //System.out.println("flag contine 4");
                        ;
                    }
                    what /= 2;
                    if (what % 2 == 1)
                    {
                        //System.out.println("flag contine 8");
                        ;
                        ret = true;
                    }
                    what /= 2;
                    if (what % 2 == 1)
                    {
                        //System.out.println("flag contine 16");
                        Issued_Command = Issued_Command.replace(message, "****");

                    }
                    what /= 2;
                    if (what % 2 == 1)
                    {
                        //System.out.println("flag contine 32");
                        Issued_Command = Issued_Command.replace(message,
                                                                ADC.retADCStr(Main.listaBanate
                                                                                      .getReplAt(
                                                                                              index)));

                    }
                    what /= 2;
                    if (what % 2 == 1)
                    {
                        ;//private chat too
                    }
                    what /= 2;
                    if (what % 2 == 1)
                    {
                        ;//notify opchat :
                        for (ClientNod temp : SimpleHandler.getUsers())
                        {
                            if (temp.cur_client.reg.isreg)
                            {
                                temp.cur_client
                                        .sendToClient("EMSG ABCD " +
                                                      temp.cur_client.SessionID +
                                                      " User\\s{" +
                                                      cur_client.NI +
                                                      "}\\swith\\sIP\\s{" +
                                                      cur_client.RealIP +
                                                      "}\\sand" +
                                                      "\\sCID\\s{" +
                                                      cur_client.ID +
                                                      "}\\sused\\sforbidden\\sword\\s:\\s" +
                                                      message +
                                                      " PMABCD");
                            }

                        }
                        // System.out.println("notying");
                    }
                    if (kick)
                    {
                        new STAError(cur_client, 200, "You typed forbidden word.");
                    }
                    if (ret)
                    {
                        return;
                    }
                }
            }
        }
        String thissid = null;
        int me = 0;
        while (tok.hasMoreElements())
        {
            aux = tok.nextToken();
            if (aux.startsWith("PM"))
            {
                thissid = aux.substring(2);
            }
            if (aux.startsWith("ME"))
            {
                if (aux.substring(2).equals("1"))
                {
                    me = 1;
                }
                else
                {
                    new STAError(cur_client,
                                 100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                                 "MSG Invalid Flag.");
                    return;
                }
            }


        }
        long now = System.currentTimeMillis();
        if (cur_client.LastChatMsg != 0)
        {
            if (now - cur_client.LastChatMsg < Vars.chat_interval)
            {
                if (!cur_client.reg.overridespam)

                {
                    new STAError(cur_client,
                                 100,
                                 "Chatting Too Fast. Minimum chat interval " +
                                 String.valueOf(Vars.chat_interval) +
                                 " .You made " +
                                 String.valueOf(now - cur_client.LastChatMsg) +
                                 ".");
                    return;
                }

            }

            else
            {
                cur_client.LastChatMsg = now;
            }
        }
        else
        {
            cur_client.LastChatMsg = now;
        }


        if (Issued_Command.charAt(0) == 'B') //broadcast
        {
            if (pmsid != null)
            {
                new STAError(cur_client, 100, "MSG Can't Broadcast PM.");
                return;
            }
            // ok now lets check that the chat is a command....
            if (cur_client.reg.isreg && message.charAt(0) == '!' ||
                cur_client.reg.isreg &&
                message.charAt(0) == '+' &&
                !(message.substring(1, message.length())
                         .equalsIgnoreCase("myinf"))) //ok.. command mode.
            {

                if ((message.toLowerCase().startsWith("!adc") ||
                     message.toLowerCase().startsWith("+adc")) &&
                    (message.length() > 4 ?
                     message.toLowerCase().charAt(4) != 's' :
                     true))//adc adv config panel
                {
                    cur_client.sendFromBot("[adc:] " + ADC.retNormStr(message));
                    new ADCConfig(cur_client, message);

                }
                else if (message.toLowerCase().startsWith("!cfg") ||
                         message.toLowerCase().startsWith("+cfg"))//config settings
                {
                    cur_client.sendFromBot("[config:] " + ADC.retNormStr(message));
                    new CommandParser(cur_client, message);

                }
                else
                {
                    cur_client.sendFromBot("[command:] " + ADC.retNormStr(message));
                    new CommandParser(cur_client, message);

                }
            }
            else if (message.equalsIgnoreCase("+myinf") || message.equalsIgnoreCase("!myinf"))
            {
                String toSend;

                toSend = "[Your information: ]\n Nick: " +
                         cur_client.NI +
                         "\n SID: {" +
                         cur_client.SessionID +
                         "}\n CID: {" +
                         cur_client.ID +
                         "}\n PID: {" +
                         cur_client.PD +
                         "}\n IP address: " +
                         cur_client.RealIP
                ;
                if (!cur_client.reg.isreg)
                {
                    toSend += "\nRegular user.";
                }
                else
                {
                    toSend += cur_client.reg.getRegInfo();
                }
                cur_client.sendFromBot(toSend);
            }
            else


            {
                Broadcast.getInstance().broadcast(Issued_Command);
            }
            //System.out.println("acum am trimis broadcast de la "+cur_client.ID);
            //System.out.println (Issued_Command);
        }
        else if (Issued_Command.charAt(0) == 'E') //echo direct msg
        {
            if (pmsid == null)
            {
                new STAError(cur_client, 100, "MSG Can't PM to Nobody.");
                return;
            }
            if (!thissid.equals(cur_client.SessionID))
            {
                new STAError(cur_client, 100, "MSG PM not returning to self.");
                return;
            }
            if (pmsid.equals("DCBA"))
            {
                //talking to bot security
                cur_client.sendToClient(Issued_Command);
                // ok now lets check that the chat is a command....
                if (cur_client.reg.isreg && message.charAt(0) == '!' ||
                    cur_client.reg.isreg && message.charAt(0) == '+') //ok.. command mode.
                {
                    if (message.toLowerCase().startsWith("!adc") ||
                        message.toLowerCase().startsWith("+adc"))//adc adv config panel
                    {
                        cur_client.sendFromBot("[adc:] " + ADC.retNormStr(message));
                        new ADCConfig(cur_client, message);

                    }
                    else if (message.toLowerCase().startsWith("!cfg") ||
                             message.toLowerCase().startsWith("+cfg"))//config settings
                    {
                        cur_client.sendFromBot("[config:] " + ADC.retNormStr(message));
                        new CommandParser(cur_client, message);

                    }
                    else
                    {
                        cur_client.sendFromBot("[command:] " + ADC.retNormStr(message));
                        new CommandParser(cur_client, message);

                    }
                }
                //cur_client.sendToClient ("EMSG DCBA "+cur_client.SessionID+" Hello ! PMDCBA");
                return;
            }
            else if (!pmsid.equals("ABCD"))
            {
                for (ClientNod temp : SimpleHandler.getUsers())
                {
                    if (temp.cur_client.SessionID.equals(pmsid))
                    {
                        temp.cur_client.sendToClient(Issued_Command);
                        cur_client.sendToClient(Issued_Command);
                        return;
                    }
                }
                //talking to inexisting client

                new STAError(cur_client,
                             100,
                             "MSG User not found."); //not kick, maybe the other client just left after he sent the msg;
                return;


            }
            else
            {
                //talking to bot
                //must send to all ops...

                //cant broadcast coz must send each;s SID
                for (ClientNod temp : SimpleHandler.getUsers())
                {
                    if (temp.cur_client.userok == 1)
                    {
                        if (temp.cur_client.reg.isreg && !temp.cur_client.equals(cur_client))
                        {
                            temp.cur_client
                                    .sendToClient("EMSG " +
                                                  cur_client.SessionID +
                                                  " " +
                                                  temp.cur_client.SessionID +
                                                  " " +
                                                  message +
                                                  " PMABCD");
                        }
                    }

                }


            }
            cur_client.sendToClient(Issued_Command);


        }
        else if (Issued_Command.charAt(0) == 'D') //direct direct msg
        {
            if (pmsid == null)
            {
                new STAError(cur_client, 100, "MSG Can't PM to Nobody.");
                return;
            }
            if (!thissid.equals(cur_client.SessionID))
            {
                new STAError(cur_client, 100, "MSG PM not returning to self.");
                return;
            }
            if (pmsid.equals("DCBA"))
            {
                //talking to bot security
                // cur_client.sendToClient ("DMSG DCBA "+cur_client.SessionID+" Hello ! PMDCBA");
                // ok now lets check that the chat is a command....
                if (cur_client.reg.isreg && message.charAt(0) == '!' ||
                    cur_client.reg.isreg && message.charAt(0) == '+') //ok.. command mode.
                {
                    cur_client.sendFromBot("[command:] " + ADC.retNormStr(message));
                    new CommandParser(cur_client, message);
                }
            }
            else if (!pmsid.equals("ABCD"))
            {
                for (ClientNod temp : SimpleHandler.getUsers())
                {
                    if (temp.cur_client.SessionID.equals(pmsid))
                    {
                        temp.cur_client.sendToClient(Issued_Command);
                        return;
                    }
                }
                //talking to inexisting client

                new STAError(cur_client,
                             100,
                             "MSG User not found."); //not kick, maybe the other client just left after he sent the msg;
                return;


            }
            else
            {
                //talking to bot
                //must send to all ops...

                for (ClientNod temp : SimpleHandler.getUsers())
                {
                    if (temp.cur_client.reg.isreg && !temp.cur_client.equals(cur_client))
                    {
                        temp.cur_client
                                .sendToClient("DMSG " +
                                              cur_client.SessionID +
                                              " " +
                                              temp.cur_client.SessionID +
                                              " " +
                                              message +
                                              " PMABCD");
                    }

                }


            }
        }

        else
        {
            new STAError(cur_client, 100, "MSG Invalid Context");
            return;
        }
    }

}
