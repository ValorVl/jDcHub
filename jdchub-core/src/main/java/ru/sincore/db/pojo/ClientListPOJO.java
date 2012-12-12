package ru.sincore.db.pojo;

import lombok.Getter;
import lombok.Setter;
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
    @Getter
    @Setter
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long 		id;

	/**
	 *  Column "cid" generated client id
	 */
    @Getter
    @Setter
    @Column(name = "cid", columnDefinition = "VARCHAR(200)",nullable = false)
	private String 		cid;

	/**
	 *  Count bytes of share size
	 */
    @Getter
    @Setter
    @Column(name = "share_size",nullable = false)
	private Long 		shareSize = 0L;

	/**
	 *   Count shared files
	 */
    @Getter
    @Setter
    @Column(name = "share_file_count",nullable = false)
	private Long 		sharedFilesCount = 0L;

	/**
	 *  Column "weight" right granted mask
	 */
    @Getter
    @Setter
    @Column(name = "weight", columnDefinition = "TINYINT(3) DEFAULT 0",nullable = false)
	private Integer		weight = 0;

	/**
	 *  Column "password" encrypted password string
	 */
    @Getter
    @Setter
    @Column(name = "password",columnDefinition = "VARCHAR(250)",nullable = true)
	private String 		password = "";

	/**
	 *  Column "key" boolean flag. Indicates that the user can authenticate using SSL key
	 */
    @Getter
    @Setter
    @Column(name = "key_auth_allowed", columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean 	keyAuthAllowed = false;

	/**
	 *  Column "isReg" boolean flag. Indicates whether the user is registered (or rather his nickname) on the hub or not,
	 *  if not then respectively the nickname can be registered by another user hub. Details of the behavior of this
	 *  parameter are configured separately.
	 */
    @Getter
    @Setter
    @Column(name = "is_reg",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean registred = false;

	/**
	 *  Column "nickName" String userid hub.
	 */
    @Getter
    @Setter
    @Index(name = "index_by_nick_name")
	@Column(name = "nick_name",columnDefinition = "VARCHAR(200)",nullable = false,unique = true)
	private String 		nickName;

	/**
	 *  Column "lastNick" Contains the last nickname until it changes or fixation thereof by another user
	 */
    @Getter
    @Setter
    @Column(name = "last_nick",columnDefinition = "VARCHAR(200)",nullable = true)
	private String		lastNick;

	/**
	 *  Column "regOwner" Contains the OP nickname of registered currant user. Passable DEFAULT value e.g SERVER
	 *  if user registered automatically.
	 */
    @Getter
    @Setter
    @Column(name = "reg_owner",columnDefinition = "VARCHAR(200)",nullable = true)
	private String		regOwner;

	/**
	 *  Column "regDate" Contains user registration datetime.
	 */
    @Getter
    @Setter
    @Column(name = "reg_date",columnDefinition = "DATETIME",nullable = true)
	private Date 		regDate;

	/**
	 *  Column "lastLogin" Contains last user login date. Must be empty.
	 */
    @Getter
    @Setter
    @Column(name = "last_login",columnDefinition = "DATETIME",nullable = true)
	private Date 		lastLogIn;

    /**
     *  Column "maximumTimeOnline" Contains the sum of all user's sessions in seconds (millisecond * 1000)
     */
    @Getter
    @Setter
    @Column(name = "time_online",columnDefinition = " BIGINT DEFAULT 0",nullable = false)
    private Long	timeOnline = 0L;

	/**
	 *  Column "maximumTimeOnline" Contains the maximum length of user session in seconds (millisecond * 1000)
	 */
    @Getter
    @Setter
    @Column(name = "maximum_time_online",columnDefinition = " BIGINT DEFAULT 0",nullable = false)
	private Long		maximumTimeOnline = 0L;

	/**
	 *  Colimn "currentIp" Contains curant IP address (IPv4 or IPv6)
	 */
    @Getter
    @Setter
    @Column(name = "current_ip",columnDefinition = "VARCHAR(250)",nullable = true)
	private String 		currentIp = null;

	/**
	 *  Column "realIp" Contains a "white" IP gateway, if there is. Defined by the client through NPnP
	 *  or another similar service. Passable NULL.
	 */
    @Getter
    @Setter
    @Column(name = "real_ip",columnDefinition = "VARCHAR(250)",nullable = true)
	private String 		realIp = null;

	/**
	 *  Column "hideShare" boolean flag. Hidden if a user share.
	 */
    @Getter
    @Setter
    @Column(name = "hide_share",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		hideShare = false;

	/**
	 *  Column "hideMe"  boolean flag, mar hub user is hiden
	 */
    @Getter
    @Setter
    @Column(name = "hide_me",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		hideMe = false;

	/**
	 *  Column "overrideShare" boolean flag. User miss the check share size.
	 */
    @Getter
    @Setter
    @Column(name = "override_share",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		overrideShare = false;

	/**
	 *  Column "overrideSpam" boolean flag. User miss the spam protect.
	 */
    @Getter
    @Setter
    @Column(name = "override_spam",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		overrideSpam = false;

	/**
	 *  Column "overrideFull" boolean flag. User miss the check hab full, must be addeding in reserved connection slot.
	 */
    @Getter
    @Setter
    @Column(name = "override_full",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		overrideFull = false;

	/**
	 *  Column "kickable" boolean flag market the user as non kikable
	 */
    @Getter
    @Setter
    @Column(name = "is_kickable",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		isKickable = false;

	/**
	 *  Column "renameable" boolean. Flag to prevent a user allows to change nickname
	 */
    @Getter
    @Setter
    @Column(name = "renameable",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		renameable = false;

	/**
	 *  Column "txBytes" Contains transitional byte count
	 */
    @Getter
    @Setter
    @Column(name = "tx_bytes",columnDefinition = "BIGINT DEFAULT 0")
	private Long 		txBytes = 0L;

	/**
	 *  Column "rxBytes" Contains resive byte count
	 */
    @Getter
    @Setter
    @Column(name = "rx_bytes",columnDefinition = "BIGINT DEFAULT 0")
	private Long		rxBytes = 0L;

	/**
	 *  Column "isPing" indicates if client is a pinger a.k.a. PING extension
	 */
    @Getter
    @Setter
    @Column(name = "is_ping",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		ping = false;

	/**
	 *  Column "tigerAllowed" if client supports TIGER hashes or not
	 */
    @Getter
    @Setter
    @Column(name = "tiger_allowed",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		tigerAllowed = false;

	/**
	 *  Column "ucmdAllowed" indicates if client supports UCMD messages
	 */
    @Getter
    @Setter
    @Column(name = "ucmd_allowed",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		ucmdAllowed = false;

	/**
	 *  Column "baseAllowed" indicates if client supports BASE messages
	 */
    @Getter
    @Setter
    @Column(name = "base_allowed",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		baseAllowed = false;

	/**
	 *  Column "bas0Allowed" indicates if client supports old BAS0 messages
	 */
    @Getter
    @Setter
    @Column(name = "bas0_allowed",columnDefinition = "TINYINT(1) DEFAULT 0",nullable = false)
	private Boolean		bas0Allowed = false;

	/**
	 *  Column "lastMessage" last message said by user.
	 */
    @Getter
    @Setter
    @Column(name = "last_message",columnDefinition = "TEXT",nullable = true)
	private String 		lastMessage;

	/**
	 *  Column "loginCount" Contains count user loggined actions
	 */
    @Getter
    @Setter
    @Column(name = "login_count", columnDefinition = "INTEGER DEFAULT 0")
	private Long		loginCount = 0L;

    @Getter
    @Setter
    @Column(name = "locale",length = 5,nullable = true)
	private String locale;
}
