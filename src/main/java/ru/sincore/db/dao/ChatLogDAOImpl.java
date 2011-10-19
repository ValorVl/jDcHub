package ru.sincore.db.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.ChatLogPOJO;

import java.util.Date;
import java.util.List;

/**
 * This class managed chat messages entry.
 *
 * @author Valor
 */
public class ChatLogDAOImpl implements ChatLogDAO
{
	private static final Logger log = LoggerFactory.getLogger(ChatLogDAOImpl.class);

	/**
	 * Save nonprivileged user chat message.
	 *
	 * @param nick - nick name owner message
	 * @param message - chat message string entry
	 */
	@Override
	public void saveMessage(String nick, String message)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();

		try
        {
			tx.begin();

			ChatLogPOJO data = new ChatLogPOJO();

			data.setMessage(message);
			data.setSendDate(new Date());
			data.setNickName(nick);

			session.save(data);

			tx.commit();

		}
        catch (Exception ex)
        {
			log.error(ex.toString());
			tx.rollback();
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

		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();

		try
        {
			tx.begin();

			Query query = session.createQuery("from ChatLogPOJO order by sendDate desc");

			List<ChatLogPOJO> result = (List<ChatLogPOJO>) query.setMaxResults(lastRowCount).list();

			tx.commit();

			return result;

		}
        catch (Exception ex)
		{
			log.error(ex.toString());
			tx.rollback();
		}
		return null;
	}
}
