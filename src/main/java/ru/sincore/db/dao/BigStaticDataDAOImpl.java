package ru.sincore.db.dao;

import ru.sincore.db.pojo.BigStaticDataPOJO;

import java.util.List;

/**
 * @author Valor
 */
public class BigStaticDataDAOImpl implements BigStaticDataDAO
{
	@Override
	public boolean addData(String title, String body)
	{
		return false;
	}

	@Override
	public boolean updateData(String title)
	{
		return false;
	}

	@Override
	public boolean deleteData(String title)
	{
		return false;
	}

	@Override
	public String readData(String title)
	{
		return null;
	}

	@Override
	public List<BigStaticDataPOJO> listStaticData()
	{
		return null;
	}
}
