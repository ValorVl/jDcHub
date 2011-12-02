package ru.sincore.db;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.sincore.ConfigurationManager;

import java.io.File;

public class HibernateUtils
{
    private static final Logger log = Logger.getLogger(HibernateUtils.class);

    private static final SessionFactory sessionFactory = buildSessionFactory();


    private static SessionFactory buildSessionFactory()
    {
        try
        {
            return new Configuration().configure(new File(ConfigurationManager.instance()
                                                                              .getHubConfigDir() +
                                                          "/hibernate.cfg.xml"))
                                      .buildSessionFactory();
        }
        catch (Throwable ex)
        {
            log.error("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }


    public static SessionFactory getSessionFactory()
    {
        return sessionFactory;
    }
}
