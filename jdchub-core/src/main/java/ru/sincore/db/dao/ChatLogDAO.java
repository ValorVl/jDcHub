package ru.sincore.db.dao;

import ru.sincore.db.pojo.ChatLogPOJO;

import java.util.List;

public interface ChatLogDAO
{
	void saveMessage(String nick, String message);
	List<ChatLogPOJO> getLast(Integer lastRowCount);
}
