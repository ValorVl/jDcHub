/*
 * ClientExecutor.java
 *
 * Created on 10 noiembrie 2007, 22:58
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

package ru.sincore;

/**
 * A thread that tries to send all messages in the queue;
 *
 * @author Pietricica
 */
public class ClientExecutor extends Thread
{

    /**
     * Creates a new instance of ClientExecutor
     */
    public ClientExecutor()
    {
        start();
    }


//    public void run()
//   {
//        while (!Main.Server.restart)
//        {
//            long start = System.currentTimeMillis();
//            if (SessionManager.users.isEmpty())
//            {
//                try
//                {
//                    this.sleep(1000);
//                }
//                catch (InterruptedException ex)
//                {
//
//                }
//                continue;
//            }
//
//
//            for (Client temp : SessionManager.getClients())
//            {
//                synchronized (temp.getClientHandler().Queue)
//                {
//                    if (temp.getClientHandler().Queue.First == null)
//                    {
//
//                        try
//                        {
//                            this.sleep(50);
//                        }
//                        catch (InterruptedException ex)
//                        {
//
//                        }
//                        continue;
//                    }
//                    String str = temp.getClientHandler().Queue.First.MSG + "\n";
//                    temp.getClientHandler().Queue.First = temp.getClientHandler().Queue.First.Next;
//                    while (temp.getClientHandler().Queue.First != null)
//                    {
//                        str += temp.getClientHandler().Queue.First.MSG + "\n";
//                        temp.getClientHandler().Queue.First = temp.getClientHandler().Queue.First.Next;
//                    }
//
//                    temp.handler.session.write(str.substring(0, str.length() - 1));
//
//                    try
//                    {
//                        this.sleep(50);
//                    }
//                    catch (InterruptedException ex)
//                    {
//
//                    }
//                }
//            }
//            /*long end=System.currentTimeMillis();
//    if(end-start>1000)
//    {
//         try
//            {
//                this.sleep(20);
//            } catch (InterruptedException ex)
//            {
//
//            }
//         continue;
//    }
//
//    try
//            {
//                this.sleep(1000-(end-start));
//            } catch (InterruptedException ex)
//            {
//
//            }*/
//        }
//	/*
 //   }

}
