/*
 * PythonScript.java
 *
 * Created on 18 februarie 2008, 13:28
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

package dshub.python;

import dshub.*;

/**
 * @author Pietricica
 */
public class PythonScript extends Thread
{
    private boolean active;
    private boolean ok;

    private String name;


    public String getScriptName()
    {
        return name;
    }


    public void setActive(boolean active)
    {
        this.active = active;
    }


    public void setOk(boolean ok)
    {
        this.ok = ok;
    }


    public boolean isActive()
    {
        return active;
    }


    public boolean isOk()
    {
        return ok;
    }


    public PythonScript(String name)
    {
        this.name = name;
        setActive(true);
        setOk(true);

    }


    public void Start()
    {
        start();

    }


    public void run()
    {
        try
        {
            PythonManager.Interpretor.execfile(Main.myPath + "/scripts/py/" + this.getScriptName());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
