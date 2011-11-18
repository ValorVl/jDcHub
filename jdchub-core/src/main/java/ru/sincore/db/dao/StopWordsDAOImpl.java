package ru.sincore.db.dao;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.StopWordsPOJO;

import java.util.List;

public class StopWordsDAOImpl implements StopWordsDAO
{

	private static final Logger log = LoggerFactory.getLogger(StopWordsDAOImpl.class);
	private String marker = Marker.ANY_MARKER;

	@Override
	public void addMatch(String match)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx  = session.getTransaction();

		try{
			tx.begin();

			StopWordsPOJO pojo = new StopWordsPOJO();

			pojo.setPattern(match);

			session.save(pojo);

			tx.commit();
		}
		catch (Exception ex)
		{
			log.error(marker,ex);
			tx.rollback();
		}

	}

	@Override
	public List<StopWordsPOJO> getMatches()
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx  = session.getTransaction();

		String query = "from StopWordsPOJO order by id asc";

		try
		{
			tx.begin();

			Query request = session.createQuery(query);

			List<StopWordsPOJO> result = (List<StopWordsPOJO>) request.list();

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
	public void delMatchById()
	{

	}

	@Override
	public void updateMatchCounter(Integer matchId)
	{

	}
}
