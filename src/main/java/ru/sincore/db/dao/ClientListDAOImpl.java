package ru.sincore.db.dao;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.ClientListPOJO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DAO Class for manipulation clients entity
 * @author Valor
 * @since  13.09.2011
 * @version 0.0.1
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

				if (params.getAccountFlyable() == null)
				{
					params.setAccountFlyable(false);
				}


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
	public List<ClientListPOJO> getClientList(Boolean regOnly)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx 	= session.getTransaction();

		String query = 	"select accountFlyable, bas0Allowed,baseAllowed,cid,classMask,commandMask,currentIp,helpMask," +
						"hideMe,hideShare,id,keyAuthAllowed,isKickable,lastIp,lastLogIn,lastMessage,lastNick,loginCount," +
						"maximumTimeOnline,nickName,opChatAccess,overrideFull,overrideShare,overrideSpam,password,ping,realIp," +
						"isReg,regDate,regOwner,renameable,txBytes,tigerAllowed,txBytes,ucmdAllowed" +
						" from ClientListPOJO where isReg = :flag order by nickName,regDate";

		try
		{
			tx.begin();

			ArrayList<ClientListPOJO> data = new ArrayList<ClientListPOJO>();

			Query request = session.createQuery(query).setParameter("flag",regOnly);

			List<?> result = request.list();

			tx.commit();

			for (Object obj : result)
			{
				ClientListPOJO pojo = new ClientListPOJO();

				Object[] array = (Object[]) obj;

				pojo.setAccountFlyable((Boolean) 	array[0]);
				pojo.setBas0Allowed((Boolean)		array[1]);
				pojo.setBaseAllowed((Boolean)		array[2]);
				pojo.setCid((String)				array[3]);
				pojo.setClassMask((Integer)			array[4]);
				pojo.setCommandMask((byte[])		array[5]);
				pojo.setCurrentIp((String)			array[6]);
				pojo.setHelpMask((byte[])			array[7]);
				pojo.setHideMe((Boolean)			array[8]);
				pojo.setHideShare((Boolean)			array[9]);
				pojo.setId((Long)					array[10]);
				pojo.setKeyAuthAllowed((Boolean)	array[11]);
				pojo.setKickable((Boolean)			array[12]);
				pojo.setLastIp((String)				array[13]);
				pojo.setLastLogIn((Date)			array[14]);
				pojo.setLastMessage((String)		array[15]);
				pojo.setLastNick((String)			array[16]);
				pojo.setLoginCount((Long)			array[17]);
				pojo.setMaximumTimeOnline((Long) 	array[18]);
				pojo.setNickName((String)			array[19]);
				pojo.setOpChatAccess((Boolean)		array[20]);
				pojo.setOverrideFull((Boolean)		array[21]);
				pojo.setOverrideShare((Boolean)		array[22]);
				pojo.setOverrideSpam((Boolean)		array[23]);
				pojo.setPassword((String)			array[24]);
				pojo.setPing((Boolean)				array[25]);
				pojo.setRealIp((String)				array[26]);
				pojo.setReg((Boolean)				array[27]);
				pojo.setRegDate((Date)				array[28]);
				pojo.setRegOwner((String)			array[29]);
				pojo.setRenameable((Boolean)		array[30]);
				pojo.setRxBytes((Long)				array[31]);
				pojo.setTigerAllowed((Boolean)		array[32]);
				pojo.setTxBytes((Long)				array[33]);
				pojo.setUcmdAllowed((Boolean)		array[34]);

				data.add(pojo);

			}

			return data;

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
