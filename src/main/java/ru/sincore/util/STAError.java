/*
 * STAError.java
 *
 * Created on 17 martie 2007, 11:14
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

package ru.sincore.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.ConfigurationManager;
import ru.sincore.Exceptions.STAException;
import ru.sincore.client.AbstractClient;


/**
 * Provides a simple way to throw STA exceptions to clients
 * ( when clients send abnormal or erroneous messages ).
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-09-07
 */
public class STAError
{
    private static final Logger log = LoggerFactory.getLogger(STAError.class);

    AbstractClient client;
    int     errorCode;
    String  errorDescription;


    /**
     * Creates a new instance of STAError
     * @param client Client wich message was
     * @param errorCode Error code
     * @param errorDescription Desctiption of the error
     * @throws STAException
     */
    public STAError(AbstractClient client, int errorCode, String errorDescription)
            throws STAException
    {
        log.error("Hub sent to client \"" +
                  client.getNick() +
                  "\"(" +
                  client.getSid() +
                  ") error message : " +
                  errorDescription);

        this.client = client;
        this.errorCode = errorCode;

        this.errorDescription = AdcUtils.retADCStr(errorDescription).replaceAll("\\\\sTL", " TL");
        String errorString;
        if (errorCode == 0)
        {
            errorString = "ISTA 000 " + this.errorDescription;
        }
        else
        {
            errorString = "ISTA " + Integer.toString(this.errorCode) + " " + this.errorDescription;
        }

        client.sendRawCommand(errorString);
        if (errorCode >= 200)
        {
            if (!ConfigurationManager.instance().getString(ConfigurationManager.REDIRECT_URL).isEmpty())
            {
                client.sendRawCommand("IQUI " +
                                      client.getSid() +
                                      " RD" +
                                      ConfigurationManager.instance()
                                                          .getString(ConfigurationManager.REDIRECT_URL));
            }
            throw new STAException(errorString, errorCode);
        }

    }


    /**
     * Creates a new instance of STAError
     * @param client Client wich message was
     * @param errorCode Error code
     * @param errorDescription Desctiption of the error
     * @param prefix
     * @param flag
     * @throws STAException
     */
    public STAError(AbstractClient client, int errorCode, String errorDescription, String prefix, String flag)
            throws STAException
    {
        log.error("Hub sent to client \"" +
                  client.getNick() +
                  "\"(" +
                  client.getSid() +
                  ") error message : \'" +
                  errorDescription +
                  "\' and prefix = \'" +
                  prefix +
                  "\' and flag = \'" +
                  flag +
                  "\'");

        this.client = client;
        this.errorCode = errorCode;

        this.errorDescription = AdcUtils.retADCStr(errorDescription);



        String errorString =
                "ISTA " + Integer.toString(this.errorCode) + " " +
                this.errorDescription + " " + prefix + flag;

        client.sendRawCommand(errorString);
        if (errorCode >= 200)
        {
            if (!ConfigurationManager.instance().getString(ConfigurationManager.REDIRECT_URL).isEmpty())
            {
                client.sendRawCommand("IQUI " +
                                      client.getSid() +
                                      " RD" +
                                      ConfigurationManager.instance()
                                                          .getString(ConfigurationManager.REDIRECT_URL));
            }
            throw new STAException(errorString, errorCode);
        }

    }

}
