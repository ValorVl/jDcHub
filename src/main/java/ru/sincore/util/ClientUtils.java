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

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
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
	public static void kickClient(AbstractClient commandOwner,String clientNick, String reason)
	{

		String OpChatSID = configInstance.getString(ConfigurationManager.OP_CHAT_SID);

		Client client = (Client) ClientManager.getInstance().getClientByNick(clientNick);
		AbstractClient opChat = ClientManager.getInstance().getClientBySID(OpChatSID);

		if (client == null)
		{
			commandOwner.sendPrivateMessageFromHub("Client :"+clientNick+" offline now. Can not be kicked!");
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
                    "trying to kick a "+ clientWeight +" Kick clients with more of your weight is unacceptable!");
            return;
        }

		if (isKickable)
		{
			kickedClient.setIp(client.getIpAddressV4());
			kickedClient.setBanType(0);
			kickedClient.setDateStart(startBanDate);
			kickedClient.setDateStop(calendar.getTime());
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

					client.sendPrivateMessageFromHub("You was kicked by >>"+ commandOwner.getNick()+ "<< reason : "+reason);

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
			commandOwner.sendPrivateMessageFromHub("Client "+clientNick+" doesn\'t have kickable flag and can not be kicked!");
		}
	}

    /**
     * The method showing formated client statistic information
     *
     * @param client client object
     */
    public static void getClientStat(AbstractClient client)
    {
        if (client == null)
        {
            return;
        }


        StringBuilder infoStr = new StringBuilder();

        infoStr.append('\n');

        infoStr.append(" >> You information\n");
        infoStr.append(" >> You nickname : ");
        infoStr.append(client.getNick());
        infoStr.append('\n');

        //TODO add other info and localize this

        client.sendPrivateMessageFromHub(infoStr.toString());
    }
}