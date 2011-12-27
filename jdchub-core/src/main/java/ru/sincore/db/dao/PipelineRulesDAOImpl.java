/*
* StopWordsDAOImpl.java
*
* Copyright (C) 2011 Alexey 'lh' Antonov
* Copyright (C) 2011 Valor
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
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.PipelineRulesPOJO;

import java.util.List;

public class PipelineRulesDAOImpl implements PipelineRulesDAO
{
	private static final Logger log = LoggerFactory.getLogger(PipelineRulesDAOImpl.class);
	private String marker = Marker.ANY_MARKER;

    private String pipeline;
    

    public PipelineRulesDAOImpl(String pipeline)
    {
        this.pipeline = pipeline;
    }
    
    
    @Override
    public boolean addRule(String pattern, String processor, String param)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();

            PipelineRulesPOJO pojo = new PipelineRulesPOJO();

            pojo.setPipeline(pipeline);
            pojo.setMatcher(pattern);
            pojo.setProcessor(processor);
            pojo.setParam(param);

            session.save(pojo);

            tx.commit();
        }
        catch (Exception ex)
        {
            log.error(ex.toString());
            tx.rollback();
            return false;
        }

        return true;
    }


    @Override
    public boolean updateRule(PipelineRulesPOJO pojo)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();

            session.update(pojo);

            tx.commit();
        }
        catch (Exception ex)
        {
            log.error(ex.toString());
            tx.rollback();
            return false;
        }

        return true;
    }


    @Override
    public boolean deleteRule(Long id)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();

            PipelineRulesPOJO pojo = new PipelineRulesPOJO();

			pojo.setId(id);

            if (pojo != null)
            {
                session.delete(pojo);
            }

            tx.commit();
        }
        catch (Exception ex)
        {
            log.error(ex.toString());
            tx.rollback();
            return false;
        }

        return true;
    }


    @Override
    public List<PipelineRulesPOJO> getRules()
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        List<PipelineRulesPOJO> result = null;

        try
        {
            tx.begin();

            Query request = session.createQuery("from PipelineRulesPOJO where pipeline=:pipeline")
                                   .setParameter("pipeline", pipeline);
            
            result = request.list();

            tx.commit();
        }
        catch (Exception ex)
        {
            log.error(ex.toString());
            tx.rollback();
            return null;
        }

        return result;
    }


    @Override
    public List<String> getPipelines()
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        List<String> result = null;

        try
        {
            tx.begin();

            Query request = session.createQuery("select pipeline from PipelineRulesPOJO group by pipeline");

            result = request.list();

            tx.commit();
        }
        catch (Exception ex)
        {
            log.error(ex.toString());
            tx.rollback();
            return null;
        }

        return result;
    }
}
