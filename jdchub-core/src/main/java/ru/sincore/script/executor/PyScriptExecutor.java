/*
* ScriptExecutor.java
*
* Created on 19 12 2011, 16:33
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

package ru.sincore.script.executor;

import org.python.core.*;
import org.python.util.PythonInterpreter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.script.ScriptExecutor;
import ru.sincore.script.ScriptTask;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-12-19
 */
public class PyScriptExecutor extends ScriptExecutor
{
    private static final Logger log = LoggerFactory.getLogger(PyScriptExecutor.class);
    
    private PythonInterpreter interpreter;
    private PySystemState state;


    public PyScriptExecutor(BlockingQueue taskQueue, String scriptsPath)
    {
        super(taskQueue, scriptsPath);

        this.state = Py.getSystemState();
        this.state.path.insert(0, new PyString(scriptsPath));
        this.interpreter = new PythonInterpreter(null, state);
    }
    

    @Override
    public void run()
    {
        while(!isStopped())
        {
            try
            {
                // if task queue is empty, waiting 1ms and check queue again
                if (getTaskQueue().isEmpty())
                {
                    try
                    {
                        Thread.sleep(1);
                    }
                    catch (InterruptedException e)
                    {
                        // ignore
                    }

                    continue;
                }

                // getting task
                ScriptTask task = (ScriptTask) getTaskQueue().take();

                log.debug("Executing script \'" + task.getScriptName() + "\'");

                // set script args
                PyList args = new PyList();
                // set up script name as first argv parameter
                args.append(new PyString(task.getScriptName()));

                // set up another argv parameters
                for (Object arg : task.getArgs())
                {
                    args.append((PyObject) arg);
                }

                this.state.argv = args;

                // set up script's local variables
                for (Map.Entry<String, Object> variable : task.getLocalVariables().entrySet())
                {
                    this.interpreter.set(variable.getKey(), variable.getValue());
                }

                // execute script
                this.interpreter.execfile(getScriptsPath() + task.getScriptName());
            }
            catch (Exception e)
            {
                log.error(e.toString());
            }

        }
    }
}
