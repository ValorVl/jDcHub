package ru.sincore.db.dao;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.BanListPOJO;

import java.util.ArrayList;
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

		try{

			session.getTransaction().begin();

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

			session.beginTransaction().commit();
			session.close();

		}catch (Exception ex)
		{
			session.getTransaction().rollback();
			log.error(ex);
			return false;
		}finally {
			try{
				if (session.isOpen())
				{
					session.close();
				}
			}catch (Exception ex)
			{
				log.error(ex);
			}
		}

		return true;
	}

	/**
	 * Check ip address, or nickname in the presence of the banned list
	 * @param ip ip address hub user
	 * @param nick nickname hub user
	 * @return BanListPOJO object
	 */
	@Override
	public BanListPOJO getBan(String ip, String nick)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();

		try{
			session.getTransaction().begin();

			Query query = session.createQuery("select ip, nick, hostName, banType, dateStart, fateStop, nikOp, reason, shareSize, email from BanListPOJO where ip = :ip or nick = :nick")
					.setParameter("ip", ip).setParameter("nick", nick);

			List<Object> data 		= query.list();
			BanListPOJO  banList 	= new BanListPOJO();

			session.getTransaction().commit();

			for (Object obj : data)
			{
				Object[] array = (Object[]) obj;

				banList.setIp((String)			array[0]);
				banList.setNick((String) 		array[1]);
				banList.setHostName((String) 	array[2]);
				banList.setBanType((Integer) 	array[3]);
				banList.setDateStart((Date) 	array[4]);
				banList.setFateStop((Date) 		array[5]);
				banList.setNikOp((String) 		array[6]);
				banList.setReason((String) 		array[7]);
				banList.setShareSize((Long) 	array[8]);
				banList.setEmail((String) 		array[9]);
			}

			return banList;

		}catch (Exception ex)
		{
			log.error(ex);
			session.getTransaction().rollback();
		}finally {
			try{
				if (session.isOpen())
				{
					session.close();
				}
			}catch (Exception ex)
			{
				log.error(ex);
			}
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



		try{
			session.getTransaction().begin();

			Query query = session.createQuery("select ip, nick, dateStart, fateStop, nikOp, shareSize, reason " +
											  "from BanListPOJO order by dateStart desc");

			List<?> result = query.setMaxResults(rowCount).list();

			ArrayList<BanListPOJO> data = new ArrayList<BanListPOJO>();

			session.beginTransaction().commit();

			for (Object obj : result)
			{
				Object[] array = (Object[]) obj;

				String 		ip 			= (String) 	array[0];
				String      nick 		= (String) 	array[1];
				Date		dateStart 	= (Date) 	array[2];
				Date		stopDate 	= (Date) 	array[3];
				String		nickOp 		= (String) 	array[4];
				Long		shareSize 	= (Long) 	array[5];
				String 		reason 		= (String) 	array[6];

				BanListPOJO pojo = new BanListPOJO();

				pojo.setIp(ip);
				pojo.setNick(nick);
				pojo.setDateStart(dateStart);
				pojo.setFateStop(stopDate);
				pojo.setNikOp(nickOp);
				pojo.setShareSize(shareSize);
				pojo.setReason(reason);

				data.add(pojo);
			}

			return data;

		}catch (HibernateException ex)
		{
			log.error(ex);
		}finally {
			try{
				 if (session.isOpen())
				 {
					session.close();
				 }
			}catch (Exception ex)
			{
				log.error(ex);
			}
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
		return null;
	}
}
