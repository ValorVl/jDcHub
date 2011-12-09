/*
* StopWordsDAO.java
*
* Copyright (C) 2011 Alexey 'lh' Antonov
* Copyright (C) 2011 Valor
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

import ru.sincore.db.pojo.PipelineRulesPOJO;

import java.util.List;

public interface PipelineRulesDAO
{
    public boolean addRule(String pattern, String processor, String param);
    public boolean updateRule(PipelineRulesPOJO pojo);
    public boolean deleteRule(String pattern, String processor, String param);
    public boolean deleteRule(PipelineRulesPOJO pojo);
    public List<PipelineRulesPOJO> getRules();
    public List<String> getPipelines();
}
