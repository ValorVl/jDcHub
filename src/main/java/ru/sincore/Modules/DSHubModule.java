/*
 * DSHubModule.java
 *
 * Created on 06 decembrie 2007, 20:50
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

package ru.sincore.Modules;

import ru.sincore.ClientHandler;

import javax.swing.JFrame;

/**
 * Interface designed for plugins to implement
 * The implementing classes shouldnt have a complicated constructor since a new
 * instance is anyway created for testing purposes.
 * Instead, the startup method should initialise the plugin and return the result
 * <p/>
 * Any plugin must have a class named PluginMain, which implements this interface
 * and included in the package dshub.plugin
 * Otherwise, the plugin will not be installed correctly.
 *
 * @author Pietricica
 */
public interface DSHubModule
{
    /**
     * Possible return value for onCommand() method
     * If this value is returned then the method did nothing
     */
    public static final int DO_NOTHING   = 0;
    /**
     * Possible return value for onCommand() method
     * If this value is returned then the method treated the
     * command as a normal one.
     */
    public static final int ACK_COMMAND  = 1;
    /**
     * Possible return value for onCommand() method
     * If this value is returned then the method treated the
     * command as a hidden one and it shouldnt be shown to cmdhistory
     * or other users.
     */
    public static final int HIDE_COMMAND = 2;

    /**
     * Called by hub main threads when registered users give a command (starting with + or ! )
     *
     * @arguments cur_client, the ClientHandler for the client who issued the Issued_Command, given in string
     * and with no protocol thingies
     * Must return ACK_COMMAND if it handled the command normally and command should be shown on
     * cmdhistory, or DO_NOTHING if it did nothing
     * Should return HIDE_COMMAND if the command contained some password or something and cmdhistory should not show it.
     * Other return values are reserved for future use.
     */
    public int onCommand(ClientHandler cur_client, String Issued_Command);

    /**
     * Called by hub main threads when a new client connects and its logged in ok
     *
     * @arguments cur_client, the ClientHandler for the client who connected
     */
    public void onConnect(ClientHandler cur_client);

    /**
     * Called by hub main threads when a client sends any raw command;
     *
     * @arguments cur_client, the ClientHandler for the client who sent the raw, given in string
     * with all the protocol thingies. This method is always called after the DSHub internal
     * methods are called to work at the raw command.
     */
    public void onRawCommand(ClientHandler cur_client, String Raw_Command);

    /**
     * Called by hub main threads when a client quits the hub;
     *
     * @arguments cur_client, the ClientHandler for the client who quitted;
     */
    public void onClientQuit(ClientHandler cur_client);

    /**
     * Called by hub main threads when registering plugin at startup or restarts
     * MUST return true if everything is ok ( classes ok, initialisation ok, nothing missing ( additional dependecies maybe ))
     * and false if startup failed.
     * If false returned, hub will ignore plugin.
     */
    public boolean startup();

    /**
     * Called by hub main threads when closing plugin at quitting main application or restarts
     * Should clear everything up.
     */
    public void close();

    /**
     * Called by main GUI thread when users wants to click the plugin allocated button
     *
     * @argument parent = the main GUI Frame that calls this function
     */
    public void onGUIClick(JFrame parent);

    /**
     * Must return the module name, String
     */
    public String getName();


}
