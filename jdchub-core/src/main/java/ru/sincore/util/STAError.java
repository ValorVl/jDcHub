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
import ru.sincore.i18n.Messages;


/**
 * Provides a simple way to throw STA exceptions to clients
 * ( when clients send abnormal or erroneous messages ).
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov
 * @author Alenxader 'hatred' Drozdov
 * @since 2011-09-07
 */
public class STAError
{
    private static final Logger log = LoggerFactory.getLogger(STAError.class);

    private AbstractClient client           = null;
    private int            errorCode        = 0;
    private String         errorDescription = null;
    private String         prefix           = null;
    private String         flag             = null;


    /**
     * Creates a new instance of STAError
     * @param client Client wich message was
     * @param errorCode Error code
     * @param messageId Desctiption of the error
     * @throws STAException
     */
    public STAError(AbstractClient client, int errorCode, String messageId)
            throws STAException
    {
        log.debug("Hub sent to client \"" +
                  client.getNick() +
                  "\"(" +
                  client.getSid() +
                  ") error message : " +
                  messageId);

        this.client           = client;
        this.errorCode        = errorCode;
        this.errorDescription = AdcUtils.toAdcString(processText(messageId, null)).replaceAll("\\\\sTL", " TL");

    }


    /**
     * Creates a new instance of STAError
     * @param client Client wich message was
     * @param errorCode Error code
     * @param messageId Desctiption of the error
     * @param prefix
     * @param flag
     * @throws STAException
     */
    public STAError(AbstractClient client, int errorCode, String messageId, String prefix, String flag)
    {
        log.debug("Hub sent to client \"" +
                  client.getNick() +
                  "\"(" +
                  client.getSid() +
                  ") error message : \'" +
                  messageId +
                  "\' and prefix = \'" +
                  prefix +
                  "\' and flag = \'" +
                  flag +
                  "\'");

        if (prefix == null || flag == null)
        {
            throw new NullPointerException("'prefix' or 'flag' can not be null");
        }

        this.client           = client;
        this.errorCode        = errorCode;
        this.errorDescription = AdcUtils.toAdcString(processText(messageId, null));
        this.prefix           = prefix;
        this.flag             = flag;
    }


    /**
     * Creates a new instance of STAError
     * @param client Client wich message was
     * @param errorCode Error code
     * @param messageId Desctiption of the error
     * @throws STAException
     */
    public STAError(AbstractClient client, int errorCode, String messageId, Object values)
            throws STAException
    {
        log.debug("Hub sent to client \"" +
                  client.getNick() +
                  "\"(" +
                  client.getSid() +
                  ") error message : " +
                  messageId);

        this.client           = client;
        this.errorCode        = errorCode;
        this.errorDescription = AdcUtils.toAdcString(processText(messageId, values)).replaceAll("\\\\sTL", " TL");

    }


    /**
     * Creates a new instance of STAError
     * @param client Client wich message was
     * @param errorCode Error code
     * @param messageId Desctiption of the error
     * @param prefix
     * @param flag
     * @throws STAException
     */
    public STAError(AbstractClient client, int errorCode, String messageId, Object values, String prefix, String flag)
    {
        log.debug("Hub sent to client \"" +
                  client.getNick() +
                  "\"(" +
                  client.getSid() +
                  ") error message : \'" +
                  messageId +
                  "\' and prefix = \'" +
                  prefix +
                  "\' and flag = \'" +
                  flag +
                  "\'");

        if (prefix == null || flag == null)
        {
            throw new NullPointerException("'prefix' or 'flag' can not be null");
        }

        this.client           = client;
        this.errorCode        = errorCode;
        this.errorDescription = AdcUtils.toAdcString(processText(messageId, values));
        this.prefix           = prefix;
        this.flag             = flag;
    }


    private String processText(String messageId, Object values)
    {
        String localeString = null;
        String resultString;

        if (client.isExtendedFieldExists("LC"))
        {
            localeString = (String) client.getExtendedField("LC");
        }

        if (values == null)
        {
            resultString = Messages.get(messageId, localeString);
        }
        else
        {
            resultString = Messages.get(messageId, values, localeString);
        }

        return resultString;
    }


    public String rawCommand()
    {
        StringBuffer sb = new StringBuffer("ISTA ");

        if (errorCode == 0)
        {
            sb.append("000");
        }
        else
        {
            sb.append(Integer.toString(this.errorCode));
        }

        sb.append(" ");
        sb.append(errorDescription);

        if (prefix != null)
        {
            sb.append(" ");
            sb.append(prefix);
            sb.append(flag);
        }

        return sb.toString();
    }


    public void send()
            throws STAException
    {
        client.sendRawCommand(rawCommand());
        if (errorCode >= 200)
        {
            if (!ConfigurationManager.getInstance().getString(ConfigurationManager.REDIRECT_URL).isEmpty())
            {
                client.sendRawCommand("IQUI " +
                                      client.getSid() +
                                      " RD" +
                                      ConfigurationManager.getInstance()
                                                          .getString(ConfigurationManager.REDIRECT_URL));
            }
            throw new STAException(errorCode, rawCommand());
        }
    }

}
