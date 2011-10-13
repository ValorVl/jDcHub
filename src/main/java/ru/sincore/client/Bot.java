/*
 * Bot.java
 *
 * Created on 07 october 2011, 12:15
 *
 * Copyright (C) 2011 Alexey 'lh' Antonov
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

package ru.sincore.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.util.AdcUtils;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-10-07
 */
public class Bot extends AbstractClient
{
    private static final Logger log = LoggerFactory.getLogger(Bot.class);


    @Override
    public String getINF()
    {
        StringBuilder auxstr = new StringBuilder();

        auxstr.append("BINF " + getSid() + " ID" + getCid() + " NI" + getNick());
        if (getClientType() != 0) // TODO should change.. more working here
        {
            auxstr.append(" CT");
            auxstr.append(getClientType());
        }
        if (getDescription() != null)
        {
            if (!getDescription().equals(""))
            {
                auxstr.append(" DE");
                auxstr.append(getDescription());
            }
        }


        return auxstr.toString();
    }
}
