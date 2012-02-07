/*
* ShareSizeSaver.java
*
* Created on 07 02 2012, 15:00
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

import ru.sincore.ClientManager;
import ru.sincore.db.dao.ShareSizeDAO;
import ru.sincore.db.dao.ShareSizeDAOImpl;

import java.util.TimerTask;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-07
 */
public class ShareSizeSaver extends TimerTask
{
    @Override
    public void run()
    {
        ShareSizeDAO shareSizeDAO = new ShareSizeDAOImpl();
        shareSizeDAO.add(ClientManager.getInstance().getTotalShare());
    }
}
