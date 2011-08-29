package ru.sincore.db;

import org.apache.log4j.Logger;

public class DbConnectionProvider
{

    private static volatile DbConnectionProvider instance = new DbConnectionProvider();

    private static final Logger _log = Logger
	    .getLogger(DbConnectionProvider.class);

    public static DbConnectionProvider getInstance()
    {
	return instance;
    }

    private DbConnectionProvider()
    {

    }

}
