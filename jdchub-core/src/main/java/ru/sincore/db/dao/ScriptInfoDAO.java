/*
* ScriptInfoDAO.java
*
* Created on 03 02 2012, 10:19
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

package ru.sincore.db.dao;

import ru.sincore.db.pojo.ScriptInfoPOJO;

import java.util.List;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-03
 */
public interface ScriptInfoDAO
{
    boolean addScriptInfo(ScriptInfoPOJO script);
    boolean addScriptInfo(String name);
    boolean updateScriptInfo(ScriptInfoPOJO script);
    boolean deleteScriptInfo(String name);
    List<ScriptInfoPOJO> getScriptInfoList();
    ScriptInfoPOJO getScriptInfo(String name);
}
