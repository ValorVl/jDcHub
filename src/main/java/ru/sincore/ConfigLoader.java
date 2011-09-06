package ru.sincore;

import org.apache.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

public class ConfigLoader
{

    private static final Logger _log = Logger.getLogger(ConfigLoader.class);

	private static final String HUB_CONFIG;
	private static final String HUB_MESSAGES;

	static
	{
		HUB_CONFIG = "./etc/hub.properties";
		HUB_MESSAGES = "./etc/messages/messages.properties";
	}

    /**
     * Hub properties
     */

    public static String HUB_LISTEN;
    public static String HUB_PORT;

    public static String HUB_NAME;
    public static String HUB_DESCRIPTION;
    public static String HUB_OWNER;

	// Chat log settings

	public static int    LAST_MESSAGES_COUNT;

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
        initHub();
		loadMesages();
    }


	private static void loadMesages()
	{
		File hubPropertiesFile;
		FileInputStream fileInput;
		BufferedInputStream buffInput;
		Properties prop;

        try
        {
            hubPropertiesFile 		= new File(HUB_MESSAGES);
            fileInput 				= new FileInputStream(hubPropertiesFile);
            buffInput 				= new BufferedInputStream(fileInput);
            prop 					= new Properties();

            prop.load(buffInput);

            _log.info("=== Load hub messages >>>");



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
		BufferedInputStream buffInput;
		Properties prop;

        try
        {
            hubPropertiesFile 		= new File(HUB_CONFIG);
            fileInput 				= new FileInputStream(hubPropertiesFile);
            buffInput 				= new BufferedInputStream(fileInput);
            prop 					= new Properties();

            prop.load(buffInput);

            _log.info("=== Load hub properties >>>");

			LAST_MESSAGES_COUNT									= Integer.parseInt(prop.getProperty("last.messages.count"),10);

		 	buffInput.close();
        }
        catch (Exception e)
        {
            _log.fatal("Fatal error >>>", e);
        }
    }

}
