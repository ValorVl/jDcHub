/*
* StopWordsPOJO.java
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

package ru.sincore.db.pojo;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author Valor
 */
@Entity
@Table(name = "pipeline_rules")
public class PipelineRulesPOJO
{
    @Getter
    @Setter
    @Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long    id;

    @Getter
    @Setter
    @Column(name = "pipeline", columnDefinition = "TEXT")
    private String  pipeline;

    @Getter
    @Setter
    @Column(name = "matcher", columnDefinition = "TEXT")
	private String  matcher;

    @Getter
    @Setter
    @Column(name = "processor", columnDefinition = "TEXT")
    private String  processor;

    @Getter
    @Setter
    @Column(name = "param", columnDefinition = "TEXT")
    private String  param;
}
