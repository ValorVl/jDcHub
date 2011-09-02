package dshub;
/*
 * HubServer.java
 *
 * Created on 03 martie 2007, 23:00
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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.swing.JOptionPane;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.ssl.SslFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import dshub.Modules.Modulator;
import dshub.adcs.CertManager;
import dshub.adcs.SSLManager;
import dshub.banning.BanList;
import dshub.banning.bans;
import dshub.conf.Port;
import dshub.conf.Variables;
import dshub.conf.Vars;
import dshub.i18n.Translation;
import dshub.util.ADC;


/**
 * Basic hub server listener and socket receiver, sends users to each's thread after connecting.
 * Handles the hub databases kept in files ( regs, config and bans).
 *
 * @author Pietricica
 */
public class HubServer extends Thread
{

    /**
     * Server main socket
     */
    // private ServerSocket MainSocket;

    public SSLManager sslmanager;

    InputStream  IS;
    OutputStream OS;
    int          port;

    ClientAssasin myAssasin;

    public boolean adcs_ok = false;

    //ClientHandler firstclient;

    private static Variables vars;
    private static RegConfig rcfg;
    private static bans      bcfg;

    public static boolean done_adcs = false;

    public static boolean restart;
    //public static IoServiceManager IOSM;
    // public static ServiceManager SM;

    //private SocketAcceptorConfig cfg;

    public IoAcceptor acceptor;
    // private ExecutorService x;//,y;
    // private InetSocketAddress address;
    public Calendar   MyCalendar;


    /**
     * Creates a new instance of HubServer
     */
    public HubServer()
    {

        //myPath="";
        //System.out.println (myPath);
        org.apache
                .log4j
                .PropertyConfigurator
                .configure(getClass().getResource("/log4j.properties"));
        //                Main.myPath+"log4j.properties");
        start();
        setPriority(NORM_PRIORITY + 1);

    }


    public void run()
    {


        sslmanager = new SSLManager(new CertManager());
        SslFilter sslfilter = sslmanager.getSSLFilter();
        if (sslfilter != null)
        {
            adcs_ok = true;
        }
        /* if(adcs_ok)
             System.out.println("ADCS OK");
         else
             System.out.println("ADCS not OK");
        */
        try
        {
            File MotdFile = new File(Main.myPath + "motd");
            FileReader mr = new FileReader(MotdFile);
            BufferedReader mb = new BufferedReader(mr);
            Main.MOTD = "";
            while ((Main.auxhelp = mb.readLine()) != null)
            {
                Main.MOTD = Main.MOTD + Main.auxhelp + "\n";
            }
            mb.close();
            Main.MOTD = Main.MOTD.substring(0, Main.MOTD.length() - 1);
            ADC.MOTD = Main.MOTD;
        }
        catch (IOException e)
        {
            Main.MOTD = ADC.MOTD;

        }
        restart = false;
        vars = new Variables();
        reloadregs();
        reloadconfig();
        reloadbans();

        // port=Vars.Default_Port;

        Modulator.findModules();
        try
        {
            this.sleep(500);
        }
        catch (Exception e)
        {
        }

        //   new ClientNod();
        // ByteBuffer.setUseDirectBuffers(false);
        //   ByteBuffer.setAllocator(new PooledByteBufferAllocator());


        // x=Executors.newCachedThreadPool();
        //  y=Executors.newCachedThreadPool();

        acceptor = new NioSocketAcceptor();
        NioSocketAcceptor nsa = (NioSocketAcceptor) acceptor;
        nsa.setReuseAddress(true);


        // cfg = new SocketAcceptorConfig();
        // cfg.setThreadModel(ThreadModel.MANUAL);

        //cfg.getSessionConfig().setReceiveBufferSize(102400);
        // cfg.getSessionConfig().setSendBufferSize(102400);

        if (Vars.adcs_mode)
        {

            if (adcs_ok)
            {
                acceptor.getFilterChain()
                        .addLast("sslFilter", sslfilter);
            }
            else
            {
                Main.GUI
                        .SetStatus(
                                "Couldn't find suitable keys and certificate.\nPlease load them or regenerate.\n" +
                                "ADC Secure mode has been disabled.",
                                JOptionPane.WARNING_MESSAGE);
                Vars.adcs_mode = false;
            }

        }
        done_adcs = true;
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        TextLineCodecFactory myx = new TextLineCodecFactory(Charset.forName("UTF-8"), "\n", "\n");
        myx.setDecoderMaxLineLength(64 * 1024);
        myx.setEncoderMaxLineLength(64 * 1024);
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(myx));
        MyCalendar = Calendar.getInstance();
        // DefaultIoFilterChainBuilder filterChainBuilder = cfg.getFilterChain();
        //  filterChainBuilder.addLast("threadPool", new ExecutorFilter(y));
        //cfg.getSessionConfig().setKeepAlive(true);

        acceptor.getSessionConfig().setReadBufferSize(64 * 1024);
        //acceptor.getSessionConfig().WriteBufferSize( 2048000 );
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 50);
        acceptor.setHandler(new SimpleHandler());
//System.out.println(acceptor.getSessionConfig().getWriteTimeout());
        // acceptor.setCloseOnDeactivation(true);
        //cfg.getSessionConfig().
        //System.out.println(cfg.getSessionConfig().getReceiveBufferSize());
        // IOSM=new IoServiceManager(acceptor);
        //SM=new ServiceManager(acceptor);
        //  IOSM.startCollectingStats(10000);
        // address=new InetSocketAddress(port);


        String pop = "";
        for (Port port : Vars.activePorts)
        {
            if (this.addPort(port) == true)
            {
                pop += port.portValue + " ";
            }
        }
        if (pop.equals(""))
        {
            Main.PopMsg("Couldn't start server on any set ports.");
        }
        else

        {
            Main.PopMsg("Server created. Listening on ports: " + pop + ".");
        }


        if (Main.GUIok)
        {
            if (pop.equals(""))
            {
                Main.GUI.SetStatus("Couldn't start server on any set ports.");
            }
            else

            {
                Main.GUI.SetStatus("Server created. Listening on ports: " + pop + ".");
            }

        }


        Date d = new Date(Main.curtime);
        Main.PopMsg("Start Time:" + d.toString());
        System.out.print("\n>");

        myAssasin = new ClientAssasin();//temporary removed
        //  ClientExecutor myExecutor=new ClientExecutor();


    }


    public boolean addPort(Port port)
    {
        // Vars.activePorts.add(port);
        try
        {
            acceptor.bind(new InetSocketAddress(port.portValue));
            port.setStatus(true);

        }
        catch (java.net.BindException jbe)
        {
            Main.PopMsg("Network problem. Unable to listen on port " + port.portValue + "." + jbe);
            port.setStatus(false);
            port.MSG = jbe.toString();
            if (Main.GUIok)
            {
                Main.GUI
                        .SetStatus("Network problem. Unable to listen on port " +
                                   port.portValue +
                                   "." +
                                   jbe, JOptionPane.ERROR_MESSAGE);

            }
            return false;
        }
        catch (java.lang.IllegalArgumentException ee)
        {
            Main.PopMsg("Invalid port " + port.portValue + "." + ee);
            port.setStatus(false);
            port.MSG = ee.toString();
            if (Main.GUIok)
            {
                Main.GUI
                        .SetStatus("invalid port " + port.portValue + "." + ee,
                                   JOptionPane.ERROR_MESSAGE);

            }
            return false;
        }
        catch (IOException ex)
        {
            // ex.printStackTrace();
            port.setStatus(false);
            port.MSG = ex.toString();
            return false;
        }

        return true;
    }


    public void delPort(Port port)
    {
        //  Vars.activePorts.remove(port);
        //acceptor.unbind();
        try
        {
            acceptor.unbind(new InetSocketAddress(port.portValue));
            //    System.out.println("deleted port"+port.portValue);
        }
        catch (Exception exception)
        {

            exception.printStackTrace();
        }

        //    System.out.println("hmm");
    }


    public void shutdown()
    {
        for (Port x : Vars.activePorts)
        {
            delPort(x);
        }

        //  acceptor.unbind();
        // x.shutdown();
    }


    public static ClientNod AddClient()
    {
        //ClientHandler ret;
        if (restart)
        {
            return null;
        }

        ClientNod newclient = new ClientNod();
        /*synchronized ( SimpleHandler.Users)
        {
        SimpleHandler.Users.add(newclient);
        }/*
       /*newclient.NextClient=ClientNod.FirstClient.NextClient;
       ClientNod.FirstClient.NextClient=newclient;
       newclient.PrevClient=ClientNod.FirstClient;
       if(newclient.NextClient!=null)
       newclient.NextClient.PrevClient=newclient;*/
        return newclient;


    }


    public static void rewriteconfig()
    {
        rewriteconfig("config");
    }


    public static boolean rewriteconfig(String configName)
    {
        File MainConfigFile;
        MainConfigFile = new File(Main.myPath + configName);

        try
        {
            FileOutputStream MainConfigFileOutput = new FileOutputStream(MainConfigFile);
            GZIPOutputStream gzos = new GZIPOutputStream(MainConfigFileOutput);  // Compress.
            ObjectOutputStream out = new ObjectOutputStream(gzos);  // Save objects
            out.writeObject(new Variables());
            out.flush();                 // Always flush the output.
            out.close();                 // And close the stream.

        }
        catch (IOException e)
        {
            Main.PopMsg(e.toString());
            return false;
        }
        return true;
    }


    public static void rewritebans()
    {
        rewritebans("banlist");
    }


    public static boolean rewritebans(String banlistName)
    {
        File MainBanFile;
        MainBanFile = new File(Main.myPath + banlistName);

        try
        {
            FileOutputStream MainBanFileOutput = new FileOutputStream(MainBanFile);
            GZIPOutputStream gzos = new GZIPOutputStream(MainBanFileOutput);  // Compress.
            ObjectOutputStream out = new ObjectOutputStream(gzos);  // Save objects
            out.writeObject(new bans());
            out.flush();                 // Always flush the output.
            out.close();                 // And close the stream.

        }
        catch (IOException e)
        {
            Main.PopMsg(e.toString());
            return false;
        }
        return true;
    }


    public static void rewriteregs()
    {
        rewriteregs("regs");
    }


    public static boolean rewriteregs(String regName)
    {
        File MainRegFile;

        MainRegFile = new File(Main.myPath + regName);
        try
        {

            FileOutputStream MainRegFileOutput = new FileOutputStream(MainRegFile);
            GZIPOutputStream gzosreg = new GZIPOutputStream(MainRegFileOutput);  // Compress.
            ObjectOutputStream outreg = new ObjectOutputStream(gzosreg);  // Save objects
            outreg.writeObject(new RegConfig());
            outreg.flush();                 // Always flush the output.
            outreg.close();                 // And close the stream.

        }
        catch (IOException e)
        {
            Main.PopMsg(e.toString());
            return false;
        }
        return true;
    }


    public void reloadconfig()
    {
        File MainConfigFile;
        MainConfigFile = new File(Main.myPath + "config");

        try
        {
            FileInputStream MainConfigFileReader = new FileInputStream(MainConfigFile);
            GZIPInputStream gzis = new GZIPInputStream(MainConfigFileReader);
            ObjectInputStream in = new ObjectInputStream(gzis);


            vars = (Variables) in.readObject();
            Vars.Timeout_Login = vars.Timeout_Login;
            // Vars.Default_Port=vars.Default_Port;

            Vars.HubVersion = vars.HubVersion;
            Vars.HubDE = vars.HubDE;
            Vars.HubName = vars.HubName;


            Vars.min_ni = vars.min_ni;
            Vars.max_ni = vars.max_ni;
            Vars.max_de = vars.max_de;
            Vars.max_share = vars.max_share;
            Vars.min_share = vars.min_share;
            Vars.max_sl = vars.max_sl;
            Vars.min_sl = vars.min_sl;
            Vars.max_em = vars.max_em;
            Vars.max_hubs_op = vars.max_hubs_op;
            Vars.max_hubs_reg = vars.max_hubs_reg;
            Vars.max_hubs_user = vars.max_hubs_user;
            Vars.min_sch_chars = vars.min_sch_chars;
            Vars.max_sch_chars = vars.max_sch_chars;
            Vars.max_chat_msg = vars.max_chat_msg;
            Vars.history_lines = vars.history_lines;
            Vars.command_pm = vars.command_pm;

            Vars.Opchat_name = vars.Opchat_name;
            Vars.Opchat_desc = vars.Opchat_desc;
            Vars.kick_time = vars.kick_time;
            Vars.Msg_Banned = vars.Msg_Banned;
            Vars.Msg_Search_Spam = vars.Msg_Search_Spam;

            Vars.reg_only = vars.reg_only;
            Vars.nick_chars = vars.nick_chars;
            Vars.max_users = vars.max_users;
            Vars.Msg_Full = vars.Msg_Full;

            Vars.OpChatCid = vars.OpChatCid;
            Vars.SecurityCid = vars.SecurityCid;

            Vars.Hub_Host = vars.Hub_Host;
            Vars.Proxy_Host = vars.Proxy_Host;
            Vars.Proxy_Port = vars.Proxy_Port;
            Vars.redirect_url = vars.redirect_url;
            Vars.adcs_mode = vars.adcs_mode;

            Vars.certlogin = vars.certlogin;

            Vars.chat_interval = vars.chat_interval;

            Vars.savelogs = vars.savelogs;
            Vars.automagic_search = vars.automagic_search;
            Vars.search_log_base = vars.search_log_base;
            Vars.search_steps = vars.search_steps;
            Vars.search_spam_reset = vars.search_spam_reset;
            Vars.bot_name = vars.bot_name;
            Vars.bot_desc = vars.bot_desc;

            Vars.activePlugins = vars.activePlugins;
            Vars.activePorts = vars.activePorts;

            Vars.lang = vars.lang;

            if (Vars.lang.length() > 4)
            {
                Translation.curLocale =
                        new Locale(Vars.lang.substring(0, 2), Vars.lang.substring(3));
                Locale.setDefault(Translation.curLocale);
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
            }

            Vars.BMSG = vars.BMSG;
            Vars.EMSG = vars.EMSG;
            Vars.DMSG = vars.DMSG;
            Vars.HMSG = vars.HMSG;
            Vars.FMSG = vars.FMSG;

            Vars.BSTA = vars.BSTA;
            Vars.ESTA = vars.ESTA;
            Vars.DSTA = vars.DSTA;
            Vars.FSTA = vars.FSTA;
            Vars.HSTA = vars.HSTA;

            Vars.BCTM = vars.BCTM;
            Vars.DCTM = vars.DCTM;
            Vars.ECTM = vars.ECTM;
            Vars.FCTM = vars.FCTM;
            Vars.HCTM = vars.HCTM;

            Vars.BRCM = vars.BRCM;
            Vars.DRCM = vars.DRCM;
            Vars.ERCM = vars.ERCM;
            Vars.FRCM = vars.FRCM;
            Vars.HRCM = vars.HRCM;

            Vars.BINF = vars.BINF;
            Vars.DINF = vars.DINF;
            Vars.EINF = vars.EINF;
            Vars.FINF = vars.FINF;
            Vars.HINF = vars.HINF;

            Vars.BSCH = vars.BSCH;
            Vars.DSCH = vars.DSCH;
            Vars.ESCH = vars.ESCH;
            Vars.FSCH = vars.FSCH;
            Vars.HSCH = vars.HSCH;

            Vars.BRES = vars.BRES;
            Vars.DRES = vars.DRES;
            Vars.ERES = vars.ERES;
            Vars.FRES = vars.FRES;
            Vars.HRES = vars.HRES;

            Vars.BPAS = vars.BPAS;
            Vars.DPAS = vars.DPAS;
            Vars.EPAS = vars.EPAS;
            Vars.FPAS = vars.FPAS;
            Vars.HPAS = vars.HPAS;

            Vars.BSUP = vars.BSUP;
            Vars.DSUP = vars.DSUP;
            Vars.ESUP = vars.ESUP;
            Vars.FSUP = vars.FSUP;
            Vars.HSUP = vars.HSUP;

            in.close();

        }
        catch (FileNotFoundException fnfe)
        {
            //file not found so were gonna make it
            Main.PopMsg("Generated new PID/CID for OpChat and Hub Security.");
            rewriteconfig();


        }
        catch (IOException e)
        {
            Main.PopMsg("Error accesing config files.Attempting overwrite with default values.");
            Main.PopMsg("Generated new PID/CID for OpChat and Hub Security.");
            rewriteconfig();
        }
        catch (ClassNotFoundException e)
        {
            Main.PopMsg("Internal Error Config Corrupted Files. FAIL.");
        }

    }


    public void reloadregs()
    {
        File MainRegFile;

        MainRegFile = new File(Main.myPath + "regs");
        try
        {


            FileInputStream MainRegFileReader = new FileInputStream(MainRegFile);
            GZIPInputStream gzisreg = new GZIPInputStream(MainRegFileReader);
            ObjectInputStream inreg = new ObjectInputStream(gzisreg);

            rcfg = (RegConfig) inreg.readObject();

            for (int i = 1; i < rcfg.reg_count; i++)
            {
                AccountsConfig.addReg(rcfg.nods[i]);
            }


            inreg.close();
        }
        catch (FileNotFoundException fnfe)
        {
            //file not found so were gonna make it
            rewriteregs();


        }
        catch (IOException e)
        {
            Main.PopMsg("Error accesing regs files.Attempting overwrite with default values.");
            rewriteregs();
        }
        catch (ClassNotFoundException e)
        {
            Main.PopMsg("Internal Error Corrupted Regs File. FAIL.");
        }

    }


    public void reloadbans()
    {
        File MainBanFile;

        MainBanFile = new File(Main.myPath + "banlist");
        try
        {


            FileInputStream MainBanFileReader = new FileInputStream(MainBanFile);
            GZIPInputStream gzisreg = new GZIPInputStream(MainBanFileReader);
            ObjectInputStream inreg = new ObjectInputStream(gzisreg);

            bcfg = (bans) inreg.readObject();

            for (int i = 1; i < bcfg.i; i++)
            {
                bcfg.bans[i].Next = null;
                BanList.addban(bcfg.bans[i]);
            }


            inreg.close();
        }
        catch (FileNotFoundException fnfe)
        {
            //file not found so were gonna make it
            rewriteregs();


        }
        catch (IOException e)
        {
            Main.PopMsg("Error accesing bans files.Attempting overwrite with default values.");
            rewriteregs();
        }
        catch (ClassNotFoundException e)
        {
            Main.PopMsg("Internal Error Corrupted Bans File. FAIL.");
        }

    }
}
