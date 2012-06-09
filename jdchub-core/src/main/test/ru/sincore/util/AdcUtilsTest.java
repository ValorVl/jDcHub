/*
* AdcUtilsTest.java
*
* Created on 09 06 2012, 12:52
*
* Copyright (C) 2012 Alexey 'lh' Antonov
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

package ru.sincore.util;

import org.testng.annotations.Test;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-06-09
 */
public class AdcUtilsTest
{
    @Test
    public void testFromAdcString()
            throws Exception
    {
        String result = AdcUtils.fromAdcString("simple\\sstring\\swith\\sspaces");
        if (!result.equals("simple string with spaces"))
        {
            System.out.println(result);
            throw new AssertionError();
        }

        result = AdcUtils.fromAdcString("simple\\nstring\\nwith\\nline\\nbreaks");
        if (!result.equals("simple\nstring\nwith\nline\nbreaks"))
        {
            System.out.println(result);
            throw new AssertionError();
        }

        result = AdcUtils.fromAdcString("simple\\\\string\\\\with\\\\backslashes");
        if (!result.equals("simple\\string\\with\\backslashes"))
        {
            System.out.println(result);
            throw new AssertionError();
        }

        result = AdcUtils.fromAdcString("string\\s\\nwith\\s\\nspaces\\s\\nand\\s\\nline\\s\\nbreaks");
        if (!result.equals("string \nwith \nspaces \nand \nline \nbreaks"))
        {
            System.out.println(result);
            throw new AssertionError();
        }

        result = AdcUtils.fromAdcString("string\\s\\\\space\\s\\\\backslashes");
        if (!result.equals("string \\space \\backslashes"))
        {
            System.out.println(result);
            throw new AssertionError();
        }

        result = AdcUtils.fromAdcString("string\\\\\\nbackslashes\\\\\\nbreaks");
        if (!result.equals("string\\\nbackslashes\\\nbreaks"))
        {
            System.out.println(result);
            throw new AssertionError();
        }

        result = AdcUtils.fromAdcString("string\\s\\\\\\nbackslashes\\s\\\\\\nbreaks");
        if (!result.equals("string \\\nbackslashes \\\nbreaks"))
        {
            System.out.println(result);
            throw new AssertionError();
        }

        result = AdcUtils.fromAdcString("string\\n\\s\\\\\\\\\\s\\nbackslashes\\n\\\\\\\\\\nbreaks");
        if (!result.equals("string\n \\\\ \nbackslashes\n\\\\\nbreaks"))
        {
            System.out.println(result);
            throw new AssertionError();
        }

        result = AdcUtils.fromAdcString("string\\n\\s\\\\\\\\\\\\\\s\\nbackslashes\\n\\\\\\\\\\\\\\nbreaks");
        if (!result.equals("string\n \\\\\\ \nbackslashes\n\\\\\\\nbreaks"))
        {
            System.out.println(result);
            throw new AssertionError();
        }
    }


    @Test
    public void testToAdcString()
            throws Exception
    {
        String result = AdcUtils.toAdcString("simple string with spaces");
        if (!result.equals("simple\\sstring\\swith\\sspaces"))
        {
            throw new AssertionError();
        }

        result = AdcUtils.toAdcString("simple\nstring\nwith\nline\nbreaks");
        if (!result.equals("simple\\nstring\\nwith\\nline\\nbreaks"))
        {
            throw new AssertionError();
        }

        result = AdcUtils.toAdcString("simple\\string\\with\\backslashes");
        if (!result.equals("simple\\\\string\\\\with\\\\backslashes"))
        {
            throw new AssertionError();
        }

        result = AdcUtils.toAdcString("string \nwith \nspaces \nand \nline \nbreaks");
        if (!result.equals("string\\s\\nwith\\s\\nspaces\\s\\nand\\s\\nline\\s\\nbreaks"))
        {
            throw new AssertionError();
        }

        result = AdcUtils.toAdcString("string \\space \\backslashes");
        if (!result.equals("string\\s\\\\space\\s\\\\backslashes"))
        {
            throw new AssertionError();
        }

        result = AdcUtils.toAdcString("string\\\nbackslashes\\\nbreaks");
        if (!result.equals("string\\\\\\nbackslashes\\\\\\nbreaks"))
        {
            throw new AssertionError();
        }

        result = AdcUtils.toAdcString("string \\\nbackslashes \\\nbreaks");
        if (!result.equals("string\\s\\\\\\nbackslashes\\s\\\\\\nbreaks"))
        {
            throw new AssertionError();
        }

        result = AdcUtils.toAdcString("string\n \\\\ \nbackslashes\n\\\\\nbreaks");
        if (!result.equals("string\\n\\s\\\\\\\\\\s\\nbackslashes\\n\\\\\\\\\\nbreaks"))
        {
            throw new AssertionError();
        }

        result = AdcUtils.toAdcString("string\n \\\\\\ \nbackslashes\n\\\\\\\nbreaks");
        if (!result.equals("string\\n\\s\\\\\\\\\\\\\\s\\nbackslashes\\n\\\\\\\\\\\\\\nbreaks"))
        {
            throw new AssertionError();
        }
    }
}
