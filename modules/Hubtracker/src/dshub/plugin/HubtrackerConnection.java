/*
 * HubtrackerConnection.java
 *
 * Created on 29 noiembrie 2007, 17:33
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

import dshub.Vars;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.Proxy;

/**
 *
 * @author Pietricica
 */
public class HubtrackerConnection extends Thread
{
    String user,pass,e_mail;
    HubtrackerCmd curCmd;
    private boolean done=false;
    /** Creates a new instance of HubtrackerConnection */
    public HubtrackerConnection(HubtrackerCmd curCmd,String user, String pass,String e_mail)
    {
        this.user=user;
        this.e_mail=e_mail;
        this.curCmd=curCmd;
        this.pass=pass;
         //curCmd.handler.sendFromBot("ok now");
        start();
    }
    public void run ()
    {
        
        BufferedReader inp = null;
        try 
        {
          String urlString = "http://www.hubtracker.com/query.php?action=add&username="+user+"&password="+pass+"&email="+e_mail+"&address="+Vars.Hub_Host;
        
          URL url = new URL(urlString);
          URLConnection conn;
          if(!Vars.Proxy_Host.equals(""))
              
           conn = url.openConnection(new Proxy(Proxy.Type.HTTP,new InetSocketAddress(Vars.Proxy_Host,Vars.Proxy_Port)));
          else
              conn=url.openConnection();
          
          conn.setDoInput(true); // or 
          conn.setDoOutput(true);
         
         // System.out.println("ok now");
          /* really open connection */
          conn.connect(); // establish connection
          
          inp = new BufferedReader(
                  new InputStreamReader(conn.getInputStream()));
          String xx ;
          while((xx= inp.readLine())!=null)
              PluginMain.result+="\n"+xx;
          
          if(curCmd!=null)
              
              this.curCmd.cur_client.sendFromBot("[hubtracker:] "+PluginMain.result);
          else
              PluginMain.curFrame.showMsg();
          
         // System.out.println(result);
          inp.close(); 
          inp = null;
        }
        catch (MalformedURLException ue) 
        {
            PluginMain.result=ue.toString();
        }
        catch (Exception e)
        {
        PluginMain.result=e.toString();
        }
        done=true;
        
    }
    public boolean isDone()
    {
        try
        {
            this.sleep(100);
        } catch (InterruptedException ex)
        {
            
        }
        return done;
    }
    
}
