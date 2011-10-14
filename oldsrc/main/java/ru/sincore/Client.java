package ru.sincore;

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

import org.apache.mina.core.future.WriteFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Exceptions.STAException;
import ru.sincore.Modules.Modulator;
import ru.sincore.Modules.Module;
import ru.sincore.adc.Features;
import ru.sincore.adc.State;
import ru.sincore.db.dao.ClientListDAO;
import ru.sincore.db.dao.ClientListDAOImpl;
import ru.sincore.db.pojo.ClientListPOJO;
import ru.sincore.util.AdcUtils;

import java.util.List;
import java.util.Vector;


/**
 * A class that contains client information and provides
 * functions to work with client.
 * Contains client information in {@link ru.sincore.ClientHandler}  class instance.
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
 * @author Valoer
 *
 * @since 2011-09-06
 */
public class Client
{
    public static final Logger log = LoggerFactory.getLogger(Client.class);

	private ConfigurationManager configInstance = ConfigurationManager.instance();

    /**
     * Handler to client info
     */
    private ClientHandler handler;


    /**
     * Client Feature list
     */
    private List<String> features;

    /**
     * Creates a new instance of Client
     */
    public Client()
    {
        handler = new ClientHandler();
        features = new Vector<String>();
    }


    /**
     * Return ClientHandler
     * @return handler
     */
    public ClientHandler getClientHandler()
    {
        return handler;
    }

	/**
	 *  Store data about client if client begin registration procedure, or update statistic data.
	 *
	 *
	 *  @see ClientListDAOImpl
	 *  @see ClientListPOJO
	 *  @see ClientHandler
	 */
    public void storeInfo() throws STAException
	{



		ClientListDAO clientListDAO = new ClientListDAOImpl();

		ClientListPOJO clientInfo = clientListDAO.getClientByNick(handler.getNI());

		if (clientInfo == null)
		{
			clientInfo = new ClientListPOJO();
		}

		// set CID
		clientInfo.setCid(handler.getID());
		// set IP
		clientInfo.setCurrentIp(handler.getI4());
		// set Nickname
        clientInfo.setNickName(handler.getNI());
		// set password
		clientInfo.setPassword(handler.getPassword());

		clientInfo.setClientType(Integer.valueOf(handler.getCT()));

		if (!handler.isReg())
		{
			clientInfo.setSharedFilesCount(0L);
			clientInfo.setShareSize(0L);
			clientInfo.setRegDate(handler.getCreatedOn());
			clientInfo.setWeight(10);
			clientInfo.setRegOwner(handler.getWhoRegged());
			clientInfo.setReg(true);
		}
		else
		{
			clientInfo.setSharedFilesCount(handler.getSF());
			clientInfo.setShareSize(handler.getSS());
			clientInfo.setWeight(handler.getWeight());
		}



		if (handler.isReg())
		{
			clientInfo.setLastNick(handler.getLastNick());
		}

		clientInfo.setHideMe(handler.isHideMe());
        clientInfo.setOverrideShare(handler.isOverrideShare());
        clientInfo.setOverrideSpam(handler.isOverrideSpam());
        clientInfo.setOverrideFull(handler.isOverrideFull());
        clientInfo.setKickable(handler.isKickable());
        clientInfo.setRenameable(handler.isRenameable());
        clientInfo.setAccountFlyable(handler.isAccountFlyable());
        clientInfo.setOpChatAccess(handler.isOpchatAccess());
        clientInfo.setLastMessage(handler.getLastMessageText());

		if (handler.isReg())
			clientInfo.setLastIp(handler.getI4());

		if (handler.getTimeOnline() == null || !handler.isReg())
		{
			clientInfo.setMaximumTimeOnline(0L);
		}
		else
		{
			clientInfo.setMaximumTimeOnline(handler.getTimeOnline());
		}

        // TODO [lh] remove next 2 lines
        clientInfo.setCommandMask("".getBytes());
        clientInfo.setHelpMask("".getBytes());


        clientListDAO.addClient(clientInfo);
	}


    public boolean loadInfo()
    {
        ClientListPOJO clientInfo = null;
        ClientListDAO clientListDAO = new ClientListDAOImpl();
        clientInfo = clientListDAO.getClientByNick(handler.getNI());
        if (clientInfo == null)
            return false;

        handler.setWeight(clientInfo.getWeight());
        handler.setPassword(clientInfo.getPassword());
        handler.setReg(clientInfo.getReg());
        handler.setLastNick(clientInfo.getLastNick());
        // TODO [lh] rename
        handler.setWhoRegged(clientInfo.getRegOwner());
        // TODO [lh] rename, change type
        //handler.setCreatedOn(clientInfo.getRegDate().getTime());
        // TODO [lh] change type
        //handler.setLastLogin(clientInfo.getLastLogIn().getTime());
        handler.setLastIP(clientInfo.getLastIp());
        handler.setHideMe(clientInfo.getHideMe());
        handler.setOverrideShare(clientInfo.getOverrideShare());
        handler.setOverrideSpam(clientInfo.getOverrideSpam());
        handler.setOverrideFull(clientInfo.getOverrideFull());
        handler.setKickable(clientInfo.getKickable());
        handler.setRenameable(clientInfo.getRenameable());
        handler.setAccountFlyable(clientInfo.getAccountFlyable());
        handler.setOpchatAccess(clientInfo.getOpChatAccess());
        handler.setLastMessageText(clientInfo.getLastMessage());

        return true;
    }


    public void operationComplete(WriteFuture future)
    {
        // if data was not written
        if (!future.isWritten())
            // close connection immediately
            handler.getSession().close(true);
    }


    // TODO [lh] Next two functions must be rewritten to remove code duplication
	// TODO [Valor] Fuck.. this is ban or kick ?
    public void kickMeOut(ClientHandler whokicked,
                          String kickmsg,
                          int bantype,
                          Long kicktime)
    {
        kickmsg = AdcUtils.fromAdcString(kickmsg);

        if (!handler.isKickable())
        {
            whokicked.sendFromBot("" + handler.getNI() + " is unkickable.");
            return;
        }

        if (kicktime != -1)
            // convert it from sec to ms
            kicktime *= 1000;


//        KickListDAOImpl kickList = new KickListDAOImpl();
//        Calendar c = new GregorianCalendar();
//
//		c.add(Calendar.MINUTE,5); //add to the current date 5  minutes
//		c.add(Calendar.HOUR,6); // add to the current date 6 days
//		c.add(Calendar.DAY_OF_MONTH,2); //add to the current date 2 months
//
//		Date banExpired = c.getTime();
//
//		// Build kick list object
//		KickListPOJO kickClient = new KickListPOJO();
//
//		kickClient.setIp(handler.getU4());
//		kickClient.setKickDate(new Date());
//		kickClient.setKickExpiredDate(banExpired);
//		kickClient.setKickOwner(whokicked.getNI());
//		kickClient.setNickName(handler.getNI());
//		kickClient.setReason(kickmsg);
//
//        kickList.addKickedClient(kickClient);


        switch (bantype)
        {
            // ban by nick
            case 1:
//                BanList.addban(bantype, handler.getNI(), kicktime, whokicked.getNI(), kickmsg);
                break;

            // ban by ip
            case 2:
//                BanList.addban(bantype, handler.getRealIP(), kicktime, whokicked.getNI(), kickmsg);
                break;

            // ban by cid
            case 3:
//                BanList.addban(bantype, handler.getID(), kicktime, whokicked.getNI(), kickmsg);
                break;
        }

        String brcast = "IQUI " +
                        handler.getSID() +
                        " ID" +
                        whokicked.getSID() +
                        " TL" +
                        Long.toString(kicktime);

        if (!kickmsg.equals(""))
            brcast = brcast + " MS" + AdcUtils.toAdcString(kickmsg);

        if (!ConfigurationManager.instance().getString(ConfigurationManager.REDIRECT_URL).equals(""))
            brcast = brcast + " RD" + AdcUtils.toAdcString(ConfigurationManager.instance()
                                                                               .getString(
                                                                                       ConfigurationManager.REDIRECT_URL));

        Broadcast.getInstance().broadcast(brcast);

        handler.setKicked();
        handler.getSession().close(true);


        whokicked.sendFromBot("Kicked user " +
                              handler.getNI() +
                              " with CID " +
                              handler.getID() +
                              " out in flames.");

        log.info(whokicked.getNI() +
                    " kicked user " +
                    handler.getNI() +
                    " with CID " +
                    handler.getID() +
                    " out in flames.");
    }


    public void kickMeByBot(String kickmsg, int bantype, Long kicktime)
    {
        kickmsg = AdcUtils.fromAdcString(kickmsg);

        if (!handler.isKickable())
        {
            return;
        }

        if (kicktime != -1)
            kicktime *= 1000;

        switch (bantype)
        {
            // ban by nick
            case 1:
//                BanList.addban(bantype, handler.getNI(), kicktime, ConfigurationManager.instance().getString(ConfigurationManager.BOT_CHAT_NAME), kickmsg);
                break;

            // ban by ip
            case 2:
//                BanList.addban(bantype, handler.getRealIP(), kicktime, ConfigurationManager.instance().getString(ConfigurationManager.BOT_CHAT_NAME), kickmsg);
                break;

            // ban by cid
            case 3:
//                BanList.addban(bantype, handler.getID(), kicktime, ConfigurationManager.instance().getString(ConfigurationManager.BOT_CHAT_NAME), kickmsg);
                break;
        }

        String brcast = "IQUI " +
                        handler.getSID() +
                        " ID" +
                        ConfigurationManager.instance().getString(ConfigurationManager.BOT_CHAT_SID) +
                        " TL" + Long.toString(kicktime);

        if (!kickmsg.equals(""))
            brcast = brcast + " MS" + AdcUtils.toAdcString(kickmsg);

        if (!ConfigurationManager.instance().getString(ConfigurationManager.REDIRECT_URL).equals(""))
            brcast = brcast + " RD" + AdcUtils.toAdcString(ConfigurationManager.instance()
                                                                               .getString(
                                                                                       ConfigurationManager.REDIRECT_URL));

        Broadcast.getInstance().broadcast(brcast);

        handler.setKicked();
        this.handler.getSession().close(true);

        log.info(ConfigurationManager.instance().getString(ConfigurationManager.BOT_CHAT_NAME) +
                 " kicked user " +
                 handler.getNI() +
                 " with CID " +
                 handler.getID() +
                 " out in flames.");
    }


    public void kickMeByBot(String kickmsg, int bantype)
    {
        kickMeByBot(kickmsg, bantype, Long.parseLong(Integer.toString(ConfigurationManager.instance().getInt(ConfigurationManager.KICK_DURATION))));
    }


    public void kickMeOut(ClientHandler whokicked, String kickmsg, int bantype)
    {
        kickMeOut(whokicked, kickmsg, bantype, Long.parseLong(Integer.toString(ConfigurationManager.instance().getInt(ConfigurationManager.KICK_DURATION))));
    }


    public void dropMeImGhost()
    {
        Broadcast.getInstance().broadcast("IQUI " + handler.getSID(), this);
        handler.setKicked();
        handler.getSession().close(true);
    }


    public void dropMe(ClientHandler whokicked)
    {
        if (!handler.isKickable())
        {
            whokicked.sendFromBot("" + handler.getNI() + " is undroppable.");
            return;
        }

        Broadcast.getInstance()
                 .broadcast("IQUI " + handler.getSID() + " ID" + whokicked.getSID());

        handler.setKicked();
        this.handler.getSession().close(true);

        whokicked.sendFromBot("Dropped user " +
									  handler.getNI() +
									  " with CID " +
									  handler.getID() +
									  " down from the sky.");
    }


    public void redirectMe(ClientHandler whokicked, String URL)
    {
        if (!handler.isKickable())
        {
            whokicked.sendFromBot("" + handler.getNI() + " is unredirectable.");
            return;
        }

        Broadcast.getInstance()
                 .broadcast("IQUI " +
                            handler.getSID() +
                            " ID" +
                            whokicked.getSID() +
                            " RD" +
                            URL);

        handler.setKicked();
        this.handler.getSession().close(true);


        whokicked.sendFromBot("Redirected user " +
									  handler.getNI() +
									  " with CID " +
									  handler.getID() +
									  " to " +
									  URL +
									  ".");
    }


    /**
     * Handler is called when user is logged in
     */
    public void onLoggedIn()
    {
        handler.sendToClient("ISTA 000 Authenticated.");

        handler.setLastNick(handler.getNI());
        handler.setLastIP(handler.getRealIP());

        //user is OK, logged in and cool
        handler.setValidated();
        handler.setState(State.NORMAL);
        handler.setLastLogin(handler.getLoggedAt());

        if (handler.isHideMe())
            handler.sendFromBot("You are currently hidden.");

        handler.setLoggedAt(System.currentTimeMillis());
    }


    private void sendUsersInfs()
    {
        for (Client client : ClientManager.getInstance().getClients())
        {
            if (client.getClientHandler().isValidated() && !client.equals(this))
                handler.sendToClient(client.getClientHandler().getINF());
        }
    }


    /**
     * Handler is called when user is connected and after logged in process
     */
    public void onConnected()
    {
        ConfigurationManager configurationManager = ConfigurationManager.instance();

        // make client active
        handler.setActive(true);

        ClientManager.getInstance().moveClientToRegularMap(this);

        //ok now sending infs of all others to the handler
        sendUsersInfs();

        handler.sendToClient("BINF " +
                             configurationManager.getString(ConfigurationManager.BOT_CHAT_SID) +
                             " ID" +
                             configurationManager.getString(ConfigurationManager.SECURITY_CID) +
                             " NI" +
                             AdcUtils.toAdcString(
                                     configurationManager.getString(ConfigurationManager.BOT_CHAT_NAME)
                                                 ) +
                             " CT5 DE" +
                             AdcUtils.toAdcString(
                                     configurationManager.getString(ConfigurationManager.BOT_CHAT_DESCRIPTION)
                                                 ));

        handler.putOpchat(true);
        //sending inf about itself too
        handler.sendToClient(handler.getINF());

        //ok now must send INF to all clients
        Broadcast.getInstance().broadcast(handler.getINF(), this);


        if (isFeature(Features.UCMD))
        {
            //ok, he is ucmd ok, so
            handler.sendToClient("ICMD Test CT1 TTTest");
        }
        // TODO [lh] send MOTD to client
        //handler.sendFromBot(bigTextManager.getMOTD(fromClient));

        /** calling plugins...*/
        for (Module myMod : Modulator.myModules)
        {
            myMod.onConnect(handler);
        }

    }


    public void addFeature(String feature)
    {
        if (!features.contains(feature))
        {
            features.add(feature);
        }
    }


    public void removeFeature(String feature)
    {
        if (features.contains(feature))
        {
            features.remove(feature);
        }
    }


    public boolean isFeature(String feature)
    {
        return features.contains(feature);
    }


    public void setFeature(String feature, boolean isEnabled)
    {
        if (isEnabled)
        {
            addFeature(feature);
        }
        else
        {
            removeFeature(feature);
        }
    }
}
