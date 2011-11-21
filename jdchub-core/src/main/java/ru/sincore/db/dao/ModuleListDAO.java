/*
* ModuleListDAO.java
*
* Created on 21 11 2011, 15:00
*
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

package ru.sincore.db.dao;

import ru.sincore.db.pojo.ModuleListPOJO;

import java.util.List;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-11-21
 */
public interface ModuleListDAO
{
    boolean addModule(ModuleListPOJO module);
    boolean addModule(String name, boolean enabled);
    boolean updateModule(ModuleListPOJO module);
    boolean deleteModule(String name);
    List<ModuleListPOJO> getModuleList();
    ModuleListPOJO getModule(String name);
}
