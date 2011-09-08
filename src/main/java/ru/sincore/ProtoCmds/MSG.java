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

package ru.sincore.ProtoCmds;

import ru.sincore.Exceptions.STAException;
import ru.sincore.conf.ADCConfig;
import ru.sincore.conf.Vars;
import ru.sincore.util.ADC;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;
import ru.sincore.*;

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
    public MSG(Client client, String State, String Issued_Command)
            throws STAException
    {
        if (State.equals("IDENTIFY") || State.equals("VERIFY") || State.equals("PROTOCOL"))
        {
            new STAError(client,
                         200 + Constants.STA_INVALID_STATE,
                         "MSG Invalid State.",
                         "FC",
                         Issued_Command.substring(0, 4));
            return;
        }
        if (!client.getClientHandler().reg.overridespam)
        {
            switch (Issued_Command.charAt(0))
            {
                case 'B':
                    if (Vars.BMSG != 1)
                    {
                        new STAError(client, 100, "MSG Invalid Context B");
                        return;
                    }
                    break;
                case 'E':
                    if (Vars.EMSG != 1)
                    {
                        new STAError(client, 100, "MSG Invalid Context E");
                        return;
                    }
                    break;
                case 'D':
                    if (Vars.DMSG != 1)
                    {
                        new STAError(client, 100, "MSG Invalid Context D");
                        return;
                    }
                    break;
                case 'F':
                    if (Vars.FMSG != 1)
                    {
                        new STAError(client, 100, "MSG Invalid Context F");
                        return;
                    }
                    break;
                case 'H':
                    if (Vars.HMSG != 1)
                    {
                        new STAError(client, 100, "MSG Invalid Context H");
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
                client.getClientHandler().sendFromBot("Test OK.");
            }
            return;
        }

        if (!tok.nextToken().equals(client.getClientHandler().SessionID))
        {
            new STAError(client, 200, "Protocol Error. Wrong SID supplied.");
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
                    !(client.getClientHandler().reg.overridespam))
            {
                new STAError(client, 100, "Message exceeds maximum lenght.");
                return;
            }
        }

        if (!client.getClientHandler().reg.overridespam &&
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
                        client.myNod.kickMeByBot("You typed forbidden word.", 3);
                    }
                    what /= 2;
                    if (what % 2 == 1)
                    {
                        //System.out.println("flag contine 4");

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
                        for (Client temp : SessionManager.getUsers())
                        {
                            if (temp.handler.reg.isreg)
                            {
                                temp.handler
                                        .sendToClient("EMSG ABCD " +
                                                      temp.handler.SessionID +
                                                      " User\\s{" +
                                                      client.NI +
                                                      "}\\swith\\sIP\\s{" +
                                                      client.RealIP +
                                                      "}\\sand" +
                                                      "\\sCID\\s{" +
                                                      client.ID +
                                                      "}\\sused\\sforbidden\\sword\\s:\\s" +
                                                      message +
                                                      " PMABCD");
                            }

                        }
                        // System.out.println("notying");
                    }
                    if (kick)
                    {
                        new STAError(client, 200, "You typed forbidden word.");
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
                    new STAError(client,
                                 100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                                 "MSG Invalid Flag.");
                    return;
                }
            }


        }
        long now = System.currentTimeMillis();
        if (client.LastChatMsg != 0)
        {
            if (now - client.LastChatMsg < Vars.chat_interval)
            {
                if (!client.reg.overridespam)

                {
                    new STAError(client,
                                 100,
                                 "Chatting Too Fast. Minimum chat interval " +
                                 String.valueOf(Vars.chat_interval) +
                                 " .You made " +
                                 String.valueOf(now - client.LastChatMsg) +
                                 ".");
                    return;
                }

            }

            else
            {
                client.LastChatMsg = now;
            }
        }
        else
        {
            client.LastChatMsg = now;
        }


        if (Issued_Command.charAt(0) == 'B') //broadcast
        {
            if (pmsid != null)
            {
                new STAError(client, 100, "MSG Can't Broadcast PM.");
                return;
            }
            // ok now lets check that the chat is a command....
            if (client.reg.isreg && message.charAt(0) == '!' ||
                client.reg.isreg &&
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
                    client.sendFromBot("[adc:] " + ADC.retNormStr(message));
                    new ADCConfig(client, message);

                }
                else if (message.toLowerCase().startsWith("!cfg") ||
                         message.toLowerCase().startsWith("+cfg"))//config settings
                {
                    client.sendFromBot("[config:] " + ADC.retNormStr(message));
                    new CommandParser(client, message);

                }
                else
                {
                    client.sendFromBot("[command:] " + ADC.retNormStr(message));
                    new CommandParser(client, message);

                }
            }
            else if (message.equalsIgnoreCase("+myinf") || message.equalsIgnoreCase("!myinf"))
            {
                String toSend;

                toSend = "[Your information: ]\n Nick: " +
                         client.NI +
                         "\n SID: {" +
                         client.SessionID +
                         "}\n CID: {" +
                         client.ID +
                         "}\n PID: {" +
                         client.PD +
                         "}\n IP address: " +
                         client.RealIP
                ;
                if (!client.reg.isreg)
                {
                    toSend += "\nRegular user.";
                }
                else
                {
                    toSend += client.reg.getRegInfo();
                }
                client.sendFromBot(toSend);
            }
            else


            {
                Broadcast.getInstance().broadcast(Issued_Command);
            }
            //System.out.println("acum am trimis broadcast de la "+handler.ID);
            //System.out.println (Issued_Command);
        }
        else if (Issued_Command.charAt(0) == 'E') //echo direct msg
        {
            if (pmsid == null)
            {
                new STAError(client, 100, "MSG Can't PM to Nobody.");
                return;
            }
            if (!thissid.equals(client.SessionID))
            {
                new STAError(client, 100, "MSG PM not returning to self.");
                return;
            }
            if (pmsid.equals("DCBA"))
            {
                //talking to bot security
                client.sendToClient(Issued_Command);
                // ok now lets check that the chat is a command....
                if (client.reg.isreg && message.charAt(0) == '!' ||
                    client.reg.isreg && message.charAt(0) == '+') //ok.. command mode.
                {
                    if (message.toLowerCase().startsWith("!adc") ||
                        message.toLowerCase().startsWith("+adc"))//adc adv config panel
                    {
                        client.sendFromBot("[adc:] " + ADC.retNormStr(message));
                        new ADCConfig(client, message);

                    }
                    else if (message.toLowerCase().startsWith("!cfg") ||
                             message.toLowerCase().startsWith("+cfg"))//config settings
                    {
                        client.sendFromBot("[config:] " + ADC.retNormStr(message));
                        new CommandParser(client, message);

                    }
                    else
                    {
                        client.sendFromBot("[command:] " + ADC.retNormStr(message));
                        new CommandParser(client, message);

                    }
                }
                //handler.sendToClient ("EMSG DCBA "+handler.SessionID+" Hello ! PMDCBA");
                return;
            }
            else if (!pmsid.equals("ABCD"))
            {
                for (Client temp : SessionManager.getUsers())
                {
                    if (temp.handler.SessionID.equals(pmsid))
                    {
                        temp.handler.sendToClient(Issued_Command);
                        client.sendToClient(Issued_Command);
                        return;
                    }
                }
                //talking to inexisting client

                new STAError(client,
                             100,
                             "MSG User not found."); //not kick, maybe the other client just left after he sent the msg;
                return;


            }
            else
            {
                //talking to bot
                //must send to all ops...

                //cant broadcast coz must send each;s SID
                for (Client temp : SessionManager.getUsers())
                {
                    if (temp.handler.userok == 1)
                    {
                        if (temp.handler.reg.isreg && !temp.handler.equals(client))
                        {
                            temp.handler
                                    .sendToClient("EMSG " +
                                                  client.SessionID +
                                                  " " +
                                                  temp.handler.SessionID +
                                                  " " +
                                                  message +
                                                  " PMABCD");
                        }
                    }

                }


            }
            client.sendToClient(Issued_Command);


        }
        else if (Issued_Command.charAt(0) == 'D') //direct direct msg
        {
            if (pmsid == null)
            {
                new STAError(client, 100, "MSG Can't PM to Nobody.");
                return;
            }
            if (!thissid.equals(client.SessionID))
            {
                new STAError(client, 100, "MSG PM not returning to self.");
                return;
            }
            if (pmsid.equals("DCBA"))
            {
                //talking to bot security
                // handler.sendToClient ("DMSG DCBA "+handler.SessionID+" Hello ! PMDCBA");
                // ok now lets check that the chat is a command....
                if (client.reg.isreg && message.charAt(0) == '!' ||
                    client.reg.isreg && message.charAt(0) == '+') //ok.. command mode.
                {
                    client.sendFromBot("[command:] " + ADC.retNormStr(message));
                    new CommandParser(client, message);
                }
            }
            else if (!pmsid.equals("ABCD"))
            {
                for (Client temp : SessionManager.getUsers())
                {
                    if (temp.handler.SessionID.equals(pmsid))
                    {
                        temp.handler.sendToClient(Issued_Command);
                        return;
                    }
                }
                //talking to inexisting client

                new STAError(client,
                             100,
                             "MSG User not found."); //not kick, maybe the other client just left after he sent the msg;
                return;


            }
            else
            {
                //talking to bot
                //must send to all ops...

                for (Client client : SessionManager.getUsers())
                {
                    if (client.getClientHandler().reg.isreg && !client.getClientHandler().equals(client))
                    {
                        client.getClientHandler()
                                .sendToClient("DMSG " +
                                              client.SessionID +
                                              " " +
                                              client.getClientHandler().SessionID +
                                              " " +
                                              message +
                                              " PMABCD");
                    }

                }


            }
        }

        else
        {
            new STAError(client, 100, "MSG Invalid Context");
            return;
        }
    }

}
