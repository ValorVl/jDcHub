package ru.sincore.db.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.CmdLogPOJO;

import java.util.Date;
import java.util.List;

/**
 * @author Valor
 */
public class CmdLogDAOImpl implements CmdLogDAO
{

	private static final Logger log = LoggerFactory.getLogger(CmdLogDAOImpl.class);

	private String marker = Marker.ANY_MARKER;

	@Override
	public void putLog(String nickName, String commandName, String commandResult, String commandArgs)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx 	= session.getTransaction();

		try
		{
			tx.begin();

			CmdLogPOJO pojo = new CmdLogPOJO();

			pojo.setNickName(nickName);
			pojo.setCommandName(commandName);
			pojo.setExecuteResult(commandResult);
			pojo.setCommandArgs(commandArgs);
			pojo.setExecuteDate(new Date());

			session.save(pojo);

			tx.commit();

		}catch (HibernateException ex)
		{
			tx.rollback();
			log.error(marker,ex);
		}

	}

	@Override
	public List<CmdLogPOJO> search(Date putLogDate, String commandName)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx 	= session.getTransaction();

		try
		{
			tx.begin();

			Query query = session.createQuery("from CmdLogPOJO where executeDate = :execdate or commandName = :cmdname");

			query.setParameter("execdate",putLogDate).setParameter("cmdname",commandName);

			List<CmdLogPOJO> result = (List<CmdLogPOJO>) query.list();

			tx.commit();

			return result;

		}catch (HibernateException ex)
		{
			tx.rollback();
			log.error(marker,ex);
		}

		return null;
	}
}
