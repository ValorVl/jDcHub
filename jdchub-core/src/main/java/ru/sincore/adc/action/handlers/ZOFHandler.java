/*
* ZOFHandler.java
*
* Created on 20 02 2012, 15:21
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

package ru.sincore.adc.action.handlers;

import org.apache.mina.filter.compression.CompressionFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.Exceptions.CommandException;
import ru.sincore.Exceptions.STAException;
import ru.sincore.adc.action.actions.ZOF;
import ru.sincore.client.AbstractClient;
import ru.sincore.util.Constants;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-20
 */
public class ZOFHandler extends AbstractActionHandler<ZOF>
{
    private static final Logger log = LoggerFactory.getLogger(ZOFHandler.class);

    
    public ZOFHandler(AbstractClient client, ZOF action)
    {
        super(client, action);
    }


    @Override
    public void handle()
            throws STAException
    {
        try
        {
            switch (action.getMessageType())
            {
                case H:
                    CompressionFilter compressionFilter =
                            (CompressionFilter) client.getSession().getFilterChain().get(
                                    Constants.ZLIB_FILTER);

                    if (compressionFilter != null)
                    {
                        compressionFilter.setCompressInbound(false);
                    }
                    break;
            }
        }
        catch (CommandException e)
        {
            log.error(e.toString());
        }
    }
}
