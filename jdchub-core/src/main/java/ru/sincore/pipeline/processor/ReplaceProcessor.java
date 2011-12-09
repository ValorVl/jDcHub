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

import ru.sincore.adc.action.actions.MSG;
import ru.sincore.pipeline.Processor;

import java.util.regex.Pattern;

/**
 * Replaces substring matching by pattern to replaceString.
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-12-08
 */
public class ReplaceProcessor implements Processor<MSG>
{
    private Pattern pattern;
    private String  replaceString;
    

    public ReplaceProcessor()
    {
    }
    
    
    @Override
    public void setMatcher(Object matcher)
    {
        this.pattern = Pattern.compile((String) matcher);
    }


    @Override
    public void setParameter(String parameter)
    {
        this.replaceString = parameter;
    }


    @Override
    public void process(MSG object)
    {
    }

}
