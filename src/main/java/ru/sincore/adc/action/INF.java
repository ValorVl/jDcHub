package ru.sincore.adc.action;

import ru.sincore.Broadcast;
import ru.sincore.Client;
import ru.sincore.ClientManager;
import ru.sincore.ConfigLoader;
import ru.sincore.Exceptions.STAException;
import ru.sincore.Modules.Modulator;
import ru.sincore.Modules.Module;
import ru.sincore.TigerImpl.Base32;
import ru.sincore.TigerImpl.Tiger;
import ru.sincore.adc.Context;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.i18n.Messages;
import ru.sincore.util.AdcUtils;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.StringTokenizer;

/**
 * @author Valor
 */
public class INF extends Action
{
    public INF(MessageType messageType, int context, Client fromClient, Client toClient)
    {
        super(messageType, context, fromClient, toClient);

        this.availableContexts = Context.F | Context.T;
        this.availableStates = State.IDENTIFY | State.NORMAL;
    }


    public INF(MessageType messageType, int context, Client client)
    {
        this(messageType,
             context,
             (context == Context.F ? client : null),
             (context == Context.T ? null : client));

    }


    @Override
    public String toString()
    {
        return null;
    }


    private boolean validateNick(Client client)
    {
        // Check nick on size
        if (client.getClientHandler().getNI().length() > ConfigLoader.MAX_NICK_SIZE)
        {
            return false;
        }

        return true;
    }


    private void checkClientInformation()
            throws STAException
    {
        if (!fromClient.getClientHandler().isOverrideSpam())
        {
            if (fromClient.getClientHandler().getEM() != null)
            {
                // TODO [lh] validate email
//                if (!ValidateField(fromClient.getClientHandler().getEM()))
//                {
//                    new STAError(fromClient,
//                                 (fromClient.getClientHandler().getState() == State.PROTOCOL ?
//                                 Constants.STA_SEVERITY_FATAL : Constants.STA_SEVERITY_RECOVERABLE),
//                                 "E-mail contains forbidden words.");
//                    return;
//                }
            }

            if (fromClient.getClientHandler().getDE() != null)
            {
                // TODO [lh] validate description
//                if (!ValidateField(fromClient.getClientHandler().getDE()))
//                {
//                    new STAError(fromClient,
//                                 state == State.PROTOCOL ? Constants.STA_SEVERITY_FATAL : Constants.STA_SEVERITY_RECOVERABLE,
//                                 "Description contains forbidden words");
//                    return;
//                }
            }

            if (fromClient.getClientHandler().getSL() != null)
            {
                if (ConfigLoader.MIN_SLOT_COUNT != 0 && fromClient.getClientHandler().getSL() == 0)
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 "Too few slots, open up more.",
                                 "FB",
                                 "SL");
                }
            }
            //TODO : add without tag allow ?
            //checking all:
            if (fromClient.getClientHandler().getNI() != null)
            {
                if (fromClient.getClientHandler().getNI().length() > ConfigLoader.MAX_NICK_SIZE)
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_INVALID,
                                 "Nick too large",
                                 "FB",
                                 "NI");
                    return;
                }

                if (fromClient.getClientHandler().getNI().length() < ConfigLoader.MIN_NICK_SIZE)
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_INVALID,
                                 "Nick too small",
                                 "FB",
                                 "NI");
                    return;
                }
            }

            if (fromClient.getClientHandler().getDE() != null)
            {
                if (fromClient.getClientHandler().getDE().length() >
                    ConfigLoader.MAX_DESCRIPTION_CHAR_COUNT)
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 "Description too large",
                                 "FB",
                                 "DE");
                    return;
                }
            }

            if (fromClient.getClientHandler().getEM() != null)
            {
                if (fromClient.getClientHandler().getEM().length() >
                    ConfigLoader.MAX_EMAIL_CHAR_COUNT)
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 "E-mail too large",
                                 "FB",
                                 "EM");
                    return;
                }
            }

            // TODO [lh] is MAX_SHARE_SIZE needed?
            // TODO [lh] how to set an unlimited share size?
            if (fromClient.getClientHandler().getSS() != null)
            {
                if (fromClient.getClientHandler().getSS() >
                    ConfigLoader.MAX_SHARE_SIZE)
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 "Share too large",
                                 "FB",
                                 "SS");
                    return;
                }

                if (fromClient.getClientHandler().getSS() <
                    ConfigLoader.MIN_SHARE_SIZE)
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 "Share too small " +
                                 ConfigLoader.MIN_SHARE_SIZE +
                                 " MiB required.",
                                 "FB",
                                 "SS");
                    return;
                }
            }

            if (fromClient.getClientHandler().getSL() != null)
            {
                if (fromClient.getClientHandler().getSL() <
                    ConfigLoader.MIN_SLOT_COUNT)
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 "Too few slots, open up more.",
                                 "FB",
                                 "SL");
                    return;
                }

                if (fromClient.getClientHandler().getSL() >
                    ConfigLoader.MAX_SLOT_COUNT)
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 "Too many slots, close some.",
                                 "FB",
                                 "SL");
                    return;
                }
            }

            if (fromClient.getClientHandler().getHN() != null)
            {
                if (fromClient.getClientHandler().getHN() >
                    ConfigLoader.MAX_HUBS_USERS)
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 "Too many hubs open, close some.",
                                 "FB",
                                 "HN");
                    return;
                }
            }

            if (fromClient.getClientHandler().getHO() != null)
            {
                if (fromClient.getClientHandler().getHO() >
                    ConfigLoader.MAX_OP_IN_HUB)
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 "You are operator on too many hubs. Sorry.",
                                 "FB",
                                 "HO");
                    return;
                }
            }

            if (fromClient.getClientHandler().getHR() != null)
            {
                if (fromClient.getClientHandler().getHR() >
                    ConfigLoader.MAX_HUBS_REGISTERED)
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 "You are registered on too many hubs. Sorry.",
                                 "FB",
                                 "HR");
                    return;
                }
            }
        }

    }


    private void validateMinimalINF()
            throws STAException
    {
        if (fromClient.getClientHandler().getID() == null)
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         "Missing field",
                         "FM",
                         "ID");
            return;
        }
        else if (fromClient.getClientHandler().getID().equals(""))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         "Missing field",
                         "FM",
                         "ID");
            return;
        }
        else if (ClientManager.getInstance().getClientByCID(fromClient.getClientHandler().getID()) != null)
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_CID_TAKEN,
                         "CID taken. Please go to Settings and pick new PID.");
            return;
        }

        if (fromClient.getClientHandler().getPD() == null)
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         "Missing field",
                         "FM",
                         "PD");
            return;
        }
        else if (fromClient.getClientHandler().getPD().equals(""))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         "Missing field",
                         "FM",
                         "PD");
            return;
        }

        if (fromClient.getClientHandler().getNI() == null)
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         "Missing field",
                         "FM",
                         "NI");
            return;
        }
        else if (fromClient.getClientHandler().getNI().equals(""))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         "Missing field",
                         "FM",
                         "NI");
            return;
        }

        if (fromClient.getClientHandler().getHN() != null)
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         "Missing field",
                         "FM",
                         "HN");
            return;
        }
        else if (fromClient.getClientHandler().getHN().equals(""))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         "Missing field",
                         "FM",
                         "HN");
            return;
        }
    }


    private void parseINF(StringTokenizer tokenizer)
            throws STAException
    {
        StringBuilder currentINF = new StringBuilder();
        currentINF.append("BINF ");
        currentINF.append(fromClient.getClientHandler().getSID());

        String thesid = tokenizer.nextToken();
        if (!thesid.equals(fromClient.getClientHandler().getSID()))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Protocol Error.Wrong SID supplied.");
            return;
        }

        tokenizer.nextToken();
        try
        {
            while (tokenizer.hasMoreElements())
            {
                String token = tokenizer.nextToken();

                if (token.startsWith("ID"))//meaning we have the ID thingy
                {

                    if (fromClient.getClientHandler().getState() != State.PROTOCOL)
                    {
                        new STAError(fromClient,
                                     Constants.STA_SEVERITY_RECOVERABLE,
                                     "Can't change CID while connected.");
                        return;
                    }

                    fromClient.getClientHandler().setID(token.substring(2));
                    currentINF.append(" ID");
                    currentINF.append(fromClient.getClientHandler().getID());
                }
                else if (token.startsWith("NI"))
                {
                    // TODO validate nick

/*
                if (! Nick.validateNick(aux.substring(2)))
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_INVALID,
                                 "Nick not valid, please choose another");
                    return;
                }
*/

                    fromClient.getClientHandler().setNI(token.substring(2));

                    if (fromClient.getClientHandler().getState() != State.PROTOCOL)
                    {
                        // TODO change nick to new nick
                        // save information about it into db
                    }

                    currentINF.append(" NI");
                    currentINF.append(fromClient.getClientHandler().getNI());
                }
                else if (token.startsWith("PD"))//the PiD
                {
                    if (fromClient.getClientHandler().getState() != State.PROTOCOL)
                    {
                        new STAError(fromClient,
                                     Constants.STA_SEVERITY_RECOVERABLE,
                                     "Can't change PID while connected.");
                        return;
                    }

                    fromClient.getClientHandler().setPD(token.substring(2));
                }
                else if (token.startsWith("I4"))
                {
                    String ipv4 = token.substring(2);

                    fromClient.getClientHandler().setI4(ipv4);

                    if (ipv4.equals("0.0.0.0") ||
                        ipv4.equals("localhost"))//only if active client
                    {
                        fromClient.getClientHandler()
                                  .setI4(fromClient.getClientHandler().getRealIP());
                    }
                    else if (!ipv4.equals(fromClient.getClientHandler().getRealIP()) &&
                             !ipv4.equals("") &&
                             !fromClient.getClientHandler().getRealIP().equals("127.0.0.1"))
                    {
                        new STAError(fromClient,
                                     Constants.STA_SEVERITY_FATAL + Constants.STA_INVALID_IP,
                                     "Wrong IP address supplied.",
                                     "I4",
                                     fromClient.getClientHandler().getRealIP());
                        return;
                    }

                    currentINF.append(" I4");
                    currentINF.append(fromClient.getClientHandler().getI4());
                }
                else if (token.startsWith("I6"))
                {
                    fromClient.getClientHandler().setI6(token.substring(2));
                    currentINF.append(" I6");
                    currentINF.append(fromClient.getClientHandler().getI6());
                }
                else if (token.startsWith("U4"))
                {
                    fromClient.getClientHandler().setU4(token.substring(2));
                    currentINF.append(" U4");
                    currentINF.append(fromClient.getClientHandler().getU4());
                }
                else if (token.startsWith("U6"))
                {
                    fromClient.getClientHandler().setU6(token.substring(2));
                    currentINF.append(" U6");
                    currentINF.append(fromClient.getClientHandler().getU6());
                }
                else if (token.startsWith("SS"))
                {
                    fromClient.getClientHandler().setSS(Long.parseLong(token.substring(2)));
                    currentINF.append(" SS");
                    currentINF.append(fromClient.getClientHandler().getSS());
                }
                else if (token.startsWith("SF"))
                {
                    fromClient.getClientHandler().setSF(Long.parseLong(token.substring(2)));
                    currentINF.append(" SF");
                    currentINF.append(fromClient.getClientHandler().getSF());
                }
                else if (token.startsWith("VE"))
                {
                    fromClient.getClientHandler().setVE(token.substring(2));
                    currentINF.append(" VE");
                    currentINF.append(fromClient.getClientHandler().getVE());
                }
                else if (token.startsWith("US"))
                {
                    fromClient.getClientHandler().setUS(Long.parseLong(token.substring(2)));
                    currentINF.append(" US");
                    currentINF.append(fromClient.getClientHandler().getUS());
                }
                else if (token.startsWith("DS"))
                {
                    fromClient.getClientHandler().setDS(Long.parseLong(token.substring(2)));
                    currentINF.append(" DS");
                    currentINF.append(fromClient.getClientHandler().getDS());
                }
                else if (token.startsWith("SL"))
                {
                    fromClient.getClientHandler().setSL(Integer.parseInt(token.substring(2)));
                    currentINF.append(" SL");
                    currentINF.append(fromClient.getClientHandler().getSL());
                }
                else if (token.startsWith("AS"))
                {
                    fromClient.getClientHandler().setAS(Long.parseLong(token.substring(2)));
                    currentINF.append(" AS");
                    currentINF.append(fromClient.getClientHandler().getAS());
                }
                else if (token.startsWith("AM"))
                {
                    fromClient.getClientHandler().setAM(Long.parseLong(token.substring(2)));
                    currentINF.append(" AM");
                    currentINF.append(fromClient.getClientHandler().getAM());
                }
                else if (token.startsWith("EM"))
                {
                    fromClient.getClientHandler().setEM(token.substring(2));
                    currentINF.append(" EM");
                    currentINF.append(fromClient.getClientHandler().getEM());
                }
                else if (token.startsWith("DE"))
                {
                    fromClient.getClientHandler().setDE(token.substring(2));
                    currentINF.append(" DE");
                    currentINF.append(fromClient.getClientHandler().getDE());
                }
                else if (token.startsWith("HN"))
                {
                    fromClient.getClientHandler().setHN(Integer.parseInt(token.substring(2)));

                    if (fromClient.getClientHandler().getState() == State.NORMAL)
                    {
                        currentINF.append(" HN");
                        currentINF.append(fromClient.getClientHandler().getHN());
                    }
                }
                else if (token.startsWith("HR"))
                {
                    fromClient.getClientHandler().setHR(Integer.parseInt(token.substring(2)));
                    currentINF.append(" HR");
                    currentINF.append(fromClient.getClientHandler().getHR());
                }
                else if (token.startsWith("HO"))
                {
                    fromClient.getClientHandler().setHO(Integer.parseInt(token.substring(2)));
                    currentINF.append(" HO");
                    currentINF.append(fromClient.getClientHandler().getHO());
                }
                else if (token.startsWith("TO"))
                {
                    fromClient.getClientHandler().setTO(token.substring(2));
                    currentINF.append(" TO");
                    currentINF.append(fromClient.getClientHandler().getTO());
                }
                else if (token.startsWith("AW"))
                {
                    fromClient.getClientHandler().setAW(Integer.parseInt(token.substring(2)));
                    currentINF.append(" AW");
                    currentINF.append(fromClient.getClientHandler().getAW());
                }
                else if (token.startsWith("CT"))
                {
                    if (fromClient.getClientHandler().isOverrideSpam())
                    {
                        fromClient.getClientHandler().setCT(token.substring(2));
                        currentINF.append(" CT");
                        currentINF.append(fromClient.getClientHandler().getCT());
                    }
                    else
                    {
                        new STAError(fromClient,
                                     Constants.STA_SEVERITY_FATAL +
                                     Constants.STA_GENERIC_LOGIN_ERROR,
                                     "Not allowed to have CT field.");
                        return;
                    }
                }
                else if (token.startsWith("HI"))
                {
                    fromClient.getClientHandler().setHI(Integer.parseInt(token.substring(2)) != 0);
                    currentINF.append(" HI");
                    currentINF.append(fromClient.getClientHandler().isHI());
                }

                else if (token.startsWith("SU"))
                {
                    fromClient.getClientHandler().setSU(token.substring(2));
                    currentINF.append(" SU");
                    currentINF.append(fromClient.getClientHandler().getSU());
                }
                else
                {
                    // TODO [lh] is we really must add unknown fields to result INF string?

                    //new STAError(handler,Constants.STA_SEVERITY_FATAL+Constants.STA_GENERIC_PROTOCOL_ERROR,"Protocol Error.");
                    //  return ;
                    currentINF.append(" ");
                    currentINF.append(token);
                }
            }
        }
        catch (NumberFormatException nfe)
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Your client sent weird info, Protocol Error.");
            return;
        }

        validateMinimalINF();

        // TODO [lh] check if user is banned first
        // TODO [lh] add ban check code here
//        fromClient.getClientHandler().myban = BanList.getban(3, fromClient.getClientHandler().ID);
//        if (fromClient.getClientHandler().myban == null)
//        {
//            fromClient.getClientHandler().myban =
//                    BanList.getban(2, (fromClient.getClientHandler().realIP));
//        }
//        if (fromClient.getClientHandler().myban == null)
//        {
//            fromClient.getClientHandler().myban =
//                    BanList.getban(1, fromClient.getClientHandler().NI);
//        }
//        if (fromClient.getClientHandler().myban != null) //banned
//        {
//            if (fromClient.getClientHandler().myban.time == -1)
//            {
//                String msg = "Hello there. You are permanently banned.\nOp who banned you: " +
//                             fromClient.getClientHandler().myban.banop +
//                             "\nReason: " +
//                             fromClient.getClientHandler().myban.banreason +
//                             "\n" +
//                             Messages.BAN_MESSAGE;
//
//                new STAError(fromClient,
//                             Constants.STA_SEVERITY_FATAL + Constants.STA_PERMANENTLY_BANNED,
//                             msg);
//                return;
//            }
//
//            long TL = System.currentTimeMillis() -
//                      fromClient.getClientHandler().myban.timeofban -
//                      fromClient.getClientHandler().myban.time;
//            TL = -TL;
//            if (TL > 0)
//            {
//                String msg = "Hello there. You are temporary banned.\nOp who banned you: " +
//                             fromClient.getClientHandler().myban.banop +
//                             "\nReason: " +
//                             fromClient.getClientHandler().myban.banreason +
//                             "\nThere are still " +
//                             Long.toString(TL / 1000) +
//                             " seconds remaining.\n" +
//                             Messages.BAN_MESSAGE +
//                             " TL" +
//                             Long.toString(TL / 1000);
//
//                new STAError(fromClient,
//                             Constants.STA_SEVERITY_FATAL + Constants.STA_TEMP_BANNED,
//                             msg);
//                return;
//            }
//        }

        // Check nick availability
        // I think, that algo maybe must be rewritten
        for (Client client : ClientManager.getInstance().getClients())
        {
            if (!client.equals(fromClient))
            {
                if (client.getClientHandler().isValidated())
                {
                    if (client.getClientHandler()
                                .getNI()
                                .toLowerCase()
                                .equals(fromClient.getClientHandler().getNI().toLowerCase()) &&
                        !client.getClientHandler().getID().equals(fromClient.getClientHandler().getID()))
                    {
                        new STAError(fromClient,
                                     Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_TAKEN,
                                     "Nick taken, please choose another");
                        return;
                    }
                }
                /* if(state.equals ("PROTOCOL"))
                if(SessionManager.users.containsKey(handler.ID) || temp.handler.ID.equals(handler.ID))//&& temp.handler.CIDsecure)
                {
                    new STAError(handler,Constants.STA_SEVERITY_FATAL+Constants.STA_CID_TAKEN,"CID taken. Please go to Settings and pick new PID.");
                    return;
                }*/
            }
        }


        // TODO [lh] add nick reservation check
//        if (AccountsConfig.nickReserved(fromClient.getClientHandler().NI,
//                                        fromClient.getClientHandler().ID))
//        {
//            int x = (fromClient.getClientHandler().getState() == State.PROTOCOL) ?
//                    Constants.STA_SEVERITY_FATAL : Constants.STA_SEVERITY_RECOVERABLE;
//            new STAError(fromClient,
//                         x + Constants.STA_NICK_TAKEN,
//                         "Nick reserved. Please choose another.");
//            return;
//        }

        // now must check if hub is full...
        if (fromClient.getClientHandler().getState() == State.PROTOCOL) //otherwise is already connected, no point in checking this
        {
            /** must check the hideme var*/
            if (fromClient.getClientHandler().isHideMe())
            {
                currentINF.append(" HI1");
                fromClient.getClientHandler().setHI(true);
            }

            if (ConfigLoader.MAX_USERS <= ClientManager.getInstance().getClientsCount() &&
                !fromClient.getClientHandler().isOverrideFull())
            {
                new STAError(fromClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_HUB_FULL,
                             "Hello there. Hub is full, there are " +
                             String.valueOf(ClientManager.getInstance().getClientsCount()) +
                             " users online.\n" +
                             Messages.HUB_FULL_MESSAGE);
                return;
            }
        }

        checkClientInformation();

        if (fromClient.getClientHandler().getID().equals(ConfigLoader.OP_CHAT_CID))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_CID_TAKEN,
                         "CID taken. Please go to Settings and pick new PID.");
            return;
        }
        if (fromClient.getClientHandler().getID().equals(ConfigLoader.SECURITY_CID))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_CID_TAKEN,
                         "CID taken. Please go to Settings and pick new PID.");
            return;
        }
        if (fromClient.getClientHandler().getNI().equalsIgnoreCase(ConfigLoader.OP_CHAT_NAME))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_TAKEN,
                         "Nick taken, please choose another");
            return;
        }
        if (fromClient.getClientHandler().getNI().equalsIgnoreCase(ConfigLoader.BOT_CHAT_NAME))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_TAKEN,
                         "Nick taken, please choose another");
            return;
        }

        if (fromClient.getClientHandler().isBas0() && fromClient.getClientHandler().getBase() != 2)
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         "Your client uses a very old ADC version." +
                         "Please update in order to connect to this hub." +
                         "You can get a new version usually by visiting" +
                         "the developer's webpage from Help/About menu.");
        }

        if (fromClient.getClientHandler().getState() == State.PROTOCOL)
            makeProtocolStateChecks();

        Broadcast.getInstance().broadcast(currentINF.toString());
    }


    private void makeProtocolStateChecks()
            throws STAException
    {
        try
        {
            Tiger myTiger = new Tiger();

            myTiger.engineReset();
            myTiger.init();
            byte[] bytepid = Base32.decode(fromClient.getClientHandler().getPD());


            myTiger.engineUpdate(bytepid, 0, bytepid.length);

            byte[] finalTiger = myTiger.engineDigest();
            if (!Base32.encode(finalTiger).equals(fromClient.getClientHandler().getID()))
            {
                new STAError(fromClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_LOGIN_ERROR,
                             "Invalid CID check.");
                return;
            }
            if (fromClient.getClientHandler().getPD().length() != 39)
            {
                throw new IllegalArgumentException();
            }
        }
        catch (IllegalArgumentException iae)
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_INVALID_PID,
                         "Invalid PID supplied.");
            return;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return;
        }


    /*------------ok now must see if the client is registered...---------------*/

        // TODO [lh] load information about client from db

        if (fromClient.getClientHandler().isReg())
        {
            if (fromClient.getClientHandler().getPassword().equals(""))//no pass defined ( yet)
            {
                fromClient.getClientHandler().sendToClient(
                        "ISTA 000 Registered,\\sno\\spassword\\srequired.\\sThough,\\sits\\srecomandable\\sto\\sset\\sone.");
                fromClient.getClientHandler().sendToClient("ISTA 000 Authenticated.");


                fromClient.getClientHandler().setLastNick(fromClient.getClientHandler().getNI());
                fromClient.getClientHandler().setLastIP(fromClient.getClientHandler().getRealIP());

                //user is OK, logged in and cool
                fromClient.getClientHandler().setValidated();
                fromClient.getClientHandler().setState(State.NORMAL);
                fromClient.getClientHandler().setLastLogin(fromClient.getClientHandler().getLoggedAt());

                if (fromClient.getClientHandler().isHideMe())
                    fromClient.getClientHandler().sendFromBot("You are currently hidden.");

                fromClient.getClientHandler().setLoggedAt(System.currentTimeMillis());
            }
            else
            {
                // check client for registration (Moscow city style : do you have the passport?)
                fromClient.getClientHandler()
                          .sendToClient("ISTA 000 Registered,\\stype\\syour\\spassword.");

                /* creates some hash for the GPA random data*/
                fromClient.getClientHandler().setEncryptionSalt(Base32.encode(generateSalt()));
                fromClient.getClientHandler().sendToClient("IGPA " +
                                                           fromClient.getClientHandler()
                                                                     .getEncryptionSalt());

                // set client state VARIFY
                fromClient.getClientHandler().setState(State.VERIFY);
            }
        }
        else
        {
            // TODO [lh] add new client registration code here
            if (ConfigLoader.MARK_REGISTRATION_ONLY)
            {
                new STAError(fromClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_REG_ONLY,
                             "Registered only hub.");
                return;
            }
        }

        ClientManager.getInstance().moveClientToRegularMap(fromClient);

        //ok now sending infs of all others to the handler
        sendUsersInfs();

        fromClient.getClientHandler().sendToClient("BINF " +
                                                   ConfigLoader.BOT_CHAT_SID +
                                                   " ID" +
                                                   ConfigLoader.SECURITY_CID +
                                                   " NI" +
                                                   AdcUtils.retADCStr(ConfigLoader.BOT_CHAT_NAME) +
                                                   " CT5 DE" +
                                                   AdcUtils.retADCStr(ConfigLoader.BOT_CHAT_DESCRIPTION));

        fromClient.getClientHandler().putOpchat(true);
        //sending inf about itself too
        fromClient.getClientHandler().sendToClient(fromClient.getClientHandler().getINF());

        //ok now must send INF to all clients
        Broadcast.getInstance().broadcast(fromClient.getClientHandler().getINF(), fromClient);


        if (fromClient.getClientHandler().isUcmd())
        {
            //ok, he is ucmd ok, so
            fromClient.getClientHandler().sendToClient("ICMD Test CT1 TTTest");
        }
        // TODO [lh] send MOTD to client
        //fromClient.getClientHandler().sendFromBot(bigTextManager.getMOTD(fromClient));

        /** calling plugins...*/
        for (Module myMod : Modulator.myModules)
        {
            myMod.onConnect(fromClient.getClientHandler());
        }
    }


    /**
     * Creates some hash for the GPA random data
     * @return salt
     */
    private byte[] generateSalt()
    {
        Tiger myTiger = new Tiger();

        myTiger.engineReset();
        byte[] T = Long.toString(System.currentTimeMillis()).getBytes(); //taken from cur time
        myTiger.engineUpdate(T, 0, T.length);

        return myTiger.engineDigest();
    }


    private void sendUsersInfs()
    {
        for (Client client : ClientManager.getInstance().getClients())
        {
            if (client.getClientHandler().isValidated() && !client.equals(fromClient))
                fromClient.getClientHandler().sendToClient(client.getClientHandler().getINF());
        }
    }


    @Override
    protected void parseIncoming()
            throws STAException
    {
        StringTokenizer tokenizer = new StringTokenizer(rawCommand, " ");

        // pass first 5 symbols: message type, command name and whitespace
        tokenizer.nextToken();

        // parse header
        switch (messageType)
        {
            case INVALID_MESSAGE_TYPE:
                break;

            case B:
                // parse incoming INF message
                parseINF(tokenizer);
                break;

            case C:
            case I:
            case H:
                break;

            case D:
            case E:
                break;

            case F:
                break;

            case U:
                break;
        }
    }

}
