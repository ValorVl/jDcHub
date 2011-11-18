package ru.sincore.db.pojo;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "ban_list")
public class BanListPOJO
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id", columnDefinition = "INTEGER", length = 11)
	private Long id;

	@Column(name = "ip", columnDefinition = "TEXT")
	private String ip;

	@Column(name = "nick", length = 150)
	private String nick;

	@Column(name = "ban_type", columnDefinition = "TINYINT")
	private Integer banType;

	@Column(name = "host_name", length = 150)
	private String hostName;

	@Column(name = "date_start")
	private Date dateStart;

	@Column(name = "date_stop")
	private Date dateStop;

	@Column(name = "op_nick")
	private String opNick;

	@Column(name = "reason", columnDefinition = "TEXT")
	private String reason;

	@Column(name = "share_size",columnDefinition = "BIGINT")
	private Long shareSize;

	@Column(name = "email",length = 200,nullable = true)
	private String email;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getIp()
	{
		return ip;
	}

	public void setIp(String ip)
	{
		this.ip = ip;
	}

	public String getNick()
	{
		return nick;
	}

	public void setNick(String nick)
	{
		this.nick = nick;
	}

	public Integer getBanType()
	{
		return banType;
	}

	public void setBanType(Integer banType)
	{
		this.banType = banType;
	}

	public String getHostName()
	{
		return hostName;
	}

	public void setHostName(String hostName)
	{
		this.hostName = hostName;
	}

	public Date getDateStart()
	{
		return dateStart;
	}

	public void setDateStart(Date dateStart)
	{
		this.dateStart = dateStart;
	}

	public Date getDateStop()
	{
		return dateStop;
	}

	public void setDateStop(Date fateStop)
	{
		this.dateStop = fateStop;
	}

	public String getOpNick()
	{
		return opNick;
	}

	public void setOpNick(String opNick)
	{
		this.opNick = opNick;
	}

	public String getReason()
	{
		return reason;
	}

	public void setReason(String reason)
	{
		this.reason = reason;
	}

	public Long getShareSize()
	{
		return shareSize;
	}

	public void setShareSize(Long shareSize)
	{
		this.shareSize = shareSize;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String email)
	{
		this.email = email;
	}
}
