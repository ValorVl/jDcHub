package ru.sincore.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Client;

public abstract class AbstractCmd
{
	private static final Logger log = LoggerFactory.getLogger(AbstractCmd.class);

	String 	cmdNames;
	String 	cmdArgs;
	String 	cmdDescription;
	String 	cmdExecutorClass;
	String 	cmdSyntax;
	Integer cmdWeight;
	Boolean enabled;
	Boolean logged;


	public abstract void execute(String cmd,String args, Client client);

	Boolean validateRights(Integer clientRightWeight)
	{
		log.info("Validate...");

		if(clientRightWeight >= cmdWeight)
		{
			log.info("Right normal.. follow..");
			return true;
		}else
		{
			log.info("Client right weight :"+clientRightWeight+" < "+cmdWeight);
			return false;
		}
	}

	public String getCmdNames()
	{
		return cmdNames;
	}

	public void setCmdNames(String cmdNames)
	{
		this.cmdNames = cmdNames;
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

	public String getCmdExecutorClass()
	{
		return cmdExecutorClass;
	}

	public void setCmdExecutorClass(String cmdExecutorClass)
	{
		this.cmdExecutorClass = cmdExecutorClass;
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

	public Boolean getEnabled()
	{
		return enabled;
	}

	public void setEnabled(Boolean enabled)
	{
		this.enabled = enabled;
	}

	public Boolean getLogged()
	{
		return logged;
	}

	public void setLogged(Boolean logged)
	{
		this.logged = logged;
	}
}
