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
import org.slf4j.Marker;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.STAException;
import ru.sincore.Main;
import ru.sincore.client.AbstractClient;
import ru.sincore.db.dao.BanListDAOImpl;
import ru.sincore.db.dao.ClientCountDAOImpl;
import ru.sincore.db.dao.ShareSizeDAOImpl;
import ru.sincore.db.pojo.BanListPOJO;
import ru.sincore.i18n.Messages;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *  @author Valor
 */
public class ClientUtils
{
	private static final Logger log = LoggerFactory.getLogger(ClientUtils.class);
	private static String marker = Marker.ANY_NON_NULL_MARKER;

	/**
	 * Method for kick fucking user! Kick is the same as ban for 5 mins.
	 * @param commandOwner Op nickname which want to ban user
	 * @param clientNick kicked client
	 * @param banType type of ban: kick, temp ban, perm ban
     * @param banExpiredDate ban expires date
     * @param reason reason kicked
     * @return status - was client kicked
	 */
	public static boolean kickOrBanClient(AbstractClient commandOwner,
                                       String clientNick,
                                       int banType,
									   Date banExpiredDate,
                                       String reason)
	{

		AbstractClient client = ClientManager.getInstance().getClientByNick(clientNick);

		if (client == null)
		{
			commandOwner.sendPrivateMessageFromHub("Client :" + clientNick + " offline now. Can not be kicked or baned!");
			return false;
		}

		boolean isKickable = client.isKickable();
        int     clientWeight     = client.getWeight();
        int     kickOwnerWeight  = commandOwner.getWeight();

		BanListPOJO kickedClient = new BanListPOJO();

		BanListDAOImpl banListDAO = new BanListDAOImpl();

		Date startBanDate = new Date();
		Calendar calendar = new GregorianCalendar();

		calendar.setTime(startBanDate);
		calendar.add(Calendar.MINUTE, 5);

        if (kickOwnerWeight <= clientWeight)
        {
            commandOwner.sendPrivateMessageFromHub("Your weight - " +
                                                   kickOwnerWeight +
                                                   " weight of the client you are " +
                                                   "trying to kick or ban - " +
                                                   clientWeight +
                                                   ". Kick or ban clients with bigger weight than yours is unacceptable!");
            return false;
        }

		if (!isKickable)
		{
            commandOwner.sendPrivateMessageFromHub("Client with nick \'" + clientNick + "\' can not be kicked!");
            return false;
        }

        kickedClient.setIp(client.getRealIP());
        kickedClient.setBanType(banType);
        kickedClient.setDateStart(startBanDate);

        if (banType == Constants.KICK)
        {
            kickedClient.setDateStop(calendar.getTime());
        }
        else
        {
            kickedClient.setDateStop(banExpiredDate);
        }

        kickedClient.setEmail(client.getEmail());
        kickedClient.setHostName("Feature not implemented yet");
        kickedClient.setNick(client.getNick());
        kickedClient.setOpNick(commandOwner.getNick());
        kickedClient.setShareSize(client.getShareSize());

        if (reason != null)
        {
            kickedClient.setReason(reason);
        }
        else
        {
            kickedClient.setReason("No\\sreason");
        }

        boolean kickProcedure = banListDAO.addBan(kickedClient);

        if (!kickProcedure)
        {
            commandOwner.sendPrivateMessageFromHub("Client " +
                                                   clientNick +
                                                   " doesn\'t have dropped, can not be kicked!");
            return false;
        }
        switch (banType)
        {
            case Constants.KICK:
                client.sendPrivateMessageFromHub("You was kicked by >>" +
                                                 commandOwner.getNick() +
                                                 "<< reason : " +
                                                 reason);
                sendMessageToOpChat("Client " +
                                                 client.getNick() +
                                                 "was kicked by " +
                                                 commandOwner.getNick() +
                                                 " with reason : " +
                                                 reason);
                break;

            case Constants.BAN_TEMPORARY:
                client.sendPrivateMessageFromHub("You was banned by >>" +
                                                 commandOwner.getNick() +
                                                 "<< reason : " +
                                                 reason +
                                                 " ban expires date :" +
                                                 banExpiredDate);
                sendMessageToOpChat("Client " +
                                                 client.getNick() +
                                                 " was banned by " +
                                                 commandOwner.getNick() +
                                                 " with reason : " +
                                                 reason +
                                                 ". Ban expires at " +
                                                 banExpiredDate);
                break;

            case Constants.BAN_PERMANENT:
                client.sendPrivateMessageFromHub("You was permanently banned by >>" +
                                                 commandOwner.getNick() +
                                                 "<< reason : " +
                                                 reason);
                sendMessageToOpChat("Client " +
                                                 client.getNick() +
                                                 " was permanently banned by " +
                                                 commandOwner.getNick() +
                                                 " with reason : " +
                                                 reason);
                break;
        }

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

        return true;
    }


    public static void sendMessageToOpChat(String message)
    {
        ConfigurationManager configurationManager = ConfigurationManager.instance();
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
                                         maxOnlinePeriodStr
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