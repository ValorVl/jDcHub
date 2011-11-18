package ru.sincore.db.dao;

import ru.sincore.db.pojo.CmdLogPOJO;

import java.util.Date;
import java.util.List;

public interface CmdLogDAO
{
	void putLog(String nickName, String commandName, String commandResult, String commandArgs);
	List<CmdLogPOJO> search(Date putLogDate, String commandName);
}
