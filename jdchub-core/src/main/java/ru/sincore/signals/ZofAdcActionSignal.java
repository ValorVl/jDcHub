/*
* ZofAdcActionSignal.java
*
* Created on 20 02 2012, 16:28
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

package ru.sincore.signals;

import ru.sincore.adc.action.actions.ZOF;
import ru.sincore.client.AbstractClient;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-20
 */
public class ZofAdcActionSignal extends GenericAdcActionSignal<ZOF>
{
    public ZofAdcActionSignal(AbstractClient client, ZOF action)
    {
        super(client, action);
    }
}
