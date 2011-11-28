package ru.sincore.adc.action_obsolete;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Broadcast;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.TigerImpl.Base32;
import ru.sincore.TigerImpl.Tiger;
import ru.sincore.adc.Context;
import ru.sincore.adc.Features;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.client.AbstractClient;
import ru.sincore.db.dao.BanListDAO;
import ru.sincore.db.dao.BanListDAOImpl;
import ru.sincore.db.pojo.BanListPOJO;
import ru.sincore.i18n.Messages;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.util.StringTokenizer;

/**
 * @author Valor
 * @author Alexey 'lh' Antonov
 * @since 2011-09-27
 */
public class INF extends Action
{
    private static final Logger log = LoggerFactory.getLogger(INF.class);


    ConfigurationManager configurationManager = ConfigurationManager.instance();

    public INF(MessageType messageType, int context, AbstractClient fromClient, AbstractClient toClient)
    {
        super(messageType, context, fromClient, toClient);

        this.availableContexts = Context.F | Context.T;
        this.availableStates = State.IDENTIFY | State.NORMAL;
    }


    public INF(MessageType messageType, int context, AbstractClient client)
    {
        this(messageType,
             context,
             (context == Context.T ? client : null),
             (context == Context.F ? null : client));
    }

    public INF(MessageType messageType, int context, AbstractClient client, String rawCommand)
            throws CommandException, STAException
    {
        this(messageType,
             context,
             client);

        parse(rawCommand);
    }

    @Override
    public String toString()
    {
        return null;
    }


    private boolean validateNick(AbstractClient client)
            throws STAException
    {
        // TODO [lh] Replace by normal nick validation
        if (fromClient.getNick().length() > configurationManager.getInt(ConfigurationManager.MAX_NICK_SIZE))
        {
            new STAError(client,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_INVALID,
                         Messages.NICK_TOO_LARGE,
                         "FB",
                         "NI").send();
            return false;
        }

        if (fromClient.getNick().length() < configurationManager.getInt(ConfigurationManager.MIN_NICK_SIZE))
        {
            new STAError(client,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_INVALID,
                         Messages.NICK_TOO_SMALL,
                         "FB",
                         "NI").send();
            return false;
        }

        return true;
    }


    private void checkClientInformation()
            throws STAException
    {
        if (!fromClient.isOverrideSpam())
        {
            if (fromClient.getEmail() != null)
            {
                // TODO [lh] validate email
//                if (!ValidateField(fromClient.getEM()))
//                {
//                    new STAError(fromClient,
//                                 (fromClient.getState() == State.PROTOCOL ?
//                                 Constants.STA_SEVERITY_FATAL : Constants.STA_SEVERITY_RECOVERABLE),
//                                 "E-mail contains forbidden words.");
//                    return;
//                }
            }

            if (fromClient.getDescription() != null)
            {
                // TODO [lh] validate description
//                if (!ValidateField(fromClient.getDE()))
//                {
//                    new STAError(fromClient,
//                                 state == State.PROTOCOL ? Constants.STA_SEVERITY_FATAL : Constants.STA_SEVERITY_RECOVERABLE,
//                                 "Description contains forbidden words");
//                    return;
//                }
            }

            if (fromClient.getUploadSlotsOpened() != null)
            {
                if (configurationManager.getInt(ConfigurationManager.MIN_SLOT_COUNT) != 0 && fromClient.getUploadSlotsOpened() == 0)
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_FEW_SLOTS,
                                 configurationManager.getInt(ConfigurationManager.MIN_SLOT_COUNT),
                                 "FB",
                                 "SL").send();
                }
            }
            //TODO : add without tag allow ?
            //checking all:
            if (fromClient.getNick() != null)
            {
                validateNick(fromClient);
            }

            if (fromClient.getDescription() != null)
            {
                if (fromClient.getDescription().length() >
                    configurationManager.getInt(ConfigurationManager.MAX_DESCRIPTION_CHAR_COUNT))
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_LARGE_DESCRIPTION,
                                 configurationManager.getInt(ConfigurationManager.MAX_DESCRIPTION_CHAR_COUNT),
                                 "FB",
                                 "DE").send();
                    return;
                }
            }

            if (fromClient.getEmail() != null)
            {
                if (fromClient.getEmail().length() >
                    configurationManager.getInt(ConfigurationManager.MAX_EMAIL_CHAR_COUNT))
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_LARGE_MAIL,
                                 configurationManager.getInt(ConfigurationManager.MAX_EMAIL_CHAR_COUNT),
                                 "FB",
                                 "EM").send();
                    return;
                }
            }

            // TODO [lh] is MAX_SHARE_SIZE needed?
            // TODO [lh] how to set an unlimited share size?
            if (fromClient.getShareSise() != null)
            {
                if (fromClient.getShareSise() >
                    configurationManager.getLong(ConfigurationManager.MAX_SHARE_SIZE))
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_LARGE_SHARE,
                                 configurationManager.getLong(ConfigurationManager.MAX_SHARE_SIZE),
                                 "FB",
                                 "SS").send();
                    return;
                }

                if (fromClient.getShareSise() <
                    configurationManager.getLong(ConfigurationManager.MIN_SHARE_SIZE))
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_SMALL_SHARE,
                                 configurationManager.getLong(ConfigurationManager.MIN_SHARE_SIZE),
                                 "FB",
                                 "SS").send();
                    return;
                }
            }

            if (fromClient.getUploadSlotsOpened() != null)
            {
                if (fromClient.getUploadSlotsOpened() <
                    configurationManager.getInt(ConfigurationManager.MIN_SLOT_COUNT))
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_FEW_SLOTS,
                                 configurationManager.getInt(ConfigurationManager.MIN_SLOT_COUNT),
                                 "FB",
                                 "SL").send();
                    return;
                }

                if (fromClient.getUploadSlotsOpened() >
                    configurationManager.getInt(ConfigurationManager.MAX_SLOT_COUNT))
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_MANY_SLOTS,
                                 configurationManager.getInt(ConfigurationManager.MAX_SLOT_COUNT),
                                 "FB",
                                 "SL").send();
                    return;
                }
            }

            if (fromClient.getNumberOfNormalStateHubs() != null)
            {
                if (fromClient.getNumberOfNormalStateHubs() >
                    configurationManager.getInt(ConfigurationManager.MAX_HUBS_USERS))
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_MANY_HUBS_OPEN,
                                 configurationManager.getInt(ConfigurationManager.MAX_HUBS_USERS),
                                 "FB",
                                 "HN").send();
                    return;
                }
            }

            if (fromClient.getNumberOfHubsWhereOp() != null)
            {
                if (fromClient.getNumberOfHubsWhereOp() >
                    configurationManager.getInt(ConfigurationManager.MAX_OP_IN_HUB))
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.OPERATOR_ON_MANY_HUBS,
                                 configurationManager.getInt(ConfigurationManager.MAX_OP_IN_HUB),
                                 "FB",
                                 "HO").send();
                    return;
                }
            }

            if (fromClient.getNumberOfHubsWhereRegistred() != null)
            {
                if (fromClient.getNumberOfHubsWhereRegistred() >
                    configurationManager.getInt(ConfigurationManager.MAX_HUBS_REGISTERED))
                {
                    new STAError(fromClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.REGISTERED_ON_MANY_HUBS,
                                 configurationManager.getInt(ConfigurationManager.MAX_HUBS_REGISTERED),
                                 "FB",
                                 "HR").send();
                    return;
                }
            }
        }

    }


    private void validateMinimalINF()
            throws STAException
    {
        if (fromClient.getCid() == null)
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "ID").send();
            return;
        }
        else if (fromClient.getCid().equals(""))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "ID").send();
            return;
        }

        if (fromClient.getPid() == null)
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "PD").send();
            return;
        }
        else if (fromClient.getPid().equals(""))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "PD").send();
            return;
        }

        if (fromClient.getNick() == null)
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "NI").send();
            return;
        }
        else if (fromClient.getNick().equals(""))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "NI").send();
            return;
        }

        if (fromClient.getNumberOfNormalStateHubs() == null)
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "HN").send();
        }
    }


    private void parseINF(StringTokenizer tokenizer)
            throws STAException
    {
        StringBuilder currentINF = new StringBuilder();
        currentINF.append("BINF ");
        currentINF.append(fromClient.getSid());

        String thesid = tokenizer.nextToken();
        if (!thesid.equals(fromClient.getSid()))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         Messages.WRONG_SID).send();
            return;
        }

        try
        {
            while (tokenizer.hasMoreElements())
            {
                String token = tokenizer.nextToken();

                if (token.startsWith("ID"))//meaning we have the ID thingy
                {
                    if (fromClient.getState() != State.PROTOCOL)
                    {
                        new STAError(fromClient,
                                     Constants.STA_SEVERITY_RECOVERABLE,
                                     Messages.CANT_CHANGE_CID).send();
                        return;
                    }

                    if (ClientManager.getInstance().getClientByCID(fromClient.getCid()) != null)
                    {
                        new STAError(fromClient,
                                     Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_CID_TAKEN,
                                     "CID already taken by another user. Please generate new CID in your client.").send();
                        return;
                    }

                    fromClient.setCid(token.substring(2));
                    currentINF.append(" ID");
                    currentINF.append(fromClient.getCid());
                }
                else if (token.startsWith("NI"))
                {
                    // TODO validate nick
                    fromClient.setNick(token.substring(2));

                    if (ClientManager.getInstance().getClientByNick(fromClient.getNick()) != null)
                    {
                        new STAError(fromClient,
                                     Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_NICK_TAKEN,
                                     "Nick already taken. Please choose another.");
                        return;
                    }

                    if (fromClient.getState() != State.PROTOCOL)
                    {
                        // TODO change nick to new nick
                        // save information about it into db
                    }

                    currentINF.append(" NI");
                    currentINF.append(fromClient.getNick());
                }
                else if (token.startsWith("PD"))//the PiD
                {
                    if (fromClient.getState() != State.PROTOCOL)
                    {
                        new STAError(fromClient,
                                     Constants.STA_SEVERITY_RECOVERABLE,
                                     Messages.CANT_CHANGE_PID).send();
                        return;
                    }

                    fromClient.setPid(token.substring(2));
                }
                else if (token.startsWith("I4"))
                {
                    String ipv4 = token.substring(2);

                    fromClient.setIpAddressV4(ipv4);

                    if (ipv4.equals("0.0.0.0") ||
                        ipv4.equals("localhost"))//only if active client
                    {
                        fromClient.setIpAddressV4(fromClient.getRealIP());
                    }
                    else if (!ipv4.equals(fromClient.getRealIP()) &&
                             !ipv4.equals("") &&
                             !fromClient.getRealIP().equals("127.0.0.1"))
                    {
                        new STAError(fromClient,
                                     Constants.STA_SEVERITY_FATAL + Constants.STA_INVALID_IP,
                                     Messages.WRONG_IP_ADDRESS,
                                     "I4",
                                     fromClient.getRealIP()).send();
                        return;
                    }

                    currentINF.append(" I4");
                    currentINF.append(fromClient.getIpAddressV4());
                }
                else if (token.startsWith("I6"))
                {
                    fromClient.setIpAddressV6(token.substring(2));
                    currentINF.append(" I6");
                    currentINF.append(fromClient.getIpAddressV6());
                }
                else if (token.startsWith("U4"))
                {
                    fromClient.setUdpPortV4(token.substring(2));
                    currentINF.append(" U4");
                    currentINF.append(fromClient.getUdpPortV4());
                }
                else if (token.startsWith("U6"))
                {
                    fromClient.setUdpPortV6(token.substring(2));
                    currentINF.append(" U6");
                    currentINF.append(fromClient.getUdpPortV6());
                }
                else if (token.startsWith("SS"))
                {
                    fromClient.setShareSise(Long.parseLong(token.substring(2)));
                    currentINF.append(" SS");
                    currentINF.append(fromClient.getShareSise());
                }
                else if (token.startsWith("SF"))
                {
                    fromClient.setSharedFiles(Long.parseLong(token.substring(2)));
                    currentINF.append(" SF");
                    currentINF.append(fromClient.getSharedFiles());
                }
                else if (token.startsWith("VE"))
                {
                    fromClient.setClientIdentificationVersion(token.substring(2));
                    currentINF.append(" VE");
                    currentINF.append(fromClient.getClientIdentificationVersion());
                }
                else if (token.startsWith("US"))
                {
                    fromClient.setMaxUploadSpeed(Long.parseLong(token.substring(2)));
                    currentINF.append(" US");
                    currentINF.append(fromClient.getMaxUploadSpeed());
                }
                else if (token.startsWith("DS"))
                {
                    fromClient.setMaxDownloadSpeed(Long.parseLong(token.substring(2)));
                    currentINF.append(" DS");
                    currentINF.append(fromClient.getMaxDownloadSpeed());
                }
                else if (token.startsWith("SL"))
                {
                    fromClient.setUploadSlotsOpened(Integer.parseInt(token.substring(2)));
                    currentINF.append(" SL");
                    currentINF.append(fromClient.getUploadSlotsOpened());
                }
                else if (token.startsWith("AS"))
                {
                    fromClient.setAutomaticSlotAllocator(Long.parseLong(token.substring(2)));
                    currentINF.append(" AS");
                    currentINF.append(fromClient.getAutomaticSlotAllocator());
                }
                else if (token.startsWith("AM"))
                {
                    fromClient.setMinAutomaticSlots(Long.parseLong(token.substring(2)));
                    currentINF.append(" AM");
                    currentINF.append(fromClient.getMinAutomaticSlots());
                }
                else if (token.startsWith("EM"))
                {
                    fromClient.setEmail(token.substring(2));
                    currentINF.append(" EM");
                    currentINF.append(fromClient.getEmail());
                }
                else if (token.startsWith("DE"))
                {
                    fromClient.setDescription(token.substring(2));
                    currentINF.append(" DE");
                    currentINF.append(fromClient.getDescription());
                }
                else if (token.startsWith("HN"))
                {
                    fromClient.setNumberOfNormalStateHubs(Integer.parseInt(token.substring(2)));

                    if (fromClient.getState() == State.NORMAL)
                    {
                        currentINF.append(" HN");
                        currentINF.append(fromClient.getNumberOfNormalStateHubs());
                    }
                }
                else if (token.startsWith("HR"))
                {
                    fromClient.setNumberOfHubsWhereRegistred(Integer.parseInt(token.substring(2)));
                    currentINF.append(" HR");
                    currentINF.append(fromClient.getNumberOfHubsWhereRegistred());
                }
                else if (token.startsWith("HO"))
                {
                    fromClient.setNumberOfHubsWhereOp(Integer.parseInt(token.substring(2)));
                    currentINF.append(" HO");
                    currentINF.append(fromClient.getNumberOfHubsWhereOp());
                }
                else if (token.startsWith("TO"))
                {
                    fromClient.setToken(token.substring(2));
                    currentINF.append(" TO");
                    currentINF.append(fromClient.getToken());
                }
                else if (token.startsWith("AW"))
                {
                    fromClient.setAwayStatus(Integer.parseInt(token.substring(2)));
                    currentINF.append(" AW");
                    currentINF.append(fromClient.getAwayStatus());
                }
                else if (token.startsWith("CT"))
                {
                    if (fromClient.isOverrideSpam())
                    {
                        fromClient.setClientType(Integer.parseInt(token.substring(2)));
                        currentINF.append(" CT");
                        currentINF.append(fromClient.getClientType());
                    }
                    else
                    {
                        new STAError(fromClient,
                                     Constants.STA_SEVERITY_FATAL +
                                     Constants.STA_GENERIC_LOGIN_ERROR,
                                     Messages.CT_FIELD_DISALLOWED).send();
                        return;
                    }
                }
                else if (token.startsWith("HI"))
                {
                    fromClient.setHidden(Integer.parseInt(token.substring(2)) != 0);
                    currentINF.append(" HI");
                    currentINF.append(fromClient.isHidden());
                }
                // TODO [lh] Replace by SUP class usage
                else if (token.startsWith("SU"))
                {
                    String tokens = token.substring(2);
                    StringTokenizer featuresTokenizer = new StringTokenizer(tokens, ",");
                    while (featuresTokenizer.hasMoreTokens())
                    {
                        fromClient.addFeature(featuresTokenizer.nextToken());
                    }

                    currentINF.append(" SU");
                    currentINF.append(tokens);
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
                         Messages.WEIRD_INFO).send();
            return;
        }

        validateMinimalINF();

        // check if user is banned first
        BanListDAO banList = new BanListDAOImpl();
        BanListPOJO banInfo = banList.getLastBan(fromClient.getNick(), fromClient.getRealIP());

        if (banInfo != null)
        {
            long timeLeft = banInfo.getDateStop().getTime() - System.currentTimeMillis();

            if (timeLeft > 0)
            {
                String timeLeftString = DurationFormatUtils.formatDuration(
                        timeLeft,
                        Messages.get(Messages.TIME_PERIOD_FORMAT,
                                     (String) fromClient.getExtendedField("LC")),
                        true);

                new STAError(fromClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_TEMP_BANNED,
                             Messages.BAN_MESSAGE,
                             new Object[] {banInfo.getOpNick(),
                                           banInfo.getReason(),
                                           timeLeftString},
                             "TL",
                             Long.toString(timeLeft / 1000)).send();
            }
        }


        // Check nick availability
        // I think, that algo maybe must be rewritten
        for (AbstractClient client : ClientManager.getInstance().getClients())
        {
            if (!client.equals(fromClient))
            {
                if (client.isValidated())
                {
                    if (client.getNick().toLowerCase().equals(fromClient.getNick().toLowerCase()) &&
                        !client.getCid().equals(fromClient.getCid()))
                    {
                        new STAError(fromClient,
                                     Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_TAKEN,
                                     Messages.NICK_TAKEN).send();
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
//        if (AccountsConfig.nickReserved(fromClient.NI,
//                                        fromClient.ID))
//        {
//            int x = (fromClient.getState() == State.PROTOCOL) ?
//                    Constants.STA_SEVERITY_FATAL : Constants.STA_SEVERITY_RECOVERABLE;
//            new STAError(fromClient,
//                         x + Constants.STA_NICK_TAKEN,
//                         "Nick reserved. Please choose another.");
//            return;
//        }

        // now must check if hub is full...
        if (fromClient.getState() == State.PROTOCOL) //otherwise is already connected, no point in checking this
        {
            if (configurationManager.getInt(ConfigurationManager.MAX_USERS) <= ClientManager.getInstance().getClientsCount() &&
                !fromClient.isOverrideFull())
            {
                new STAError(fromClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_HUB_FULL,
                             Messages.HUB_FULL_MESSAGE,
                             String.valueOf(ClientManager.getInstance().getClientsCount())).send();
                return;
            }
        }

        checkClientInformation();

        if (fromClient.getCid().equals(configurationManager.getString(ConfigurationManager.OP_CHAT_CID)))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_CID_TAKEN,
                         Messages.CID_TAKEN).send();
            return;
        }
        if (fromClient.getCid().equals(configurationManager.getString(ConfigurationManager.SECURITY_CID)))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_CID_TAKEN,
                         Messages.CID_TAKEN).send();
            return;
        }
        if (fromClient.getNick().equalsIgnoreCase(configurationManager.getString(ConfigurationManager.OP_CHAT_NAME)))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_TAKEN,
                         Messages.NICK_TAKEN).send();
            return;
        }
        if (fromClient.getNick().equalsIgnoreCase(configurationManager.getString(ConfigurationManager.BOT_CHAT_NAME)))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_TAKEN,
                         Messages.NICK_TAKEN).send();
            return;
        }

        //if (fromClient.isBas0() && fromClient.getBase() != 2)
        if (!fromClient.isFeature(Features.BASE))
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
                         Messages.VERY_OLD_ADC).send();
        }

        if (fromClient.getState() == State.PROTOCOL)
            makeProtocolStateChecks();

        Broadcast.getInstance().broadcast(currentINF.toString(), fromClient);
    }


    private void makeProtocolStateChecks()
            throws STAException
    {
        try
        {
            Tiger myTiger = new Tiger();

            myTiger.engineReset();
            myTiger.init();
            byte[] bytepid = Base32.decode(fromClient.getPid());


            myTiger.engineUpdate(bytepid, 0, bytepid.length);

            byte[] finalTiger = myTiger.engineDigest();
            if (!Base32.encode(finalTiger).equals(fromClient.getCid()))
            {
                new STAError(fromClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_LOGIN_ERROR,
                             Messages.INVALID_CID).send();
                return;
            }
            if (fromClient.getPid().length() != 39)
            {
                throw new IllegalArgumentException();
            }
        }
        catch (IllegalArgumentException iae)
        {
            new STAError(fromClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_INVALID_PID,
                         Messages.INVALID_PID).send();
            return;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return;
        }


    /*------------ok now must see if the client is registered...---------------*/

        if (!fromClient.loadInfo())
        {
            // info about  client not found
            // store info to db about new client
            //fromClient.storeInfo();
        }

        if (fromClient.isRegistred())
        {
            if (fromClient.getPassword().equals(""))//no pass defined ( yet)
            {
                new STAError(fromClient, Constants.STA_SEVERITY_SUCCESS, Messages.EMPTY_PASSWORD).send();
                fromClient.onLoggedIn();
            }
            else
            {
                // check client for registration (Moscow city style : do you have the passport?)
                new STAError(fromClient, Constants.STA_SEVERITY_SUCCESS, Messages.PASSWORD_REQUIRED).send();

                /* creates some hash for the GPA random data*/
                fromClient.setEncryptionSalt(Base32.encode(generateSalt()));
                fromClient.sendRawCommand("IGPA " + fromClient.getEncryptionSalt());

                // set client state VARIFY
                fromClient.setState(State.VERIFY);
                return;
            }
        }
        else
        {
            // TODO [lh] add new client registration code here
            if (configurationManager.getBoolean(ConfigurationManager.MARK_REGISTRATION_ONLY))
            {
                new STAError(fromClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_REG_ONLY,
                             Messages.REGISTERED_ONLY);
                return;
            }

            fromClient.setValidated();
        }

        fromClient.onConnected();
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

    @Override
    protected void parseIncoming()
            throws STAException
    {
        StringTokenizer tokenizer = new StringTokenizer(rawCommand, " ");

        // pass first 5 symbols: message type, actionName name and whitespace
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
