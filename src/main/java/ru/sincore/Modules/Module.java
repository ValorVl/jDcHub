/*
 * Module.java
 *
 * Created on 12 decembrie 2007, 12:13
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

package dshub.Modules;


import dshub.ClientHandler;
import dshub.Main;
import dshub.conf.Vars;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;

/**
 * @author Pietricica
 */
public class Module
{
    private boolean isok;

    private int ID;

    public static int mID = 0;

    private boolean enabled;

    private String moduleName;

    private DSHubModule curModule;

    private JCheckBox enableCheck;

    private JButton guiButton;


    /**
     * Creates a new instance of Module
     */
    public Module(DSHubModule curModule)
    {
        isok = true;
        this.curModule = curModule;

        try
        {
            moduleName = curModule.getName();
        }
        catch (AbstractMethodError abe)
        {
            isok = false;
        }
        if (!isok)
        {
            return;
        }
        this.setID(mID++);
        loadEnable();

    }


    public void setCheckBox(JCheckBox cb1)
    {
        this.enableCheck = cb1;
        this.enableCheck.setSelected(isEnabled());

        enableCheck.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (enableCheck.isSelected())
                {
                    setEnabled(true);
                }
                else
                {
                    setEnabled(false);
                }
            }
        });

    }


    public void setButton(JButton b1)
    {
        this.guiButton = b1;
        this.guiButton.setEnabled(this.isEnabled());
        guiButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                if (Main.GUIok)
                {
                    onGUIClick(Main.GUI);
                }
            }
        });

    }


    public JCheckBox getCheckBox()
    {
        return this.enableCheck;
    }


    public JButton getGUIButton()
    {
        return this.guiButton;
    }


    public boolean isOK()
    {
        return this.isok;
    }


    public String getName()
    {
        return this.moduleName;
    }


    public void onGUIClick(JFrame parent)
    {
        if (isOK() && isEnabled())
        {
            try
            {
                this.curModule.onGUIClick(parent);
            }
            catch (AbstractMethodError abe)
            {
                isok = false;
            }
        }

    }


    public int onCommand(ClientHandler cur_client, String Issued_Command)
    {
        if (isOK() && isEnabled())
        {
            try
            {
                return this.curModule.onCommand(cur_client, Issued_Command);
            }
            catch (AbstractMethodError abe)
            {
                isok = false;

            }
        }
        return 0;
    }


    public void onRawCommand(ClientHandler cur_client, String Raw_Command)
    {
        if (isOK() && isEnabled())
        {
            try
            {
                this.curModule.onRawCommand(cur_client, Raw_Command);
            }
            catch (AbstractMethodError abe)
            {
                isok = false;
            }
        }
    }


    public void onClientQuit(ClientHandler cur_client)
    {
        if (isOK() && isEnabled())
        {
            try
            {
                this.curModule.onClientQuit(cur_client);
            }
            catch (AbstractMethodError abe)
            {
                isok = false;
            }
        }
    }


    public void onConnect(ClientHandler cur_client)
    {
        if (isOK() && isEnabled())
        {
            try
            {
                this.curModule.onConnect(cur_client);
            }
            catch (AbstractMethodError abe)
            {
                isok = false;
            }
        }
    }


    public void close()
    {
        try
        {
            this.curModule.close();
        }
        catch (AbstractMethodError abe)
        {
            isok = false;
        }
        enabled = false;
        isok = false;
    }


    public void setID(int i)
    {
        this.ID = i;
    }


    public int getID()
    {
        return this.ID;
    }


    public boolean isEnabled()
    {
        return enabled;
    }


    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        if (!(this.isEnabled()))
        {
            this.close();
        }
        else
        {
            try
            {
                isok = curModule.startup();
            }
            catch (AbstractMethodError abe)
            {
                isok = false;
            }
        }
        int index = Vars.activePlugins.indexOf(this.getName());


        Vars.activePlugins = Vars.activePlugins.substring(0, index - 1) +
                             (this.isEnabled() ? "+" : "-") +
                             Vars.activePlugins.substring(index);
        if (this.enableCheck != null)
        {
            this.enableCheck.setSelected(this.enabled);
        }
        if (this.guiButton != null)
        {
            this.guiButton.setEnabled(this.isEnabled());
        }
    }


    public void loadEnable()
    {
        int index = Vars.activePlugins.indexOf(this.getName());
        if (index == -1)
        {
            //new module found !


            Vars.activePlugins += "+" + this.getName();
            this.setEnabled(true);
        }
        else if (Vars.activePlugins.charAt(index - 1) == '-')
        {
            this.setEnabled(false);
        }
        else
        {
            this.setEnabled(true);
        }
        if (this.enableCheck != null)
        {
            this.enableCheck.setSelected(this.enabled);
        }
    }


}
