/*
* ModuleListDAOImpl.java
*
* Created on 21 11 2011, 15:06
*
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

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.ModuleListPOJO;

import java.util.List;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-11-21
 */
public class ModuleListDAOImpl implements ModuleListDAO
{
    private static final Logger log = LoggerFactory.getLogger(ModuleListDAOImpl.class);


    @Override
    public boolean addModule(ModuleListPOJO module)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();
            session.save(module);
            tx.commit();

            log.debug("Module " + module.getName() + " stored to db.");

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
    public boolean addModule(String name, boolean enabled)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();

            ModuleListPOJO pojo = new ModuleListPOJO();

            pojo.setName(name);
            pojo.setEnabled(enabled);

            session.save(pojo);
            tx.commit();

            log.debug("Module " + name + " stored to db.");

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
    public boolean updateModule(ModuleListPOJO module)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();
            session.update(module);
            tx.commit();

            log.debug("Module " + module.getName() + " updated");

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
    public boolean deleteModule(String name)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();

            Query query = session.createQuery("delete from ModuleListPOJO where name = :name").setParameter("name", name);

            query.executeUpdate();

            tx.commit();

            log.debug("Module " + name + " removed from db.");

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
    public List<ModuleListPOJO> getModuleList()
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();

            Query query = session.createQuery("from ModuleListPOJO ");

            List<ModuleListPOJO> result = query.list();

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
    public ModuleListPOJO getModule(String name)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();

            Query query = session.createQuery("from ModuleListPOJO where name = :name")
                                 .setParameter("name", name);

            ModuleListPOJO result = (ModuleListPOJO) query.uniqueResult();

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
}
