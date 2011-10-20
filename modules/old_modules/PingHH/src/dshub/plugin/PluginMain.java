/*
 * PluginMain.java
 *
 * Created on 20 decembrie 2007, 10:25
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
import ru.sincore.Modules.DSHubModule;
import ru.sincore.ClientHandler;
import ru.sincore.Main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.swing.JFrame;

/**
 * This is a generic DSHub module plugin, implementing the 
 *Module interface, does nothing, just aids future developers in adding modules
 *to DSHub using an already built-up schema.
 *
 * @author Pietricica
 */
public class PluginMain implements DSHubModule
{
    static HublistList curlist;
    static PingFrame pframe;
    /** Called by hub main threads when registered users give a command (starting with + or ! )
     *@arguments cur_client, the ClientHandler for the client who issued the Issued_Command, given in string
     *and with no protocol thingies
     *Must return ACK_COMMAND if it handled the command normally and command should be shown on
     * cmdhistory, or DO_NOTHING if it did nothing
     * Should return HIDE_COMMAND if the command contained some password or something and cmdhistory should not show it.
     * Other return values are reserved for future use.
     */
    public int onCommand(ClientHandler cur_client,String Issued_Command)
    {
        if(Issued_Command.toLowerCase().startsWith("hublist") )
        {
            new HublistCmd(cur_client,Issued_Command);
            return DSHubModule.ACK_COMMAND;
        }
        else if(Issued_Command.toLowerCase().startsWith("help"))
        {
            cur_client.sendFromBot("[PingHH: ] Ping Extension Hublist Autoregistering Module Help:\nAvailable commands: "+
                    "\nhublist -- A simple way to register the hub on hublists, also to add/remove hublists.");
            return DSHubModule.ACK_COMMAND;
        }
        return DSHubModule.DO_NOTHING;
           
    }
    /** Called by hub main threads when a new client connects and its logged in ok
     *@arguments cur_client, the ClientHandler for the client who connected
     */
    public void onConnect(ClientHandler cur_client)
    {
        
    }
    /** Called by hub main threads when a client sends any raw command;
     *@arguments cur_client, the ClientHandler for the client who sent the raw, given in string
     *with all the protocol thingies. This method is always called after the DSHub internal 
     *methods are called to work at the raw command.
     */
    public void onRawCommand(ClientHandler cur_client,String Raw_Command)
    {
        
    }
    /** Called by hub main threads when a client quits the hub;
     *@arguments cur_client, the ClientHandler for the client who quitted;
     */
    public void onClientQuit(ClientHandler cur_client)
    {
        
    }
    /** Called by hub main threads when registering plugin at startup or restarts
     * MUST return true if everything is ok ( classes ok, initialisation ok, nothing missing ( additional dependecies maybe ))
     * and false if startup failed.
     * If false returned, hub will ignore plugin.
     */
    public boolean startup()
    {
        curlist=new HublistList();
        
        return reloadList();
        
        //System.out.println("pinghh ok");
        
    }
    /** Called by hub main threads when closing plugin at quitting main application or restarts
     * Should clear everything up.
     */
    public void close()
    {
        
    }
    
    /** Called by main GUI thread when users wants to click the plugin allocated button 
      *@argument parent = the main GUI Frame that calls this function
     *
     */
    public void onGUIClick(JFrame parent)
    {
        ;//does nothing
       // System.out.println("clicked !");
        pframe=new PingFrame();
        pframe.setVisible(true);
    }
    
    public String getName()
    {
        return "Ping Hub-Hublist Communication";
    }
    
    public static boolean reloadList()
    {
        try
        {
        FileInputStream HublistListReader=new FileInputStream (Main.myPath+"hublists.txt");
        
        ObjectInputStream in=new ObjectInputStream(HublistListReader);
        curlist=(HublistList) in.readObject();
        }
        catch ( FileNotFoundException fnfe)
        {
            //file not found so were gonna make it
             Main.PopMsg("[PingHH]: Created default hublist List.");
           return rewriteList();

            
            
        }
        catch (IOException e)
        {
            Main.PopMsg("[PingHH]: Error accesing hublists files. Attempting overwrite with default values.");
            
            return rewriteList();
        }
         catch(ClassNotFoundException e)
        {
            Main.PopMsg("[PingHH]: Internal Error Config Corrupted Files. FAIL.");
            return false;
        }
        return true;
    }
    
    public static boolean rewriteList()
    {
        

       
         try 
            {
           
            FileOutputStream HublistListOutput=new FileOutputStream(Main.myPath+"hublists.txt");
            
            ObjectOutputStream outreg = new ObjectOutputStream(HublistListOutput);  // Save objects
            outreg.writeObject(curlist);      
            outreg.flush();                 // Always flush the output.
            outreg.close();                 // And close the stream.

            }
            catch (IOException e)
            {
                //Main.PopMsg(e.toString());
                return false;
            }
        return true;
    }
    
    
}
