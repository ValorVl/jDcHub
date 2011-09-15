package ru.sincore.db.dao;

import ru.sincore.db.pojo.CmdListPOJO;

import java.util.List;

public interface CmdListDAO
{
	boolean addCommand(String name, int weight, String executorClass, String[] args, String description, String syntax,Boolean enabled, Boolean logged);
	boolean delCommand(String name);
	boolean updateCommand(CmdListPOJO commandObject);
	List<CmdListPOJO> getCommandList();

}
