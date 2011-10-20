package dshub.plugin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/*Copyright (C) 2003-2007 Edgewall Software
All rights reserved.*/

 	public class TraceRoute {
        
        public static String Trace(String host) {

        try
        {
            return "Hop Count: " + getHops(getDefaultTraceRoute(),host) ;
        } 
                            
            catch (IOException e) 
            {
            return e.toString();
            }
}
 	
 	                 /**
 	                 returns number of hops
 	                  */
            public static String getHops(String cmd, String dest) throws IOException
                                 {
 	                    String line;
 	                    Process pr = null;
                            String result="Printing Results\n"; 	                   

 	                    int maxInvalidIP = 2;       //Maximum number of sequential * allowed before stop
 	                    
 	                    try{
 	                        //System.out.println("trace: " + dest);
 	                        
 	                      pr = Runtime.getRuntime().exec(cmd + " " +dest);
	
 	                      BufferedReader din = new BufferedReader(new InputStreamReader(pr.getInputStream()));
 	                
 	                      do {
 	                        if( (line = din.readLine())==null ) 
                                              {  return result; }
                                            result+=line+ "\n";
 	                      } while(true);
 	                     }
                        catch( Exception e)
                                   {
                                     return e.toString();
                                         }
 	                  }
 	                 
 	                  private static String getDefaultTraceRoute() {
 	                      /*  Windows*/
 	                      if(System.getProperty("os.name").indexOf("Windows")!=-1)
 	                       return  "tracert -d";
 	                      /* On Un*x */
 	                      else return "traceroute -n";
 	                  }
 	                 
 	
	}
