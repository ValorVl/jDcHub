/*
* MessageSender.java
*
* Created on 05 09 2012, 14:26
*
* Copyright (C) 2012 Alexey 'lh' Antonov
*
* For more info read COPYRIGHT file in project root directory.
*
*/

package ru.sincore;

import ru.sincore.client.AbstractClient;

import java.util.List;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-09-05
 */
class MessageSender implements Runnable
{
    private String            message = "";
    private AbstractClient fromClient = null;
    private AbstractClient    toClient = null;
    private boolean           featured = false;
    private List<String>   requiredFeatures = null;
    private List<String>      excludedFeatures = null;


    public MessageSender(String message, AbstractClient fromClient, AbstractClient toClient,
                         List<String> requiredFeatures, List<String> excludedFeatures)
    {
        this.message = message;
        this.fromClient = fromClient;
        this.toClient = toClient;

        this.featured = true;
        this.requiredFeatures = requiredFeatures;
        this.excludedFeatures = excludedFeatures;
    }


    public MessageSender(String message, AbstractClient fromClient, AbstractClient toClient)
    {
        this(message, fromClient, toClient, null, null);
        this.featured = false;
    }


    public void run()
    {
        boolean doSend = true;

        if (ConfigurationManager.getInstance().getBoolean(ConfigurationManager.FEATURED_BROADCAST))
        {
            if (featured)
            {
                if (requiredFeatures != null)
                {
                    for (String feature : requiredFeatures)
                    {
                        if (!toClient.isFeature(feature))
                        {
                            doSend = false;
                            break;
                        }
                    }
                }

                if (excludedFeatures != null)
                {
                    for (String feature : excludedFeatures)
                    {
                        if (toClient.isFeature(feature))
                        {
                            doSend = false;
                            break;
                        }
                    }
                }
            }
        }

        if (toClient.isActive() && doSend)
        {
            if (fromClient != null)
            {
                if ((!message.startsWith("E") && !message.startsWith("B")) &&
                    toClient.equals(fromClient))
                {
                    return;
                }
            }

            toClient.sendRawCommand(message);
        }
    }
}
