package ru.sincore.db.pojo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * If you do changes in class fields, please do the same changes in AbstractCmd class.
 */

/**
 * @author Valor
 * @since 14.09.2011
 * @author Alexey 'lh' Antonov
 */
@Entity
@Table(name = "cmd_list")
public class CmdListPOJO implements Serializable
{
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long 	id;
	@Column(name = "command_name",columnDefinition = "VARCHAR(50)",nullable = false,unique = true)
	private String 	commandName;
	@Column(name = "command_weight",columnDefinition = "INT(3) DEFAULT 100")
	private Integer commandWeight = 100;
	@Column(name = "command_args",columnDefinition = "TEXT",nullable = true)
	private String 	commandArgs = "";
	@Column(name = "command_syntax",columnDefinition = "TEXT",nullable = true)
	private String 	commandSyntax = "";
	@Column(name = "commandDescription", columnDefinition = "TEXT",nullable = true)
	private String  commandDescription = "";
	@Column(name = "enabled",columnDefinition = "TINYINT(1) DEFAULT 1")
	private Boolean enabled = true;
	@Column(name = "logs",columnDefinition = "TINYINT(1) DEFAULT 1")
	private Boolean logs = true;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getCommandName()
	{
		return commandName;
	}

	public void setCommandName(String commandName)
	{
		this.commandName = commandName;
	}

	public Integer getCommandWeight()
	{
		return commandWeight;
	}

	public void setCommandWeight(Integer commandWeight)
	{
		this.commandWeight = commandWeight;
	}

	public String getCommandArgs()
	{
		return commandArgs;
	}

	public void setCommandArgs(String commandArgs)
	{
		this.commandArgs = commandArgs;
	}

	public String getCommandSyntax()
	{
		return commandSyntax;
	}

	public void setCommandSyntax(String commandSyntax)
	{
		this.commandSyntax = commandSyntax;
	}

	public String getCommandDescription()
	{
		return commandDescription;
	}

	public void setCommandDescription(String commandDescription)
	{
		this.commandDescription = commandDescription;
	}

	public Boolean isLogs()
	{
		return logs;
	}

	public void setLogs(Boolean logs)
	{
		this.logs = logs;
	}

	public Boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
	}
}
