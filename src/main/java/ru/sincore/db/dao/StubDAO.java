package ru.sincore.db.dao;

import ru.sincore.db.DbConnectionProvider;
import ru.sincore.db.pojo.TestPojo;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 * *****************************************************************************
 * Copyright (c) 2011  valor.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * <p/>
 * Package : ru.sincore.db.dao
 * <p/>
 * Date: 01.09.11
 * Time: 17:44
 * <p/>
 * Contributors:
 * valor - initial API and implementation
 * ****************************************************************************
 */

public class StubDAO
{

	private EntityManagerFactory factory = null;

	public void add(String val)
	{
		factory = DbConnectionProvider.getFactory();

		EntityManager em = factory.createEntityManager();

		em.getTransaction().begin();

		TestPojo t = new TestPojo();

		t.setTest(val);

		em.persist(t);

		em.getTransaction().commit();
		em.close();

	}

	public void del(Long id)
	{
		factory = DbConnectionProvider.getFactory();

		EntityManager em = factory.createEntityManager();

		em.getTransaction().begin();

		TestPojo t = em.find(TestPojo.class,id);

		t.setId(id);

		em.remove(t);

		em.getTransaction().commit();
		em.close();

	}


}
