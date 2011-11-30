/*
 * BigTextManager.java
 *
 * Copyright (C) 2011 Alexey 'lh' Antonov
 * Copyright (C) 2011 Alexander 'hatred' Drozdov
 * Copyright (C) 2011 Valor
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package ru.sincore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.db.dao.BigStaticDataDAO;
import ru.sincore.db.dao.BigTextDataDAOImpl;
import ru.sincore.db.pojo.BigTextDataPOJO;

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
    public static final String TOPIC    = "TOPIC";


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

        if ((text == null || text.isEmpty()) && locale != null)
        {
            text = bigStaticDataDAO.getData(title, null);
        }

        if (text == null || text.isEmpty())
        {
            text = "";
        }

		return text;
	}


    public boolean setText(String title, String text)
    {
        return this.setText(title, text, defaultLocale);
    }


    public boolean setText(String title, String text, String locale)
    {
        if ((title == null || title.isEmpty() || title.equals("")) ||
            (text == null || text.isEmpty() || text.equals("")))
        {
            return false;
        }

        if (locale == null || locale.isEmpty() || locale.equals(""))
        {
            locale = defaultLocale;
        }

        BigStaticDataDAO bigStaticDataDAO = new BigTextDataDAOImpl();

        return bigStaticDataDAO.updateData(title, locale, text) ||
               bigStaticDataDAO.addData(title, locale, text);

    }
}
