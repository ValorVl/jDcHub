package ru.sincore.db.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.CmdListPOJO;

import java.util.ArrayList;
import java.util.List;

/**
 *  @author Valor
 *  @since 14.09.2011
 *  @version 0.0.1
 */
public class CmdListDAOImpl implements CmdListDAO
{
	private static final Logger log = LoggerFactory.getLogger(CmdListDAOImpl.class);

	private static String marker = Marker.ANY_MARKER;

	@Override
	public boolean addCommand(String name,
							  int weight,
							  String executorClass,
							  String[] args,
							  String description,
							  String syntax,
							  Boolean enabled,
							  Boolean logged)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();

		try{

			tx.begin();

			CmdListPOJO pojo = new CmdListPOJO();

			pojo.setCommandName(name);
			pojo.setCommandWeight(weight);
			pojo.setCommandExecutorClass(executorClass);
			pojo.setCommandArgs(args.toString());
			pojo.setCommandDescription(description);
			pojo.setCommandSyntax(syntax);
			pojo.setEnabled(enabled);
			pojo.setLogged(logged);

			session.save(pojo);
			tx.commit();

			if (log.isDebugEnabled())
			{
				log.debug(marker,"Command  :"+name+" stored.");
			}

			return true;

		}catch (Exception ex)
		{
			tx.rollback();
			log.error(marker, ex);
		}

		return false;
	}

	@Override
	public boolean delCommand(String name)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();

		try{

			tx.begin();

			Query query = session.createQuery("delete from CmdListPOJO where commandName = :commandName").setParameter("commandName",name);

			query.executeUpdate();

			tx.commit();

			if(log.isDebugEnabled())
			{
				log.debug(marker,"Command : "+name+" deleted");
			}

			return true;

		}catch (Exception ex)
		{
			tx.rollback();
			log.error(marker, ex);
		}
		return false;
	}

	@Override
	public boolean updateCommand(CmdListPOJO commandObject)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();

		try{

			tx.begin();
			session.update(commandObject);
			tx.commit();

			if (log.isDebugEnabled())
			{
				log.debug(marker,"Command : "+commandObject.getCommandName()+" updated");
			}

			return true;

		}catch (Exception ex)
		{
			tx.rollback();
			log.error(marker, ex);
		}

		return false;
	}

	@Override
	public List<CmdListPOJO> getCommandList()
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx = session.getTransaction();

		try{

			tx.begin();

			Query query = session.createQuery("select id,commandName,commandExecutorClass," +
						"commandWeight,commandArgs,commandSyntax,commandDescription,enabled,logged from CmdListPOJO");

			List<?> resultList = query.list();

			ArrayList<CmdListPOJO> result 	= new ArrayList<CmdListPOJO>();

			tx.commit();

			for (Object obj : resultList)
			{

				Object[] array = (Object[]) obj;

				Long 	id		    	= (Long)			array[0];
				String  commandName 	= (String)			array[1];
				String	commandExec 	= (String)			array[2];
				Integer commandWeight 	= (Integer)			array[3];
				String  commandArgs		= (String)			array[4];
				String  commandSyntax	= (String)			array[5];
				String  commandDesc		= (String)			array[6];
				Boolean enabled			= (Boolean)			array[7];
				Boolean logged			= (Boolean)			array[8];

				CmdListPOJO pojo 		= new CmdListPOJO();

				pojo.setId(id);
				pojo.setCommandName(commandName);
				pojo.setCommandExecutorClass(commandExec);
				pojo.setCommandWeight(commandWeight);
				pojo.setCommandArgs(commandArgs);
				pojo.setCommandSyntax(commandSyntax);
				pojo.setCommandDescription(commandDesc);
				pojo.setEnabled(enabled);
				pojo.setLogged(logged);

				result.add(pojo);
			}

			return result;

		}catch (HibernateException ex)
		{
			tx.rollback();
			log.error(marker, ex);
		}

		return null;
	}
}
