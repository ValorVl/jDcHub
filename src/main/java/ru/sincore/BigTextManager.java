package ru.sincore;

import org.omg.CORBA.StringHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.db.dao.BigStaticDataDAO;
import ru.sincore.db.dao.BigTextDataDAOImpl;

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

	private String 	defaultLocale 		= ConfigurationManager.instance().getString(ConfigurationManager.HUB_DEFAULT_LOCALE);

    public static final String MOTD     = "MOTD";
    public static final String RULES    = "RULES";


    /**
     * Return localized bit text
     *
     * @param title Text title for search
     * @return Big text in default locale
     */
    public String getText(String title)
    {
        return this.getText(title, defaultLocale);
    }


    /**
	 * Return localized bit text
     *
	 * @param title Text title for search
     * @param locale Text locale
     * @return Localized text
	 */
	public String getText(String title, String locale)
	{
        BigStaticDataDAO bigStaticDataDAO = new BigTextDataDAOImpl();

        String text = bigStaticDataDAO.getData(title, locale);
        if (text == null || text.isEmpty())
        {
            if (!defaultLocale.equals(locale))
            {
                text = bigStaticDataDAO.getData(title, defaultLocale);
            }
        }

        if (text == null || text.isEmpty())
        {
            text = bigStaticDataDAO.getData(title, null);
        }

        if (text == null || text.isEmpty())
        {
            text = "";
        }

		return bigStaticDataDAO.getData(title, locale);
	}

}
