package ru.sincore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.io.File;

/**
 * This class manages the preparation for sending the customer a very large blocks of text,
 * such as text files from MOTD, RULES, etc ..
 *
 * @author Valor
 */
public class BigTextManager
{
	private static final Logger log = LoggerFactory.getLogger(BigTextManager.class);
	private String 	marker 			= Marker.ANY_MARKER;

	private int 	storageType 		= ConfigLoader.BIG_FILE_STORAGE;
	private String 	defaultLocale 		= ConfigLoader.HUB_DEFAULT_LOCALE;
	private String	fileStorageLocation = ConfigLoader.FILE_STORAGE_LOCATION;

	/**
	 * A method prepare MOTD text block
	 * @param client client handler, if null will send MOTD in default locale
	 * @return MOTD localized text
	 */
	public String getMOTD(Client client)
	{
		String motd = "Hell is here";



		if (storageType == 0)
		{
			File file = new File("");
		}
		else if (storageType == 1)
		{

		}

		return motd;
	}

	public String getABOUT()
	{
		return "";
	}

}
