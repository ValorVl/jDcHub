/*
* ClientCountPOJO.java
*
* Created on 07 02 2012, 13:12
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

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * POJO for periodicaly saving number of online client on hub
 *
 * @author Alexey 'lh' Antonov
 * @since 2012-02-07
 */
@Entity
@Table(name = "client_count")
public class ClientCountPOJO implements Serializable
{
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Getter
    @Setter
    @Column(name = "timestamp", columnDefinition = "DATETIME", nullable = false)
    private Date timestamp = new Date();

    @Getter
    @Setter
    @Column(name = "count", columnDefinition = "INTEGER DEFAULT 0")
    private Long count = 0L;
}
