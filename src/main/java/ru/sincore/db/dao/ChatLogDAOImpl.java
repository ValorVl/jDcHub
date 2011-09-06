package ru.sincore.db.dao;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.ChatLogPOJO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class managed chat messages entry.
 *
 * @author Valor
 */
public class ChatLogDAOImpl implements ChatLogDAO
{
	private static final Logger log = Logger.getLogger(ChatLogDAOImpl.class);

	/**
	 * Save nonprivileged user chat message.
	 *
	 * @param nick - nick name owner message
	 * @param message - chat message string entry
	 */
	@Override
	public void saveMessage(String nick, String message)
	{
		Session session = null;

		try{

			session = HibernateUtils.getSessionFactory().openSession();

			session.getTransaction().begin();

			ChatLogPOJO data = new ChatLogPOJO();

			data.setMessage(message);
			data.setSendDate(new Date());
			data.setNickName(nick);

			session.save(data);

			session.getTransaction().commit();
			session.close();

		}catch (Exception ex){
			log.error(ex);
			if (session == null)
			{
				throw new AssertionError();
			}
			session.getTransaction().rollback();
		}finally {
			try{
				if (session != null)
				{
					session.close();
				}
			}catch (Exception ex){
				log.error(ex);
			}
		}

	}

	/**
	 * The number of rows returned in a chat with a successful connection to the server
	 *
	 * @param lastRowCount returned row count
	 */
	@Override
	public List<ChatLogPOJO> getLast(Integer lastRowCount)
	{

		Session session = null;

		try{

			session = HibernateUtils.getSessionFactory().openSession();
			session.getTransaction().begin();

			Query query = session.createQuery("select sendDate, nickName, message from ChatLogPOJO order by sendDate desc");

			List<?>	result =  query.setMaxResults(lastRowCount).list();

			ArrayList<ChatLogPOJO> data = new ArrayList<ChatLogPOJO>();

			for (Object obj : result)
			{
				Object[] 	array 	= (Object[]) obj;
				Date		date 	= (Date) array[0];
				String 		nick 	= (String) array[1];
				String 		message = (String) array[2];

				ChatLogPOJO pojo = new ChatLogPOJO();

				pojo.setSendDate(date);
				pojo.setNickName(nick);
				pojo.setMessage(message);

				data.add(pojo);
			}


			session.getTransaction().commit();

			return data;

		}catch (Exception ex)
		{
			log.error("Can not get chat entry >> ",ex);
			if (session != null)
			{
				if (session.isOpen())
				{
					session.getTransaction().rollback();
				}
			}
		}
		finally {
			try{
				if(session.isOpen())
				{
					session.close();
				}
			}catch (Exception ex)
			{
				log.error("Can not close entity manager >>",ex);
			}
		}
		return null;
	}
}
