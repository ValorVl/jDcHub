package ru.sincore.db;

import ru.sincore.ConfigLoader;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class DbConnectionProvider
{
//	private static final Map<String,String> properties = new HashMap<String, String>();

	private static volatile EntityManagerFactory instance;

	static {
		Map<String,String> properties = new HashMap<String, String>();

		properties.put("hibernate.connection.username", ConfigLoader.DB_USER_NAME);
		properties.put("hibernate.connection.password",ConfigLoader.DB_PASSPWORD);
		properties.put("hibernate.connection.url", ConfigLoader.DB_CONNECTION_DSN.toString());
		properties.put("hibernate.connection.driver_class",ConfigLoader.DB_ENGINE);
		properties.put("hibernate.dialect",ConfigLoader.DB_DIALECT);
		properties.put("hibernate.c3p0.min_size", String.valueOf(ConfigLoader.DB_PULL_MIN));
		properties.put("hibernate.c3p0.max_size", String.valueOf(ConfigLoader.DB_PULL_MAX));
		properties.put("hibernate.c3p0.timeout",String.valueOf(ConfigLoader.DB_TIMEOUT));

		instance = Persistence.createEntityManagerFactory("jdchub", properties);
	}

	public static EntityManagerFactory getFactory()
	{
		return instance;
	}
}
