package ru.sincore.db.dao;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.BanListPOJO;

import java.util.Date;
import java.util.List;

public class BanListDAOImpl implements BanListDAO
{
	private static final Logger log = Logger.getLogger(BanListDAOImpl.class);

	/**
	 * A method add hub user into ban list.
	 *
	 * @param nick nickname a hub user
	 * @param ip hub user ip address
	 * @param host hub user host name
	 * @param banType ban type, passable 1 - nick ban, 2 - ip ban, 3 -
	 * @param start ban start date and time
	 * @param end ban end date, if -1 else perm ban
	 * @param banOwner nickname ban owner
	 * @param reason reason description
	 * @param shareSize share size
	 * @param email email address hub user
	 * @return 1 id end only if ban success added otherwise return -1
	 */
	@Override
	public Boolean addBan(String nick,
						  String ip,
						  String host,
						  Integer banType,
						  Date start,
						  Date end,
						  String banOwner,
						  String reason,
						  Long shareSize,
						  String email)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();

		try{

			tx.begin();

			BanListPOJO data = new BanListPOJO();

			data.setNick(nick);
			data.setIp(ip);
			data.setHostName(host);
			data.setBanType(banType);
			data.setDateStart(start);
			data.setFateStop(end);
			data.setNikOp(banOwner);
			data.setReason(reason);
			data.setShareSize(shareSize);
			data.setEmail(email);

			session.save(data);

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
	 * @param ip ip address hub user
	 * @param nick nickname hub user
	 * @return BanListPOJO object
	 */
	@Override
	public List<BanListPOJO> getBan(String ip, String nick)
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

		}catch (Exception ex)
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
