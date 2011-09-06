package ru.sincore.db.dao;

import org.testng.annotations.Test;

public class ChatLogDAOImplTest
{
	@Test
	public void testSaveMessage() throws Exception
	{
		ChatLogDAOImpl chat = new ChatLogDAOImpl();
		chat.saveMessage("Valor","Message");
	}

	@Test
	public void testGetLast() throws Exception
	{

	}
}
