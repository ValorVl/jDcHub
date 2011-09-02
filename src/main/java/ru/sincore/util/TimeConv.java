/*
 * TimeConv.java
 *
 * Created on 20 octombrie 2007, 13:29
 *
 * DSHub ADC HubSoft
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

package ru.sincore.util;

import ru.sincore.Exceptions.NotCompatibleStringException;


/**
 * Basic Time intervals manangement. Converts from long millis to time format and viceversa.
 * the compatible format is : #xxxw#xxd#xxh#xxm#xxs#xx. where xxx stands for any numbers;
 * and w= weeks, d days, h hours m minutes, s seconds, . milliseconds.
 * # is a parsing delimitator. If a number is 0, it can be skipped.
 * Example : #1w#3h#55m#2s = 1 week , 0 days, 3 hours, 55 minutes 2 seconds and 0 millis.
 *
 * @author Pietricica
 */
public class TimeConv
{

    /**
     * Creates a new instance of TimeConv
     */
    public TimeConv()
    {
    }


    /**
     * this function converts from millis ( x) into a string compatible format.
     * the compatible format is : #xxxw#xxd#xxh#xxm#xxs#xx. where xxx stands for any numbers;
     * and w= weeks, d days, h hours m minutes, s seconds, . milliseconds.
     * # is a parsing delimitator. If a number is 0, it can be skipped.
     * Example : #1w#3h#55m#2s = 1 week , 0 days, 3 hours, 55 minutes 2 seconds and 0 millis.
     */
    public static String getStrTime(long x)
    {
        String ret = "";

        long weeks = x / (1000 * 60 * 60 * 24 * 7);
        if (weeks > 0L)
        {
            ret += "#" + weeks + "w";
        }
        x = x % (1000 * 60 * 60 * 24 * 7);
        int days = (int) (x / (1000 * 60 * 60 * 24));
        if (days > 0)
        {
            ret += "#" + days + "d";
        }
        x = x % (1000 * 60 * 60 * 24);
        int hours = (int) (x / (1000 * 60 * 60));
        if (hours > 0)
        {
            ret += "#" + hours + "h";
        }
        x = x % (1000 * 60 * 60);
        int minutes = (int) (x / (1000 * 60));
        if (minutes > 0)
        {
            ret += "#" + minutes + "m";
        }
        x = x % (1000 * 60);
        int seconds = (int) (x / (1000));
        if (seconds > 0)
        {
            ret += "#" + seconds + "s";
        }
        x = x % (1000);
        if (x > 0)
        {
            ret += "#" + x + ".";
        }
        return ret;
    }


    /**
     * this function converts from a string compatible format into a long millis
     */
    public static long getLongTime(String str)
            throws NotCompatibleStringException
    {
        long ret = 0;


        while (!str.equals(""))
        {
            if (str.charAt(0) != '#' || str.length() < 3)
            {
                throw new NotCompatibleStringException();
            }
            int x = str.substring(1).indexOf('#');
            if (x < 0)

            {
                x = str.length() - 1;
            }
            //else x=x-1;
            long nr = 1;
            char y = str.charAt(x);

            if (y == 'w')
            {
                nr *= 1000 * 60 * 60 * 24 * 7;
            }
            else if (y == 'd')
            {
                nr *= 1000 * 60 * 60 * 24;
            }
            else if (y == 'h')
            {
                nr *= 1000 * 60 * 60;
            }
            else if (y == 'm')
            {
                nr *= 1000 * 60;
            }
            else if (y == 's')
            {
                nr *= 1000;
            }
            else if (y == '.')
            {
                ;
            }
            else
            {
                throw new NotCompatibleStringException();
            }
            int i = 1;
            long rez = 0;
            for (; i <= x - 1; i++)
            {
                if (str.charAt(i) < '0' || str.charAt(i) > '9')
                {
                    throw new NotCompatibleStringException();
                }
                else
                {
                    rez += (str.charAt(i) - '0') * (int) (Math.pow(10, x - 1 - i));
                }
            }
            nr *= rez;
            ret += nr;

            str = str.substring(x + 1);
        }
        return ret;
    }
}
