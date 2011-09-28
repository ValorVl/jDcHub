package ru.sincore.db.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.ClientListPOJO;

import java.util.List;

/**
 * DAO Class for manipulation clients entity
 * @author Valor
 * @since  13.09.2011
 * @version 0.0.2
 */
public class ClientListDAOImpl implements ClientListDAO
{

	private static final Logger log = LoggerFactory.getLogger(ClientListDAOImpl.class);

	private final String marker = Marker.ANY_MARKER;

	@Override
	public boolean addClient(ClientListPOJO params)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();

		try
		{
		   	tx.begin();

			if (!params.equals(null))
			{
				session.save(params);
				tx.commit();
			}
			else
			{
				log.debug("NULL Object params! Return FALSE");
				return false;
			}

		}catch (Exception ex)
		{
		   	log.error(marker,ex);
			tx.rollback();
		}
		return false;
	}


	@Override
	public boolean delClient(String nickName)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx 	= session.getTransaction();

		try{
			tx.begin();

			ClientListPOJO pojo = new ClientListPOJO();

			if (!nickName.isEmpty())
			{
				pojo.setNickName(nickName);
			}
			else
			{
				log.debug("Param NickName is empty! Return FALSE");
				return false;
			}

			session.delete(pojo);
			tx.commit();

			return true;

		}catch (Exception ex)
		{
			tx.rollback();
			log.error(marker, ex);
		}
		return false;
	}

	@Override
	public ClientListPOJO getClientByNick(String nick)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx  = session.getTransaction();

		String query = "from ClientListPOJO where nickName =:nick";

		try{

			tx.begin();

			Query request = session.createQuery(query).setParameter("nick",nick);

			ClientListPOJO client = (ClientListPOJO) request.uniqueResult();

			tx.commit();

			return client;

		}catch (Exception ex)
		{
			log.error(marker,ex);
			tx.rollback();
		}

		return null;
	}

	@Override
	public List<ClientListPOJO> getClientList(Boolean regOnly)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx 	= session.getTransaction();

		String query = 	"from ClientListPOJO where isReg = :flag order by nickName,regDate";

		try
		{
			tx.begin();

			Query request = session.createQuery(query).setParameter("flag",regOnly);

			List<ClientListPOJO> result = (List<ClientListPOJO>) request.list();

			tx.commit();

			return result;

		}catch (Exception ex)
		{
		   	log.error(marker,ex);
			tx.rollback();
		}

		return null;
	}

	@Override
	public boolean updateClient(ClientListPOJO object)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx  = session.getTransaction();

		try{

			tx.begin();

			session.update(object);

			tx.commit();

			return true;

		}catch (Exception ex)
		{
			log.error(marker,ex);
			tx.rollback();
		}
		return false;
	}
}
