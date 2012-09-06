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
    private ConfigurationManager configurationManager = ConfigurationManager.getInstance();


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

            if (!client.getSid().equals(action.getSourceSID()))
            {
                log.error("SID " +
                         client.getSid() +
                         " not equal to " +
                         action.getSourceSID());

                new STAError(client,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             Messages.WRONG_SID).send();
                return;

            }

            if (action.isFlagSet(Flags.PID))
            {
                if (client.getState() != State.IDENTIFY)
                {
                    new STAError(client,
                                 Constants.STA_SEVERITY_RECOVERABLE,
                                 Messages.CANT_CHANGE_PID).send();
                    return;
                }

                client.setPid(action.getPid());
            }

            if (action.isFlagSet(Flags.CID))
            {
                log.info("Client with SID [" +
                         client.getSid() +
                         "] and IP [" +
                         client.getRealIP() +
                         "] CID: " + action.getCid());

                if (client.getState() == State.NORMAL)
                {
                        new STAError(client,
                                     Constants.STA_SEVERITY_RECOVERABLE,
                                     Messages.CANT_CHANGE_CID).send();
                }

                //check for already connected clients with same CID
                AbstractClient previouseClient = ClientManager.getInstance().getClientByCID(action.getCid());
                if (previouseClient == null)
                {
                    previouseClient = ClientManager.getInstance().getUninitializedClientByCID(action.getCid());
                }

                if (previouseClient != null)
                {
                    log.info("CID " +
                             action.getCid() +
                             " already taken by client " +
                             previouseClient.getNick() +
                             " with SID " +
                             previouseClient.getSid());

                    // disconnect previously connected client
                    // (for example it may be zombie client which waiting 2 hours for tcp_connection_timeout)
                    if (configurationManager.getBoolean(ConfigurationManager.DISCONNECT_ONLINE_WITH_SAME_CID))
                    {
                        previouseClient.sendPrivateMessageFromHub(Messages.get(Messages.DISCONNECT_BY_CID_TAKEN,
                                                                               previouseClient.getExtendedField(
                                                                                       "LC")));
                        previouseClient.disconnect();
                    }

                    new STAError(client,
                                 Constants.STA_SEVERITY_FATAL + Constants.STA_CID_TAKEN,
                                 Messages.CID_TAKEN).send();
                }

                client.setCid(action.getCid());
            }
            else if (client.getState() == State.IDENTIFY)
            {
                // Error, cause in IDENTIFY state CID field must be represented
                log.info("CID does not set by client with SID [" +
                         client.getSid() +
                         "]");
                new STAError(client, Constants.STA_SEVERITY_FATAL + Constants.STA_ACCESS_DENIED,
                             Messages.INVALID_CID).send();
            }


            // NI field in IDENTIFY state required!
            if ((client.getState() == State.IDENTIFY) && !action.isFlagSet(Flags.NICK))
            {
                new STAError(client,
                             Constants.STA_SEVERITY_FATAL +
                             Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                             Messages.NICK_MISSING,
                             "FM",
                             "NI").send();
            }


            if (action.isFlagSet(Flags.NICK))
            {
                // TODO: nick validation

                if (ClientManager.getInstance().getClientByNick(action.getNick()) != null)
                {
                    new STAError(client,
                                 Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_TAKEN,
                                 Messages.NICK_TAKEN).send();
                }

                client.setNick(action.getNick());
                log.info("Client with SID " +
                        "[" +
                        client.getSid() +
                        "]" +
                        " get nick " +
                        client.getNick());

                if (!validateNick())
                {
                    return;
                }


                if (client.getState() != State.IDENTIFY)
                {
                    // TODO change nick to new nick
                    // save information about it into db
                }
            }

            // load info about newly connected client
            if (client.getState() == State.IDENTIFY)
            {
                client.loadInfo();
            }


            if (action.isFlagSet(Flags.ADDR_IPV4))
            {
                String ipv4 = action.getFlagValue(Flags.ADDR_IPV4);
                client.setIpAddressV4(ipv4);

                if (ipv4.equals("0.0.0.0") ||
                    ipv4.equals("localhost"))//only if active client
                {
                    client.setIpAddressV4(client.getRealIP());
                    action.setFlagValue(Flags.ADDR_IPV4, client.getRealIP());
                }
                else if (!ipv4.equals(client.getRealIP()) &&
                         !ipv4.equals("") &&
                         !client.getRealIP().equals("127.0.0.1"))
                {
                    new STAError(client,
                                 Constants.STA_SEVERITY_FATAL + Constants.STA_INVALID_IP,
                                 Messages.WRONG_IP_ADDRESS,
                                 "I4",
                                 client.getRealIP()).send();
                }
            }
            else
            {
                client.setIpAddressV4(client.getRealIP());
                action.setFlagValue(Flags.ADDR_IPV4, client.getRealIP());
            }

            if (action.isFlagSet(Flags.CLIENT_TYPE))
            {
                if (client.isOverrideSpam())
                {
                    client.setClientType(action.getFlagValue(Flags.CLIENT_TYPE, 0));
                }
                else
                {
                    new STAError(client,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_GENERIC_LOGIN_ERROR,
                                 Messages.CT_FIELD_DISALLOWED).send();
                }
            }

            if (action.isFlagSet(Flags.ADDR_IPV6))
            {
                client.setIpAddressV6(
                        action.<String>getFlagValue(Flags.ADDR_IPV6));
            }

            if (action.isFlagSet(Flags.UDP_PORT_IPV4))
            {
                client.setUdpPortV4(
                        action.<String>getFlagValue(Flags.UDP_PORT_IPV4));
            }

            if (action.isFlagSet(Flags.UDP_PORT_IPV6))
            {
                client.setUdpPortV6(
                        action.<String>getFlagValue(Flags.UDP_PORT_IPV6));
            }

            if (action.isFlagSet(Flags.SHARE_SIZE))
            {
                client.setShareSize(
                        action.<Long>getFlagValue(Flags.SHARE_SIZE, 0L));
            }

            if (action.isFlagSet(Flags.SHARED_FILES))
            {
                client.setSharedFiles(
                        action.<Long>getFlagValue(Flags.SHARED_FILES, 0L));
            }

            if (action.isFlagSet(Flags.VERSION))
            {
                client.setClientIdentificationVersion(
                        action.<String>getFlagValue(Flags.VERSION, ""));
            }

            if (action.isFlagSet(Flags.MAX_UPLOAD_SPEED))
            {
                client.setMaxUploadSpeed(
                        action.<Long>getFlagValue(Flags.MAX_UPLOAD_SPEED, 0L));
            }

            if (action.isFlagSet(Flags.MAX_DOWNLOAD_SPEED))
            {
                client.setMaxDownloadSpeed(
                        action.<Long>getFlagValue(Flags.MAX_DOWNLOAD_SPEED, 0L));
            }

            if (action.isFlagSet(Flags.OPENED_UPLOAD_SLOTS))
            {
                client.setUploadSlotsOpened(
                        action.<Integer>getFlagValue(Flags.OPENED_UPLOAD_SLOTS, 0));
            }

            if (action.isFlagSet(Flags.AUTOMATIC_SLOT_ALLOCATOR))
            {
                client.setAutomaticSlotAllocator(
                        action.<Long>getFlagValue(Flags.AUTOMATIC_SLOT_ALLOCATOR, 0L));
            }

            if (action.isFlagSet(Flags.MIN_AUTOMATIC_SLOTS))
            {
                client.setMinAutomaticSlots(
                        action.<Long>getFlagValue(Flags.MIN_AUTOMATIC_SLOTS, 0L));
            }

            if (action.isFlagSet(Flags.EMAIL))
            {
                client.setEmail(
                        action.<String>getFlagValue(Flags.EMAIL, ""));
            }

            if (action.isFlagSet(Flags.DESCRIPTION))
            {
                client.setDescription(
                        action.<String>getFlagValue(Flags.DESCRIPTION, ""));
            }

            if (action.isFlagSet(Flags.AMOUNT_HUBS_WHERE_NORMAL_USER))
            {
                client.setNumberOfNormalStateHubs(
                        action.<Integer>getFlagValue(Flags.AMOUNT_HUBS_WHERE_NORMAL_USER, 0));
            }

            if (action.isFlagSet(Flags.AMOUNT_HUBS_WHERE_REGISTERED_USER))
            {
                client.setNumberOfHubsWhereRegistred(
                        action.<Integer>getFlagValue(Flags.AMOUNT_HUBS_WHERE_REGISTERED_USER,
                                                     0));
            }

            if (action.isFlagSet(Flags.AMOUNT_HUBS_WHERE_OP_USER))
            {
                client.setNumberOfHubsWhereOp(
                        action.<Integer>getFlagValue(Flags.AMOUNT_HUBS_WHERE_OP_USER, 0));
            }

            if (action.isFlagSet(Flags.TOKEN))
            {
                client.setToken(
                        action.<String>getFlagValue(Flags.TOKEN, ""));
            }

            if (action.isFlagSet(Flags.AWAY))
            {
                client.setAwayStatus(
                        action.<Integer>getFlagValue(Flags.AWAY, 0));
            }

            if (action.isFlagSet(Flags.HIDDEN))
            {
                client.setHidden(
                        action.<Boolean>getFlagValue(Flags.HIDDEN, false));
            }


            // set features
            if (action.isFlagSet(Flags.FEATURES))
            {
                for (String feature : action.getFeatures())
                {
                    client.addFeature(feature);
                }
            }

            if (client.isFeature(Features.RXTX))
            {
                /**
                 * RXTX extension.
                 */
                if (action.isFlagSet(Flags.RX_BYTES))
                {
                    client.setRxBytes(
                            client.getRxBytes() + action.<Long>getFlagValue(Flags.RX_BYTES, 0L));
                }

                if (action.isFlagSet(Flags.TX_BYTES))
                {
                    client.setTxBytes(
                            client.getTxBytes() + action.<Long>getFlagValue(Flags.TX_BYTES, 0L));
                }

                log.debug("RXTX: rx = " +
                          action.<Long>getFlagValue(Flags.RX_BYTES, 0L) +
                         ", tx = " +
                         action.<Long>getFlagValue(Flags.TX_BYTES, 0L));
            }

            // Minimal validation
            validateMinimalINF();


            // Check for ban:
            isBanned();

            // now must check if hub is full...
            //otherwise is already connected, no point in checking this
            if (client.getState() == State.IDENTIFY)
            {
                if (configurationManager.getInt(ConfigurationManager.MAX_USERS) <=
                    ClientManager.getInstance().getClientsCount() &&
                    !client.isOverrideFull())
                {
                    new STAError(client,
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


            if (!client.isFeature(Features.BASE))
            {
                new STAError(client,
                             Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             Messages.VERY_OLD_ADC).send();
            }


            if (client.getState() == State.IDENTIFY && doProtocolStateChecks() == false)
            {
                return;
            }

            // TODO [lh] check if this broadcast is needed
            //Broadcast.getInstance().broadcast(action.getRawCommand(), client);
        }
        catch (NumberFormatException nfe)
        {
            try
            {
                new STAError(client,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             Messages.WEIRD_INFO).send();
            }
            catch (STAException e)
            {
                log.error(e.toString());
            }
        }
        catch (CommandException e)
        {
            log.error(e.toString());
        }
        catch (STAException e)
        {
            client.disconnect();
            log.error(e.toString());
        }

    }


    /**
     * Nick validation method
     * @return valid nick (true) or not (false)
     * @throws STAException
     */
    private boolean validateNick()
            throws STAException
    {
        if (client.getNick() == null)
        {
            new STAError(client,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.NICK_MISSING,
                         "FM",
                         "NI").send();
            return false;
        }
        else if (client.getNick().equals(""))
        {
            new STAError(client,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.NICK_MISSING,
                         "FM",
                         "NI").send();
            return false;
        }

        if (client.getNick().length() >
            configurationManager.getInt(ConfigurationManager.MAX_NICK_SIZE))
        {
            new STAError(client,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_NICK_INVALID,
                         Messages.NICK_TOO_LARGE,
                         "FB",
                         "NI").send();
            return false;
        }

        if (client.getNick().length() <
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


    private void validateMinimalINF()
            throws STAException
    {
        if (client.getCid() == null)
        {
            new STAError(client,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "ID").send();
            return;
        }
        else if (client.getCid().equals(""))
        {
            new STAError(client,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "ID").send();
            return;
        }

        if (client.getPid() == null)
        {
            new STAError(client,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "PD").send();
            return;
        }
        else if (client.getPid().equals(""))
        {
            new STAError(client,
                         Constants.STA_SEVERITY_FATAL +
                         Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                         Messages.MISSING_FIELD,
                         "FM",
                         "PD").send();
            return;
        }

        if (client.getNumberOfNormalStateHubs() == null)
        {
            new STAError(client,
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
        BanListPOJO banInfo = banList.getLastBan(client.getNick(), client.getRealIP());

        if (banInfo != null)
        {
            if (banInfo.getBanType() == Constants.BAN_PERMANENT)
            {
                new STAError(client,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_PERMANENTLY_BANNED,
                             Messages.PERM_BAN_MESSAGE,
                             new Object[]{banInfo.getOpNick(),
                                          banInfo.getReason()}).send();
                return true;
            }

            long timeLeft = banInfo.getDateStop().getTime() - System.currentTimeMillis();

            if (timeLeft > 0)
            {
                String timeLeftString = DurationFormatUtils.formatDuration(
                        timeLeft,
                        Messages.get(Messages.TIME_PERIOD_FORMAT,
                                     (String) client.getExtendedField("LC")),
                        true);

                new STAError(client,
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


    private boolean isClientInformationValid()
            throws STAException
    {
        if (!client.isOverrideSpam())
        {
            if (client.getEmail() != null)
            {
                // TODO [lh] validate email
//                if (!ValidateField(client.getEM()))
//                {
//                    new STAError(client,
//                                 (client.getState() == State.PROTOCOL ?
//                                 Constants.STA_SEVERITY_FATAL : Constants.STA_SEVERITY_RECOVERABLE),
//                                 "E-mail contains forbidden words.");
//                    return false;
//                }
            }

            if (client.getDescription() != null)
            {
                // TODO [lh] validate description
//                if (!ValidateField(client.getDE()))
//                {
//                    new STAError(client,
//                                 state == State.PROTOCOL ? Constants.STA_SEVERITY_FATAL : Constants.STA_SEVERITY_RECOVERABLE,
//                                 "Description contains forbidden words");
//                    return false;
//                }
            }

            if (client.getUploadSlotsOpened() != null)
            {
                if (configurationManager.getInt(ConfigurationManager.MIN_SLOT_COUNT) != 0 &&
                    client.getUploadSlotsOpened() == 0)
                {
                    new STAError(client,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_FEW_SLOTS,
                                 configurationManager.getInt(ConfigurationManager.MIN_SLOT_COUNT),
                                 "FB",
                                 "SL").send();
                    return false;
                }
            }

            if (client.getDescription() != null)
            {
                if (client.getDescription().length() >
                    configurationManager.getInt(ConfigurationManager.MAX_DESCRIPTION_CHAR_COUNT))
                {
                    new STAError(client,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_LARGE_DESCRIPTION,
                                 configurationManager.getInt(ConfigurationManager.MAX_DESCRIPTION_CHAR_COUNT),
                                 "FB",
                                 "DE").send();
                    return false;
                }
            }

            if (client.getEmail() != null)
            {
                if (client.getEmail().length() >
                    configurationManager.getInt(ConfigurationManager.MAX_EMAIL_CHAR_COUNT))
                {
                    new STAError(client,
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
            if (client.getShareSize() != null)
            {
                if ((configurationManager.getLong(ConfigurationManager.MAX_SHARE_SIZE) != 0) &&
                    (client.getShareSize() >
                     configurationManager.getLong(ConfigurationManager.MAX_SHARE_SIZE)))
                {
                    new STAError(client,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_LARGE_SHARE,
                                 configurationManager.getLong(ConfigurationManager.MAX_SHARE_SIZE),
                                 "FB",
                                 "SS").send();
                    return false;
                }

                if (client.getShareSize() <
                    configurationManager.getLong(ConfigurationManager.MIN_SHARE_SIZE))
                {
                    new STAError(client,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_SMALL_SHARE,
                                 configurationManager.getLong(ConfigurationManager.MIN_SHARE_SIZE),
                                 "FB",
                                 "SS").send();
                    return false;
                }
            }

            if (client.getUploadSlotsOpened() != null)
            {
                if (client.getUploadSlotsOpened() <
                    configurationManager.getInt(ConfigurationManager.MIN_SLOT_COUNT))
                {
                    new STAError(client,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_FEW_SLOTS,
                                 configurationManager.getInt(ConfigurationManager.MIN_SLOT_COUNT),
                                 "FB",
                                 "SL").send();
                    return false;
                }

                if (client.getUploadSlotsOpened() >
                    configurationManager.getInt(ConfigurationManager.MAX_SLOT_COUNT))
                {
                    new STAError(client,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_MANY_SLOTS,
                                 configurationManager.getInt(ConfigurationManager.MAX_SLOT_COUNT),
                                 "FB",
                                 "SL").send();
                    return false;
                }
            }

            if (client.getNumberOfNormalStateHubs() != null)
            {
                if (client.getNumberOfNormalStateHubs() >
                    configurationManager.getInt(ConfigurationManager.MAX_HUBS_USERS))
                {
                    new STAError(client,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.TOO_MANY_HUBS_OPEN,
                                 configurationManager.getInt(ConfigurationManager.MAX_HUBS_USERS),
                                 "FB",
                                 "HN").send();
                    return false;
                }
            }

            if (client.getNumberOfHubsWhereOp() != null)
            {
                if (client.getNumberOfHubsWhereOp() >
                    configurationManager.getInt(ConfigurationManager.MAX_OP_IN_HUB))
                {
                    new STAError(client,
                                 Constants.STA_SEVERITY_FATAL +
                                 Constants.STA_REQUIRED_INF_FIELD_BAD_MISSING,
                                 Messages.OPERATOR_ON_MANY_HUBS,
                                 configurationManager.getInt(ConfigurationManager.MAX_OP_IN_HUB),
                                 "FB",
                                 "HO").send();
                    return false;
                }
            }

            if (client.getNumberOfHubsWhereRegistred() != null)
            {
                if (client.getNumberOfHubsWhereRegistred() >
                    configurationManager.getInt(ConfigurationManager.MAX_HUBS_REGISTERED))
                {
                    new STAError(client,
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


    private boolean doProtocolStateChecks()
            throws CommandException, STAException
    {
        try
        {
            Tiger myTiger = new Tiger();

            myTiger.engineReset();
            myTiger.init();
            byte[] bytepid = Base32.decode(client.getPid());


            myTiger.engineUpdate(bytepid, 0, bytepid.length);

            byte[] finalTiger = myTiger.engineDigest();
            if (!Base32.encode(finalTiger).equals(client.getCid()))
            {
                new STAError(client,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_LOGIN_ERROR,
                             Messages.INVALID_CID).send();
                return false;
            }
            if (client.getPid().length() != 39)
            {
                throw new IllegalArgumentException();
            }
        }
        catch (IllegalArgumentException iae)
        {
            new STAError(client,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_INVALID_PID,
                         Messages.INVALID_PID).send();
            return false;
        }
        catch (Exception e)
        {
            log.error(e.toString());
            return false;
        }


        /*------------ok now must see if the client is registered...---------------*/

        if (client.isRegistred())
        {
            if (client.getPassword().equals(""))//no pass defined ( yet)
            {
                new STAError(client,
                             Constants.STA_SEVERITY_SUCCESS,
                             Messages.EMPTY_PASSWORD).send();
                client.onLoggedIn();
            }
            else
            {
                // check client for registration (Moscow city style : do you have the passport?)
                new STAError(client,
                             Constants.STA_SEVERITY_SUCCESS,
                             Messages.PASSWORD_REQUIRED).send();

                /* creates some hash for the GPA random data*/
                client.setEncryptionSalt(Base32.encode(generateSalt()));

                GPA igpa = new GPA();
                igpa.setMessageType(MessageType.I);
                igpa.setPassword(client.getEncryptionSalt());

                client.sendRawCommand(igpa.getRawCommand());

                // set client state VARIFY
                client.setState(State.VERIFY);
                return true;
            }
        }
        else
        {
            // TODO [lh] add new client registration code here
            if (configurationManager.getBoolean(ConfigurationManager.MARK_REGISTRATION_ONLY))
            {
                new STAError(client,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_REG_ONLY,
                             Messages.REGISTERED_ONLY);
                return false;
            }

            client.setValidated();
            client.setState(State.NORMAL);
        }

        client.onConnected();

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
