package ru.sincore;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Class/file description
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 03.11.11
 *         Time: 11:28
 */
public class MainTest
{
    private static final Logger log         = Logger.getLogger(MainTest.class);

    @BeforeMethod
    public void setUp()
            throws Exception
    {
        PropertyConfigurator.configure(ConfigurationManager.getInstance().getHubConfigDir() + "/log4j.properties");
    }


    @Test
    public void testExit()
            throws Exception
    {
        Main.main(new String[] {});
        Main.exit();
    }


    @Test
    public void testRestart()
            throws Exception
    {
        // Start Server getInstance
        Main.main(new String[] {});

        log.info("\n\n\nWait for restart\n\n\n");
        Thread.sleep(5000);
        Main.restart();

        log.info("\n\n\nWait for two immediately restarts\n\n\n");
        Thread.sleep(5000);
        Main.restart();
        Main.restart();

        log.info("\n\n\nWait for concurent restarts\n\n\n");
        Thread.sleep(5000);
        for (int i = 0; i < 5; i++)
        {
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    Main.restart();
                }
            }).start();
        }
    }
}
