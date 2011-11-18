package ru.sincore.db.pojo;

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

import javax.persistence.*;

/**
 * @author Valor
 */
@Entity
@Table(name = "chat_list")
public class ChatListPOJO
{

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long 		id;

	//Client (user) type, 1=bot,32=hub
	@Column(name = "type", length = 2, nullable = false)
	private Integer		type = 1;

	@Column(name = "chat_name", columnDefinition = "VARCHAR(100)", nullable = false)
	private String 		chatName;

	@Column(name = "chat_description", columnDefinition = "TEXT", nullable = false)
	private String 		chatDescription =" ";

	@Column(name = "weight",length = 3,nullable = false)
	private Integer 	rightWeight = 0;

	@Column(name = "sid",columnDefinition = "VARCHAR(4)", nullable = false)
	private String		chatCid;

	@Column(name = "enabled", columnDefinition = "TINYINT(1)", nullable = false)
	private Boolean		enabled = false;

	@Column(name = "chat_owner", length = 255, nullable = true)
	private String		chatOwner =" ";

	@Column(name = "chat_handler", columnDefinition = "VARCHAR(250)", nullable = true)
	private String		chatHandler =" ";

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public Integer getType()
	{
		return type;
	}

	public void setType(Integer type)
	{
		this.type = type;
	}

	public String getChatName()
	{
		return chatName;
	}

	public void setChatName(String chatName)
	{
		this.chatName = chatName;
	}

	public String getChatDescription()
	{
		return chatDescription;
	}

	public void setChatDescription(String chatDescription)
	{
		this.chatDescription = chatDescription;
	}

	public Integer getRightWeight()
	{
		return rightWeight;
	}

	public void setRightWeight(Integer rightWeight)
	{
		this.rightWeight = rightWeight;
	}

	public String getChatCid()
	{
		return chatCid;
	}

	public void setChatCid(String chatCid)
	{
		this.chatCid = chatCid;
	}

	public Boolean getEnabled()
	{
		return enabled;
	}

	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
	}

	public String getChatOwner()
	{
		return chatOwner;
	}

	public void setChatOwner(String chatOwner)
	{
		this.chatOwner = chatOwner;
	}

	public String getChatHandler()
	{
		return chatHandler;
	}

	public void setChatHandler(String chatHandler)
	{
		this.chatHandler = chatHandler;
	}
}
