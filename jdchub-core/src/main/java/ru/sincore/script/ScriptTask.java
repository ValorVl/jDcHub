/*
* ScriptTask.java
*
* Created on 19 12 2011, 17:19
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

import org.python.core.PyList;
import org.python.core.PyObject;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-12-19
 */
public class ScriptTask
{
    private String scriptName;
    private String engineType;
    private List args;
    private Map<String, Object> localVariables;
    
    public ScriptTask()
    {
        this.args = new LinkedList();
        this.localVariables = new HashMap<String, Object>();
    }

    
    public String getScriptName()
    {
        return this.scriptName;
    }
    

    public void setScriptName(String name)
    {
        this.scriptName = name;
    }


    public String getEngineType()
    {
        return engineType;
    }


    public void setEngineType(String engineType)
    {
        this.engineType = engineType;
    }


    public List getArgs()
    {
        return this.args;
    }
    
    
    public void addArg(Object arg)
    {
        this.args.add(arg);
    }
    
    
    public Map<String, Object> getLocalVariables()
    {
        return this.localVariables;
    }
    
    
    public void setLocalVariable(String name, PyObject value)
    {
        this.localVariables.put(name, value);
    }
}
