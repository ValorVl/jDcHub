/*
* CmdLogDAOImpl.java
*
*
* Copyright (C) 2011 Valor
* Copyright (C) 2011 Alexey 'lh' Antonov
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package ru.sincore.db.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.CmdLogPOJO;

import java.util.Date;
import java.util.List;

/**
 * @author Valor
 * @author Alexey 'lh' Antonov
 */
public class CmdLogDAOImpl implements CmdLogDAO
{
	private static final Logger log = LoggerFactory.getLogger(CmdLogDAOImpl.class);


	@Override
	public void putLog(String commandName, String commandArgs, String nickName, String commandResult)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx 	= session.getTransaction();

		try
		{
			tx.begin();

			CmdLogPOJO pojo = new CmdLogPOJO();

			pojo.setNickName(nickName);
			pojo.setCommandName(commandName);
			pojo.setCommandArgs(commandArgs);
            pojo.setExecuteResult(commandResult);
			pojo.setExecuteDate(new Date());

			session.save(pojo);

			tx.commit();
		}
        catch (HibernateException ex)
		{
			tx.rollback();
			log.error(ex.toString());
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
		}
        catch (HibernateException ex)
		{
			tx.rollback();
			log.error(ex.toString());
		}

		return null;
	}
}
