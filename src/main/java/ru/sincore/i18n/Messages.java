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


	// define message var

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

	public static synchronized void load()
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
}
