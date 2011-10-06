package ru.sincore.db.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.KickListPOJO;

import java.util.List;

public class KickListDAOImpl implements KickListDAO
{

	private static final Logger log = LoggerFactory.getLogger(KickListDAOImpl.class);
	private String marker = Marker.ANY_MARKER;

	@Override
	public void addKickedClient(KickListPOJO kick)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx  = session.getTransaction();

		try{

			tx.begin();

			if (kick != null)
			{
				session.save(kick);
				tx.commit();
			}
			tx.rollback();
		}
		catch (Exception ex)
		{
			log.error(marker,ex);
			tx.rollback();
		}
	}

	@Override
	public List<KickListPOJO> getKicked()
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx  = session.getTransaction();

		String query = "from KickListPOJO order by kickDate desc";

		try{

			tx.begin();

			Query request = session.createQuery(query);

			List<KickListPOJO> result = (List<KickListPOJO>) request.list();

			tx.commit();

			return result;
		}
		catch (Exception ex)
		{
			log.error(marker,ex);
			tx.rollback();
		}

		return null;
	}

	@Override
	public KickListPOJO getKickedByNick(String nick)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx  = session.getTransaction();

		String query = "from KickListPOJO where nickName = :nick order by kickDate desc";

		try{

			tx.begin();

			Query request = session.createQuery(query);

			request.setParameter("nick",nick);

			KickListPOJO result = (KickListPOJO) request.uniqueResult();

			tx.commit();

			return result;
		}
		catch (Exception ex)
		{
			log.error(marker,ex);
			tx.rollback();
		}

		return null;
	}

	@Override
	public void updateKickStatus(KickListPOJO kick)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx  = session.getTransaction();

		try{

			tx.begin();

			session.update(kick);

			tx.commit();

		}
		catch (Exception ex)
		{
			log.error(marker, ex);
			tx.rollback();
		}
	}
}
