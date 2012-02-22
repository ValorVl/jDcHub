package ru.sincore.adc.action.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.*;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Features;
import ru.sincore.adc.Flags;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.adc.action.actions.INF;
import ru.sincore.adc.action.actions.SID;
import ru.sincore.adc.action.actions.SUP;
import ru.sincore.client.AbstractClient;
import ru.sincore.i18n.Messages;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

/**
 * SUP action handler
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 22.11.11
 *         Time: 16:01
 */
public class SUPHandler extends AbstractActionHandler<SUP>
{
    private final static Logger log = LoggerFactory.getLogger(SUPHandler.class);
    private ConfigurationManager configurationManager = ConfigurationManager.getInstance();


    public SUPHandler(AbstractClient sourceClient, SUP action)
    {
        super(sourceClient, action);
    }


    @Override
    public void handle()
            throws STAException
    {
        log.debug("Handle SUP action...");
        try
        {
            action.tryParse();

            for (String feature : action.getFeatures().keySet())
            {
                client.setFeature(feature, action.getFeatures().get(feature));
            }

            // TODO: check client SID and sid given in Client class...

            if (!client.isFeature(Features.BAS0) && !client.isFeature(Features.BASE))
            {
                new STAError(client,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             Messages.BASE_FEATURE_NOT_SUPPORTED).send();
            }

            // Check support TIGER hash..
            if (!client.isFeature(Features.TIGER))
            {
                new STAError(client,
                             Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_NO_HASH_OVERLAP,
                             Messages.HASH_FUNCTION_NOT_SELECTED).send();
            }

            // if client in PROTOCOL state, send info about hub to him
            if (client.getState() == State.PROTOCOL)
                sendClientInitializationInfo();
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


    /**
     * Handshake stage 1.
     * Sends to client initialization info about hub and his sid.
     *
     * @throws STAException
     * @throws CommandException
     */
    private void sendClientInitializationInfo()
            throws CommandException, STAException
    {
        client.sendRawCommand(Main.getServer().getSup().getRawCommand());

        SID sid = new SID();
        sid.setMessageType(MessageType.I);
        sid.setSourceSID(client.getSid());
        client.sendRawCommand(sid.getRawCommand());


        INF inf = new INF();
        inf.setMessageType(MessageType.I);
        inf.setFlagValue(Flags.CLIENT_TYPE, 32);
        inf.setFlagValue(Flags.VERSION,
                         configurationManager.getAdcString(ConfigurationManager.HUB_VERSION));
        inf.setFlagValue(Flags.NICK,
                         configurationManager.getAdcString(ConfigurationManager.HUB_NAME));

        BigTextManager bigTextManager = new BigTextManager();
        // hub description == hub topic
        String hubDescription = bigTextManager.getText(BigTextManager.TOPIC);

        if (hubDescription != null && !hubDescription.isEmpty() && !hubDescription.equals(""))
        {
            inf.setFlagValue(Flags.DESCRIPTION,
                             hubDescription);
        }
        else if (!configurationManager.getAdcString(ConfigurationManager.HUB_DESCRIPTION).isEmpty())
        {
            inf.setFlagValue(Flags.DESCRIPTION,
                             configurationManager.getAdcString(ConfigurationManager.HUB_DESCRIPTION));
        }


        // Check client flag isPingExtensionSupports, if true, send PING string
        if (client.isFeature(Features.PING))
        {
            inf = pingQuery(inf);
        }

        client.sendRawCommand(inf.getRawCommand());
    }


    /**
     * Method build PING request string
     * @param inf
     * @return ping request string
     * @throws CommandException
     * @throws STAException
     */
    private static INF pingQuery(INF inf)
            throws CommandException, STAException
    {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();

        inf.setFlagValue(Flags.HUB_HOST, configurationManager.getString(ConfigurationManager.HUB_LISTEN));
        inf.setFlagValue(Flags.HUB_USERS_ONLINE, ClientManager.getInstance().getClientsCount());
        inf.setFlagValue(Flags.HUB_TOTAL_SHARE_SIZE, ClientManager.getInstance().getTotalShare());
        inf.setFlagValue(Flags.HUB_TOTAL_SHARED_FILES, ClientManager.getInstance().getTotalFileCount());
        inf.setFlagValue(Flags.HUB_MIN_ALLOWED_SHARE_SIZE, 1024 * 1024 * configurationManager.getLong(ConfigurationManager.MIN_SHARE_SIZE));
        inf.setFlagValue(Flags.HUB_MAX_ALLOWED_SHARE_SIZE, 1024 * 1024 * configurationManager.getLong(
                ConfigurationManager.MAX_SHARE_SIZE));
        inf.setFlagValue(Flags.HUB_MIN_ALLOWED_SLOTS, configurationManager.getInt(
                ConfigurationManager.MIN_SLOT_COUNT));
        inf.setFlagValue(Flags.HUB_MAX_ALLOWED_SLOTS, configurationManager.getInt(
                ConfigurationManager.MAX_SLOT_COUNT));
        inf.setFlagValue(Flags.HUB_MAX_AMOUNT_HUBS_WHERE_NORMAL_USER, configurationManager.getInt(
                ConfigurationManager.MAX_HUBS_USERS));
        inf.setFlagValue(Flags.HUB_MAX_AMOUNT_HUBS_WHERE_REGISTERED_USER, configurationManager.getInt(
                ConfigurationManager.MAX_HUBS_REGISTERED));
        inf.setFlagValue(Flags.HUB_MAX_AMOUNT_HUBS_WHERE_OP, configurationManager.getInt(ConfigurationManager.MAX_OP_IN_HUB));
        inf.setFlagValue(Flags.HUB_MAX_ALLOWED_USERS, configurationManager.getInt(ConfigurationManager.MAX_USERS));
        // TODO [lh] Remove Main class usage
        inf.setFlagValue(Flags.HUB_UPTIME, Main.getUptime());

        return inf;
    }


}
