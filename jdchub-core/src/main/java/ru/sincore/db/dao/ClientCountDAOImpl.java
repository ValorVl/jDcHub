/*
* ClientCountDAOImpl.java
*
* Created on 07 02 2012, 14:19
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
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.ClientCountPOJO;

import java.util.Date;
import java.util.List;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-07
 */
public class ClientCountDAOImpl implements ClientCountDAO
{
    private static final Logger log = LoggerFactory.getLogger(ClientCountDAOImpl.class);
    
    
    @Override
    public boolean addEntry(Long count)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();
            
            ClientCountPOJO clientCountPOJO = new ClientCountPOJO();
            clientCountPOJO.setCount(count);
            
            session.save(clientCountPOJO);

            tx.commit();
        }
        catch (HibernateException e)
        {
            log.error(e.toString());
            tx.rollback();
            return false;
        }
        
        return true;
    }


    @Override
    public List<ClientCountPOJO> getEntriesInDateRange(Date start, Date end)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        List<ClientCountPOJO> result = null;
        
        try
        {
            tx.begin();

            Criteria criteria = session.createCriteria(ClientCountPOJO.class);
            criteria.add(Restrictions.between("timestamp", start, end));
            
            result = (List<ClientCountPOJO>) criteria.list();
            
            tx.commit();
        }
        catch (HibernateException e)
        {
            log.error(e.toString());
            tx.rollback();
            return null;
        }
        
        return result;
    }
}
