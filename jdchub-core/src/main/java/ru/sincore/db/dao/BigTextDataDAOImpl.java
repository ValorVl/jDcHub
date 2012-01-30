package ru.sincore.db.dao;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.BigTextDataPOJO;

import java.util.List;

/**
 * @author Valor
 * @author Alexey 'lh' Antonov
 */
public class BigTextDataDAOImpl implements BigTextDataDAO
{
    private static final Logger log = LoggerFactory.getLogger(BigTextDataDAOImpl.class);

    @Override
    public boolean addData(String title, String locale, String data)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();

            BigTextDataPOJO textDataPOJO = new BigTextDataPOJO();

            textDataPOJO.setTitle(title);
            textDataPOJO.setLocale(locale);
            textDataPOJO.setData(data.getBytes());

            session.save(data);

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
    public boolean updateData(String title, String locale, String data)
    {
        if ((data == null) || (data.isEmpty()))
        {
            return false;
        }

        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();

            Criteria criteria = session.createCriteria(BigTextDataPOJO.class);
            criteria.add(Restrictions.eq("title", title));
            if (locale == null)
            {
                criteria.add(Restrictions.isNull("locale"));
            }
            else
            {
                criteria.add(Restrictions.eq("locale", locale));
            }

            BigTextDataPOJO result = (BigTextDataPOJO) criteria.uniqueResult();

            if (result == null)
            {
                return false;
            }

            result.setData(data.getBytes());

            session.update(result);

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
    public boolean deleteData(String title, String locale)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        try
        {
            tx.begin();

            Criteria criteria = session.createCriteria(BigTextDataPOJO.class);
            criteria.add(Restrictions.eq("title", title));
            if (locale == null)
            {
                criteria.add(Restrictions.isNull("locale"));
            }
            else
            {
                criteria.add(Restrictions.eq("locale", locale));
            }

            BigTextDataPOJO result = (BigTextDataPOJO) criteria.uniqueResult();

            if (result == null)
            {
                return false;
            }

            session.delete(result);

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
    public String getData(String title, String locale)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx = session.getTransaction();

        String resultText = null;

        try
        {
            tx.begin();

            Criteria criteria = session.createCriteria(BigTextDataPOJO.class);
            criteria.add(Restrictions.eq("title", title));
            if (locale == null)
            {
                criteria.add(Restrictions.isNull("locale"));
            }
            else
            {
                criteria.add(Restrictions.eq("locale", locale));
            }

            BigTextDataPOJO result = (BigTextDataPOJO) criteria.uniqueResult();

            tx.commit();

            if (result != null)
            {
                resultText = new String(result.getData());
            }

        }
        catch (Exception ex)
        {
            log.error(ex.toString());
            tx.rollback();
        }

        return resultText;
    }


    @Override
    public List<BigTextDataPOJO> listStaticData()
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx 	= session.getTransaction();

        List<BigTextDataPOJO> result = null;
        try
        {
            tx.begin();

            Query request = session.createQuery("from BigTextDataPOJO order by title,locale");

            result = (List<BigTextDataPOJO>) request.list();

            tx.commit();
        }
        catch (Exception ex)
        {
            log.error(ex.toString());
            tx.rollback();
        }

        return result;
    }


    public List<String> getLocales(String title)
    {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction tx 	= session.getTransaction();

        List<String> result = null;
        try
        {
            tx.begin();

            Criteria criteria = session.createCriteria(BigTextDataPOJO.class);
            criteria.add(Restrictions.eq("title", title));
            criteria.setProjection(Projections.distinct(Projections.property("locale")));

            result = criteria.list();

            tx.commit();
        }
        catch (Exception ex)
        {
            log.error(ex.toString());
            tx.rollback();
        }

        return result;
    }
}
