package ru.sincore.adc.action.handlers;

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
import ru.sincore.adc.Features;
import ru.sincore.adc.Flags;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.adc.action.actions.GPA;
import ru.sincore.adc.action.actions.INF;
import ru.sincore.client.AbstractClient;
import ru.sincore.db.dao.BanListDAO;
import ru.sincore.db.dao.BanListDAOImpl;
import ru.sincore.db.pojo.BanListPOJO;
import ru.sincore.i18n.Messages;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

/**
 * INF message handler
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 25.11.11
 *         Time: 9:17
 */
public class INFHandler extends AbstractActionHandler<INF>
{
    private final static Logger log = LoggerFactory.getLogger(INFHandler.class);
    ConfigurationManager configurationManager = ConfigurationManager.instance();

    public INFHandler(AbstractClient sourceClient,
                      AbstractClient targetClient,
                      INF            action)
    {
        super(sourceClient, targetClient, action);
    }


    public INFHandler(AbstractClient sourceClient, INF action)
    {
        super(sourceClient, action);
    }


    @Override
    public void handle()
            throws STAException
    {
        log.debug("Handle INF action...");

        try
        {
            action.tryParse();

            if (!sourceClient.getSid().equals(action.getSourceSID()))
            {
                new STAError(sourceClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             Messages.WRONG_SID).send();
                return;

            }

            if (action.isFlagSet(Flags.CID))
            {
                if (sourceClient.getState() != State.PROTOCOL)
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_RECOVERABLE,
                                 Messages.CANT_CHANGE_CID).send();
                    return;
                }

                if (ClientManager.getInstance().getClientByCID(sourceClient.getCid()) != null)
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_CID_TAKEN,
                                 "CID already taken by another user. Please generate new CID in your client.")
                            .send();
                    return;
                }

                sourceClient.setCid(action.getCid());
            }


            // TODO: _must_ nick be present?
            if (action.isFlagSet(Flags.NICK))
            {
                // TODO: nick validation
                sourceClient.setNick(action.getNick());

                if (ClientManager.getInstance().getClientByNick(sourceClient.getNick()) != null)
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_NICK_TAKEN,
                                 "Nick already taken. Please choose another.").send();
                    return;
                }

                if (sourceClient.getState() != State.PROTOCOL)
                {
                    // TODO change nick to new nick
                    // save information about it into db
                }
            }


            if (action.isFlagSet(Flags.PID))
            {
                if (sourceClient.getState() != State.PROTOCOL)
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_RECOVERABLE,
                                 Messages.CANT_CHANGE_PID).send();
                    return;
                }

                sourceClient.setPid(action.getPid());
            }


            if (action.isFlagSet(Flags.ADDR_IPV4))
            {
                String ipv4 = action.getFlagValue(Flags.ADDR_IPV4);
                sourceClient.setIpAddressV4(ipv4);

                if (ipv4.equals("0.0.0.0") ||
                    ipv4.equals("localhost"))//only if active client
                {
                    sourceClient.setIpAddressV4(sourceClient.getRealIP());
                    action.setFlagValue(Flags.ADDR_IPV4, sourceClient.getRealIP());
                }
                else if (!ipv4.equals(sourceClient.getRealIP()) &&
                         !ipv4.equals("") &&
                         !sourceClient.getRealIP().equals("127.0.0.1"))
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_FATAL + Constants.STA_INVALID_IP,
                                 Messages.WRONG_IP_ADDRESS,
                                 "I4",
                                 sourceClient.getRealIP()).send();
                    return;
                }
            }
            else
            {
                sourceClient.setIpAddressV4(sourceClient.getRealIP());
                action.setFlagValue(Flags.ADDR_IPV4, sourceClient.getRealIP());
            }

            if (action.isFlagSet(Flags.CLIENT_TYPE))
            {
                if (sourceClient.isOverrideSpam())
                {
                    sourceClient.setClientType(action.getFlagValue(Flags.CLIENT_TYPE, 0));
                }
                else
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_GENERIC_LOGIN_ERROR,
                                 Messages.CT_FIELD_DISALLOWED).send();
                    return;
                }
            }

            if (action.isFlagSet(Flags.ADDR_IPV6))
            {
                sourceClient.setIpAddressV6(
                        action.<String>getFlagValue(Flags.ADDR_IPV6));
            }

            if (action.isFlagSet(Flags.UDP_PORT_IPV4))
            {
                sourceClient.setUdpPortV4(
                        action.<String>getFlagValue(Flags.UDP_PORT_IPV4));
            }

            if (action.isFlagSet(Flags.UDP_PORT_IPV6))
            {
                sourceClient.setUdpPortV6(
                        action.<String>getFlagValue(Flags.UDP_PORT_IPV6));
            }

            if (action.isFlagSet(Flags.SHARE_SIZE))
            {
                sourceClient.setShareSise(
                        action.<Long>getFlagValue(Flags.SHARE_SIZE, 0L));
            }

            if (action.isFlagSet(Flags.SHARED_FILES))
            {
                sourceClient.setSharedFiles(
                        action.<Long>getFlagValue(Flags.SHARED_FILES, 0L));
            }

            if (action.isFlagSet(Flags.VERSION))
            {
                sourceClient.setClientIdentificationVersion(
                        action.<String>getFlagValue(Flags.VERSION, ""));
            }

            if (action.isFlagSet(Flags.MAX_UPLOAD_SPEED))
            {
                sourceClient.setMaxUploadSpeed(
                        action.<Long>getFlagValue(Flags.MAX_UPLOAD_SPEED, 0L));
            }

            if (action.isFlagSet(Flags.MAX_DOWNLOAD_SPEED))
            {
                sourceClient.setMaxDownloadSpeed(
                        action.<Long>getFlagValue(Flags.MAX_DOWNLOAD_SPEED, 0L));
            }

            if (action.isFlagSet(Flags.OPENED_UPLOAD_SLOTS))
            {
                sourceClient.setUploadSlotsOpened(
                        action.<Integer>getFlagValue(Flags.OPENED_UPLOAD_SLOTS, 0));
            }

            if (action.isFlagSet(Flags.AUTOMATIC_SLOT_ALLOCATOR))
            {
                sourceClient.setAutomaticSlotAllocator(
                        action.<Long>getFlagValue(Flags.AUTOMATIC_SLOT_ALLOCATOR, 0L));
            }

            if (action.isFlagSet(Flags.MIN_AUTOMATIC_SLOTS))
            {
                sourceClient.setMinAutomaticSlots(
                        action.<Long>getFlagValue(Flags.MIN_AUTOMATIC_SLOTS, 0L));
            }

            if (action.isFlagSet(Flags.EMAIL))
            {
                sourceClient.setEmail(
                        action.<String>getFlagValue(Flags.EMAIL, ""));
            }

            if (action.isFlagSet(Flags.DESCRIPTION))
            {
                sourceClient.setDescription(
                        action.<String>getFlagValue(Flags.DESCRIPTION, ""));
            }

            if (action.isFlagSet(Flags.AMOUNT_HUBS_WHERE_NORMAL_USER))
            {
                sourceClient.setNumberOfNormalStateHubs(
                        action.<Integer>getFlagValue(Flags.AMOUNT_HUBS_WHERE_NORMAL_USER, 0));
            }

            if (action.isFlagSet(Flags.AMOUNT_HUBS_WHERE_REGISTERED_USER))
            {
                sourceClient.setNumberOfHubsWhereRegistred(
                        action.<Integer>getFlagValue(Flags.AMOUNT_HUBS_WHERE_REGISTERED_USER,
                                                        0));
            }

            if (action.isFlagSet(Flags.AMOUNT_HUBS_WHERE_OP_USER))
            {
                sourceClient.setNumberOfHubsWhereOp(
                        action.<Integer>getFlagValue(Flags.AMOUNT_HUBS_WHERE_OP_USER, 0));
            }

            if (action.isFlagSet(Flags.TOKEN))
            {
                sourceClient.setToken(
                        action.<String>getFlagValue(Flags.TOKEN, ""));
            }

            if (action.isFlagSet(Flags.AWAY))
            {
                sourceClient.setAwayStatus(
                        action.<Integer>getFlagValue(Flags.AWAY, 0));
            }

            if (action.isFlagSet(Flags.HIDDEN))
            {
                sourceClient.setHidden(
                        action.<Boolean>getFlagValue(Flags.HIDDEN, false));
            }

            if (action.isFlagSet(Flags.FEATURES))
            {
                for (String feature : action.getFeatures())
                {
                    sourceClient.addFeature(feature);
                }
            }


            // Minimal validation
            validateMinimalINF();

            // Check for ban:
            isBanned();

            // Check nick availability
            if (!isNickAvail())
            {
                return;
            }

            // now must check if hub is full...
            if (sourceClient.getState() ==
                State.PROTOCOL) //otherwise is already connected, no point in checking this
            {
                if (configurationManager.getInt(ConfigurationManager.MAX_USERS) <=
                    ClientManager.getInstance().getClientsCount() &&
                    !sourceClient.isOverrideFull())
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_FATAL + Constants.STA_HUB_FULL,
                                 Messages.HUB_FULL_MESSAGE,
                                 String.valueOf(ClientManager.getInstance()
                                                             .getClientsCount())).send();
                    return;
                }
            }


            if (!isClientInformationValid())
            {
                return;
            }


            if (sourceClient.getCid()
                            .equals(configurationManager.getString(ConfigurationManager.OP_CHAT_CID)))
            {
                new STAError(sourceClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_CID_TAKEN,
                             Messages.CID_TAKEN).send();
                return;
            }
            if (sourceClient.getCid()
                            .equals(configurationManager.getString(ConfigurationManager.SECURITY_CID)))
            {
                new STAError(sourceClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_CID_TAKEN,
                             Messages.CID_TAKEN).send();
                return;
            }
            if (sourceClient.getNick()
                            .equalsIgnoreCase(configurationManager.getString(ConfigurationManager.OP_CHAT_NAME)))
            {
                new STAError(sourceClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_TAKEN,
                             Messages.NICK_TAKEN).send();
                return;
            }
            if (sourceClient.getNick()
                            .equalsIgnoreCase(configurationManager.getString(ConfigurationManager.BOT_CHAT_NAME)))
            {
                new STAError(sourceClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_TAKEN,
                             Messages.NICK_TAKEN).send();
                return;
            }

            if (!sourceClient.isFeature(Features.BASE))
            {
                new STAError(sourceClient,
                             Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             Messages.VERY_OLD_ADC).send();
            }


            if (sourceClient.getState() == State.PROTOCOL && doProtocolStateChecks() == false)
            {
                return;
            }


            Broadcast.getInstance().broadcast(action.getRawCommand(), sourceClient);
        }
        catch (NumberFormatException nfe)
        {
            try
            {
                new STAError(sourceClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             Messages.WEIRD_INFO).send();
            }
            catch (STAException e)
            {
                e.printStackTrace();
            }
            return;
        }
        catch (CommandException e)
        {
            e.printStackTrace();
        }
        catch (STAException e)
        {
            e.printStackTrace();
        }

    }


    private void validateMinimalINF()
            throws STAException
    {
        if (sourceClient.getCid() == null)
        {
            new STAError(sourceClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "ID").send();
            return;
        }
        else if (sourceClient.getCid().equals(""))
        {
            new STAError(sourceClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "ID").send();
            return;
        }

        if (sourceClient.getPid() == null)
        {
            new STAError(sourceClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "PD").send();
            return;
        }
        else if (sourceClient.getPid().equals(""))
        {
            new STAError(sourceClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "PD").send();
            return;
        }

        if (sourceClient.getNick() == null)
        {
            new STAError(sourceClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "NI").send();
            return;
        }
        else if (sourceClient.getNick().equals(""))
        {
            new STAError(sourceClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "NI").send();
            return;
        }

        if (sourceClient.getNumberOfNormalStateHubs() == null)
        {
            new STAError(sourceClient,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "HN").send();
        }
    }


    private boolean isBanned()
            throws STAException
    {
        BanListDAO banList = new BanListDAOImpl();
        BanListPOJO banInfo = banList.getLastBan(sourceClient.getNick(), sourceClient.getRealIP());

        if (banInfo != null)
        {
            long timeLeft = banInfo.getDateStop().getTime() - System.currentTimeMillis();

            if (timeLeft > 0)
            {
                String timeLeftString = DurationFormatUtils.formatDuration(
                        timeLeft,
                        Messages.get(Messages.TIME_PERIOD_FORMAT,
                                     (String) sourceClient.getExtendedField("LC")),
                        true);

                new STAError(sourceClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_TEMP_BANNED,
                             Messages.BAN_MESSAGE,
                             new Object[]{ banInfo.getOpNick(),
                                           banInfo.getReason(),
                                           timeLeftString },
                             "TL",
                             Long.toString(timeLeft / 1000)).send();

                return true;
            }
        }

        return false;
    }


    private boolean isNickAvail()
            throws STAException
    {
        for (AbstractClient client : ClientManager.getInstance().getClients())
        {
            if (!client.equals(sourceClient))
            {
                if (client.isValidated())
                {
                    if (client.getNick()
                              .toLowerCase()
                              .equals(sourceClient.getNick().toLowerCase()) &&
                        !client.getCid().equals(sourceClient.getCid()))
                    {
                        new STAError(sourceClient,
                                     Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_TAKEN,
                                     Messages.NICK_TAKEN).send();
                        return false;
                    }
                }
                /* if(state.equals ("PROTOCOL"))
                if(SessionManager.users.containsKey(handler.ID) || temp.handler.ID.equals(handler.ID))//&& temp.handler.CIDsecure)
                {
                    new STAError(handler,Constants.STA_SEVERITY_FATAL+Constants.STA_CID_TAKEN,"CID taken. Please go to Settings and pick new PID.");
                    return false;
                }*/
            }
        }

        return true;
    }


    private boolean isClientInformationValid()
            throws STAException
    {
        if (!sourceClient.isOverrideSpam())
        {
            if (sourceClient.getEmail() != null)
            {
                // TODO [lh] validate email
//                if (!ValidateField(sourceClient.getEM()))
//                {
//                    new STAError(sourceClient,
//                                 (sourceClient.getState() == State.PROTOCOL ?
//                                 Constants.STA_SEVERITY_FATAL : Constants.STA_SEVERITY_RECOVERABLE),
//                                 "E-mail contains forbidden words.");
//                    return false;
//                }
            }

            if (sourceClient.getDescription() != null)
            {
                // TODO [lh] validate description
//                if (!ValidateField(sourceClient.getDE()))
//                {
//                    new STAError(sourceClient,
//                                 state == State.PROTOCOL ? Constants.STA_SEVERITY_FATAL : Constants.STA_SEVERITY_RECOVERABLE,
//                                 "Description contains forbidden words");
//                    return false;
//                }
            }

            if (sourceClient.getUploadSlotsOpened() != null)
            {
                if (configurationManager.getInt(ConfigurationManager.MIN_SLOT_COUNT) != 0 &&
                    sourceClient.getUploadSlotsOpened() == 0)
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_FEW_SLOTS,
                                 configurationManager.getInt(ConfigurationManager.MIN_SLOT_COUNT),
                                 "FB",
                                 "SL").send();
                    return false;
                }
            }
            //TODO : add without tag allow ?
            //checking all:
            if (sourceClient.getNick() != null)
            {
                if (isNickValidated(sourceClient) == false)
                {
                    return false;
                }
            }

            if (sourceClient.getDescription() != null)
            {
                if (sourceClient.getDescription().length() >
                    configurationManager.getInt(ConfigurationManager.MAX_DESCRIPTION_CHAR_COUNT))
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_LARGE_DESCRIPTION,
                                 configurationManager.getInt(ConfigurationManager.MAX_DESCRIPTION_CHAR_COUNT),
                                 "FB",
                                 "DE").send();
                    return false;
                }
            }

            if (sourceClient.getEmail() != null)
            {
                if (sourceClient.getEmail().length() >
                    configurationManager.getInt(ConfigurationManager.MAX_EMAIL_CHAR_COUNT))
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_LARGE_MAIL,
                                 configurationManager.getInt(ConfigurationManager.MAX_EMAIL_CHAR_COUNT),
                                 "FB",
                                 "EM").send();
                    return false;
                }
            }

            // TODO [lh] is MAX_SHARE_SIZE needed?
            // TODO [lh] how to set an unlimited share size?
            if (sourceClient.getShareSise() != null)
            {
                if (sourceClient.getShareSise() >
                    configurationManager.getLong(ConfigurationManager.MAX_SHARE_SIZE))
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_LARGE_SHARE,
                                 configurationManager.getLong(ConfigurationManager.MAX_SHARE_SIZE),
                                 "FB",
                                 "SS").send();
                    return false;
                }

                if (sourceClient.getShareSise() <
                    configurationManager.getLong(ConfigurationManager.MIN_SHARE_SIZE))
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_SMALL_SHARE,
                                 configurationManager.getLong(ConfigurationManager.MIN_SHARE_SIZE),
                                 "FB",
                                 "SS").send();
                    return false;
                }
            }

            if (sourceClient.getUploadSlotsOpened() != null)
            {
                if (sourceClient.getUploadSlotsOpened() <
                    configurationManager.getInt(ConfigurationManager.MIN_SLOT_COUNT))
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_FEW_SLOTS,
                                 configurationManager.getInt(ConfigurationManager.MIN_SLOT_COUNT),
                                 "FB",
                                 "SL").send();
                    return false;
                }

                if (sourceClient.getUploadSlotsOpened() >
                    configurationManager.getInt(ConfigurationManager.MAX_SLOT_COUNT))
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_MANY_SLOTS,
                                 configurationManager.getInt(ConfigurationManager.MAX_SLOT_COUNT),
                                 "FB",
                                 "SL").send();
                    return false;
                }
            }

            if (sourceClient.getNumberOfNormalStateHubs() != null)
            {
                if (sourceClient.getNumberOfNormalStateHubs() >
                    configurationManager.getInt(ConfigurationManager.MAX_HUBS_USERS))
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_MANY_HUBS_OPEN,
                                 configurationManager.getInt(ConfigurationManager.MAX_HUBS_USERS),
                                 "FB",
                                 "HN").send();
                    return false;
                }
            }

            if (sourceClient.getNumberOfHubsWhereOp() != null)
            {
                if (sourceClient.getNumberOfHubsWhereOp() >
                    configurationManager.getInt(ConfigurationManager.MAX_OP_IN_HUB))
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.OPERATOR_ON_MANY_HUBS,
                                 configurationManager.getInt(ConfigurationManager.MAX_OP_IN_HUB),
                                 "FB",
                                 "HO").send();
                    return false;
                }
            }

            if (sourceClient.getNumberOfHubsWhereRegistred() != null)
            {
                if (sourceClient.getNumberOfHubsWhereRegistred() >
                    configurationManager.getInt(ConfigurationManager.MAX_HUBS_REGISTERED))
                {
                    new STAError(sourceClient,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.REGISTERED_ON_MANY_HUBS,
                                 configurationManager.getInt(ConfigurationManager.MAX_HUBS_REGISTERED),
                                 "FB",
                                 "HR").send();
                    return false;
                }
            }
        }

        return true;
    }


    private boolean isNickValidated(AbstractClient client)
            throws STAException
    {
        // TODO [lh] Replace by normal nick validation
        if (sourceClient.getNick().length() >
            configurationManager.getInt(ConfigurationManager.MAX_NICK_SIZE))
        {
            new STAError(client,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_INVALID,
                         Messages.NICK_TOO_LARGE,
                         "FB",
                         "NI").send();
            return false;
        }

        if (sourceClient.getNick().length() <
            configurationManager.getInt(ConfigurationManager.MIN_NICK_SIZE))
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


    private boolean doProtocolStateChecks()
            throws STAException, CommandException, STAException
    {
        try
        {
            Tiger myTiger = new Tiger();

            myTiger.engineReset();
            myTiger.init();
            byte[] bytepid = Base32.decode(sourceClient.getPid());


            myTiger.engineUpdate(bytepid, 0, bytepid.length);

            byte[] finalTiger = myTiger.engineDigest();
            if (!Base32.encode(finalTiger).equals(sourceClient.getCid()))
            {
                new STAError(sourceClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_LOGIN_ERROR,
                             Messages.INVALID_CID).send();
                return false;
            }
            if (sourceClient.getPid().length() != 39)
            {
                throw new IllegalArgumentException();
            }
        }
        catch (IllegalArgumentException iae)
        {
            new STAError(sourceClient,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_INVALID_PID,
                         Messages.INVALID_PID).send();
            return false;
        }
        catch (Exception e)
        {
            System.out.println(e);
            return false;
        }


        /*------------ok now must see if the client is registered...---------------*/

        if (!sourceClient.loadInfo())
        {
            // info about  client not found
            // store info to db about new client
            //sourceClient.storeInfo();
        }

        if (sourceClient.isRegistred())
        {
            if (sourceClient.getPassword().equals(""))//no pass defined ( yet)
            {
                new STAError(sourceClient,
                             Constants.STA_SEVERITY_SUCCESS,
                             Messages.EMPTY_PASSWORD).send();
                sourceClient.onLoggedIn();
            }
            else
            {
                // check client for registration (Moscow city style : do you have the passport?)
                new STAError(sourceClient,
                             Constants.STA_SEVERITY_SUCCESS,
                             Messages.PASSWORD_REQUIRED).send();

                /* creates some hash for the GPA random data*/
                sourceClient.setEncryptionSalt(Base32.encode(generateSalt()));

                GPA igpa = new GPA();
                igpa.setMessageType(MessageType.I);
                igpa.setPassword(sourceClient.getEncryptionSalt());

                sourceClient.sendRawCommand(igpa.getRawCommand());

                // set client state VARIFY
                sourceClient.setState(State.VERIFY);
                return true;
            }
        }
        else
        {
            // TODO [lh] add new client registration code here
            if (configurationManager.getBoolean(ConfigurationManager.MARK_REGISTRATION_ONLY))
            {
                new STAError(sourceClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_REG_ONLY,
                             Messages.REGISTERED_ONLY);
                return false;
            }

            sourceClient.setValidated();
            sourceClient.setState(State.NORMAL);
        }

        sourceClient.onConnected();

        return true;
    }


    /**
     * Creates some hash for the GPA random data
     *
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

}
