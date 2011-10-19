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

			String[] argsArray;

			StringTokenizer argsTokens = new StringTokenizer(args.trim()," ");

			int countTokens = 0;

			argsArray = new String[argsTokens.countTokens()];

			while (argsTokens.hasMoreElements())
			{
				argsArray[countTokens++] = argsTokens.nextToken();
			}

			return argsArray;

		}catch (Exception e)
		{
			log.error(marker,e);
		}

		return null;
	}

}
