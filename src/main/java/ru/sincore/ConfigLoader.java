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

	static
	{
		HUB_CONFIG = "./etc/hub.properties";
	}

    /**
     * Hub properties
     */

    public static String HUB_LISTEN;
    public static String HUB_PORT;

    public static String HUB_NAME;
    public static String HUB_DESCRIPTION;
    public static String HUB_OWNER;

	public static String HUB_MESSAGES_FILE_DIR;
	public static String HUB_MESSAGES_LANG;

	// Chat log settings

	public static int    LAST_MESSAGES_COUNT;




    public static void init()
    {
        initHub();
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

			HUB_MESSAGES_FILE_DIR								= prop.getProperty("core.client.messages.dir","./etc/clientmessages");
			HUB_MESSAGES_LANG									= prop.getProperty("core.client.messages.lang","EN");

		 	buffInput.close();
        }
        catch (Exception e)
        {
            _log.fatal("Fatal error >>>", e);
        }
    }

}
