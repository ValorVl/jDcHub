/*
* ReplaceHandler.java
*
* Created on 08 12 2011, 11:55
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
import ru.sincore.adc.action.actions.MSG;
import ru.sincore.pipeline.Processor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Replaces substring matching by pattern to replaceString.
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-12-08
 */
public class ReplaceProcessor implements Processor<MSG>
{
    private static final Logger log = LoggerFactory.getLogger(ReplaceProcessor.class);

    private Pattern pattern;
    private String  replaceString;
    

    public ReplaceProcessor()
    {
    }
    
    
    @Override
    public void setMatcher(Object matcher)
    {
        // case insensitive pattern
        this.pattern = Pattern.compile((String) matcher, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }


    @Override
    public void setParameter(String parameter)
    {
        this.replaceString = parameter;
    }


    @Override
    public void process(MSG object)
    {
        if (pattern == null || replaceString == null)
        {
            log.debug("Matcher or parameter have not been set.");
            return;
        }

        String message = null;

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
        message = matcher.replaceAll(replaceString);

        try
        {
            object.setMessage(message);
        }
        catch (Exception e)
        {
            log.debug(e.toString());
        }
    }

}
