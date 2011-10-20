/*
 * HublistList.java
 *
 * Created on 22 ianuarie 2008, 12:52
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

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author Pietricica
 */
public class HublistList implements Serializable
{
    
    LinkedList<Hublist> hList;
    
    public HublistList()
    {
        hList=new LinkedList<Hublist>();
        Hublist hubtracker=new Hublist();
        hubtracker.URL="hublist.hubtracker.com:3639";
        hubtracker.Website="www.hubtracker.com";
        hList.add(hubtracker);
    }
    
    public void add(String URL)
    {
        Hublist newH=new Hublist();
        newH.URL=URL;
        hList.add(newH);
        
        PluginMain.rewriteList();
    }
    
    public boolean rem(String URL)
    {
        for( Hublist hublist: hList)
          if(hublist.URL.equals(URL))
          {
              hList.remove(hublist);
              PluginMain.rewriteList();
              return true;
          }
        return false;
    }
    
    public boolean mod ( String oldURL, String newURL)
    {
        for( Hublist hublist: hList)
          if(hublist.URL.equals(oldURL))
          {
              hublist.URL=newURL;
              PluginMain.rewriteList();
              return true;
          }
        return false;
    }
    
}
