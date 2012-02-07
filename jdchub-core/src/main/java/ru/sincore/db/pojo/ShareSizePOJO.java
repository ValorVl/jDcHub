/*
* ShareSizePOJO.java
*
* Created on 07 02 2012, 13:22
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
import java.util.Date;

/**
 * POJO for periodicaly saving total share size on hub
 *
 * @author Alexey 'lh' Antonov
 * @since 2012-02-07
 */
@Entity
@Table(name = "share_size")
public class ShareSizePOJO implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "timestamp", columnDefinition = "DATETIME", nullable = false)
    private Date timestamp = new Date();

    @Column(name = "share_size",columnDefinition = "BIGINT DEFAULT 0")
    private Long shareSize = 0L;


    public Long getId()
    {
        return id;
    }


    public void setId(Long id)
    {
        this.id = id;
    }


    public Date getTimestamp()
    {
        return timestamp;
    }


    public void setTimestamp(Date timestamp)
    {
        this.timestamp = timestamp;
    }


    public Long getShareSize()
    {
        return shareSize;
    }


    public void setShareSize(Long shareSize)
    {
        this.shareSize = shareSize;
    }
}
