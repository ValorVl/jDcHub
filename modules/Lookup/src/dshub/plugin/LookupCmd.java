package dshub.plugin;
import ru.sincore.Client;
import ru.sincore.ClientHandler;
import ru.sincore.SessionManager;

import java.util.StringTokenizer;
import java.net.*;
import java.io.*;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;



/* Lookup Plugin v.0.1 for DSHub
 * Made by Toast (with loads of help from Pietry) 
 * Idea from Mutor on Ptokax Board
 * Please visit www.adcportal.com for more ideas
 * and keep your DSHub updated 	http://www.death-squad.ro/dshub
 * 
 * NSLookup Code    -   www.javadb.com
 * Whois Code       -   www.java2s.com
 */

 public class LookupCmd
        {
    
    public LookupCmd ( ClientHandler cur_client, String Issued_Command )
        {
            StringTokenizer ST=new StringTokenizer( Issued_Command );
            ST.nextToken();
            
            if ( ! ( ST.hasMoreTokens() ) ) 
        {
            cur_client.sendFromBot("The lookup command list\n\nUsage:\n\n!lookup <switch> <input>\n\nAvailable Switches:\n\nCommand		Input	  Information\n\nnslookup		<dns>	- Shows DNS to IP information in client\nisPing		<nick>	- Shows isPing statistics in client\nrevdns		<nick>	- Shows hostname of user\ntraceroute	<nick>	- Shows route to user taken by server\nwhois 		<nick>	- Shows ripe information on user" );
            return; //1
        }
            String carrier = ST.nextToken();  

            if( carrier.equalsIgnoreCase("revdns")) //begining of Reverse DNS Code
        {
            if ( ! ( ST.hasMoreTokens() ) )
        {
            cur_client.sendFromBot("Error: couldn't retrive reverse dns information" );
            return; //2
        }
            //if we reached here, then it means that 1) we have revdns because of //1
            //and 2)we have an output because we have another token, otherwise we would have exited via //2
            
            String input= ST.nextToken();
            Client user = null;
            for ( Client x : SessionManager.getUsers() )
        {
            if( x .handler.NI.equalsIgnoreCase(input ))
            user=x;
             
        }
            if ( user == null ) // we did not find any user matching the nick 
            {
                cur_client.sendFromBot("Error: No user with that nick" );
                return;
            }
            try {
            InetAddress addr = InetAddress.getByName(user.handler.realIP);
            cur_client.sendFromBot("Fetching Reverse DNS Information... (please be patient)");
            cur_client.sendFromBot(addr.getHostName());
            }
            catch ( UnknownHostException uhe )
            {
            cur_client.sendFromBot( "Error: Couldn't resolve to host" );
            return;
            }
            }
            else if( carrier.equalsIgnoreCase("whois")) // Begining of Whois Code
            {
            if ( ! ( ST.hasMoreTokens() ) )
                   
                    { 
                    cur_client.sendFromBot( "Error: couldn't retrive whois information" );
                    return;
                    }
               
            String input = ST.nextToken();
            
            Client user = null;
            for ( Client x : SessionManager.getUsers() )
        {
            if( x .handler.NI.equalsIgnoreCase(input ))
            user=x;
             
        }
            if ( user == null ) // we did not find any user matching the nick 
            {
                cur_client.sendFromBot("Error: No user with that nick" );
                return;
            }
            cur_client.sendFromBot("Fetching Whois Information... (please be patient)");
            Socket theSocket;
            DataInputStream theWhoisStream;
            PrintStream ps;

            try {
            theSocket = new Socket("whois.ripe.net", 43);
            ps = new PrintStream(theSocket.getOutputStream());
      
            ps.print(user.handler.realIP +"\r\n");
            theWhoisStream = new DataInputStream(theSocket.getInputStream());
            String s;
            while ((s = theWhoisStream.readLine()) != null) {
            cur_client.sendFromBot(s);
            } 
            }
            catch (Exception e) {
            System.err.println(e);
            }
            }
            else if( carrier.equalsIgnoreCase("traceroute")) // Begining of Traceroute Code
            {
            if ( ! ( ST.hasMoreTokens() ) )
                   
                    { 
                    cur_client.sendFromBot( "Error: couldn't retrive traceroute infomation" );
                    return;
                    }
               
            String input = ST.nextToken();
            
            Client user = null;
            for ( Client x : SessionManager.getUsers() )
        {
            if( x .handler.NI.equalsIgnoreCase(input ))
            user=x;
             
        }
            if ( user == null ) // we did not find any user matching the nick 
            {
                cur_client.sendFromBot("Error: No user with that nick" );
                return;
            }
            cur_client.sendFromBot("Fetching Traceroute Information... (please be patient)");
            cur_client.sendFromBot(TraceRoute.Trace(user.handler.realIP));
                        
            }   
            else if( carrier.equalsIgnoreCase("nslookup")) // Begining of NSLookup Code
            {
            if ( ! ( ST.hasMoreTokens() ) )
                   
                    { 
                    cur_client.sendFromBot( "Error: couldn't retrive nslookup information" );
                    return;
                    }
               
            String input = ST.nextToken();
          
            try {
            
            InetAddress inetHost = InetAddress.getByName(input);
            String hostName = inetHost.getHostName();
            cur_client.sendFromBot("Fetching NSLookup Information... (please be patient)");
            cur_client.sendFromBot("The host name was: " + hostName);
            cur_client.sendFromBot("The hosts IP address is: " + inetHost.getHostAddress());
            
            } catch(UnknownHostException ex) {
            
            cur_client.sendFromBot("Unrecognized host");
        }
    }
     else if( carrier.equalsIgnoreCase("isPing")) // Begining of Ping Code
            {
            if ( ! ( ST.hasMoreTokens() ) )
                   
                    { 
                    cur_client.sendFromBot( "Error: couldn't retrive isPing information" );
                    return;
                    }
               
            String input = ST.nextToken();
                        Client user = null;
            for ( Client x : SessionManager.getUsers() )
        {
            if( x .handler.NI.equalsIgnoreCase(input ))
            user=x;
             
        }
            if ( user == null ) // we did not find any user matching the nick 
            {
                cur_client.sendFromBot("Error: No user with that nick" );
                return;
            }
            
            {
            cur_client.sendFromBot("Fetching Ping Information... (please be patient)");
            
            int i=0;
           
            String ip;
            ip = user.handler.realIP;
            String pingResult = "";
            String pingCmd = "isPing " + ip;
            
            try 
            {
                Runtime r = Runtime.getRuntime();
                Process p =  r.exec(pingCmd);
                BufferedReader in = new BufferedReader(new
                InputStreamReader(p.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null   && i<5)
                    
            {
                cur_client.sendFromBot(inputLine);
                i++;
            }
                cur_client.sendFromBot("Ping is done..");
                in.close();
                return;
            
            }
                catch (IOException e)
            {
                System.out.println(e);
            }
            catch(Exception ee)
            {
                System.out.println(ee);
            }
            
            }
    
     }      
            else
                
                    {
                    cur_client.sendFromBot("Error: Unknown Switch use !lookup for list of commands" );
                    return;
                    }
            
            }
    }


