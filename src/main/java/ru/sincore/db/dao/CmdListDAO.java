package ru.sincore.db.dao;

import ru.sincore.db.pojo.CmdListPOJO;

import java.util.List;

public interface CmdListDAO
{
	boolean addCommand(String name, int weight, String args, String description, String syntax,Boolean enabled, Boolean logs);
    boolean addCommand(CmdListPOJO command);
	boolean delCommand(String name);
	boolean updateCommand(CmdListPOJO commandObject);
	List<CmdListPOJO> getCommandList();
    CmdListPOJO getCommand(String name);
}
