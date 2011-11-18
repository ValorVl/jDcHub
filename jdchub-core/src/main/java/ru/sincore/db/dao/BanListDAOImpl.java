package ru.sincore.db.dao;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.BanListPOJO;

import java.util.List;

public class BanListDAOImpl implements BanListDAO
{
	private static final Logger log = Logger.getLogger(BanListDAOImpl.class);

	/**
	 * A method add hub user into ban list.
	 *
	 * @return 1 id end only if ban success added otherwise return -1
	 */
	@Override
	public Boolean addBan(BanListPOJO ban)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();

		try{

			tx.begin();

			session.save(ban);

			tx.commit();

		}catch (Exception ex)
		{
			tx.rollback();
			log.error(ex);
			return false;
		}

		return true;
	}

	/**
	 * Check ip address, or nickname in the presence of the banned list
	 *
	 * @param nick nickname hub user
     * @param ip ip address hub user
	 * @return BanListPOJO object
	 */
	@Override
	public List<BanListPOJO> getBan(String nick, String ip)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();

		try{
			tx.begin();

			Query query = session.createQuery("from BanListPOJO where ip = :ip or nick = :nick");
			query.setParameter("ip", ip).setParameter("nick", nick);

			List<BanListPOJO> result 		= (List<BanListPOJO>) query.list();

			tx.commit();


			return result;

		}
        catch (Exception ex)
		{
			log.error(ex);
			tx.rollback();
		}

        return null;
	}


    @Override
    public BanListPOJO getLastBan(String nick, String ip)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try{
            tx.begin();

            Query query = session.createQuery("from BanListPOJO where ip = :ip or nick = :nick order by dateStart desc");
            query.setParameter("ip", ip).setParameter("nick", nick);

            BanListPOJO result = (BanListPOJO) query.uniqueResult();

            tx.commit();

            return result;

        }
        catch (Exception ex)
        {
            log.error(ex);
            tx.rollback();
        }

        return null;
    }


    /**
	 * Get latest ban entry, limit parameter rowCount
	 * @param rowCount
	 * @return	List BanListPOJO ban entry
	 */
	@Override
	public List<BanListPOJO> lsBan(Integer rowCount)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();

		try{
			tx.begin();

			Query query = session.createQuery("from BanListPOJO order by dateStart desc");

			List<BanListPOJO> result = (List<BanListPOJO>) query.setMaxResults(rowCount).list();

			tx.commit();

			return result;

		}catch (HibernateException ex)
		{
			log.error(ex);
			tx.rollback();
		}

		return null;
	}

	/**
	 * Returns a list of all cases ban user
	 * @param nick nickname hub user
	 * @return Bans list
	 */
	@Override
	public List<BanListPOJO> userBanList(String nick)
	{
		return null;  //TODO Coming soon
	}
}
