package ru.sincore;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;

public class ConfigLoader
{

    private static final Logger _log = Logger.getLogger(ConfigLoader.class);

    private static String DATABASE_CONFIG = "./etc/database.properties";
    private static String HUB_CONFIG      = "./etc/hub.properties";

    /**
     * Database connectivity properties
     */

    public static URL     DB_CONNECTION_DSN;
    public static String  DB_USER_NAME;
    public static String  DB_PASSPWORD;
    public static int     DB_PORT;
    public static int     DB_PULL_MIN;
    public static int     DB_PULL_MAX;
    public static boolean DB_KEEP_ALIVE;

    /**
     * Hub properties
     */

    public static String HUB_LISTEN;
    public static String HUB_PORT;

    public static String HUB_NAME;
    public static String HUB_DESCRIPTION;
    public static String HUB_OWNER;

    /**
     * ADC properties
     */

    public static int BMSG;
    public static int DMSG;
    public static int EMSG;
    public static int FMSG;
    public static int HMSG;

    public static int BSTA;
    public static int DSTA;
    public static int ESTA;
    public static int FSTA;
    public static int HSTA;

    public static int BCTM;
    public static int DCTM;
    public static int ECTM;
    public static int FCTM;
    public static int HCTM;

    public static int BRCM;
    public static int DRCM;
    public static int ERCM;
    public static int FRCM;
    public static int HRCM;

    public static int BINF;
    public static int DINF;
    public static int EINF;
    public static int FINF;
    public static int HINF;

    public static int BSCH;
    public static int DSCH;
    public static int ESCH;
    public static int FSCH;
    public static int HSCH;

    public static int BRES;
    public static int DRES;
    public static int ERES;
    public static int FRES;
    public static int HRES;

    public static int BPAS;
    public static int DPAS;
    public static int EPAS;
    public static int FPAS;
    public static int HPAS;

    public static int BSUP;
    public static int DSUP;
    public static int ESUP;
    public static int FSUP;
    public static int HSUP;


    public ConfigLoader()
    {
    }


    public static void init()
    {
        initDb();
        initHub();
    }


    private static void initDb()
    {

        try
        {
            File databasePropertiesFile = new File(DATABASE_CONFIG);
            FileInputStream fileInput = new FileInputStream(
                    databasePropertiesFile);
            BufferedInputStream buffInput = new BufferedInputStream(fileInput);
            Properties prop = new Properties();

            prop.load(buffInput);

            _log.info("=== Load database properties >>>");

            DB_CONNECTION_DSN = new URL(prop.getProperty(""));
            DB_USER_NAME = prop.getProperty("");
            DB_PASSPWORD = prop.getProperty("");
            DB_PORT = Integer.parseInt(prop.getProperty(""));
            DB_PULL_MIN = Integer.parseInt(prop.getProperty(""));
            DB_PULL_MAX = Integer.parseInt(prop.getProperty(""));
            DB_KEEP_ALIVE = Boolean.valueOf(prop.getProperty(""));

            buffInput.close();
        }
        catch (Exception e)
        {
            _log.fatal("Fatal error >>>", e);
        }

    }


    private static void initHub()
    {

        try
        {
            File databasePropertiesFile = new File(HUB_CONFIG);
            FileInputStream fileInput = new FileInputStream(
                    databasePropertiesFile);
            BufferedInputStream buffInput = new BufferedInputStream(fileInput);
            Properties prop = new Properties();

            prop.load(buffInput);

            _log.info("=== Load hub properties >>>");

            buffInput.close();
        }
        catch (Exception e)
        {
            _log.fatal("Fatak error >>>", e);
        }
    }

}
