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

import javax.persistence.*;

/**
 * @author Valor
 */
@Entity
@Table(name = "pipeline_rules")
public class PipelineRulesPOJO
{
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long    id;

    @Column(name = "pipeline", columnDefinition = "TEXT")
    private String  pipeline;
    
	@Column(name = "matcher", columnDefinition = "TEXT")
	private String  matcher;

    @Column(name = "processor", columnDefinition = "TEXT")
    private String  processor;

    @Column(name = "param", columnDefinition = "TEXT")
    private String  param;


    public Long getId()
    {
        return id;
    }


    public void setId(Long id)
    {
        this.id = id;
    }


    public String getPipeline()
    {
        return pipeline;
    }


    public void setPipeline(String pipeline)
    {
        this.pipeline = pipeline;
    }


    public String getMatcher()
    {
        return matcher;
    }


    public void setMatcher(String matcher)
    {
        this.matcher = matcher;
    }


    public String getProcessor()
    {
        return processor;
    }


    public void setProcessor(String processor)
    {
        this.processor = processor;
    }


    public String getParam()
    {
        return param;
    }


    public void setParam(String param)
    {
        this.param = param;
    }
}
