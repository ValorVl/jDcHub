/*
* ScriptInfoPOJO.java
*
* Created on 03 02 2012, 10:03
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

package ru.sincore.db.pojo;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Alexey 'lh' Antonov
 * @since 2012-02-03
 */
@Entity
@Table(name = "script_info")
public class ScriptInfoPOJO implements Serializable
{
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "name", columnDefinition = "VARCHAR(50)", nullable = false, unique = true)
    private String name;

    @Column(name = "args", columnDefinition = "VARCHAR(50)", nullable = true, unique = false)
    private String args;

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


    public String getArgs()
    {
        return args;
    }


    public void setArgs(String args)
    {
        this.args = args;
    }


    public Boolean isEnabled()
    {
        return enabled;
    }


    public void setEnabled(Boolean enabled)
    {
        this.enabled = enabled;
    }


    public void enable()
    {
        this.setEnabled(true);
    }


    public void disable()
    {
        this.setEnabled(false);
    }
}
