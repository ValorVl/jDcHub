package ru.sincore.i18n;

import org.apache.log4j.Logger;
import ru.sincore.ConfigLoader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

/**
 *  Class load all localized string messages
 *
 *  @author Valor
 */
public class Messages
{
	private static final Logger log = Logger.getLogger(Messages.class);

	private static final String SERVER_MESSAGE_FILE = "./etc/messages/servermessages.properties";
	// define server message var

	public static String SERVER_MESSAGE_STUB;
	public static String RESTART_HUB;
	public static String CLOSE_HUB;
	public static String ACCOUNT_REGISTER;
	public static String REGISTER_CID;
	public static String NOT_CID;
	public static String NO_USER;
	public static String SERVER_STARTUP;
	public static String SERVER_STARTUP_DONE;
	public static String SEARCHING_IN_PROGRESS;
	public static String CID_UNBANNED;
	public static String CID_NOT_BANNED;
	public static String USER_REGISTER;
	public static String DONE;


	//define client message var

	public static String REG_MESSAGE;

	//Empty constructor
	Messages(){}

	/**
	 * Get preconfigured messages file
	 *
	 * @return return localization filename
	 */
	private static String localePref()
	{

		String configLang = ConfigLoader.HUB_MESSAGES_LANG.toLowerCase(new Locale("en_US")); // protect config param
		String selectedLang = "en"; // by default

		File dir  = new File(ConfigLoader.HUB_MESSAGES_FILE_DIR);


		if (dir.isDirectory() && dir.exists())
		{
			for (File file : dir.listFiles())
			{
				String fileName = file.getName();
				String langToken = fileName.substring(fileName.indexOf("."));

				if (langToken.equals(configLang))
				{
					selectedLang = langToken;
				}

			}
		}
		return "messages"+selectedLang;
	}

	public static synchronized void loadClientMessages()
	{
		AtomicReference<Properties> messages = new AtomicReference<Properties>();
		File messagesFile;
		FileInputStream fileInputStream = null;
		BufferedInputStream bufferedInputStream = null;

		try
		{

			messagesFile = new File(localePref());
			fileInputStream = new FileInputStream(messagesFile);
			bufferedInputStream = new BufferedInputStream(fileInputStream);
			messages.set(new Properties());
			messages.get().load(bufferedInputStream);

			// start init messages var
			messages.get().clear();


		} catch (IOException ex)
		{
			log.error("Can not load messages file", ex);
		} finally
		{
			try
			{
				assert fileInputStream != null;
				fileInputStream.close();
				assert bufferedInputStream != null;
				bufferedInputStream.close();
			} catch (IOException ex)
			{
				log.error(ex);
			}
		}
	}

	private static void loadServerMesages()
	{
		File hubPropertiesFile;
		FileInputStream fileInput = null;
		BufferedInputStream buffInput = null;
		Properties prop;

        try
        {
            hubPropertiesFile 		= new File(SERVER_MESSAGE_FILE);
            fileInput 				= new FileInputStream(hubPropertiesFile);
            buffInput 				= new BufferedInputStream(fileInput);
            prop 					= new Properties();

			prop.clear();
            prop.load(buffInput);


			SERVER_MESSAGE_STUB		= prop.getProperty("core.server.message.stub");
			RESTART_HUB 			= prop.getProperty("core.server.message.restart_hub");
			CLOSE_HUB				= prop.getProperty("core.server.message.close_hub");
			ACCOUNT_REGISTER		= prop.getProperty("core.server.message.account_register");
			REGISTER_CID			= prop.getProperty("core.server.message.register_cid");
        }
        catch (Exception e)
        {
            log.fatal(e);
        }finally {
			try
			{
				fileInput.close();
				buffInput.close();

			}catch (IOException ex)
			{
				log.error(ex);
			}
		}
	}
}
