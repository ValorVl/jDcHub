/*
 * TestFrame.java
 *
 * Created on 16 iunie 2007, 14:32
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

package ru.sincore.gui;

import ru.sincore.Modules.Modulator;
import ru.sincore.Modules.Module;
import ru.sincore.python.*;
import ru.sincore.util.ADC;
import ru.sincore.util.HostTester;
import ru.sincore.util.TimeConv;
import ru.sincore.banning.Ban;
import ru.sincore.banning.BanList;
import ru.sincore.conf.Port;
import ru.sincore.conf.Vars;
import ru.sincore.i18n.Translation;
import ru.sincore.*;

import java.awt.event.KeyEvent;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.DefaultListModel;

/**
 * Basic GUI for DSHub ( TestFrame is a test name that was converted into main GUI, didnt change it after
 * I decided to make a GUI )
 * Provides all hub configuration, accounts, and adc adv config panel.
 *
 * @author Pietricica
 */
public class TestFrame extends javax.swing.JFrame
{
    BanWordsList listaBanate;
    ImageIcon    myIco;
    ImageIcon    offIco, onIco;
    boolean initialised = false;


    /**
     * Creates new form TestFrame
     */
    public TestFrame()
    {

        initComponents();
        /*java.awt.EventQueue.invokeLater (new Runnable ()
		{
		    public void run ()
		    {
		       // setVisible (true);
		 
		 
		    }
		});*/

        langcombo.removeAllItems();
        langcombo.addItem("en_US");
        langcombo.addItem("ro_RO");
        langcombo.addItem("it_IT");
        langcombo.addItem("sv_SE");
        langcombo.addItem("de_DE");
        langcombo.addItem("nl_NL");
        langcombo.addItem("ru_RU");
        langcombo.setSelectedItem(Translation.curLocale.getLanguage() + "_"
                                  + Translation.curLocale.getCountry());

        //init list model for jList1 to DefaultListModel
        DefaultListModel modelLista = new DefaultListModel();
        jList1.setModel(modelLista);
        listaBanate = Main.listaBanate;
        int i, n = listaBanate.size();
        for (i = 0; i < n; i++)
        {
            modelLista.addElement(listaBanate.elementAt(i));
        }
        myIco = new ImageIcon(getClass().getResource("/dshub/ds.jpg"));
        onIco = new ImageIcon(getClass().getResource("/dshub/on.jpg"));
        offIco = new ImageIcon(getClass().getResource("/dshub/off.jpg"));
        //this.setIconImage(new ImageIcon("/dshub/ds.ico").getImage());
        this.setIconImage(myIco.getImage());
        refreshInit();

        //Modulator.findModules();
    }


    public void refreshGUIPlugs()
    {
        //jScrollPane11.removeAll();
        int y = 20;
        for (Module myPlug : Modulator.myModules)
        {

            myPlug.getName();
            // if(firstClick)
            //    myPlug.loadEnable();
            JPanel myPanel = new JPanel();

            //myPanel.setSize(PluginPanel.getWidth()-20,50);
            myPanel.setBorder(javax.swing.BorderFactory
                                      .createTitledBorder(myPlug.getName() + " Module"));
            org.jdesktop.layout.GroupLayout myPanelLayout = new org.jdesktop.layout.GroupLayout(
                    myPanel);
            myPanel.setLayout(myPanelLayout);
            JCheckBox enableCheck = new JCheckBox("Enable");
            JButton guiClick = new JButton("Open Plugin GUI");
            enableCheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(
                    0, 0, 0, 0));
            enableCheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
            myPanelLayout.setHorizontalGroup(myPanelLayout.createParallelGroup(
                    org.jdesktop.layout.GroupLayout.LEADING).add(
                    myPanelLayout.createSequentialGroup().add(8, 8, 8).add(
                            enableCheck).addPreferredGap(
                            org.jdesktop.layout.LayoutStyle.RELATED, 89,
                            Short.MAX_VALUE).add(guiClick).add(38, 38, 38)
                                 .addContainerGap(534, Short.MAX_VALUE)));
            myPanelLayout.setVerticalGroup(myPanelLayout.createParallelGroup(
                    org.jdesktop.layout.GroupLayout.LEADING).add(
                    myPanelLayout.createSequentialGroup().add(8, 8, 8).add(
                            myPanelLayout.createParallelGroup(
                                    org.jdesktop.layout.GroupLayout.BASELINE)
                                         .add(enableCheck).add(guiClick))
                                 .addContainerGap(13, Short.MAX_VALUE)));
            PluginPanel.add(myPanel,
                            new org.netbeans.lib.awtextra.AbsoluteConstraints(10, y,
                                                                              520, 60));
            myPlug.setCheckBox(enableCheck);
            myPlug.setButton(guiClick);
            y += 60;

            // myPanel.add(enableCheck);
        }

    }


    public void refreshListaBanate()
    {
        DefaultListModel modelLista = (DefaultListModel) jList1.getModel();
        modelLista.removeAllElements();
        int i, n = listaBanate.size();
        for (i = 0; i < n; i++)
        {
            modelLista.addElement(listaBanate.elementAt(i));
        }
    }


    private void deleteSelectedReg()
    {
        int row = AccountTable.getSelectedRow();
        if (row == -1)
        {
            return;
        }
        String CID = (String) AccountTable.getModel().getValueAt(row, 0);
        // Main.PopMsg(CID);
        if (AccountsConfig.unreg(CID))
        {
            DefaultTableModel AccountModel = (DefaultTableModel) AccountTable
                    .getModel();
            Nod n = AccountsConfig.First;
            int regcount = 0;
            while (n != null)
            {
                regcount++;
                n = n.Next;
            }

            if (regcount != AccountModel.getRowCount())
            {
                AccountModel.setRowCount(0);
                n = AccountsConfig.First;
                while (n != null)
                {
                    String blah00 = "";
                    Date d = new Date(n.CreatedOn);
                    if (n.LastNI != null)
                    {
                        blah00 = n.LastNI;
                    }
                    else
                    {
                        blah00 = "Never seen online.";
                    }

                    AccountModel.addRow(new Object[]{ n.CID, blah00,
                                                      n.LastIP, n.WhoRegged, d.toString() });
                    n = n.Next;
                }
            }
            for (ClientNod temp : SimpleHandler.getUsers())
            {
                if (temp.cur_client.userok == 1)
                {
                    if ((temp.cur_client.ID.equals(CID)))
                    {
                        temp.cur_client
                                .sendFromBot(""
                                             +
                                             "Your account has been deleted. From now on you are a simple user.");
                        temp.cur_client.putOpchat(false);
                        temp.cur_client.CT = "0";

                        Broadcast.getInstance()
                                 .broadcast(
                                         "BINF " + temp.cur_client.SessionID
                                         + " CT");
                        temp.cur_client.can_receive_cmds = false;
                        temp.cur_client.reg = new Nod();
                        Main.PopMsg("User " + temp.cur_client.NI
                                    + " with CID " + CID + " found, deleted.");
                        Main.Server.rewriteregs();
                        SetStatus("Reg Deleted");
                        return;
                    }
                }
            }

            Main.PopMsg("Reg " + CID + " deleted.");
            Main.Server.rewriteregs();

        }

        SetStatus("Reg Deleted");
    }


    public void selectPr(long prop, String repl)
    {
        jRadioButton1.setSelected(false);
        jRadioButton2.setSelected(false);
        jRadioButton4.setSelected(false);
        jRadioButton5.setSelected(false);
        jRadioButton6.setSelected(false);
        privatecheck.setSelected(false);
        notifycheck.setSelected(false);
        searchcheck.setSelected(false);
        jTextField3.setEditable(false);
        jTextField3.setText("");
        if ((prop & BannedWord.dropped) != 0)
        {
            jRadioButton1.setSelected(true);
        }
        if ((prop & BannedWord.kicked) != 0)
        {
            jRadioButton2.setSelected(true);
        }
        if ((prop & BannedWord.noAction) != 0)
        {
            jRadioButton3.setSelected(true);
        }

        if ((prop & BannedWord.hidden) != 0)
        {
            jRadioButton4.setSelected(true);
        }
        if ((prop & BannedWord.replaced) != 0)
        {
            jRadioButton5.setSelected(true);
        }
        if ((prop & BannedWord.modified) != 0)
        {
            jRadioButton6.setSelected(true);
            jTextField3.setEditable(true);
            jTextField3.setText(repl);
        }
        if ((prop & BannedWord.privatechat) != 0)
        {
            privatecheck.setSelected(true);
        }
        if ((prop & BannedWord.notify) != 0)
        {
            notifycheck.setSelected(true);
        }
        if ((prop & BannedWord.searches) != 0)
        {
            searchcheck.setSelected(true);
        }
    }


    public long getClientPr()
    {
        long prop = 0;

        if (jRadioButton1.isSelected())
        {
            prop = prop | BannedWord.dropped;
        }
        if (jRadioButton2.isSelected())
        {
            prop = prop | BannedWord.kicked;
        }
        if (jRadioButton3.isSelected())
        {
            prop = prop | BannedWord.noAction;
        }
        // System.out.println(jRadioButton1.isSelected()+" "+jRadioButton2.isSelected()+" "+
        //         jRadioButton3.isSelected());
        //System.out.println("sss - "+prop+" - sss");
        return prop;
    }


    public long getClientAddPr()
    {
        long prop = 0;

        if (jRadioButton7.isSelected())
        {
            prop = prop | BannedWord.dropped;
        }
        if (jRadioButton8.isSelected())
        {
            prop = prop | BannedWord.kicked;
        }
        if (jRadioButton9.isSelected())
        {
            prop = prop | BannedWord.noAction;
        }
        // System.out.println(jRadioButton1.isSelected()+" "+jRadioButton2.isSelected()+" "+
        //         jRadioButton3.isSelected());
        //System.out.println("sss - "+prop+" - sss");
        return prop;
    }


    public long getWordPr()
    {
        long prop = 0;

        if (jRadioButton4.isSelected())
        {
            prop = prop | BannedWord.hidden;
        }
        if (jRadioButton5.isSelected())
        {
            prop = prop | BannedWord.replaced;
        }
        if (jRadioButton6.isSelected())
        {
            prop = prop | BannedWord.modified;
        }
        // System.out.println(prop);
        return prop;
    }


    public long getWordAddPr()
    {
        long prop = 0;

        if (jRadioButton10.isSelected())
        {
            prop = prop | BannedWord.hidden;
        }
        if (jRadioButton11.isSelected())
        {
            prop = prop | BannedWord.replaced;
        }
        if (jRadioButton12.isSelected())
        {
            prop = prop | BannedWord.modified;
        }
        // System.out.println(prop);
        return prop;
    }


    public String getRepl()
    {
        if (jRadioButton6.isSelected())
        {
            return jTextField3.getText();
        }
        return "";
    }


    public String getAddRepl()
    {
        if (jRadioButton12.isSelected())
        {
            return jTextField5.getText();
        }
        return "";
    }


    public void refreshPyScripts()
    {
        DefaultTableModel PyModel = (DefaultTableModel) PyTable.getModel();
        PyModel.setRowCount(0);
        for (PythonScript pyS : PythonManager.scripts)
        {
            if (pyS.isOk())
            {
                PyModel.addRow(new Object[]{ pyS.getScriptName(),
                                             pyS.isActive() });
            }
        }

    }


    public void refreshStats()
    {
        Runtime myRun = Runtime.getRuntime();

        int i = 0, j = 0;
        for (ClientNod temp : SimpleHandler.getUsers())
        {
            if (temp.cur_client.userok == 1)
            {
                i++;
            }
            else
            {
                j++;
            }

        }

        long up = System.currentTimeMillis() - Main.curtime; //uptime in millis

        Date b = new Date(Main.curtime);

        osname.setText(Main.Proppies.getProperty("os.name"));
        osversion.setText(Main.Proppies.getProperty("os.version"));
        osarch.setText(Main.Proppies.getProperty("os.arch"));
        jrename.setText(Main.Proppies.getProperty("java.version"));
        jreprovider.setText(Main.Proppies.getProperty("java.vendor"));
        cpunumber.setText(Integer.toString(myRun.availableProcessors()));
        usercount.setText(Integer.toString(i));
        connectingcount.setText(Integer.toString(j));
        uptime.setText(TimeConv.getStrTime(up));
        startuptime.setText(b.toString());

        jTextArea2.setText("Death Squad Hub. Version " + Vars.HubVersion
                           + ".\n" + "  Running on "
                           + Main.Proppies.getProperty("os.name") + " Version "
                           + Main.Proppies.getProperty("os.version") + " on Architecture "
                           + Main.Proppies.getProperty("os.arch") + "\n"
                           + "  Java Runtime Environment "
                           + Main.Proppies.getProperty("java.version") + " from "
                           + Main.Proppies.getProperty("java.vendor") + "\n"
                           + "  Java Virtual Machine "
                           + Main.Proppies.getProperty("java.vm.specification.version")
                           + "\n" + "  Available CPU's to JVM "
                           + Integer.toString(myRun.availableProcessors()) + "\n"
                           + "  Available Memory to JVM: "
                           + Long.toString(myRun.maxMemory()) + " Bytes, where free: "
                           + Long.toString(myRun.freeMemory()) + " Bytes\n"
                           + "Hub Statistics:\n" + "  Online users: "
                           + Integer.toString(i) + "\n" + "  Connecting users: "
                           + Integer.toString(j) + "\n" + "  Uptime: "
                           + TimeConv.getStrTime(up) + "\n" + "  Start Time: "
                           + b.toString()// + //"\n  Bytes red per second: "
                           // "\n  Bytes read per second: "+Main.Server.acceptor.getReadBytesThroughput()+
                           //     "\n  Bytes written per second: "+Main.Server.acceptor.getWrittenBytesThroughput()
                           //+ "\n  Bytes written per second: "
                           //	+ Main.Server.IOSM.getTotalByteWrittenThroughput()

                          );
    }


    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        jLabel62 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jButton33 = new javax.swing.JButton();
        jButton34 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel69 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jButton7 = new javax.swing.JButton();
        jPanel20 = new javax.swing.JPanel();
        namefield = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        topicfield = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        regonlycheck = new javax.swing.JCheckBox();
        jPanel21 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanel19 = new javax.swing.JPanel();
        proxycheck = new javax.swing.JCheckBox();
        proxyhostfield = new javax.swing.JTextField();
        jLabel67 = new javax.swing.JLabel();
        proxyportfield = new javax.swing.JTextField();
        jLabel68 = new javax.swing.JLabel();
        hubhostfield = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        addnewport = new javax.swing.JButton();
        remport = new javax.swing.JButton();
        pane5 = new javax.swing.JScrollPane();
        portlist = new javax.swing.JTable();
        jPanel8 = new javax.swing.JPanel();
        fieldtimeout = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        maxnifield = new javax.swing.JTextField();
        minnifield = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        maxdefield = new javax.swing.JTextField();
        maxsharefield = new javax.swing.JTextField();
        minsharefield = new javax.swing.JTextField();
        maxslfield = new javax.swing.JTextField();
        minslfield = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        maxemfield = new javax.swing.JTextField();
        maxhubsopfield = new javax.swing.JTextField();
        maxhubsregfield = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        maxhubsuserfield = new javax.swing.JTextField();
        jButton8 = new javax.swing.JButton();
        jPanel13 = new javax.swing.JPanel();
        maxusersfield = new javax.swing.JTextField();
        jLabel32 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        maxschcharsfield = new javax.swing.JTextField();
        minschcharsfield = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        nickcharsfield = new javax.swing.JTextArea();
        jButton9 = new javax.swing.JButton();
        xxx = new javax.swing.JPanel();
        maxchatmsgfield = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        chatintervalfield = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        automagicsearchfield = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        searchlogbasefield = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        searchstepsfield = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        searchspamresetfield = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        msgsearchspamfield = new javax.swing.JTextArea();
        jLabel47 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        miscpanel = new javax.swing.JPanel();
        jButton11 = new javax.swing.JButton();
        jPanel35 = new javax.swing.JPanel();
        opchatnamefield = new javax.swing.JTextField();
        botnamefield = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        botdescfield = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        opchatdescfield = new javax.swing.JTextField();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jPanel36 = new javax.swing.JPanel();
        redirecturl = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        jPanel38 = new javax.swing.JPanel();
        kicktimefield = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jPanel39 = new javax.swing.JPanel();
        historylinesfield = new javax.swing.JTextField();
        jLabel39 = new javax.swing.JLabel();
        jPanel37 = new javax.swing.JPanel();
        jPanel34 = new javax.swing.JPanel();
        jLabel42 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        msgfullfield = new javax.swing.JTextArea();
        jLabel41 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        msgbannedfield = new javax.swing.JTextArea();
        jButton31 = new javax.swing.JButton();
        jPanel40 = new javax.swing.JPanel();
        savelogscheck = new javax.swing.JCheckBox();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jPanel42 = new javax.swing.JPanel();
        jPanel43 = new javax.swing.JPanel();
        jLabel72 = new javax.swing.JLabel();
        langcombo = new javax.swing.JComboBox();
        jLabel59 = new javax.swing.JLabel();
        jPanel44 = new javax.swing.JPanel();
        command_pmcheck = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButton4 = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        AccountTable = new javax.swing.JTable();
        jButton22 = new javax.swing.JButton();
        jButton35 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        BanTable = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jButton6 = new javax.swing.JButton();
        jLabel89 = new javax.swing.JLabel();
        osname = new javax.swing.JLabel();
        jLabel90 = new javax.swing.JLabel();
        osversion = new javax.swing.JLabel();
        jLabel91 = new javax.swing.JLabel();
        osarch = new javax.swing.JLabel();
        jLabel92 = new javax.swing.JLabel();
        jrename = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jreprovider = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        cpunumber = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        usercount = new javax.swing.JLabel();
        jLabel96 = new javax.swing.JLabel();
        connectingcount = new javax.swing.JLabel();
        jLabel97 = new javax.swing.JLabel();
        uptime = new javax.swing.JLabel();
        jLabel98 = new javax.swing.JLabel();
        startuptime = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jPanel15 = new javax.swing.JPanel();
        jRadioButton4 = new javax.swing.JRadioButton();
        jRadioButton5 = new javax.swing.JRadioButton();
        jRadioButton6 = new javax.swing.JRadioButton();
        jTextField3 = new javax.swing.JTextField();
        privatecheck = new javax.swing.JCheckBox();
        notifycheck = new javax.swing.JCheckBox();
        searchcheck = new javax.swing.JCheckBox();
        jPanel16 = new javax.swing.JPanel();
        jTextField4 = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        jButton26 = new javax.swing.JButton();
        jButton27 = new javax.swing.JButton();
        jButton28 = new javax.swing.JButton();
        jButton29 = new javax.swing.JButton();
        jPanel17 = new javax.swing.JPanel();
        jTextField2 = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        jButton23 = new javax.swing.JButton();
        jRadioButton7 = new javax.swing.JRadioButton();
        jRadioButton8 = new javax.swing.JRadioButton();
        jRadioButton9 = new javax.swing.JRadioButton();
        jRadioButton10 = new javax.swing.JRadioButton();
        jRadioButton11 = new javax.swing.JRadioButton();
        jRadioButton12 = new javax.swing.JRadioButton();
        jTextField5 = new javax.swing.JTextField();
        jPanel18 = new javax.swing.JPanel();
        jButton25 = new javax.swing.JButton();
        jButton24 = new javax.swing.JButton();
        jScrollPane9 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel9 = new javax.swing.JPanel();
        jLabel51 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton18 = new javax.swing.JButton();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton21 = new javax.swing.JButton();
        jPanel25 = new javax.swing.JPanel();
        BMSGcheck = new javax.swing.JCheckBox();
        DMSGcheck = new javax.swing.JCheckBox();
        EMSGcheck = new javax.swing.JCheckBox();
        FMSGcheck = new javax.swing.JCheckBox();
        HMSGcheck = new javax.swing.JCheckBox();
        jButton12 = new javax.swing.JButton();
        jPanel26 = new javax.swing.JPanel();
        BSTAcheck = new javax.swing.JCheckBox();
        DSTAcheck = new javax.swing.JCheckBox();
        ESTAcheck = new javax.swing.JCheckBox();
        FSTAcheck = new javax.swing.JCheckBox();
        HSTAcheck = new javax.swing.JCheckBox();
        jPanel27 = new javax.swing.JPanel();
        BCTMcheck = new javax.swing.JCheckBox();
        DCTMcheck = new javax.swing.JCheckBox();
        ECTMcheck = new javax.swing.JCheckBox();
        FCTMcheck = new javax.swing.JCheckBox();
        HCTMcheck = new javax.swing.JCheckBox();
        jPanel28 = new javax.swing.JPanel();
        BRCMcheck = new javax.swing.JCheckBox();
        DRCMcheck = new javax.swing.JCheckBox();
        ERCMcheck = new javax.swing.JCheckBox();
        FRCMcheck = new javax.swing.JCheckBox();
        HRCMcheck = new javax.swing.JCheckBox();
        jPanel29 = new javax.swing.JPanel();
        BINFcheck = new javax.swing.JCheckBox();
        DINFcheck = new javax.swing.JCheckBox();
        EINFcheck = new javax.swing.JCheckBox();
        FINFcheck = new javax.swing.JCheckBox();
        HINFcheck = new javax.swing.JCheckBox();
        jPanel30 = new javax.swing.JPanel();
        BSCHcheck = new javax.swing.JCheckBox();
        DSCHcheck = new javax.swing.JCheckBox();
        ESCHcheck = new javax.swing.JCheckBox();
        FSCHcheck = new javax.swing.JCheckBox();
        HSCHcheck = new javax.swing.JCheckBox();
        jPanel31 = new javax.swing.JPanel();
        BREScheck = new javax.swing.JCheckBox();
        DREScheck = new javax.swing.JCheckBox();
        EREScheck = new javax.swing.JCheckBox();
        FREScheck = new javax.swing.JCheckBox();
        HREScheck = new javax.swing.JCheckBox();
        jPanel32 = new javax.swing.JPanel();
        BPAScheck = new javax.swing.JCheckBox();
        DPAScheck = new javax.swing.JCheckBox();
        EPAScheck = new javax.swing.JCheckBox();
        FPAScheck = new javax.swing.JCheckBox();
        HPAScheck = new javax.swing.JCheckBox();
        jPanel33 = new javax.swing.JPanel();
        BSUPcheck = new javax.swing.JCheckBox();
        DSUPcheck = new javax.swing.JCheckBox();
        ESUPcheck = new javax.swing.JCheckBox();
        FSUPcheck = new javax.swing.JCheckBox();
        HSUPcheck = new javax.swing.JCheckBox();
        PPanel = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        PluginPanel = new javax.swing.JPanel();
        jButton30 = new javax.swing.JButton();
        jLabel55 = new javax.swing.JLabel();
        jPanel22 = new javax.swing.JPanel();
        jLabel58 = new javax.swing.JLabel();
        jButton32 = new javax.swing.JButton();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel41 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        PyTable = new javax.swing.JTable();
        jLabel60 = new javax.swing.JLabel();
        jPanel45 = new javax.swing.JPanel();
        jLabel74 = new javax.swing.JLabel();
        jLabel75 = new javax.swing.JLabel();
        jLabel76 = new javax.swing.JLabel();
        jLabel77 = new javax.swing.JLabel();
        jLabel78 = new javax.swing.JLabel();
        jLabel81 = new javax.swing.JLabel();
        jPanel46 = new javax.swing.JPanel();
        jLabel80 = new javax.swing.JLabel();
        jLabel82 = new javax.swing.JLabel();
        jPanel47 = new javax.swing.JPanel();
        jLabel87 = new javax.swing.JLabel();
        jLabel85 = new javax.swing.JLabel();
        genbutton = new javax.swing.JButton();
        jLabel84 = new javax.swing.JLabel();
        loadkeysbutton = new javax.swing.JButton();
        jPanel48 = new javax.swing.JPanel();
        jLabel83 = new javax.swing.JLabel();
        usecertificatescheck = new javax.swing.JCheckBox();
        jLabel79 = new javax.swing.JLabel();
        jPanel49 = new javax.swing.JPanel();
        jLabel88 = new javax.swing.JLabel();
        enableadcs = new javax.swing.JButton();
        disableadcs = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        LogText = new javax.swing.JTextArea();
        jPanel4 = new javax.swing.JPanel();
        Panelxxx = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton3 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        StatusLabel = new javax.swing.JLabel();
        adcslabel = new javax.swing.JLabel();

        jLabel62.setText("jLabel62");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DSHub ADC HubSoft created by Pietricica");
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setResizable(false);
        addWindowFocusListener(new java.awt.event.WindowFocusListener()
        {
            public void windowGainedFocus(java.awt.event.WindowEvent evt)
            {
                formWindowGainedFocus(evt);
            }


            public void windowLostFocus(java.awt.event.WindowEvent evt)
            {
            }
        });

        jButton1.setText("Exit");
        jButton1.setToolTipText("Exits the program.");
        jButton1.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Hide interface");
        jButton2.setToolTipText("Hides the Window, but hub keeps running.");
        jButton2.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton2ActionPerformed(evt);
            }
        });

        jTabbedPane1.setBorder(javax.swing
                                       .BorderFactory
                                       .createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTabbedPane1.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                jTabbedPane1MousePressed(evt);
            }
        });

        jPanel1.setToolTipText("Easy startup");
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel5.setText("Welcome to DSHub !");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(370, 20, -1, -1));

        jLabel44.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dshub/ds.jpg"))); // NOI18N
        jPanel1.add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 20, -1, -1));

        jButton33.setText("About DSHub...");
        jButton33.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton33ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton33,
                    new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 130, 130, -1));

        jButton34.setText("License information");
        jButton34.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton34ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton34,
                    new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 160, 130, -1));

        jLabel1.setText(
                "Your hub uses the new ADC protocol, you can read more about it in the Help Tab.");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 60, -1, -1));

        jLabel2.setText(
                "The hub interface is rather simple, but here you will get a simple map overview of it:");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 80, -1, -1));

        jLabel3.setText(
                "The STATUS string in the lower part of the interface always points out what is the hub doing, or how ");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 100, -1, -1));

        jLabel4.setText(
                "the last command was interpreted, or it's result. More information about given commands are in the ");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 120, -1, -1));

        jLabel6.setText("Log tab, among with other events that are triggered on your hub.");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, -1, -1));

        jLabel7.setText(
                "Hub main control buttons such as closing button ( Exit ) and restarting ( Restart) are always visible");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 170, -1, -1));

        jLabel8.setText(
                "on your screen. Also, the Hide interface button that hides the grapchical interface is present. Be careful on using it, because you");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 190, -1, -1));

        jLabel9.setText(
                "can restore the interface only as a registered user with the rightful attributes ( or via console ).");
        jPanel1.add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, -1, -1));

        jLabel14.setText(
                "You can start setting up your hub on the Settings tab, and start making registered accounts on your accounts tab.");
        jPanel1.add(jLabel14, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 240, -1, -1));

        jLabel61.setText(
                "You can also see the bans in your hub on the Bans tab, the current hub statistics in the Stats tab, and you can start setting up forbidden ");
        jPanel1.add(jLabel61, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 260, -1, -1));

        jLabel65.setText(
                "regular expressions for the chat search and more on the Chat Control tab. ");
        jPanel1.add(jLabel65, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 280, -1, -1));

        jLabel66.setText(
                "The Additional Modules and Scripts tab allow you full control to what plugins your hub is running and what scripts are currently active.");
        jPanel1.add(jLabel66, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 310, -1, -1));

        jLabel69.setText(
                "For advanced ADC protocol gurus, you can setup every context for each command on the Advanced tab. ");
        jPanel1.add(jLabel69, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 330, -1, -1));

        jLabel70.setText(
                "ADC and DSHub were never so simple !  Sit back and enjoy your dshub over the world of Direct Connect !");
        jPanel1.add(jLabel70, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 360, -1, -1));

        jTabbedPane1.addTab("Start", null, jPanel1, "");

        jPanel2.setToolTipText("Hub Settings");

        jTabbedPane2.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        jPanel7.setToolTipText("Primary Settings");
        jPanel7.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton7.setText("Save Settings");
        jButton7.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton7ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton7, new org.netbeans.lib.awtextra.AbsoluteConstraints(490, 160, -1, -1));

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder("Hub Settings"));

        namefield.setPreferredSize(new java.awt.Dimension(180, 20));

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel19.setText("Hub name to display in main window.");

        topicfield.setPreferredSize(new java.awt.Dimension(180, 20));

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel18.setText("Current hub topic, to be shown in title bar.");

        regonlycheck.setFont(new java.awt.Font("Tahoma", 0, 10));
        regonlycheck.setText("Registered users only hub.");
        regonlycheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        regonlycheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        regonlycheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                regonlycheckActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel20Layout =
                new org.jdesktop.layout.GroupLayout(jPanel20);
        jPanel20.setLayout(jPanel20Layout);
        jPanel20Layout.setHorizontalGroup(
                jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel20Layout.createSequentialGroup()
                                                 .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jPanel20Layout.createSequentialGroup()
                                                                                       .add(jPanel20Layout
                                                                                                    .createParallelGroup(
                                                                                                            org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                    .add(namefield,
                                                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                                    .add(topicfield,
                                                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                                                       .add(14,
                                                                                            14,
                                                                                            14)
                                                                                       .add(jPanel20Layout
                                                                                                    .createParallelGroup(
                                                                                                            org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                    .add(jLabel18)
                                                                                                    .add(jLabel19)))
                                                                    .add(jPanel20Layout.createSequentialGroup()
                                                                                       .addContainerGap()
                                                                                       .add(regonlycheck)))
                                                 .addContainerGap(71, Short.MAX_VALUE))
                                         );
        jPanel20Layout.setVerticalGroup(
                jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel20Layout.createSequentialGroup()
                                                 .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(namefield,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jLabel19))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jPanel20Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(topicfield,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jLabel18))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(regonlycheck)
                                                 .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE))
                                       );

        jPanel7.add(jPanel20, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 470, 90));

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder("Connection Settings"));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel17.setText("Current ports for server listening.");

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder("Proxy Settings"));

        proxycheck.setFont(new java.awt.Font("Tahoma", 0, 10));
        proxycheck.setText("Use Proxy for http access required by some modules");
        proxycheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        proxycheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        proxycheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                proxycheckActionPerformed(evt);
            }
        });

        proxyhostfield.setPreferredSize(new java.awt.Dimension(180, 20));

        jLabel67.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel67.setText("Proxy host");

        proxyportfield.setPreferredSize(new java.awt.Dimension(180, 20));

        jLabel68.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel68.setText("Proxy port");

        org.jdesktop.layout.GroupLayout jPanel19Layout =
                new org.jdesktop.layout.GroupLayout(jPanel19);
        jPanel19.setLayout(jPanel19Layout);
        jPanel19Layout.setHorizontalGroup(
                jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel19Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(proxycheck)
                                                                    .add(jPanel19Layout.createSequentialGroup()
                                                                                       .add(jPanel19Layout
                                                                                                    .createParallelGroup(
                                                                                                            org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                                            false)
                                                                                                    .add(proxyhostfield,
                                                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                         Short.MAX_VALUE)
                                                                                                    .add(proxyportfield,
                                                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                         Short.MAX_VALUE))
                                                                                       .add(14,
                                                                                            14,
                                                                                            14)
                                                                                       .add(jPanel19Layout
                                                                                                    .createParallelGroup(
                                                                                                            org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                    .add(jLabel68)
                                                                                                    .add(jLabel67))))
                                                 .addContainerGap())
                                         );
        jPanel19Layout.setVerticalGroup(
                jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel19Layout.createSequentialGroup()
                                                 .add(proxycheck)
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(proxyhostfield,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jLabel67))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jPanel19Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(proxyportfield,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jLabel68))
                                                 .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE))
                                       );

        hubhostfield.setPreferredSize(new java.awt.Dimension(180, 20));

        jLabel50.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel50.setText("Hub host ( address ) ( enter your DNS here )");

        addnewport.setText("Add new");
        addnewport.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                addnewportActionPerformed(evt);
            }
        });

        remport.setText("Remove");
        remport.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                remportActionPerformed(evt);
            }
        });

        portlist.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Value", "Status", "Message"
                }
        )
        {
            Class[] types = new Class[]{
                    java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[]{
                    false, false, false
            };


            public Class getColumnClass(int columnIndex)
            {
                return types[columnIndex];
            }


            public boolean isCellEditable(int rowIndex, int columnIndex)
            {
                return canEdit[columnIndex];
            }
        });
        portlist.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                portlistKeyPressed(evt);
            }


            public void keyTyped(java.awt.event.KeyEvent evt)
            {
                portlistKeyTyped(evt);
            }
        });
        pane5.setViewportView(portlist);

        org.jdesktop.layout.GroupLayout jPanel21Layout =
                new org.jdesktop.layout.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
                jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel21Layout.createSequentialGroup()
                                                 .add(jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jPanel21Layout.createSequentialGroup()
                                                                                       .add(pane5,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                            283,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                       .add(18,
                                                                                            18,
                                                                                            18)
                                                                                       .add(jPanel21Layout
                                                                                                    .createParallelGroup(
                                                                                                            org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                    .add(jLabel17)
                                                                                                    .add(jPanel21Layout
                                                                                                                 .createSequentialGroup()
                                                                                                                 .add(27,
                                                                                                                      27,
                                                                                                                      27)
                                                                                                                 .add(jPanel21Layout
                                                                                                                              .createParallelGroup(
                                                                                                                                      org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                                                                      false)
                                                                                                                              .add(org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                                                   remport,
                                                                                                                                   org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                   org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                   Short.MAX_VALUE)
                                                                                                                              .add(org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                                                   addnewport,
                                                                                                                                   org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                   86,
                                                                                                                                   Short.MAX_VALUE)))))
                                                                    .add(jPanel21Layout.createParallelGroup(
                                                                            org.jdesktop.layout.GroupLayout.LEADING)
                                                                                       .add(jPanel21Layout
                                                                                                    .createSequentialGroup()
                                                                                                    .addContainerGap()
                                                                                                    .add(jPanel19,
                                                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                                                       .add(org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                            jPanel21Layout
                                                                                                    .createSequentialGroup()
                                                                                                    .add(hubhostfield,
                                                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                                    .add(17,
                                                                                                         17,
                                                                                                         17)
                                                                                                    .add(jLabel50))))
                                                 .addContainerGap())
                                         );
        jPanel21Layout.setVerticalGroup(
                jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel21Layout.createSequentialGroup()
                                                 .add(jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jPanel21Layout.createSequentialGroup()
                                                                                       .add(jLabel17)
                                                                                       .addPreferredGap(
                                                                                               org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                       .add(addnewport)
                                                                                       .addPreferredGap(
                                                                                               org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                       .add(remport))
                                                                    .add(pane5,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         100,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
                                                                  7,
                                                                  Short.MAX_VALUE)
                                                 .add(jPanel21Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(hubhostfield,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jLabel50))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jPanel19,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                      100,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                       );

        jPanel7.add(jPanel21, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 90, 470, 260));

        jTabbedPane2.addTab("Main Settings", jPanel7);

        jPanel8.setToolTipText("Hub Restrictions for Users");

        fieldtimeout.setText("20");

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel20.setText(
                "Number of seconds for hub to wait for connecting users until kick them out, Integer");

        maxnifield.setText("64");

        minnifield.setText("1");

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel21.setText("Maximum nick size, integer. ");

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel22.setText("Minimum nick size, integer.");

        maxdefield.setText("120");

        maxsharefield.setText("1125899906842624");

        minsharefield.setText("0");

        maxslfield.setText("1000");

        minslfield.setText("0");

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel23.setText("Maximum description size, integer.");

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel24.setText("Maximum share size, MiB, long integer. ");

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel25.setText("Minimum share size, MiB, long integer. ");

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel26.setText("Maximum slot number, integer. ");

        jLabel27.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel27.setText("Minimum slot number, integer. ");

        maxemfield.setText("128");

        maxhubsopfield.setText("40");

        maxhubsregfield.setText("30");

        jLabel28.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel28.setText("Maximum e-mail string size, integer. ");

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel29.setText("Maximum hubs where user is op, integer.");

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel30.setText("Maximum hubs where user is reg, integer.");

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel31.setText("Maximum hubs where user is user, integer.");

        maxhubsuserfield.setText("200");

        jButton8.setText("Save Settings");
        jButton8.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton8ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel8Layout =
                new org.jdesktop.layout.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
                jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                             .add(jPanel8Layout.createSequentialGroup()
                                               .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                 .add(jPanel8Layout.createSequentialGroup()
                                                                                   .add(27, 27, 27)
                                                                                   .add(jPanel8Layout
                                                                                                .createParallelGroup(
                                                                                                        org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                        false)
                                                                                                .add(maxhubsuserfield)
                                                                                                .add(maxhubsregfield)
                                                                                                .add(maxhubsopfield)
                                                                                                .add(maxemfield)
                                                                                                .add(minslfield)
                                                                                                .add(maxslfield)
                                                                                                .add(minsharefield)
                                                                                                .add(maxsharefield)
                                                                                                .add(maxdefield)
                                                                                                .add(org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                                     minnifield)
                                                                                                .add(org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                                     maxnifield)
                                                                                                .add(fieldtimeout,
                                                                                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                     131,
                                                                                                     Short.MAX_VALUE))
                                                                                   .add(34, 34, 34)
                                                                                   .add(jPanel8Layout
                                                                                                .createParallelGroup(
                                                                                                        org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                .add(jLabel20)
                                                                                                .add(jLabel21)
                                                                                                .add(jLabel22)
                                                                                                .add(jLabel23)
                                                                                                .add(jLabel24)
                                                                                                .add(jLabel25)
                                                                                                .add(jLabel26)
                                                                                                .add(jLabel27)
                                                                                                .add(jLabel28)
                                                                                                .add(jLabel29)
                                                                                                .add(jLabel30)
                                                                                                .add(jLabel31)))
                                                                 .add(jPanel8Layout.createSequentialGroup()
                                                                                   .add(264,
                                                                                        264,
                                                                                        264)
                                                                                   .add(jButton8)))
                                               .addContainerGap(75, Short.MAX_VALUE))
                                        );
        jPanel8Layout.setVerticalGroup(
                jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                             .add(jPanel8Layout.createSequentialGroup()
                                               .addContainerGap()
                                               .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                 .add(jLabel20)
                                                                 .add(fieldtimeout,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                               .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                 .add(maxnifield,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                 .add(jLabel21))
                                               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                               .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                 .add(minnifield,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                 .add(jLabel22))
                                               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                               .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                 .add(maxdefield,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                 .add(jLabel23))
                                               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                               .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                 .add(maxsharefield,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                 .add(jLabel24))
                                               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                               .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                 .add(minsharefield,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                 .add(jLabel25))
                                               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                               .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                 .add(maxslfield,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                 .add(jLabel26))
                                               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                               .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                 .add(minslfield,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                 .add(jLabel27))
                                               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                               .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                 .add(maxemfield,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                 .add(jLabel28))
                                               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                               .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                 .add(maxhubsopfield,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                 .add(jLabel29))
                                               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                               .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                 .add(maxhubsregfield,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                 .add(jLabel30))
                                               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                               .add(jPanel8Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                 .add(maxhubsuserfield,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                 .add(jLabel31))
                                               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
                                                                13,
                                                                Short.MAX_VALUE)
                                               .add(jButton8)
                                               .addContainerGap())
                                      );

        jTabbedPane2.addTab("Restrictions1", jPanel8);

        jPanel13.setToolTipText("Hub Restrictions");

        maxusersfield.setText("1000");

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel32.setText("Maximum number of online users, integer.");

        jLabel35.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel35.setText("Chars that could be used for a nick, String. ");

        maxschcharsfield.setText("256");
        maxschcharsfield.setPreferredSize(new java.awt.Dimension(130, 19));

        minschcharsfield.setText("3");
        minschcharsfield.setPreferredSize(new java.awt.Dimension(130, 19));

        jLabel36.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel36.setText("Maximum search chars, integer.");

        jLabel37.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel37.setText("Minimum search chars, integer.");

        nickcharsfield.setColumns(20);
        nickcharsfield.setRows(5);
        jScrollPane7.setViewportView(nickcharsfield);

        jButton9.setText("Save Settings");
        jButton9.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton9ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel13Layout =
                new org.jdesktop.layout.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
                jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel13Layout.createSequentialGroup()
                                                 .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jPanel13Layout.createSequentialGroup()
                                                                                       .add(28,
                                                                                            28,
                                                                                            28)
                                                                                       .add(jPanel13Layout
                                                                                                    .createParallelGroup(
                                                                                                            org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                    .add(jPanel13Layout
                                                                                                                 .createSequentialGroup()
                                                                                                                 .add(jPanel13Layout
                                                                                                                              .createParallelGroup(
                                                                                                                                      org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                                                                      false)
                                                                                                                              .add(org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                                                   minschcharsfield,
                                                                                                                                   org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                   org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                   Short.MAX_VALUE)
                                                                                                                              .add(org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                                                   maxschcharsfield,
                                                                                                                                   org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                   org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                                   Short.MAX_VALUE))
                                                                                                                 .add(21,
                                                                                                                      21,
                                                                                                                      21)
                                                                                                                 .add(jPanel13Layout
                                                                                                                              .createParallelGroup(
                                                                                                                                      org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                                              .add(jLabel36)
                                                                                                                              .add(jLabel37)))
                                                                                                    .add(jPanel13Layout
                                                                                                                 .createSequentialGroup()
                                                                                                                 .add(maxusersfield,
                                                                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                      129,
                                                                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                                                 .add(21,
                                                                                                                      21,
                                                                                                                      21)
                                                                                                                 .add(jLabel32))
                                                                                                    .add(jScrollPane7,
                                                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                         463,
                                                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                                                    .add(jPanel13Layout.createSequentialGroup()
                                                                                       .add(149,
                                                                                            149,
                                                                                            149)
                                                                                       .add(jLabel35))
                                                                    .add(jPanel13Layout.createSequentialGroup()
                                                                                       .add(256,
                                                                                            256,
                                                                                            256)
                                                                                       .add(jButton9)))
                                                 .addContainerGap(154, Short.MAX_VALUE))
                                         );
        jPanel13Layout.setVerticalGroup(
                jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel13Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(maxusersfield,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jLabel32))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jScrollPane7,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                      48,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jLabel35)
                                                 .add(17, 17, 17)
                                                 .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(maxschcharsfield,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jLabel36))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jPanel13Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(minschcharsfield,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jLabel37))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
                                                                  107,
                                                                  Short.MAX_VALUE)
                                                 .add(jButton9)
                                                 .add(69, 69, 69))
                                       );

        jTabbedPane2.addTab("Restrictions2", jPanel13);

        xxx.setToolTipText("Spam settings");

        maxchatmsgfield.setText("512");

        jLabel38.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel38.setText("Maximum chat message size, integer.");

        chatintervalfield.setText("500");

        jLabel43.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel43.setText("Interval between chat lines, millis, Integer.");

        jButton10.setText("Save Settings");
        jButton10.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton10ActionPerformed(evt);
            }
        });

        automagicsearchfield.setText("36");

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel15.setText("Interval between automagic searches for each user, seconds, Integer.");

        searchlogbasefield.setText("2000");

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel16.setText("Logarithmic base for user searches interval,millis, Integer.");

        searchstepsfield.setText("6");

        jLabel45.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel45.setText("Maximum nr of search steps allowed until reset needed, Integer.");

        searchspamresetfield.setText("300");

        jLabel46.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel46.setText("Interval until search_steps is being reset, seconds, Integer.");

        msgsearchspamfield.setColumns(20);
        msgsearchspamfield.setRows(5);
        jScrollPane8.setViewportView(msgsearchspamfield);

        jLabel47.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel47.setText("Message that appears as a result when search is delayed, String.");

        jButton5.setText("[?]");
        jButton5.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton5ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout xxxLayout = new org.jdesktop.layout.GroupLayout(xxx);
        xxx.setLayout(xxxLayout);
        xxxLayout.setHorizontalGroup(
                xxxLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                         .add(xxxLayout.createSequentialGroup()
                                       .add(xxxLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                     .add(xxxLayout.createSequentialGroup()
                                                                   .add(28, 28, 28)
                                                                   .add(xxxLayout.createParallelGroup(
                                                                           org.jdesktop.layout.GroupLayout.LEADING)
                                                                                 .add(jScrollPane8,
                                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                      468,
                                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                 .add(xxxLayout.createSequentialGroup()
                                                                                               .add(xxxLayout
                                                                                                            .createParallelGroup(
                                                                                                                    org.jdesktop.layout.GroupLayout.LEADING,
                                                                                                                    false)
                                                                                                            .add(searchstepsfield)
                                                                                                            .add(searchlogbasefield)
                                                                                                            .add(automagicsearchfield)
                                                                                                            .add(chatintervalfield)
                                                                                                            .add(maxchatmsgfield,
                                                                                                                 org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                 129,
                                                                                                                 Short.MAX_VALUE)
                                                                                                            .add(searchspamresetfield))
                                                                                               .add(25,
                                                                                                    25,
                                                                                                    25)
                                                                                               .add(xxxLayout
                                                                                                            .createParallelGroup(
                                                                                                                    org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                            .add(jLabel16)
                                                                                                            .add(jLabel15)
                                                                                                            .add(jLabel38)
                                                                                                            .add(jLabel43)
                                                                                                            .add(xxxLayout
                                                                                                                         .createSequentialGroup()
                                                                                                                         .addPreferredGap(
                                                                                                                                 org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                                                         .add(xxxLayout
                                                                                                                                      .createParallelGroup(
                                                                                                                                              org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                                                      .add(jLabel46)
                                                                                                                                      .add(xxxLayout
                                                                                                                                                   .createSequentialGroup()
                                                                                                                                                   .add(jLabel45)
                                                                                                                                                   .add(53,
                                                                                                                                                        53,
                                                                                                                                                        53)
                                                                                                                                                   .add(jButton5))))))))
                                                     .add(xxxLayout.createSequentialGroup()
                                                                   .add(95, 95, 95)
                                                                   .add(jLabel47))
                                                     .add(xxxLayout.createSequentialGroup()
                                                                   .add(255, 255, 255)
                                                                   .add(jButton10)))
                                       .addContainerGap(80, Short.MAX_VALUE))
                                    );
        xxxLayout.setVerticalGroup(
                xxxLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                         .add(xxxLayout.createSequentialGroup()
                                       .addContainerGap()
                                       .add(xxxLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                     .add(maxchatmsgfield,
                                                          org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                          org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                          org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                     .add(jLabel38))
                                       .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                       .add(xxxLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                     .add(chatintervalfield,
                                                          org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                          org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                          org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                     .add(jLabel43))
                                       .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                       .add(xxxLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                     .add(automagicsearchfield,
                                                          org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                          org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                          org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                     .add(jLabel15))
                                       .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                       .add(xxxLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                     .add(searchlogbasefield,
                                                          org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                          org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                          org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                     .add(jLabel16))
                                       .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                       .add(xxxLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                     .add(jLabel45)
                                                     .add(searchstepsfield,
                                                          org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                          org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                          org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                     .add(jButton5))
                                       .add(9, 9, 9)
                                       .add(xxxLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                     .add(searchspamresetfield,
                                                          org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                          org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                          org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                     .add(jLabel46))
                                       .add(21, 21, 21)
                                       .add(jScrollPane8,
                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                            59,
                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                       .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                       .add(jLabel47)
                                       .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
                                                        64,
                                                        Short.MAX_VALUE)
                                       .add(jButton10)
                                       .addContainerGap())
                                  );

        jTabbedPane2.addTab("Spam Settings", xxx);

        miscpanel.setToolTipText("Misc Settings");
        miscpanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton11.setText("Save Settings");
        jButton11.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton11ActionPerformed(evt);
            }
        });
        miscpanel.add(jButton11,
                      new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 160, -1, -1));

        jPanel35.setBorder(javax.swing.BorderFactory.createTitledBorder("Bots settings"));

        opchatnamefield.setText("OpChat");
        opchatnamefield.setMinimumSize(new java.awt.Dimension(130, 20));
        opchatnamefield.setPreferredSize(new java.awt.Dimension(140, 20));

        botnamefield.setText("DSHub");
        botnamefield.setPreferredSize(new java.awt.Dimension(140, 20));

        jLabel48.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel48.setText("Hub security bot name.");

        botdescfield.setText("www.death-squad.ro/dshub");
        botdescfield.setPreferredSize(new java.awt.Dimension(140, 20));

        jLabel49.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel49.setText("Hub security bot description.");

        opchatdescfield.setText("BoT");
        opchatdescfield.setPreferredSize(new java.awt.Dimension(140, 20));

        jLabel33.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel33.setText("The Operator Chat Bot Nick.");

        jLabel34.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel34.setText("The Operator Chat Bot Description.");

        org.jdesktop.layout.GroupLayout jPanel35Layout =
                new org.jdesktop.layout.GroupLayout(jPanel35);
        jPanel35.setLayout(jPanel35Layout);
        jPanel35Layout.setHorizontalGroup(
                jPanel35Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel35Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel35Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jPanel35Layout.createSequentialGroup()
                                                                                       .add(opchatnamefield,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                       .add(18,
                                                                                            18,
                                                                                            18)
                                                                                       .add(jLabel33))
                                                                    .add(jPanel35Layout.createSequentialGroup()
                                                                                       .add(opchatdescfield,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                       .add(18,
                                                                                            18,
                                                                                            18)
                                                                                       .add(jLabel34))
                                                                    .add(jPanel35Layout.createSequentialGroup()
                                                                                       .add(jPanel35Layout
                                                                                                    .createParallelGroup(
                                                                                                            org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                    .add(botdescfield,
                                                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                                    .add(botnamefield,
                                                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                                                       .add(18,
                                                                                            18,
                                                                                            18)
                                                                                       .add(jPanel35Layout
                                                                                                    .createParallelGroup(
                                                                                                            org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                    .add(jLabel49)
                                                                                                    .add(jLabel48))))
                                                 .addContainerGap(141, Short.MAX_VALUE))
                                         );
        jPanel35Layout.setVerticalGroup(
                jPanel35Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel35Layout.createSequentialGroup()
                                                 .add(jPanel35Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(opchatnamefield,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jLabel33))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                 .add(jPanel35Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(opchatdescfield,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jLabel34))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                 .add(jPanel35Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(botnamefield,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jLabel48))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jPanel35Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(botdescfield,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jLabel49))
                                                 .addContainerGap(15, Short.MAX_VALUE))
                                       );

        miscpanel.add(jPanel35,
                      new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 480, 150));

        jPanel36.setBorder(javax.swing.BorderFactory.createTitledBorder("Redirects"));

        redirecturl.setMinimumSize(new java.awt.Dimension(130, 20));
        redirecturl.setPreferredSize(new java.awt.Dimension(140, 20));

        jLabel53.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel53.setText("The main redirect URL to send faulty users ( or default redirects )");

        org.jdesktop.layout.GroupLayout jPanel36Layout =
                new org.jdesktop.layout.GroupLayout(jPanel36);
        jPanel36.setLayout(jPanel36Layout);
        jPanel36Layout.setHorizontalGroup(
                jPanel36Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel36Layout.createSequentialGroup()
                                                 .add(10, 10, 10)
                                                 .add(redirecturl,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                 .add(18, 18, 18)
                                                 .add(jLabel53)
                                                 .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE))
                                         );
        jPanel36Layout.setVerticalGroup(
                jPanel36Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel36Layout.createSequentialGroup()
                                                 .add(jPanel36Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(redirecturl,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jLabel53))
                                                 .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE))
                                       );

        miscpanel.add(jPanel36,
                      new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 170, 480, 50));

        jPanel38.setBorder(javax.swing.BorderFactory.createTitledBorder("Kick settings"));

        kicktimefield.setText("300");
        kicktimefield.setPreferredSize(new java.awt.Dimension(140, 20));

        jLabel40.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel40.setText("The time to ban a user with a kick, in seconds.");

        org.jdesktop.layout.GroupLayout jPanel38Layout =
                new org.jdesktop.layout.GroupLayout(jPanel38);
        jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(
                jPanel38Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel38Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(kicktimefield,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                 .add(18, 18, 18)
                                                 .add(jLabel40)
                                                 .addContainerGap(92, Short.MAX_VALUE))
                                         );
        jPanel38Layout.setVerticalGroup(
                jPanel38Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel38Layout.createSequentialGroup()
                                                 .add(jPanel38Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(kicktimefield,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jLabel40))
                                                 .addContainerGap(13, Short.MAX_VALUE))
                                       );

        miscpanel.add(jPanel38,
                      new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 230, 480, 60));

        jPanel39.setBorder(javax.swing.BorderFactory.createTitledBorder("History settings"));

        historylinesfield.setText("50");
        historylinesfield.setPreferredSize(new java.awt.Dimension(140, 20));

        jLabel39.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel39.setText("Number of lines to keep in chat and command history.");

        org.jdesktop.layout.GroupLayout jPanel39Layout =
                new org.jdesktop.layout.GroupLayout(jPanel39);
        jPanel39.setLayout(jPanel39Layout);
        jPanel39Layout.setHorizontalGroup(
                jPanel39Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel39Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(historylinesfield,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                 .add(18, 18, 18)
                                                 .add(jLabel39)
                                                 .addContainerGap(56, Short.MAX_VALUE))
                                         );
        jPanel39Layout.setVerticalGroup(
                jPanel39Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel39Layout.createSequentialGroup()
                                                 .add(jPanel39Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(historylinesfield,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jLabel39))
                                                 .addContainerGap(13, Short.MAX_VALUE))
                                       );

        miscpanel.add(jPanel39,
                      new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 300, 480, 60));

        jTabbedPane2.addTab("Misc Settings1", miscpanel);

        jPanel37.setToolTipText("Miscellaneous Settings");
        jPanel37.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jPanel34.setBorder(javax.swing.BorderFactory.createTitledBorder("Messages"));

        jLabel42.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel42.setText("The additional message to be shown to connecting users when hub full.");

        msgfullfield.setColumns(20);
        msgfullfield.setRows(5);
        jScrollPane6.setViewportView(msgfullfield);

        jLabel41.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel41.setText("The aditional message to show to banned users when connecting.");

        msgbannedfield.setColumns(20);
        msgbannedfield.setRows(5);
        jScrollPane5.setViewportView(msgbannedfield);

        org.jdesktop.layout.GroupLayout jPanel34Layout =
                new org.jdesktop.layout.GroupLayout(jPanel34);
        jPanel34.setLayout(jPanel34Layout);
        jPanel34Layout.setHorizontalGroup(
                jPanel34Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel34Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel34Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jScrollPane6,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         568,
                                                                         Short.MAX_VALUE)
                                                                    .add(jLabel42)
                                                                    .add(jLabel41)
                                                                    .add(jScrollPane5,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         568,
                                                                         Short.MAX_VALUE))
                                                 .addContainerGap())
                                         );
        jPanel34Layout.setVerticalGroup(
                jPanel34Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel34Layout.createSequentialGroup()
                                                 .add(jLabel42)
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jScrollPane6,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                 .add(jLabel41)
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jScrollPane5,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                 .addContainerGap(42, Short.MAX_VALUE))
                                       );

        jPanel37.add(jPanel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, 600, 250));

        jButton31.setText("Save Settings");
        jButton31.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton31ActionPerformed(evt);
            }
        });
        jPanel37.add(jButton31,
                     new org.netbeans.lib.awtextra.AbsoluteConstraints(270, 340, -1, -1));

        jPanel40.setBorder(javax.swing.BorderFactory.createTitledBorder("Logging settings"));

        savelogscheck.setFont(new java.awt.Font("Tahoma", 0, 10));
        savelogscheck.setText("Save logs to file.");
        savelogscheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        savelogscheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        savelogscheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                savelogscheckActionPerformed(evt);
            }
        });

        jLabel56.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel56.setText("Logs have their file name the date when hub was started.");

        jLabel57.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel57.setText("They are saved to /logs directory.");

        org.jdesktop.layout.GroupLayout jPanel40Layout =
                new org.jdesktop.layout.GroupLayout(jPanel40);
        jPanel40.setLayout(jPanel40Layout);
        jPanel40Layout.setHorizontalGroup(
                jPanel40Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel40Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(savelogscheck)
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                 .add(jPanel40Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jLabel57)
                                                                    .add(jLabel56))
                                                 .addContainerGap(102, Short.MAX_VALUE))
                                         );
        jPanel40Layout.setVerticalGroup(
                jPanel40Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel40Layout.createSequentialGroup()
                                                 .add(jPanel40Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(savelogscheck)
                                                                    .add(jLabel56))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jLabel57)
                                                 .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE))
                                       );

        jPanel37.add(jPanel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, 480, 60));

        jTabbedPane2.addTab("Misc Settings2", jPanel37);

        jPanel43.setBorder(javax.swing.BorderFactory.createTitledBorder("Internationalization"));

        jLabel72.setText("Select the language you want for the DSHub interface:");

        langcombo.setMaximumRowCount(15);
        langcombo.setModel(new javax.swing.DefaultComboBoxModel(new String[]{ "Item 1", "Item 2",
                                                                              "Item 3",
                                                                              "Item 4" }));
        langcombo.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                langcomboMouseClicked(evt);
            }
        });
        langcombo.addItemListener(new java.awt.event.ItemListener()
        {
            public void itemStateChanged(java.awt.event.ItemEvent evt)
            {
                langcomboItemStateChanged(evt);
            }
        });
        langcombo.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                langcomboActionPerformed(evt);
            }
        });
        langcombo.addPropertyChangeListener(new java.beans.PropertyChangeListener()
        {
            public void propertyChange(java.beans.PropertyChangeEvent evt)
            {
                langcomboPropertyChange(evt);
            }
        });

        jLabel59.setText(
                "The two small letters is the language code, and the two capitals represent the country code.");

        org.jdesktop.layout.GroupLayout jPanel43Layout =
                new org.jdesktop.layout.GroupLayout(jPanel43);
        jPanel43.setLayout(jPanel43Layout);
        jPanel43Layout.setHorizontalGroup(
                jPanel43Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel43Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel43Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jPanel43Layout.createSequentialGroup()
                                                                                       .add(jLabel72)
                                                                                       .add(27,
                                                                                            27,
                                                                                            27)
                                                                                       .add(langcombo,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                                    .add(jLabel59))
                                                 .addContainerGap(133, Short.MAX_VALUE))
                                         );
        jPanel43Layout.setVerticalGroup(
                jPanel43Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel43Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel43Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(jLabel72)
                                                                    .add(langcombo,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jLabel59)
                                                 .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE))
                                       );

        jPanel44.setBorder(javax.swing.BorderFactory.createTitledBorder("Other Settings"));

        command_pmcheck.setText("Send all hub commands to PM");
        command_pmcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                command_pmcheckActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel44Layout =
                new org.jdesktop.layout.GroupLayout(jPanel44);
        jPanel44.setLayout(jPanel44Layout);
        jPanel44Layout.setHorizontalGroup(
                jPanel44Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel44Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(command_pmcheck)
                                                 .addContainerGap(440, Short.MAX_VALUE))
                                         );
        jPanel44Layout.setVerticalGroup(
                jPanel44Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel44Layout.createSequentialGroup()
                                                 .add(command_pmcheck)
                                                 .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE))
                                       );

        org.jdesktop.layout.GroupLayout jPanel42Layout =
                new org.jdesktop.layout.GroupLayout(jPanel42);
        jPanel42.setLayout(jPanel42Layout);
        jPanel42Layout.setHorizontalGroup(
                jPanel42Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel42Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel42Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jPanel44,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         Short.MAX_VALUE)
                                                                    .add(jPanel43,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                 .addContainerGap())
                                         );
        jPanel42Layout.setVerticalGroup(
                jPanel42Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel42Layout.createSequentialGroup()
                                                 .add(33, 33, 33)
                                                 .add(jPanel43,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                 .add(jPanel44,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                 .addContainerGap(174, Short.MAX_VALUE))
                                       );

        jTabbedPane2.addTab("Other settings", jPanel42);

        org.jdesktop.layout.GroupLayout jPanel2Layout =
                new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                             .add(jPanel2Layout.createSequentialGroup()
                                               .addContainerGap()
                                               .add(jTabbedPane2,
                                                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                    735,
                                                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                               .addContainerGap(12, Short.MAX_VALUE))
                                        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                             .add(jPanel2Layout.createSequentialGroup()
                                               .addContainerGap()
                                               .add(jTabbedPane2,
                                                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                    369,
                                                    Short.MAX_VALUE)
                                               .addContainerGap())
                                      );

        jTabbedPane1.addTab("Settings", null, jPanel2, "Hub Settings...");

        jPanel3.setToolTipText("The Hub Accounts");

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 14));
        jLabel10.setText("Here you can modify the hub accounts");

        jLabel11.setText("Reg new user by inputing either CID or the nick (if logged in):");

        jTextField1.setToolTipText("Enter the CID/Nick(his CID will be added) you want to add");

        jButton4.setText("Reg !");
        jButton4.setToolTipText("Adds the CID/nick to database");
        jButton4.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel12.setText(
                "Note: Registrations are CID only, if you enter a nick, his CID will be added. Press delete on an account to ureg it.");

        jScrollPane2.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                jScrollPane2KeyPressed(evt);
            }
        });

        AccountTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "CID", "Last Nick:", "Last IP:", "Regged By:", "Regged At:"
                }
        )
        {
            Class[] types = new Class[]{
                    java.lang.String.class, java.lang.String.class, java.lang.String.class,
                    java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean[]{
                    true, false, false, false, false
            };


            public Class getColumnClass(int columnIndex)
            {
                return types[columnIndex];
            }


            public boolean isCellEditable(int rowIndex, int columnIndex)
            {
                return canEdit[columnIndex];
            }
        });
        AccountTable.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                AccountTableKeyPressed(evt);
            }


            public void keyTyped(java.awt.event.KeyEvent evt)
            {
                AccountTableKeyTyped(evt);
            }
        });
        jScrollPane2.setViewportView(AccountTable);

        jButton22.setText("Edit Account");
        jButton22.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton22ActionPerformed(evt);
            }
        });

        jButton35.setText("Delete Account");
        jButton35.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton35ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout =
                new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                             .add(jPanel3Layout.createSequentialGroup()
                                               .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                 .add(jPanel3Layout.createSequentialGroup()
                                                                                   .add(230,
                                                                                        230,
                                                                                        230)
                                                                                   .add(jLabel10))
                                                                 .add(jPanel3Layout.createSequentialGroup()
                                                                                   .add(78, 78, 78)
                                                                                   .add(jLabel12))
                                                                 .add(jPanel3Layout.createSequentialGroup()
                                                                                   .addContainerGap()
                                                                                   .add(jPanel3Layout
                                                                                                .createParallelGroup(
                                                                                                        org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                .add(jPanel3Layout
                                                                                                             .createSequentialGroup()
                                                                                                             .add(jLabel11)
                                                                                                             .addPreferredGap(
                                                                                                                     org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                                             .add(jTextField1,
                                                                                                                  org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                  299,
                                                                                                                  org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                                             .addPreferredGap(
                                                                                                                     org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                                             .add(jButton4))
                                                                                                .add(jPanel3Layout
                                                                                                             .createSequentialGroup()
                                                                                                             .add(jScrollPane2,
                                                                                                                  org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                  624,
                                                                                                                  org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                                             .addPreferredGap(
                                                                                                                     org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                                             .add(jPanel3Layout
                                                                                                                          .createParallelGroup(
                                                                                                                                  org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                                          .add(jButton35,
                                                                                                                               org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                               107,
                                                                                                                               Short.MAX_VALUE)
                                                                                                                          .add(jButton22,
                                                                                                                               org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                                                               107,
                                                                                                                               Short.MAX_VALUE))))))
                                               .addContainerGap())
                                        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                             .add(jPanel3Layout.createSequentialGroup()
                                               .add(jLabel10)
                                               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                               .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                 .add(jPanel3Layout.createSequentialGroup()
                                                                                   .add(jButton22,
                                                                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                        97,
                                                                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                   .addPreferredGap(
                                                                                           org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                   .add(jButton35,
                                                                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                        52,
                                                                                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                                 .add(jScrollPane2,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                      302,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED,
                                                                12,
                                                                Short.MAX_VALUE)
                                               .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                 .add(jLabel11)
                                                                 .add(jTextField1,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                 .add(jButton4))
                                               .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                               .add(jLabel12)
                                               .addContainerGap())
                                      );

        jTabbedPane1.addTab("Accounts", null, jPanel3, "The Hub Accounts");

        jPanel5.setToolTipText("Ban Handler");

        BanTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{

                },
                new String[]{
                        "Type", "Reason", "Who banned", "Time Issued", "Nick", "IP", "CID",
                        "Remaining time"
                }
        )
        {
            Class[] types = new Class[]{
                    java.lang.Integer.class, java.lang.String.class, java.lang.String.class,
                    java.lang.String.class, java.lang.String.class, java.lang.String.class,
                    java.lang.String.class, java.lang.Long.class
            };


            public Class getColumnClass(int columnIndex)
            {
                return types[columnIndex];
            }
        });
        jScrollPane10.setViewportView(BanTable);

        org.jdesktop.layout.GroupLayout jPanel5Layout =
                new org.jdesktop.layout.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
                jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                             .add(org.jdesktop.layout.GroupLayout.TRAILING,
                                  jPanel5Layout.createSequentialGroup()
                                               .addContainerGap()
                                               .add(jScrollPane10,
                                                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                    737,
                                                    Short.MAX_VALUE)
                                               .addContainerGap())
                                        );
        jPanel5Layout.setVerticalGroup(
                jPanel5Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                             .add(org.jdesktop.layout.GroupLayout.TRAILING,
                                  jPanel5Layout.createSequentialGroup()
                                               .addContainerGap()
                                               .add(jScrollPane10,
                                                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                    340,
                                                    Short.MAX_VALUE)
                                               .add(40, 40, 40))
                                      );

        jTabbedPane1.addTab("Bans", null, jPanel5, "Ban Handler...");

        jPanel6.setToolTipText("Some Hub Statistics...");
        jPanel6.addMouseMotionListener(new java.awt.event.MouseMotionAdapter()
        {
            public void mouseMoved(java.awt.event.MouseEvent evt)
            {
                jPanel6MouseMoved(evt);
            }
        });
        jPanel6.addFocusListener(new java.awt.event.FocusAdapter()
        {
            public void focusGained(java.awt.event.FocusEvent evt)
            {
                jPanel6FocusGained(evt);
            }


            public void focusLost(java.awt.event.FocusEvent evt)
            {
                jPanel6FocusLost(evt);
            }
        });
        jPanel6.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jPanel6MouseClicked(evt);
            }
        });
        jPanel6.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jTextArea2.setColumns(20);
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

        jPanel6.add(jScrollPane3,
                    new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 700, 80));

        jButton6.setText("Update Statistics");
        jButton6.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton6ActionPerformed(evt);
            }
        });
        jPanel6.add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(310, 100, -1, -1));

        jLabel89.setText("DSHub running on ");
        jPanel6.add(jLabel89, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        osname.setText("jLabel90");
        jPanel6.add(osname, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 130, 140, -1));

        jLabel90.setText("Operating system version:");
        jPanel6.add(jLabel90, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 150, 140, -1));

        osversion.setText("jLabel91");
        jPanel6.add(osversion,
                    new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 150, 110, -1));

        jLabel91.setText("System architecture:");
        jPanel6.add(jLabel91, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 170, -1, -1));

        osarch.setText("osarch");
        jPanel6.add(osarch, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 170, 80, -1));

        jLabel92.setText("Java Runtime Environment: ");
        jPanel6.add(jLabel92, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 190, -1, -1));

        jrename.setText("jLabel93");
        jPanel6.add(jrename, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 190, -1, -1));

        jLabel93.setText("Java Runtime Environment provider:");
        jPanel6.add(jLabel93, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 210, -1, -1));

        jreprovider.setText("jLabel94");
        jPanel6.add(jreprovider,
                    new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 210, -1, -1));

        jLabel94.setText("Current processors available:");
        jPanel6.add(jLabel94, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 230, -1, -1));

        cpunumber.setText("jLabel95");
        jPanel6.add(cpunumber, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 230, -1, -1));

        jLabel95.setText("Current online user count:");
        jPanel6.add(jLabel95, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 250, -1, -1));

        usercount.setText("jLabel96");
        jPanel6.add(usercount, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 250, -1, -1));

        jLabel96.setText("Current connecting user count:");
        jPanel6.add(jLabel96, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 270, -1, -1));

        connectingcount.setText("jLabel97");
        jPanel6.add(connectingcount,
                    new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 270, -1, -1));

        jLabel97.setText("Hub uptime:");
        jPanel6.add(jLabel97, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 290, -1, -1));

        uptime.setText("jLabel98");
        jPanel6.add(uptime, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 290, -1, -1));

        jLabel98.setText("Startup time:");
        jPanel6.add(jLabel98, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 310, -1, -1));

        startuptime.setText("jLabel99");
        jPanel6.add(startuptime,
                    new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 310, -1, -1));

        jTabbedPane1.addTab("Stats", null, jPanel6, "Some Hub Statistics...");

        jPanel10.setToolTipText("Forbidden words");

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Selected Options"));
        jPanel11.setFont(new java.awt.Font("Tahoma", 0, 10));

        jPanel14.setBorder(javax.swing.BorderFactory.createTitledBorder("Client Action"));

        buttonGroup2.add(jRadioButton1);
        jRadioButton1.setFont(new java.awt.Font("Tahoma", 0, 10));
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Drop");
        jRadioButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButton1.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jRadioButton1MouseClicked(evt);
            }
        });

        buttonGroup2.add(jRadioButton2);
        jRadioButton2.setFont(new java.awt.Font("Tahoma", 0, 10));
        jRadioButton2.setText("Kick");
        jRadioButton2.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButton2.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jRadioButton2MouseClicked(evt);
            }
        });

        buttonGroup2.add(jRadioButton3);
        jRadioButton3.setFont(new java.awt.Font("Tahoma", 0, 10));
        jRadioButton3.setText("No Action");
        jRadioButton3.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton3.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButton3.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jRadioButton3MouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel14Layout =
                new org.jdesktop.layout.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
                jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel14Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jRadioButton1)
                                                                    .add(jRadioButton2)
                                                                    .add(jRadioButton3))
                                                 .addContainerGap(145, Short.MAX_VALUE))
                                         );
        jPanel14Layout.setVerticalGroup(
                jPanel14Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel14Layout.createSequentialGroup()
                                                 .add(jRadioButton1)
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jRadioButton2)
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jRadioButton3)
                                                 .addContainerGap(37, Short.MAX_VALUE))
                                       );

        jPanel15.setBorder(javax.swing.BorderFactory.createTitledBorder("Word Action"));
        jPanel15.setFont(new java.awt.Font("Tahoma", 0, 10));

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setFont(new java.awt.Font("Tahoma", 0, 10));
        jRadioButton4.setSelected(true);
        jRadioButton4.setText("Hide Line");
        jRadioButton4.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton4.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButton4.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jRadioButton4MouseClicked(evt);
            }
        });

        buttonGroup1.add(jRadioButton5);
        jRadioButton5.setFont(new java.awt.Font("Tahoma", 0, 10));
        jRadioButton5.setText("Replace with *");
        jRadioButton5.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton5.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButton5.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jRadioButton5MouseClicked(evt);
            }
        });

        buttonGroup1.add(jRadioButton6);
        jRadioButton6.setFont(new java.awt.Font("Tahoma", 0, 10));
        jRadioButton6.setText("Modify");
        jRadioButton6.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton6.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButton6.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jRadioButton6MouseClicked(evt);
            }
        });
        jRadioButton6.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                jRadioButton6StateChanged(evt);
            }
        });

        jTextField3.setEditable(false);
        jTextField3.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                jTextField3KeyReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel15Layout =
                new org.jdesktop.layout.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
                jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel15Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jTextField3,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         209,
                                                                         Short.MAX_VALUE)
                                                                    .add(jRadioButton4)
                                                                    .add(jRadioButton5)
                                                                    .add(jRadioButton6))
                                                 .addContainerGap())
                                         );
        jPanel15Layout.setVerticalGroup(
                jPanel15Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel15Layout.createSequentialGroup()
                                                 .add(jRadioButton4)
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jRadioButton5)
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jRadioButton6)
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jTextField3,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                 .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE))
                                       );

        privatecheck.setFont(new java.awt.Font("Tahoma", 0, 10));
        privatecheck.setText("Control also Private Chat");
        privatecheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        privatecheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        privatecheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                privatecheckActionPerformed(evt);
            }
        });

        notifycheck.setFont(new java.awt.Font("Tahoma", 0, 10));
        notifycheck.setText("Notify operator chat");
        notifycheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        notifycheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        notifycheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                notifycheckActionPerformed(evt);
            }
        });

        searchcheck.setFont(new java.awt.Font("Tahoma", 0, 10));
        searchcheck.setText("Control also Searches");
        searchcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                searchcheckActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel11Layout =
                new org.jdesktop.layout.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
                jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel11Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jPanel11Layout.createSequentialGroup()
                                                                                       .add(jPanel14,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                       .addPreferredGap(
                                                                                               org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                       .add(jPanel15,
                                                                                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                            Short.MAX_VALUE))
                                                                    .add(jPanel11Layout.createSequentialGroup()
                                                                                       .add(privatecheck)
                                                                                       .add(45,
                                                                                            45,
                                                                                            45)
                                                                                       .add(searchcheck)
                                                                                       .addPreferredGap(
                                                                                               org.jdesktop.layout.LayoutStyle.RELATED,
                                                                                               61,
                                                                                               Short.MAX_VALUE)
                                                                                       .add(notifycheck,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                            119,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                                 .addContainerGap())
                                         );
        jPanel11Layout.setVerticalGroup(
                jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel11Layout.createSequentialGroup()
                                                 .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING,
                                                                                         false)
                                                                    .add(jPanel15,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         Short.MAX_VALUE)
                                                                    .add(jPanel14,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         Short.MAX_VALUE))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jPanel11Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(privatecheck)
                                                                    .add(notifycheck)
                                                                    .add(searchcheck))
                                                 .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE))
                                       );

        jPanel16.setBorder(javax.swing.BorderFactory.createTitledBorder("File List"));

        jLabel63.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel63.setText("File:");

        jButton26.setText("Load");
        jButton26.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jButton26MouseClicked(evt);
            }
        });

        jButton27.setText("Append");
        jButton27.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jButton27MouseClicked(evt);
            }
        });

        jButton28.setText("Save");
        jButton28.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jButton28MouseClicked(evt);
            }
        });

        jButton29.setText("Browse");
        jButton29.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jButton29MouseClicked(evt);
            }
        });
        jButton29.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton29ActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel16Layout =
                new org.jdesktop.layout.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
                jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel16Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(org.jdesktop.layout.GroupLayout.TRAILING,
                                                                         jPanel16Layout.createSequentialGroup()
                                                                                       .add(jButton26,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                            92,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                       .addPreferredGap(
                                                                                               org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                       .add(jButton27)
                                                                                       .addPreferredGap(
                                                                                               org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                       .add(jButton28))
                                                                    .add(jPanel16Layout.createSequentialGroup()
                                                                                       .add(jLabel63)
                                                                                       .addPreferredGap(
                                                                                               org.jdesktop.layout.LayoutStyle.RELATED,
                                                                                               44,
                                                                                               Short.MAX_VALUE)
                                                                                       .add(jTextField4,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                            341,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                       .addPreferredGap(
                                                                                               org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                       .add(jButton29)))
                                                 .addContainerGap())
                                         );

        jPanel16Layout.linkSize(new java.awt.Component[]{ jButton26, jButton27, jButton28 },
                                org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel16Layout.setVerticalGroup(
                jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel16Layout.createSequentialGroup()
                                                 .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(jLabel63)
                                                                    .add(jButton29,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         19,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jTextField4,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jPanel16Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(jButton28)
                                                                    .add(jButton27)
                                                                    .add(jButton26))
                                                 .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE))
                                       );

        jPanel16Layout.linkSize(new java.awt.Component[]{ jButton29, jLabel63, jTextField4 },
                                org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel16Layout.linkSize(new java.awt.Component[]{ jButton26, jButton27, jButton28 },
                                org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel17.setBorder(javax.swing
                                   .BorderFactory
                                   .createTitledBorder("Add word ( regular expression )"));

        jTextField2.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mousePressed(java.awt.event.MouseEvent evt)
            {
                jTextField2MousePressed(evt);
            }
        });
        jTextField2.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyPressed(java.awt.event.KeyEvent evt)
            {
                jTextField2KeyPressed(evt);
            }
        });

        jLabel64.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel64.setText("Word:");

        jButton23.setText("Add");
        jButton23.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jButton23MouseClicked(evt);
            }
        });

        buttonGroup3.add(jRadioButton7);
        jRadioButton7.setFont(new java.awt.Font("Tahoma", 0, 10));
        jRadioButton7.setSelected(true);
        jRadioButton7.setText("Drop");
        jRadioButton7.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton7.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup3.add(jRadioButton8);
        jRadioButton8.setFont(new java.awt.Font("Tahoma", 0, 10));
        jRadioButton8.setText("Kick");
        jRadioButton8.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton8.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup3.add(jRadioButton9);
        jRadioButton9.setFont(new java.awt.Font("Tahoma", 0, 10));
        jRadioButton9.setText("No Action");
        jRadioButton9.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton9.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup4.add(jRadioButton10);
        jRadioButton10.setFont(new java.awt.Font("Tahoma", 0, 10));
        jRadioButton10.setSelected(true);
        jRadioButton10.setText("Hide Line");
        jRadioButton10.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton10.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup4.add(jRadioButton11);
        jRadioButton11.setFont(new java.awt.Font("Tahoma", 0, 10));
        jRadioButton11.setText("Replace With *");
        jRadioButton11.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton11.setMargin(new java.awt.Insets(0, 0, 0, 0));

        buttonGroup4.add(jRadioButton12);
        jRadioButton12.setFont(new java.awt.Font("Tahoma", 0, 10));
        jRadioButton12.setText("Modify");
        jRadioButton12.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        jRadioButton12.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jRadioButton12.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                jRadioButton12StateChanged(evt);
            }
        });

        jTextField5.setEditable(false);

        org.jdesktop.layout.GroupLayout jPanel17Layout =
                new org.jdesktop.layout.GroupLayout(jPanel17);
        jPanel17.setLayout(jPanel17Layout);
        jPanel17Layout.setHorizontalGroup(
                jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel17Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jPanel17Layout.createSequentialGroup()
                                                                                       .add(jLabel64)
                                                                                       .addPreferredGap(
                                                                                               org.jdesktop.layout.LayoutStyle.RELATED,
                                                                                               42,
                                                                                               Short.MAX_VALUE)
                                                                                       .add(jTextField2,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                            342,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                       .add(8, 8, 8)
                                                                                       .add(jButton23,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                            65,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                                    .add(jPanel17Layout.createSequentialGroup()
                                                                                       .add(jPanel17Layout
                                                                                                    .createParallelGroup(
                                                                                                            org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                    .add(jRadioButton7)
                                                                                                    .add(jRadioButton10))
                                                                                       .add(45,
                                                                                            45,
                                                                                            45)
                                                                                       .add(jPanel17Layout
                                                                                                    .createParallelGroup(
                                                                                                            org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                    .add(jRadioButton8)
                                                                                                    .add(jRadioButton11))
                                                                                       .add(13,
                                                                                            13,
                                                                                            13)
                                                                                       .add(jPanel17Layout
                                                                                                    .createParallelGroup(
                                                                                                            org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                    .add(jPanel17Layout
                                                                                                                 .createSequentialGroup()
                                                                                                                 .add(jRadioButton9)
                                                                                                                 .addPreferredGap(
                                                                                                                         org.jdesktop.layout.LayoutStyle.RELATED,
                                                                                                                         222,
                                                                                                                         Short.MAX_VALUE))
                                                                                                    .add(jPanel17Layout
                                                                                                                 .createSequentialGroup()
                                                                                                                 .add(jRadioButton12)
                                                                                                                 .addPreferredGap(
                                                                                                                         org.jdesktop.layout.LayoutStyle.RELATED,
                                                                                                                         51,
                                                                                                                         Short.MAX_VALUE)
                                                                                                                 .add(jTextField5,
                                                                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                      175,
                                                                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                                                 .add(10,
                                                                                                                      10,
                                                                                                                      10)))))
                                                 .addContainerGap())
                                         );
        jPanel17Layout.setVerticalGroup(
                jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel17Layout.createSequentialGroup()
                                                 .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(jLabel64)
                                                                    .add(jTextField2,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                    .add(jButton23,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         13,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(jRadioButton7)
                                                                    .add(jRadioButton8)
                                                                    .add(jRadioButton9))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jPanel17Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(jRadioButton11)
                                                                    .add(jRadioButton10)
                                                                    .add(jRadioButton12)
                                                                    .add(jTextField5,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                 .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                  Short.MAX_VALUE))
                                       );

        jPanel17Layout.linkSize(new java.awt.Component[]{ jButton23, jLabel64, jTextField2 },
                                org.jdesktop.layout.GroupLayout.VERTICAL);

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder("Forbidden Words List"));

        jButton25.setText("Delete");
        jButton25.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jButton25MouseClicked(evt);
            }
        });

        jButton24.setText("Clear List");
        jButton24.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jButton24MouseClicked(evt);
            }
        });

        jList1.addKeyListener(new java.awt.event.KeyAdapter()
        {
            public void keyReleased(java.awt.event.KeyEvent evt)
            {
                jList1KeyReleased(evt);
            }
        });
        jList1.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                jList1MouseClicked(evt);
            }
        });
        jScrollPane9.setViewportView(jList1);

        org.jdesktop.layout.GroupLayout jPanel18Layout =
                new org.jdesktop.layout.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
                jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel18Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jScrollPane9,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         190,
                                                                         Short.MAX_VALUE)
                                                                    .add(jPanel18Layout.createSequentialGroup()
                                                                                       .add(jButton24,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                            88,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                       .addPreferredGap(
                                                                                               org.jdesktop.layout.LayoutStyle.RELATED,
                                                                                               14,
                                                                                               Short.MAX_VALUE)
                                                                                       .add(jButton25,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                            88,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                                 .addContainerGap())
                                         );

        jPanel18Layout.linkSize(new java.awt.Component[]{ jButton24, jButton25 },
                                org.jdesktop.layout.GroupLayout.HORIZONTAL);

        jPanel18Layout.setVerticalGroup(
                jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(org.jdesktop.layout.GroupLayout.TRAILING,
                                   jPanel18Layout.createSequentialGroup()
                                                 .add(jScrollPane9,
                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                      307,
                                                      Short.MAX_VALUE)
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jPanel18Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(jButton24)
                                                                    .add(jButton25,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                         23,
                                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                 .addContainerGap())
                                       );

        jPanel18Layout.linkSize(new java.awt.Component[]{ jButton24, jButton25 },
                                org.jdesktop.layout.GroupLayout.VERTICAL);

        org.jdesktop.layout.GroupLayout jPanel10Layout =
                new org.jdesktop.layout.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
                jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel10Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel18,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                 .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jPanel11,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         Short.MAX_VALUE)
                                                                    .add(jPanel17,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         Short.MAX_VALUE)
                                                                    .add(jPanel16,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                         Short.MAX_VALUE))
                                                 .addContainerGap())
                                         );
        jPanel10Layout.setVerticalGroup(
                jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel10Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel10Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jPanel10Layout.createSequentialGroup()
                                                                                       .add(jPanel18,
                                                                                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                            Short.MAX_VALUE)
                                                                                       .addContainerGap())
                                                                    .add(jPanel10Layout.createSequentialGroup()
                                                                                       .add(jPanel11,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                       .addPreferredGap(
                                                                                               org.jdesktop.layout.LayoutStyle.RELATED,
                                                                                               org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                               Short.MAX_VALUE)
                                                                                       .add(jPanel16,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                       .addPreferredGap(
                                                                                               org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                       .add(jPanel17,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                                       .add(11,
                                                                                            11,
                                                                                            11))))
                                       );

        jTabbedPane1.addTab("Chat Control", jPanel10);

        jPanel9.setToolTipText("ADC advanced configuration panel");
        jPanel9.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel51.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel51.setText("The ADC advanced configuration Panel.");
        jPanel9.add(jLabel51, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jLabel52.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel52.setText("Here you can configure the ADC commands separately.");
        jPanel9.add(jLabel52, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 30, -1, -1));

        jLabel54.setFont(new java.awt.Font("Tahoma", 0, 10));
        jLabel54.setText("Allowed contexts:");
        jPanel9.add(jLabel54, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 60, -1, -1));

        jButton13.setText("[?]");
        jButton13.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton13ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton13, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 230, -1, -1));

        jButton14.setText("[?]");
        jButton14.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton14ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton14, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 230, -1, -1));

        jButton15.setText("[?]");
        jButton15.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton15ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton15, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 230, -1, -1));

        jButton16.setText("[?]");
        jButton16.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton16ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton16, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 230, -1, -1));

        jButton17.setText("[?]");
        jButton17.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton17ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton17, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 230, -1, -1));

        jButton18.setText("[?]");
        jButton18.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton18ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton18, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 230, -1, -1));

        jButton19.setText("[?]");
        jButton19.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton19ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton19, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 230, -1, -1));

        jButton20.setText("[?]");
        jButton20.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton20ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton20, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 230, -1, -1));

        jButton21.setText("[?]");
        jButton21.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton21ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton21, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 230, -1, -1));

        jPanel25.setBorder(javax.swing
                                   .BorderFactory
                                   .createTitledBorder(null,
                                                       "MSG",
                                                       javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                                       javax.swing.border.TitledBorder.DEFAULT_POSITION,
                                                       new java.awt.Font("Tahoma",
                                                                         1,
                                                                         11))); // NOI18N
        jPanel25.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BMSGcheck.setText("B");
        BMSGcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BMSGcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        BMSGcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BMSGcheckActionPerformed(evt);
            }
        });
        jPanel25.add(BMSGcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        DMSGcheck.setText("D");
        DMSGcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        DMSGcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        DMSGcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                DMSGcheckActionPerformed(evt);
            }
        });
        jPanel25.add(DMSGcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        EMSGcheck.setText("E");
        EMSGcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        EMSGcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        EMSGcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                EMSGcheckActionPerformed(evt);
            }
        });
        jPanel25.add(EMSGcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        FMSGcheck.setText("F");
        FMSGcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        FMSGcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        FMSGcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                FMSGcheckActionPerformed(evt);
            }
        });
        jPanel25.add(FMSGcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        HMSGcheck.setText("H");
        HMSGcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        HMSGcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        HMSGcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                HMSGcheckActionPerformed(evt);
            }
        });
        jPanel25.add(HMSGcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, -1));

        jPanel9.add(jPanel25, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 90, 50, 130));

        jButton12.setText("[?]");
        jButton12.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton12ActionPerformed(evt);
            }
        });
        jPanel9.add(jButton12, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 10, -1, -1));

        jPanel26.setBorder(javax.swing
                                   .BorderFactory
                                   .createTitledBorder(null,
                                                       "STA",
                                                       javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                                       javax.swing.border.TitledBorder.DEFAULT_POSITION,
                                                       new java.awt.Font("Tahoma",
                                                                         1,
                                                                         11))); // NOI18N
        jPanel26.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BSTAcheck.setText("B");
        BSTAcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BSTAcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        BSTAcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BSTAcheckActionPerformed(evt);
            }
        });
        jPanel26.add(BSTAcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        DSTAcheck.setText("D");
        DSTAcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        DSTAcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        DSTAcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                DSTAcheckActionPerformed(evt);
            }
        });
        jPanel26.add(DSTAcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        ESTAcheck.setText("E");
        ESTAcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ESTAcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ESTAcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ESTAcheckActionPerformed(evt);
            }
        });
        jPanel26.add(ESTAcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        FSTAcheck.setText("F");
        FSTAcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        FSTAcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        FSTAcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                FSTAcheckActionPerformed(evt);
            }
        });
        jPanel26.add(FSTAcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        HSTAcheck.setText("H");
        HSTAcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        HSTAcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        HSTAcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                HSTAcheckActionPerformed(evt);
            }
        });
        jPanel26.add(HSTAcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, -1));

        jPanel9.add(jPanel26, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 90, 50, 130));

        jPanel27.setBorder(javax.swing
                                   .BorderFactory
                                   .createTitledBorder(null,
                                                       "CTM",
                                                       javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                                       javax.swing.border.TitledBorder.DEFAULT_POSITION,
                                                       new java.awt.Font("Tahoma",
                                                                         1,
                                                                         11))); // NOI18N
        jPanel27.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BCTMcheck.setText("B");
        BCTMcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BCTMcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        BCTMcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BCTMcheckActionPerformed(evt);
            }
        });
        jPanel27.add(BCTMcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        DCTMcheck.setText("D");
        DCTMcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        DCTMcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        DCTMcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                DCTMcheckActionPerformed(evt);
            }
        });
        jPanel27.add(DCTMcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        ECTMcheck.setText("E");
        ECTMcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ECTMcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ECTMcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ECTMcheckActionPerformed(evt);
            }
        });
        jPanel27.add(ECTMcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        FCTMcheck.setText("F");
        FCTMcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        FCTMcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        FCTMcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                FCTMcheckActionPerformed(evt);
            }
        });
        jPanel27.add(FCTMcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        HCTMcheck.setText("H");
        HCTMcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        HCTMcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        HCTMcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                HCTMcheckActionPerformed(evt);
            }
        });
        jPanel27.add(HCTMcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, -1));

        jPanel9.add(jPanel27, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 90, 50, 130));

        jPanel28.setBorder(javax.swing
                                   .BorderFactory
                                   .createTitledBorder(null,
                                                       "RCM",
                                                       javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                                       javax.swing.border.TitledBorder.DEFAULT_POSITION,
                                                       new java.awt.Font("Tahoma",
                                                                         1,
                                                                         11))); // NOI18N
        jPanel28.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BRCMcheck.setText("B");
        BRCMcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BRCMcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        BRCMcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BRCMcheckActionPerformed(evt);
            }
        });
        jPanel28.add(BRCMcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        DRCMcheck.setText("D");
        DRCMcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        DRCMcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        DRCMcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                DRCMcheckActionPerformed(evt);
            }
        });
        jPanel28.add(DRCMcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        ERCMcheck.setText("E");
        ERCMcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ERCMcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ERCMcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ERCMcheckActionPerformed(evt);
            }
        });
        jPanel28.add(ERCMcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        FRCMcheck.setText("F");
        FRCMcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        FRCMcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        FRCMcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                FRCMcheckActionPerformed(evt);
            }
        });
        jPanel28.add(FRCMcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        HRCMcheck.setText("H");
        HRCMcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        HRCMcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        HRCMcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                HRCMcheckActionPerformed(evt);
            }
        });
        jPanel28.add(HRCMcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, -1));

        jPanel9.add(jPanel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 90, 50, 130));

        jPanel29.setBorder(javax.swing
                                   .BorderFactory
                                   .createTitledBorder(null,
                                                       "INF",
                                                       javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                                       javax.swing.border.TitledBorder.DEFAULT_POSITION,
                                                       new java.awt.Font("Tahoma",
                                                                         1,
                                                                         11))); // NOI18N
        jPanel29.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BINFcheck.setText("B");
        BINFcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BINFcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        BINFcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BINFcheckActionPerformed(evt);
            }
        });
        jPanel29.add(BINFcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        DINFcheck.setText("D");
        DINFcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        DINFcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        DINFcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                DINFcheckActionPerformed(evt);
            }
        });
        jPanel29.add(DINFcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        EINFcheck.setText("E");
        EINFcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        EINFcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        EINFcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                EINFcheckActionPerformed(evt);
            }
        });
        jPanel29.add(EINFcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        FINFcheck.setText("F");
        FINFcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        FINFcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        FINFcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                FINFcheckActionPerformed(evt);
            }
        });
        jPanel29.add(FINFcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        HINFcheck.setText("H");
        HINFcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        HINFcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        HINFcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                HINFcheckActionPerformed(evt);
            }
        });
        jPanel29.add(HINFcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, -1));

        jPanel9.add(jPanel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 90, 50, 130));

        jPanel30.setBorder(javax.swing
                                   .BorderFactory
                                   .createTitledBorder(null,
                                                       "SCH",
                                                       javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                                       javax.swing.border.TitledBorder.DEFAULT_POSITION,
                                                       new java.awt.Font("Tahoma",
                                                                         1,
                                                                         11))); // NOI18N
        jPanel30.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BSCHcheck.setText("B");
        BSCHcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BSCHcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        BSCHcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BSCHcheckActionPerformed(evt);
            }
        });
        jPanel30.add(BSCHcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        DSCHcheck.setText("D");
        DSCHcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        DSCHcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        DSCHcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                DSCHcheckActionPerformed(evt);
            }
        });
        jPanel30.add(DSCHcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        ESCHcheck.setText("E");
        ESCHcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ESCHcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ESCHcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ESCHcheckActionPerformed(evt);
            }
        });
        jPanel30.add(ESCHcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        FSCHcheck.setText("F");
        FSCHcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        FSCHcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        FSCHcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                FSCHcheckActionPerformed(evt);
            }
        });
        jPanel30.add(FSCHcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        HSCHcheck.setText("H");
        HSCHcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        HSCHcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        HSCHcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                HSCHcheckActionPerformed(evt);
            }
        });
        jPanel30.add(HSCHcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, -1));

        jPanel9.add(jPanel30, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 90, 50, 130));

        jPanel31.setBorder(javax.swing
                                   .BorderFactory
                                   .createTitledBorder(null,
                                                       "RES",
                                                       javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                                       javax.swing.border.TitledBorder.DEFAULT_POSITION,
                                                       new java.awt.Font("Tahoma",
                                                                         1,
                                                                         11))); // NOI18N
        jPanel31.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BREScheck.setText("B");
        BREScheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BREScheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        BREScheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BREScheckActionPerformed(evt);
            }
        });
        jPanel31.add(BREScheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        DREScheck.setText("D");
        DREScheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        DREScheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        DREScheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                DREScheckActionPerformed(evt);
            }
        });
        jPanel31.add(DREScheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        EREScheck.setText("E");
        EREScheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        EREScheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        EREScheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                EREScheckActionPerformed(evt);
            }
        });
        jPanel31.add(EREScheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        FREScheck.setText("F");
        FREScheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        FREScheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        FREScheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                FREScheckActionPerformed(evt);
            }
        });
        jPanel31.add(FREScheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        HREScheck.setText("H");
        HREScheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        HREScheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        HREScheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                HREScheckActionPerformed(evt);
            }
        });
        jPanel31.add(HREScheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, -1));

        jPanel9.add(jPanel31, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 90, 50, 130));

        jPanel32.setBorder(javax.swing
                                   .BorderFactory
                                   .createTitledBorder(null,
                                                       "PAS",
                                                       javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                                       javax.swing.border.TitledBorder.DEFAULT_POSITION,
                                                       new java.awt.Font("Tahoma",
                                                                         1,
                                                                         11))); // NOI18N
        jPanel32.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BPAScheck.setText("B");
        BPAScheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BPAScheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        BPAScheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BPAScheckActionPerformed(evt);
            }
        });
        jPanel32.add(BPAScheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        DPAScheck.setText("D");
        DPAScheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        DPAScheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        DPAScheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                DPAScheckActionPerformed(evt);
            }
        });
        jPanel32.add(DPAScheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        EPAScheck.setText("E");
        EPAScheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        EPAScheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        EPAScheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                EPAScheckActionPerformed(evt);
            }
        });
        jPanel32.add(EPAScheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        FPAScheck.setText("F");
        FPAScheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        FPAScheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        FPAScheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                FPAScheckActionPerformed(evt);
            }
        });
        jPanel32.add(FPAScheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        HPAScheck.setText("H");
        HPAScheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        HPAScheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        HPAScheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                HPAScheckActionPerformed(evt);
            }
        });
        jPanel32.add(HPAScheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, -1));

        jPanel9.add(jPanel32, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 90, 50, 130));

        jPanel33.setBorder(javax.swing
                                   .BorderFactory
                                   .createTitledBorder(null,
                                                       "SUP",
                                                       javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                                                       javax.swing.border.TitledBorder.DEFAULT_POSITION,
                                                       new java.awt.Font("Tahoma",
                                                                         1,
                                                                         11))); // NOI18N
        jPanel33.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BSUPcheck.setText("B");
        BSUPcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        BSUPcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        BSUPcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BSUPcheckActionPerformed(evt);
            }
        });
        jPanel33.add(BSUPcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 20, -1, -1));

        DSUPcheck.setText("D");
        DSUPcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        DSUPcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        DSUPcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                DSUPcheckActionPerformed(evt);
            }
        });
        jPanel33.add(DSUPcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        ESUPcheck.setText("E");
        ESUPcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        ESUPcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        ESUPcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                ESUPcheckActionPerformed(evt);
            }
        });
        jPanel33.add(ESUPcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 60, -1, -1));

        FSUPcheck.setText("F");
        FSUPcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        FSUPcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        FSUPcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                FSUPcheckActionPerformed(evt);
            }
        });
        jPanel33.add(FSUPcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 80, -1, -1));

        HSUPcheck.setText("H");
        HSUPcheck.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        HSUPcheck.setMargin(new java.awt.Insets(0, 0, 0, 0));
        HSUPcheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                HSUPcheckActionPerformed(evt);
            }
        });
        jPanel33.add(HSUPcheck, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 100, -1, -1));

        jPanel9.add(jPanel33, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 90, 50, 130));

        jTabbedPane1.addTab("Advanced", jPanel9);

        PPanel.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                PPanelMouseClicked(evt);
            }
        });
        PPanel.addFocusListener(new java.awt.event.FocusAdapter()
        {
            public void focusGained(java.awt.event.FocusEvent evt)
            {
                PPanelFocusGained(evt);
            }
        });
        PPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jScrollPane11.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        PluginPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        jScrollPane11.setViewportView(PluginPanel);

        PPanel.add(jScrollPane11,
                   new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, 720, 350));

        jButton30.setText("Rescan for modules");
        jButton30.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton30ActionPerformed(evt);
            }
        });
        PPanel.add(jButton30, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 10, 160, -1));

        jLabel55.setText(
                "Modules are the way DSHub can be extended. Place them in the /modules subdirectory.");
        PPanel.add(jLabel55, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 10, -1, -1));

        jTabbedPane1.addTab("Additional Modules", PPanel);

        jLabel58.setText(
                "The DSHub scripts and the scripting languages currently supported. Scripts path: /scripts .");

        jButton32.setText("Rescan Scripts");
        jButton32.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton32ActionPerformed(evt);
            }
        });

        jTabbedPane3.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        jPanel41.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        PyTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        { null, null },
                        { null, null },
                        { null, null },
                        { null, null }
                },
                new String[]{
                        "Script name", "Active"
                }
        )
        {
            Class[] types = new Class[]{
                    java.lang.String.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean[]{
                    false, true
            };


            public Class getColumnClass(int columnIndex)
            {
                return types[columnIndex];
            }


            public boolean isCellEditable(int rowIndex, int columnIndex)
            {
                return canEdit[columnIndex];
            }
        });
        PyTable.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                PyTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(PyTable);

        jPanel41.add(jScrollPane1,
                     new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 30, 570, 290));

        jLabel60.setText("Relative path: /py");
        jPanel41.add(jLabel60, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 10, -1, -1));

        jTabbedPane3.addTab("Python", jPanel41);

        org.jdesktop.layout.GroupLayout jPanel22Layout =
                new org.jdesktop.layout.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
                jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel22Layout.createSequentialGroup()
                                                 .add(jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                    .add(jPanel22Layout.createSequentialGroup()
                                                                                       .add(19,
                                                                                            19,
                                                                                            19)
                                                                                       .add(jLabel58)
                                                                                       .add(18,
                                                                                            18,
                                                                                            18)
                                                                                       .add(jButton32))
                                                                    .add(jPanel22Layout.createSequentialGroup()
                                                                                       .add(28,
                                                                                            28,
                                                                                            28)
                                                                                       .add(jTabbedPane3,
                                                                                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                            719,
                                                                                            Short.MAX_VALUE)))
                                                 .addContainerGap())
                                         );
        jPanel22Layout.setVerticalGroup(
                jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel22Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jPanel22Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(jLabel58)
                                                                    .add(jButton32))
                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                                 .add(jTabbedPane3,
                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                      335,
                                                      Short.MAX_VALUE)
                                                 .addContainerGap())
                                       );

        jTabbedPane1.addTab("Scripts", jPanel22);

        jPanel45.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel74.setText(
                "ADC Secure (ADCS) is a standard ADC extensions that enables running the standard ADC protocol over the ");
        jPanel45.add(jLabel74, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 20, -1, -1));

        jLabel75.setText(
                "Secure Socket Layer / Transport Layer Security ( SSL/TLS ). All messages between the hub and the clients are sent encrypted but the ADC");
        jPanel45.add(jLabel75, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, -1, -1));

        jLabel76.setText(
                "commands remain the same. Client to client are also encrypted if the clients support it.");
        jPanel45.add(jLabel76, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 60, -1, -1));

        jLabel77.setText(
                "To enable ADCS you need a pair of keys for the hubsoft, a private and a public key. You also need a certificate for your public key signed by the");
        jPanel45.add(jLabel77, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, -1, -1));

        jLabel78.setText(
                "hub itself. Clients you register will get a certificate for their public key signed by the hub, this can replace the password based login.");
        jPanel45.add(jLabel78, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 110, -1, -1));

        jLabel81.setText("To prepare your hub for ADCS, meet the following steps:");
        jPanel45.add(jLabel81, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 130, -1, -1));

        jPanel46.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel46.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel80.setText("Step 1:");
        jPanel46.add(jLabel80, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel82.setText(
                "Make sure you have your hub name and settings properly set up ( The key pairs and certificate are created based on your hub name ).");
        jPanel46.add(jLabel82, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 10, -1, -1));

        jPanel45.add(jPanel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 710, 40));

        jPanel47.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel47.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel87.setText("Step 2:");
        jPanel47.add(jLabel87, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        jLabel85.setText("Pick");
        jPanel47.add(jLabel85, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 10, -1, -1));

        genbutton.setText("Generate new pair of keys and certificate for your hub");
        genbutton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                genbuttonActionPerformed(evt);
            }
        });
        jPanel47.add(genbutton, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 10, -1, -1));

        jLabel84.setText("or");
        jPanel47.add(jLabel84, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 40, -1, -1));

        loadkeysbutton.setText("Load keys and certificate previously generated from file");
        jPanel47.add(loadkeysbutton,
                     new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 40, 330, -1));

        jPanel45.add(jPanel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 200, 710, 70));

        jPanel48.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel48.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel83.setText("Optional Step 3:");
        jPanel48.add(jLabel83, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, -1));

        usecertificatescheck.setText(
                "Use certificates for login instead of literal passwords ( improved security )");
        usecertificatescheck.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                usecertificatescheckActionPerformed(evt);
            }
        });
        jPanel48.add(usecertificatescheck,
                     new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, -1, -1));

        jLabel79.setText(
                "To generate a certificate for a specific registered user, go to it's configuration panel and click \"Generate certificate for this reg\"");
        jPanel48.add(jLabel79, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 40, -1, -1));

        jPanel45.add(jPanel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 280, 710, 60));

        jPanel49.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel88.setText("Step 4:");

        enableadcs.setText("Enable ADC Secure");
        enableadcs.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                enableadcsActionPerformed(evt);
            }
        });

        disableadcs.setText("Disable ADC Secure");
        disableadcs.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                disableadcsActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel49Layout =
                new org.jdesktop.layout.GroupLayout(jPanel49);
        jPanel49.setLayout(jPanel49Layout);
        jPanel49Layout.setHorizontalGroup(
                jPanel49Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel49Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jLabel88)
                                                 .add(157, 157, 157)
                                                 .add(enableadcs)
                                                 .add(18, 18, 18)
                                                 .add(disableadcs)
                                                 .addContainerGap(234, Short.MAX_VALUE))
                                         );
        jPanel49Layout.setVerticalGroup(
                jPanel49Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jPanel49Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jLabel88)
                                                 .addContainerGap(11, Short.MAX_VALUE))
                              .add(org.jdesktop.layout.GroupLayout.TRAILING,
                                   jPanel49Layout.createSequentialGroup()
                                                 .addContainerGap(13, Short.MAX_VALUE)
                                                 .add(jPanel49Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                                                    .add(disableadcs)
                                                                    .add(enableadcs)))
                                       );

        jPanel45.add(jPanel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 350, 710, 40));

        jTabbedPane1.addTab("ADC Secure", jPanel45);

        jPanel12.setToolTipText("Hub Log");

        LogText.setColumns(20);
        LogText.setRows(5);
        jScrollPane4.setViewportView(LogText);

        org.jdesktop.layout.GroupLayout jPanel12Layout =
                new org.jdesktop.layout.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
                jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(org.jdesktop.layout.GroupLayout.TRAILING,
                                   jPanel12Layout.createSequentialGroup()
                                                 .addContainerGap()
                                                 .add(jScrollPane4,
                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                      737,
                                                      Short.MAX_VALUE)
                                                 .addContainerGap())
                                         );
        jPanel12Layout.setVerticalGroup(
                jPanel12Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                              .add(jScrollPane4,
                                   org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                   391,
                                   Short.MAX_VALUE)
                                       );

        jTabbedPane1.addTab("Log", jPanel12);

        jPanel4.setToolTipText("Some Help ...");

        jTextArea1.setColumns(20);
        jTextArea1.setEditable(false);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        Panelxxx.setViewportView(jTextArea1);

        org.jdesktop.layout.GroupLayout jPanel4Layout =
                new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                             .add(org.jdesktop.layout.GroupLayout.TRAILING,
                                  jPanel4Layout.createSequentialGroup()
                                               .addContainerGap()
                                               .add(Panelxxx,
                                                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                    737,
                                                    Short.MAX_VALUE)
                                               .addContainerGap())
                                        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                             .add(jPanel4Layout.createSequentialGroup()
                                               .add(Panelxxx,
                                                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                    318,
                                                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                               .addContainerGap(73, Short.MAX_VALUE))
                                      );

        jTabbedPane1.addTab("Help", null, jPanel4, "Some Help...");

        jButton3.setText("Restart Hub");
        jButton3.setToolTipText("Restarts hub with current settings.");
        jButton3.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel13.setText("STATUS:");
        jLabel13.setToolTipText("The Hub current Status.");

        StatusLabel.setFont(new java.awt.Font("Tahoma", 0, 10));
        StatusLabel.setText("Initialising ...");

        adcslabel.setText("Checking....");
        adcslabel.setToolTipText("Click for information");
        adcslabel.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                adcslabelMouseClicked(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout =
                new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                      .add(layout.createSequentialGroup()
                                 .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                            .add(layout.createSequentialGroup()
                                                       .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                  .add(layout.createSequentialGroup()
                                                                             .add(37, 37, 37)
                                                                             .add(jLabel13)
                                                                             .add(25, 25, 25)
                                                                             .add(StatusLabel,
                                                                                  org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                  441,
                                                                                  org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                                  .add(layout.createSequentialGroup()
                                                                             .add(58, 58, 58)
                                                                             .add(adcslabel)))
                                                       .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                                       .add(jButton3)
                                                       .add(27, 27, 27)
                                                       .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                                                  .add(jButton2,
                                                                       org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                       109,
                                                                       Short.MAX_VALUE)
                                                                  .add(jButton1,
                                                                       org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                       109,
                                                                       Short.MAX_VALUE)))
                                            .add(org.jdesktop.layout.GroupLayout.TRAILING,
                                                 layout.createSequentialGroup()
                                                       .addContainerGap()
                                                       .add(jTabbedPane1)))
                                 .addContainerGap())
                                 );
        layout.setVerticalGroup(
                layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                      .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                                                           .addContainerGap(20,
                                                                                            Short.MAX_VALUE)
                                                                           .add(jTabbedPane1,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                423,
                                                                                org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                                                           .add(layout.createParallelGroup(
                                                                                   org.jdesktop.layout.GroupLayout.LEADING)
                                                                                      .add(layout.createSequentialGroup()
                                                                                                 .addPreferredGap(
                                                                                                         org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                                 .add(layout.createParallelGroup(
                                                                                                         org.jdesktop.layout.GroupLayout.LEADING)
                                                                                                            .add(org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                                                 layout.createSequentialGroup()
                                                                                                                       .add(jButton1)
                                                                                                                       .addPreferredGap(
                                                                                                                               org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                                                       .add(jButton2)
                                                                                                                       .addPreferredGap(
                                                                                                                               org.jdesktop.layout.LayoutStyle.RELATED))
                                                                                                            .add(org.jdesktop.layout.GroupLayout.TRAILING,
                                                                                                                 layout.createSequentialGroup()
                                                                                                                       .add(layout.createParallelGroup(
                                                                                                                               org.jdesktop.layout.GroupLayout.BASELINE)
                                                                                                                                  .add(jLabel13)
                                                                                                                                  .add(StatusLabel,
                                                                                                                                       org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                                                                                                                                       29,
                                                                                                                                       org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                                                                                                       .addPreferredGap(
                                                                                                                               org.jdesktop.layout.LayoutStyle.RELATED)
                                                                                                                       .add(adcslabel)
                                                                                                                       .add(5,
                                                                                                                            5,
                                                                                                                            5))))
                                                                                      .add(layout.createSequentialGroup()
                                                                                                 .add(31,
                                                                                                      31,
                                                                                                      31)
                                                                                                 .add(jButton3)))
                                                                           .add(20, 20, 20))
                               );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jTabbedPane1MousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jTabbedPane1MousePressed
    {//GEN-HEADEREND:event_jTabbedPane1MousePressed
        // TODO add your handling code here:
        JPanel x = null;
        try
        {
            x = (JPanel) jTabbedPane1.getSelectedComponent();
        }
        catch (ClassCastException cce)
        {

        }

        if (x != null)
        {
            if (x == PPanel)
            {

                refreshGUIPlugs();
            }
        }
    }//GEN-LAST:event_jTabbedPane1MousePressed


    private void PPanelFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_PPanelFocusGained
    {//GEN-HEADEREND:event_PPanelFocusGained
        // TODO add your handling code here:

    }//GEN-LAST:event_PPanelFocusGained


    private void PPanelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_PPanelMouseClicked
    {//GEN-HEADEREND:event_PPanelMouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_PPanelMouseClicked


    private void proxycheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_proxycheckActionPerformed
    {//GEN-HEADEREND:event_proxycheckActionPerformed
        if (proxycheck.isSelected())
        {
            proxyhostfield.setEditable(true);
            proxyportfield.setEditable(true);
        }
        else
        {

            proxyhostfield.setEditable(false);
            proxyportfield.setEditable(false);

        }
    }//GEN-LAST:event_proxycheckActionPerformed


    private void notifycheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_notifycheckActionPerformed
    {//GEN-HEADEREND:event_notifycheckActionPerformed
        if (notifycheck.isSelected())
        {
            long prop = getClientPr() + getWordPr();
            if (privatecheck.isSelected())
            {
                prop += BannedWord.privatechat;
            }
            if (searchcheck.isSelected())
            {
                prop += BannedWord.searches;
            }
            prop += BannedWord.notify;
            listaBanate.modifyMultiPrAt(jList1.getSelectedIndices(), prop);
        }
        else
        {
            long prop = getClientPr() + getWordPr();
            if (privatecheck.isSelected())
            {
                prop += BannedWord.privatechat;
            }
            if (searchcheck.isSelected())
            {
                prop += BannedWord.searches;
            }

            listaBanate.modifyMultiPrAt(jList1.getSelectedIndices(), prop);
        }
    }//GEN-LAST:event_notifycheckActionPerformed


    private void privatecheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_privatecheckActionPerformed
    {//GEN-HEADEREND:event_privatecheckActionPerformed
        if (privatecheck.isSelected())
        {
            long prop = getClientPr() + getWordPr() + BannedWord.privatechat;
            if (notifycheck.isSelected())
            {
                prop += BannedWord.notify;
            }
            if (searchcheck.isSelected())
            {
                prop += BannedWord.searches;
            }

            listaBanate.modifyMultiPrAt(jList1.getSelectedIndices(), prop);
        }
        else
        {

            long prop = getClientPr() + getWordPr();
            if (notifycheck.isSelected())
            {
                prop += BannedWord.notify;
            }
            if (searchcheck.isSelected())
            {
                prop += BannedWord.searches;
            }
            listaBanate.modifyMultiPrAt(jList1.getSelectedIndices(), prop);
        }
    }//GEN-LAST:event_privatecheckActionPerformed


    private void jButton29ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton29ActionPerformed
    {//GEN-HEADEREND:event_jButton29ActionPerformed
        // TODO add your handling code here:
        String path = jTextField4.getText();
        JFileChooser fc;
        if (!(path.equals("")))
        {
            fc = new JFileChooser(new File(path));
        }
        else
        {
            fc = new JFileChooser();
        }

        fc.setMultiSelectionEnabled(false);
        int sel = 0;
        sel = fc.showDialog(this, "Select File:");
        if (sel == JFileChooser.APPROVE_OPTION)
        {
            jTextField4.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }//GEN-LAST:event_jButton29ActionPerformed


    private void jButton29MouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_jButton29MouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_jButton29MouseClicked


    private void jButton28MouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_jButton28MouseClicked
        // TODO add your handling code here:
        //cc
        String path = jTextField4.getText();
        if (listaBanate.printFile(path) == true)
        {
            this.SetStatus("List saved.");
        }
        else
        {
            this.SetStatus("File access error.", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton28MouseClicked


    private void jButton27MouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_jButton27MouseClicked
        // TODO add your handling code here:
        String path = jTextField4.getText();
        File f = new File(path);
        if (f.isFile())
        {
            // listaBanate.clean();
            listaBanate.loadFile(path);
            this.refreshListaBanate();
            this.SetStatus("List saved.");
        }
    }//GEN-LAST:event_jButton27MouseClicked


    private void jButton26MouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_jButton26MouseClicked
        // TODO add your handling code here:
        String path = jTextField4.getText();
        File f = new File(path);
        if (f.isFile())
        {
            listaBanate.clean();
            listaBanate.loadFile(path);
            this.refreshListaBanate();
            this.SetStatus("List loaded.");
        }
    }//GEN-LAST:event_jButton26MouseClicked


    private void jTextField2KeyPressed(java.awt.event.KeyEvent evt)
    {//GEN-FIRST:event_jTextField2KeyPressed
        // TODO add your handling code here:
        if (evt.getKeyCode() == KeyEvent.VK_ENTER)
        {
            String banWord = jTextField2.getText();
            int l = banWord.length();

            if (!listaBanate.ver_regex(banWord))
            {
                JOptionPane.showMessageDialog(null, banWord
                                                    + "is an invalid regex", "Error",
                                              JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (l != 0)
            {
                if (listaBanate.searchEl(banWord) == -1)
                {
                    DefaultListModel modelLista = (DefaultListModel) jList1
                            .getModel();
                    modelLista.add(0, banWord);
                }
                long prop = 0;
                String repl = "";
                prop = prop | this.getClientAddPr() | this.getWordAddPr();
                repl += this.getAddRepl();
                listaBanate.add(banWord, prop, repl);
            }
        }
    }//GEN-LAST:event_jTextField2KeyPressed


    private void jTextField2MousePressed(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_jTextField2MousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2MousePressed


    private void jList1KeyReleased(java.awt.event.KeyEvent evt)
    {//GEN-FIRST:event_jList1KeyReleased
        // TODO add your handling code here:

        if (evt.getKeyCode() == KeyEvent.VK_DELETE)
        {
            int[] indici = jList1.getSelectedIndices();
            listaBanate.removeElementsAt(indici);
            DefaultListModel modelLista = (DefaultListModel) jList1.getModel();

            Arrays.sort(indici);
            for (int i = indici.length - 1; i >= 0; i--)
            {
                modelLista.removeElementAt(indici[i]);
            }

            return;
        }

        int[] indici = jList1.getSelectedIndices();

        int i, n;
        n = indici.length;
        if (n == 0)
        {
            selectPr(BannedWord.dropped | BannedWord.hidden, "");
            return;
        }
        long prop = listaBanate.getPrAt(indici[0]);
        String repl = listaBanate.getReplAt(indici[0]);
        selectPr(prop, repl);
    }//GEN-LAST:event_jList1KeyReleased


    private void jButton25MouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_jButton25MouseClicked
        // TODO add your handling code here:
        int[] indici = jList1.getSelectedIndices();
        listaBanate.removeElementsAt(indici);
        DefaultListModel modelLista = (DefaultListModel) jList1.getModel();

        Arrays.sort(indici);
        for (int i = indici.length - 1; i >= 0; i--)
        {
            modelLista.removeElementAt(indici[i]);
        }

    }//GEN-LAST:event_jButton25MouseClicked


    private void jTextField3KeyReleased(java.awt.event.KeyEvent evt)
    {//GEN-FIRST:event_jTextField3KeyReleased
        // TODO add your handling code here:

        long prop = getWordPr();
        listaBanate.modifyMultiWordPrAt(jList1.getSelectedIndices(), prop,
                                        jTextField3.getText());
        prop += getClientPr();
        listaBanate.modifyMultiPrAt(jList1.getSelectedIndices(), prop
                                                                 +
                                                                 (notifycheck.isSelected() ?
                                                                  BannedWord.notify :
                                                                  0)
                                                                 +
                                                                 (privatecheck.isSelected() ?
                                                                  BannedWord.privatechat :
                                                                  0)
                                                                 +
                                                                 (searchcheck.isSelected() ?
                                                                  BannedWord.searches :
                                                                  0));
    }//GEN-LAST:event_jTextField3KeyReleased


    private void jRadioButton6MouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_jRadioButton6MouseClicked
        // TODO add your handling code here:
        long prop = getWordPr();
        listaBanate.modifyMultiWordPrAt(jList1.getSelectedIndices(), prop,
                                        jTextField3.getText());
        prop += getClientPr();
        listaBanate.modifyMultiPrAt(jList1.getSelectedIndices(), prop
                                                                 +
                                                                 (notifycheck.isSelected() ?
                                                                  BannedWord.notify :
                                                                  0)
                                                                 +
                                                                 (privatecheck.isSelected() ?
                                                                  BannedWord.privatechat :
                                                                  0)
                                                                 +
                                                                 (searchcheck.isSelected() ?
                                                                  BannedWord.searches :
                                                                  0));
    }//GEN-LAST:event_jRadioButton6MouseClicked


    private void jRadioButton5MouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_jRadioButton5MouseClicked
        // TODO add your handling code here:
        long prop = getWordPr() + getClientPr();
        jTextField3.setText("");
        listaBanate.modifyMultiPrAt(jList1.getSelectedIndices(), prop
                                                                 +
                                                                 (notifycheck.isSelected() ?
                                                                  BannedWord.notify :
                                                                  0)
                                                                 +
                                                                 (privatecheck.isSelected() ?
                                                                  BannedWord.privatechat :
                                                                  0)
                                                                 +
                                                                 (searchcheck.isSelected() ?
                                                                  BannedWord.searches :
                                                                  0));
    }//GEN-LAST:event_jRadioButton5MouseClicked


    private void jRadioButton4MouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_jRadioButton4MouseClicked
        // TODO add your handling code here:
        long prop = getWordPr() + getClientPr();
        jTextField3.setText("");
        listaBanate.modifyMultiPrAt(jList1.getSelectedIndices(), prop
                                                                 +
                                                                 (notifycheck.isSelected() ?
                                                                  BannedWord.notify :
                                                                  0)
                                                                 +
                                                                 (privatecheck.isSelected() ?
                                                                  BannedWord.privatechat :
                                                                  0)
                                                                 +
                                                                 (searchcheck.isSelected() ?
                                                                  BannedWord.searches :
                                                                  0));
    }//GEN-LAST:event_jRadioButton4MouseClicked


    private void jRadioButton3MouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_jRadioButton3MouseClicked
        // TODO add your handling code here:
        long prop = getClientPr() + getWordPr();
        // System.out.println(prop);
        listaBanate.modifyMultiPrAt(jList1.getSelectedIndices(), prop
                                                                 +
                                                                 (notifycheck.isSelected() ?
                                                                  BannedWord.notify :
                                                                  0)
                                                                 +
                                                                 (privatecheck.isSelected() ?
                                                                  BannedWord.privatechat :
                                                                  0)
                                                                 +
                                                                 (searchcheck.isSelected() ?
                                                                  BannedWord.searches :
                                                                  0));
    }//GEN-LAST:event_jRadioButton3MouseClicked


    private void jRadioButton2MouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_jRadioButton2MouseClicked
        // TODO add your handling code here:
        long prop = getClientPr() + getWordPr();
        //System.out.println(prop);
        listaBanate.modifyMultiPrAt(jList1.getSelectedIndices(), prop
                                                                 +
                                                                 (notifycheck.isSelected() ?
                                                                  BannedWord.notify :
                                                                  0)
                                                                 +
                                                                 (privatecheck.isSelected() ?
                                                                  BannedWord.privatechat :
                                                                  0)
                                                                 +
                                                                 (searchcheck.isSelected() ?
                                                                  BannedWord.searches :
                                                                  0));
    }//GEN-LAST:event_jRadioButton2MouseClicked


    private void jRadioButton1MouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_jRadioButton1MouseClicked
        // TODO add your handling code here:
        long prop = getClientPr() + getWordPr();
        // System.out.println(prop);
        listaBanate.modifyMultiPrAt(jList1.getSelectedIndices(), prop
                                                                 +
                                                                 (notifycheck.isSelected() ?
                                                                  BannedWord.notify :
                                                                  0)
                                                                 +
                                                                 (privatecheck.isSelected() ?
                                                                  BannedWord.privatechat :
                                                                  0)
                                                                 +
                                                                 (searchcheck.isSelected() ?
                                                                  BannedWord.searches :
                                                                  0));
    }//GEN-LAST:event_jRadioButton1MouseClicked


    private void jList1MouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_jList1MouseClicked
        // TODO add your handling code here:
        int[] indici = jList1.getSelectedIndices();

        int i, n;
        n = indici.length;
        if (n == 0)
        {
            selectPr(BannedWord.dropped | BannedWord.hidden, "");
            return;
        }
        long prop = listaBanate.getPrAt(indici[0]);
        String repl = listaBanate.getReplAt(indici[0]);
        /*
		for (i=1;i<n;i++){
		    prop = prop & listaBanate.getPrAt(indici[i]);
		    if (!repl.equals(listaBanate.getReplAt(indici[i]))){
		        repl="";
		    };
		}
		 */
        // System.out.println(prop);
        selectPr(prop, repl);
    }//GEN-LAST:event_jList1MouseClicked


    private void jButton23MouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_jButton23MouseClicked
        // TODO add your handling code here:

        String banWord = jTextField2.getText();
        int l = banWord.length();
        if (!listaBanate.ver_regex(banWord))
        {
            JOptionPane.showMessageDialog(null, banWord
                                                + " is an invalid Regular Expression.", "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (l != 0)
        {
            if (listaBanate.searchEl(banWord) == -1)
            {
                DefaultListModel modelLista = (DefaultListModel) jList1
                        .getModel();
                modelLista.add(0, banWord);
            }
            long prop = 0;
            String repl = "";
            prop = prop | this.getClientAddPr() | this.getWordAddPr();
            repl += this.getAddRepl();
            listaBanate.add(banWord, prop + BannedWord.notify
                                     + BannedWord.privatechat, repl);
        }
    }//GEN-LAST:event_jButton23MouseClicked


    private void jRadioButton12StateChanged(javax.swing.event.ChangeEvent evt)
    {//GEN-FIRST:event_jRadioButton12StateChanged
        // TODO add your handling code here:
        jTextField5.setEditable(jRadioButton12.isSelected());
    }//GEN-LAST:event_jRadioButton12StateChanged


    private void jButton24MouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_jButton24MouseClicked
        // TODO add your handling code here:
        listaBanate.clean();
        DefaultListModel modelLista = (DefaultListModel) jList1.getModel();

        modelLista.removeAllElements();
    }//GEN-LAST:event_jButton24MouseClicked


    private void jRadioButton6StateChanged(javax.swing.event.ChangeEvent evt)
    {//GEN-FIRST:event_jRadioButton6StateChanged
        // TODO add your handling code here:
        jTextField3.setEditable(jRadioButton6.isSelected());

    }//GEN-LAST:event_jRadioButton6StateChanged


    private void jButton22ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton22ActionPerformed
    {//GEN-HEADEREND:event_jButton22ActionPerformed
        int row = AccountTable.getSelectedRow();
        if (row == -1)
        {
            return;
        }
        String CID = (String) AccountTable.getModel().getValueAt(row, 0);
        this.setVisible(false);
        AccountEditer Acc1 = new AccountEditer(CID);
        Acc1.setVisible(true);
    }//GEN-LAST:event_jButton22ActionPerformed


    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_jButton21ActionPerformed

        JOptionPane.showMessageDialog(null,
                                      "SUP is the ADC feature negotiating, command, hub-client.\n" +
                                      "( B ) No defined purpose.\n" +
                                      "( D ) No defined purpose.\n" +
                                      "( E ) No defined purpose.\n" +
                                      "( F ) No defined purpose.\n" +
                                      "( H ) Send to hub.",
                                      "SUP",
                                      JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton21ActionPerformed


    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_jButton20ActionPerformed


        JOptionPane.showMessageDialog(null,
                                      "RES is the search result command, is used to reply to searches.\n" +
                                      "( B ) No defined purpose.\n" +
                                      "( D ) Reply to a single user.\n" +
                                      "( E ) same as D.\n" +
                                      "( F ) No defined purpose.\n" +
                                      "( H ) No defined purpose.",
                                      "RES",
                                      JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton20ActionPerformed


    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_jButton19ActionPerformed

        JOptionPane.showMessageDialog(null,
                                      "PAS is the command that supplies the password to the hub.\n" +
                                      "( B ) No defined purpose.\n" +
                                      "( D ) No defined purpose.\n" +
                                      "( E ) No defined purpose.\n" +
                                      "( F ) No defined purpose.\n" +
                                      "( H ) Send to hub.",
                                      "PAS",
                                      JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton19ActionPerformed


    private void jButton18ActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_jButton18ActionPerformed

        JOptionPane.showMessageDialog(null,
                                      "INF is the information specifier command, is used to tell other clients\nabout one's ADC client.\n" +
                                      "( B ) Send info to all other clients.\n" +
                                      "( D ) No defined purpose.\n" +
                                      "( E ) No defined purpose.\n" +
                                      "( F ) No defined purpose.\n" +
                                      "( H ) No defined purpose.",
                                      "INF",
                                      JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton18ActionPerformed


    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_jButton17ActionPerformed


        JOptionPane.showMessageDialog(null,
                                      "SCH is the search command, is used to search for files.\n" +
                                      "( B ) Send search request to all other clients.\n" +
                                      "( D ) Search on a single user.\n" +
                                      "( E ) same as D.\n" +
                                      "( F ) Search featured clients.\n" +
                                      "( H ) No defined purpose.",
                                      "SCH",
                                      JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton17ActionPerformed


    private void jButton16ActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_jButton16ActionPerformed

        JOptionPane.showMessageDialog(null,
                                      "RCM is the reverse connect to me command, is used for requesting a direct\nconnection from another client, by a passive TCP user.\n" +
                                      "( B ) No defined purpose.\n" +
                                      "( D ) Requesting from other client.\n" +
                                      "( E ) same as D.\n" +
                                      "( F ) No defined purpose.\n" +
                                      "( H ) No defined purpose.",
                                      "RCM",
                                      JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton16ActionPerformed


    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_jButton15ActionPerformed

        JOptionPane.showMessageDialog(null,
                                      "CTM is the connect to me command, is used for requesting a\ndirectconnection from another client.\n" +
                                      "( B ) No defined purpose.\n" +
                                      "( D ) Connecting to other client.\n" +
                                      "( E ) same as D.\n" +
                                      "( F ) No defined purpose.\n" +
                                      "( H ) No defined purpose.",
                                      "CTM",
                                      JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton15ActionPerformed


    private void HSTAcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_HSTAcheckActionPerformed
        if (HSTAcheck.isSelected())

        {
            Main.PopMsg("HSTA changed from \"0\" to \"1\".");
            Vars.HSTA = 1;
        }
        else
        {
            Main.PopMsg("HSTA changed from \"1\" to \"0\".");
            Vars.HSTA = 0;
        }
    }//GEN-LAST:event_HSTAcheckActionPerformed


    private void FSTAcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_FSTAcheckActionPerformed
        if (FSTAcheck.isSelected())

        {
            Main.PopMsg("FSTA changed from \"0\" to \"1\".");
            Vars.FSTA = 1;
        }
        else
        {
            Main.PopMsg("FSTA changed from \"1\" to \"0\".");
            Vars.FSTA = 0;
        }
    }//GEN-LAST:event_FSTAcheckActionPerformed


    private void ESTAcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_ESTAcheckActionPerformed
        if (ESTAcheck.isSelected())

        {
            Main.PopMsg("ESTA changed from \"0\" to \"1\".");
            Vars.ESTA = 1;
        }
        else
        {
            Main.PopMsg("ESTA changed from \"1\" to \"0\".");
            Vars.ESTA = 0;
        }
    }//GEN-LAST:event_ESTAcheckActionPerformed


    private void DSTAcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_DSTAcheckActionPerformed
        if (DSTAcheck.isSelected())

        {
            Main.PopMsg("DSTA changed from \"0\" to \"1\".");
            Vars.DSTA = 1;
        }
        else
        {
            Main.PopMsg("DSTA changed from \"1\" to \"0\".");
            Vars.DSTA = 0;
        }
    }//GEN-LAST:event_DSTAcheckActionPerformed


    private void BSTAcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_BSTAcheckActionPerformed
        if (BSTAcheck.isSelected())

        {
            Main.PopMsg("BSTA changed from \"0\" to \"1\".");
            Vars.BSTA = 1;
        }
        else
        {
            Main.PopMsg("BSTA changed from \"1\" to \"0\".");
            Vars.BSTA = 0;
        }
    }//GEN-LAST:event_BSTAcheckActionPerformed


    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_jButton14ActionPerformed

        JOptionPane.showMessageDialog(null,
                                      "STA is the status command, can be used either for confirming\ncommands or signaling some error.\n" +
                                      "( B ) no defined purpose for STA.\n" +
                                      "( D ) Can be sent to a specified client.\n" +
                                      "( E ) same as D.\n" +
                                      "( F ) No defined purpose.\n" +
                                      "( H ) To be sent to hub.",
                                      "STA",
                                      JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton14ActionPerformed


    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_jButton13ActionPerformed


        JOptionPane.showMessageDialog(null, "MSG is the chat command.\n" +
                                            "( B ) is broadcast MSG ( main chat ).\n" +
                                            "( D ) is direct msg, used for private message.\n" +
                                            "( E ) is used for private message too.\n" +
                                            "( F ) can be used by ADC clients.\n" +
                                            "( H ) can be used in some messages from clients.",
                                      "MSG",
                                      JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton13ActionPerformed


    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_jButton12ActionPerformed

        JOptionPane.showMessageDialog(null,
                                      "ADC uses a context for each command. That is necessary because every command can be\n" +
                                      "treated differently according to it's context. The context for each command that you can change:\n" +
                                      "Broadcast ( B ) is the context in which the command is being sent to all clients connected.\n" +
                                      "Direct Message ( D ) is intended for a single user and coming from a single user.\n" +
                                      "This is tipically used for requesting direct connection, or perhaps private message.\n" +
                                      "Direct Echo Message ( E ) is much alike D, except that the message is sent to first user too ( echoing it ).\n" +
                                      "Feature Broadcast ( F ) is much alike ( B ), except that the broadcast is not sent to all, but to users \n" +
                                      "that have some feature, like passive searching (sending to active only).\n" +
                                      "To Hub only ( H ) is a message from a single client intended for hub only, like negotiating protocol features.",
                                      "What are this contexts about ?",
                                      JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_jButton12ActionPerformed


    private void HMSGcheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_HMSGcheckActionPerformed
    {//GEN-HEADEREND:event_HMSGcheckActionPerformed
        if (HMSGcheck.isSelected())

        {
            Main.PopMsg("HMSG changed from \"0\" to \"1\".");
            Vars.HMSG = 1;
        }
        else
        {
            Main.PopMsg("HMSG changed from \"1\" to \"0\".");
            Vars.HMSG = 0;
        }
    }//GEN-LAST:event_HMSGcheckActionPerformed


    private void FMSGcheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_FMSGcheckActionPerformed
    {//GEN-HEADEREND:event_FMSGcheckActionPerformed
        if (FMSGcheck.isSelected())

        {
            Main.PopMsg("FMSG changed from \"0\" to \"1\".");
            Vars.FMSG = 1;
        }
        else
        {
            Main.PopMsg("FMSG changed from \"1\" to \"0\".");
            Vars.FMSG = 0;
        }
    }//GEN-LAST:event_FMSGcheckActionPerformed


    private void EMSGcheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_EMSGcheckActionPerformed
    {//GEN-HEADEREND:event_EMSGcheckActionPerformed
        if (EMSGcheck.isSelected())

        {
            Main.PopMsg("EMSG changed from \"0\" to \"1\".");
            Vars.EMSG = 1;
        }
        else
        {
            Main.PopMsg("EMSG changed from \"1\" to \"0\".");
            Vars.EMSG = 0;
        }
    }//GEN-LAST:event_EMSGcheckActionPerformed


    private void DMSGcheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_DMSGcheckActionPerformed
    {//GEN-HEADEREND:event_DMSGcheckActionPerformed
        if (DMSGcheck.isSelected())

        {
            Main.PopMsg("DMSG changed from \"0\" to \"1\".");
            Vars.DMSG = 1;
        }
        else
        {
            Main.PopMsg("DMSG changed from \"1\" to \"0\".");
            Vars.DMSG = 0;
        }
    }//GEN-LAST:event_DMSGcheckActionPerformed


    private void BMSGcheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BMSGcheckActionPerformed
    {//GEN-HEADEREND:event_BMSGcheckActionPerformed

        if (BMSGcheck.isSelected())

        {
            Main.PopMsg("BMSG changed from \"0\" to \"1\".");
            Vars.BMSG = 1;
        }
        else
        {
            Main.PopMsg("BMSG changed from \"1\" to \"0\".");
            Vars.BMSG = 0;
        }
    }//GEN-LAST:event_BMSGcheckActionPerformed


    private void AccountTableKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_AccountTableKeyPressed
    {//GEN-HEADEREND:event_AccountTableKeyPressed
        // TODO add your handling code here:
        //SetStatus("lesbo");

        if (evt.getKeyCode() == evt.VK_DELETE)
        // SetStatus("gay");
        {
            //need to ureg that reg
            //  int row=AccountTable.getEditingRow ();
            deleteSelectedReg();
        }
    }//GEN-LAST:event_AccountTableKeyPressed


    private void AccountTableKeyTyped(java.awt.event.KeyEvent evt)//GEN-FIRST:event_AccountTableKeyTyped
    {//GEN-HEADEREND:event_AccountTableKeyTyped
        // TODO add your handling code here:

    }//GEN-LAST:event_AccountTableKeyTyped


    private void jScrollPane2KeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_jScrollPane2KeyPressed
    {//GEN-HEADEREND:event_jScrollPane2KeyPressed
        // TODO add your handling code here:

    }//GEN-LAST:event_jScrollPane2KeyPressed


    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton5ActionPerformed
    {//GEN-HEADEREND:event_jButton5ActionPerformed
        // TODO add your handling code here:
        JDialog bla = new JDialog(this, true);
        JPanel jp = new JPanel();

        bla.setSize(300, 400);
        bla.setTitle("What this means ?");

        bla.getContentPane().add(jp);
        JTextArea jl = new JTextArea(
                "DSHub has now powerful searching features.\nFirst, we need to make a distinction between the\nautomagic "
                +
                "and the user searches.\n First type is made by client at a regular interval\nand DSHub keeps a liniar spam setting.\n"
                +
                " Second type are user searches ( manual searches )\nthat the user takes.\nFor this type (because of the human factor)\n"
                +
                "DSHub keeps a logarithmic spam setting.\nThis way, the 2nd search is at search_log_base\ninterval, but third, is at search_log_base^2\n"
                +
                "and so on, until the power gets to max_steps.\n After this point, the user needs to wait\nsearch_spam_reset seconds to get his burst back.\n"
                +
                "The searches are being kept in queue ( not ignored !)\nand are processed once the timeout is completed\nso user doesnt need to search again\n"
                +
                "but just wait for his search to be completed.\nThe messages appears as a fictive result\nin his search box, which will be filled\n"
                +
                "once the search string is being sent to others.\n");
        // jl.setSize (100,30);
        jp.add(jl);
        // jp.add(new JLabel("test"));
        bla.setVisible(true);
        // jc.add (new JLabel("Blabla"));
    }//GEN-LAST:event_jButton5ActionPerformed


    private void savelogscheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_savelogscheckActionPerformed
    {//GEN-HEADEREND:event_savelogscheckActionPerformed
        // TODO add your handling code here:
        if (savelogscheck.isSelected())
        //Main.PopMsg("clicked");
        {
            Main.PopMsg("Save_logs changed from \"0\" to \"1\".");
            Vars.savelogs = 1;
        }
        else
        {
            Main.PopMsg("Save_logs changed from \"1\" to \"0\".");
            Vars.savelogs = 0;
        }
    }//GEN-LAST:event_savelogscheckActionPerformed


    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton11ActionPerformed
    {//GEN-HEADEREND:event_jButton11ActionPerformed
        // TODO add your handling code here:
        String Thing = opchatnamefield.getText();
        try
        {
            String aucsy = Vars.Opchat_name;
            if (!(aucsy.equals(Thing)))
            {
                if (!Vars.ValidateNick(Thing))
                {
                    throw new Exception();
                }
                for (ClientNod tempy : SimpleHandler.getUsers())
                {
                    if (tempy.cur_client.userok == 1)
                    {
                        if ((tempy.cur_client.NI.toLowerCase().equals(Thing
                                                                              .toLowerCase())))
                        {
                            throw new Exception();
                        }
                    }

                }

                Vars.Opchat_name = Thing;
                Main.PopMsg("Opchat_name changed from \"" + aucsy + "\" to \""
                            + Thing + "\".");
                Broadcast.getInstance().broadcast(
                        "BINF ABCD NI" + Vars.Opchat_name,
                        Broadcast.STATE_ALL_KEY);

            }
        }
        catch (Exception e)
        {
            opchatnamefield.setText(Vars.Opchat_name);
        }
        Thing = opchatdescfield.getText();

        String aucsy = Vars.Opchat_desc;
        if (!(aucsy.equals(Thing)))
        {

            Vars.Opchat_desc = Thing;
            Main.PopMsg("Opchat_desc changed from \"" + aucsy + "\" to \""
                        + Thing + "\".");
            Broadcast.getInstance().broadcast(
                    "BINF ABCD DE" + ADC.retADCStr(Vars.Opchat_desc),
                    Broadcast.STATE_ALL_KEY);

        }
        Thing = historylinesfield.getText();
        try
        {
            int aucs = Vars.history_lines;
            if (aucs != Integer.parseInt(Thing))
            {
                Vars.history_lines = Integer.parseInt(Thing.toString());
                Main.PopMsg("History_lines changed from \""
                            + Integer.toString(aucs) + "\" to \""
                            + Vars.history_lines + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            historylinesfield.setText(Integer.toString(Vars.history_lines));
        }
        Thing = kicktimefield.getText();
        try
        {
            int aucs = Vars.kick_time;
            if (aucs != Integer.parseInt(Thing))
            {
                Vars.kick_time = Integer.parseInt(Thing.toString());
                Main.PopMsg("Kick_time changed from \""
                            + Integer.toString(aucs) + "\" to \"" + Vars.kick_time
                            + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            kicktimefield.setText(Integer.toString(Vars.kick_time));
        }

        Thing = redirecturl.getText();

        aucsy = Vars.redirect_url;
        if (!(aucsy.equals(Thing)))
        {

            Vars.redirect_url = Thing;
            Main.PopMsg("Redirect_url changed from \"" + aucsy + "\" to \""
                        + Thing + "\".");

        }

        /**bot_name*/
        Thing = botnamefield.getText();
        try
        {
            aucsy = Vars.bot_name;
            if (!(aucsy.equals(Thing)))
            {
                if (!Vars.ValidateNick(Thing))
                {
                    throw new Exception();
                }
                for (ClientNod tempy : SimpleHandler.getUsers())
                {
                    if (tempy.cur_client.userok == 1)
                    {
                        if ((tempy.cur_client.NI.toLowerCase().equals(Thing
                                                                              .toLowerCase())))
                        {
                            throw new Exception();
                        }
                    }

                }

                Vars.bot_name = Thing;
                Main.PopMsg("bot_name changed from \"" + aucsy + "\" to \""
                            + Thing + "\".");
                Broadcast.getInstance().broadcast(
                        "BINF DCBA NI" + ADC.retADCStr(Vars.bot_name));

            }
        }
        catch (Exception e)
        {
            botnamefield.setText(Vars.bot_name);
        }
        /**bot desc*/
        Thing = botdescfield.getText();

        aucsy = Vars.bot_desc;
        if (!(aucsy.equals(Thing)))
        {

            Vars.bot_desc = Thing;
            Main.PopMsg("Bot_desc changed from \"" + aucsy + "\" to \"" + Thing
                        + "\".");
            Broadcast.getInstance().broadcast(
                    "BINF DCBA DE" + ADC.retADCStr(Vars.bot_desc));

        }

        SetStatus("Miscellaneous settings saved.");
    }//GEN-LAST:event_jButton11ActionPerformed


    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton10ActionPerformed
    {//GEN-HEADEREND:event_jButton10ActionPerformed
        // TODO add your handling code here:
        String Thing = maxchatmsgfield.getText();
        try
        {
            int aucsy = Vars.max_chat_msg;
            if (aucsy != Integer.parseInt(Thing))
            {
                Vars.max_chat_msg = Integer.parseInt(Thing.toString());
                Main.PopMsg("Max_chat_msg changed from \""
                            + Integer.toString(aucsy) + "\" to \""
                            + Vars.max_chat_msg + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            maxchatmsgfield.setText(Integer.toString(Vars.max_chat_msg));
        }
        Thing = chatintervalfield.getText();
        try
        {
            int aucsy = Vars.chat_interval;
            if (aucsy != Integer.parseInt(Thing))
            {
                Vars.chat_interval = Integer.parseInt(Thing.toString());
                Main.PopMsg("Chat_interval changed from \""
                            + Integer.toString(aucsy) + "\" to \""
                            + Vars.chat_interval + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            chatintervalfield.setText(Integer.toString(Vars.chat_interval));
        }

        Thing = automagicsearchfield.getText();
        try
        {
            int aucsy = Vars.automagic_search;
            if (aucsy != Integer.parseInt(Thing))
            {

                Vars.automagic_search = Integer.parseInt(Thing.toString());
                Main.PopMsg("Automagic_search changed from \""
                            + Integer.toString(aucsy) + "\" to \""
                            + Vars.automagic_search + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            automagicsearchfield.setText(Integer
                                                 .toString(Vars.automagic_search));
        }
        Thing = searchlogbasefield.getText();
        try
        {
            int aucsy = Vars.search_log_base;
            if (aucsy != Integer.parseInt(Thing))
            {

                Vars.search_log_base = Integer.parseInt(Thing.toString());
                Main.PopMsg("Search_log_base changed from \""
                            + Integer.toString(aucsy) + "\" to \""
                            + Vars.search_log_base + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            searchlogbasefield.setText(Integer.toString(Vars.search_log_base));
        }
        Thing = searchstepsfield.getText();
        try
        {
            int aucsy = Vars.search_steps;
            if (aucsy != Integer.parseInt(Thing))
            {

                Vars.search_steps = Integer.parseInt(Thing.toString());
                Main.PopMsg("Search_steps changed from \""
                            + Integer.toString(aucsy) + "\" to \""
                            + Vars.search_steps + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            searchstepsfield.setText(Integer.toString(Vars.search_steps));
        }
        Thing = searchspamresetfield.getText();
        try
        {
            int aucsy = Vars.search_spam_reset;
            if (aucsy != Integer.parseInt(Thing))
            {

                Vars.search_spam_reset = Integer.parseInt(Thing.toString());
                Main.PopMsg("Search_spam_reset changed from \""
                            + Integer.toString(aucsy) + "\" to \""
                            + Vars.search_spam_reset + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            searchspamresetfield.setText(Integer
                                                 .toString(Vars.search_spam_reset));
        }

        Thing = msgsearchspamfield.getText();

        String aucsy = Vars.Msg_Search_Spam;
        if (!(aucsy.equals(Thing)))
        {

            Vars.Msg_Search_Spam = Thing;
            Main.PopMsg("Msg_Search_Spam changed from \"" + aucsy + "\" to \""
                        + Thing + "\".");

        }

        SetStatus("Spam settings saved.");
    }//GEN-LAST:event_jButton10ActionPerformed


    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton9ActionPerformed
    {//GEN-HEADEREND:event_jButton9ActionPerformed
        // TODO add your handling code here:
        String Thing = maxusersfield.getText();
        try
        {
            int aucsy = Vars.max_users;
            if (aucsy != Integer.parseInt(Thing))
            {
                Vars.max_users = Integer.parseInt(Thing.toString());
                Main.PopMsg("Max_users changed from \""
                            + Integer.toString(aucsy) + "\" to \"" + Vars.max_users
                            + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            maxusersfield.setText(Integer.toString(Vars.max_users));
        }
        Thing = nickcharsfield.getText();

        String aucsy = Vars.nick_chars;
        if (!(aucsy.equals(Thing)))
        {
            Vars.nick_chars = Thing;
            Main.PopMsg("Nick_chars changed from \"" + aucsy + "\" to \""
                        + Thing + "\".");
        }
        nickcharsfield.setText(aucsy);
        Thing = maxschcharsfield.getText();
        try
        {
            int aucs = Vars.max_sch_chars;
            if (aucs != Integer.parseInt(Thing))
            {
                Vars.max_sch_chars = Integer.parseInt(Thing.toString());
                Main.PopMsg("Max_sch_chars changed from \""
                            + Integer.toString(aucs) + "\" to \""
                            + Vars.max_sch_chars + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            maxschcharsfield.setText(Integer.toString(Vars.max_sch_chars));
        }

        SetStatus("Restrictions settings saved.");

    }//GEN-LAST:event_jButton9ActionPerformed


    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton8ActionPerformed
    {//GEN-HEADEREND:event_jButton8ActionPerformed
        // TODO add your handling code here:
        /** timeout_login*/
        String Thing = fieldtimeout.getText();
        try
        {
            int aucsy = Vars.Timeout_Login;
            int aux = Integer.parseInt(Thing);
            if (aucsy != aux)
            {
                Vars.Timeout_Login = aux;
                Main.PopMsg("Timeout_Login changed from \""
                            + Integer.toString(aucsy) + "\" to \"" + Thing + "\".");
            }
        }
        catch (NumberFormatException nfe)
        {
            // System.out.println("Invalid number");
            fieldtimeout.setText(Integer.toString(Vars.Timeout_Login));
        }

        Thing = maxnifield.getText();
        try
        {
            int aucsy = Vars.max_ni;
            if (aucsy != Integer.parseInt(Thing))
            {
                Vars.max_ni = Integer.parseInt(Thing.toString());
                Main.PopMsg("Max_ni changed from \"" + Integer.toString(aucsy)
                            + "\" to \"" + Vars.max_ni + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            maxnifield.setText(Integer.toString(Vars.max_ni));
        }
        Thing = minnifield.getText();
        try
        {
            int aucsy = Vars.min_ni;
            if (aucsy != Integer.parseInt(Thing))
            {
                Vars.min_ni = Integer.parseInt(Thing.toString());
                Main.PopMsg("Min_ni changed from \"" + Integer.toString(aucsy)
                            + "\" to \"" + Vars.min_ni + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            minnifield.setText(Integer.toString(Vars.min_ni));
        }
        Thing = maxdefield.getText();
        try
        {
            int aucsy = Vars.max_de;
            if (aucsy != Integer.parseInt(Thing))
            {
                Vars.max_de = Integer.parseInt(Thing.toString());
                Main.PopMsg("Max_de changed from \"" + Integer.toString(aucsy)
                            + "\" to \"" + Vars.max_de + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            maxdefield.setText(Integer.toString(Vars.max_de));
        }
        Thing = maxsharefield.getText();
        try
        {
            Long aucsy = Vars.max_share;
            if (aucsy != Long.parseLong(Thing))
            {
                Vars.max_share = Long.parseLong(Thing.toString());
                Main.PopMsg("Max_share changed from \"" + Long.toString(aucsy)
                            + "\" to \"" + Vars.max_share + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            maxsharefield.setText(Long.toString(Vars.min_share));
        }
        Thing = minsharefield.getText();
        try
        {
            Long aucsy = Vars.min_share;
            if (aucsy != Long.parseLong(Thing))
            {
                Vars.min_share = Long.parseLong(Thing.toString());
                Main.PopMsg("Min_share changed from \"" + Long.toString(aucsy)
                            + "\" to \"" + Vars.min_share + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            minsharefield.setText(Long.toString(Vars.min_share));
        }
        Thing = maxslfield.getText();
        try
        {
            int aucsy = Vars.max_sl;
            if (aucsy != Integer.parseInt(Thing))
            {
                Vars.max_sl = Integer.parseInt(Thing.toString());
                Main.PopMsg("Max_sl changed from \"" + Integer.toString(aucsy)
                            + "\" to \"" + Vars.max_sl + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            maxslfield.setText(Integer.toString(Vars.max_sl));
        }
        Thing = minslfield.getText();
        try
        {
            int aucsy = Vars.min_sl;
            if (aucsy != Integer.parseInt(Thing))
            {
                Vars.min_sl = Integer.parseInt(Thing.toString());
                Main.PopMsg("Min_sl changed from \"" + Integer.toString(aucsy)
                            + "\" to \"" + Vars.min_sl + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            minslfield.setText(Integer.toString(Vars.min_sl));
        }
        Thing = maxemfield.getText();
        try
        {
            int aucsy = Vars.max_em;
            if (aucsy != Integer.parseInt(Thing))
            {
                Vars.max_em = Integer.parseInt(Thing.toString());
                Main.PopMsg("Max_em changed from \"" + Integer.toString(aucsy)
                            + "\" to \"" + Vars.max_em + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            maxemfield.setText(Integer.toString(Vars.max_em));
        }
        Thing = maxhubsopfield.getText();
        try
        {
            int aucsy = Vars.max_hubs_op;
            if (aucsy != Integer.parseInt(Thing))
            {
                Vars.max_hubs_op = Integer.parseInt(Thing.toString());
                Main.PopMsg("Max_hubs_op changed from \""
                            + Integer.toString(aucsy) + "\" to \""
                            + Vars.max_hubs_op + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            maxhubsopfield.setText(Integer.toString(Vars.max_hubs_op));
        }
        Thing = maxhubsregfield.getText();
        try
        {
            int aucsy = Vars.max_hubs_reg;
            if (aucsy != Integer.parseInt(Thing))
            {
                Vars.max_hubs_reg = Integer.parseInt(Thing.toString());
                Main.PopMsg("Max_hubs_reg changed from \""
                            + Integer.toString(aucsy) + "\" to \""
                            + Vars.max_hubs_reg + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            maxhubsregfield.setText(Integer.toString(Vars.max_hubs_reg));
        }
        Thing = maxhubsuserfield.getText();
        try
        {
            int aucsy = Vars.max_hubs_user;
            if (aucsy != Integer.parseInt(Thing))
            {
                Vars.max_hubs_user = Integer.parseInt(Thing.toString());
                Main.PopMsg("Max_hubs_user changed from \""
                            + Integer.toString(aucsy) + "\" to \""
                            + Vars.max_hubs_user + "\".");
            }

        }
        catch (NumberFormatException nfe)
        {
            maxhubsuserfield.setText(Integer.toString(Vars.max_hubs_user));
        }

        Main.Server.rewriteconfig();
        SetStatus("Restrictions settings saved.");
    }//GEN-LAST:event_jButton8ActionPerformed


    private void regonlycheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_regonlycheckActionPerformed
    {//GEN-HEADEREND:event_regonlycheckActionPerformed
        // TODO add your handling code here:
        if (regonlycheck.isSelected())
        //Main.PopMsg("clicked");
        {
            Main.PopMsg("Reg_only changed from \"0\" to \"1\".");
            Vars.reg_only = 1;
        }
        else
        {
            Main.PopMsg("Reg_only changed from \"1\" to \"0\".");
            Vars.reg_only = 0;
        }
    }//GEN-LAST:event_regonlycheckActionPerformed


    public void insertLog(String bla)
    {
        LogText.append(bla + "\n");
    }


    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton7ActionPerformed
    {//GEN-HEADEREND:event_jButton7ActionPerformed
        // TODO add your handling code here:
        /* String thenewport= portfield.getText();
		 //System.out.println (newport);
		 try {
		     int x=Integer.parseInt(thenewport);
		     if(x!=Vars.Default_Port)
		         if(x>3 && x<65000) {
		         int y=Vars.Default_Port;
		         Vars.Default_Port=x;
		         //Main.Server.vars.Default_Port=x;
		         Main.PopMsg("New Default Port change from "+y+" to "+x+".");
		         }
		 } catch (Exception e) {
		 }*/
        String newtopic = topicfield.getText();
        if (!(newtopic.equals(Vars.HubDE)))
        {
            if (newtopic.equals(""))
            {

                Broadcast.getInstance().broadcast("IINF DE");
                if (!Vars.HubDE.equals(""))
                {
                    //System.out.println("Topic \""+Vars.HubDE+"\" deleted.");
                    Broadcast.getInstance().broadcast(
                            "IMSG Topic was deleted by Server.");
                    Main.PopMsg("Topic was deleted by Server.");
                }
                else
                //System.out.println("There wasn't any topic anyway.");
                {
                    Vars.HubDE = "";
                }

            }
            else
            {
                String auxbuf = newtopic;

                Vars.HubDE = Vars.HubDE;
                // System.out.println("Topic changed from \""+Vars.HubDE+"\" "+"to \""+auxbuf+"\".");
                auxbuf = auxbuf;
                Vars.HubDE = auxbuf;

                Broadcast.getInstance().broadcast(
                        "IINF DE" + ADC.retADCStr(auxbuf));
                Broadcast.getInstance().broadcast(
                        "IMSG Topic was changed by Server to \"" + Vars.HubDE
                        + "\".");
                Main.PopMsg("Topic was changed by Server to \"" + Vars.HubDE
                            + "\".");

            }
        }
        /**hub name*/
        String NowName = namefield.getText();

        if (!(NowName.equals(Vars.HubName)))
        {

            Main.PopMsg("Hub_Name changed from \"" + Vars.HubName + "\" to \""
                        + NowName + "\".");
            Vars.HubName = NowName.toString();

            Broadcast.getInstance().broadcast(
                    "IINF NI" + ADC.retADCStr(Vars.HubName));
        }

        /** hub host */
        setHost();

        /** proxy settings */
        SetProxy();

        Main.Server.rewriteconfig();
        refreshAll();

        SetStatus("Main settings saved.");

    }//GEN-LAST:event_jButton7ActionPerformed


    private void setHost()
    {
        String new_name = hubhostfield.getText();
        if (new_name == null)
        {
            JOptionPane.showMessageDialog(null, "Hub_host cannot be null",
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
        else if ("".equals(new_name))
        {
            JOptionPane.showMessageDialog(null, "Hub_host cannot be empty.",
                                          "Error", JOptionPane.ERROR_MESSAGE);
        }
        if (!(new_name.equals(Vars.Hub_Host)))
        {

            int x = new_name.indexOf(':');
            if (x == -1 || x > new_name.length() - 1)
            {
                JOptionPane.showMessageDialog(null,
                                              "Hub_host must be in format address:port.", "Error",
                                              JOptionPane.ERROR_MESSAGE);
                return;
            }

            int result = JOptionPane
                    .showConfirmDialog(
                            this,
                            "Press ok to scan hub_host ( may take a while) \nso please be patient",
                            Vars.HubName, JOptionPane.OK_OPTION,
                            JOptionPane.INFORMATION_MESSAGE);
            if (result == JOptionPane.NO_OPTION)
            {
                return;
            }
            if (!(HostTester.hostOK(new_name)))
            {
                JOptionPane
                        .showMessageDialog(
                                null,
                                new_name
                                + " does not point to one of your eth interfaces. "
                                +
                                "\nReasons: DNS not correctly set;  you dont have a external real IP \n(if you are creating"
                                +
                                ""
                                +
                                " LAN hub, use your LAN local IP as a hub_host);\nnot even package routing to your system work.",
                                "Error", JOptionPane.ERROR_MESSAGE);

                return;
            }

            Main.PopMsg("Hub_host changed from \"" + Vars.Hub_Host + "\" to \""
                        + new_name + "\".");

            Vars.Hub_Host = new_name;
            Main.Server.rewriteconfig();

        }
    }


    private void SetProxy()
    {
        if (!proxycheck.isSelected())
        {
            if (!("".equals(Vars.Proxy_Host)))
            {
                Main.PopMsg("Proxy_Host changed from \"" + Vars.Proxy_Host
                            + "\" to \"" + (Vars.Proxy_Host = "") + "\".");
            }
            if (0 != Vars.Proxy_Port)
            {
                Main.PopMsg("Proxy_Port changed from \"" + Vars.Proxy_Port
                            + "\" to \"" + (Vars.Proxy_Port = 0) + "\".");
            }
            proxyhostfield.setEditable(false);
            proxyportfield.setEditable(false);
            proxyhostfield.setText("");
            proxyportfield.setText("");

            refreshAll();
            return;
        }
        if (proxyhostfield.getText().equals("") && proxycheck.isSelected())
        {
            refreshAll();
            return;
        }
        try
        {
            if ((!(Vars.Proxy_Host.equals(proxyhostfield.getText())) || Vars.Proxy_Port != Integer
                    .parseInt(proxyportfield.getText()))
                || (!Vars.Proxy_Host.equals("") && !proxycheck.isSelected()))
            {

                proxyhostfield.setEditable(true);
                proxyportfield.setEditable(true);

                int x = Integer.parseInt(proxyportfield.getText());

                if ((x < 1 || x > 65355))
                {
                    this.SetStatus("Invalid port number.",
                                   JOptionPane.ERROR_MESSAGE);
                    refreshAll();
                    return;

                }
                else if (x != Vars.Proxy_Port)
                {
                    Main.PopMsg("Proxy_Port changed from \"" + Vars.Proxy_Port
                                + "\" to \"" + (Vars.Proxy_Port = x) + "\".");
                }

                if (Vars.Proxy_Port == 0)
                {
                    this.SetStatus("Setup proxy port first.",
                                   JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String neww = proxyhostfield.getText();
                try
                {
                    new Proxy(Proxy.Type.HTTP, new InetSocketAddress(neww,
                                                                     Vars.Proxy_Port));
                }
                catch (Exception e)
                {
                    this
                            .SetStatus(
                                    "Invalid proxy. Possible reasons: incorrect input, domain resolution failure, invalid proxy.",
                                    JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String y = proxyhostfield.getText();
                if (!(y.equals(Vars.Proxy_Port)))
                {
                    Main.PopMsg("Proxy_Host changed from \"" + Vars.Proxy_Host
                                + "\" to \"" + (Vars.Proxy_Host = y) + "\".");
                }

            }
        }
        catch (NumberFormatException nfe)
        {
            this.SetStatus("Invalid port number.", JOptionPane.ERROR_MESSAGE);
            refreshAll();
            return;
        }
    }


    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton6ActionPerformed
    {//GEN-HEADEREND:event_jButton6ActionPerformed
        // TODO add your handling code here:
        refreshStats();
    }//GEN-LAST:event_jButton6ActionPerformed


    private void jPanel6MouseMoved(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jPanel6MouseMoved
    {//GEN-HEADEREND:event_jPanel6MouseMoved
        // TODO add your handling code here:

        // refreshAll();
    }//GEN-LAST:event_jPanel6MouseMoved


    public void refreshInit()
    {
        resizeBanTable(2, 7, 6, 5, 6, 7, 10, 4);
        insertBans();
        /**setting window name*/
        //System.out.println("gay");
        this.setTitle(Vars.HubName + " running on " + Vars.HubVersion
                      + " A New Generation 2007-2008");
        //jLabel51.setIcon (new Icon("ds.bmp"));
        //addImage(jPanel1, "ds.bmp");
        /**showing accounts*/

        AccountTable.setAutoResizeMode(AccountTable.AUTO_RESIZE_OFF);
        AccountTable.getColumnModel().getColumn(0).setPreferredWidth(
                AccountTable.getWidth() / 5);
        AccountTable.getColumnModel().getColumn(1).setPreferredWidth(
                AccountTable.getWidth() / 5);
        AccountTable.getColumnModel().getColumn(2).setPreferredWidth(
                AccountTable.getWidth() / 5);
        AccountTable.getColumnModel().getColumn(3).setPreferredWidth(
                AccountTable.getWidth() / 5);
        AccountTable.getColumnModel().getColumn(4).setPreferredWidth(
                AccountTable.getWidth() / 5);

        portlist.setAutoResizeMode(portlist.AUTO_RESIZE_OFF);
        portlist.getColumnModel().getColumn(0).setPreferredWidth(50);
        portlist.getColumnModel().getColumn(1).setPreferredWidth(100);
        portlist.getColumnModel().getColumn(2).setPreferredWidth(131);

        PyTable.setAutoResizeMode(portlist.AUTO_RESIZE_OFF);

        PyTable.getColumnModel().getColumn(0).setPreferredWidth(500);
        PyTable.getColumnModel().getColumn(1).setPreferredWidth(60);

    }


    public void refreshAll()
    {
        refreshInit();

        DefaultTableModel AccountModel = (DefaultTableModel) AccountTable
                .getModel();
        Nod n = AccountsConfig.First;
        int regcount = 0;
        while (n != null)
        {
            regcount++;
            n = n.Next;
        }

        if (regcount != AccountModel.getRowCount())
        {
            AccountModel.setRowCount(0);
            n = AccountsConfig.First;
            while (n != null)
            {
                String blah00 = "";
                Date d = new Date(n.CreatedOn);
                if (n.LastNI != null)
                {
                    blah00 = n.LastNI;
                }
                else
                {
                    blah00 = "Never seen online.";
                }

                AccountModel.addRow(new Object[]{ n.CID, blah00, n.LastIP,
                                                  n.WhoRegged, d.toString() });
                n = n.Next;
            }
        }
        //  blah00=blah00.substring (0,blah00.length ()-2);
        // System.out.println (blah00);
        if (HubServer.done_adcs)
        {
            if (Vars.adcs_mode && Main.Server.adcs_ok)
            {

                getEnableadcs().setEnabled(false);
                disableadcs.setEnabled(true);
                adcslabel.setIcon(onIco);
                adcslabel.setText("Running in ADC Secure mode");


            }
            else
            {
                if (Main.Server.adcs_ok)
                {
                    getEnableadcs().setEnabled(true);
                }
                else
                {
                    getEnableadcs().setEnabled(false);
                }
                disableadcs.setEnabled(false);
                adcslabel.setIcon(offIco);
                adcslabel.setText("ADC Secure mode not enabled");
            }
        }
        //if(Main.Server.adcs_ok)

        /**setting stuff*/
        jTextArea1.setText(ADC.GreetingMsg);
        jTextArea1.setSelectionStart(0);
        jTextArea1.setSelectionEnd(0);

        Runtime myRun = Runtime.getRuntime();
        refreshPyScripts();

        /*int i = 0, j = 0;
		for (ClientNod temp : SimpleHandler.getUsers()) {
			if (temp.cur_client.userok == 1)
				i++;
			else
				j++;

		}

		long up = System.currentTimeMillis() - Main.curtime; //uptime in millis

		Date b = new Date(Main.curtime);
		jTextArea2.setText("Death Squad Hub. Version "
				+ Vars.HubVersion
				+ ".\n"
				+ "  Running on "
				+ Main.Proppies.getProperty("os.name")
				+ " Version "
				+ Main.Proppies.getProperty("os.version")
				+ " on Architecture "
				+ Main.Proppies.getProperty("os.arch")
				+ "\n"
				+ "  Java Runtime Environment "
				+ Main.Proppies.getProperty("java.version")
				+ " from "
				+ Main.Proppies.getProperty("java.vendor")
				+ "\n"
				+ "  Java Virtual Machine "
				+ Main.Proppies.getProperty("java.vm.specification.version")
				+ "\n"
				+ "  Available CPU's to JVM "
				+ Integer.toString(myRun.availableProcessors())
				+ "\n"
				+ "  Available Memory to JVM: "
				+ Long.toString(myRun.maxMemory())
				+ " Bytes, where free: "
				+ Long.toString(myRun.freeMemory())
				+ " Bytes\n"
				+ "Hub Statistics:\n"
				+ "  Online users: "
				+ Integer.toString(i)
				+ "\n"
				+ "  Connecting users: "
				+ Integer.toString(j)
				+ "\n"
				+ "  Uptime: "
				+ TimeConv.getStrTime(up)
				+ "\n"
				+ "  Start Time: "
				+ b.toString() //+
				//+ "\n  Bytes read per second: "
			//	+ (Main.Server.IOSM == null ? "0.0" : Main.Server.IOSM
			//			.getTotalByteReadThroughput())
			//	+ "\n  Bytes written per second: "
			//	+ (Main.Server.IOSM == null ? "0.0" : Main.Server.IOSM
			//			.getTotalByteWrittenThroughput())
              //   "\n  Bytes read per second: "+(Main.Server.acceptor == null 
            //    		 ? "0.0" : Main.Server.acceptor.getReadBytesThroughput())+
           //    "\n  Bytes written per second: "+(Main.Server.acceptor == null ? 
          //  		   "0.0": Main.Server.acceptor.getWrittenBytesThroughput())
		);
*/
        refreshStats();
        /*
		 * max_em                  128        -- Maximum e-mail string size, integer.
		 max_hubs_op             100       -- Maximum hubs where user is op, integer.
		 max_hubs_reg            30       -- Maximum hubs where user is reg, integer.
		 max_hubs_user           200         -- Maximum hubs where user is user, integer.
		 max_sch_chars           256      -- Maximum search chars, integer.
		 min_sch_chars           3      -- Minimum search chars, integer.
		 max_chat_msg            512       -- Maximum chat message size, integer.
		 max_users               1000         -- Maximum number of online users, integer.
		 history_lines           50         -- Number of lines to keep in chat history.
		 opchat_name             OpChat       -- The Operator Chat Bot Nick.
		 opchat_desc             BoT       -- The Operator Chat Bot Description.
		 kick_time               300         -- The time to ban a user with a kick, in seconds.
		 msg_banned              Have a nice day and don't forget to smile !        -- The aditional message to show to banned users when connecting.
		 msg_full                Have a nice day and don't forget to smile !      -- Message to be shown to connecting users when hub full.
		 reg_only                0      -- 1 = registered only hub. 0 = otherwise.
		 nick_chars              ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890[]()-.,;'`~*&^%$#@!+=_|{}<>:         -- Chars that could be used for a nick, String.
		 chat_interval           500         -- Interval between chat lines, millis, Integer.
		 */
        // portfield.setText(Integer.toString(Vars.Default_Port));
        //Port [] curports=(Port [])Vars.activePorts.toArray();
        //int [] ports=new int [curports.length];
        //int I =0;
        // ports[I++]=x.portValue;
        /*DefaultListModel y=new DefaultListModel();
		
		for( Port x : Vars.activePorts)
		y.addElement( x.portValue);
		portlist.setModel(y);*/

        DefaultTableModel PortsModel = (DefaultTableModel) portlist.getModel();
        PortsModel.setRowCount(0);
        for (Port x : Vars.activePorts)
        {
            PortsModel.addRow(new Object[]{ x.portValue,
                                            x.getStatus() ? "LISTENING" : "DEAD",
                                            x.getStatus() ? "" : x.MSG });
        }

        hubhostfield.setText(Vars.Hub_Host);

        topicfield.setText(Vars.HubDE);

        fieldtimeout.setText(Integer.toString(Vars.Timeout_Login));

        namefield.setText(Vars.HubName);

        maxnifield.setText(Integer.toString(Vars.max_ni));

        minnifield.setText(Integer.toString(Vars.min_ni));

        maxdefield.setText(Integer.toString(Vars.max_de));

        maxsharefield.setText(Long.toString(Vars.max_share));

        minsharefield.setText(Long.toString(Vars.min_share));

        maxslfield.setText(Integer.toString(Vars.max_sl));

        minslfield.setText(Integer.toString(Vars.min_sl));

        maxemfield.setText(Integer.toString(Vars.max_em));

        maxhubsopfield.setText(Integer.toString(Vars.max_hubs_op));

        maxhubsregfield.setText(Integer.toString(Vars.max_hubs_reg));

        maxhubsuserfield.setText(Integer.toString(Vars.max_hubs_user));

        maxschcharsfield.setText(Integer.toString(Vars.max_sch_chars));

        minschcharsfield.setText(Integer.toString(Vars.min_sch_chars));

        maxchatmsgfield.setText(Integer.toString(Vars.max_chat_msg));

        maxusersfield.setText(Integer.toString(Vars.max_users));

        historylinesfield.setText(Integer.toString(Vars.history_lines));

        opchatnamefield.setText(Vars.Opchat_name);

        opchatdescfield.setText(Vars.Opchat_desc);

        kicktimefield.setText(Integer.toString(Vars.kick_time));

        msgbannedfield.setText(Vars.Msg_Banned);

        msgfullfield.setText(Vars.Msg_Full);

        if (Vars.reg_only == 1)
        {
            regonlycheck.setSelected(true);
        }
        else
        {
            regonlycheck.setSelected(false);
        }
        if (Vars.savelogs == 1)
        {
            savelogscheck.setSelected(true);
        }
        else
        {
            savelogscheck.setSelected(false);
        }

        nickcharsfield.setText(Vars.nick_chars);

        chatintervalfield.setText(Integer.toString(Vars.chat_interval));

        automagicsearchfield.setText(Integer.toString(Vars.automagic_search));
        searchlogbasefield.setText(Integer.toString(Vars.search_log_base));
        searchstepsfield.setText(Integer.toString(Vars.search_steps));
        msgsearchspamfield.setText(Vars.Msg_Search_Spam);
        searchspamresetfield.setText(Integer.toString(Vars.search_spam_reset));
        botnamefield.setText(Vars.bot_name);

        botdescfield.setText(Vars.bot_desc);

        redirecturl.setText(Vars.redirect_url);

        if (!Vars.Proxy_Host.equals(""))
        {
            proxycheck.setSelected(true);
            proxyhostfield.setText(Vars.Proxy_Host);
            proxyportfield.setText(Integer.toString(Vars.Proxy_Port));
            proxyportfield.setEditable(true);
            proxyhostfield.setEditable(true);
        }
        else
        {
            proxycheck.setSelected(false);
            proxyhostfield.setText(null);
            proxyportfield.setText(null);
            proxyportfield.setEditable(false);
            proxyhostfield.setEditable(false);
        }

        if (Vars.command_pm == 1)
        {
            command_pmcheck.setSelected(true);
        }
        else
        {
            command_pmcheck.setSelected(false);
        }

        if (Vars.BMSG == 1)
        {
            BMSGcheck.setSelected(true);
        }
        else
        {
            BMSGcheck.setSelected(false);
        }
        if (Vars.EMSG == 1)
        {
            EMSGcheck.setSelected(true);
        }
        else
        {
            EMSGcheck.setSelected(false);
        }
        if (Vars.DMSG == 1)
        {
            DMSGcheck.setSelected(true);
        }
        else
        {
            DMSGcheck.setSelected(false);
        }
        if (Vars.HMSG == 1)
        {
            HMSGcheck.setSelected(true);
        }
        else
        {
            HMSGcheck.setSelected(false);
        }
        if (Vars.FMSG == 1)
        {
            FMSGcheck.setSelected(true);
        }
        else
        {
            FMSGcheck.setSelected(false);
        }
        if (Vars.BSTA == 1)
        {
            BSTAcheck.setSelected(true);
        }
        else
        {
            BSTAcheck.setSelected(false);
        }
        if (Vars.ESTA == 1)
        {
            ESTAcheck.setSelected(true);
        }
        else
        {
            ESTAcheck.setSelected(false);
        }
        if (Vars.DSTA == 1)
        {
            DSTAcheck.setSelected(true);
        }
        else
        {
            DSTAcheck.setSelected(false);
        }
        if (Vars.HSTA == 1)
        {
            HSTAcheck.setSelected(true);
        }
        else
        {
            HSTAcheck.setSelected(false);
        }
        if (Vars.FSTA == 1)
        {
            FSTAcheck.setSelected(true);
        }
        else
        {
            FSTAcheck.setSelected(false);
        }

        if (Vars.BCTM == 1)
        {
            BCTMcheck.setSelected(true);
        }
        else
        {
            BCTMcheck.setSelected(false);
        }
        if (Vars.ECTM == 1)
        {
            ECTMcheck.setSelected(true);
        }
        else
        {
            ECTMcheck.setSelected(false);
        }
        if (Vars.DCTM == 1)
        {
            DCTMcheck.setSelected(true);
        }
        else
        {
            DCTMcheck.setSelected(false);
        }
        if (Vars.HCTM == 1)
        {
            HCTMcheck.setSelected(true);
        }
        else
        {
            HCTMcheck.setSelected(false);
        }
        if (Vars.FCTM == 1)
        {
            FCTMcheck.setSelected(true);
        }
        else
        {
            FCTMcheck.setSelected(false);
        }

        if (Vars.BRCM == 1)
        {
            BRCMcheck.setSelected(true);
        }
        else
        {
            BRCMcheck.setSelected(false);
        }
        if (Vars.ERCM == 1)
        {
            ERCMcheck.setSelected(true);
        }
        else
        {
            ERCMcheck.setSelected(false);
        }
        if (Vars.DRCM == 1)
        {
            DRCMcheck.setSelected(true);
        }
        else
        {
            DRCMcheck.setSelected(false);
        }
        if (Vars.HRCM == 1)
        {
            HRCMcheck.setSelected(true);
        }
        else
        {
            HRCMcheck.setSelected(false);
        }
        if (Vars.FRCM == 1)
        {
            FRCMcheck.setSelected(true);
        }
        else
        {
            FRCMcheck.setSelected(false);
        }

        if (Vars.BINF == 1)
        {
            BINFcheck.setSelected(true);
        }
        else
        {
            BINFcheck.setSelected(false);
        }
        if (Vars.EINF == 1)
        {
            EINFcheck.setSelected(true);
        }
        else
        {
            EINFcheck.setSelected(false);
        }
        if (Vars.DINF == 1)
        {
            DINFcheck.setSelected(true);
        }
        else
        {
            DINFcheck.setSelected(false);
        }
        if (Vars.HINF == 1)
        {
            HINFcheck.setSelected(true);
        }
        else
        {
            HINFcheck.setSelected(false);
        }
        if (Vars.FINF == 1)
        {
            FINFcheck.setSelected(true);
        }
        else
        {
            FINFcheck.setSelected(false);
        }

        if (Vars.BSCH == 1)
        {
            BSCHcheck.setSelected(true);
        }
        else
        {
            BSCHcheck.setSelected(false);
        }
        if (Vars.ESCH == 1)
        {
            ESCHcheck.setSelected(true);
        }
        else
        {
            ESCHcheck.setSelected(false);
        }
        if (Vars.DSCH == 1)
        {
            DSCHcheck.setSelected(true);
        }
        else
        {
            DSCHcheck.setSelected(false);
        }
        if (Vars.HSCH == 1)
        {
            HSCHcheck.setSelected(true);
        }
        else
        {
            HSCHcheck.setSelected(false);
        }
        if (Vars.FSCH == 1)
        {
            FSCHcheck.setSelected(true);
        }
        else
        {
            FSCHcheck.setSelected(false);
        }

        if (Vars.BRES == 1)
        {
            BREScheck.setSelected(true);
        }
        else
        {
            BREScheck.setSelected(false);
        }
        if (Vars.ERES == 1)
        {
            EREScheck.setSelected(true);
        }
        else
        {
            EREScheck.setSelected(false);
        }
        if (Vars.DRES == 1)
        {
            DREScheck.setSelected(true);
        }
        else
        {
            DREScheck.setSelected(false);
        }
        if (Vars.HRES == 1)
        {
            HREScheck.setSelected(true);
        }
        else
        {
            HREScheck.setSelected(false);
        }
        if (Vars.FRES == 1)
        {
            FREScheck.setSelected(true);
        }
        else
        {
            FREScheck.setSelected(false);
        }

        if (Vars.BPAS == 1)
        {
            BPAScheck.setSelected(true);
        }
        else
        {
            BPAScheck.setSelected(false);
        }
        if (Vars.EPAS == 1)
        {
            EPAScheck.setSelected(true);
        }
        else
        {
            EPAScheck.setSelected(false);
        }
        if (Vars.DPAS == 1)
        {
            DPAScheck.setSelected(true);
        }
        else
        {
            DPAScheck.setSelected(false);
        }
        if (Vars.HPAS == 1)
        {
            HPAScheck.setSelected(true);
        }
        else
        {
            HPAScheck.setSelected(false);
        }
        if (Vars.FPAS == 1)
        {
            FPAScheck.setSelected(true);
        }
        else
        {
            FPAScheck.setSelected(false);
        }

        if (Vars.BSUP == 1)
        {
            BSUPcheck.setSelected(true);
        }
        else
        {
            BSUPcheck.setSelected(false);
        }
        if (Vars.ESUP == 1)
        {
            ESUPcheck.setSelected(true);
        }
        else
        {
            ESUPcheck.setSelected(false);
        }
        if (Vars.DSUP == 1)
        {
            DSUPcheck.setSelected(true);
        }
        else
        {
            DSUPcheck.setSelected(false);
        }
        if (Vars.HSUP == 1)
        {
            HSUPcheck.setSelected(true);
        }
        else
        {
            HSUPcheck.setSelected(false);
        }
        if (Vars.FSUP == 1)
        {
            FSUPcheck.setSelected(true);
        }
        else
        {
            FSUPcheck.setSelected(false);
        }

        initialised = true;

    }


    private void jPanel6MouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_jPanel6MouseClicked
    {//GEN-HEADEREND:event_jPanel6MouseClicked
        // TODO add your handling code here:

    }//GEN-LAST:event_jPanel6MouseClicked


    private void jPanel6FocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jPanel6FocusLost
    {//GEN-HEADEREND:event_jPanel6FocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_jPanel6FocusLost


    private void jPanel6FocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_jPanel6FocusGained
    {//GEN-HEADEREND:event_jPanel6FocusGained

    }//GEN-LAST:event_jPanel6FocusGained


    public void resizeBanTable(int ty, int r, int o, int t, int n, int i,
                               int c, int re)
    {
        // how many parts represents each column
        // preffered call : 
        int suma = ty + r + o + t + n + i + c + re;
        DefaultTableModel BanModel = (DefaultTableModel) BanTable.getModel();
        BanTable.setAutoResizeMode(BanTable.AUTO_RESIZE_OFF);
        BanTable.getColumnModel().getColumn(0).setPreferredWidth(
                BanTable.getWidth() * ty / suma);
        BanTable.getColumnModel().getColumn(1).setPreferredWidth(
                BanTable.getWidth() * r / suma);
        BanTable.getColumnModel().getColumn(2).setPreferredWidth(
                BanTable.getWidth() * o / suma);
        BanTable.getColumnModel().getColumn(3).setPreferredWidth(
                BanTable.getWidth() * t / suma);
        BanTable.getColumnModel().getColumn(4).setPreferredWidth(
                BanTable.getWidth() * n / suma);
        BanTable.getColumnModel().getColumn(5).setPreferredWidth(
                BanTable.getWidth() * i / suma);
        BanTable.getColumnModel().getColumn(6).setPreferredWidth(
                BanTable.getWidth() * c / suma);
        BanTable.getColumnModel().getColumn(7).setPreferredWidth(
                BanTable.getWidth() * re / suma);

    }


    public void insertBans()
    {
        Ban n = BanList.First;
        int bancount = 0;
        while (n != null)
        {
            bancount++;
            n = n.Next;
        }
        /** 0 -- no ban
         * 1 -- nick ban
         * 2 -- ip ban
         * 3 -- cid ban
         */
        DefaultTableModel BanModel = (DefaultTableModel) BanTable.getModel();
        if (bancount != BanModel.getRowCount())
        {
            BanModel.setRowCount(0);
            n = BanList.First;
            while (n != null)
            {

                Date d = new Date(n.timeofban);

                String type;
                switch (n.bantype)
                {
                    case 1:
                        type = "Nick";
                        break;
                    case 2:
                        type = "IP";
                        break;
                    default:
                        type = "CID";
                }

                BanModel.addRow(new Object[]{ type,
                                              ADC.retNormStr(n.banreason), n.banop, d.toString(),
                                              n.nick, n.ip, n.cid, n.getTimeLeft() });
                n = n.Next;
            }
        }
    }


    private void formWindowGainedFocus(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowGainedFocus
    {//GEN-HEADEREND:event_formWindowGainedFocus
        refreshAll();

    }//GEN-LAST:event_formWindowGainedFocus


    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton4ActionPerformed
    {//GEN-HEADEREND:event_jButton4ActionPerformed
        //clicked reg...
        Main.Reg(jTextField1.getText());
        ((DefaultTableModel) AccountTable.getModel()).setRowCount(0);
        Nod n = AccountsConfig.First;
        while (n != null)
        {
            String blah00 = "";
            Date d = new Date(n.CreatedOn);
            if (n.LastNI != null)
            {
                blah00 = n.LastNI;
            }
            else
            {
                blah00 = "Never seen online.";
            }

            ((DefaultTableModel) AccountTable.getModel()).addRow(new Object[]{
                    n.CID, blah00, n.LastIP, n.WhoRegged, d.toString() });
            n = n.Next;
        }
        Main.Server.rewriteregs();
    }//GEN-LAST:event_jButton4ActionPerformed


    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton3ActionPerformed
    {//GEN-HEADEREND:event_jButton3ActionPerformed
        SetStatus("Restarting... Press OK and wait a few seconds....");
        Main.Restart();

    }//GEN-LAST:event_jButton3ActionPerformed


    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton2ActionPerformed
    {//GEN-HEADEREND:event_jButton2ActionPerformed
        this.setVisible(false);
        this.dispose();
        System.gc();
        Main.GUIshowing = false;
    }//GEN-LAST:event_jButton2ActionPerformed


    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
    {//GEN-HEADEREND:event_jButton1ActionPerformed
        Main.Exit();
    }//GEN-LAST:event_jButton1ActionPerformed


    private void jButton30ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton30ActionPerformed
    {//GEN-HEADEREND:event_jButton30ActionPerformed

        Modulator.findModules();
        this.refreshGUIPlugs();
        String foundPlugs = "";
        for (Module myPlug : Modulator.myModules)
        {
            foundPlugs += "\n" + myPlug.getName();
        }
        if (foundPlugs.equals(""))
        {
            foundPlugs = "No plugin found.";
        }
        else
        {
            foundPlugs = "Found following plugins:" + foundPlugs;
        }
        JOptionPane.showMessageDialog(this, foundPlugs, Vars.HubName,
                                      JOptionPane.INFORMATION_MESSAGE);

    }//GEN-LAST:event_jButton30ActionPerformed


    private void searchcheckActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_searchcheckActionPerformed
    {//GEN-HEADEREND:event_searchcheckActionPerformed
        if (searchcheck.isSelected())
        {
            long prop = getClientPr() + getWordPr();
            if (privatecheck.isSelected())
            {
                prop += BannedWord.privatechat;
            }
            if (notifycheck.isSelected())
            {
                prop += BannedWord.notify;
            }
            prop += BannedWord.searches;
            listaBanate.modifyMultiPrAt(jList1.getSelectedIndices(), prop);
        }
        else
        {
            long prop = getClientPr() + getWordPr();
            if (privatecheck.isSelected())
            {
                prop += BannedWord.privatechat;
            }
            if (notifycheck.isSelected())
            {
                prop += BannedWord.notify;
            }

            listaBanate.modifyMultiPrAt(jList1.getSelectedIndices(), prop);
        }
    }//GEN-LAST:event_searchcheckActionPerformed


    private void jButton31ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton31ActionPerformed
    {//GEN-HEADEREND:event_jButton31ActionPerformed
        String Thing, aucsy;

        Thing = msgbannedfield.getText();

        aucsy = Vars.Msg_Banned;
        if (!(aucsy.equals(Thing)))
        {

            Vars.Msg_Banned = Thing;
            Main.PopMsg("Msg_Banned changed from \"" + aucsy + "\" to \""
                        + Thing + "\".");

        }
        Thing = msgfullfield.getText();

        aucsy = Vars.Msg_Full;
        if (!(aucsy.equals(Thing)))
        {

            Vars.Msg_Full = Thing;
            Main.PopMsg("Msg_Full changed from \"" + aucsy + "\" to \"" + Thing
                        + "\".");

        }

        SetStatus("Miscellaneous settings saved.");
    }//GEN-LAST:event_jButton31ActionPerformed


    private void portlistKeyTyped(java.awt.event.KeyEvent evt)//GEN-FIRST:event_portlistKeyTyped
    {//GEN-HEADEREND:event_portlistKeyTyped

    }//GEN-LAST:event_portlistKeyTyped


    private void remportActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_remportActionPerformed
    {//GEN-HEADEREND:event_remportActionPerformed


        //  int row=AccountTable.getEditingRow ();
        try
        {
            int row = portlist.getSelectedRow();
            int curport = (Integer) portlist.getModel().getValueAt(row, 0);
            Port elim = null;
            for (Port newport : Vars.activePorts)
            {
                if (newport.portValue == curport)
                {
                    elim = newport;

                }
            }
            if (elim == null)
            {
                return;
            }
            Vars.activePorts.remove(elim);

            Main.Server.delPort(elim);

            SetStatus("Removed port " + elim.portValue);
            if (curport == Integer.parseInt(Vars.Hub_Host
                                                    .substring(Vars.Hub_Host.indexOf(':') + 1)))
            {
                if (!Vars.activePorts.isEmpty())
                {
                    for (Port cur : Vars.activePorts)
                    {
                        if (cur.getStatus())
                        {
                            Vars.Hub_Host = Vars.Hub_Host.replace(
                                    ":" + curport, ":"
                                                   + String.valueOf(cur.portValue));
                        }
                    }
                }
            }
            refreshAll();
        }
        catch (ArrayIndexOutOfBoundsException aioobe)
        {

        }

    }//GEN-LAST:event_remportActionPerformed


    private void portlistKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_portlistKeyPressed
    {//GEN-HEADEREND:event_portlistKeyPressed
        if (evt.getKeyCode() == evt.VK_DELETE)
        // SetStatus("gay");
        {

            remportActionPerformed(null);

        }
    }//GEN-LAST:event_portlistKeyPressed


    private void addnewportActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addnewportActionPerformed
    {//GEN-HEADEREND:event_addnewportActionPerformed
        //int row=portlist.getSelectedRow();
        //int curport=(Integer)portlist.getModel().getValueAt(row,0);
        String newp = JOptionPane.showInputDialog("Enter Port Value");
        if (newp == null)
        {
            return;
        }
        try
        {
            int curport = Integer.parseInt(newp);
            Port newport = new Port(curport);

            if (Main.Server.addPort(newport) == true)
            //if(Main.Server.addPort(newport)==true)

            //    SetStatus("Adding successful. Server now listening also on "+newport.portValue);
            // else
            //        SetStatus("Adding failed. Reason: "+newport.MSG);
            {
                if (Vars.activePorts.isEmpty()
                    || !Vars.getHostPort().getStatus())
                {
                    int x = Vars.Hub_Host.indexOf(':');
                    if (x == -1 || x > Vars.Hub_Host.length() - 1)
                    {
                        return;
                    }
                    Vars.Hub_Host = Vars.Hub_Host.substring(0, x) + ":"
                                    + curport;
                }
            }
            Vars.activePorts.add(newport);
            refreshAll();
        }
        catch (NumberFormatException nfe)
        {
            SetStatus("Invalid port number", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_addnewportActionPerformed


    private void jButton32ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton32ActionPerformed
    {//GEN-HEADEREND:event_jButton32ActionPerformed
        Main.pManager.rescanScripts();
        refreshPyScripts();
        SetStatus("Python scripts refreshed.");

    }//GEN-LAST:event_jButton32ActionPerformed


    private void PyTableMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_PyTableMouseClicked
    {//GEN-HEADEREND:event_PyTableMouseClicked
        // TODO add your handling code here:
        DefaultTableModel PyModel = (DefaultTableModel) PyTable.getModel();
        int row = PyTable.getSelectedRow();
        if (PyTable.getSelectedColumn() != 1)
        {
            return;
        }
        String name = (String) PyModel.getValueAt(row, 0);
        for (PythonScript pyS : PythonManager.scripts)
        {
            if (pyS.getScriptName().equals(name))
            {
                pyS.setActive(pyS.isActive() ? false : true);
            }
        }
        refreshPyScripts();
    }//GEN-LAST:event_PyTableMouseClicked


    private void langcomboActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_langcomboActionPerformed
    {//GEN-HEADEREND:event_langcomboActionPerformed
        if (!initialised)
        {
            return;
        }
        if (langcombo.getSelectedItem().equals("en_US"))
        {
            Translation.curLocale = new Locale("en", "US");

        }
        if (langcombo.getSelectedItem().equals("ro_RO"))
        {
            Translation.curLocale = new Locale("ro", "RO");

        }
        if (langcombo.getSelectedItem().equals("sv_SE"))
        {
            Translation.curLocale = new Locale("sv", "SE");

        }
        if (langcombo.getSelectedItem().equals("it_IT"))
        {
            Translation.curLocale = new Locale("it", "IT");

        }
        if (langcombo.getSelectedItem().equals("de_DE"))
        {
            Translation.curLocale = new Locale("de", "DE");

        }
        if (langcombo.getSelectedItem().equals("nl_NL"))
        {
            Translation.curLocale = new Locale("nl", "NL");

        }
        if (langcombo.getSelectedItem().equals("ru_RU"))
        {
            Translation.curLocale = new Locale("ru", "RU");

        }
        Locale.setDefault(Translation.curLocale);
        Main.PopMsg("Language changed to "
                    + langcombo.getSelectedItem().toString());
        SetStatus("Language changed to "
                  + langcombo.getSelectedItem().toString());
        Vars.lang = langcombo.getSelectedItem().toString();
        Main.Server.rewriteconfig();
        try
        {
            Translation.Strings = ResourceBundle.getBundle("Translation",
                                                           Translation.curLocale);

        }
        catch (java.util.MissingResourceException mre)
        {
            //System.out.println("Fatal Error : Unable to locate Translation.properties file or any other translation. FAIL.");
            // System.exit(1);
            mre.printStackTrace();
        }

        refreshAll();
    }//GEN-LAST:event_langcomboActionPerformed


    private void langcomboPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_langcomboPropertyChange
    {//GEN-HEADEREND:event_langcomboPropertyChange

    }//GEN-LAST:event_langcomboPropertyChange


    private void langcomboItemStateChanged(java.awt.event.ItemEvent evt)//GEN-FIRST:event_langcomboItemStateChanged
    {//GEN-HEADEREND:event_langcomboItemStateChanged

    }//GEN-LAST:event_langcomboItemStateChanged


    private void langcomboMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_langcomboMouseClicked
    {//GEN-HEADEREND:event_langcomboMouseClicked
        // System.out.println("test");
    }//GEN-LAST:event_langcomboMouseClicked


    private void command_pmcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_command_pmcheckActionPerformed
// TODO add your handling code here:
        if (command_pmcheck.isSelected())
        {
            Vars.command_pm = 1;
            Main.PopMsg("Command_PM changed from \"0\" to \"1\"");
        }
        else
        {
            Vars.command_pm = 0;
            Main.PopMsg("Command_PM changed from \"1\" to \"0\"");
        }
    }//GEN-LAST:event_command_pmcheckActionPerformed


    private void adcslabelMouseClicked(java.awt.event.MouseEvent evt)
    {//GEN-FIRST:event_adcslabelMouseClicked
// TODO add your handling code here:


        JOptionPane.showMessageDialog(null,
                                      "ADCS is a standard ADC extension that enables running the normal ADC\n" +
                                      "protocol over the Secure Socket Layer / Transport Layer Security (SSL/TLS).\n" +
                                      "All messages between the hub and the clients are send encrypted\n" +
                                      "but the ADC commands remain the same.\n" +
                                      "Client to client connections are also encrypted if the\n" +
                                      "clients support it.\n" +
                                      "You can setup your certificate/keys on the ADCS tab.",
                                      "ADC Secure (ADCS)",
                                      JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_adcslabelMouseClicked


    private void genbuttonActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_genbuttonActionPerformed
// TODO add your handling code here:
        int x = JOptionPane.showConfirmDialog(null,
                                              "This will remove existing keys and certificates and will generate new random keys.\n" +
                                              "Any users having certificates signed by the defunct keys will be rendered useless.\n" +
                                              " Are you sure you want to regenerate ?\n" +
                                              "( The operation might take a while )",
                                              Vars.HubName,
                                              JOptionPane.OK_CANCEL_OPTION,
                                              JOptionPane.WARNING_MESSAGE);
        if (x == JOptionPane.OK_OPTION)
        {

            boolean keygenerated = Main.Server.sslmanager.getCertManager().recreateKeysCerts();
            // JOptionPane.showConfirmDialog(null,"New pair of keys and certificate were created and saved into key.crt",
            //        Vars.HubName,JOptionPane.OK_OPTION,JOptionPane.INFORMATION_MESSAGE);
            if (keygenerated)
            {
                this.SetStatus(
                        "New pair of keys and certificate were created and saved into key.crt");
                Main.Server.adcs_ok = true;
                if (!Vars.adcs_mode)
                {
                    getEnableadcs().setEnabled(true);
                }
            }
            else
            {
                JOptionPane.showMessageDialog(null,
                                              "Error creating keys and certificates. Check the log for details.",
                                              "Error",
                                              JOptionPane.INFORMATION_MESSAGE);
            }

        }
    }//GEN-LAST:event_genbuttonActionPerformed


    private void usecertificatescheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_usecertificatescheckActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_usecertificatescheckActionPerformed


    private void enableadcsActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_enableadcsActionPerformed
// TODO add your handling code here:
        int x = JOptionPane.showConfirmDialog(null,
                                              "This will enable ADC Secure mode. This means that your hub address will change\n" +
                                              "from adc://" +
                                              Vars.Hub_Host +
                                              " to adcs://" +
                                              Vars.Hub_Host +
                                              " so all users connecting on old address\n" +
                                              "will not be able to login. Also, your hub will immediately restart to apply.\n" +
                                              "Are you sure you want to proceed ? ( The operation is reversible at any time )",
                                              Vars.HubName,
                                              JOptionPane.OK_CANCEL_OPTION,
                                              JOptionPane.WARNING_MESSAGE);
        if (x == JOptionPane.OK_OPTION)
        {
            HubServer.done_adcs = false;
            if (!Main.Server.adcs_ok)
            {
                return;//cannot start adcs mode.. bug ?
            }
            Vars.adcs_mode = true;
            Main.Restart();
            Main.PopMsg("ADC Secure mode has been enabled");
        }
    }//GEN-LAST:event_enableadcsActionPerformed


    private void jButton33ActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_jButton33ActionPerformed
// TODO add your handling code here:
        JOptionPane.showMessageDialog(null, "Product version: " + Vars.HubVersion + " RC1.\n" +
                                            "Copyright (c) 2007-2008 by Eugen Hristev.\n" +
                                            "Special Thanks go to : MAGY, Spader, Toast, Naccio, Catalaur, Ciprian Dobre.\n" +
                                            "Also thanks go to everybody who helped me with code, ideas or just moral support,\n" +
                                            "also to everybody using my software and all testers for a good debugging ;)\n" +
                                            "Finally, many thanks to all the people who helped in translating, \n" +
                                            "and not to forget, all the good people working on MINA, Jython or Bouncy Castle.\n",
                                      "Death Squad Hub. The credits",
                                      JOptionPane.INFORMATION_MESSAGE, myIco);
    }//GEN-LAST:event_jButton33ActionPerformed


    private void jButton34ActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_jButton34ActionPerformed
// TODO add your handling code here:
        JOptionPane.showMessageDialog(null,
                                      "This program is distributed in the hope that it will be useful,\n" +
                                      "but WITHOUT ANY WARRANTY; without even the implied warranty of \n" +
                                      "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See\n" +
                                      "the GNU General Public License for more details. \n" +
                                      "This program uses the MINA library http://mina.apache.org licensed\n" +
                                      "under the Apache Public License.\n" +
                                      "This program uses the Jython library http://jython.sf.net licensed\n" +
                                      "under Python Software Foundation License 2\n" +
                                      "This program uses the Bouncy Castle library http://www.bouncycastle.org",
                                      "Death Squad Hub. License",
                                      JOptionPane.INFORMATION_MESSAGE,
                                      myIco);
    }//GEN-LAST:event_jButton34ActionPerformed


    private void jButton35ActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_jButton35ActionPerformed
// TODO add your handling code her
        deleteSelectedReg();
    }//GEN-LAST:event_jButton35ActionPerformed


    private void disableadcsActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_disableadcsActionPerformed
// TODO add your handling code here:
        int x = JOptionPane.showConfirmDialog(null,
                                              "This will disable ADC Secure mode. This means that your hub address will change\n" +
                                              "from adcs://" +
                                              Vars.Hub_Host +
                                              " to adc://" +
                                              Vars.Hub_Host +
                                              " so all users connecting on old address\n" +
                                              "will not be able to login. Your keys and certificates will remain intact so you\n"
                                              +
                                              "can use ADCS in the future with them. Also, your hub will immediately restart to apply.\n" +
                                              "Are you sure you want to proceed ? ( The operation is reversible at any time )",
                                              Vars.HubName,
                                              JOptionPane.OK_CANCEL_OPTION,
                                              JOptionPane.WARNING_MESSAGE);
        if (x == JOptionPane.OK_OPTION)
        {
            HubServer.done_adcs = false;
            Vars.adcs_mode = false;
            Main.Restart();
            Main.PopMsg("ADC Secure mode has been disabled");
        }
    }//GEN-LAST:event_disableadcsActionPerformed


    private void BCTMcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_BCTMcheckActionPerformed
// TODO add your handling code here:
        if (BCTMcheck.isSelected())

        {
            Main.PopMsg("BCTM changed from \"0\" to \"1\".");
            Vars.BCTM = 1;
        }
        else
        {
            Main.PopMsg("BCTM changed from \"1\" to \"0\".");
            Vars.BCTM = 0;
        }
    }//GEN-LAST:event_BCTMcheckActionPerformed


    private void DCTMcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_DCTMcheckActionPerformed
// TODO add your handling code here:
        if (DCTMcheck.isSelected())

        {
            Main.PopMsg("DCTM changed from \"0\" to \"1\".");
            Vars.DCTM = 1;
        }
        else
        {
            Main.PopMsg("DCTM changed from \"1\" to \"0\".");
            Vars.DCTM = 0;
        }
    }//GEN-LAST:event_DCTMcheckActionPerformed


    private void ECTMcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_ECTMcheckActionPerformed
// TODO add your handling code here:
        if (ECTMcheck.isSelected())

        {
            Main.PopMsg("ECTM changed from \"0\" to \"1\".");
            Vars.ECTM = 1;
        }
        else
        {
            Main.PopMsg("BCTM changed from \"1\" to \"0\".");
            Vars.ECTM = 0;
        }
    }//GEN-LAST:event_ECTMcheckActionPerformed


    private void FCTMcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_FCTMcheckActionPerformed

        if (FCTMcheck.isSelected())

        {
            Main.PopMsg("FCTM changed from \"0\" to \"1\".");
            Vars.FCTM = 1;
        }
        else
        {
            Main.PopMsg("FCTM changed from \"1\" to \"0\".");
            Vars.FCTM = 0;
        }
    }//GEN-LAST:event_FCTMcheckActionPerformed


    private void HCTMcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_HCTMcheckActionPerformed
// TODO add your handling code here:
        if (HCTMcheck.isSelected())

        {
            Main.PopMsg("HCTM changed from \"0\" to \"1\".");
            Vars.HCTM = 1;
        }
        else
        {
            Main.PopMsg("HCTM changed from \"1\" to \"0\".");
            Vars.HCTM = 0;
        }
    }//GEN-LAST:event_HCTMcheckActionPerformed


    private void BRCMcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_BRCMcheckActionPerformed
// TODO add your handling code here:
        if (BRCMcheck.isSelected())

        {
            Main.PopMsg("BRCM changed from \"0\" to \"1\".");
            Vars.BRCM = 1;
        }
        else
        {
            Main.PopMsg("BRCM changed from \"1\" to \"0\".");
            Vars.BRCM = 0;
        }
    }//GEN-LAST:event_BRCMcheckActionPerformed


    private void DRCMcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_DRCMcheckActionPerformed
// TODO add your handling code here:
        if (DRCMcheck.isSelected())

        {
            Main.PopMsg("DRCM changed from \"0\" to \"1\".");
            Vars.DRCM = 1;
        }
        else
        {
            Main.PopMsg("DRCM changed from \"1\" to \"0\".");
            Vars.DRCM = 0;
        }
    }//GEN-LAST:event_DRCMcheckActionPerformed


    private void ERCMcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_ERCMcheckActionPerformed
// TODO add your handling code here:
        if (ERCMcheck.isSelected())

        {
            Main.PopMsg("ERCM changed from \"0\" to \"1\".");
            Vars.ERCM = 1;
        }
        else
        {
            Main.PopMsg("ERCM changed from \"1\" to \"0\".");
            Vars.ERCM = 0;
        }
    }//GEN-LAST:event_ERCMcheckActionPerformed


    private void FRCMcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_FRCMcheckActionPerformed
// TODO add your handling code here:
        if (FRCMcheck.isSelected())

        {
            Main.PopMsg("FRCM changed from \"0\" to \"1\".");
            Vars.FRCM = 1;
        }
        else
        {
            Main.PopMsg("FRCM changed from \"1\" to \"0\".");
            Vars.FRCM = 0;
        }
    }//GEN-LAST:event_FRCMcheckActionPerformed


    private void HRCMcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_HRCMcheckActionPerformed
// TODO add your handling code here:
        if (HRCMcheck.isSelected())

        {
            Main.PopMsg("HRCM changed from \"0\" to \"1\".");
            Vars.HRCM = 1;
        }
        else
        {
            Main.PopMsg("HRCM changed from \"1\" to \"0\".");
            Vars.HRCM = 0;
        }
    }//GEN-LAST:event_HRCMcheckActionPerformed


    private void BSCHcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_BSCHcheckActionPerformed
// TODO add your handling code here:
        if (BSCHcheck.isSelected())

        {
            Main.PopMsg("BSCH changed from \"0\" to \"1\".");
            Vars.BSCH = 1;
        }
        else
        {
            Main.PopMsg("BSCH changed from \"1\" to \"0\".");
            Vars.BSCH = 0;
        }
    }//GEN-LAST:event_BSCHcheckActionPerformed


    private void DSCHcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_DSCHcheckActionPerformed
// TODO add your handling code here:
        if (DSCHcheck.isSelected())

        {
            Main.PopMsg("DSCH changed from \"0\" to \"1\".");
            Vars.DSCH = 1;
        }
        else
        {
            Main.PopMsg("DSCH changed from \"1\" to \"0\".");
            Vars.DSCH = 0;
        }
    }//GEN-LAST:event_DSCHcheckActionPerformed


    private void ESCHcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_ESCHcheckActionPerformed
// TODO add your handling code here:
        if (ESCHcheck.isSelected())

        {
            Main.PopMsg("ESCH changed from \"0\" to \"1\".");
            Vars.ESCH = 1;
        }
        else
        {
            Main.PopMsg("ESCH changed from \"1\" to \"0\".");
            Vars.ESCH = 0;
        }
    }//GEN-LAST:event_ESCHcheckActionPerformed


    private void FSCHcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_FSCHcheckActionPerformed
// TODO add your handling code here:
        if (FSCHcheck.isSelected())

        {
            Main.PopMsg("FSCH changed from \"0\" to \"1\".");
            Vars.FSCH = 1;
        }
        else
        {
            Main.PopMsg("FSCH changed from \"1\" to \"0\".");
            Vars.FSCH = 0;
        }
    }//GEN-LAST:event_FSCHcheckActionPerformed


    private void HSCHcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_HSCHcheckActionPerformed
// TODO add your handling code here:
        if (HSCHcheck.isSelected())

        {
            Main.PopMsg("HSCH changed from \"0\" to \"1\".");
            Vars.HSCH = 1;
        }
        else
        {
            Main.PopMsg("HSCH changed from \"1\" to \"0\".");
            Vars.HSCH = 0;
        }
    }//GEN-LAST:event_HSCHcheckActionPerformed


    private void BREScheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_BREScheckActionPerformed
// TODO add your handling code here:
        if (BREScheck.isSelected())

        {
            Main.PopMsg("BRES changed from \"0\" to \"1\".");
            Vars.BRES = 1;
        }
        else
        {
            Main.PopMsg("BRES changed from \"1\" to \"0\".");
            Vars.BRES = 0;
        }
    }//GEN-LAST:event_BREScheckActionPerformed


    private void DREScheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_DREScheckActionPerformed
// TODO add your handling code here:
        if (DREScheck.isSelected())

        {
            Main.PopMsg("DRES changed from \"0\" to \"1\".");
            Vars.DRES = 1;
        }
        else
        {
            Main.PopMsg("DRES changed from \"1\" to \"0\".");
            Vars.DRES = 0;
        }
    }//GEN-LAST:event_DREScheckActionPerformed


    private void EREScheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_EREScheckActionPerformed
// TODO add your handling code here:
        if (EREScheck.isSelected())

        {
            Main.PopMsg("ERES changed from \"0\" to \"1\".");
            Vars.ERES = 1;
        }
        else
        {
            Main.PopMsg("ERES changed from \"1\" to \"0\".");
            Vars.ERES = 0;
        }
    }//GEN-LAST:event_EREScheckActionPerformed


    private void FREScheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_FREScheckActionPerformed
// TODO add your handling code here:
        if (FREScheck.isSelected())

        {
            Main.PopMsg("FRES changed from \"0\" to \"1\".");
            Vars.FRES = 1;
        }
        else
        {
            Main.PopMsg("FRES changed from \"1\" to \"0\".");
            Vars.FRES = 0;
        }
    }//GEN-LAST:event_FREScheckActionPerformed


    private void HREScheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_HREScheckActionPerformed
// TODO add your handling code here:
        if (HREScheck.isSelected())

        {
            Main.PopMsg("HRES changed from \"0\" to \"1\".");
            Vars.HRES = 1;
        }
        else
        {
            Main.PopMsg("HRES changed from \"1\" to \"0\".");
            Vars.HRES = 0;
        }
    }//GEN-LAST:event_HREScheckActionPerformed


    private void BPAScheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_BPAScheckActionPerformed
// TODO add your handling code here:
        if (BPAScheck.isSelected())

        {
            Main.PopMsg("BPAS changed from \"0\" to \"1\".");
            Vars.BPAS = 1;
        }
        else
        {
            Main.PopMsg("BPAS changed from \"1\" to \"0\".");
            Vars.BPAS = 0;
        }
    }//GEN-LAST:event_BPAScheckActionPerformed


    private void DPAScheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_DPAScheckActionPerformed
// TODO add your handling code here:

        if (DPAScheck.isSelected())

        {
            Main.PopMsg("DPAS changed from \"0\" to \"1\".");
            Vars.DPAS = 1;
        }
        else
        {
            Main.PopMsg("DPAS changed from \"1\" to \"0\".");
            Vars.DPAS = 0;
        }
    }//GEN-LAST:event_DPAScheckActionPerformed


    private void EPAScheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_EPAScheckActionPerformed
// TODO add your handling code here:
        if (EPAScheck.isSelected())

        {
            Main.PopMsg("EPAS changed from \"0\" to \"1\".");
            Vars.EPAS = 1;
        }
        else
        {
            Main.PopMsg("EPAS changed from \"1\" to \"0\".");
            Vars.EPAS = 0;
        }
    }//GEN-LAST:event_EPAScheckActionPerformed


    private void FPAScheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_FPAScheckActionPerformed
// TODO add your handling code here:

        if (FPAScheck.isSelected())

        {
            Main.PopMsg("FPAS changed from \"0\" to \"1\".");
            Vars.FPAS = 1;
        }
        else
        {
            Main.PopMsg("FPAS changed from \"1\" to \"0\".");
            Vars.FPAS = 0;
        }
    }//GEN-LAST:event_FPAScheckActionPerformed


    private void HPAScheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_HPAScheckActionPerformed
// TODO add your handling code here:
        if (HPAScheck.isSelected())

        {
            Main.PopMsg("HPAS changed from \"0\" to \"1\".");
            Vars.HPAS = 1;
        }
        else
        {
            Main.PopMsg("HPAS changed from \"1\" to \"0\".");
            Vars.HPAS = 0;
        }
    }//GEN-LAST:event_HPAScheckActionPerformed


    private void BSUPcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_BSUPcheckActionPerformed
// TODO add your handling code here:
        if (BSUPcheck.isSelected())

        {
            Main.PopMsg("BSUP changed from \"0\" to \"1\".");
            Vars.BSUP = 1;
        }
        else
        {
            Main.PopMsg("BSUP changed from \"1\" to \"0\".");
            Vars.BSUP = 0;
        }
    }//GEN-LAST:event_BSUPcheckActionPerformed


    private void DSUPcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_DSUPcheckActionPerformed
        if (DSUPcheck.isSelected())

        {
            Main.PopMsg("DSUP changed from \"0\" to \"1\".");
            Vars.DSUP = 1;
        }
        else
        {
            Main.PopMsg("DSUP changed from \"1\" to \"0\".");
            Vars.DSUP = 0;
        }

        // TODO add your handling code here:
    }//GEN-LAST:event_DSUPcheckActionPerformed


    private void ESUPcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_ESUPcheckActionPerformed
// TODO add your handling code here:

        if (ESUPcheck.isSelected())

        {
            Main.PopMsg("ESUP changed from \"0\" to \"1\".");
            Vars.ESUP = 1;
        }
        else
        {
            Main.PopMsg("ESUP changed from \"1\" to \"0\".");
            Vars.ESUP = 0;
        }
    }//GEN-LAST:event_ESUPcheckActionPerformed


    private void FSUPcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_FSUPcheckActionPerformed
// TODO add your handling code here:
        if (FSUPcheck.isSelected())

        {
            Main.PopMsg("FSUP changed from \"0\" to \"1\".");
            Vars.FSUP = 1;
        }
        else
        {
            Main.PopMsg("FSUP changed from \"1\" to \"0\".");
            Vars.FSUP = 0;
        }
    }//GEN-LAST:event_FSUPcheckActionPerformed


    private void HSUPcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_HSUPcheckActionPerformed
// TODO add your handling code here:

        if (HSUPcheck.isSelected())

        {
            Main.PopMsg("HSUP changed from \"0\" to \"1\".");
            Vars.HSUP = 1;
        }
        else
        {
            Main.PopMsg("HSUP changed from \"1\" to \"0\".");
            Vars.HSUP = 0;
        }
    }//GEN-LAST:event_HSUPcheckActionPerformed


    private void BINFcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_BINFcheckActionPerformed
// TODO add your handling code here:
        if (BINFcheck.isSelected())

        {
            Main.PopMsg("BINF changed from \"0\" to \"1\".");
            Vars.BINF = 1;
        }
        else
        {
            Main.PopMsg("BINF changed from \"1\" to \"0\".");
            Vars.BINF = 0;
        }
    }//GEN-LAST:event_BINFcheckActionPerformed


    private void DINFcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_DINFcheckActionPerformed
// TODO add your handling code here:
        if (DINFcheck.isSelected())

        {
            Main.PopMsg("DINF changed from \"0\" to \"1\".");
            Vars.DINF = 1;
        }
        else
        {
            Main.PopMsg("DINF changed from \"1\" to \"0\".");
            Vars.DINF = 0;
        }
    }//GEN-LAST:event_DINFcheckActionPerformed


    private void EINFcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_EINFcheckActionPerformed
// TODO add your handling code here:
        if (EINFcheck.isSelected())

        {
            Main.PopMsg("EINF changed from \"0\" to \"1\".");
            Vars.EINF = 1;
        }
        else
        {
            Main.PopMsg("EINF changed from \"1\" to \"0\".");
            Vars.EINF = 0;
        }
    }//GEN-LAST:event_EINFcheckActionPerformed


    private void FINFcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_FINFcheckActionPerformed
// TODO add your handling code here:
        if (FINFcheck.isSelected())

        {
            Main.PopMsg("FINF changed from \"0\" to \"1\".");
            Vars.FINF = 1;
        }
        else
        {
            Main.PopMsg("FINF changed from \"1\" to \"0\".");
            Vars.FINF = 0;
        }
    }//GEN-LAST:event_FINFcheckActionPerformed


    private void HINFcheckActionPerformed(java.awt.event.ActionEvent evt)
    {//GEN-FIRST:event_HINFcheckActionPerformed
// TODO add your handling code here:
        if (HINFcheck.isSelected())

        {
            Main.PopMsg("HINF changed from \"0\" to \"1\".");
            Vars.HINF = 1;
        }
        else
        {
            Main.PopMsg("HINF changed from \"1\" to \"0\".");
            Vars.HINF = 0;
        }
    }//GEN-LAST:event_HINFcheckActionPerformed


    public void SetStatus(String newstring, int msgType)
    {
        StatusLabel.setText(newstring);
        if (Main.GUIshowing)
        {
            JOptionPane.showMessageDialog(this, newstring, Vars.HubName,
                                          msgType);
        }
    }


    public void SetStatus(String newstring)
    {
        StatusLabel.setText(newstring);
        if (Main.GUIshowing)
        {
            JOptionPane.showMessageDialog(this, newstring, Vars.HubName,
                                          JOptionPane.INFORMATION_MESSAGE);
        }
    }


    public void setEnableadcs(javax.swing.JButton enableadcs)
    {
        this.enableadcs = enableadcs;
    }


    public javax.swing.JButton getEnableadcs()
    {
        return enableadcs;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable       AccountTable;
    private javax.swing.JCheckBox    BCTMcheck;
    private javax.swing.JCheckBox    BINFcheck;
    private javax.swing.JCheckBox    BMSGcheck;
    private javax.swing.JCheckBox    BPAScheck;
    private javax.swing.JCheckBox    BRCMcheck;
    private javax.swing.JCheckBox    BREScheck;
    private javax.swing.JCheckBox    BSCHcheck;
    private javax.swing.JCheckBox    BSTAcheck;
    private javax.swing.JCheckBox    BSUPcheck;
    private javax.swing.JTable       BanTable;
    private javax.swing.JCheckBox    DCTMcheck;
    private javax.swing.JCheckBox    DINFcheck;
    private javax.swing.JCheckBox    DMSGcheck;
    private javax.swing.JCheckBox    DPAScheck;
    private javax.swing.JCheckBox    DRCMcheck;
    private javax.swing.JCheckBox    DREScheck;
    private javax.swing.JCheckBox    DSCHcheck;
    private javax.swing.JCheckBox    DSTAcheck;
    private javax.swing.JCheckBox    DSUPcheck;
    private javax.swing.JCheckBox    ECTMcheck;
    private javax.swing.JCheckBox    EINFcheck;
    private javax.swing.JCheckBox    EMSGcheck;
    private javax.swing.JCheckBox    EPAScheck;
    private javax.swing.JCheckBox    ERCMcheck;
    private javax.swing.JCheckBox    EREScheck;
    private javax.swing.JCheckBox    ESCHcheck;
    private javax.swing.JCheckBox    ESTAcheck;
    private javax.swing.JCheckBox    ESUPcheck;
    private javax.swing.JCheckBox    FCTMcheck;
    private javax.swing.JCheckBox    FINFcheck;
    private javax.swing.JCheckBox    FMSGcheck;
    private javax.swing.JCheckBox    FPAScheck;
    private javax.swing.JCheckBox    FRCMcheck;
    private javax.swing.JCheckBox    FREScheck;
    private javax.swing.JCheckBox    FSCHcheck;
    private javax.swing.JCheckBox    FSTAcheck;
    private javax.swing.JCheckBox    FSUPcheck;
    private javax.swing.JCheckBox    HCTMcheck;
    private javax.swing.JCheckBox    HINFcheck;
    private javax.swing.JCheckBox    HMSGcheck;
    private javax.swing.JCheckBox    HPAScheck;
    private javax.swing.JCheckBox    HRCMcheck;
    private javax.swing.JCheckBox    HREScheck;
    private javax.swing.JCheckBox    HSCHcheck;
    private javax.swing.JCheckBox    HSTAcheck;
    private javax.swing.JCheckBox    HSUPcheck;
    private javax.swing.JTextArea    LogText;
    private javax.swing.JPanel       PPanel;
    private javax.swing.JScrollPane  Panelxxx;
    private javax.swing.JPanel       PluginPanel;
    private javax.swing.JTable       PyTable;
    private javax.swing.JLabel       StatusLabel;
    private javax.swing.JLabel       adcslabel;
    private javax.swing.JButton      addnewport;
    private javax.swing.JTextField   automagicsearchfield;
    private javax.swing.JTextField   botdescfield;
    private javax.swing.JTextField   botnamefield;
    private javax.swing.ButtonGroup  buttonGroup1;
    private javax.swing.ButtonGroup  buttonGroup2;
    private javax.swing.ButtonGroup  buttonGroup3;
    private javax.swing.ButtonGroup  buttonGroup4;
    private javax.swing.JTextField   chatintervalfield;
    private javax.swing.JCheckBox    command_pmcheck;
    private javax.swing.JLabel       connectingcount;
    private javax.swing.JLabel       cpunumber;
    private javax.swing.JButton      disableadcs;
    private javax.swing.JButton      enableadcs;
    private javax.swing.JTextField   fieldtimeout;
    private javax.swing.JButton      genbutton;
    private javax.swing.JTextField   historylinesfield;
    private javax.swing.JTextField   hubhostfield;
    private javax.swing.JButton      jButton1;
    private javax.swing.JButton      jButton10;
    private javax.swing.JButton      jButton11;
    private javax.swing.JButton      jButton12;
    private javax.swing.JButton      jButton13;
    private javax.swing.JButton      jButton14;
    private javax.swing.JButton      jButton15;
    private javax.swing.JButton      jButton16;
    private javax.swing.JButton      jButton17;
    private javax.swing.JButton      jButton18;
    private javax.swing.JButton      jButton19;
    private javax.swing.JButton      jButton2;
    private javax.swing.JButton      jButton20;
    private javax.swing.JButton      jButton21;
    private javax.swing.JButton      jButton22;
    private javax.swing.JButton      jButton23;
    private javax.swing.JButton      jButton24;
    private javax.swing.JButton      jButton25;
    private javax.swing.JButton      jButton26;
    private javax.swing.JButton      jButton27;
    private javax.swing.JButton      jButton28;
    private javax.swing.JButton      jButton29;
    private javax.swing.JButton      jButton3;
    private javax.swing.JButton      jButton30;
    private javax.swing.JButton      jButton31;
    private javax.swing.JButton      jButton32;
    private javax.swing.JButton      jButton33;
    private javax.swing.JButton      jButton34;
    private javax.swing.JButton      jButton35;
    private javax.swing.JButton      jButton4;
    private javax.swing.JButton      jButton5;
    private javax.swing.JButton      jButton6;
    private javax.swing.JButton      jButton7;
    private javax.swing.JButton      jButton8;
    private javax.swing.JButton      jButton9;
    private javax.swing.JLabel       jLabel1;
    private javax.swing.JLabel       jLabel10;
    private javax.swing.JLabel       jLabel11;
    private javax.swing.JLabel       jLabel12;
    private javax.swing.JLabel       jLabel13;
    private javax.swing.JLabel       jLabel14;
    private javax.swing.JLabel       jLabel15;
    private javax.swing.JLabel       jLabel16;
    private javax.swing.JLabel       jLabel17;
    private javax.swing.JLabel       jLabel18;
    private javax.swing.JLabel       jLabel19;
    private javax.swing.JLabel       jLabel2;
    private javax.swing.JLabel       jLabel20;
    private javax.swing.JLabel       jLabel21;
    private javax.swing.JLabel       jLabel22;
    private javax.swing.JLabel       jLabel23;
    private javax.swing.JLabel       jLabel24;
    private javax.swing.JLabel       jLabel25;
    private javax.swing.JLabel       jLabel26;
    private javax.swing.JLabel       jLabel27;
    private javax.swing.JLabel       jLabel28;
    private javax.swing.JLabel       jLabel29;
    private javax.swing.JLabel       jLabel3;
    private javax.swing.JLabel       jLabel30;
    private javax.swing.JLabel       jLabel31;
    private javax.swing.JLabel       jLabel32;
    private javax.swing.JLabel       jLabel33;
    private javax.swing.JLabel       jLabel34;
    private javax.swing.JLabel       jLabel35;
    private javax.swing.JLabel       jLabel36;
    private javax.swing.JLabel       jLabel37;
    private javax.swing.JLabel       jLabel38;
    private javax.swing.JLabel       jLabel39;
    private javax.swing.JLabel       jLabel4;
    private javax.swing.JLabel       jLabel40;
    private javax.swing.JLabel       jLabel41;
    private javax.swing.JLabel       jLabel42;
    private javax.swing.JLabel       jLabel43;
    private javax.swing.JLabel       jLabel44;
    private javax.swing.JLabel       jLabel45;
    private javax.swing.JLabel       jLabel46;
    private javax.swing.JLabel       jLabel47;
    private javax.swing.JLabel       jLabel48;
    private javax.swing.JLabel       jLabel49;
    private javax.swing.JLabel       jLabel5;
    private javax.swing.JLabel       jLabel50;
    private javax.swing.JLabel       jLabel51;
    private javax.swing.JLabel       jLabel52;
    private javax.swing.JLabel       jLabel53;
    private javax.swing.JLabel       jLabel54;
    private javax.swing.JLabel       jLabel55;
    private javax.swing.JLabel       jLabel56;
    private javax.swing.JLabel       jLabel57;
    private javax.swing.JLabel       jLabel58;
    private javax.swing.JLabel       jLabel59;
    private javax.swing.JLabel       jLabel6;
    private javax.swing.JLabel       jLabel60;
    private javax.swing.JLabel       jLabel61;
    private javax.swing.JLabel       jLabel62;
    private javax.swing.JLabel       jLabel63;
    private javax.swing.JLabel       jLabel64;
    private javax.swing.JLabel       jLabel65;
    private javax.swing.JLabel       jLabel66;
    private javax.swing.JLabel       jLabel67;
    private javax.swing.JLabel       jLabel68;
    private javax.swing.JLabel       jLabel69;
    private javax.swing.JLabel       jLabel7;
    private javax.swing.JLabel       jLabel70;
    private javax.swing.JLabel       jLabel72;
    private javax.swing.JLabel       jLabel74;
    private javax.swing.JLabel       jLabel75;
    private javax.swing.JLabel       jLabel76;
    private javax.swing.JLabel       jLabel77;
    private javax.swing.JLabel       jLabel78;
    private javax.swing.JLabel       jLabel79;
    private javax.swing.JLabel       jLabel8;
    private javax.swing.JLabel       jLabel80;
    private javax.swing.JLabel       jLabel81;
    private javax.swing.JLabel       jLabel82;
    private javax.swing.JLabel       jLabel83;
    private javax.swing.JLabel       jLabel84;
    private javax.swing.JLabel       jLabel85;
    private javax.swing.JLabel       jLabel87;
    private javax.swing.JLabel       jLabel88;
    private javax.swing.JLabel       jLabel89;
    private javax.swing.JLabel       jLabel9;
    private javax.swing.JLabel       jLabel90;
    private javax.swing.JLabel       jLabel91;
    private javax.swing.JLabel       jLabel92;
    private javax.swing.JLabel       jLabel93;
    private javax.swing.JLabel       jLabel94;
    private javax.swing.JLabel       jLabel95;
    private javax.swing.JLabel       jLabel96;
    private javax.swing.JLabel       jLabel97;
    private javax.swing.JLabel       jLabel98;
    private javax.swing.JList        jList1;
    private javax.swing.JPanel       jPanel1;
    private javax.swing.JPanel       jPanel10;
    private javax.swing.JPanel       jPanel11;
    private javax.swing.JPanel       jPanel12;
    private javax.swing.JPanel       jPanel13;
    private javax.swing.JPanel       jPanel14;
    private javax.swing.JPanel       jPanel15;
    private javax.swing.JPanel       jPanel16;
    private javax.swing.JPanel       jPanel17;
    private javax.swing.JPanel       jPanel18;
    private javax.swing.JPanel       jPanel19;
    private javax.swing.JPanel       jPanel2;
    private javax.swing.JPanel       jPanel20;
    private javax.swing.JPanel       jPanel21;
    private javax.swing.JPanel       jPanel22;
    private javax.swing.JPanel       jPanel25;
    private javax.swing.JPanel       jPanel26;
    private javax.swing.JPanel       jPanel27;
    private javax.swing.JPanel       jPanel28;
    private javax.swing.JPanel       jPanel29;
    private javax.swing.JPanel       jPanel3;
    private javax.swing.JPanel       jPanel30;
    private javax.swing.JPanel       jPanel31;
    private javax.swing.JPanel       jPanel32;
    private javax.swing.JPanel       jPanel33;
    private javax.swing.JPanel       jPanel34;
    private javax.swing.JPanel       jPanel35;
    private javax.swing.JPanel       jPanel36;
    private javax.swing.JPanel       jPanel37;
    private javax.swing.JPanel       jPanel38;
    private javax.swing.JPanel       jPanel39;
    private javax.swing.JPanel       jPanel4;
    private javax.swing.JPanel       jPanel40;
    private javax.swing.JPanel       jPanel41;
    private javax.swing.JPanel       jPanel42;
    private javax.swing.JPanel       jPanel43;
    private javax.swing.JPanel       jPanel44;
    private javax.swing.JPanel       jPanel45;
    private javax.swing.JPanel       jPanel46;
    private javax.swing.JPanel       jPanel47;
    private javax.swing.JPanel       jPanel48;
    private javax.swing.JPanel       jPanel49;
    private javax.swing.JPanel       jPanel5;
    private javax.swing.JPanel       jPanel6;
    private javax.swing.JPanel       jPanel7;
    private javax.swing.JPanel       jPanel8;
    private javax.swing.JPanel       jPanel9;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton10;
    private javax.swing.JRadioButton jRadioButton11;
    private javax.swing.JRadioButton jRadioButton12;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JRadioButton jRadioButton5;
    private javax.swing.JRadioButton jRadioButton6;
    private javax.swing.JRadioButton jRadioButton7;
    private javax.swing.JRadioButton jRadioButton8;
    private javax.swing.JRadioButton jRadioButton9;
    private javax.swing.JScrollPane  jScrollPane1;
    private javax.swing.JScrollPane  jScrollPane10;
    private javax.swing.JScrollPane  jScrollPane11;
    private javax.swing.JScrollPane  jScrollPane2;
    private javax.swing.JScrollPane  jScrollPane3;
    private javax.swing.JScrollPane  jScrollPane4;
    private javax.swing.JScrollPane  jScrollPane5;
    private javax.swing.JScrollPane  jScrollPane6;
    private javax.swing.JScrollPane  jScrollPane7;
    private javax.swing.JScrollPane  jScrollPane8;
    private javax.swing.JScrollPane  jScrollPane9;
    private javax.swing.JTabbedPane  jTabbedPane1;
    private javax.swing.JTabbedPane  jTabbedPane2;
    private javax.swing.JTabbedPane  jTabbedPane3;
    private javax.swing.JTextArea    jTextArea1;
    private javax.swing.JTextArea    jTextArea2;
    private javax.swing.JTextField   jTextField1;
    private javax.swing.JTextField   jTextField2;
    private javax.swing.JTextField   jTextField3;
    private javax.swing.JTextField   jTextField4;
    private javax.swing.JTextField   jTextField5;
    private javax.swing.JLabel       jrename;
    private javax.swing.JLabel       jreprovider;
    private javax.swing.JTextField   kicktimefield;
    private javax.swing.JComboBox    langcombo;
    private javax.swing.JButton      loadkeysbutton;
    private javax.swing.JTextField   maxchatmsgfield;
    private javax.swing.JTextField   maxdefield;
    private javax.swing.JTextField   maxemfield;
    private javax.swing.JTextField   maxhubsopfield;
    private javax.swing.JTextField   maxhubsregfield;
    private javax.swing.JTextField   maxhubsuserfield;
    private javax.swing.JTextField   maxnifield;
    private javax.swing.JTextField   maxschcharsfield;
    private javax.swing.JTextField   maxsharefield;
    private javax.swing.JTextField   maxslfield;
    private javax.swing.JTextField   maxusersfield;
    private javax.swing.JTextField   minnifield;
    private javax.swing.JTextField   minschcharsfield;
    private javax.swing.JTextField   minsharefield;
    private javax.swing.JTextField   minslfield;
    private javax.swing.JPanel       miscpanel;
    private javax.swing.JTextArea    msgbannedfield;
    private javax.swing.JTextArea    msgfullfield;
    private javax.swing.JTextArea    msgsearchspamfield;
    private javax.swing.JTextField   namefield;
    private javax.swing.JTextArea    nickcharsfield;
    private javax.swing.JCheckBox    notifycheck;
    private javax.swing.JTextField   opchatdescfield;
    private javax.swing.JTextField   opchatnamefield;
    private javax.swing.JLabel       osarch;
    private javax.swing.JLabel       osname;
    private javax.swing.JLabel       osversion;
    private javax.swing.JScrollPane  pane5;
    private javax.swing.JTable       portlist;
    private javax.swing.JCheckBox    privatecheck;
    private javax.swing.JCheckBox    proxycheck;
    private javax.swing.JTextField   proxyhostfield;
    private javax.swing.JTextField   proxyportfield;
    private javax.swing.JTextField   redirecturl;
    private javax.swing.JCheckBox    regonlycheck;
    private javax.swing.JButton      remport;
    private javax.swing.JCheckBox    savelogscheck;
    private javax.swing.JCheckBox    searchcheck;
    private javax.swing.JTextField   searchlogbasefield;
    private javax.swing.JTextField   searchspamresetfield;
    private javax.swing.JTextField   searchstepsfield;
    private javax.swing.JLabel       startuptime;
    private javax.swing.JTextField   topicfield;
    private javax.swing.JLabel       uptime;
    private javax.swing.JCheckBox    usecertificatescheck;
    private javax.swing.JLabel       usercount;
    private javax.swing.JPanel       xxx;
    // End of variables declaration//GEN-END:variables

    // private JPanel jPanel1;

}
