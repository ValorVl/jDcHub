/*
* WebManagementConsole.java
*
* Created on 09 11 2011, 13:24
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

package jdchub.module;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.resource.Resource;
import ru.sincore.modules.Module;
import org.eclipse.jetty.xml.XmlConfiguration;

import java.io.IOException;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-11-09
 */
public class ModuleMain extends Module
{
    private final String moduleName = "WebManagementConsole";
    private final String moduleVersion = "0.1.0";

    private Server server = null;

    @Override
    public boolean init()
    {
        XmlConfiguration configuration = null;

        try
        {
            Resource resource = Resource.newSystemResource("./etc/config.xml");
            configuration = new XmlConfiguration(resource.getInputStream());
            server = (Server) configuration.configure();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        try
        {
            server.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        if (server.isStarted())
        {
            (new Thread(new HTTPServer(server))).start();
        }

        System.out.println("Module " + moduleName + " inited");

        return server.isRunning();
    }


    @Override
    public boolean deinit()
    {
        if (server == null)
        {
            return false;
        }

        System.out.println(server.getState());

        try
        {
            server.stop();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        System.out.println("Module " + moduleName + " deinited");
        return server.isStopped();
    }


    @Override
    public String getName()
    {
        return moduleName;
    }


    @Override
    public String getVersion()
    {
        return moduleVersion;
    }
}
