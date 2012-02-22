package ru.sincore.db.dao;

import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.sincore.ConfigurationManager;
import ru.sincore.db.pojo.CmdListPOJO;

import java.util.List;

public class CmdListDAOImplTest
{

	@BeforeMethod
	public void setUp() throws Exception
	{
		PropertyConfigurator.configure(ConfigurationManager.getInstance().getHubConfigDir() + "/log4j.properties");
	}

	@Test
	public void testAddCommand() throws Exception
	{
	   	CmdListDAOImpl cmdList = new CmdListDAOImpl();
		boolean res = cmdList.addCommand("Test1",10,"bla","arg","Desc",true,false);
		System.out.println(res);
	}

	@Test
	public void testDelCommand() throws Exception
	{
		CmdListDAOImpl cmdList = new CmdListDAOImpl();
		cmdList.delCommand("Test");
	}

	@Test
	public void testUpdateCommand() throws Exception
	{
		CmdListDAOImpl cmdList = new CmdListDAOImpl();

		CmdListPOJO pojo = new CmdListPOJO();

		pojo.setId(1L);

		cmdList.updateCommand(pojo);
	}

	@Test
	public void testGetCommandList() throws Exception
	{
		try{
			CmdListDAOImpl cmdList = new CmdListDAOImpl();
			List<CmdListPOJO> commands = cmdList.getCommandList();

			if (commands != null)
			{
				for(CmdListPOJO list : commands)
				{
					System.out.println(list.getCommandName());
				}
			}

		}catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}
}
