package ru.sincore.util;
/*
 * AdcUtils.java
 *
 * Created on 04 martie 2007, 13:20
 *
 * DSHub AdcUtils HubSoft
 * Copyright (C) 2007,2008  Eugen Hristev
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

import ru.sincore.TigerImpl.Base32;

import java.util.regex.Pattern;

/**
 * This class is the main storage place for AdcUtils command that hub has to send.
 * Also contains functions to modify strings from normal to AdcUtils type and viceversa.
 *
 * @author Pietricica
 * @author Valor
 *
 */
abstract public class AdcUtils
{
	private static final Pattern COMPILE = Pattern.compile("\\b(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
														   "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
														   "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
														   "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\b");

	public static String fromAdcString(String adcString)
    {
        if (adcString == null)
            return null;

        return adcString.replaceAll("\\\\s", " ")
                   .replaceAll("\\\\n", "\n")
                   .replaceAll("\\\\\\\\", "\\\\")
                   .replaceAll("\\\\ ", "\\\\s")
                   .replaceAll("\\\\\\n", "\\\\n");
    }

    public static String toAdcString(String normalString)
    {
        if (normalString == null)
            return null;

        return normalString.replaceAll("\\\\", "\\\\\\\\")
                   .replaceAll(" ", "\\\\s")
                   .replaceAll("\n", "\\\\n");
    }

    public static boolean isIP(String ipString)
    {
        return COMPILE.matcher(ipString).matches();
    }

    public static boolean isCID(String cid)
    {
        if (cid.length() != 39)
        {
            return false;
        }
        try
        {
            Base32.decode(cid);
        }
        catch (IllegalArgumentException iae)
        {
            return false;
        }
        return true;

    }
}
