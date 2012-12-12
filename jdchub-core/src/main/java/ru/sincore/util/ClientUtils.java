package ru.sincore.util;

/*
 * jDcHub ADC HubSoft
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Broadcast;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.*;
import ru.sincore.Main;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.action.actions.MSG;
import ru.sincore.client.AbstractClient;
import ru.sincore.db.dao.*;
import ru.sincore.db.pojo.BanListPOJO;
import ru.sincore.db.pojo.ClientListPOJO;
import ru.sincore.i18n.Messages;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *  @author Valor
 */
public class ClientUtils
{
	private static final Logger log = LoggerFactory.getLogger(ClientUtils.class);

	/**
	 * Method for kick fucking user! Kick is the same as ban for 5 mins.
	 * @param commandOwnerNick Op nickname which want to ban user (must be in db).
	 * @param clientNick kicked client
     * @param reason reason kicked
     * @return status - was client kicked
	 */
    public static boolean kick(String commandOwnerNick,
                               String clientNick,
                               String reason)
            throws
            UserOfflineException,
            ClientProtectedException,
            UserNotFoundException,
            NotEnoughWeightException
    {
        AbstractClient commandOwner = ClientManager.getInstance().getClientByNick(commandOwnerNick);

        if (commandOwner == null)
        {
            throw new UserNotFoundException(commandOwnerNick);
        }


        AbstractClient client = ClientManager.getInstance().getClientByNick(clientNick);

		if (client == null)
		{
			throw new UserOfflineException(clientNick);
		}

        if (!client.isKickable())
        {
            throw new ClientProtectedException(clientNick, "kick");
        }

        // check weight
        if (commandOwner.getWeight() <= client.getWeight())
        {
            throw new NotEnoughWeightException(commandOwner.getWeight(), client.getWeight());
        }


        // make kick/ban
        BanListPOJO kick = new BanListPOJO();

        kick.setBanType(Constants.KICK);

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(kick.getDateStart());
        calendar.add(Calendar.MINUTE, 5);
        kick.setDateStop(calendar.getTime());

        kick.setNick(clientNick);

        kick.setIp(null);

        kick.setEmail(client.getEmail());
        kick.setHostName("Feature not implemented yet");
        kick.setOpNick(commandOwner.getNick());

        if (reason != null)
        {
            kick.setReason(reason);
        }
        else
        {
            kick.setReason("No\\sreason");
        }

        disconnectClient(client);

        return new BanListDAOImpl().addBan(kick);
    }


    /**
     * Ban client by nick or ip.
     * 
     * @param commandOwnerNick Op nickname which want to ban user (must be in db)
     * @param clientNick Client nick
     * @param banType Temp or perm ban.
     * @param banExpiredDate Ban expiration date.
     * @param reason Ban reason.
     * @return Was client banned?
     */
    public static boolean ban(String commandOwnerNick,
                              String clientNick,
                              int banType,
                              Date banExpiredDate,
                              String reason)
            throws UserNotFoundException, NotEnoughWeightException, UserOfflineException
    {
        AbstractClient client = null;
        AbstractClient commandOwner = ClientManager.getInstance().getClientByNick(commandOwnerNick);

        if (commandOwner == null)
        {
            throw new UserOfflineException(commandOwnerNick);
        }

        client = ClientManager.getInstance().getClientByNick(clientNick);

        // check weight
        if ((client != null) && (commandOwner.getWeight() <= client.getWeight()))
        {
            throw new NotEnoughWeightException(commandOwner.getWeight(), client.getWeight());
        }


        // make kick/ban
        BanListPOJO ban = new BanListPOJO();

        ban.setBanType(banType);
        ban.setDateStop(banExpiredDate);
        ban.setNick(clientNick);
        ban.setHostName("Feature not implemented yet");
        ban.setOpNick(commandOwner.getNick());
        ban.setIp(null);

        if (reason != null)
        {
            ban.setReason(reason);
        }
        else
        {
            ban.setReason("No\\sreason");
        }

        disconnectClient(client);

        return new BanListDAOImpl().addBan(ban);
    }


    /**
     * Ban client by nick or ip.
     *
     * @param commandOwnerNick Op nickname which want to ban user (must be in db)
     * @param subnetUtils describes ip range for ban by address and mask
     * @param banType Temp or perm ban.
     * @param banExpiredDate Ban expiration date.
     * @param reason Ban reason.
     * @return Was client banned?
     */
    public static boolean ban(String commandOwnerNick,
                              SubnetUtils subnetUtils,
                              int banType,
                              Date banExpiredDate,
                              String reason)
            throws UserNotFoundException, NotEnoughWeightException
    {
        ClientListDAO clientListDAO = new ClientListDAOImpl();
        ClientListPOJO commandOwner = clientListDAO.getClientByNick(commandOwnerNick);

        if (commandOwner == null)
        {
            throw new UserNotFoundException(commandOwnerNick);
        }

        List<AbstractClient> clients = ClientManager.getInstance().getClientsByNetwork(subnetUtils);

        if (!clients.isEmpty())
        {
            // check weight
            for (AbstractClient client : clients)
            {
                if (commandOwner.getWeight() <= client.getWeight())
                {
                    clients.remove(client);
                }
            }
        }

        // make kick/ban
        BanListPOJO ban = new BanListPOJO();

        ban.setBanType(banType);
        ban.setDateStop(banExpiredDate);
        ban.setNick(null);
        ban.setHostName("Feature not implemented yet");
        ban.setOpNick(commandOwner.getNickName());
        ban.setIp(subnetUtils.getInfo().getCidrSignature());

        if (reason != null)
        {
            ban.setReason(reason);
        }
        else
        {
            ban.setReason("No\\sreason");
        }

        for (AbstractClient client : clients)
            disconnectClient(client);

        return new BanListDAOImpl().addBan(ban);
    }


    private static void disconnectClient(AbstractClient client)
    {
        try
        {
            new STAError(client,
                         Constants.STA_SEVERITY_FATAL + Constants.STA_GENERIC_KICK_DISCONNECT_BAN,
                         Messages.GENERIC_KICK_DISCONNECT_BAN).send();
        }
        catch (STAException e)
        {
            // ignore
        }

        //disconnect session
        client.disconnect();
    }
    

    /**
     * Construct MSG and broadcast it as hub bot' message.
     *
     * @param message Text message
     */
    public static void broadcastTextMessageFromHub(String message)
    {
        try
        {
            AbstractClient hubBot = ClientManager.getInstance()
                                                 .getClientBySID(ConfigurationManager.getInstance()
                                                                                     .getString(
                                                                                             ConfigurationManager.HUB_SID));
            MSG outgoingMessage = new MSG();
            outgoingMessage.setMessage(message);
            outgoingMessage.setMessageType(MessageType.B);
            outgoingMessage.setSourceSID(hubBot.getSid());
            // send outgoing message
            Broadcast.getInstance().broadcast(outgoingMessage.getRawCommand(), hubBot);
        }
        catch (Exception e)
        {
            log.error(e.toString());
        }
    }
    
    
    public static void sendMessageToOpChat(String message)
    {
        ConfigurationManager configurationManager = ConfigurationManager.getInstance();
        ClientManager.getInstance()
                     .getClientBySID(
                             configurationManager.getString(ConfigurationManager.OP_CHAT_SID)
                                    )
                     .sendPrivateMessageFromHub(message);
    }


    /**
     * The method showing formated client statistic information
     *
     * @param client client object
     * @return Client's stats
     */
    public static String getClientStats(AbstractClient client)
    {
        if (client == null)
        {
            return "";
        }

        if (client.isRegistred())
        {
            String onlinePeriodStr = DurationFormatUtils.formatDuration(client.getTimeOnline(),
                                                                        Messages.get(Messages.TIME_PERIOD_FORMAT,
                                                                                     (String) client
                                                                                             .getExtendedField(
                                                                                                     "LC")),
                                                                        true);
            String maxOnlinePeriodStr =
                    DurationFormatUtils.formatDuration(client.getMaximumTimeOnline(),
                                                       Messages.get(Messages.TIME_PERIOD_FORMAT,
                                                                    (String) client.getExtendedField(
                                                                            "LC")),
                                                       true);

            return Messages.get("core.registered_client_info",
                                 new Object[]
                                 {
                                         client.getNick(),
                                         client.getWeight(),
                                         ((client.getPassword() != null) && (!client.getPassword().equals(""))) ?
                                            "Yes" : "No",
                                         client.getLastLogin(),
                                         client.getLastIP(),
                                         client.getLoginCount(),
                                         client.getRegistrationDate(),
                                         client.getRegistratorNick(),
                                         onlinePeriodStr,
                                         maxOnlinePeriodStr,
                                         humanReadableByteCount(client.getRxBytes(), false),
                                         humanReadableByteCount(client.getTxBytes(), false)
                                 },
                                 (String)client.getExtendedField("LC"));
        }
        else
        {
            return Messages.get("core.unregistered_client_info",
                                 new Object[]
                                 {
                                         client.getNick(),
                                         client.getWeight()
                                 },
                                 (String)client.getExtendedField("LC"));
        }
    }


    /**
     * Converts byte size into human readable format.
     * Code found here : <a href="http://stackoverflow.com/a/3758880/157466">http://stackoverflow.com/a/3758880/157466</a>
     * @param bytes bytes count
     * @param si like MiB or MB
     * @return formatted string
     */
    public static String humanReadableByteCount(long bytes, boolean si)
    {
        int unit = si ? 1000 : 1024;
        if (bytes < unit)
        {
            return bytes + " B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }


    public static String getHubInfo(AbstractClient client)
    {
        String timeFormated = DurationFormatUtils.formatDuration(Main.getUptime(),
                                                                 "MM-dd HH:mm:ss",
                                                                 true);

        Long maxClientCount = (new ClientCountDAOImpl()).getMaxCount();
        Long maxShareSize = (new ShareSizeDAOImpl()).getMaxShareSize();
        String formattedMaxShareSize = humanReadableByteCount(maxShareSize, false);
        
        return Messages.get(Messages.HUB_INFO_MESSAGE,
                            new Object[]
                            {
                                    timeFormated,
                                    maxClientCount,
                                    formattedMaxShareSize
                            },
                            (String) client.getExtendedField("LC")
                           );

    }
}