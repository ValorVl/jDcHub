package ru.sincore;
/*
 * ClientHandler.java
 *
 * Created on 03 martie 2007, 23:09
 *
 * DSHub ADC HubSoft
 * Copyright (C) 2007,2008  Eugen Hristev
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

import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import ru.sincore.adc.State;
import ru.sincore.util.AdcUtils;

/**
 * Main client class, keeps all info regarding a client.
 * Also implements disconnecting methods.
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-06
 */
public class ClientHandler
{
    private int loggedIn = 0;

    /**
     * User fully authorized and validated
     */
    private boolean validated = false;

    /**
     * Is client uses active (if true) or passive (if false) mode.
     */
    private boolean active = false;

    private boolean canReceiveCmds = false;

    /**
     * Time when client was logged in.
     */
    private long loggedAt = 0L;

    /**
     * Salt for encryption algorithm.
     * Needed while session is live.
     */
    private String encryptionSalt;

    /**
     * Time when last MSG command was recieved.
     */
    private long lastMSG = 0L;

    /**
     * Time when last CTM command was recieved.
     */
    private long lastCTM = 0L;

    /**
     * Time when last INF command was recieved.
     */
    private long lastINF = 0L;

    // TODO use it in SessionManager#sessionIdle function
    /**
     * Time when last keep alive packed recieved
     */
    private long lastKeepAlive;

    /**
     * Client state
     */
    private int state = State.INVALID_STATE;

    /**
     * Real client ip
     */
    private String realIP;

    /**
     * The CID of the client. Mandatory for C-C connections.
     */
    private String ID = "";

    /**
     * The PID of the client.
     * Hubs must check that the Tiger(PID) == CID and then
     * discard the field before broadcasting it to other clients.
     * Must not be sent in C-C connections.
     */
    private String PD;

    /**
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
    private String I4;

    /**
     * IPv6 address without port.
     * A zero address (::) means that the server should replace
     * it with the IP of the client.
     * Any client that supports incoming TCPv6 connections must also
     * add the feature TCP6 to their SU field.
     */
    private String I6;

    /**
     * Client UDP port.
     * Any client that supports incoming UDPv4 packets must also
     * add the feature UDP4 to their SU field.
     */
    private String U4;

    /**
     * Same as U4, but for IPv6.
     * Any client that supports incoming UDPv6 packets must also
     * add the feature UDP6 to their SU field.
     */
    private String U6;

    /**
     * Share size in bytes, integer.
     */
    private Long SS;

    /**
     * Number of shared files, integer
     */
    private Long SF;

    /**
     * Client identification,
     * version (client-specific, a short identifier
     * then a floating-point version number is recommended).
     * Hubs should not discriminate agains clients based on
     * their VE tag but instead rely on SUP when it comes to
     * which clients should be allowed (for example, we only want regex clients).
     */
    private String VE;

    /**
     * Maximum upload speed, bits/sec, integer
     */
    private Long US;

    /**
     * Maximum download speed, bits/sec, integer
     */
    private Long DS;

    /**
     * Upload slots open, integer
     */
    private Integer SL;

    /**
     * Automatic slot allocator speed limit, bytes/sec, integer.
     * This is the recommended method of slot allocation,
     * the client keeps opening slots as long as its total upload speed
     * doesn't exceed this value. SL then serves as a minimum number of slots open.
     */
    private Long AS;

    /**
     * Maximum number of slots open in automatic slot manager mode, integer.
     */
    private Long AM;

    /**
     * E-mail address, string.
     */
    private String EM;

    /**
     * Nickname, string.
     * The hub must ensure that this is unique in the hub up to case-sensitivity.
     * Valid are all characters in the Unicode character set with code point above 32,
     * although hubs may limit this further as they like with an appropriate error message.
     * When sent for hub, this is the nick that should be displayed before messages from the hub,
     * and may also be used as short name for the hub.
     */
    private String NI = "";

    /**
     * Description, string.
     * Valid are all characters in the Unicode character
     * set with code point equal to or greater than 32.
     * When sent by hub, this string should be displayed
     * in the window title of the hub window (if one exists)
     */
    private String DE;

    /**
     * Hubs where user is a normal user and in NORMAL state, integer.
     * While connecting, clients should not count the hub they're connecting to.
     * Hubs should increase one of the three the hub counts by one
     * before passing the client to NORMAL state.
     */
    private Integer HN;

    /**
     * Hubs where user is registered (had to supply password) and in NORMAL state, integer.
     */
    private Integer HR;

    /**
     * Hubs where user is op and in NORMAL state, integer.
     */
    private Integer HO;

    /**
     * Token, as received in RCM/CTM, when establishing a C-C connection.
     */
    private String TO;

    /**
     * Client (user) type, 1=bot, 2=registered user, 4=operator,
     * 8=super user, 16=hub owner, 32=hub (used when the hub sends an INF about itself).
     * Multiple types are specified by adding the numbers together.
     */
    private String CT = "0";

    /**
     * 1=Away
     * 2=Extended away, not interested in hub chat
     * (hubs may skip sending broadcast type MSG commands to clients with this flag)
     */
    private Integer AW;

    /**
     * 1=Bot (in particular, this means that the client does not support file transfers,
     * and thus should never be queried for direct connections)*/
    //private String BO;

    /**
     * 1=Hidden, should not be shown on the user list.
     */
    private boolean HI;

    /**
     * 1=Hub, this INF is about the hub itself
     */
    private boolean HU;

    /**
     * Comma-separated list of feature FOURCC's.
     * This notifies other clients of extended capabilities of the connecting client.
     * Use with discretion.
     */
    private String SU;

    /**
     * URL of referer (hub in case of redirect, web page)
     */
    private String RF;

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


    private String inQueueSearch = null;

    /**
     * Flag indicates about client was kicked
     */
    private boolean kicked = false;

    /**
     * Client session id.
     */
    private String SID;

    /**
     * indicates if client is a pinger a.k.a. PING extension
     */
    private boolean pingExtensionSupports;

    /**
     * indicates if client supports UCMD messages
     */
    private boolean ucmd = false;

    /**
     * indicates if client supports BASE messages
     */
    private int base = 0;

    /**
     * indicates if client supports old BAS0 messages
     */
    private boolean bas0;

    /**
     * if client supports TIGER hashes or not
     */
    private boolean tigrSupports;

    /**
     * Client Connect time in millis as Syste.gettimemillis() ; ;)
     */
    private long connectTime;

    /**
     * Client NIO session.
     */
    private IoSession session;

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//
	// Stored client params
	//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Client password
	 */
    private String  password;

	/**
	 *  Client registration flag
	 */
    private boolean isReg;
    private String  lastNick;
    private String  whoRegged;
    private Long    createdOn;
    private Long    lastLogin;
    private Long    timeOnline;
    private String lastIP;


    public String getLastMessageText()
    {
        return lastMessageText;
    }


    public void setLastMessageText(String lastMessageText)
    {
        this.lastMessageText = lastMessageText;
    }


    private String lastMessageText;

    private boolean hideShare;
    private boolean hideMe;

    private boolean overrideShare;
    private boolean overrideSpam;
    private boolean overrideFull;
    private boolean kickable = true;
	private boolean renameable;
    private boolean accountFlyable;
    private boolean opchatAccess;
    private boolean nickProtected;

	/**
	 *  Client rights weight, default 0
	 */
	private int		weight = 0;

    /**
     * Creates a new instance of ClientHandler
     */
    public ClientHandler()
    {
        connectTime = System.currentTimeMillis();
    }


    public void initStoreParamsFromDb()
	{

	}


    public void storePramsInDb()
	{

	}


    public String getClientINF()
	{
		return null;
	}


    public int getLoggedIn()
    {
        return loggedIn;
    }


    public void setLoggedIn(int loggedIn)
    {
        this.loggedIn = loggedIn;
    }


    /**
     * Is user fully authorized and validated
     */
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


    /**
     * Is client uses active (if true) or passive (if false) mode.
     */
    public boolean isActive()
    {
        return active;
    }


    public void setActive(boolean active)
    {
        this.active = active;
    }


    public boolean isCanReceiveCmds()
    {
        return canReceiveCmds;
    }


    public void setCanReceiveCmds(boolean canReceiveCmds)
    {
        this.canReceiveCmds = canReceiveCmds;
    }


    /**
     * Time when client was logged in.
     */
    public long getLoggedAt()
    {
        return loggedAt;
    }


    public void setLoggedAt(long loggedAt)
    {
        this.loggedAt = loggedAt;
    }


    /**
     * Salt for encryption algorithm.
     * Needed while session is live.
     */
    public String getEncryptionSalt()
    {
        return encryptionSalt;
    }


    public void setEncryptionSalt(String encryptionSalt)
    {
        this.encryptionSalt = encryptionSalt;
    }


    /**
     * Time when last MSG command was recieved.
     */
    public long getLastMSG()
    {
        return lastMSG;
    }


    public void setLastMSG(long lastMSG)
    {
        this.lastMSG = lastMSG;
    }


    /**
     * Time when last CTM command was recieved.
     */
    public long getLastCTM()
    {
        return lastCTM;
    }


    public void setLastCTM(long lastCTM)
    {
        this.lastCTM = lastCTM;
    }


    /**
     * Time when last INF command was recieved.
     */
    public long getLastINF()
    {
        return lastINF;
    }


    public void setLastINF(long lastINF)
    {
        this.lastINF = lastINF;
    }


    /**
     * Time when last keep alive packed recieved
     */
    public long getLastKeepAlive()
    {
        return lastKeepAlive;
    }


    public void setLastKeepAlive(long lastKeepAlive)
    {
        this.lastKeepAlive = lastKeepAlive;
    }


    /**
     * Client state
     */
    public int getState()
    {
        return state;
    }


    public void setState(int state)
    {
        this.state = state;
    }


    /**
     * Real client ip
     */
    public String getRealIP()
    {
        return realIP;
    }


    public void setRealIP(String realIP)
    {
        this.realIP = realIP;
    }


    /**
     * The CID of the client. Mandatory for C-C connections.
     */
    public String getID()
    {
        return ID;
    }


    public void setID(String ID)
    {
        this.ID = ID;
    }


    /**
     * The PID of the client.
     * Hubs must check that the Tiger(PID) == CID and then
     * discard the field before broadcasting it to other clients.
     * Must not be sent in C-C connections.
     */
    public String getPD()
    {
        return PD;
    }


    public void setPD(String PD)
    {
        this.PD = PD;
    }


    /**
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
    public String getI4()
    {
        return I4;
    }


    public void setI4(String i4)
    {
        I4 = i4;
    }


    /**
     * IPv6 address without port.
     * A zero address (::) means that the server should replace
     * it with the IP of the client.
     * Any client that supports incoming TCPv6 connections must also
     * add the feature TCP6 to their SU field.
     */
    public String getI6()
    {
        return I6;
    }


    public void setI6(String i6)
    {
        I6 = i6;
    }


    /**
     * Client UDP port.
     * Any client that supports incoming UDPv4 packets must also
     * add the feature UDP4 to their SU field.
     */
    public String getU4()
    {
        return U4;
    }


    public void setU4(String u4)
    {
        U4 = u4;
    }


    /**
     * Same as U4, but for IPv6.
     * Any client that supports incoming UDPv6 packets must also
     * add the feature UDP6 to their SU field.
     */
    public String getU6()
    {
        return U6;
    }


    public void setU6(String u6)
    {
        U6 = u6;
    }


    /**
     * Share size in bytes, integer.
     */
    public Long getSS()
    {
        return SS;
    }


    public void setSS(Long SS)
    {
        this.SS = SS;
    }


    /**
     * Number of shared files, integer
     */
    public Long getSF()
    {
        return SF;
    }


    public void setSF(Long SF)
    {
        this.SF = SF;
    }


    /**
     * Client identification,
     * version (client-specific, a short identifier
     * then a floating-point version number is recommended).
     * Hubs should not discriminate agains clients based on
     * their VE tag but instead rely on SUP when it comes to
     * which clients should be allowed (for example, we only want regex clients).
     */
    public String getVE()
    {
        return VE;
    }


    public void setVE(String VE)
    {
        this.VE = VE;
    }


    /**
     * Maximum upload speed, bits/sec, integer
     */
    public Long getUS()
    {
        return US;
    }


    public void setUS(Long US)
    {
        this.US = US;
    }


    /**
     * Maximum download speed, bits/sec, integer
     */
    public Long getDS()
    {
        return DS;
    }


    public void setDS(Long DS)
    {
        this.DS = DS;
    }


    /**
     * Upload slots open, integer
     */
    public Integer getSL()
    {
        return SL;
    }


    public void setSL(Integer SL)
    {
        this.SL = SL;
    }


    /**
     * Automatic slot allocator speed limit, bytes/sec, integer.
     * This is the recommended method of slot allocation,
     * the client keeps opening slots as long as its total upload speed
     * doesn't exceed this value. SL then serves as a minimum number of slots open.
     */
    public Long getAS()
    {
        return AS;
    }


    public void setAS(Long AS)
    {
        this.AS = AS;
    }


    /**
     * Maximum number of slots open in automatic slot manager mode, integer.
     */
    public Long getAM()
    {
        return AM;
    }


    public void setAM(Long AM)
    {
        this.AM = AM;
    }


    /**
     * E-mail address, string.
     */
    public String getEM()
    {
        return EM;
    }


    public void setEM(String EM)
    {
        this.EM = EM;
    }


    /**
     * Nickname, string.
     * The hub must ensure that this is unique in the hub up to case-sensitivity.
     * Valid are all characters in the Unicode character set with code point above 32,
     * although hubs may limit this further as they like with an appropriate error message.
     * When sent for hub, this is the nick that should be displayed before messages from the hub,
     * and may also be used as short name for the hub.
     */
    public String getNI()
    {
        return NI;
    }


    public void setNI(String NI)
    {
        this.NI = NI;
    }


    /**
     * Description, string.
     * Valid are all characters in the Unicode character
     * set with code point equal to or greater than 32.
     * When sent by hub, this string should be displayed
     * in the window title of the hub window (if one exists)
     */
    public String getDE()
    {
        return DE;
    }


    public void setDE(String DE)
    {
        this.DE = DE;
    }


    /**
     * Hubs where user is a normal user and in NORMAL state, integer.
     * While connecting, clients should not count the hub they're connecting to.
     * Hubs should increase one of the three the hub counts by one
     * before passing the client to NORMAL state.
     */
    public Integer getHN()
    {
        return HN;
    }


    public void setHN(Integer HN)
    {
        this.HN = HN;
    }


    /**
     * Hubs where user is registered (had to supply password) and in NORMAL state, integer.
     */
    public Integer getHR()
    {
        return HR;
    }


    public void setHR(Integer HR)
    {
        this.HR = HR;
    }


    /**
     * Hubs where user is op and in NORMAL state, integer.
     */
    public Integer getHO()
    {
        return HO;
    }


    public void setHO(Integer HO)
    {
        this.HO = HO;
    }


    /**
     * Token, as received in RCM/CTM, when establishing a C-C connection.
     */
    public String getTO()
    {
        return TO;
    }


    public void setTO(String TO)
    {
        this.TO = TO;
    }


    /**
     * Client (user) type, 1=bot, 2=registered user, 4=operator,
     * 8=super user, 16=hub owner, 32=hub (used when the hub sends an INF about itself).
     * Multiple types are specified by adding the numbers together.
     */
    public String getCT()
    {
        return CT;
    }


    public void setCT(String CT)
    {
        this.CT = CT;
    }


    /**
     * 1=Away
     * 2=Extended away, not interested in hub chat
     * (hubs may skip sending broadcast type MSG commands to clients with this flag)
     */
    public Integer getAW()
    {
        return AW;
    }


    public void setAW(Integer AW)
    {
        this.AW = AW;
    }


    /**
     * 1=Hidden, should not be shown on the user list.
     */
    public boolean isHI()
    {
        return HI;
    }


    public void setHI(boolean HI)
    {
        this.HI = HI;
    }


    /**
     * 1=Hub, this INF is about the hub itself
     */
    public boolean isHU()
    {
        return HU;
    }


    public void setHU(boolean HU)
    {
        this.HU = HU;
    }


    /**
     * Comma-separated list of feature FOURCC's.
     * This notifies other clients of extended capabilities of the connecting client.
     * Use with discretion.
     */
    public String getSU()
    {
        return SU;
    }


    public void setSU(String SU)
    {
        this.SU = SU;
    }


    /**
     * URL of referer (hub in case of redirect, web page)
     */
    public String getRF()
    {
        return RF;
    }


    public void setRF(String RF)
    {
        this.RF = RF;
    }


    /**
     * Client search step
     */
    public int getSearchStep()
    {
        return searchStep;
    }


    public void setSearchStep(int searchStep)
    {
        this.searchStep = searchStep;
    }


    /**
     * Time when client runs last search
     */
    public long getLastSearch()
    {
        return lastSearch;
    }


    public void setLastSearch(long lastSearch)
    {
        this.lastSearch = lastSearch;
    }


    /**
     * Time when client do last automagic search
     */
    public long getLastAutomagicSearch()
    {
        return lastAutomagicSearch;
    }


    public void setLastAutomagicSearch(long lastAutomagicSearch)
    {
        this.lastAutomagicSearch = lastAutomagicSearch;
    }


    public String getInQueueSearch()
    {
        return inQueueSearch;
    }


    public void setInQueueSearch(String inQueueSearch)
    {
        this.inQueueSearch = inQueueSearch;
    }


    /**
     * Flag indicates about client was kicked
     */
    public boolean isKicked()
    {
        return kicked;
    }


    public void setKicked()
    {
        this.kicked = true;
    }


    public void setKicked(boolean kicked)
    {
        this.kicked = kicked;
    }


    /**
     * Client session id.
     */
    public String getSID()
    {
        return SID;
    }


    public void setSID(String SID)
    {
        this.SID = SID;
    }


    /**
     * indicates if client is a pinger a.k.a. PING extension
     */
    public boolean isPingExtensionSupports()
    {
        return pingExtensionSupports;
    }


    public void setPingExtensionSupports(boolean pingExtensionSupports)
    {
        this.pingExtensionSupports = pingExtensionSupports;
    }


    /**
     * indicates if client supports UCMD messages
     */
    public boolean isUcmd()
    {
        return ucmd;
    }


    public void setUcmd(boolean ucmd)
    {
        this.ucmd = ucmd;
    }


    /**
     * indicates if client supports BASE messages
     */
    public int getBase()
    {
        return base;
    }


    public void setBase(int base)
    {
        this.base = base;
    }


    /**
     * indicates if client supports old BAS0 messages
     */
    public boolean isBas0()
    {
        return bas0;
    }


    public void setBas0(boolean bas0)
    {
        this.bas0 = bas0;
    }


    /**
     * if client supports TIGER hashes or not
     */
    public boolean isTigrSupports()
    {
        return tigrSupports;
    }


    public void setTigrSupports(boolean tigrSupports)
    {
        this.tigrSupports = tigrSupports;
    }


    /**
     * Client Connect time in millis as Syste.gettimemillis() ; ;)
     */
    public long getConnectTime()
    {
        return connectTime;
    }


    public void setConnectTime(long connectTime)
    {
        this.connectTime = connectTime;
    }


    /**
     * Client NIO session.
     */
    public IoSession getSession()
    {
        return session;
    }


    public void setSession(IoSession session)
    {
        this.session = session;
    }


    public String getPassword()
    {
        return password;
    }


    public void setPassword(String password)
    {
        this.password = password;
    }


    public boolean isReg()
    {
        return isReg;
    }


    public void setReg(boolean reg)
    {
        isReg = reg;
    }


    public String getLastNick()
    {
        return lastNick;
    }


    public void setLastNick(String lastNick)
    {
        this.lastNick = lastNick;
    }


    public String getWhoRegged()
    {
        return whoRegged;
    }


    public void setWhoRegged(String whoRegged)
    {
        this.whoRegged = whoRegged;
    }


    public Long getCreatedOn()
    {
        return createdOn;
    }


    public void setCreatedOn(Long createdOn)
    {
        this.createdOn = createdOn;
    }


    public Long getLastLogin()
    {
        return lastLogin;
    }


    public void setLastLogin(Long lastLogin)
    {
        this.lastLogin = lastLogin;
    }


    public Long getTimeOnline()
    {
        return timeOnline;
    }


    public void setTimeOnline(Long timeOnline)
    {
        this.timeOnline = timeOnline;
    }


    /**
     * Increases timeOnline by timeOnlineDelta value.
     * @param timeOnlineDelta time online delta value
     */
    public void increaseTimeOnline(Long timeOnlineDelta)
    {
        this.timeOnline += timeOnlineDelta;
    }

    public String getLastIP()
    {
        return lastIP;
    }


    public void setLastIP(String lastIP)
    {
        this.lastIP = lastIP;
    }


    public boolean isHideShare()
    {
        return hideShare;
    }


    public void setHideShare(boolean hideShare)
    {
        this.hideShare = hideShare;
    }


    public boolean isHideMe()
    {
        return hideMe;
    }


    public void setHideMe(boolean hideMe)
    {
        this.hideMe = hideMe;
    }


    public boolean isOverrideShare()
    {
        return overrideShare;
    }


    public void setOverrideShare(boolean overrideShare)
    {
        this.overrideShare = overrideShare;
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


    public boolean isAccountFlyable()
    {
        return accountFlyable;
    }


    public void setAccountFlyable(boolean accountFlyable)
    {
        this.accountFlyable = accountFlyable;
    }


    public boolean isOpchatAccess()
    {
        return opchatAccess;
    }


    public void setOpchatAccess(boolean opchatAccess)
    {
        this.opchatAccess = opchatAccess;
    }


    public boolean isNickProtected()
    {
        return nickProtected;
    }


    public void setNickProtected(boolean nickProtected)
    {
        this.nickProtected = nickProtected;
    }


    public int getWeight()
    {
        return weight;
    }


    public void setWeight(int weight)
    {
        this.weight = weight;
    }


    /**
     * sends the message String in RAW to client.
     * adds the \n ending char ;)
     * @param message Message string to client
     * @return WriteFuture indicates when message was really sent
     */
    public WriteFuture sendToClient(String message)
    {
        // TODO Add queueing outgoing messages
        //this.Queue.addMsg (bla);
        return session.write(message);
    }


    public String getINF()
    {
        String auxstr = "";
        auxstr = auxstr + "BINF " + SID + " ID" + ID + " NI" + NI;
        //these were mandatory fields.. now adding the extra...
        if (I4 != null)
        {
            if (!I4.equals(""))
            {
                auxstr = auxstr + " I4" + I4;
            }
        }
        if (AM != null)
        {
                auxstr = auxstr + " AM" + AM;
        }
        if (AS != null)
        {
                auxstr = auxstr + " AS" + AS;
        }
        if (AW != null)
        {
                auxstr = auxstr + " AW" + AW;
        }
        if (DE != null)
        {
            if (!DE.equals(""))
            {
                auxstr = auxstr + " DE" + DE;
            }
        }
        if (DS != null)
        {
                auxstr = auxstr + " DS" + DS;
        }
        if (EM != null)
        {
            if (!EM.equals(""))
            {
                auxstr = auxstr + " EM" + EM;
            }
        }
        if (HI != false)
        {
            // TODO should change.. only for ops :)
            auxstr = auxstr + " HI1";
        }
        if (HN != null)
        {
                auxstr = auxstr + " HN" + HN;
        }
        if (HO != null)
        {
                auxstr = auxstr + " HO" + HO;
        }
        if (HR != null)
        {
                auxstr = auxstr + " HR" + HR;
        }
        if (HU != false)
        {
            auxstr = auxstr + " HU1";
        }
        if (CT != null)
        {
            if (!CT.equals(""))
            {
                if (!CT.equals("0")) // TODO should change.. more working here
                {
                    auxstr = auxstr + " CT" + CT;
                }
            }
        }
        if (SF != null)
        {
                auxstr = auxstr + " SF" + SF;
        }
        if (SS != null)
        {
                auxstr = auxstr + " SS" + SS;
        }
        if (SL != null)
        {
                auxstr = auxstr + " SL" + SL;
        }
        if (SU != null)
        {
            if (!SU.equals(""))
            {
                auxstr = auxstr + " SU" + SU;
            }
        }
        if (TO != null)
        {
            if (!TO.equals(""))
            {
                auxstr = auxstr + " TO" + TO;
            }
        }
        if (U4 != null)
        {
            if (!U4.equals(""))
            {
                auxstr = auxstr + " U4" + U4;
            }
        }
        if (U6 != null)
        {
            if (!U6.equals(""))
            {
                auxstr = auxstr + " U6" + U6;
            }
        }
        if (VE != null)
        {
            if (!VE.equals(""))
            {
                auxstr = auxstr + " VE" + VE;
            }
        }
        if (US != null)
        {
            if (US != 0)
            {
                auxstr = auxstr + " US" + US;
            }
        }

        return auxstr;
    }


    public void sendFromBot(String text)
    {
        if (text.isEmpty())
        {
            return;
        }
        if (isValidated())
        {
            if (canReceiveCmds && ConfigurationManager.instance().getBoolean(ConfigurationManager.COMMAND_PM_RETURN))
            {
                sendFromBotPM(text);
            }
            else
            {
                this.sendToClient("EMSG DCBA " + this.SID + " " + AdcUtils.retADCStr(text));
            }
        }
    }


    public void sendFromBotPM(String text)
    {
        if (isValidated())
        {
            this.sendToClient("EMSG DCBA " +
                              this.SID +
                              " " +
                              AdcUtils.retADCStr(text) +
                              " PMDCBA");
        }
    }


    public void putOpchat(boolean x)
    {
        if (x)
        {
            if (this.isReg && this.opchatAccess)
            {
                this.sendToClient("BINF ABCD ID" +
                                  ConfigurationManager.instance().getString(ConfigurationManager.OP_CHAT_CID) +
                                  " NI" +
                                  AdcUtils.retADCStr(ConfigurationManager.instance().getString(ConfigurationManager.OP_CHAT_NAME)) +
                                  " CT5 DE" +
                                  AdcUtils.retADCStr(ConfigurationManager.instance().getString(ConfigurationManager.OP_CHAT_DESCRIPTION)));
            }
        }
        else
        {
            if (this.isReg && this.opchatAccess)
            {
                this.sendToClient("IQUI ABCD");
            }
        }
    }


}
