/*
 * Hublist.java
 *
 * Created on 21 ianuarie 2008, 16:32
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

import dshub.ADC;
import dshub.Vars;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 *
 * @author Pietricica
 */
public class Hublist implements Serializable
{
   public String URL;
   public String Name;
   public String Website;
   
   public String status="UNKNOWN";
   
   public boolean register()
   {
       try
        {
            Socket hublistConn = new Socket();
            int x= URL.indexOf(':')  ;
                      if(x==-1 || x>(URL.length()-1))
                       {
                        
                       return false;
                       }   
                       int port=Integer.parseInt(URL.substring(x+1));
                       
                       
            hublistConn.connect(new InetSocketAddress(URL.substring(0,x), port),2*1000);
            hublistConn.setSoTimeout(2000);
            //System.out.println(Host.substring(0,x)+" "+port);
           
            BufferedReader in = new BufferedReader(new InputStreamReader(hublistConn.getInputStream()));
            PrintStream out=new PrintStream(hublistConn.getOutputStream());
            out.println("H"+ADC.Init.substring(1));
            String SUP=in.readLine();
            if(!SUP.startsWith("ISUP"))
                return false;
            if(!SUP.contains("ADPIN"))
                return false;
            
            
            String INF=in.readLine();
            if(!INF.startsWith("IINF"))
                    return false;
            StringTokenizer tok= new StringTokenizer ( INF);
            while(tok.hasMoreTokens())
            {
                String t=tok.nextToken();
                if(t.startsWith("NI"))
                    this.Name=t.substring(2);
                if(t.startsWith("WS"))
                    this.Name=t.substring(2);
            }
            
            if(Vars.HubDE.equals (""))
            out.println("IINF CT32 VE"+ADC.retADCStr (Vars.HubVersion)+" NI"+ADC.retADCStr(Vars.HubName)
                     +ADC.getPingString()
                    
                    
                    );
        else
           out.println("IINF CT32 VE"+ADC.retADCStr (Vars.HubVersion)+" NI"+ADC.retADCStr(Vars.HubName)+ " DE"+ADC.retADCStr(Vars.HubDE)+
                   ADC.getPingString()
                     );
            
            in.close();
            out.close();
            hublistConn.close();
            

            
        } catch (UnknownHostException ex)
        {
            //Logger.getLogger(HostTester.class.getName()).log(Level.SEVERE, null, ex);
            //ex.printStackTrace();
            return false;
        } catch (IOException ex)
        {
           // Logger.getLogger(HostTester.class.getName()).log(Level.SEVERE, null, ex);
            //ex.printStackTrace();
          return false;
        }
        catch ( NumberFormatException nfe)
        {
            return false;
        }
        
        catch ( Exception e)
        {
           // e.printStackTrace();
           return false;
        }
        
        return true;
   }
    
    
}
