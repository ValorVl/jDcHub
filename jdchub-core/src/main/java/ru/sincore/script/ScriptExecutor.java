/*
* ScriptExecutor.java
*
* Created on 21 12 2011, 12:22
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

import java.util.concurrent.BlockingQueue;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-12-21
 */
public class ScriptExecutor extends Thread
{
    private BlockingQueue   taskQueue = null;
    private String          scriptsPath;

    private boolean isStopped = false;


    public ScriptExecutor(BlockingQueue taskQueue, String scriptsPath)
    {
        this.scriptsPath = scriptsPath;
        this.taskQueue = taskQueue;
    }


    public BlockingQueue getTaskQueue()
    {
        return taskQueue;
    }


    public void setTaskQueue(BlockingQueue taskQueue)
    {
        this.taskQueue = taskQueue;
    }


    public String getScriptsPath()
    {
        return scriptsPath;
    }


    public void setScriptsPath(String scriptsPath)
    {
        this.scriptsPath = scriptsPath;
    }


    public synchronized void stopExecution()
    {
        this.isStopped = true;
    }


    public synchronized boolean isStopped()
    {
        return isStopped;
    }

}
