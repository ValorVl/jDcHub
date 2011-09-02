package ru.sincore.db.pojo;

import javax.persistence.*;

/**
 * *****************************************************************************
 * Copyright (c) 2011  valor.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * <p/>
 * Package : ru.sincore.db.pojo
 * <p/>
 * Date: 01.09.11
 * Time: 17:38
 * <p/>
 * Contributors:
 * valor - initial API and implementation
 * ****************************************************************************
 */

@Entity
@Table(name = "stub")
public class TestPojo
{
	@Id
	@Column(name = "id", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO)
   	private Long id;

	@Column(name = "test")
	private String test;

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public String getTest()
	{
		return test;
	}

	public void setTest(String test)
	{
		this.test = test;
	}
}
