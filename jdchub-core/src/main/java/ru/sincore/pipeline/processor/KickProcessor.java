/*
* KickProcessor.java
*
* Created on 12 12 2011, 13:27
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

package ru.sincore.pipeline.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.ClientManager;
import ru.sincore.ConfigurationManager;
import ru.sincore.adc.action.actions.MSG;
import ru.sincore.client.AbstractClient;
import ru.sincore.i18n.Messages;
import ru.sincore.pipeline.Processor;
import ru.sincore.util.ClientUtils;
import ru.sincore.util.Constants;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-12-12
 */
public class KickProcessor implements Processor<MSG>
{
    private static final Logger log = LoggerFactory.getLogger(KickProcessor.class);
    
    private Pattern pattern;

    
    @Override
    public void setMatcher(Object matcher)
    {
        this.pattern = Pattern.compile((String) matcher, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }


    @Override
    public void setParameter(String parameter)
    {
        // ignore this method call
    }


    @Override
    public void process(MSG object)
    {
        if (pattern == null)
        {
            log.debug("Matcher or parameter have not been set.");
            return;
        }
        
        String message;

        try
        {
            message = object.getMessage();
        }
        catch (Exception e)
        {
            // done processing if we can't get message
            log.debug(e.toString());
            return;
        }

        Matcher matcher = pattern.matcher(message.subSequence(0, message.length()));

        if (matcher.matches())
        {
            AbstractClient sourceClient = null;

            try
            {
                sourceClient = ClientManager.getInstance().getClientBySID(object.getSourceSID());
            }
            catch (Exception e)
            {
                log.debug(e.toString());
                return;
            }


            try
            {
                ClientUtils.kickOrBanClient(
                        ClientManager.getInstance().getClientBySID(ConfigurationManager.instance().getString(ConfigurationManager.HUB_SID)),
                        sourceClient.getNick(),
                        Constants.KICK,
                        null,
                        "Kicked by Word filter for usage forbidden word");
            }
            catch (Exception e)
            {
                log.debug(e.toString());
                return;
            }

            try
            {
                object.setMessage(Messages.get(Messages.FORBIDDEN_WORD_USAGE, sourceClient, (String)sourceClient.getExtendedField("LC")));
            }
            catch (Exception e)
            {
                log.debug(e.toString());
            }
        }

    }
}
