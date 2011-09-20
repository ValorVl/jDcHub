package ru.sincore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

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
	 * @return MOTD localized text
	 */
	public String getMOTD()
	{
		return "";
	}

	public String getABOUT()
	{
		return "";
	}

}
