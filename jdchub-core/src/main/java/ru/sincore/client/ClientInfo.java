/*
 * ClientInfo.java
 *
 * Created on 07 october 2011, 13:47
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

import org.apache.commons.lang.math.IntRange;
import org.apache.mina.core.session.IoSession;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.ClientType;
import ru.sincore.adc.Flags;
import ru.sincore.adc.MessageType;
import ru.sincore.adc.State;
import ru.sincore.adc.action.actions.INF;

import java.util.*;

/**
  * @author Alexey 'lh' Antonov
  * @since 2011-10-07
  */
public class ClientInfo
{
    /**
     * Client session id.
     */
    private String sid;

    /**
     * NI field.
     * Nickname, string.
     * The hub must ensure that this is unique in the hub up to case-sensitivity.
     * Valid are all characters in the Unicode character set with code point above 32,
     * although hubs may limit this further as they like with an appropriate error message.
     * When sent for hub, this is the nick that should be displayed before messages from the hub,
     * and may also be used as short name for the hub.
     */
    private String nick = "";

    /**
     *  Client registration flag
     */
    private boolean registred = false;

    /**
     * Client password
     */
    private String password = "";

    /**
     *  Client rights weight, default 0
     */
    private int	weight = 0;

    /**
     * Client state
     */
    private int state = State.INVALID_STATE;

    /**
     * User fully authorized and validated
     */
    private boolean validated = false;

    /**
     * Is client uses active (if true) or passive (if false) mode.
     */
    private boolean active = false;

    /**
     * ID field.
     * The CID of the client. Mandatory for C-C connections.
     */
    private String cid = "";

    /**
     * PD field.
     * The PID of the client.
     * Hubs must check that the Tiger(PID) == CID and then
     * discard the field before broadcasting it to other clients.
     * Must not be sent in C-C connections.
     */
    private String pid;

    /**
     * EM field.
     * E-mail address, string.
     */
    private String email;

    /**
     * DE field.
     * Description, string.
     * Valid are all characters in the Unicode character
     * set with code point equal to or greater than 32.
     * When sent by hub, this string should be displayed
     * in the window title of the hub window (if one exists)
     */
    private String description;

    /**
     * CT field.
     * Client (user) type, 1=bot, 2=registered user, 4=operator,
     * 8=super user, 16=hub owner, 32=hub (used when the hub sends an INF about itself).
     * Multiple types are specified by adding the numbers together.
     */
    private int clientType = 0;

    /**
     * Client Connect time in millis as Syste.gettimemillis() ; ;)
     */
    private long connectTime;

    /**
     * Client last nick. Loading from db.
     */
    private String  lastNick;

    /**
     * By whom client was registred
     */
    private String  registratorNick;

    /**
     * Date when client was created.
     */
    private Date registrationDate;

    /**
     * Last login date.
     */
    private Date    lastLogin;

    /**
     * Number of hub visits
     */
    private long    loginCount = 0L;

    /**
     * How much client was online.
     */
    private long    timeOnline;

    /**
     * Maximum time online
     */
    private long    maximumTimeOnline;

    /**
     * Client last ip.
     */
    private String  lastIP;

    /**
     * Time when client was logged in.
     */
    private Date loggedIn;

    /**
     * Salt for encryption algorithm.
     * Needed while session is live.
     */
    private String encryptionSalt;

    /**
     * Time when last MSG actionName was recieved.
     */
    private long lastMSG = 0L;

    /**
     * Time when last CTM actionName was recieved.
     */
    private long lastCTM = 0L;

    /**
     * Time when last INF actionName was recieved.
     */
    private long lastINF = 0L;

    /**
     * Time when last keep alive packed recieved
     */
    private long lastKeepAlive;

    /**
     * Real client ip
     */
    private String realIP;

    /**
     * I4 field.
     * IPv4 address without port.
     * A zero address (0.0.0.0) means that the server should replace
     * it with the real IP of the client.
     * Hubs must check that a specified address corresponds to what
     * the client is connecting from to avoid DoS attacks,
     * and only allow trusted clients to specify a different address.
     * Clients should use the zero address when connecting,
     * but may opt not to do so at the user's discretion.
     * Any client that supports incoming TCPv4 connections must also
     * add the feature TCP4 to their SU field.
     */
    private String ipAddressV4;

    /**
     * I6 field.
     * IPv6 address without port.
     * A zero address (::) means that the server should replace
     * it with the IP of the client.
     * Any client that supports incoming TCPv6 connections must also
     * add the feature TCP6 to their SU field.
     */
    private String ipAddressV6;

    /**
     * U4 field
     * Client UDP port.
     * Any client that supports incoming UDPv4 packets must also
     * add the feature UDP4 to their SU field.
     */
    private String udpPortV4;

    /**
     * U6 field
     * Same as U4, but for IPv6.
     * Any client that supports incoming UDPv6 packets must also
     * add the feature UDP6 to their SU field.
     */
    private String udpPortV6;

    /**
     * SS field
     * Share size in bytes, integer.
     */
    private Long shareSize;

    /**
     * SF field.
     * Number of shared files, integer
     */
    private Long sharedFiles;

    /**
     * VE field.
     * Client identification,
     * version (client-specific, a short identifier
     * then a floating-point version number is recommended).
     * Hubs should not discriminate agains clients based on
     * their VE tag but instead rely on SUP when it comes to
     * which clients should be allowed (for example, we only want regex clients).
     */
    private String clientIdentificationVersion;

    /**
     * US field.
     * Maximum upload speed, bits/sec, integer
     */
    private Long maxUploadSpeed;

    /**
     * DS field.
     * Maximum download speed, bits/sec, integer
     */
    private Long maxDownloadSpeed;

    /**
     * SL field.
     * Upload slots open, integer
     */
    private Integer uploadSlotsOpened;

    /**
     * AS field.
     * Automatic slot allocator speed limit, bytes/sec, integer.
     * This is the recommended method of slot allocation,
     * the client keeps opening slots as long as its total upload speed
     * doesn't exceed this value. SL then serves as a minimum number of slots open.
     */
    private Long automaticSlotAllocator;

    /**
     * AM field.
     * Minimum number of slots open in automatic slot manager mode, integer.
     */
    private Long minAutomaticSlots;

    /**
     * HN field.
     * Hubs where user is a normal user and in NORMAL state, integer.
     * While connecting, clients should not count the hub they're connecting to.
     * Hubs should increase one of the three the hub counts by one
     * before passing the client to NORMAL state.
     */
    private Integer numberOfNormalStateHubs;

    /**
     * HR field.
     * Hubs where user is registered (had to supply password) and in NORMAL state, integer.
     */
    private Integer numberOfHubsWhereRegistred;

    /**
     * HO field.
     * Hubs where user is op and in NORMAL state, integer.
     */
    private Integer numberOfHubsWhereOp;

    /**
     * TO field.
     * Token, as received in RCM/CTM, when establishing a C-C connection.
     */
    private String token;

    /**
     * AW field.
     * 1=Away
     * 2=Extended away, not interested in hub chat
     * (hubs may skip sending broadcast type MSG commands to clients with this flag)
     */
    private Integer awayStatus;

    /**
     * HI field.
     * 1=Hidden, should not be shown on the user list.
     */
    private boolean hidden;

    /**
     * HU field.
     * 1=Hub, this INF is about the hub itself
     */
    private boolean hubItself = false;

    /**
     * RF field.
     * URL of referer (hub in case of redirect, web page)
     */
    private String redirectUrl;

    /**
     * Client search step
     */
    private int searchStep = 0;

    /**
     * Time when client runs last search
     */
    private long lastSearch = 0L;

    /**
     * Time when client do last automagic search
     */
    private long lastAutomagicSearch = 0L;

    /**
     * Is client able to override share
     */
    private boolean overrideShare = false;

    /**
     * Ban by share flag
     */
    private boolean bannedByShare = false;
    
    /**
     * Is client able to override spam
     */
    private boolean overrideSpam = false;

    /**
     * Is client able to connect to hub, when hub is full
     */
    private boolean overrideFull = false;

    /**
     * Is client kickable
     */
    private boolean kickable = true;

    /**
     * Is client can change nick
     */
    private boolean renameable = false;

    /**
     * Last said message
     */
    private String lastMessageText = "";

    private String inQueueSearch = null;

    private boolean mustBeDisconnected = false;

    private HashMap<String, Object> extentedFields = new HashMap<String, Object>();

    /**
     * SU field.
     * Comma-separated list of feature FOURCC's.
     * This notifies other clients of extended capabilities of the connecting client.
     * Use with discretion.
     */
    private Vector<String> features = new Vector<String>();

    /**
     * This field contains additional stats about user for internal hub usage.
     */
    private HashMap<String, Object> additionalStats = new HashMap<String, Object>();


//************************************ Functions ***************************************************


    public String getSid()
    {
        return sid;
    }


    public void setSid(String sid)
    {
        this.sid = sid;
    }


    public String getNick()
    {
        return nick;
    }


    public void setNick(String nick)
    {
        this.nick = nick;
    }


    public boolean isRegistred()
    {
        return registred;
    }


    public void setRegistred(boolean registred)
    {
        this.registred = registred;
    }


    public String getPassword()
    {
        return password;
    }


    public void setPassword(String password)
    {
        this.password = password;
    }


    public int getWeight()
    {
        return weight;
    }


    public boolean isOp()
    {
        return getWeight() > ConfigurationManager.instance().getInt(ConfigurationManager.CLIENT_WEIGHT_REGISTRED);
    }


    public void setWeight(int weight)
    {
        this.weight = weight;
    }


    public int getState()
    {
        return state;
    }


    public void setState(int state)
    {
        this.state = state;
    }


    public boolean isValidated()
    {
        return validated;
    }


    public void setValidated()
    {
        this.validated = true;
    }


    public void setValidated(boolean validated)
    {
        this.validated = validated;
    }


    public boolean isActive()
    {
        return active;
    }


    public void setActive(boolean active)
    {
        this.active = active;
    }


    public String getCid()
    {
        return cid;
    }


    public void setCid(String cid)
    {
        this.cid = cid;
    }


    public String getPid()
    {
        return pid;
    }


    public void setPid(String pid)
    {
        this.pid = pid;
    }


    public String getEmail()
    {
        return email;
    }


    public void setEmail(String email)
    {
        this.email = email;
    }


    public String getDescription()
    {
        return description;
    }


    public void setDescription(String description)
    {
        this.description = description;
    }


    public int getClientType()
    {
        return clientType;
    }


    public void setClientType(int clientType)
    {
        this.clientType = clientType;
    }


    public void setClientTypeByWeight(int clientType)
    {
        ConfigurationManager configurationManager =  ConfigurationManager.instance();

        ArrayList<IntRange> clientTypeRanges = new ArrayList<IntRange>(5);
        // add unregistred users range
        clientTypeRanges.add(new IntRange(0,
                                          configurationManager.getInt(ConfigurationManager.CLIENT_WEIGHT_UNREGISTRED)));
        // add registred users range
        clientTypeRanges.add(new IntRange(configurationManager.getInt(ConfigurationManager.CLIENT_WEIGHT_UNREGISTRED) + 1,
                                          configurationManager.getInt(ConfigurationManager.CLIENT_WEIGHT_REGISTRED)));
        // add operators range
        clientTypeRanges.add(new IntRange(configurationManager.getInt(ConfigurationManager.CLIENT_WEIGHT_REGISTRED) + 1,
                                          configurationManager.getInt(ConfigurationManager.CLIENT_WEIGHT_OPERATOR)));
        // add super users range
        clientTypeRanges.add(new IntRange(configurationManager.getInt(ConfigurationManager.CLIENT_WEIGHT_OPERATOR) + 1,
                                          configurationManager.getInt(ConfigurationManager.CLIENT_WEIGHT_SUPER_USER)));
        // add hub owner
        clientTypeRanges.add(new IntRange(configurationManager.getInt(ConfigurationManager.CLIENT_WEIGHT_HUB_OWNER)));

        int i = 0;
        boolean rangeFound = false;
        while (i < clientTypeRanges.size() && !rangeFound)
        {
            if (clientTypeRanges.get(i).containsInteger(this.weight))
            {
                rangeFound = true;
            }
            else
            {
                i++;
            }
        }

        switch (i)
        {
            case 0:
                this.setClientType(ClientType.UNREGISTRED_USER);
                break;

            case 1:
                this.setClientType(ClientType.REGISTERED_USER);
                break;

            case 2:
                this.setClientType(ClientType.REGISTERED_USER | ClientType.OPERATOR);
                break;

            case 3:
                this.setClientType(ClientType.REGISTERED_USER | ClientType.SUPER_USER);
                break;

            case 4:
                this.setClientType(ClientType.REGISTERED_USER | ClientType.HUB_OWNER);
                break;

            default:
        }
    }


    public long getConnectTime()
    {
        return connectTime;
    }


    public void setConnectTime(long connectTime)
    {
        this.connectTime = connectTime;
    }


    public String getLastNick()
    {
        return lastNick;
    }


    public void setLastNick(String lastNick)
    {
        this.lastNick = lastNick;
    }


    public String getRegistratorNick()
    {
        return registratorNick;
    }


    public void setRegistratorNick(String registratorNick)
    {
        this.registratorNick = registratorNick;
    }


    public Date getRegistrationDate()
    {
        return registrationDate;
    }


    public void setRegistrationDate(Date registrationDate)
    {
        this.registrationDate = registrationDate;
    }


    public Date getLastLogin()
    {
        return lastLogin;
    }


    public void setLastLogin(Date lastLogin)
    {
        this.lastLogin = lastLogin;
    }


    public long getLoginCount()
    {
        return loginCount;
    }


    public void setLoginCount(long loginCount)
    {
        this.loginCount = loginCount;
    }


    public long getTimeOnline()
    {
        return timeOnline;
    }


    public void setTimeOnline(long timeOnline)
    {
        this.timeOnline = timeOnline;
    }


    public void increaseTimeOnline(long timeRange)
    {
        this.timeOnline += timeRange;
    }


    public long getMaximumTimeOnline()
    {
        return maximumTimeOnline;
    }


    public void setMaximumTimeOnline(long maximumTimeOnline)
    {
        this.maximumTimeOnline = maximumTimeOnline;
    }


    public String getLastIP()
    {
        return lastIP;
    }


    public void setLastIP(String lastIP)
    {
        this.lastIP = lastIP;
    }


    public Date getLoggedIn()
    {
        return loggedIn;
    }


    public void setLoggedIn(Date loggedIn)
    {
        this.loggedIn = loggedIn;
    }


    public String getEncryptionSalt()
    {
        return encryptionSalt;
    }


    public void setEncryptionSalt(String encryptionSalt)
    {
        this.encryptionSalt = encryptionSalt;
    }


    public long getLastMSG()
    {
        return lastMSG;
    }


    public void setLastMSG(long lastMSG)
    {
        this.lastMSG = lastMSG;
    }


    public long getLastCTM()
    {
        return lastCTM;
    }


    public void setLastCTM(long lastCTM)
    {
        this.lastCTM = lastCTM;
    }


    public long getLastINF()
    {
        return lastINF;
    }


    public void setLastINF(long lastINF)
    {
        this.lastINF = lastINF;
    }


    public long getLastKeepAlive()
    {
        return lastKeepAlive;
    }


    public void setLastKeepAlive(long lastKeepAlive)
    {
        this.lastKeepAlive = lastKeepAlive;
    }


    public String getRealIP()
    {
        return realIP;
    }


    public void setRealIP(String realIP)
    {
        this.realIP = realIP;
    }


    public String getIpAddressV4()
    {
        return ipAddressV4;
    }


    public void setIpAddressV4(String ipAddressV4)
    {
        this.ipAddressV4 = ipAddressV4;
    }


    public String getIpAddressV6()
    {
        return ipAddressV6;
    }


    public void setIpAddressV6(String ipAddressV6)
    {
        this.ipAddressV6 = ipAddressV6;
    }


    public String getUdpPortV4()
    {
        return udpPortV4;
    }


    public void setUdpPortV4(String udpPortV4)
    {
        this.udpPortV4 = udpPortV4;
    }


    public String getUdpPortV6()
    {
        return udpPortV6;
    }


    public void setUdpPortV6(String udpPortV6)
    {
        this.udpPortV6 = udpPortV6;
    }


    public Long getShareSize()
    {
        return shareSize;
    }


    public void setShareSize(Long shareSize)
    {
        this.shareSize = shareSize;

        this.setBannedByShare(false);

        if (!this.isOverrideShare())
        {
            if (shareSize <
                ConfigurationManager.instance()
                                    .getLong(ConfigurationManager.BAN_BY_SHARE_MIN_SHARE))
            {
                this.setBannedByShare(true);
            }
        }
    }


    public Long getSharedFiles()
    {
        return sharedFiles;
    }


    public void setSharedFiles(Long sharedFiles)
    {
        this.sharedFiles = sharedFiles;
    }


    public String getClientIdentificationVersion()
    {
        return clientIdentificationVersion;
    }


    public void setClientIdentificationVersion(String clientIdentificationVersion)
    {
        this.clientIdentificationVersion = clientIdentificationVersion;
    }


    public Long getMaxUploadSpeed()
    {
        return maxUploadSpeed;
    }


    public void setMaxUploadSpeed(Long maxUploadSpeed)
    {
        this.maxUploadSpeed = maxUploadSpeed;
    }


    public Long getMaxDownloadSpeed()
    {
        return maxDownloadSpeed;
    }


    public void setMaxDownloadSpeed(Long maxDownloadSpeed)
    {
        this.maxDownloadSpeed = maxDownloadSpeed;
    }


    public Integer getUploadSlotsOpened()
    {
        return uploadSlotsOpened;
    }


    public void setUploadSlotsOpened(Integer uploadSlotsOpened)
    {
        this.uploadSlotsOpened = uploadSlotsOpened;
    }


    public Long getAutomaticSlotAllocator()
    {
        return automaticSlotAllocator;
    }


    public void setAutomaticSlotAllocator(Long automaticSlotAllocator)
    {
        this.automaticSlotAllocator = automaticSlotAllocator;
    }


    public Long getMinAutomaticSlots()
    {
        return minAutomaticSlots;
    }


    public void setMinAutomaticSlots(Long minAutomaticSlots)
    {
        this.minAutomaticSlots = minAutomaticSlots;
    }


    public Integer getNumberOfNormalStateHubs()
    {
        return numberOfNormalStateHubs;
    }


    public void setNumberOfNormalStateHubs(Integer numberOfNormalStateHubs)
    {
        this.numberOfNormalStateHubs = numberOfNormalStateHubs;
    }


    public Integer getNumberOfHubsWhereRegistred()
    {
        return numberOfHubsWhereRegistred;
    }


    public void setNumberOfHubsWhereRegistred(Integer numberOfHubsWhereRegistred)
    {
        this.numberOfHubsWhereRegistred = numberOfHubsWhereRegistred;
    }


    public Integer getNumberOfHubsWhereOp()
    {
        return numberOfHubsWhereOp;
    }


    public void setNumberOfHubsWhereOp(Integer numberOfHubsWhereOp)
    {
        this.numberOfHubsWhereOp = numberOfHubsWhereOp;
    }


    public String getToken()
    {
        return token;
    }


    public void setToken(String token)
    {
        this.token = token;
    }


    public Integer getAwayStatus()
    {
        return awayStatus;
    }


    public void setAwayStatus(Integer awayStatus)
    {
        this.awayStatus = awayStatus;
    }


    public boolean isHidden()
    {
        return hidden;
    }


    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
    }


    public boolean isHubItself()
    {
        return hubItself;
    }


    public void setHubItself(boolean hubItself)
    {
        this.hubItself = hubItself;
    }


    public String getRedirectUrl()
    {
        return redirectUrl;
    }


    public void setRedirectUrl(String redirectUrl)
    {
        this.redirectUrl = redirectUrl;
    }


    public int getSearchStep()
    {
        return searchStep;
    }


    public void setSearchStep(int searchStep)
    {
        this.searchStep = searchStep;
    }


    public long getLastSearch()
    {
        return lastSearch;
    }


    public void setLastSearch(long lastSearch)
    {
        this.lastSearch = lastSearch;
    }


    public long getLastAutomagicSearch()
    {
        return lastAutomagicSearch;
    }


    public void setLastAutomagicSearch(long lastAutomagicSearch)
    {
        this.lastAutomagicSearch = lastAutomagicSearch;
    }


    public boolean isOverrideShare()
    {
        return overrideShare;
    }


    public void setOverrideShare(boolean overrideShare)
    {
        this.overrideShare = overrideShare;
    }


    public boolean isBannedByShare()
    {
        return bannedByShare;
    }


    public void setBannedByShare(boolean bannedByShare)
    {
        this.bannedByShare = bannedByShare;
    }


    public boolean isOverrideSpam()
    {
        return overrideSpam;
    }


    public void setOverrideSpam(boolean overrideSpam)
    {
        this.overrideSpam = overrideSpam;
    }


    public boolean isOverrideFull()
    {
        return overrideFull;
    }


    public void setOverrideFull(boolean overrideFull)
    {
        this.overrideFull = overrideFull;
    }


    public boolean isKickable()
    {
        return kickable;
    }


    public void setKickable(boolean kickable)
    {
        this.kickable = kickable;
    }


    public boolean isRenameable()
    {
        return renameable;
    }


    public void setRenameable(boolean renameable)
    {
        this.renameable = renameable;
    }


    public String getLastMessageText()
    {
        return lastMessageText;
    }


    public void setLastMessageText(String lastMessageText)
    {
        this.lastMessageText = lastMessageText;
    }


    public String getInQueueSearch()
    {
        return inQueueSearch;
    }


    public void setInQueueSearch(String inQueueSearch)
    {
        this.inQueueSearch = inQueueSearch;
    }

    public void setExtendedField(String fieldName, Object value)
    {
        extentedFields.put(fieldName, value);
    }


    public void disconnect()
    {
        setMustBeDisconnected(true);
    }


    public void setMustBeDisconnected(boolean mustBeDisconnected)
    {
        this.mustBeDisconnected = mustBeDisconnected;
    }

    public boolean isMustBeDisconnected()
    {
        return this.mustBeDisconnected;
    }


    public Object getExtendedField(String fieldName)
    {
        if (isExtendedFieldExists(fieldName))
        {
            return extentedFields.get(fieldName);
        }
        else
        {
            return null;
        }
    }


    public boolean isExtendedFieldExists(String fieldName)
    {
        return extentedFields.containsKey(fieldName);
    }


    public void removeExtendedField(String fieldName)
    {
        extentedFields.remove(fieldName);
    }


    public void addFeature(String feature)
    {
        if (!features.contains(feature))
        {
            features.add(feature);
        }
    }


    public List<String> getFeatues()
    {
        return features;
    }


    public String getSupportedFeatures()
    {
        StringBuilder supportedFeatures = new StringBuilder();

        for (String feature : features)
        {
            supportedFeatures.append(feature);
            supportedFeatures.append(",");
        }

        supportedFeatures.deleteCharAt(supportedFeatures.length() - 1);

        return supportedFeatures.toString();
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


    public Object getAdditionalStat(String name)
    {
        return additionalStats.get(name);
    }


    public Object setAdditionalStat(String name, Object value)
    {
        return this.additionalStats.put(name, value);
    }


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

            if (getShareSize() != null)
            {
                binf.setFlagValue(Flags.SHARE_SIZE, getShareSize());
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


    public void setSession(IoSession session) {}
    public IoSession getSession() {return null;}
    public void removeSession(boolean immediately) {}
}
