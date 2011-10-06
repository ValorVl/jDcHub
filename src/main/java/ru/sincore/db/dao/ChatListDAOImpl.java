package ru.sincore.db.dao;

/*
 * jDcHub ADC HubSoft
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import ru.sincore.db.HibernateUtils;
import ru.sincore.db.pojo.ChatListPOJO;

import java.util.List;

public class ChatListDAOImpl implements ChatListDAO
{

	private static final Logger log = LoggerFactory.getLogger(ChatListDAOImpl.class);
	private String marker = Marker.ANY_NON_NULL_MARKER;

	@Override
	public List<ChatListPOJO> getChatList()
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx  = session.getTransaction();

		try
		{
			tx.begin();

			Query request = session.createQuery("from ChatListPOJO");

			List<ChatListPOJO> result = (List<ChatListPOJO>) request.list();

			tx.commit();

			return result;
		}
		catch (HibernateException ex)
		{
			tx.rollback();
			log.error(marker,ex);
		}

		return null;
	}

	@Override
	public void updateChatParams(ChatListPOJO chat)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx  = session.getTransaction();

		try
		{
			tx.begin();

			session.update(chat);

			tx.commit();

		}
		catch (HibernateException ex)
		{
			tx.rollback();
			log.error(marker,ex);
		}
	}

	@Override
	public void addChat(ChatListPOJO chat)
	{
		Session session = HibernateUtils.getSessionFactory().openSession();
		Transaction tx  = session.getTransaction();

		try
		{
			tx.begin();

			session.save(chat);

			tx.commit();
		}
		catch (HibernateException ex)
		{
			tx.rollback();
			log.error(marker,ex);
		}
	}
}
