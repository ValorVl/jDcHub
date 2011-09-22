/*
 * SIDGenerator.java
 *
 * Created on 12 martie 2007, 19:22
 *
 * DSHub ADC HubSoft
 * Copyright (C) 2007,2008  Eugen Hristev
 * Copyright (C) 2011 Alexey 'lh' Antonov
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

package ru.sincore.TigerImpl;

import ru.sincore.ClientManager;
import ru.sincore.ConfigLoader;
import ru.sincore.util.Constants;

import java.util.Random;

/**
 * Sid class ensures there is a SIDGenerator available for a connecting client.
 * created 3 bytes ( aka 5 base32 symbols) but use just first 4.
 *
 * @author Pietricica
 *
 * @author Alexey 'lh' Antonov\
 * @since 2011-09-19
 */
public class SIDGenerator
{
    static Random random = new Random();

    /**
     * Generates new unique SID
     * @return unique SID for newly connected client
     */
    public static String generate()
    {
        byte[] sid = new byte[3];

        boolean ok = false;
        while (!ok)
        {
            ok = true;

            // Generate new bytes for SID
            random.nextBytes(sid);

            // if sid not unique, regenerate sid
            if (ClientManager.getInstance().getClientBySID(Base32.encode(sid)) != null)
                continue;

            if (Base32.encode(sid).substring(0, 4).equals(ConfigLoader.HUB_SID) ||
                Base32.encode(sid).substring(0, 4).equals(ConfigLoader.BOT_CHAT_SID))
                ok = false;
        }

        return Base32.encode(sid);
    }
}