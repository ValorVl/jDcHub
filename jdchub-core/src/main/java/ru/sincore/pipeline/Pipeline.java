/*
* AbstractPipeline.java
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

package ru.sincore.pipeline;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;

/**
 * Process incoming object by applying all processors to some object.<p/>
 * One pipeline may process only one type objects.
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-12-08
 */
public class Pipeline <T>
{
    private final static Logger log = LoggerFactory.getLogger(Pipeline.class);

    protected HashMap<String, Processor<T>> processors = new HashMap<String, Processor<T>>();


    public void process(T object)
    {
        for (Processor<T> handler : this.getProcessors())
        {
            handler.process(object);
        }
    }
    
    
    public void addProcessor(String processorName, Processor processor)
    {
        if (processorName == null || processorName.isEmpty() || processorName.equals(""))
        {
            log.debug("Invalid handlerHame parameter in Pipeline#addHandler !");
            return;
        }

        if (processor == null)
        {
            log.debug("Invalid processor parameter in Pipeline#addHandler !");
            return;
        }

        processors.put(processorName, processor);
    }


    public Processor<T> getProcessor(String processorName)
    {
        return processors.get(processorName);
    }


    public Collection<Processor<T>> getProcessors()
    {
        return processors.values();
    }
}
