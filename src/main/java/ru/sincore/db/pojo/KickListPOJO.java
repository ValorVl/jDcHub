package ru.sincore.db.pojo;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "kick_list")
public class KickListPOJO
{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long 		id;

	@Column(name = "nick_name", length = 250)
	private String 		nickName;

	@Column(name = "kick_owner", length = 250)
	private String 		kickOwner;

	@Column(name = "kick_date")
	private Date		kickDate;

	@Column(name = "kick_expired_date")
	private Date		kickExpiredDate;

	@Column(name = "ip", length = 200)
	private String 		ip;

	@Column(name = "reason" ,columnDefinition = "TEXT")
	private String 		reason;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getNickName()
	{
		return nickName;
	}

	public void setNickName(String nickName)
	{
		this.nickName = nickName;
	}

	public String getKickOwner()
	{
		return kickOwner;
	}

	public void setKickOwner(String kickOwner)
	{
		this.kickOwner = kickOwner;
	}

	public Date getKickDate()
	{
		return kickDate;
	}

	public void setKickDate(Date kickDate)
	{
		this.kickDate = kickDate;
	}

	public Date getKickExpiredDate()
	{
		return kickExpiredDate;
	}

	public void setKickExpiredDate(Date kickExpiredDate)
	{
		this.kickExpiredDate = kickExpiredDate;
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}
}
