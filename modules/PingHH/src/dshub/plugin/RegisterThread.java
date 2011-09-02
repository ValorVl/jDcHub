/*
 * RegisterThread.java
 *
 * Created on 22 ianuarie 2008, 14:16
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

package dshub.plugin;

import ru.sincore.ClientHandler;
import dshub.Vars;

import javax.swing.JOptionPane;

/**
 *
 * @author Pietricica
 */
public class RegisterThread extends Thread
{
    ClientHandler cur_client=null;
    public RegisterThread()
    {
        start();
    }
    public RegisterThread(ClientHandler cur_client)
    {
        this.cur_client=cur_client;
        start();
    }
    
    public void run()
    {
        for( Hublist h: PluginMain.curlist.hList)
        {
            h.status=h.register()?"OK":"FAILED";
        }
        if(cur_client==null)
        {
        JOptionPane.showMessageDialog(null,"Hublist Registration finished.",
                    Vars.HubName,JOptionPane.INFORMATION_MESSAGE);
        }
        else
        {
            if(cur_client!=null)
            cur_client.sendFromBot("Hublist Registration finished.");
        }
        if(PluginMain.pframe!=null)
             PluginMain.pframe.refresh();
    }

}
