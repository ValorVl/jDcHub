/*
 * PluginMain.java
 *
 * Created on 07 decembrie 2007, 18:32
 *
 * DSHub ADC HubSoft
 * Copyright (C) 2007  Pietricica
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
import ru.sincore.util.*;
import ru.sincore.ClientHandler;
import ru.sincore.Main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.StringTokenizer;
import javax.swing.JFrame;

/**
 * This is a generic DSHub module plugin, implementing the 
 * Module interface, does nothing, just aids future developers in adding modules
 * to DSHub using an already built-up schema.
 *
 * @author Pietricica
 */
public class PluginMain implements DSHubModule
{
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
        if(Issued_Command.toLowerCase().startsWith("feed") )
        {
            new feed(cur_client,Issued_Command,false);
            return DSHubModule.ACK_COMMAND;
        }
        else if(Issued_Command.toLowerCase().startsWith("help"))
        {
            cur_client.sendFromBot("[rss feed: ] RSS Feed Module Help:\n"+
"Available commands:\nfeed -- a rss feed tool, use with no parameters for details."
                 );
            return DSHubModule.ACK_COMMAND;
        }
        return DSHubModule.DO_NOTHING;
           
    }
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
     StringTokenizer ST=new StringTokenizer(Raw_Command) ; //parsing the raw command;
     if(!ST.hasMoreTokens())
    	 return;
     ST.nextToken(); //this must be the BMSG
     if(!ST.hasMoreTokens())
    	 return;
     ST.nextToken();//this should be the SID
     if(!ST.hasMoreTokens())
    	 return;
     String cmd= ST.nextToken(); // this should be the actual command
     if(cmd.startsWith("+feed"))
     new  feed(cur_client, AdcUtils.fromAdcString(cmd.substring(1)),true);
     if(cmd.startsWith("+help"))
         cur_client.sendFromBot("[rss feed: ] RSS Feed Module Help:\n"+
                 "Available commands:\nfeed -- a rss feed tool, use with no parameters for details.");
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

        BufferedReader BR = null;
        
        try
        {
            
            
            File RSSFile = new File(Main.myPath+"rss"); //making a new File thingy that access the "rss" file
            BR = new BufferedReader(new FileReader(RSSFile)); //making a reader so we can read from the file
            feed.Address=BR.readLine();
            BR.close();
            

            
        } 
        catch (FileNotFoundException ex) //if the file doesnt exist
        {
            //we create it
            writeRSSAddress();
        } 
        catch(Exception e) //some other exception, like : we dont have the rights to 
                //read the file, or some corrupted file/data
        {
            System.out.println("RSS Feed plugin error: can't read settings file. Bypassed.");
            //there is nothing we can do, the plugin can;t save the address in the file.. such is life
            //the user will have to change the address manually after every restart
            //or make something to be able to access the file
        }
        System.out.println("RSS Feed Plugin Loaded...");
        return true; //plugin was initialized ok
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
     new rssgui().setVisible(true);
    }
    
    public String getName()
    {
        return "RSS Feed Plugin";
    }
    
    
    public static void writeRSSAddress()
    {
        try
        {
            
            
           FileWriter fstream = new FileWriter(Main.myPath+"rss");
           BufferedWriter out = new BufferedWriter(fstream);
           out.write(feed.Address);
    //Close the output stream
    out.close();

            
        } 
        catch (Exception e)
        {
            //could not write. :(
        }
    }
    
    
}
