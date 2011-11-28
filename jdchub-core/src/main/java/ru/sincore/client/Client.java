/*
 * Client.java
 *
 * Created on 07 october 2011, 12:05
 *
 * Copyright (C) 2011 Alexey 'lh' Antonov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package ru.sincore.client;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.BigTextManager;
import ru.sincore.Broadcast;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.Features;
import ru.sincore.adc.Flags;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.adc.action.actions.INF;
import ru.sincore.db.dao.ChatLogDAO;
import ru.sincore.db.dao.ChatLogDAOImpl;
import ru.sincore.db.dao.ClientListDAO;
import ru.sincore.db.dao.ClientListDAOImpl;
import ru.sincore.db.pojo.ChatLogPOJO;
import ru.sincore.db.pojo.ClientListPOJO;
import ru.sincore.i18n.Messages;
import ru.sincore.util.AdcUtils;
import ru.sincore.util.ClientUtils;
import ru.sincore.util.Constants;
import ru.sincore.util.STAError;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-10-07
 */
public class Client extends AbstractClient
{
    private static final Logger log = LoggerFactory.getLogger(Client.class);

    /**
     * Client NIO session.
     */
    private IoSession session;


    public void sendPM(AbstractClient fromClient, String rawMessage)
    {
        session.write(rawMessage);
    }


    public void sendMessage(String rawMessage)
    {
        session.write(rawMessage);
    }


    public void sendRawCommand(String rawCommand)
    {
        session.write(rawCommand);
    }


    public void setSession(IoSession session)
    {
        this.session = session;
    }


    public void removeSession(boolean immediately)
    {
        session.close(immediately);
    }


    /**
     *  Store data about client if client begin registration procedure, or update statistic data.
     *
     *
     *  @see ru.sincore.db.dao.ClientListDAOImpl
     *  @see ru.sincore.db.pojo.ClientListPOJO
     */
    public void storeInfo() throws STAException
    {
        ClientListDAO clientListDAO = new ClientListDAOImpl();

        ClientListPOJO clientInfo = clientListDAO.getClientByNick(this.getNick());

        if (clientInfo == null)
        {
            clientInfo = new ClientListPOJO();
        }

        // set CID
        clientInfo.setCid(this.getCid());
        // set IP
        clientInfo.setCurrentIp(this.getIpAddressV4());
        // set Nickname
        clientInfo.setNickName(this.getNick());
        // set password
        clientInfo.setPassword(this.getPassword());

        if (!clientInfo.isRegistred())
        {
            clientInfo.setRegDate(this.getRegistrationDate());
            clientInfo.setRegOwner(this.getRegistratorNick());
        }

        clientInfo.setSharedFilesCount(this.getSharedFiles());
        clientInfo.setShareSize(this.getShareSise());
        clientInfo.setWeight(this.getWeight());
        clientInfo.setLastNick(this.getLastNick());

        clientInfo.setRegistred(this.isRegistred());
        clientInfo.setOverrideShare(this.isOverrideShare());
        clientInfo.setOverrideSpam(this.isOverrideSpam());
        clientInfo.setOverrideFull(this.isOverrideFull());
        clientInfo.setKickable(this.isKickable());
        clientInfo.setRenameable(this.isRenameable());
        clientInfo.setLastMessage(this.getLastMessageText());
        clientInfo.setLoginCount(this.getLoginCount());
        clientInfo.setTimeOnline(this.getTimeOnline());
        clientInfo.setLastLogIn(this.getLoggedIn());


        long timeOnline = System.currentTimeMillis() - this.getLoggedIn().getTime();
        if (clientInfo.getMaximumTimeOnline() < timeOnline)
        {
            clientInfo.setMaximumTimeOnline(timeOnline);
        }


        clientListDAO.addClient(clientInfo);
    }


    public boolean loadInfo()
    {
        ClientListPOJO clientInfo = null;
        ClientListDAO clientListDAO = new ClientListDAOImpl();
        clientInfo = clientListDAO.getClientByNick(this.getNick());
        if (clientInfo == null)
            return false;

        this.setWeight(clientInfo.getWeight());
        this.setPassword(clientInfo.getPassword());
        this.setRegistred(clientInfo.isRegistred());
        this.setClientTypeByWeight(clientInfo.getWeight());
        this.setLastNick(clientInfo.getLastNick());
        this.setLastLogin(clientInfo.getLastLogIn());
        this.setRegistrationDate(clientInfo.getRegDate());
        this.setRegistratorNick(clientInfo.getRegOwner());
        this.setLastIP(clientInfo.getCurrentIp());
        this.setOverrideShare(clientInfo.getOverrideShare());
        this.setOverrideSpam(clientInfo.getOverrideSpam());
        this.setOverrideFull(clientInfo.getOverrideFull());
        this.setKickable(clientInfo.getKickable());
        this.setRenameable(clientInfo.getRenameable());
        this.setLastMessageText(clientInfo.getLastMessage());
        this.setLoginCount(clientInfo.getLoginCount() + 1);
        this.setTimeOnline(clientInfo.getTimeOnline());
        this.setMaximumTimeOnline(clientInfo.getMaximumTimeOnline());

        return true;
    }


    @Override
    public void onConnected()
    {
        // make client active
        this.setActive(true);

        ClientManager.getInstance().moveClientToRegularMap(this);

        //ok now sending infs of all others to the handler
        ClientManager.getInstance().sendClientsInfsToClient(this);

        //sending inf about itself too
        this.sendRawCommand(this.getINF());

        //ok now must send INF to all clients
        Broadcast.getInstance().broadcast(this.getINF(), this);

        if (isFeature(Features.UCMD))
        {
            //ok, he is ucmd ok, so
            this.sendRawCommand("ICMD Test CT1 TTTest");
        }

        // send MOTD
        this.sendMOTD();

        // send info client's stats
        sendMessageFromHub(Messages.get("core.client_info_header",
                                        this.getExtendedField("LC")) +
                           ClientUtils.getClientStats(this));

        // send N last messages from main chat
        this.sendNLastMessages();
    }


    private void sendMOTD()
    {
        BigTextManager bigTextManager = new BigTextManager();
        this.sendMessageFromHub(
                AdcUtils.fromAdcString(
                        bigTextManager.getText(
                                BigTextManager.MOTD,
                                (String)getExtendedField("LC"))));
    }


    private void sendNLastMessages()
    {
        ChatLogDAO chatLogDAO = new ChatLogDAOImpl();
        int lastMessageCount = ConfigurationManager.instance().getInt(ConfigurationManager.LAST_MESSAGES_COUNT);
        List<ChatLogPOJO> chatLog = chatLogDAO.getLast(lastMessageCount);

        // reverse message list (from older to newer)
        Collections.reverse(chatLog);

        StringBuilder message = new StringBuilder();

        message.append(Messages.get("core.last_chat_messages_header",
                                    lastMessageCount,
                                    (String)getExtendedField("LC")));

        for (ChatLogPOJO chatLogEntry : chatLog)
        {
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss: ");
            message.append(df.format(chatLogEntry.getSendDate()));
            message.append("<");
            message.append(chatLogEntry.getNickName());
            message.append("> ");
            message.append(AdcUtils.fromAdcString(chatLogEntry.getMessage()));
            message.append("\n");
        }

        this.sendMessageFromHub(message.toString());
    }


    @Override
    public void onLoggedIn()
    {
        try
        {
            new STAError(this, Constants.STA_SEVERITY_SUCCESS, Messages.AUTHENTICATED).send();
        }
        catch (STAException e)
        {
            e.printStackTrace();
        }

        this.setLastNick(this.getNick());
        this.setLastIP(this.getRealIP());

        //user is OK, logged in and cool
        this.setValidated();
        this.setState(State.NORMAL);

        this.setLoggedIn(new Date());
    }


    @Override
    public String getINF()
    {

        INF binf = new INF();

        try
        {
            binf.setMessageType(MessageType.B);
            binf.setSourceSID(getSid());
            binf.setCid(getCid());
            binf.setNick(getNick());

            //these were mandatory fields.. now adding the extra...
            if (getIpAddressV4() != null && !getIpAddressV4().equals(""))
            {
                binf.setFlagValue(Flags.ADDR_IPV4, getIpAddressV4());
            }

            if (getMinAutomaticSlots() != null)
            {
                binf.setFlagValue(Flags.MIN_AUTOMATIC_SLOTS, getMinAutomaticSlots());
            }

            if (getAutomaticSlotAllocator() != null)
            {
                binf.setFlagValue(Flags.AUTOMATIC_SLOT_ALLOCATOR, getAutomaticSlotAllocator());
            }

            if (getAwayStatus() != null)
            {
                binf.setFlagValue(Flags.AWAY, getAwayStatus());
            }

            if (getDescription() != null && !getDescription().equals(""))
            {
                binf.setFlagValue(Flags.DESCRIPTION, getDescription());
            }

            if (getMaxDownloadSpeed() != null)
            {
                binf.setFlagValue(Flags.MAX_DOWNLOAD_SPEED, getMaxDownloadSpeed());
            }

            if (getEmail() != null && !getEmail().equals(""))
            {
                binf.setFlagValue(Flags.EMAIL, getEmail());
            }

            if (isHidden() != false)
            {
                // TODO should change.. only for ops :)
                binf.setFlagValue(Flags.HIDDEN, true);
            }

            if (getNumberOfNormalStateHubs() != null)
            {
                binf.setFlagValue(Flags.AMOUNT_HUBS_WHERE_NORMAL_USER, getNumberOfNormalStateHubs());
            }

            if (getNumberOfHubsWhereOp() != null)
            {
                binf.setFlagValue(Flags.AMOUNT_HUBS_WHERE_OP_USER, getNumberOfHubsWhereOp());
            }

            if (getNumberOfHubsWhereRegistred() != null)
            {
                binf.setFlagValue(Flags.AMOUNT_HUBS_WHERE_REGISTERED_USER, getNumberOfHubsWhereRegistred());
            }

            if (isHubItself() != false)
            {
                binf.setFlagValue(Flags.HUB_ITSELF, true);
            }

            if (getClientType() != 0) // TODO should change.. more working here
            {
                binf.setFlagValue(Flags.CLIENT_TYPE, getClientType());
            }

            if (getSharedFiles() != null)
            {
                binf.setFlagValue(Flags.SHARED_FILES, getSharedFiles());
            }

            if (getShareSise() != null)
            {
                binf.setFlagValue(Flags.SHARE_SIZE, getShareSise());
            }

            if (getUploadSlotsOpened() != null)
            {
                binf.setFlagValue(Flags.OPENED_UPLOAD_SLOTS, getUploadSlotsOpened());
            }

            if (getFeatues().size() > 0)
            {
                binf.setFeatures(getFeatues());
            }

            if (getToken() != null && !getToken().equals(""))
            {
                binf.setFlagValue(Flags.TOKEN, getToken());
            }

            if (getUdpPortV4() != null && !getUdpPortV4().equals(""))
            {
                binf.setFlagValue(Flags.UDP_PORT_IPV4, getUdpPortV4());
            }

            if (getUdpPortV6() != null && !getUdpPortV6().equals(""))
            {
                binf.setFlagValue(Flags.UDP_PORT_IPV6, getUdpPortV6());
            }

            if (getClientIdentificationVersion() != null && !getClientIdentificationVersion().equals(""))
            {
                binf.setFlagValue(Flags.VERSION, getClientIdentificationVersion());
            }

            if (getMaxUploadSpeed() != null && getMaxUploadSpeed() != 0)
            {
                binf.setFlagValue(Flags.MAX_UPLOAD_SPEED, getMaxUploadSpeed());
            }

            return binf.getRawCommand();
        }
        catch (CommandException e)
        {
            e.printStackTrace();
        }
        catch (STAException e)
        {
            e.printStackTrace();
        }

        return null;
    }
}