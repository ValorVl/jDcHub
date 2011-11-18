/*
* CmdLogDAO.java
*
*
* Copyright (C) 2011 Valor
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

import ru.sincore.db.pojo.CmdLogPOJO;

import java.util.Date;
import java.util.List;

public interface CmdLogDAO
{
	void putLog(String commandName, String commandArgs, String nickName, String commandResult);
	List<CmdLogPOJO> search(Date putLogDate, String commandName);
}
