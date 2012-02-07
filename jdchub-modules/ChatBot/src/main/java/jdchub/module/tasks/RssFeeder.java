/*
* RssFeeder.java
*
* Created on 03 02 2012, 16:32
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

package jdchub.module.tasks;

import jdchub.module.ChatBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimerTask;

/**
 * Rss feeder (works with RSS feed only, Atom not supported yet)
 *
 * @author Alexey 'lh' Antonov
 * @since 2012-02-03
 */
public class RssFeeder extends TimerTask
{
    private static final Logger log = LoggerFactory.getLogger(RssFeeder.class); 
    
    private ChatBot parent = null;
    
    private URL feedURL = null;
    private String lastFeedTitle = null;

    public RssFeeder(ChatBot parent, String feedURL)
    {
        this.parent = parent;

        try
        {
            this.feedURL = new URL(feedURL);
        }
        catch (MalformedURLException e)
        {
            this.feedURL = null;
            log.error("Invalid feed URL : \'" + feedURL + "\'\n" + e.toString());
        }
    }


    @Override
    public void run()
    {
        try
        {
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            domFactory.setNamespaceAware(true); // never forget this!
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            Document doc = builder.parse(feedURL.openStream());

            XPathFactory factory = XPathFactory.newInstance();
            XPath xpath = factory.newXPath();

            XPathExpression expr = xpath.compile("/rss/channel/item[1]/title");
            String title = (String) expr.evaluate(doc, XPathConstants.STRING);

            if (title.equals(lastFeedTitle))
            {
                return;
            }

            expr = xpath.compile("/rss/channel/item[1]/link");
            String link = (String) expr.evaluate(doc, XPathConstants.STRING);

            SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);

            expr = xpath.compile("/rss/channel/item[1]/pubDate");
            Date pubDate = dateFormat.parse((String) expr.evaluate(doc, XPathConstants.STRING));

            expr = xpath.compile("/rss/channel/item[1]/editDate");
            Date editDate = dateFormat.parse((String) expr.evaluate(doc, XPathConstants.STRING));


            StringBuilder message = new StringBuilder();
            message.append(title);

            if (pubDate.equals(editDate))
            {
                message.append(" [New]");
            }
            else
            {
                message.append(" [Update]");
            }

            message.append('\n');
            message.append(link);

            parent.sendMessage(message.toString());

            lastFeedTitle = title;
        }
        catch (Exception e)
        {
            log.error(e.toString());
        }
    }
}
