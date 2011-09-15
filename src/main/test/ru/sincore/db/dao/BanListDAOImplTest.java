package ru.sincore.db.dao;


import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.sincore.db.pojo.BanListPOJO;

import java.util.Date;
import java.util.List;

public class BanListDAOImplTest
{
	@BeforeMethod
	public void setUp() throws Exception
	{
		PropertyConfigurator.configure("./etc/log4j.properties");
	}

	@Test
	public void testAddBan() throws Exception
	{
		BanListDAOImpl banList = new BanListDAOImpl();
		banList.addBan("Valor","10.10.10.125","valor",1,new Date(),
					   new Date(),"Valor","test",13123L,"");
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
