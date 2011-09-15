package ru.sincore.db.dao;

import ru.sincore.db.pojo.BigStaticDataPOJO;

import java.util.List;

/**
 * @author Valor
 */
public interface BigStaticDataDAO
{
	boolean addData(String title, String body);
	boolean updateData(String title);
	boolean deleteData(String title);
	String	readData(String title);
	List<BigStaticDataPOJO> listStaticData();
}
