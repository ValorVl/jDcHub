package ru.sincore.db.dao;


import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.sincore.ConfigurationManager;
import ru.sincore.db.pojo.BanListPOJO;

import java.util.Date;
import java.util.List;

public class BanListDAOImplTest
{
	@BeforeMethod
	public void setUp() throws Exception
	{
		PropertyConfigurator.configure(ConfigurationManager.getInstance().getHubConfigDir() + "/log4j.properties");
	}

	@Test
	public void testAddBan() throws Exception
	{
		BanListDAOImpl banList = new BanListDAOImpl();
        BanListPOJO banListPOJO = new BanListPOJO();

        // fill ban list pojo
        banListPOJO.setNick("Valor");
        banListPOJO.setIp("10.10.10.125");
        banListPOJO.setOpNick("Valor");
        banListPOJO.setBanType(1);
        banListPOJO.setDateStart(new Date());
        banListPOJO.setDateStop(new Date((new Date()).getTime() + 12123L));
        banListPOJO.setEmail("valor@valor.en");
        banListPOJO.setReason("Reason");

		banList.addBan(banListPOJO);
	}

	@Test
	public void testGetBan() throws Exception
	{

	}

	@Test
	public void testLsBan() throws Exception
	{
	   	BanListDAOImpl banList = new BanListDAOImpl();
		List<BanListPOJO> bans = banList.lsBan(10);

		for (BanListPOJO obj : bans)
		{

			System.out.println(obj.getIp());
		}
	}

	@Test
	public void testUserBanList() throws Exception
	{

	}
}
