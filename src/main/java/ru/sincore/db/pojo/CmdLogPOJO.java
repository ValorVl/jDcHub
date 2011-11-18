/*
* CmdLogPOJO.java
*
*
* Copyright (C) 2011 Valor
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

package ru.sincore.db.pojo;

import org.hibernate.annotations.Index;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "cmd_log")
public class CmdLogPOJO
{
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	Long 		id;

	@Index(name = "nick_index")
	@Column(name = "nick_name", columnDefinition = "VARCHAR(150)")
	String 		nickName;

	@Index(name = "command_index")
	@Column(name = "command_name",columnDefinition = "VARCHAR(50)")
	String 		commandName;

	@Column(name = "command_args",columnDefinition = "VARCHAR(250)")
	String 		commandArgs;

	@Index(name = "date_index")
	@Column(name = "execute_date",columnDefinition = "DATETIME")
	Date		executeDate;

	@Column(name = "execute_result",columnDefinition = "TEXT")
	String		executeResult;

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

	public String getCommandName()
	{
		return commandName;
	}

	public void setCommandName(String commandName)
	{
		this.commandName = commandName;
	}

	public Date getExecuteDate()
	{
		return executeDate;
	}

	public void setExecuteDate(Date executeDate)
	{
		this.executeDate = executeDate;
	}

	public String getExecuteResult()
	{
		return executeResult;
	}

	public void setExecuteResult(String executeResult)
	{
		this.executeResult = executeResult;
	}

	public String getCommandArgs()
	{
		return commandArgs;
	}

	public void setCommandArgs(String commandArgs)
	{
		this.commandArgs = commandArgs;
	}
}
