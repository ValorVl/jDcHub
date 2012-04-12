/*
* NewRssFeedEvent.java
*
* Created on 09 02 2012, 13:04
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

package ru.sincore.events;

/**
 * RSS feed event (inside it looks like RSS post POJO)
 *
 * @author Alexey 'lh' Antonov
 * @since 2012-02-09
 */
public class NewRssFeed
{
    /**
     * CR - Name of the author.
     */
    private String authorName = null;

    /**
     * TI - Name of post.
     */
    private String postName = null;

    /**
     * DE - Content summary of post.
     */
    private String postDescription = null;

    /**
     * LI - Direct link to post.
     */
    private String link = null;

    /**
     * FN - Feed name.
     */
    private String feedName = null;

    /**
     * FD - Feed description.
     */
    private String feedDescription = null;

    /**
     * DT - Time of publish. Specified in seconds since UNIX epoch.
     */
    private long   publishTime = 0L;

    /**
     * RM - 1 = Remove feed from aggregator.
     */
    private boolean removeFeed = false;
    

    public NewRssFeed()
    {

    }


    public NewRssFeed(String authorName,
                      String postName,
                      String postDescription,
                      String link,
                      String feedName,
                      String feedDescription,
                      long publishTime,
                      boolean removeFeed)
    {
        this.authorName = authorName;
        this.postName   = postName;
        this.postDescription = postDescription;
        this.link = link;
        this.feedName = feedName;
        this.feedDescription = feedDescription;
        this.publishTime = publishTime;
        this.removeFeed = removeFeed;
    }


    public String getAuthorName()
    {
        return authorName;
    }


    public void setAuthorName(String authorName)
    {
        this.authorName = authorName;
    }


    public String getPostName()
    {
        return postName;
    }


    public void setPostName(String postName)
    {
        this.postName = postName;
    }


    public String getPostDescription()
    {
        return postDescription;
    }


    public void setPostDescription(String postDescription)
    {
        this.postDescription = postDescription;
    }


    public String getLink()
    {
        return link;
    }


    public void setLink(String link)
    {
        this.link = link;
    }


    public String getFeedName()
    {
        return feedName;
    }


    public void setFeedName(String feedName)
    {
        this.feedName = feedName;
    }


    public String getFeedDescription()
    {
        return feedDescription;
    }


    public void setFeedDescription(String feedDescription)
    {
        this.feedDescription = feedDescription;
    }


    public long getPublishTime()
    {
        return publishTime;
    }


    public void setPublishTime(long publishTime)
    {
        this.publishTime = publishTime;
    }


    public boolean isRemoveFeed()
    {
        return removeFeed;
    }


    public void setRemoveFeed(boolean removeFeed)
    {
        this.removeFeed = removeFeed;
    }
    
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();

        if (authorName != null)
        {
            result.append("CR: ");
            result.append(authorName);
            result.append('\n');
        }

        if (postName != null)
        {
            result.append("TI: ");
            result.append(postName);
            result.append('\n');
        }

        if (postDescription != null)
        {
            result.append("DE: ");
            result.append(postDescription);
            result.append('\n');
        }

        if (link != null)
        {
            result.append("LI: ");
            result.append(link);
            result.append('\n');
        }

        if (feedName != null)
        {
            result.append("FN: ");
            result.append(feedName);
            result.append('\n');
        }

        if (feedDescription != null)
        {
            result.append("FD: ");
            result.append(feedDescription);
            result.append('\n');
        }

        result.append("DT: ");
        result.append(publishTime);
        result.append('\n');

        result.append("RM: ");
        result.append(removeFeed);
        result.append('\n');

        return result.toString();
    }
}
