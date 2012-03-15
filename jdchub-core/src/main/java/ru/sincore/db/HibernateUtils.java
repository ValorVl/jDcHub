package ru.sincore.db;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import ru.sincore.ConfigurationManager;

import java.io.File;
import java.util.List;

public class HibernateUtils
{
    private static final Logger log = Logger.getLogger(HibernateUtils.class);

    private static final Boolean buildingConfiguration = true;

    private static Configuration configuration = null;
    private static SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory()
    {
        try
        {
            configuration =
                    new Configuration().configure(new File(ConfigurationManager.getInstance()
                                                                               .getHubConfigDir() +
                                                           "/hibernate.cfg.xml"));
            return configuration.buildSessionFactory();
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


    /**
     * Add annotaded class to hibernate configuration
     * and build new session factory with added classes.
     *
     * @param classes List of annotated classes for adding to hibernate configuration.
     */
    public static void addAnnotatedClass(List<Class> classes)
    {
        synchronized (buildingConfiguration)
        {
            try
            {
                for (Class clazz : classes)
                {
                    configuration.addAnnotatedClass(clazz);
                }

                sessionFactory = configuration.buildSessionFactory();
            }
            catch (Throwable ex)
            {
                log.error("SessionFactory creation failed while adding annotated classes.", ex);
                throw new ExceptionInInitializerError(ex);
            }
        }
    }
}
