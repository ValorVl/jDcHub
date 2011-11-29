package ru.sincore.adc.action.handlers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.Main;
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


    public SUPHandler(AbstractClient sourceClient,
                      AbstractClient targetClient,
                      SUP            action)
    {
        super(sourceClient, targetClient, action);
    }


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
                sourceClient.setFeature(feature, action.getFeatures().get(feature));
            }

            // TODO: check client SID and sid given in Client class...

            if (!sourceClient.isFeature(Features.BAS0) && !sourceClient.isFeature(Features.BASE))
            {
                new STAError(sourceClient,
                             Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_PROTOCOL_ERROR,
                             Messages.BASE_FEATURE_NOT_SUPPORTED).send();
            }

            // Check support TIGER hash..
            if (!sourceClient.isFeature(Features.TIGER))
            {
                new STAError(sourceClient,
                             Constants.STA_SEVERITY_RECOVERABLE + Constants.STA_NO_HASH_OVERLAP,
                             Messages.HASH_FUNCTION_NOT_SELECTED).send();
            }

            // if client in PROTOCOL state, send info about hub to him
            if (sourceClient.getState() == State.PROTOCOL)
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
            throws STAException, CommandException, STAException
    {
        SUP isup = new SUP();
        isup.setMessageType(MessageType.I);
        // TODO: [hatred] ask feathures from config and via sync signal or any other way
        // BASE
        isup.getFeatures().put(Features.BASE,  true);
        isup.getFeatures().put(Features.BAS0,  true);
        isup.getFeatures().put(Features.TIGER, true);
        isup.getFeatures().put(Features.UCM0,  true);
        isup.getFeatures().put(Features.ADC0,  true);
        // Extended
        isup.getFeatures().put(Features.PING,  true);
        isup.getFeatures().put(Features.SEGA,  true);
        sourceClient.sendRawCommand(isup.getRawCommand());


        SID isid = new SID();
        isid.setMessageType(MessageType.I);
        isid.setSourceSID(sourceClient.getSid());
        sourceClient.sendRawCommand(isid.getRawCommand());


        INF iinf = new INF();
        iinf.setMessageType(MessageType.I);
        iinf.setFlagValue(Flags.CLIENT_TYPE, 32);
        iinf.setFlagValue(Flags.VERSION ,    ConfigurationManager.instance().getAdcString(ConfigurationManager.HUB_VERSION));
        iinf.setFlagValue(Flags.NICK,        ConfigurationManager.instance().getAdcString(ConfigurationManager.HUB_NAME));

        if (!ConfigurationManager.instance().getAdcString(ConfigurationManager.HUB_DESCRIPTION).isEmpty())
        {
            iinf.setFlagValue(Flags.DESCRIPTION, ConfigurationManager.instance().getAdcString(ConfigurationManager.HUB_DESCRIPTION));
        }

        // Check client flag isPingExtensionSupports, if true, send PING string
        if (sourceClient.isFeature(Features.PING))
        {
            iinf = pingQuery(iinf);
        }

        sourceClient.sendRawCommand(iinf.getRawCommand());
    }


    /**
     * Method build PING request string
     * @return ping request string
     */
    private static INF pingQuery(INF iinf)
            throws CommandException, STAException
    {
        iinf.setFlagValue(Flags.HUB_HOST, ConfigurationManager.instance().getString(ConfigurationManager.HUB_LISTEN));
        iinf.setFlagValue(Flags.HUB_USERS_ONLINE, ClientManager.getInstance().getClientsCount());
        iinf.setFlagValue(Flags.HUB_TOTAL_SHARE_SIZE, ClientManager.getInstance().getTotalShare());
        iinf.setFlagValue(Flags.HUB_TOTAL_SHARED_FILES, ClientManager.getInstance().getTotalFileCount());
        iinf.setFlagValue(Flags.HUB_MIN_ALLOWED_SHARE_SIZE, 2048 * ConfigurationManager.instance().getLong(ConfigurationManager.MIN_SHARE_SIZE));
        iinf.setFlagValue(Flags.HUB_MAX_ALLOWED_SHARE_SIZE, 2048 * ConfigurationManager.instance().getLong(ConfigurationManager.MAX_SHARE_SIZE));
        iinf.setFlagValue(Flags.HUB_MIN_ALLOWED_SLOTS, ConfigurationManager.instance().getInt(ConfigurationManager.MIN_SLOT_COUNT));
        iinf.setFlagValue(Flags.HUB_MAX_ALLOWED_SLOTS, ConfigurationManager.instance().getInt(ConfigurationManager.MAX_SLOT_COUNT));
        iinf.setFlagValue(Flags.HUB_MAX_AMOUNT_HUBS_WHERE_NORMAL_USER, ConfigurationManager.instance().getInt(ConfigurationManager.MAX_HUBS_USERS));
        iinf.setFlagValue(Flags.HUB_MAX_AMOUNT_HUBS_WHERE_REGISTERED_USER, ConfigurationManager.instance().getInt(ConfigurationManager.MAX_HUBS_REGISTERED));
        iinf.setFlagValue(Flags.HUB_MAX_AMOUNT_HUBS_WHERE_OP, ConfigurationManager.instance().getInt(ConfigurationManager.MAX_OP_IN_HUB));
        iinf.setFlagValue(Flags.HUB_MAX_ALLOWED_USERS, ConfigurationManager.instance().getInt(ConfigurationManager.MAX_USERS));
        // TODO [lh] Remove Main class usage
        iinf.setFlagValue(Flags.HUB_UPTIME, Main.getUptime());

        return iinf;
    }


}
