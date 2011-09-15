package ru.sincore.db.pojo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Valor
 * @since 14.09.2011
 * @version 0.0.1
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
	@Column(name = "command_executor_class",columnDefinition = "VARCHAR(250)",nullable = false)
	private String 	commandExecutorClass;
	@Column(name = "command_weight",columnDefinition = "INT(3) DEFAULT 0")
	private Integer commandWeight;
	@Column(name = "command_args",columnDefinition = "TEXT",nullable = true)
	private String 	commandArgs;
	@Column(name = "command_syntax",columnDefinition = "TEXT",nullable = true)
	private String 	commandSyntax;
	@Column(name = "commandDescription", columnDefinition = "TEXT",nullable = true)
	private String  commandDescription;
	@Column(name = "enabled",columnDefinition = "TINYINT(1) DEFAULT 1")
	private Boolean enabled;
	@Column(name = "logged",columnDefinition = "TINYINT(1) DEFAULT 1")
	private Boolean logged;

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

	public String getCommandExecutorClass()
	{
		return commandExecutorClass;
	}

	public void setCommandExecutorClass(String commandExecutorClass)
	{
		this.commandExecutorClass = commandExecutorClass;
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

	public Boolean getLogged()
	{
		return logged;
	}

	public void setLogged(Boolean logged)
	{
		this.logged = logged;
	}

	public Boolean getEnabled()
	{
		return enabled;
	}

	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
	}
}
