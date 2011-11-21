/*
* ModuleListPOJO.java
*
* Created on 21 11 2011, 14:55
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

package ru.sincore.db.pojo;

import javax.persistence.*;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-11-21
 */
@Entity
@Table(name = "module_list")
public class ModuleListPOJO
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long 	id;
    @Column(name = "name",columnDefinition = "VARCHAR(50)",nullable = false,unique = true)
    private String 	name;
    @Column(name = "enabled",columnDefinition = "TINYINT(1) DEFAULT 1")
    private Boolean enabled = true;


    public Long getId()
    {
        return id;
    }


    public void setId(Long id)
    {
        this.id = id;
    }


    public String getName()
    {
        return name;
    }


    public void setName(String name)
    {
        this.name = name;
    }


    public Boolean getEnabled()
    {
        return enabled;
    }


    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }
}
