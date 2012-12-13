package ru.sincore.db.dao;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.BanListPOJO;
import ru.sincore.util.Constants;

import java.util.Date;
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

		try
        {
			tx.begin();

			session.save(ban);

			tx.commit();

            log.debug("[ADD] Ban to user \'" + ban.getNick() + "\' stored to db.");

            return true;
		}
        catch (Exception ex)
		{
			tx.rollback();
			log.error(ex);
		}

        return false;
	}

	/**
	 * Check ip address, or nickname in the presence of the banned list
	 *
	 * @param nick nickname hub user
     * @param ip ip address hub user
	 * @return BanListPOJO object
	 */
	@Override
	public BanListPOJO getBan(String nick, String ip)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();

		try{
			tx.begin();

			Query query;

            if (!StringUtils.isEmpty(nick))
            {
                query = session.createQuery("from BanListPOJO where nick = :nick and dateStop > :currentDate order by dateStop desc");
                query.setParameter("nick", nick);
                query.setParameter("currentDate", new Date());
            }
            else if (!StringUtils.isEmpty(ip))
            {
                query = session.createQuery("from BanListPOJO where dateStop > :currentDate and (inet_aton(:ip) between inet_aton_net(ip) and inet_aton_bc(ip)) order by dateStop desc");
                query.setParameter("ip", ip);
                query.setParameter("currentDate", new Date());
            }
            else
                return null;

			List<BanListPOJO> result = (List<BanListPOJO>) query.setMaxResults(1).list();

			tx.commit();

            if ((result == null) || result.isEmpty())
            {
                return null;
            }

            return result.get(0);
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

        try
        {
            tx.begin();

            Query query = session.createQuery(
                    "from BanListPOJO where nick = :nick or inet_aton(:ip) between inet_aton_net(ip) and inet_aton_bc(ip) order by dateStop desc"
                                             );
            query.setParameter("nick", nick).setParameter("ip", ip);

            List<BanListPOJO> result = (List<BanListPOJO>) query.setMaxResults(1).list();

            tx.commit();

            if ((result == null) || result.isEmpty())
            {
                return null;
            }

            return result.get(0);
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


    @Override
    public List<BanListPOJO> getAllBans(int banShowType, int page, int count)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try{
            tx.begin();

            StringBuilder queryString = new StringBuilder();
            queryString.append("from BanListPOJO ");

            if (banShowType == Constants.ACTIVE)
                queryString.append("where dateStop > :currentDate ");
            else if (banShowType == Constants.EXPIRED)
                queryString.append("where dateStop <= :currentDate ");

            queryString.append("order by dateStart desc");

            Query query = session.createQuery(queryString.toString());

            if (banShowType == Constants.ACTIVE || banShowType == Constants.EXPIRED)
                query.setParameter("currentDate", new Date());

            query.setFirstResult(page*count);
            query.setMaxResults(count);

            List<BanListPOJO> result = (List<BanListPOJO>) query.list();

            tx.commit();

            return result;

        }catch (HibernateException ex)
        {
            log.error(ex);
            tx.rollback();
        }

        return null;
    }
}
