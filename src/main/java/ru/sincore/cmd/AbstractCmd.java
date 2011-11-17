package ru.sincore.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.client.AbstractClient;

public abstract class AbstractCmd
{
	private static final Logger log = LoggerFactory.getLogger(AbstractCmd.class);

	String  cmdName;
	String 	cmdArgs;
	String 	cmdDescription;
	String 	cmdSyntax;
	Integer cmdWeight;
	Boolean enabled;
	Boolean logs;


	public abstract void execute(String cmd,String args, AbstractClient client);

	Boolean validateRights(Integer clientRightWeight)
	{
		log.info("Validating user rights to execute command...");

		if(clientRightWeight >= cmdWeight)
		{
			log.info("Rights are normal. Continue executing.");
			return true;
		}
        else
		{
			log.info("User have no anough weight.");
			return false;
		}
	}

	public String getCmdName()
	{
		return cmdName;
	}

	public void setCmdName(String cmdName)
	{
		this.cmdName = cmdName;
	}

	public String getCmdArgs()
	{
		return cmdArgs;
	}

	public void setCmdArgs(String cmdArgs)
	{
		this.cmdArgs = cmdArgs;
	}

	public String getCmdDescription()
	{
		return cmdDescription;
	}

	public void setCmdDescription(String cmdDescription)
	{
		this.cmdDescription = cmdDescription;
	}

	public String getCmdSyntax()
	{
		return cmdSyntax;
	}

	public void setCmdSyntax(String cmdSyntax)
	{
		this.cmdSyntax = cmdSyntax;
	}

	public Integer getCmdWeight()
	{
		return cmdWeight;
	}

	public void setCmdWeight(Integer cmdWeight)
	{
		this.cmdWeight = cmdWeight;
	}

	public Boolean isEnabled()
	{
		return enabled;
	}

	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
	}

	public Boolean isLogs()
	{
		return logs;
	}

	public void setLogs(Boolean logs)
	{
		this.logs = logs;
	}
}
