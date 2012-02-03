/*
* ScriptExecutionPool.java
*
* Created on 20 12 2011, 10:52
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

package ru.sincore.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-12-20
 */
public class ScriptExecutionPool
{
    private static final Logger log = LoggerFactory.getLogger(ScriptExecutionPool.class);

    private BlockingQueue           taskQueue = null;
    private List<ScriptExecutor>    executors = new ArrayList<ScriptExecutor>();
    private boolean                 isStopped = false;
    

    public ScriptExecutionPool(Class clazz, String scriptsPath, int numberOfThreads, int maxNumberOfTasks)
    {
        taskQueue = new LinkedBlockingQueue(maxNumberOfTasks);


        // creating numberOfThreads executors and putting them to executors list
        Constructor executorsConstructor = null;
        try
        {
            executorsConstructor = clazz.getConstructor(BlockingQueue.class, String.class);
            executorsConstructor.setAccessible(true);
        }
        catch (NoSuchMethodException e)
        {
            log.error(e.toString());
        }

        if (executorsConstructor != null)
        {
            for (int i = 0; i < numberOfThreads; i++)
            {
                try
                {
                    executors.add( (ScriptExecutor) executorsConstructor.newInstance(taskQueue, scriptsPath));
                }
                catch (Exception e)
                {
                    log.error(e.toString());
                }
            }

            for (ScriptExecutor executor : executors)
            {
                executor.start();
            }
        }
    }


    public void execute(ScriptTask task)
    {
        if (this.isStopped)
        {
            throw new IllegalStateException("ScriptExecutionPool is stopped");
        }

        try
        {
            this.taskQueue.put(task);
        }
        catch (InterruptedException e)
        {
            log.error(e.toString());
        }
    }


    public synchronized void stop()
    {
        this.isStopped = true;

        for (ScriptExecutor executor : executors)
        {
            executor.stopExecution();
        }
    }
    
    
    public synchronized boolean isQueueEmpty()
    {
        return this.taskQueue.isEmpty();
    }
}
