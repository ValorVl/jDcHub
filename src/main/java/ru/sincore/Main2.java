package ru.sincore;

import ru.sincore.db.dao.ChatLogDAOImpl;
import ru.sincore.db.pojo.ChatLogPOJO;

import java.util.Iterator;

public class Main2
{
	public static void main(String[] args)
	{
		ChatLogDAOImpl dao = new ChatLogDAOImpl();

		Iterator it = dao.getLast(1).iterator();

		while (it.hasNext())
		{
			ChatLogPOJO next = (ChatLogPOJO) it.next();

			System.out.println(next.getSendDate()+"  "+next.getNickName()+"  "+next.getMessage());

		}



	}
}
