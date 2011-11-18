package ru.sincore;

import java.util.Iterator;

import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Class/file description
 *
 * @author hatred
 *         <p/>
 *         Date: 29.09.11
 *         Time: 12:48
 */
public class ConfigurationManagerTest
{
    @BeforeMethod
    public void setUp()
            throws Exception
    {
        PropertyConfigurator.configure("./etc/log4j.properties");
    }


    @Test
    public void testInstance()
            throws Exception
    {
        ConfigurationManager manager = ConfigurationManager.instance();
        System.out.println("Instance: " + manager);
    }


    @Test
    public void testLoad()
            throws Exception
    {
        ConfigurationManager manager = ConfigurationManager.instance();
        System.out.println(manager.getKeys());
        Iterator<String> iterator = manager.getKeys();
        while (iterator.hasNext())
        {
            String key = iterator.next();
            System.out.println("Key = " + key + ", value = " + manager.getProperty(key));
        }

    }


    @Test
    public void testReload()
            throws Exception
    {
        ConfigurationManager manager = ConfigurationManager.instance();
        System.out.println("Instance: " + manager);
        manager.reload();
        System.out.println(manager.getKeys());
        Iterator<String> iterator = manager.getKeys();
        while (iterator.hasNext())
        {
            String key = iterator.next();
            System.out.println("Key = " + key + ", value = " + manager.getProperty(key));
        }
    }
}
