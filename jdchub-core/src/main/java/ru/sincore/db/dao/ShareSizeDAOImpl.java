/*
* ShareSizeDAOImpl.java
*
* Created on 07 02 2012, 14:43
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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.ShareSizePOJO;

import java.util.Date;
import java.util.List;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-07
 */
public class ShareSizeDAOImpl implements ShareSizeDAO
{
    private static final Logger log = LoggerFactory.getLogger(ShareSizeDAOImpl.class);


    @Override
    public boolean add(Long shareSize)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();

            ShareSizePOJO shareSizePOJO = new ShareSizePOJO();
            shareSizePOJO.setShareSize(shareSize);

            session.save(shareSizePOJO);

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
    public List<ShareSizePOJO> getShareSizeInDateRange(Date start, Date end)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        List<ShareSizePOJO> result = null;

        try
        {
            tx.begin();

            Criteria criteria = session.createCriteria(ShareSizePOJO.class);
            criteria.add(Restrictions.between("timestamp", start, end));

            result = (List<ShareSizePOJO>) criteria.list();

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


    @Override
    public Long getMaxShareSize()
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        Long result;

        try
        {
            tx.begin();

            Criteria criteria = session.createCriteria(ShareSizePOJO.class);
            criteria.setProjection(Projections.max("shareSize"));

            result = (Long) criteria.uniqueResult();

            tx.commit();
        }
        catch (HibernateException e)
        {
            tx.rollback();
            log.error(e.toString());
            return 0L;
        }

        return result;
    }
}
