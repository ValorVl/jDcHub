package ru.sincore.cmd;

/*
 * jDcHub ADC HubSoft
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.StringTokenizer;
import java.util.Vector;

/**
 *  Class contains command utils methods
 *
 *  @author Valor
 */
public class CmdUtils
{

	private static final Logger log = LoggerFactory.getLogger(CmdUtils.class);
	private static String marker = Marker.ANY_MARKER;

	/**
	 * The method convert input string to array
	 * @param args src string
	 * @return Array tokens
	 */
	public static String[] strArgToArray(String args)
	{
		try{

			Vector<String> argsArray = new Vector<String>();

			StringTokenizer argsTokens = new StringTokenizer(args.trim()," ");

            boolean isInQuotes;

			while (argsTokens.hasMoreTokens())
			{
                String token = argsTokens.nextToken();

                if (token.startsWith("\""))
                {
                    StringBuilder quotedToken = new StringBuilder();
                    token = token.substring(1);

                    isInQuotes = true;
                    while (isInQuotes)
                    {
                        if (token.endsWith("\""))
                        {
                            token = token.substring(0, token.length() - 1);
                            isInQuotes = false;

                            quotedToken.append(token);
                            quotedToken.append(" ");
                        }
                        else
                        {
                            quotedToken.append(token);
                            quotedToken.append(" ");

                            if (argsTokens.hasMoreTokens())
                                token = argsTokens.nextToken();
                            else
                                isInQuotes = false;
                        }
                    }

                    isInQuotes = false;
                    token = quotedToken.toString();
                }

                argsArray.add(token);
			}

			return (String[]) argsArray.toArray();

		}catch (Exception e)
		{
			log.error(marker, e);
		}

		return null;
	}

}
