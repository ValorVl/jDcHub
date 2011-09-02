package dshub.util;

import dshub.ClientHandler;
import dshub.Exceptions.STAException;
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
import dshub.conf.Vars;

/**
 * Provides a simple way to throw STA exceptions to clients
 * ( when clients send abnormal or erroneous messages ).
 *
 * @author Pietricica
 */


public class STAError
{
    ClientHandler cur_client;
    int           error_code;
    String        error_desc;


    /**
     * Creates a new instance of STAError
     */
    public STAError(ClientHandler CH, int ec, String error_d)
            throws STAException
    {
        cur_client = CH;
        error_code = ec;

        error_desc = ADC.retADCStr(error_d).replaceAll("\\\\sTL", " TL");
        String Error_string;
        if (ec == 0)
        {
            Error_string = "ISTA 000 " + error_desc;
        }
        else
        {
            Error_string = "ISTA " + Integer.toString(error_code) + " " + error_desc;
        }

        cur_client.sendToClient(Error_string);
        if (ec >= 200)
        {
            if (!Vars.redirect_url.equals(""))
            {
                cur_client.closingwrite = cur_client.sendToClient("IQUI " +
                                                                  cur_client.SessionID +
                                                                  " RD" +
                                                                  Vars.redirect_url);
            }
            throw new STAException(Error_string, ec);
        }

    }


    public STAError(ClientHandler CH, int ec, String error_d, String Prefix, String Flag)
            throws STAException
    {
        cur_client = CH;
        error_code = ec;

        error_desc = ADC.retADCStr(error_d);


        String Error_string =
                "ISTA " + Integer.toString(error_code) + " " + error_desc + " " + Prefix + Flag;

        cur_client.sendToClient(Error_string);
        if (ec >= 200)
        {
            if (!Vars.redirect_url.equals(""))
            {
                cur_client.closingwrite = cur_client.sendToClient("IQUI " +
                                                                  cur_client.SessionID +
                                                                  " RD" +
                                                                  Vars.redirect_url);
            }
            throw new STAException(Error_string, ec);
        }

    }

}
