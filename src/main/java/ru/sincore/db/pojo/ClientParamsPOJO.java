package ru.sincore.db.pojo;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "client_params")
public class ClientParamsPOJO
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id",columnDefinition = "INTEGER", length = 11)
	private Long 		id;
	private String 		cid;
	private String 		password;
	private Boolean 	key;
	private Boolean 	isReg;
	private String		lastNick;
	private String		regOwner;
	private Date 		regDate;
	private Date 		lastLogIn;
	private Long		timeOnline;
	private String 		lastIp;
	private Boolean		hideShare;
	private Boolean		hideMe;
	private Boolean		overrideShare;
	private Boolean		overrideSpam;
	private Boolean		overrideFull;
	private Boolean		kickable;
	private Boolean		renameable;
	private Boolean		accountFlyable;
	private Boolean		opchatAccess;
}
