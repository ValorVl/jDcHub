package ru.sincore.db.pojo;

import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *  JPA POJO Class mapping HubUserList
 *
 *  @author Valor
 *  @since 12.09.2011
 *  @version 0.0.1
 */
@Entity
@Table(name = "client_list")
public class ClientListPOJO implements Serializable
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long 		id;

	/**
	 *  Column "cid" generated client id
	 */
	@Column(name = "cid", columnDefinition = "VARCHAR(200)",nullable = false)
	private String 		cid;

	/**
	 *  Count bytes of share size
	 */
	@Column(name = "share_size",nullable = false)
	private Long 		shareSize = 0L;
	/**
	 *   Count shared files
	 */
	@Column(name = "share_file_count",nullable = false)
	private Long 		sharedFilesCount = 0L;

	/**
	 *  Column "weight" right granted mask
	 */
	@Column(name = "weight", columnDefinition = "TINYINT(3) DEFAULT 0",nullable = false)
	private Integer		weight = 0;

	/**
	 *  Column "password" encrypted password string
	 */
	@Column(name = "password",columnDefinition = "VARCHAR(250)",nullable = true)
	private String 		password = "";

	/**
	 *  Column "key" boolean flag. Indicates that the user can authenticate using SSL key
	 */
	@Column(name = "key_auth_allowed", columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean 	keyAuthAllowed = false;

	/**
	 *  Column "isReg" boolean flag. Indicates whether the user is registered (or rather his nickname) on the hub or not,
	 *  if not then respectively the nickname can be registered by another user hub. Details of the behavior of this
	 *  parameter are configured separately.
	 */
	@Column(name = "is_reg",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean registred = false;
	/**
	 *  Column "nickName" String userid hub.
	 */
	@Index(name = "index_by_nick_name")
	@Column(name = "nick_name",columnDefinition = "VARCHAR(200)",nullable = false,unique = true)
	private String 		nickName;

	/**
	 *  Column "lastNick" Contains the last nickname until it changes or fixation thereof by another user
	 */
	@Column(name = "last_nick",columnDefinition = "VARCHAR(200)",nullable = true)
	private String		lastNick;

	/**
	 *  Column "regOwner" Contains the OP nickname of registered currant user. Passable DEFAULT value e.g SERVER
	 *  if user registered automatically.
	 */
	@Column(name = "reg_owner",columnDefinition = "VARCHAR(200)",nullable = true)
	private String		regOwner;

	/**
	 *  Column "regDate" Contains user registration datetime.
	 */
	@Column(name = "reg_date",columnDefinition = "DATETIME",nullable = true)
	private Date 		regDate;

	/**
	 *  Column "lastLogin" Contains last user login date. Must be empty.
	 */
	@Column(name = "last_login",columnDefinition = "DATETIME",nullable = true)
	private Date 		lastLogIn;

    /**
     *  Column "maximumTimeOnline" Contains the sum of all user's sessions in seconds (millisecond * 1000)
     */
    @Column(name = "time_online",columnDefinition = " BIGINT DEFAULT 0",nullable = false)
    private Long	timeOnline = 0L;

	/**
	 *  Column "maximumTimeOnline" Contains the maximum length of user session in seconds (millisecond * 1000)
	 */
	@Column(name = "maximum_time_online",columnDefinition = " BIGINT DEFAULT 0",nullable = false)
	private Long		maximumTimeOnline = 0L;

	/**
	 *  Colimn "currentIp" Contains curant IP address (IPv4 or IPv6)
	 */
	@Column(name = "current_ip",columnDefinition = "VARCHAR(250)",nullable = true)
	private String 		currentIp = null;

	/**
	 *  Column "realIp" Contains a "white" IP gateway, if there is. Defined by the client through NPnP
	 *  or another similar service. Passable NULL.
	 */
	@Column(name = "real_ip",columnDefinition = "VARCHAR(250)",nullable = true)
	private String 		realIp = null;

	/**
	 *  Column "hideShare" boolean flag. Hidden if a user share.
	 */
	@Column(name = "hide_share",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		hideShare = false;

	/**
	 *  Column "hideMe"  boolean flag, mar hub user is hiden
	 */
	@Column(name = "hide_me",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		hideMe = false;

	/**
	 *  Column "overrideShare" boolean flag. User miss the check share size.
	 */
	@Column(name = "override_share",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		overrideShare = false;

	/**
	 *  Column "overrideSpam" boolean flag. User miss the spam protect.
	 */
	@Column(name = "override_spam",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		overrideSpam = false;

	/**
	 *  Column "overrideFull" boolean flag. User miss the check hab full, must be addeding in reserved connection slot.
	 */
	@Column(name = "override_full",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		overrideFull = false;

	/**
	 *  Column "kickable" boolean flag market the user as non kikable
	 */
	@Column(name = "is_kickable",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		isKickable = false;

	/**
	 *  Column "renameable" boolean. Flag to prevent a user allows to change nickname
	 */
	@Column(name = "renameable",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		renameable = false;

	/**
	 *  Column "txBytes" Contains transitional byte count
	 */
	@Column(name = "tx_bytes",columnDefinition = "BIGINT DEFAULT 0")
	private Long 		txBytes = 0L;

	/**
	 *  Column "rxBytes" Contains resive byte count
	 */
	@Column(name = "rx_bytes",columnDefinition = "BIGINT DEFAULT 0")
	private Long		rxBytes = 0L;

	/**
	 *  Column "isPing" indicates if client is a pinger a.k.a. PING extension
	 */
	@Column(name = "is_ping",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		ping = false;

	/**
	 *  Column "tigerAllowed" if client supports TIGER hashes or not
	 */
	@Column(name = "tiger_allowed",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		tigerAllowed = false;

	/**
	 *  Column "ucmdAllowed" indicates if client supports UCMD messages
	 */
	@Column(name = "ucmd_allowed",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		ucmdAllowed = false;

	/**
	 *  Column "baseAllowed" indicates if client supports BASE messages
	 */
	@Column(name = "base_allowed",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		baseAllowed = false;

	/**
	 *  Column "bas0Allowed" indicates if client supports old BAS0 messages
	 */
	@Column(name = "bas0_allowed",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		bas0Allowed = false;

	/**
	 *  Column "lastMessage" last message said by user.
	 */
	@Column(name = "last_message",columnDefinition = "TEXT",nullable = true)
	private String 		lastMessage;

	/**
	 *  Column "loginCount" Contains count user loggined actions
	 */
	@Column(name = "login_count", columnDefinition = "INTEGER DEFAULT 0")
	private Long		loginCount = 0L;

	@Column(name = "locale",length = 5,nullable = true)
	private String locale;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getCid()
	{
		return cid;
	}

	public void setCid(String cid)
	{
		this.cid = cid;
	}

	public Integer getWeight()
	{
		return weight;
	}

	public void setWeight(Integer weight)
	{
		this.weight = weight;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public Boolean getKeyAuthAllowed()
	{
		return keyAuthAllowed;
	}

	public void setKeyAuthAllowed(Boolean keyAuthAllowed)
	{
		this.keyAuthAllowed = keyAuthAllowed;
	}

	public Boolean isRegistred()
	{
		return registred;
	}

	public void setRegistred(Boolean reg)
	{
		registred = reg;
	}

	public String getNickName()
	{
		return nickName;
	}

	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

	public String getLastNick()
	{
		return lastNick;
	}

	public void setLastNick(String lastNick)
	{
		this.lastNick = lastNick;
	}

	public String getRegOwner()
	{
		return regOwner;
	}

	public void setRegOwner(String regOwner)
	{
		this.regOwner = regOwner;
	}

	public Date getRegDate()
	{
		return regDate;
	}

	public void setRegDate(Date regDate)
	{
		this.regDate = regDate;
	}

	public Date getLastLogIn()
	{
		return lastLogIn;
	}

	public void setLastLogIn(Date lastLogIn)
	{
		this.lastLogIn = lastLogIn;
	}

	public String getCurrentIp()
	{
		return currentIp;
	}

	public void setCurrentIp(String currentIp)
	{
		this.currentIp = currentIp;
	}

	public String getRealIp()
	{
		return realIp;
	}

	public void setRealIp(String realIp)
	{
		this.realIp = realIp;
	}

	public Boolean getHideShare()
	{
		return hideShare;
	}

	public void setHideShare(Boolean hideShare)
	{
		this.hideShare = hideShare;
	}

	public Boolean getHideMe()
	{
		return hideMe;
	}

	public void setHideMe(Boolean hideMe)
	{
		this.hideMe = hideMe;
	}

	public Boolean getOverrideShare()
	{
		return overrideShare;
	}

	public void setOverrideShare(Boolean overrideShare)
	{
		this.overrideShare = overrideShare;
	}

	public Boolean getOverrideSpam()
	{
		return overrideSpam;
	}

	public void setOverrideSpam(Boolean overrideSpam)
	{
		this.overrideSpam = overrideSpam;
	}

	public Boolean getOverrideFull()
	{
		return overrideFull;
	}

	public void setOverrideFull(Boolean overrideFull)
	{
		this.overrideFull = overrideFull;
	}

	public Boolean getKickable()
	{
		return isKickable;
	}

	public void setKickable(Boolean isKickable)
	{
		this.isKickable = isKickable;
	}

    public Boolean getRenameable()
    {
        return renameable;
    }

    public void setRenameable(Boolean renameable)
    {
        this.renameable = renameable;
    }

    public Long getTxBytes()
	{
		return txBytes;
	}

	public void setTxBytes(Long txBytes)
	{
		this.txBytes = txBytes;
	}

	public Long getRxBytes()
	{
		return rxBytes;
	}

	public void setRxBytes(Long rxBytes)
	{
		this.rxBytes = rxBytes;
	}

	public Boolean getPing()
	{
		return ping;
	}

	public void setPing(Boolean ping)
	{
		this.ping = ping;
	}

	public Boolean getTigerAllowed()
	{
		return tigerAllowed;
	}

	public void setTigerAllowed(Boolean tigerAllowed)
	{
		this.tigerAllowed = tigerAllowed;
	}

	public Boolean getUcmdAllowed()
	{
		return ucmdAllowed;
	}

	public void setUcmdAllowed(Boolean ucmdAllowed)
	{
		this.ucmdAllowed = ucmdAllowed;
	}

	public Boolean getBaseAllowed()
	{
		return baseAllowed;
	}

	public void setBaseAllowed(Boolean baseAllowed)
	{
		this.baseAllowed = baseAllowed;
	}

	public Boolean getBas0Allowed()
	{
		return bas0Allowed;
	}

	public void setBas0Allowed(Boolean bas0Allowed)
	{
		this.bas0Allowed = bas0Allowed;
	}

	public String getLastMessage()
	{
		return lastMessage;
	}

	public void setLastMessage(String lastMessage)
	{
		this.lastMessage = lastMessage;
	}

	public Long getLoginCount()
	{
		return loginCount;
	}

	public void setLoginCount(Long loginCount)
	{
		this.loginCount = loginCount;
	}

    public Long getTimeOnline()
    {
        return timeOnline;
    }

    public void setTimeOnline(Long timeOnline)
    {
        this.timeOnline = timeOnline;
    }

    public Long getMaximumTimeOnline()
	{
		return maximumTimeOnline;
	}

	public void setMaximumTimeOnline(Long maximumTimeOnline)
	{
		this.maximumTimeOnline = maximumTimeOnline;
	}

	public String getLocale()
	{
		return locale;
	}

	public void setLocale(String locale)
	{
		this.locale = locale;
	}

	public Long getShareSize()
	{
		return shareSize;
	}

	public void setShareSize(Long shareSize)
	{
		this.shareSize = shareSize;
	}

	public Long getSharedFilesCount()
	{
		return sharedFilesCount;
	}

	public void setSharedFilesCount(Long sharedFilesCount)
	{
		this.sharedFilesCount = sharedFilesCount;
	}
}
