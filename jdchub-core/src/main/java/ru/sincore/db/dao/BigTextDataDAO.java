package ru.sincore.db.dao;

import ru.sincore.db.pojo.BigTextDataPOJO;

import java.util.List;

/**
 * @author Valor
 * @author Alexey 'lh' Antonov
 */
public interface BigTextDataDAO
{
	boolean addData(String title, String locale, String data);
	boolean updateData(String title, String locale, String data);
	boolean deleteData(String title, String locale);
	String	getData(String title, String locale);
    List<String> getLocales(String title);
    List<BigTextDataPOJO> listStaticData();
}
