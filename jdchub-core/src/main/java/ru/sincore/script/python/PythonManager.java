/*
 * PythonManager.java
 *
 * Created on 18 februarie 2008, 13:24
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

package ru.sincore.script.python;

import org.python.util.PythonInterpreter;
import ru.sincore.Main;

import java.io.File;
import java.util.LinkedList;

/**
 * @author Pietricica
 */
public class PythonManager
{
    public static PythonInterpreter Interpretor;

    public static LinkedList<PythonScript> scripts;


    static
    {
        scripts = new LinkedList<PythonScript>();
    }


    public PythonManager()
    {
        Interpretor = new PythonInterpreter();
        /*  Properties props = new Properties();
                 props.setProperty("jython.path", Main.myPath);
                 PythonInterpreter.initialize(System.getProperties(), props,
                                              new String[] {""});
        */
        //interp.execfile("g:\\test.py");
        rescanScripts();
    }


    public void rescanScripts()
    {
        scripts.clear();
        File dir = new File(Main.myPath + "/scripts/py");
        if (!dir.exists())
        {
            dir.mkdir();
        }
        String[] scriptlist = dir.list();
        if (scriptlist == null)
        {
            return;
        }
        for (String name : scriptlist)
        {
            PythonScript newscript = new PythonScript(name);
            newscript.Start();
            scripts.add(newscript);
        }
    }
}
