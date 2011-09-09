package ru.sincore.util;

import ru.sincore.Client;
import ru.sincore.ConfigLoader;
import ru.sincore.Exceptions.STAException;

/*
 * STAError.java
 *
 * Created on 17 martie 2007, 11:14
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
    Client  client;
    int     errorCode;
    String  errorDescription;


    /**
     * Creates a new instance of STAError
     * @param client Client wich message was
     * @param errorCode Error code
     * @param errorDescription Desctiption of the error
     * @throws STAException
     */
    public STAError(Client client, int errorCode, String errorDescription)

            throws STAException
    {
        this.client = client;
        this.errorCode = errorCode;

        this.errorDescription = ADC.retADCStr(errorDescription).replaceAll("\\\\sTL", " TL");
        String errorString;
        if (errorCode == 0)
        {
            errorString = "ISTA 000 " + this.errorDescription;
        }
        else
        {
            errorString = "ISTA " + Integer.toString(this.errorCode) + " " + this.errorDescription;
        }

        client.getClientHandler().sendToClient(errorString);
        if (errorCode >= 200)
        {
            if (!ConfigLoader.REDIRECT_URL.isEmpty())
            {
                client.getClientHandler().closingwrite =
                        client.getClientHandler().sendToClient("IQUI " +
                                                               client.getClientHandler().SessionID +
                                                               " RD" +
                                                               ConfigLoader.REDIRECT_URL);
            }
            throw new STAException(errorString, errorCode);
        }

    }


    /**
     * Creates a new instance of STAError
     * @param client Client wich message was
     * @param errorCode Error code
     * @param errorDescription Desctiption of the error
     * @param Prefix
     * @param Flag
     * @throws STAException
     */
    public STAError(Client client, int errorCode, String errorDescription, String Prefix, String Flag)
            throws STAException
    {
        this.client = client;
        this.errorCode = errorCode;

        this.errorDescription = ADC.retADCStr(errorDescription);



        String errorString =
                "ISTA " + Integer.toString(this.errorCode) + " " +
                this.errorDescription + " " + Prefix + Flag;

        client.getClientHandler().sendToClient(errorString);
        if (errorCode >= 200)
        {
            if (!ConfigLoader.REDIRECT_URL.isEmpty())
            {
                client.getClientHandler().closingwrite =
                        client.getClientHandler().sendToClient("IQUI " +
                                                               client.getClientHandler().SessionID +
                                                               " RD" +
                                                               ConfigLoader.REDIRECT_URL);
            }
            throw new STAException(errorString, errorCode);
        }

    }

}
