package ru.sincore;

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ConfigLoader
{

    private static final Logger _log = Logger.getLogger(ConfigLoader.class);

    private static final String DATABASE_CONFIG;
	private static final String HUB_CONFIG;

	static
	{
		HUB_CONFIG = "./etc/hub.properties";
		DATABASE_CONFIG = "./etc/database.properties";
	}

	/**
     * Database connectivity properties
     */

    public static String  DB_CONNECTION_DSN;
	public static String  DB_ENGINE;
	public static String  DB_DIALECT;
    public static String  DB_USER_NAME;
    public static String  DB_PASSPWORD;
    public static int     DB_PULL_MIN;
    public static int     DB_PULL_MAX;
    public static int 	  DB_TIMEOUT;

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


    public static void init()
    {
		initDb();
        initHub();
    }


    private static void initDb()
    {
		File databasePropertiesFile;
		FileInputStream fileInput;
		BufferedInputStream buffInput;
		Properties prop;

		try
		{
			databasePropertiesFile = new File(DATABASE_CONFIG);
			fileInput = new FileInputStream(databasePropertiesFile);
			buffInput = new BufferedInputStream(fileInput);
			prop = new Properties();

			prop.load(buffInput);

			_log.info("=== Load database properties >>>");

			DB_CONNECTION_DSN 									= prop.getProperty("database.dsn");
			DB_ENGINE											= prop.getProperty("database.driver.class");
			DB_DIALECT											= prop.getProperty("database.dialect");
			DB_USER_NAME 										= prop.getProperty("database.user");
			DB_PASSPWORD 										= prop.getProperty("database.password");
			DB_PULL_MIN 										= Integer.parseInt(prop.getProperty("database.pool.min"));
			DB_PULL_MAX 										= Integer.parseInt(prop.getProperty("database.pool.max"));
			DB_TIMEOUT											= Integer.parseInt(prop.getProperty("database.pool.timeout"));

			buffInput.close();
		}
		catch (Exception e)
		{
			_log.fatal("Fatal error >>>", e);
		}

    }


    private static void initHub()
    {
		File hubPropertiesFile;
		FileInputStream fileInput;
		BufferedInputStream buffInput = null;
		Properties prop;

        try
        {
            hubPropertiesFile 		= new File(HUB_CONFIG);
            fileInput 				= new FileInputStream(hubPropertiesFile);
            buffInput 				= new BufferedInputStream(fileInput);
            prop 					= new Properties();

            prop.load(buffInput);

            _log.info("=== Load hub properties >>>");



        }
        catch (Exception e)
        {
            _log.fatal("Fatal error >>>", e);
        }finally {
			try{
				if (buffInput.available() > 0)
				{
					buffInput.close();
				}
			}catch (Exception ex)
			{
				_log.fatal(ex);
			}
		}
    }

}
