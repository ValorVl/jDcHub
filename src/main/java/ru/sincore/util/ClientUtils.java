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
import ru.sincore.client.AbstractClient;
import ru.sincore.client.Client;
import ru.sincore.db.dao.BanListDAOImpl;
import ru.sincore.db.pojo.BanListPOJO;
import ru.sincore.i18n.Messages;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 *  @author Valor
 */
public class ClientUtils
{
	private static final Logger log = LoggerFactory.getLogger(ClientUtils.class);
	private static String marker = Marker.ANY_NON_NULL_MARKER;
	private static ConfigurationManager configInstance = ConfigurationManager.instance();

	/**
	 * Method for kick fucking user! Kick this ban on 5 minutes.
	 * @param commandOwner Nickname OP who kicked
	 * @param clientNick kicked client
	 * @param reason reason kicked
	 */
	public static void kickOrBanClient(AbstractClient commandOwner,String clientNick,int banType,
									   Date banExpiredDate, String reason)
	{

		String OpChatSID = configInstance.getString(ConfigurationManager.OP_CHAT_SID);

		Client client = (Client) ClientManager.getInstance().getClientByNick(clientNick);
		AbstractClient opChat = ClientManager.getInstance().getClientBySID(OpChatSID);

		if (client == null)
		{
			commandOwner.sendPrivateMessageFromHub("Client :"+clientNick+" offline now. Can not be kicked or baned!");
			return;
		}

		boolean isKickable = client.isKickable();
        int     clientWeight     = client.getWeight();
        int     kickOwnerWeight  = commandOwner.getWeight();

		BanListPOJO kickedClient = new BanListPOJO();

		BanListDAOImpl banListDAO = new BanListDAOImpl();

		Date startBanDate = new Date();
		Calendar calendar = new GregorianCalendar();

		calendar.setTime(startBanDate);
		calendar.add(Calendar.MINUTE,5);

        if (kickOwnerWeight <= clientWeight)
        {
            commandOwner.sendPrivateMessageFromHub("Your weight "+ kickOwnerWeight +" weight of the client you are " +
                    "trying to kick or ban a "+ clientWeight +" Kick or ban clients with bigger weight of yours is unacceptable!");
            return;
        }

		if (isKickable)
		{
			kickedClient.setIp(client.getIpAddressV4());
			kickedClient.setBanType(banType);
			kickedClient.setDateStart(startBanDate);

			if (banType == 0)
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
			kickedClient.setShareSize(client.getShareSise());

			if (reason != null)
			{
				kickedClient.setReason(reason);
			}
			else
			{
				kickedClient.setReason("No\\sreason");
			}

			boolean kickProcedure = banListDAO.addBan(kickedClient);

			if (kickProcedure)
			{
				try
				{
					client.storeInfo();

					if (banType == 0)
					{
						client.sendPrivateMessageFromHub("You was kicked by >>"+ commandOwner.getNick()+ "<< reason : "+reason);
					}
					else if (banType == 1)
					{
						client.sendPrivateMessageFromHub("You was baned by >>"+ commandOwner.getNick()+ "<< reason : "+reason +
																 " ban expired date :" + banExpiredDate);
					}
					else if (banType == 2)
					{
						client.sendPrivateMessageFromHub("You was permanently baned by >>"+ commandOwner.getNick()+ "<< reason : "+reason);
					}

					client.removeSession(true);

				} catch (STAException e)
				{
					log.error(marker,e);
				}

				StringBuilder sb = new StringBuilder(5);
                //TODO localize output message
				sb.append('\n');
				sb.append(":: Client ");
				sb.append(clientNick);
				sb.append('\n');
				sb.append(new Date());

				opChat.sendPrivateMessageFromHub(sb.toString());
			}
		}
		else
		{
			commandOwner.sendPrivateMessageFromHub("Client "+clientNick+" doesn\'t have dropped, can not be kicked!");
		}
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
}