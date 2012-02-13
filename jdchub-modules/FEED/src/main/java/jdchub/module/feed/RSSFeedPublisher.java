/*
* RSSFeedPublisher.java
*
* Created on 09 02 2012, 13:17
*
* Copyright (C) 2012 Alexey 'lh' Antonov
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

package jdchub.module.feed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.ConfigurationManager;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-09
 */
public class RSSFeedPublisher
{
    private static final Logger log = LoggerFactory.getLogger(RSSFeedPublisher.class);
    
    private Timer timer;

    public RSSFeedPublisher()
    {
        timer = new Timer(true);
    }


    private boolean readUrls(List<URL> feeds)
    {
        FileInputStream fstream = null;
        DataInputStream in = null;
        BufferedReader bufferedReader = null;
        try
        {
            fstream =
                    new FileInputStream(ConfigurationManager.instance().getHubConfigDir() +
                                        "/modules/FEED/" +
                                        ConfigurationManager.instance()
                                                            .getString("file_with_feeds_urls"));
            in = new DataInputStream(fstream);
            bufferedReader = new BufferedReader(new InputStreamReader(in));


            String urlString = bufferedReader.readLine();

            while (urlString != null)
            {
                URL feedURL = null;

                try
                {
                    feedURL = new URL(urlString);
                }
                catch (MalformedURLException e)
                {
                    log.error("Invalid feed URL : \'" + feedURL + "\'\n" + e.toString());
                }

                if (feedURL != null)
                {
                    feeds.add(feedURL);
                }

                urlString = bufferedReader.readLine();
            }

        }
        catch (IOException e)
        {
            log.error(e.toString());
            return false;
        }
        finally
        {
            try
            {
                in.close();
            }
            catch (Exception e)
            {
                log.error(e.toString());
            }
        }

        return true;
    }
    
    
    public boolean start()
    {
        List<URL> feeds = new LinkedList<URL>();

        if (!readUrls(feeds))
        {
            return false;
        }

        // run rss feeders
        for (URL feed : feeds)
        {
            timer.schedule(new RSSFeeder(feed), 1000, ConfigurationManager.instance().getLong("feed_update_interval"));
        }

        return true;
    }


    public Timer getTimer()
    {
        return timer;
    }
}
