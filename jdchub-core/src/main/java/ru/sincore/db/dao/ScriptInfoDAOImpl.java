/*
* ScriptInfoDAOImpl.java
*
* Created on 03 02 2012, 10:20
*
* Copyright (C) 2012 Alexey 'lh' Antonov
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

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.ScriptInfoPOJO;

import java.util.List;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-03
 */
public class ScriptInfoDAOImpl implements ScriptInfoDAO
{
    private static final Logger log = LoggerFactory.getLogger(ModuleListDAOImpl.class);


    @Override
    public boolean addScriptInfo(ScriptInfoPOJO script)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();
            session.save(script);
            tx.commit();

            log.debug("[ADD] Info about script \'" + script.getName() + "\' stored to db.");

            return true;
        }
        catch (Exception e)
        {
            tx.rollback();
            log.error(e.toString());
        }

        return false;
    }


    @Override
    public boolean addScriptInfo(String name)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();
            
            ScriptInfoPOJO script = new ScriptInfoPOJO();
            script.setName(name);
            
            session.save(script);
            
            tx.commit();

            log.debug("[ADD] Info about script \'" + script.getName() + "\' stored to db.");

            return true;
        }
        catch (Exception e)
        {
            tx.rollback();
            log.error(e.toString());
        }

        return false;
    }


    @Override
    public boolean updateScriptInfo(ScriptInfoPOJO script)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();
            session.save(script);
            tx.commit();

            log.debug("[UPDATE] Info about script \'" + script.getName() + "\' updated.");

            return true;
        }
        catch (Exception e)
        {
            tx.rollback();
            log.error(e.toString());
        }

        return false;
    }


    @Override
    public boolean deleteScriptInfo(String name)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();

            Query query = session.createQuery("delete from ScriptInfoPOJO where name = :name").setParameter("name", name);
            query.executeUpdate();

            tx.commit();

            log.debug("[DELETE] Info about script \'" + name + "\' deleted.");

            return true;
        }
        catch (Exception e)
        {
            tx.rollback();
            log.error(e.toString());
        }

        return false;
    }


    @Override
    public List<ScriptInfoPOJO> getScriptInfoList()
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();

            Query query = session.createQuery("from ScriptInfoPOJO ");

            List<ScriptInfoPOJO> result = query.list();

            tx.commit();

            return result;
        }
        catch (Exception e)
        {
            tx.rollback();
            log.error(e.toString());
        }

        return null;
    }


    @Override
    public ScriptInfoPOJO getScriptInfo(String name)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();

            Criteria criteria = session.createCriteria(ScriptInfoPOJO.class);
            criteria.add(Restrictions.eq("name", name));

            ScriptInfoPOJO script = (ScriptInfoPOJO) criteria.uniqueResult();

            tx.commit();

            return script;
        }
        catch (Exception e)
        {
            tx.rollback();
            log.error(e.toString());
        }

        return null;
    }
}
