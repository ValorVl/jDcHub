package ru.sincore;
/*
 * Command.java
 *
 * Created on 06 martie 2007, 16:20
 *
 * DSHub AdcUtils HubSoft
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


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.Modules.Modulator;
import ru.sincore.Modules.Module;
import ru.sincore.ProtoCmds.*;
import ru.sincore.TigerImpl.Base32;
import ru.sincore.TigerImpl.Tiger;
import ru.sincore.adc.State;
import ru.sincore.banning.BanList;
import ru.sincore.i18n.Messages;
import ru.sincore.util.AdcUtils;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.StringTokenizer;

/**
 * Provides a parsing for each AdcUtils command received from client, and makes the states transitions
 * Updates all information and ensures stability.
 *
 * @author Eugen Hristev
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-06
 */

public class Command
{
	private static final Logger log = LoggerFactory.getLogger(Command.class);

    Client currentClient;
    String command;
    int    state;
	private BigTextManager bigTextManager = new BigTextManager();


    private void sendUsersInfs()
    {
        for (Client client : ClientManager.getInstance().getClients())
        {
            if (client.getClientHandler().validated == 1 && !client.equals(currentClient))
            {
                currentClient.getClientHandler()
                             .sendToClient(client.getClientHandler().getINF());
            }
        }
    }


    private boolean pushUser()
    {
        if (ClientManager.getInstance().containClientByCID(currentClient.getClientHandler().ID))
        {
            Client client = ClientManager.getInstance().getClientByCID(currentClient.getClientHandler().ID);
            client.dropMeImGhost();
        }


        ClientManager.getInstance().addClient(currentClient);
        currentClient.getClientHandler().inside = true;
        return true;
    }


    void completeLogIn()
            throws STAException
    {
        // must check if its op or not and move accordingly
        if (!currentClient.getClientHandler().reg.key) //DO NOT increase HR count and put RG field to 1
        {
            //  handler.HR=String.valueOf(Integer.parseInt(handler.HR)+1);
            currentClient.getClientHandler().CT = "2";
        }
        else //DO NOT increase HO count and put OP field to 1
        {
            //   handler.HO=String.valueOf(Integer.parseInt(handler.HO)+1);
            currentClient.getClientHandler().CT = "4";
        }


        boolean ok = pushUser();

        if (!ok)
        {
            new STAError(currentClient,
                         200 + Constants.STA_CID_TAKEN,
                         "CID taken. Please go to Settings and pick new PID.");
            return;
        }


        //ok now sending infs of all others to the handler
        sendUsersInfs();

        currentClient.getClientHandler().sendToClient("BINF DCBA ID" +
															  ConfigurationManager.instance().getString(ConfigurationManager.SECURITY_CID) +
															  " NI" +
															  AdcUtils.retADCStr(ConfigurationManager.instance().getString(ConfigurationManager.BOT_CHAT_NAME))
															  +
															  " CT5 DE" +
															  AdcUtils.retADCStr(ConfigurationManager.instance().getString(ConfigurationManager.BOT_CHAT_DESCRIPTION)));
		log.info(ConfigurationManager.instance().getString(ConfigurationManager.SECURITY_CID));
        currentClient.getClientHandler().putOpchat(true);
        currentClient.getClientHandler().sendToClient(currentClient.getClientHandler().getINF());  //sending inf about itself too



        //ok now must send INF to all clientsByCID
        Broadcast.getInstance().broadcast(currentClient.getClientHandler().getINF(), currentClient);
        currentClient.getClientHandler().validated = 1; //user is OK, logged in and cool.
        currentClient.getClientHandler().reg.LastLogin = System.currentTimeMillis();
        currentClient.getClientHandler().sendFromBot(bigTextManager.getMOTD(currentClient));
        currentClient.getClientHandler().sendFromBot(currentClient.getClientHandler().reg.HideMe ? "You are currently hidden." : "");

        currentClient.getClientHandler().loggedAt = System.currentTimeMillis();
        currentClient.getClientHandler().state = State.NORMAL;


        /** calling plugins...*/
        for (Module myMod : Modulator.myModules)
        {
            myMod.onConnect(currentClient.getClientHandler());
        }
        //handler.sendFromBot( AdcUtils.MOTD);
        currentClient.getClientHandler().canReceiveCmds = true;


    }


    boolean ValidateField(String str)
    {
        return Main.listaBanate.isOK(str) == -1;
    }


    void handleINF() throws CommandException, STAException
    {

    }


    /**
     * Main command handling function, AdcUtils specific.
     */
    void HandleIssuedCommand()
            throws CommandException, STAException
    {


        if (command.length() < 4)
        {
            new STAError(currentClient,
                         100 + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Incorrect command");
        }
        /*******************************INF COMMAND *****************************************/

        if (command.substring(1).startsWith("INF"))
        {

            //if (state.equals("IDENTIFY") || state.equals("VERIFY"))
            if ((state & (State.IDENTIFY | State.VERIFY)) != State.INVALID_STATE)
            {
                new STAError(currentClient,
                             200 + Constants.STA_INVALID_STATE,
                             "INF Invalid state.",
                             "FC",
                             command.substring(0, 4));
                return;
            }


            if (command.charAt(0) != 'B')
            {
                new STAError(currentClient, 100, "INF Invalid Context.");
                return;
            }
            if (!currentClient.getClientHandler().reg.overridespam)
            {
                switch (command.charAt(0))
                {
                    case 'B':
                        if (ConfigLoader.ADC_BINF != 1)
                        {
                            new STAError(currentClient, 100, "INF Invalid Context B");
                            return;
                        }
                        break;
                    case 'E':
                        if (ConfigLoader.ADC_EINF != 1)
                        {
                            new STAError(currentClient, 100, "INF Invalid Context E");
                            return;
                        }
                        break;
                    case 'D':
                        if (ConfigLoader.ADC_DINF != 1)
                        {
                            new STAError(currentClient, 100, "INF Invalid Context D");
                            return;
                        }
                        break;
                    case 'F':
                        if (ConfigLoader.ADC_FINF != 1)
                        {
                            new STAError(currentClient, 100, "INF Invalid Context F");
                            return;
                        }
                        break;
                    case 'H':
                        if (ConfigLoader.ADC_HINF != 1)
                        {
                            new STAError(currentClient, 100, "INF Invalid Context H");
                            return;
                        }

                }
            }


            handleINF();

        }

        /************************PAS COMMAND****************************/
        if (command.substring(1).startsWith("PAS"))
        {

            if (!currentClient.getClientHandler().reg.overridespam)
            {
                switch (command.charAt(0))
                {
                    case 'B':
                        if (ConfigLoader.ADC_BPAS != 1)
                        {
                            new STAError(currentClient, 100, "PAS Invalid Context B");
                            return;
                        }
                        break;
                    case 'E':
                        if (ConfigLoader.ADC_EPAS != 1)
                        {
                            new STAError(currentClient, 100, "PAS Invalid Context E");
                            return;
                        }
                        break;
                    case 'D':
                        if (ConfigLoader.ADC_DPAS != 1)
                        {
                            new STAError(currentClient, 100, "PAS Invalid Context D");
                            return;
                        }
                        break;
                    case 'F':
                        if (ConfigLoader.ADC_FPAS != 1)
                        {
                            new STAError(currentClient, 100, "PAS Invalid Context F");
                            return;
                        }
                        break;
                    case 'H':
                        if (ConfigLoader.ADC_HPAS != 1)
                        {
                            new STAError(currentClient, 100, "PAS Invalid Context H");
                            return;
                        }

                }
            }
            Nod k;

            if ((k = AccountsConfig.isNickRegFl(currentClient.getClientHandler().NI)) != null)
            {
                currentClient.getClientHandler().reg = k;
            }
            if (!currentClient.getClientHandler().reg.isreg)
            {
                new STAError(currentClient, 100, "Not registered.");
                return;
            }
            if (command.charAt(0) != 'H')
            {
                if (state == State.NORMAL)
                {
                    throw new CommandException("FAIL state:PROTOCOL reason:NOT BASE CLIENT");
                }
                else
                {
                    new STAError(currentClient, 100, "PAS Invalid Context.");
                    return;
                }
            }

            String realpas = "";
            try
            {
                Tiger myTiger = new Tiger();

                myTiger.engineReset();
                myTiger.init();
                // removed old adc support;
                //  byte [] bytecid=Base32.decode (handler.ID);
                byte[] pas = currentClient.getClientHandler().reg.Password.getBytes();
                byte[] random = Base32.decode(currentClient.getClientHandler().encryptionSolt);

                byte[] result = new byte[pas.length + random.length];
                //for(int i=0;i<bytecid.length;i++)
                //   result[i]=bytecid[i];

                System.arraycopy(pas,    0, result,          0, pas.length   );
                System.arraycopy(random, 0, result, pas.length, random.length);

                myTiger.engineUpdate(result, 0, result.length);

                byte[] finalTiger = myTiger.engineDigest();
                realpas = Base32.encode(finalTiger);

            }


            catch (IllegalArgumentException iae)
            {
                System.out.println(iae);
            }
            catch (Exception e)
            {
                System.out.println(e);
            }
            if (realpas.equals(command.substring(5)))
            {
                currentClient.getClientHandler().sendToClient("IMSG Authenticated.");

                currentClient.getClientHandler().sendFromBot(bigTextManager.getMOTD(currentClient));

                //System.out.println ("pwla");
                currentClient.getClientHandler().reg.LastNI = currentClient.getClientHandler().NI;
                // handler.reg.LastNI=handler.NI;
                currentClient.getClientHandler().reg.LastIP = currentClient.getClientHandler().realIP;

                if (!currentClient.getClientHandler().ID.equals(currentClient.getClientHandler().reg.CID))
                {
                    currentClient.getClientHandler().sendToClient("IMSG Account\\sCID\\supdated\\sto\\s" + currentClient.getClientHandler().ID);
                }
                currentClient.getClientHandler().reg.CID = currentClient.getClientHandler().ID;
            }
            else
            {
                new STAError(currentClient, 200 + Constants.STA_INVALID_PASSWORD, "Invalid Password.");
                return;
            }

            //System.out.println (command);
            completeLogIn();
        }

        /**********************SUP COMMAND******************************/
        if (command.substring(1).startsWith("SUP"))
        {
            new SUP(currentClient, State.toString(state), command);
        }

        /********************************MSG COMMAND************************************/
        if (command.substring(1).startsWith("MSG"))
        {
            new MSG(currentClient, State.toString(state), command);
        }

        if (command.substring(1).startsWith("SCH"))
        {
            new SCH(currentClient, State.toString(state), command);
        }

        if (command.substring(1).startsWith("STA"))
        {
            new STA(currentClient, State.toString(state), command);
        }

        if (command.substring(1).startsWith("RES ")) //direct search result, only active to passive must send this
        {
            new RES(currentClient, State.toString(state), command);
        }
        else if (command.substring(1).startsWith("CTM ")) //direct connect to me
        {
            new CTM(currentClient, State.toString(state), command);
        }
        else if (command.substring(1).startsWith("RCM ")) //reverse connect to me
        {
            new RCM(currentClient, State.toString(state), command);
        }


        /** calling plugins...*/

        for (Module myMod : Modulator.myModules)
        {
            myMod.onRawCommand(currentClient.getClientHandler(), command);
        }
    }


    /**
     * Creates a new instance of Command with following params
     * CH of type ClientHandler identifies tha client to handle
     * @param client
     * @param command      Issued_command of String type actually identifies the given command
     *                      state also of type String Identifies tha state in which tha connection is,
     *                      meaning [ accordingly to arne's draft]:
     *                      PROTOCOL (feature support discovery), IDENTIFY (user identification, static checks),
     *                      VERIFY (password check), NORMAL (normal operation) and DATA (for binary transfers).
     *                      Calling function should send one of this params, that is calling function
     *                      request... Command class does not check params.
     * @throws CommandException Something wrong happend
     * @throws STAException
     */
    public Command(Client client, String command)
            throws STAException, CommandException
    {
        log.debug("Incoming command parser thread = " + Thread.currentThread().getName());
        currentClient = client;


        if (command.equals(""))
        {
            log.debug("Empty command from client with nick = \'" + client.getClientHandler().NI +
                      "\' and SID = \'" + client.getClientHandler().SID + "\'");
            return;
        }

        this.command = command;
        state = currentClient.getClientHandler().state;
        HandleIssuedCommand();
    }

}
